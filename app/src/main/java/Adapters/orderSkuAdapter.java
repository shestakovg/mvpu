package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uni.mvpu.R;

import java.util.ArrayList;

import Entitys.orderSku;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class orderSkuAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    private ArrayList<orderSku> skuList;

    public orderSkuAdapter(Context context,  ArrayList<orderSku> skuList) {
        this.context = context;
        this.skuList = skuList;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return skuList.size();
    }

    @Override
    public Object getItem(int position) {
        return skuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private orderSku getSku(int position)
    {
        return (orderSku) getItem(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_ordersku, parent, false);
        }
        orderSku cursku = getSku(position);
        TextView textSkuName = (TextView) view.findViewById(R.id.textViewOrderSkuName);
        textSkuName.setText(cursku.skuName);
        if ((cursku.stockG <=0 ) && (cursku.stockR <= 0))
        {
                textSkuName.setTypeface(null, Typeface.NORMAL);
                textSkuName.setTextColor(Color.parseColor("#bdbdbd"));
        }
        else
        {
                textSkuName.setTypeface(null, Typeface.BOLD);
                textSkuName.setTextColor(Color.parseColor("#ff000000"));
        }
        ((TextView) view.findViewById(R.id.textViewMWH)).setText(String.format("%d",(long) cursku.stockG));
        ((TextView) view.findViewById(R.id.textViewRWH)).setText(String.format("%d",(long) cursku.stockR));
        return view;
    }
}
