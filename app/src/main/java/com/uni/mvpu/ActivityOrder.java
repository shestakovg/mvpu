package com.uni.mvpu;

import android.os.Parcelable;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import Entitys.Order;
import Entitys.OrderExtra;
import Entitys.OutletObject;
import core.appManager;
import interfaces.IOrder;


public class ActivityOrder extends  ActionBarActivity implements IOrder  {
    private Order orderObject;
    private OrderExtra orderExtra;
    private FragmentOrderSkuGroup fragGroup;
    private FragmentOrderSku  fragSku;

    private OutletObject currentOutlet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentOutlet = appManager.getOurInstance().getActiveOutletObject();
        setContentView(R.layout.activity_order);
        Bundle data = getIntent().getExtras();
        orderObject = (Order) data.getParcelable("ORDER_OBJECT");
        orderExtra = OrderExtra.intInstanceFromDb(orderObject);
        setTitle(getOutletObject().outletName+"  Заказ №: "+ orderObject.orderNumber);

        fragGroup =(FragmentOrderSkuGroup) getFragmentManager().findFragmentById(R.id.fragmentGroup);
        fragSku =(FragmentOrderSku) getFragmentManager().findFragmentById(R.id.fragmentSku);
        fragGroup.fillListViewGroupSku();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public OrderExtra getOrderExtra() {
        return orderExtra;
    }


    public void refreshSku(String skuGroup) {
        if (fragSku!=null) {
            fragSku.fillSku(orderExtra, skuGroup);
        }
    }

    @Override
    public OutletObject getOutletObject() {
        return appManager.getOurInstance().getActiveOutletObject();
    }
}
