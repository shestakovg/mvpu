package Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Entitys.DebtData;
import Entitys.Order;
import core.AppSettings;
import core.ResultObject;
import core.wputils;
import db.DbOpenHelper;

/**
 * Created by g.shestakov on 24.06.2015.
 */
public class debtListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<DebtData> debts;

    public debtListAdapter(Context context, ArrayList<DebtData> debts) {
        this.context = context;
        this.debts = debts;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return debts.size();
    }

    @Override
    public Object getItem(int position) {
        return debts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private DebtData getDebtData(int position)
    {
        return (DebtData) getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_debt, parent, false);
        }
        DebtData debt = getDebtData(position);
        ((TextView) view.findViewById(R.id.tvTransactionNumber)).setText(debt.transactionNumber);
        ((TextView) view.findViewById(R.id.tvTransactionDate)).setText(debt.transactionDate);
        ((TextView) view.findViewById(R.id.tvTransactionSum)).setText(String.format("%.2f",debt.transactionSum));
        ((TextView) view.findViewById(R.id.tvPaymentDate)).setText(debt.paymentDate);
        ((TextView) view.findViewById(R.id.tvDebt)).setText(String.format("%.2f",debt.debt));
        ((TextView) view.findViewById(R.id.tvOverdueDebt)).setText(String.format("%.2f", debt.overdueDebt));
        ((TextView) view.findViewById(R.id.tvOverdueDays)).setText(Integer.toString(debt.overdueDays));
        ((TextView) view.findViewById(R.id.tvDebtCustomername)).setText(debt.CustomerName);


        ((TextView) view.findViewById(R.id.tvClaimedSum)).setText(String.format("%.2f", debt.claimedSum));

        if (debt.overdueDays>=0) {
              if ((debt.overdueDebt - debt.claimedSum) > 0 )
              {
                  ((LinearLayout) view.findViewById(R.id.llDebtList)).setBackgroundColor( Color.parseColor(
                                (debt.color == null ? "#ffff2d58" : debt.color)  ));//Color.parseColor("#ffff2d58"));
              }
              else
              {
               //FFFCFF05
                //  ((LinearLayout) view.findViewById(R.id.llDebtList)).setBackgroundColor(Color.parseColor("#FF95AAFF"));
                  ((LinearLayout) view.findViewById(R.id.llDebtList)).setBackgroundColor(Color.parseColor((debt.color == null ? "#FF95AAFF" : debt.color) ));
              }
        }
        return view;
    }

    public ResultObject checkAllowInputPay(double paySum, String customerId)
    {
        ResultObject result = new ResultObject(this);
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        double allPays = 0;

        SQLiteDatabase db = (new DbOpenHelper(this.context)).getReadableDatabase();
        Cursor cursor = db.rawQuery("select coalesce(sum(paySum),0) from  pays where payDate = ? and customerid= ?" +
                " and exists (select * from orderHeader oh " +
                            "  left join route r on oh.outletId=r.outletId " +
                            " where oh.orderDate = ? and r.customerid = ?)",
                new String[]{wputils.getDateTime(currentDate), customerId, wputils.getDateTime(currentDate), customerId});
        cursor.moveToFirst();

        for (int i=0; i < cursor.getCount(); i++ ) {
            allPays+= cursor.getDouble(0);
            cursor.moveToNext();
        }
        db.close();
        Boolean resBool = allPays <= paySum;
        result.setResult(resBool);
        if (!resBool)
            result.setResultMessage("Сумма "+(paySum == AppSettings.PARAM_EMPTY_PAYMENT ? "0": String.format("%.2f",paySum))+" меньше заявленной ранее суммы "
                    +String.format("%.2f", allPays)+"\n"+"Нельзя уменьшать оплату, если создан заказ!");
        return result;
    }


    public void applyPay(double paySum, String customerId)
    {
        if (paySum <= 0) return;
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        SQLiteDatabase db = (new DbOpenHelper(this.context)).getWritableDatabase();
        db.execSQL("delete from  pays where payDate = ? and customerid= ?", new String[]{wputils.getDateTime(currentDate), customerId});
        for (DebtData debt : debts)
        {
            if (paySum > 0)
            {
                double curSum = (debt.debt <= paySum ? debt.debt : paySum);
                paySum -= debt.debt;
                savePay(curSum, customerId, debt.transactionId, db, currentDate);
            }
            else
            {
                break;
            }
        }
        db.close();
    }

    private void savePay(double paySum, String customerId, String transactionId, SQLiteDatabase db,  Calendar date)
    {
        ContentValues values = new ContentValues();
        values.put("payDate", wputils.getDateTime(date));
        values.put("customerid", customerId);
        values.put("transactionId", transactionId);
        values.put("paySum", paySum);
        values.put("_send",0);
        db.insert("pays", null, values);
    }
}
