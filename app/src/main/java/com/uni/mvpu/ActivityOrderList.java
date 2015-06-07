package com.uni.mvpu;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import Adapters.orderListAdapter;
import Entitys.Order;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;


public class ActivityOrderList extends ActionBarActivity {
    private int maxOrderNumber = 0;
    private String outletid;
    private TextView tvOrderDate;
    private Button btnAddOrder;
    private ListView  lvMain;
    int DIALOG_DATE = 1;
//    int myYear = new Date().getYear();
//    int myMonth = new Date().getMonth();
//    int myDay = new Date().getDay();
    private Calendar orderDate;//= new Date();

    //ArrayList<Order> orders = new ArrayList<Order>();
    orderListAdapter olAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        outletid = appManager.getOurInstance().getActiveOutletObject().outletId.toString();
        // getIntent().getStringExtra("outletid");
        tvOrderDate = (TextView) findViewById(R.id.textViewOrderDate);
        orderDate = Calendar.getInstance();
        orderDate.setTime(new Date());
        tvOrderDate.setText(DateFormat.format("Дата заказа: dd.MM.yyyy", orderDate));


        tvOrderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDate(v);
            }
        });
                //olAdapter = new orderListAdapter(this, orders);
        // настраиваем список
        lvMain = (ListView) findViewById(R.id.lvOrderList);

        fillOrders();


        btnAddOrder = (Button) findViewById(R.id.btnAddNewOrder);
        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddNewOrder(v);
            }
        });
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
//        for (int i=0;i<100;i++)
//        {
//            Order order = new Order(i, i, UUID.randomUUID().toString(), new Date(), i* 1234);
//            orders.add(order);
//        }
        ArrayList<Order> orders = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("select  _id,  orderUUID,DATETIME(orderDate) as orderDate,  outletId,  orderNumber , notes , " +
                " responseText, _1CDocNumber1,  _1CDocNumber2, _send from orderHeader where outletId = ? and DATETIME(orderDate) = ?",
                new String[] {outletid, wputils.getDateTime(orderDate)});
        //, wputils.getDateTime(orderDate)
        cursor.moveToFirst();
        maxOrderNumber = cursor.getCount();
//        this._id = _id;
//        this.orderNumber = orderNumber;
//        this.orderUUID = orderUUID;
//        this.orderDate = orderDate;
//        this.orderSum = orderSum;
        for (int i = 0; i < cursor.getCount(); i++) {
            Order order = new Order( cursor.getInt(cursor.getColumnIndex("_id"))
                    , cursor.getInt(cursor.getColumnIndex("orderNumber"))
                    , cursor.getString(cursor.getColumnIndex("orderUUID")),
                    new Date(orderDate.get(Calendar.YEAR)-1900,
                            orderDate.get(Calendar.MONTH), orderDate.get(Calendar.DAY_OF_MONTH)),
                    0);

            orders.add(order);
            cursor.moveToNext();
        }
        db.close();
        olAdapter = new orderListAdapter(this, orders);
        lvMain.setAdapter(olAdapter);
    }

    public void onClickDate(View view) {
        showDialog(DIALOG_DATE);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, orderDate.get(Calendar.YEAR),
                    orderDate.get(Calendar.MONTH), orderDate.get(Calendar.DAY_OF_MONTH));
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
//            myYear = year;
//            myMonth = monthOfYear;
//            myDay = dayOfMonth;
            orderDate = new GregorianCalendar (year, monthOfYear, dayOfMonth);
            tvOrderDate.setText(DateFormat.format("Дата заказа: dd.MM.yyyy",orderDate));
            fillOrders();
        }
    };

    private void onClickAddNewOrder(View v)
    {
        appManager.getOurInstance().addNewOrder(this, outletid, maxOrderNumber+1, orderDate);
        fillOrders();
    }
}
