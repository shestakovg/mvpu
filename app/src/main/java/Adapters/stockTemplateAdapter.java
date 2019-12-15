package Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Entitys.OrderExtra;
import Entitys.OutletObject;
import Entitys.orderSku;
import core.AppSettings;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IOrderTotal;

public class stockTemplateAdapter  extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    stockTemplateAdapter currentAdapter;
    private ArrayList<orderSku> skuList;
    private OutletObject outlet;
    private OrderExtra orderExtra;
    private IOrderTotal orderTotal;

    public stockTemplateAdapter(Context context, ArrayList<orderSku> skuList,   OutletObject outlet, OrderExtra orderExtra, IOrderTotal orderTotal) {
        this.context = context;

        this.skuList = skuList;
        this.outlet = outlet;
        this.orderExtra = orderExtra;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentAdapter = this;
        this.orderTotal = orderTotal;
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
            view = lInflater.inflate(R.layout.lv_item_stocktemplate, parent, false);
        }
        orderSku cursku = getSku(position);
        TextView textSkuName = (TextView) view.findViewById(R.id.textViewOrderSkuName);
        textSkuName.setText(cursku.skuName);
        TextView textFinalDate = (TextView) view.findViewById(R.id.orderDate);
        textFinalDate.setText(cursku.PreviousOrderDate);
        TextView qty = (TextView) view.findViewById(R.id.editMWH);
        qty.setText(Integer.toString(cursku.PreviousOrderQty));
        CheckBox exists = (CheckBox) view.findViewById(R.id.chbAvailiable);
        exists.setChecked(cursku.AvailiableInStore);
        exists.setEnabled(false);
        if (!cursku.AvailiableInStore) {
            textSkuName.setTextColor(Color.RED);
        } else {
            textSkuName.setTextColor(Color.BLACK);
        }

        textSkuName.setTag(position);
        view.setTag(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSku(v);
            }
        });
        textSkuName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSku(v);
            }
        });
        return view;
    }

    private void onClickSku(View v) {
        final orderSku sku = getSku((int) v.getTag());
        sku.AvailiableInStore = !sku.AvailiableInStore;
        currentAdapter.notifyDataSetChanged();
        orderTotal.displayTotal();
        saveSku(sku);
    }

    private void saveSku(orderSku sku) {
//        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
//        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//        db.execSQL("delete from orderDetail where skuId = ? and orderUUID = ?", new String[] {sku.skuId, orderExtra.orderUUID });
//        db.close();
//        Double  orderQTY= sku.PreviousOrderQty * 1.0;
//        sku.setQtyMWH(orderQTY.intValue());
        sku.saveDb(context, AppSettings.ORDER_TYPE_STOCK_TEMPLATE);
    }

}
