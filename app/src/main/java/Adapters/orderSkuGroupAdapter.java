package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.List;

import Entitys.groupSku;
import Entitys.orderSku;
import core.wputils;

/**
 * Created by shest on 10/8/2016.
 */

public class orderSkuGroupAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    orderSkuGroupAdapter currentAdapter;
    private List<groupSku> groupSkuList;

    public orderSkuGroupAdapter(Context context,List<groupSku> groupSkuList ) {
        this.context = context;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupSkuList = groupSkuList;
    }

    @Override
    public int getCount() {
        return groupSkuList.size();
    }

    @Override
    public groupSku getItem(int position) {
        return groupSkuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public groupSku getSkuGroup(int position)
    {
        return   getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_skugroup, parent, false);
        }
        groupSku group = getItem(position);
        ((TextView) view.findViewById(R.id.tvSkuGroupName)).setText(group.getGroupName());
        if (group.getPlanAmount()>0)
            ((TextView) view.findViewById(R.id.tvPlanFact)).setText( context.getText(R.string.StringPlan)+": "+ wputils.formatFloatWithSeparator(group.getPlanAmount()/1000)+" тыс   "
                    + context.getText(R.string.StringFact)+": "+ wputils.formatFloatWithSeparator(group.getFactAmount()/1000)+" тыс" );
        else
        ((TextView) view.findViewById(R.id.tvPlanFact)).setText((group.getOutletCount() > 0 ? context.getText(R.string.StringPlan)+": "+group.getOutletCount().toString()+"   "
                + context.getText(R.string.StringFact)+": "+group.getFactOutletCount().toString() : ""));
        view.setBackgroundColor(Color.parseColor(group.getColor()));
        return view;
    }
}
