package Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
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
import interfaces.IOrderTotal;

/**
 * Created by Gennadiy Shestakov on 2/28/2016.
 */
public class storecheckSkuAdapter  extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    storecheckSkuAdapter currentAdapter;
    private ArrayList<orderSku> skuList;
    private OutletObject outlet;
    private OrderExtra orderExtra;

    public storecheckSkuAdapter(Context context, ArrayList<orderSku> skuList,   OutletObject outlet, OrderExtra orderExtra) {
        this.context = context;

        this.skuList = skuList;
        this.outlet = outlet;
        this.orderExtra = orderExtra;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentAdapter = this;
    }

    //private IOrderTotal orderTotal;
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
            view = lInflater.inflate(R.layout.lv_item_storecheck, parent, false);
        }
        orderSku cursku = getSku(position);
        TextView textSkuName = (TextView) view.findViewById(R.id.textViewOrderSkuName);
        textSkuName.setText(cursku.skuName);
        TextView textFinalDate = (TextView) view.findViewById(R.id.finalDate);
        if (!cursku.finalDateExists)
        {    textFinalDate.setText(AppSettings.EMPTY_STORECHECK_DATE);
            textFinalDate.setTextColor(Color.parseColor("#ff423aff"));}
        else {
            textFinalDate.setText(wputils.getDateTimeString(cursku.finalDate));
            textFinalDate.setTextColor(Color.parseColor("#FFFF0C12"));
        }
        TextView qty = (TextView) view.findViewById(R.id.editMWH);

        if (cursku.qtyMWH==0)
        {
            qty.setTextColor(Color.parseColor("#ff423aff"));
            qty.setText("-");
        }
        else
        {
            qty.setTextColor(Color.parseColor("#FFFF0C12"));
            qty.setText(Integer.toString(cursku.qtyMWH));
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

    private void onClickSku(View v)
    {
        final orderSku sku = getSku((int) v.getTag());
        //Toast.makeText(context, sku.skuName, Toast.LENGTH_SHORT ).show();
        final Dialog dlgEditQty =  new Dialog(context);
        dlgEditQty.setTitle(sku.skuName);
        dlgEditQty.setContentView(R.layout.dlg_edit_storecheck);
        dlgEditQty.setCancelable(true);
        final EditText dlgEditMWH = (EditText) dlgEditQty.findViewById(R.id.editDialogMWH);
        if (sku.qtyMWH!=0)
            dlgEditMWH.setText(Integer.toString(sku.qtyMWH));
        final DatePicker finalDatePicker = (DatePicker) dlgEditQty.findViewById(R.id.datePicker);
        if (sku.finalDateExists)
            finalDatePicker.updateDate(sku.finalDate.get(Calendar.YEAR), sku.finalDate.get(Calendar.MONTH),sku.finalDate.get(Calendar.DAY_OF_MONTH));

                //setDate(new Date().getTime(), false, true);
        Button btnOk =(Button) dlgEditQty.findViewById(R.id.btnEtitQtyOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sku.qtyMWH = Integer.parseInt(dlgEditMWH.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR,finalDatePicker.getYear());
                cal.set(Calendar.MONTH,finalDatePicker.getMonth());
                cal.set(Calendar.DAY_OF_MONTH,finalDatePicker.getDayOfMonth());
                sku.setFinalDate(cal);
                sku.saveDb(context, orderExtra.orderType);
                currentAdapter.notifyDataSetChanged();
                dlgEditQty.dismiss();
            }
        });

        Button btnCancel =(Button) dlgEditQty.findViewById(R.id.btnEtitQtyCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgEditQty.dismiss();
            }
        });

        dlgEditQty.show();
    }
}
