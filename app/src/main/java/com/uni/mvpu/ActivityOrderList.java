package com.uni.mvpu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import Adapters.orderListAdapter;
import Entitys.Order;
import db.DbOpenHelper;


public class ActivityOrderList extends ActionBarActivity {
    ArrayList<Order> orders = new ArrayList<Order>();
    orderListAdapter olAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        olAdapter = new orderListAdapter(this, orders);
        fillOrders();
        olAdapter = new orderListAdapter(this, orders);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvOrderList);
        lvMain.setAdapter(olAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_order_list, menu);
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

    private void fillOrders()
    {
        for (int i=0;i<100;i++)
        {
            Order order = new Order(i, i,null, new Date(), i* 1234);
            orders.add(order);
        }
//        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
//        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select outletId , outletName , VisitDay  , VisitDayId ,VisitOrder , CustomerId ,CustomerName, partnerId, partnerName, address from route" + routeWhere + " order by VisitDayId,outletName ", null);
//        cursor.moveToFirst();
//        for (int i = 0; i < cursor.getCount(); i++) {
//
//        }
        //db.close();
    }
}
