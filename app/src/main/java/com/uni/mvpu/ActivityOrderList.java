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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import Adapters.orderListAdapter;
import Entitys.Order;
import Entitys.OutletObject;
import core.AppSettings;
import core.OrderListMode;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IUpdateOrderList;


public class ActivityOrderList extends ActionBarActivity implements IUpdateOrderList {
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

    private OrderListMode orderMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        outletid = getIntent().getStringExtra("outletid");
        if (outletid.isEmpty()) {
            orderMode = OrderListMode.orderByDay;
        }
            else
        {
            orderMode = OrderListMode.ordersByOutlets;
        }
                //appManager.getOurInstance().getActiveOutletObject().outletId.toString();
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
        // ??????????? ??????
        lvMain = (ListView) findViewById(R.id.lvOrderList);
        btnAddOrder = (Button) findViewById(R.id.btnAddNewOrder);
        if (orderMode == OrderListMode.orderByDay)
        {
            btnAddOrder.setVisibility(View.INVISIBLE);
        }
        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddNewOrder(v);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Toast.makeText(this,"onPostResume", Toast.LENGTH_SHORT).show();
        updateOrderList();
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
            appManager.getOurInstance().sendDataToServer(this, this);
           // updateOrderList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private double getOrderSum(SQLiteDatabase db , int orderId )
    {
        double result = 0;
        String query="select sum(coalesce(d.qty1,0) * coalesce(p.pric,0) + coalesce(d.qty2,0) * coalesce(p.pric,0)) as orderSumma from orderHeader h " +
                " inner join orderDetail d on d.headerid = h._id " +
                " inner join (select outletId outletId,partnerId from  route) r on r.outletId = h.outletId "+
                " left  join contracts con on  con.PartnerId = r.partnerId "+
                " inner join price p on p.priceId = coalesce(con.PriceId,'"+ AppSettings.PARAM_PRICEID_DEFAULT+"') and d.skuId = p.skuId" +
                " where h._id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(orderId)});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            result = cursor.getDouble(0);
            cursor.moveToNext();
        }
        return  result;
    }

    private void fillOrders()
    {
        ArrayList<Order> orders = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("select  h._id,  h.orderUUID,DATETIME(h.orderDate) as orderDate,  h.outletId,  h.orderNumber , h.notes , " +
                " h.responseText, h._1CDocNumber1,  h._1CDocNumber2, h._send from orderHeader h where outletId = ? and DATETIME(orderDate) = ?",
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
            order.orderSum = getOrderSum(db, order._id);
            order.outletId = cursor.getString(cursor.getColumnIndex("outletId"));
            order.sended = cursor.getInt(cursor.getColumnIndex("_send")) == 1;
            orders.add(order);
            cursor.moveToNext();
        }
        db.close();
        olAdapter = new orderListAdapter(this, orders,outletid );
        lvMain.setAdapter(olAdapter);
        showTotal(orders);
    }

    private void fillOrdersByDay()
    {
        ArrayList<Order> orders = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("select  h._id,  h.orderUUID,DATETIME(h.orderDate) as orderDate,  h.outletId,  h.orderNumber , h.notes , " +
                        " h.responseText, h._1CDocNumber1,  h._1CDocNumber2, h._send from orderHeader h " +
                        " inner join (select distinct outletId from  route) r on r.outletId = h.outletId" +
                        " where   DATETIME(h.orderDate) = ?",
                new String[] { wputils.getDateTime(orderDate)});
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
            order.orderSum = getOrderSum(db, order._id);
            order.outletId = cursor.getString(cursor.getColumnIndex("outletId"));
            order.sended = cursor.getInt(cursor.getColumnIndex("_send")) == 1;
            orders.add(order);
            cursor.moveToNext();
        }
        db.close();
        olAdapter = new orderListAdapter(this, orders,outletid );
        lvMain.setAdapter(olAdapter);
        showTotal(orders);
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

            updateOrderList();
        }
    };

    private void onClickAddNewOrder(View v)
    {
        appManager.getOurInstance().addNewOrder(this, outletid, maxOrderNumber+1, orderDate);
        fillOrders();
    }

    private void updateOrderList()
    {
        if (orderMode == OrderListMode.ordersByOutlets) {
            fillOrders();
        }
        else
        {
            fillOrdersByDay();
        }
    }

    private void showTotal(ArrayList<Order> orders)
    {
        double totalSum =0;
        for (Order order:orders)
        {
            totalSum += order.orderSum;
        }
        ((TextView) findViewById(R.id.tvListOrdersSum)).setText(String.format("%.2f",   totalSum));
    }

    @Override
    public void UpdateList() {
        updateOrderList();
    }
}
