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

import Entitys.OutletObject;
import Entitys.orderSku;

/**
 * Created by shest on 3/19/2017.
 */

public class RouteAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<OutletObject>  outletsObjectList;

    public RouteAdapter(Context context, ArrayList<OutletObject> outletsObjectList) {
        this.context = context;
        this.outletsObjectList = outletsObjectList;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return outletsObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return outletsObjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_route, parent, false);
        }
        OutletObject outlet = (OutletObject) getItem(position);

        TextView textOutletName = (TextView) view.findViewById(R.id.tvOutletName);

        textOutletName.setText(outlet.outletName);
        textOutletName.setTag(position);
        textOutletName.setTextColor(Color.BLACK);
        if (outlet.FailVisit)
            textOutletName.setTextColor(Color.RED);

        if (outlet.OrderCount > 0)
                textOutletName.setTextColor(Color.BLUE);
        if (outlet.PayCount > 0)
            textOutletName.setTextColor(Color.MAGENTA);

        if (outlet.OrderCount > 0 && outlet.PayCount > 0)
            textOutletName.setTextColor(Color.parseColor("#FF0AB616"));
        TextView textOutletInfo = (TextView) view.findViewById(R.id.tvOutletInfo);
        textOutletInfo.setText(outlet.outletAddress );
        textOutletInfo.setTag(position);

        TextView textOutletDay = (TextView) view.findViewById(R.id.tvOutletDay);
        textOutletDay.setText("День визита: "+outlet.dayOfWeekStr);
        textOutletDay.setTag(position);
        return view;
    }
}

