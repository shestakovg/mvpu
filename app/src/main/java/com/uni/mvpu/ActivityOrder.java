package com.uni.mvpu;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.UUID;

import Entitys.Order;
import Entitys.OrderExtra;
import Entitys.OutletObject;
import Entitys.orderSku;
import core.appManager;
import interfaces.IOrder;


public class ActivityOrder extends  ActionBarActivity implements IOrder  {
    private Order orderObject;
    private OrderExtra orderExtra;
    private FragmentOrderSkuGroup fragGroup;
    private FragmentOrderSku  fragSku;
    private OutletObject currentOutlet;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.activity_order_wide);}
//        else
//        {
//            setContentView(R.layout.activity_order);
//        }
        setContentView(R.layout.activity_order);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        FragmentOrderSku fragSku = new FragmentOrderSku();
//        ft.add(R.id.containerOrder, fragSku, "fragmentSku");
//        ft.addToBackStack(null);
//        ft.commit();

        currentOutlet = OutletObject.getInstance(UUID.fromString(getIntent().getStringExtra("OUTLETID")), this);
                //appManager.getOurInstance().getActiveOutletObject();

        Bundle data = getIntent().getExtras();
        orderObject = (Order) data.getParcelable("ORDER_OBJECT");
        orderExtra = OrderExtra.intInstanceFromDb(orderObject, this);
        setTitle(getOutletObject().outletName+"    Вид цен: "+getOutletObject().priceName+ "   Заказ №: "+ orderExtra.orderNumber);

        fragGroup = (FragmentOrderSkuGroup) getFragmentManager().findFragmentById(R.id.fragmentGroup);
        fragGroup.fillListViewGroupSku();
        fragSku = (FragmentOrderSku) getFragmentManager().findFragmentById(R.id.fragmentSku);
        fragSku.displayTotal();
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


    public void refreshSku(String skuGroup) {
        if (fragSku!=null && fragSku.isInLayout()) {
            fragSku.fillSku( skuGroup);
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
        dlgOrderParams.setTitle("Параметры заказа");
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
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"Кредит","Факт"});
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setPrompt("Тип продажи");


            dlgOrderParams.show();
        }
    }
