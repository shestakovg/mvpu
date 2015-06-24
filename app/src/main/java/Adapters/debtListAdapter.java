package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uni.mvpu.R;

import java.util.ArrayList;

import Entitys.DebtData;
import Entitys.Order;

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
        if (debt.overdueDays>0)
            ((LinearLayout) view.findViewById(R.id.llDebtList)).setBackgroundColor(Color.parseColor("#ffff2d58"));
        return view;
    }
}
