package com.uni.mvpu;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Adapters.debtListAdapter;
import Adapters.orderListAdapter;
import Dialogs.DlgInputPay;
import Entitys.DebtData;
import core.AppSettings;
import core.LocationDatabase;
import core.ResultObject;
import core.TouchActivity;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IInputCustomerPay;


public class ActivityDebt extends TouchActivity implements IInputCustomerPay {
    private String customerid;
    private String customerName;
    private ListView lvDebt;
    private debtListAdapter debtAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("�����");
        setContentView(R.layout.activity_debt);
        lvDebt = (ListView) findViewById(R.id.lvDebtDataList);
        if (getIntent().getStringExtra("onlyCustomer").equals("1")) {
            customerid = getIntent().getStringExtra("customerid");
            customerName = appManager.getOurInstance().getCustomerName(customerid, this);
            setTitle("�����:  " + customerName);
            fillDebtList();
        }
        else
        {
            setTitle("��� ������");
            fillPayList();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_debt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillDebtList()
    {
        ArrayList<DebtData> debts = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select  d.transactionNumber  , d.transactionDate  , d.transactionSum  , "  +
                " d.paymentDate  , d.debt  , d.overdueDebt  , d.overdueDays, d.transactionId, coalesce(p.paySum,0) claimedSum, rt.CustomerName, d.color from debts d " +
                " left join  pays p on p.transactionId = d.transactionId and p.payDate = ?" +
                " left join (select distinct CustomerId ,CustomerName from route) rt on rt.CustomerId = d.customerId"+
                " where d.customerId= ? order by DATE(substr( d.paymentDate,7,4)" +
                "||substr( d.paymentDate,4,2)" +
                "||substr( d.paymentDate,1,2)) "
                    , new String[] { wputils.getDateTime(wputils.getCurrentDate()),customerid});
        cursor.moveToFirst();
        for (int i=0; i < cursor.getCount(); i++ )
        {
            DebtData debtData = new DebtData();
            debtData.debt = cursor.getDouble(cursor.getColumnIndex("debt"));
            debtData.overdueDebt = cursor.getDouble(cursor.getColumnIndex("overdueDebt"));
            debtData.transactionSum = cursor.getDouble(cursor.getColumnIndex("transactionSum"));
            debtData.overdueDays = cursor.getInt(cursor.getColumnIndex("overdueDays"));
            debtData.transactionNumber = cursor.getString(cursor.getColumnIndex("transactionNumber"));
            debtData.transactionDate = cursor.getString(cursor.getColumnIndex("transactionDate"));
            debtData.paymentDate = cursor.getString(cursor.getColumnIndex("paymentDate"));
            debtData.transactionId = cursor.getString(cursor.getColumnIndex("transactionId"));
            debtData.claimedSum = cursor.getDouble(cursor.getColumnIndex("claimedSum"));
            debtData.CustomerName = cursor.getString(cursor.getColumnIndex("CustomerName"));
            debtData.color = cursor.getString(cursor.getColumnIndex("color"));
            debts.add(debtData);
            cursor.moveToNext();
        }
        Cursor cursorPayRequest = db.rawQuery("select count(*) from pay_requests where requestDate = ? and customerid = ?", new String[] { wputils.getDateTime(wputils.getCurrentDate()),customerid});
        cursorPayRequest.moveToFirst();
        Boolean payRequestExist =  cursorPayRequest.getInt(0) > 0;
        db.close();

        debtAdapter = new debtListAdapter(this, debts);
        lvDebt.setAdapter(debtAdapter);

        ((TextView) findViewById(R.id.tvDeptTotal)).setText(String.format("%.2f", DebtData.getDebtSum(debts)));
        ((TextView) findViewById(R.id.tvOverdueDeptTotal)).setText(String.format("%.2f", DebtData.getOverdueDebtSum(debts)));
        ((TextView) findViewById(R.id.tvClaimedSum)).setText(String.format("%.2f", DebtData.getClaimedSum(debts)));
        ((TextView) findViewById(R.id.tvStatedSumWithOverdue)).setText(String.format("%.2f", DebtData.getOverdueAndClaimedSum(debts)));
        String PaymentDescription = "������ �� ��������";
        if ( DebtData.getClaimedSum(debts) > 0 ) PaymentDescription = "�������� ������";

        if ( Math.round(DebtData.getClaimedSum(debts) - AppSettings.PARAM_EMPTY_PAYMENT ) == 0 &&  DebtData.getDebtSum(debts) > 0) PaymentDescription = "��� ������";
        if (payRequestExist) PaymentDescription+=" C����� ������ �� ��������";
        ((TextView) findViewById(R.id.tvPaymentDescription)).setText(PaymentDescription);

    }

    private void fillPayList()
    {
        ArrayList<DebtData> debts = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select  d.transactionNumber  , d.transactionDate  , d.transactionSum  , "  +
                " d.paymentDate  , d.debt  , d.overdueDebt  , d.overdueDays, d.transactionId, coalesce(p.paySum,0) claimedSum, rt.CustomerName  from pays p " +
                " left join debts d  on p.transactionId = d.transactionId " +
                " left join (select distinct CustomerId ,CustomerName from route) rt on rt.CustomerId = d.customerId"+
                " where p.payDate = ? order by DATE(substr( d.paymentDate,7,4)" +
                "||substr( d.paymentDate,4,2)" +
                "||substr( d.paymentDate,1,2)) "
                , new String[] { wputils.getDateTime(wputils.getCurrentDate())});
        cursor.moveToFirst();
        for (int i=0; i < cursor.getCount(); i++ )
        {
            DebtData debtData = new DebtData();
            debtData.debt = cursor.getDouble(cursor.getColumnIndex("debt"));
            debtData.overdueDebt = cursor.getDouble(cursor.getColumnIndex("overdueDebt"));
            debtData.transactionSum = cursor.getDouble(cursor.getColumnIndex("transactionSum"));
            debtData.overdueDays = cursor.getInt(cursor.getColumnIndex("overdueDays"));
            debtData.transactionNumber = cursor.getString(cursor.getColumnIndex("transactionNumber"));
            debtData.transactionDate = cursor.getString(cursor.getColumnIndex("transactionDate"));
            debtData.paymentDate = cursor.getString(cursor.getColumnIndex("paymentDate"));
            debtData.transactionId = cursor.getString(cursor.getColumnIndex("transactionId"));
            debtData.claimedSum = cursor.getDouble(cursor.getColumnIndex("claimedSum"));
            debtData.CustomerName = cursor.getString(cursor.getColumnIndex("CustomerName"));

            debts.add(debtData);
            cursor.moveToNext();
        }
        db.close();
        debtAdapter = new debtListAdapter(this, debts);
        lvDebt.setAdapter(debtAdapter);

        ((TextView) findViewById(R.id.tvDeptTotal)).setText(String.format("%.2f", DebtData.getDebtSum(debts)));
        ((TextView) findViewById(R.id.tvOverdueDeptTotal)).setText(String.format("%.2f", DebtData.getOverdueDebtSum(debts)));
        ((TextView) findViewById(R.id.tvClaimedSum)).setText(String.format("%.2f", DebtData.getClaimedSum(debts)));
        ((TextView) findViewById(R.id.tvStatedSumWithOverdue)).setText(String.format("%.2f", DebtData.getOverdueAndClaimedSum(debts)));
    }

    @Override
    public void processPay(double paySum) {
       // Toast.makeText(this, Double.toString(paySum), Toast.LENGTH_LONG).show();
        debtAdapter.applyPay(paySum, this.customerid);
        fillDebtList();
    }

    @Override
    public ResultObject checkAllowInputPay(double paySum) {
        return debtAdapter.checkAllowInputPay(paySum, this.customerid);
    }

    public void onClickClaimPay(View view)
    {
        DlgInputPay dlg = new DlgInputPay(this,customerName,this);
        dlg.show();

    }

    public void onClickEmptyPay(View v)
    {
        ResultObject res = debtAdapter.checkAllowInputPay(AppSettings.PARAM_EMPTY_PAYMENT, this.customerid);
        if (!res.isResult())
        {
           Toast.makeText(this, res.getResultMessage(),Toast.LENGTH_LONG).show();
        }
        else {
            debtAdapter.applyPay(AppSettings.PARAM_EMPTY_PAYMENT, this.customerid);
            fillDebtList();
        }
    }

    public void onClickPayRequest(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Context context = this;
        builder.setTitle("������� ���������� ������")
                .setMessage("������� ������ �� �����?")
                .setIcon(R.drawable.placeholder)
                .setCancelable(false)
                .setPositiveButton("��",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                savePayRequest();
                                Toast.makeText(context, "�������� ������ ���������", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("������", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {}});
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void savePayRequest() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        SQLiteDatabase db = (new DbOpenHelper(this)).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("requestDate", wputils.getDateTime(currentDate));
        values.put("customerid", customerid);
        values.put("_send",0);
        db.insert("pay_requests", null, values);
        db.close();
    }
}
