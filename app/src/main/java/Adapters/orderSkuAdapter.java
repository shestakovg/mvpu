package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import java.util.ArrayList;

import Entitys.OrderExtra;
import Entitys.OutletObject;
import Entitys.orderSku;
import interfaces.IOrderTotal;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class orderSkuAdapter extends BaseAdapter  {
    Context context;
    LayoutInflater lInflater;
    orderSkuAdapter currentAdapter;
    private ArrayList<orderSku> skuList;
    private OutletObject outlet;
    private OrderExtra orderExtra;
    private IOrderTotal orderTotal;

    public orderSkuAdapter(Context context,  ArrayList<orderSku> skuList,OutletObject outlet, OrderExtra orderExtra, IOrderTotal orderTotal) {
        this.context = context;
        this.skuList = skuList;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.outlet = outlet;
        this.orderExtra = orderExtra;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_ordersku, parent, false);
        }
        orderSku cursku = getSku(position);
        TextView textSkuName = (TextView) view.findViewById(R.id.textViewOrderSkuName);
        textSkuName.setText(cursku.skuName);
        textSkuName.setTag(position);
        textSkuName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEditSkuRow(v);
            }
        });
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

        ((TextView) view.findViewById(R.id.textViewPrice)).setText(String.format("%.2f",   cursku.price));
        ((TextView) view.findViewById(R.id.textViewSum)).setText(String.format("%.2f",   cursku.rowSum));

        ((TextView) view.findViewById(R.id.textViewMWH)).setText(String.format("%d", (long) cursku.stockG));
        ((TextView) view.findViewById(R.id.textViewRWH)).setText(String.format("%d", (long) cursku.stockR));
        Button btnEdit = (Button) view.findViewById(R.id.btnEditSkuRow);
        btnEdit.setTag(position);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // onClickEditSkuRow(v);
                deletePos(position);
            }
        });
        if (cursku.qtyMWH >0 || cursku.qtyRWH>0) {btnEdit.setVisibility(Button.VISIBLE); }
                else        {            btnEdit.setVisibility(Button.INVISIBLE);        }
        TextView edMWH =  (TextView) view.findViewById(R.id.editMWH);
        edMWH.setTag(position);
        edMWH.setText(cursku.getQtyMWHForEditText());

        TextView edRWH =  (TextView) view.findViewById(R.id.editRWH);
        edRWH.setTag(position);
        edRWH.setText(cursku.getQtyRWHForEditText());
//        edMWH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus)
//                {
//                    orderSku sku = getSku((int) v.getTag());
//                    Toast.makeText(context, ((EditText) v).getText(), Toast.LENGTH_SHORT ).show();
//
//                }
//            }
//        });
        /*edMWH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        return view;
    }

    private void onClickEditSkuRow(View v)
    {
        final orderSku sku = getSku((int) v.getTag());
        //Toast.makeText(context, sku.skuName, Toast.LENGTH_SHORT ).show();
        final Dialog dlgEditQty =  new Dialog(context);
        dlgEditQty.setTitle(sku.skuName);
        dlgEditQty.setContentView(R.layout.edit_orderqty);
        dlgEditQty.setCancelable(true);
        //((TextView) dlgEditQty.findViewById(R.id.tvEtitQtyDescription)).setText(sku.skuName);
        String stockStr = "Остатки: главный "+String.format("%d", (long)  sku.stockG)+"   розница "+String.format("%d", (long)  sku.stockR)
                + "     В ящ. "+String.format("%d", (int)  sku.getCountInBox());
        ((TextView) dlgEditQty.findViewById(R.id.tvEtitQtyStock)).setText(stockStr);

        final EditText dlgEditMWH = (EditText) dlgEditQty.findViewById(R.id.editDialogMWH);
        final EditText dlgEditRWH = (EditText) dlgEditQty.findViewById(R.id.editDialogRWH);
        dlgEditMWH.setText(sku.getQtyMWHForEditText());
        dlgEditRWH.setText(sku.getQtyRWHForEditText());
        Button btnOk =(Button) dlgEditQty.findViewById(R.id.btnEtitQtyOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!dlgEditMWH.getText().toString().trim().isEmpty()) {
                    sku.setQtyMWH(Integer.parseInt(dlgEditMWH.getText().toString()));
                    if (sku.qtyMWH % sku.getCountInBox() !=0)
                        Toast.makeText(context, "Количество по складу Главный не кратно ящику",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (sku.qtyMWH>0) sku.setQtyMWH(0);
                }

                if (!dlgEditRWH.getText().toString().trim().isEmpty()) {
                    sku.setQtyRWH(Integer.parseInt(dlgEditRWH.getText().toString()));
                    if (sku.qtyRWH % sku.getCountInBox() !=0)
                        Toast.makeText(context, "Количество по складу Розница не кратно ящику",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (sku.qtyRWH>0) sku.setQtyRWH(0);
                }
                //
                sku.saveDb(context);
                currentAdapter.notifyDataSetChanged();
                orderTotal.displayTotal();
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

    private void deletePos(int position)
    {
        final orderSku sku = getSku(position);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Удалить заказ по "+sku.skuName);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Да",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sku.setQtyMWH(0);
                        sku.setQtyRWH(0);
                        sku.deleteDb(context);
                        currentAdapter.notifyDataSetChanged();
                        orderTotal.displayTotal();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("Нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
