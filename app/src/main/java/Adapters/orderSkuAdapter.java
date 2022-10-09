package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import java.util.ArrayList;

import Entitys.OrderExtra;
import Entitys.OutletObject;
import Entitys.orderSku;
import core.AppSettings;
import core.appManager;
import core.checkRowSumEx;
import core.priceTypeManager;
import db.DbOpenHelper;
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
                textSkuName.setTextColor(Color.parseColor(cursku.OutStockColor));
        }
        else
        {
                textSkuName.setTypeface(null, Typeface.BOLD);
                textSkuName.setTextColor(Color.parseColor(cursku.Color));
        }

        TextView priceView  =  ((TextView) view.findViewById(R.id.textViewPrice));
        TextView priceViewDescription  =  ((TextView) view.findViewById(R.id.textViewPriceDescription));
        priceView.setText(String.format("%.2f",   cursku.price));
        priceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayAllPrices(position);
            }
        });
        priceViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayAllPrices(position);
            }
        });

        ((TextView) view.findViewById(R.id.textViewSum)).setText(String.format("%.2f",   cursku.rowSum));

        ((TextView) view.findViewById(R.id.textViewMWH)).setText(String.format("%d", (long) cursku.stockG));
        ((TextView) view.findViewById(R.id.textViewRWH)).setText(String.format("%d", (long) cursku.stockR));
        ((TextView) view.findViewById(R.id.textPrevOrderDate)).setText(cursku.PreviousOrderDate);
        //((TextView) view.findViewById(R.id.textPrevQty)).setText(String.format("%d", (long) cursku.PreviousOrderQty));
        ((TextView) view.findViewById(R.id.textPrevQty)).setText(Integer.toString(cursku.PreviousOrderQty));
//        ((TextView) view.findViewById(R.id.textPrevQty)).setText("100");
        ((TextView) view.findViewById(R.id.textPrevQty)).setTextColor(Color.BLACK);
//        ((TextView) view.findViewById(R.id.textViewOrderSkuGroupName)).setText(cursku.GroupName);
        if (cursku.PreviousOrderDate.isEmpty())
            ((TextView) view.findViewById(R.id.textPrevOrderCaption)).setTextColor(Color.parseColor("#bdbdbd"));
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

        TextView onlyFact = (TextView) view.findViewById(R.id.textViewOrderOnlyFact);
        if (cursku.onlyFact)
        {
            onlyFact.setText(context.getText(R.string.StringFact));
        }
        else
        {
            onlyFact.setText("");
        }
        edRWH.setTag(position);
        edRWH.setText(cursku.getQtyRWHForEditText());
        TextView outletStock =  (TextView) view.findViewById(R.id.textOutletStock);
        if (outletStock!=null)
            outletStock.setText(Integer.toString(cursku.outletStock));

        final Spinner spinnerPriceType = (Spinner) view.findViewById(R.id.spinnerPriceType);
        spinnerPriceType.setTag(position);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, priceTypeManager.getInstance().getPriceNameArray(outlet.priceName));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriceType.setAdapter(adapter);
        final int spinnerPosition = adapter.getPosition(cursku.priceName);
        spinnerPriceType.setSelection(spinnerPosition);
//        spinnerPriceType.post(new Runnable() {
//            @Override
//            public void run() {
//                spinnerPriceType.setSelection(spinnerPosition);
//            }
//        });
        spinnerPriceType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSpinnerPriceTypeItemSelected(parent, view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (cursku.IsHoreca)
            view.setBackgroundColor(Color.YELLOW);
        else
            view.setBackgroundColor(Color.WHITE);

        if (cursku.getOldPrice() != cursku.getNewPrice()) {
            view.setBackgroundResource(R.drawable.image_border);
            priceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPriceChanges(position);
                }
            });
        }
        TextView availInStore =  (TextView) view.findViewById(R.id.textAvailableInStore);
        availInStore.setText(cursku.AvailiableInStore ? R.string.title_available_in_store : R.string.title_not_available_in_store);
        if (!cursku.AvailiableInStore) {
            availInStore.setTextColor(Color.RED);
        } else {
            availInStore.setTextColor(Color.BLACK);
        }

        return view;
    }


    private boolean getOnlyFact(orderSku sku)
    {
        if (outlet.CustomerClass.equals(AppSettings.CUSTOMER_CLASS_CREDIT))
        {
            return false;
        }

        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from skuFact where skuId=? and priceId=?", new String[]{sku.skuId, sku.priceId});
        boolean result = false;
        for (int i=0; i<cursor.getCount(); i++ )
        {
            result=true;
            break;
            //cursor.moveToNext();
        }
        db.close();
        return result;
    }

    private void onSpinnerPriceTypeItemSelected(AdapterView<?> parent, View view,int position, long id)
    {
        int pos = (Integer) parent.getTag();
        orderSku cursku = (orderSku)getItem(pos);
        if (!cursku.priceName.equals(parent.getAdapter().getItem(position).toString()))
        {
            cursku.priceName = parent.getAdapter().getItem(position).toString();
            cursku.priceId = priceTypeManager.getInstance().getPriceIdByPriceName(cursku.priceName, outlet.priceId.toString());
            cursku.price = priceTypeManager.getInstance().getPriceValue(cursku.skuId, cursku.priceId);
            cursku.onlyFact = getOnlyFact(cursku);
            if (!parent.getAdapter().getItem(position).toString().equals(appManager.getOurInstance().appSetupInstance.getPriceTypeWithoutRestrictions()))
            {
                checkRowSumEx chrs =  checkRowSumEx.GetInstance(cursku.skuId, context);
                if ((cursku.qtyMWH+cursku.qtyRWH) !=0 && ((cursku.qtyMWH+cursku.qtyRWH) < chrs.getMinOrderQty()))
                {
                    cursku.qtyRWH = 0;
                    String message =context.getText(R.string.order_qty_less_min_order) +"\n"+context.getText(R.string.qty_will_clear);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

                if ((cursku.qtyMWH+cursku.qtyRWH) !=0 && ((cursku.qtyMWH+cursku.qtyRWH) > chrs.getMaxOrderQty()))
                {
                    cursku.qtyRWH = 0;
                    String message =context.getText(R.string.order_qty_more_max_order) +"\n"+context.getText(R.string.qty_will_clear);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
            cursku.calcRowSum();
            cursku.saveDb(context, orderExtra.orderType);
            currentAdapter.notifyDataSetChanged();
            orderTotal.displayTotal();

        }
    }

    private void onClickEditSkuRow(View v)
    {

        final orderSku sku = getSku((int) v.getTag());
        if ((sku.stockG + sku.stockR)==0)
        {
            Toast.makeText(context, sku.skuName+" "+ context.getText(R.string.empty_stock), Toast.LENGTH_SHORT ).show();
            return;
        }

        final boolean hasSkuDEscription = sku.skuDescription.equals("") ? false :  true;
        final Dialog dlgEditQty =  new Dialog(context);
        dlgEditQty.setContentView(R.layout.edit_orderqty_descr);

        final EditText dlgEditMWH = (EditText) dlgEditQty.findViewById(R.id.editDialogMWH);
        final EditText dlgEditRWH = (EditText) dlgEditQty.findViewById(R.id.editDialogRWH);
        final TextView txtRWHname = (TextView) dlgEditQty.findViewById(R.id.textRWHname);
        final TextView skuDescription = (TextView) dlgEditQty.findViewById(R.id.skuDescription);

        dlgEditQty.setTitle(sku.skuName);

        dlgEditQty.setCancelable(true);

        final int windowWidth = (int)(context.getResources().getDisplayMetrics().widthPixels );
       // int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.50);

        //((TextView) dlgEditQty.findViewById(R.id.tvEtitQtyDescription)).setText(sku.skuName);
        String stockStr =context.getText(R.string.StringStock)+": "+context.getText(R.string.StringMainWH) +"  "+String.format("%d", (long)  sku.stockG)+"    "+context.getText(R.string.StringRWH)+String.format("%d", (long)  sku.stockR)
                + "     "+ context.getText(R.string.StringInBox)+" "+String.format("%d", (int)  sku.getCountInBox());
        ((TextView) dlgEditQty.findViewById(R.id.tvEtitQtyStock)).setText(stockStr);



        checkRowSumEx chrs =  checkRowSumEx.GetInstance(sku.skuId, context); // new checkRowSum(sku.price);
        ((TextView) dlgEditQty.findViewById(R.id.editQtyTextMessage)).setText(chrs.getSkuPriceTitle());

        final int  minOrderQty =  chrs.getMinOrderQty();
        final int  maxOrderQty =  chrs.getMaxOrderQty();

        dlgEditMWH.setText(sku.getQtyMWHForEditText());
        dlgEditRWH.setText(sku.getQtyRWHForEditText());
        skuDescription.setText(sku.skuDescription);
        skuDescription.setKeyListener(null);
        if (!hasSkuDEscription) {
            skuDescription.setVisibility(View.INVISIBLE);
        }

        if (sku.isOnlyMWH()) {
            dlgEditRWH.setEnabled(false);
            txtRWHname.setPaintFlags(txtRWHname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        Button btnOk =(Button) dlgEditQty.findViewById(R.id.btnEtitQtyOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int locQtyMWH = 0; int locQtyRWH = 0;
                boolean allowClose = true;
                if (!dlgEditMWH.getText().toString().trim().isEmpty()) {
                    int enteredQty = Integer.parseInt(dlgEditMWH.getText().toString());
                    locQtyMWH = enteredQty;
                    if (enteredQty % sku.getCountInBox() != 0) {
                        if (sku.checkMultiplicity) allowClose = false;
                        Toast.makeText(context, context.getText(R.string.non_multiply_mvh), Toast.LENGTH_LONG).show();
                        AlertDialog.Builder ad = new AlertDialog.Builder(context);
                        ad.setTitle(context.getString(R.string.pAlert));
                        ad.setMessage(context.getText(R.string.non_multiply_mvh) + " : " + sku.skuName + "  -  " + String.format("%d", (long) enteredQty) + " ??");
                        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {
                            }
                        });
//                        ad.setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int arg1) {
//
//                            }
//                        });
                        ad.show();
                    }
                    if (allowClose)
                        sku.setQtyMWH(Integer.parseInt(dlgEditMWH.getText().toString()));
                } else {
                    if (sku.qtyMWH > 0) sku.setQtyMWH(0);
                }

                if (!dlgEditRWH.getText().toString().trim().isEmpty() ) {
                    int enteredQty = Integer.parseInt(dlgEditRWH.getText().toString());
                    locQtyRWH = enteredQty;
                    if (!appManager.getOurInstance().appSetupInstance.checkPriceTypeForRestrictions(sku.priceName) || ((locQtyMWH+locQtyRWH) >= minOrderQty) )
                    {
                        sku.setQtyRWH(locQtyRWH);
                        if (sku.qtyRWH % sku.getCountInBox() != 0)
                            Toast.makeText(context, context.getText(R.string.non_multiply_rvh), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (sku.qtyRWH > 0) sku.setQtyRWH(0);
                }
                //

                if (appManager.getOurInstance().appSetupInstance.checkPriceTypeForRestrictions(sku.priceName)
                        && (locQtyMWH+locQtyRWH) !=0 && ((locQtyMWH+locQtyRWH) < minOrderQty))
                {
                    allowClose = false;
                    Toast.makeText(context, context.getText(R.string.order_qty_less_min_order), Toast.LENGTH_LONG).show();
                }

                if ((locQtyMWH+locQtyRWH) !=0 && ((locQtyMWH+locQtyRWH) > maxOrderQty))
                {
                    allowClose = false;
                    Toast.makeText(context, context.getText(R.string.order_qty_more_max_order), Toast.LENGTH_LONG).show();
                }

                if (allowClose) {
                    sku.saveDb(context, orderExtra.orderType);
                    currentAdapter.notifyDataSetChanged();
                    orderTotal.displayTotal();
                    dlgEditQty.dismiss();
                }
            }
        });

        Button btnCancel =(Button) dlgEditQty.findViewById(R.id.btnEtitQtyCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgEditQty.dismiss();
            }
        });

        dlgEditQty.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
//                View view = dialog.findViewById(R.id.dialog_main_layout);
//                int width = view.getWidth();
//                int height = view.getHeight();
                int height = dlgEditQty.getWindow().getDecorView().getHeight();
                int width = dlgEditQty.getWindow().getDecorView().getWidth();
                int dlgWidth = hasSkuDEscription ? windowWidth : width;
                dlgEditQty.getWindow().setLayout(dlgWidth, height);

                if (hasSkuDEscription) {
                    skuDescription.setWidth((int) (dlgWidth * 0.35));
                }
            }
        });
        dlgEditQty.show();
    }

    private void deletePos(int position)
    {
        final orderSku sku = getSku(position);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(context.getText(R.string.deleteSku)+sku.skuName);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
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
        builder1.setNegativeButton(context.getText(R.string.dialogNo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void DisplayAllPrices(int position)
    {
        final orderSku sku = getSku(position);
        String messageText = "";
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select priceTypes.PriceName, coalesce(p.pric,0)  from price p " +
                " inner join " +
                "(select distinct PriceId, PriceName from contracts " +
                " union" +
                " select 'b07c23b6-ed8d-11e4-9bea-3640b58dd6a2', '–€ÕŒ  Œœ“Œ¬€…'" +
                " union" +
                " select '849d3a4e-f26e-11e5-900e-3640b58dd6a2', 'VIP  ÎËÂÌÚ˚'" +
                " union" +
                " select 'e3c64316-daa6-11e4-826d-240a64c9314e', ' ÛÔÌ˚È ÓÔÚ ‘¿ “') priceTypes" +
                " on priceTypes.PriceId = p.priceId" +
                " where  p.skuId = ?", new String[] {sku.skuId});
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++)
        {
//            result.add(new priceType(cursor.getString(0), cursor.getString(1)));
            messageText += cursor.getString(0)+" - "+ cursor.getDouble(1)+"\n";
            cursor.moveToNext();
        }
        db.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("÷ÂÌ˚")
                .setMessage(messageText)
                .setIcon(R.drawable.hrn)
                .setCancelable(false)
                .setNegativeButton("Œ ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showPriceChanges(int position) {
        final orderSku sku = getSku(position);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(context.getText(R.string.NewPriceText)+" "
                +String.format("%.2f",   sku.getNewPrice())+"\n"+
                context.getText(R.string.OldPriceText)+" "+String.format("%.2f",   sku.getOldPrice())+"\n"+
                context.getText(R.string.PriceChangesText)+" "+String.format("%.2f", sku.getNewPrice() - sku.getOldPrice())+"\n"+
                context.getText(R.string.PriceChangesPercentText)+" "+String.format("%.2f", (sku.getNewPrice() - sku.getOldPrice())/sku.getOldPrice() * 100)
        );
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
