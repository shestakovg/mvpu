package com.uni.mvpu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import Entitys.Order;
import Entitys.OrderExtra;
import Entitys.OutletObject;
import Entitys.orderControlParams;
import Entitys.orderSku;
import core.AppSettings;
import core.TouchActivity;
import core.appManager;
import core.wputils;
import interfaces.IOrder;


public class ActivityOrder extends TouchActivity implements IOrder  {
    int DIALOG_DATE = 1;
    private Order orderObject;
    private OrderExtra orderExtra;
    private FragmentOrderSkuGroup fragGroup;
    private FragmentOrderSku  fragSku;
    private FragmenStorecheckSku  fragStorecheck;
    private FragmentStockTemplate  fragmentStockTemplate;
    private OutletObject currentOutlet;
    private Context context;

    //private TextView tvDeliveryDate;

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            orderExtra.setDeliveryDate( new GregorianCalendar (year, monthOfYear, dayOfMonth));
                    //wputils.getCalendarFromDate(new Date(year, monthOfYear,dayOfMonth));
            orderExtra.saveOrderParamsDb(getBaseContext());
            displayDeliveryDate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.activity_order_wide);}
//        else
//        {
//            setContentView(R.layout.activity_order);
//        }
        context = this;
        currentOutlet = OutletObject.getInstance(UUID.fromString(getIntent().getStringExtra("OUTLETID")), this);
        //appManager.getOurInstance().getActiveOutletObject();

        Bundle data = getIntent().getExtras();
        orderObject = (Order) data.getParcelable("ORDER_OBJECT");
        orderExtra = OrderExtra.intInstanceFromDb(orderObject, this);

        if (orderExtra.orderType == AppSettings.ORDER_TYPE_ORDER) {
            setContentView(R.layout.activity_order);
        }
        else if (orderExtra.orderType == AppSettings.ORDER_TYPE_STORECHECK)
        {
            setContentView(R.layout.activity_order_wide);
        }
        else if (orderExtra.orderType == AppSettings.ORDER_TYPE_STOCK_TEMPLATE)
        {
            setContentView(R.layout.activity_order_stock_template);
        }
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        fragGroup = new FragmentOrderSkuGroup();
//        ft.add(R.id.containerOrder, fragGroup, "fragmentGroup");
//                //(FragmentOrderSkuGroup) getFragmentManager().findFragmentById(R.id.fragmentGroup);
//        fragSku = new FragmentOrderSku();
//        ft.add(R.id.containerOrder, fragSku, "fragmentSku");
//        ft.addToBackStack(null);
//        ft.commit();

        if (orderExtra.orderType == AppSettings.ORDER_TYPE_ORDER) {
            setTitle(getOutletObject().outletName + "    Заказ: " + getOutletObject().priceName + "   Номер: " + orderExtra.orderNumber);
        }
        else
        if (orderExtra.orderType == AppSettings.ORDER_TYPE_STORECHECK) {
            setTitle(getOutletObject().outletName + "   Сторчек: " + orderExtra.orderNumber);
        }
        else if (orderExtra.orderType == AppSettings.ORDER_TYPE_STOCK_TEMPLATE) {
            setTitle(getOutletObject().outletName + "   Автозаказ: " + orderExtra.orderNumber);
        }
        fragGroup = (FragmentOrderSkuGroup) getFragmentManager().findFragmentById(R.id.fragmentGroup);
        try {
            fragGroup.fillListViewGroupSku();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (orderExtra.orderType == AppSettings.ORDER_TYPE_ORDER) {
            fragSku = (FragmentOrderSku) getFragmentManager().findFragmentById(R.id.fragmentSku);
                fragSku.displayTotal();
//            tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveyDateSku);
//            tvDeliveryDate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showDialog(DIALOG_DATE);
//                }
//            });
//            displayDeliveryDate();
        }

        if (orderExtra.orderType==AppSettings.ORDER_TYPE_STORECHECK)
        {
            fragStorecheck = (FragmenStorecheckSku) getFragmentManager().findFragmentById(R.id.fragmentStorecheck);
        }

        if (orderExtra.orderType==AppSettings.ORDER_TYPE_STOCK_TEMPLATE)
        {
            fragmentStockTemplate = (FragmentStockTemplate) getFragmentManager().findFragmentById(R.id.fragmentStockTemplateSku);
        }

    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {

            Calendar cal =orderExtra.deliveryDate;
            //cal.setTime(orderExtra.deliveryDate);

            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack,  cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),  cal.get(Calendar.DAY_OF_MONTH));
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    private void displayDeliveryDate()
    {
//        if (orderExtra.deliveryDateInitialized)
//            tvDeliveryDate.setText(DateFormat.format("Доставка: dd.MM.yyyy", orderExtra.deliveryDate));
//        else
//            tvDeliveryDate.setText("Доставка: не указано");
    }

    @Override
    public void onBackPressed() {
        if (fragSku!=null && fragSku.isInLayout()) {
            orderControlParams param = fragSku.displayTotal();
            if (!param.allowOrderToSave(orderExtra, currentOutlet, this.context))
            {
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setTitle(context.getString(R.string.orderControlMessage));
                ad.setMessage(param.getControlMessage(currentOutlet) + "\n"
                       // + (orderExtra.deliveryDateInitialized ? "" : "Дата доставки не указана!\n")
                        + "Заказ не будет отправлен!\nЗакрыть заказ?");
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        OrderExtra.setOrderToInactive(orderExtra, context);
                        finish();
                    }
                });
                ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                ad.show();
            }
            else
            {
                OrderExtra.setOrderToSend(orderExtra, context);
                finish();
//              if (orderExtra.deliveryDateInitialized) {
//                  OrderExtra.setOrderToSend(orderExtra, context);
//                  finish();
//              }
//              else
//              {
//                  Toast.makeText(this,"Дата доставки не указана!\nУкажите дату доставки!",Toast.LENGTH_SHORT).show();
//              }
            }
        } else if (fragmentStockTemplate!= null && fragmentStockTemplate.isInLayout()) {
            if (!fragmentStockTemplate.getOrderHasBeenGenerated()) {
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setTitle("Автозаказ");
                ad.setMessage("Автозаказ не сформирован\n"
                            + "Закрыть форму автозаказа?");
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        finish();
                    }
                });
                ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                ad.show();
            } else {
                super.onBackPressed();
            }

        }
        else
            super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //fragGroup.upToRootGroup();
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_order, menu);
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
            showOrderParamsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public OrderExtra getOrderExtra() {
        return orderExtra;
    }


    public void refreshSku(String skuGroup, boolean onlyStock, boolean onlyHoreca) throws ParseException {
        if (orderExtra.orderType == AppSettings.ORDER_TYPE_ORDER)
        {
            if (fragSku!=null && fragSku.isInLayout()) {
                fragSku.fillSku( skuGroup, false, onlyStock, onlyHoreca );
            }
        }
        else if (orderExtra.orderType == AppSettings.ORDER_TYPE_STORECHECK)
        {
            if (fragStorecheck!=null && fragStorecheck.isInLayout())
            {
                fragStorecheck.fillSku(skuGroup);
            }
        }
        else if (orderExtra.orderType == AppSettings.ORDER_TYPE_STOCK_TEMPLATE)
        {
            if (fragmentStockTemplate!=null && fragmentStockTemplate.isInLayout())
            {
                fragmentStockTemplate.fillSku(skuGroup);
            }
        }
    }

    @Override
    public OutletObject getOutletObject() {
        return currentOutlet;
    }

    @Override
    public void showOrderParams() {
        showOrderParamsDialog();
    }

    private void showOrderParamsDialog()
    {
        final OrderExtra currnentOrder = orderExtra;
        //Toast.makeText(context, sku.skuName, Toast.LENGTH_SHORT ).show();
        final Dialog dlgOrderParams =  new Dialog(this);
        dlgOrderParams.setTitle("Параметры");
        dlgOrderParams.setContentView(R.layout.dialog_order_params);
        dlgOrderParams.setCancelable(true);
        if (currnentOrder.payType == 0 )
            ((RadioGroup) dlgOrderParams.findViewById(R.id.radioGroupPayType)).check(R.id.radioBtnCredit);
        else
            ((RadioGroup) dlgOrderParams.findViewById(R.id.radioGroupPayType)).check(R.id.radioBtnFact);
        final CheckBox checkBoxAutoLoad = (CheckBox) dlgOrderParams.findViewById(R.id.checkboxAutoLoad);
        checkBoxAutoLoad.setChecked(currnentOrder.autoLoad);
        ((EditText)dlgOrderParams.findViewById(R.id.editTextOrderNote)).setText(currnentOrder.notes);
        Button btnOk =(Button) dlgOrderParams.findViewById(R.id.btnDialogOrderParamsOK);
        btnOk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //currnentOrder.payType
                        if
                           (((RadioGroup) dlgOrderParams.findViewById(R.id.radioGroupPayType)).getCheckedRadioButtonId()== R.id.radioBtnCredit)
                            currnentOrder.payType = 0;
                        else
                            currnentOrder.payType = 1;
                        currnentOrder.autoLoad = checkBoxAutoLoad.isChecked();
                        currnentOrder.notes = ((EditText)dlgOrderParams.findViewById(R.id.editTextOrderNote)).getText().toString();
                        currnentOrder.saveOrderParamsDb(getBaseContext());
                        dlgOrderParams.dismiss();
                    }
                }
        );
        ((Button) dlgOrderParams.findViewById(R.id.btnDialogOrderParamsCancel)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlgOrderParams.dismiss();
                    }
                }

        );
            //        (RadioButton) dlgOrderParams.findViewById(R.id.radioBtnCredit).
//        Spinner spinner = (Spinner) dlgOrderParams.findViewById(R.id.spinerOrderPayType);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"??????","????"});
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setPrompt("??? ???????");
        //TextView deliveryDate = (TextView) dlgOrderParams.findViewById(R.id.tvDeliveryDate);
        //deliveryDate.setText(DateFormat.format("Доставка: dd.MM.yyyy", currnentOrder.deliveryDate));
            dlgOrderParams.show();
        }
    }

