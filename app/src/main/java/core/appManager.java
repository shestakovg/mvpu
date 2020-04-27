package core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.uni.mvpu.ActivityDebt;
import com.uni.mvpu.ActivityOrder;
import com.uni.mvpu.ActivityOrderList;
import com.uni.mvpu.ActivitySync;
import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;


import Entitys.Order;
import Entitys.OutletObject;
import Entitys.priceType;
import db.DbOpenHelper;
import interfaces.IManagementGPSLogger;
import sync.sendOrders;
import sync.sendPays;
import sync.sendTask;

/**
 * Created by g.shestakov on 26.05.2015.
 */
public class appManager {
    public final int ANNOUNCED_EMPTY_PAY = -100;
    private static appManager ourInstance ; //= new appManager();

    public AppSettings appSetupInstance ;
    public static appManager getOurInstance() {
        if (ourInstance == null)
        {
//            Exception ex = new Exception("appManager not exist");
//
//            throw ex;
        }
        return ourInstance;
    }

    public static appManager getOurInstance(Context context) {
        if (ourInstance == null)
        {
            // Create the instance
            ourInstance = new appManager(context);
        }
        return ourInstance;
    }

    public static void setOurInstance(appManager ourInstance) {
        appManager.ourInstance = ourInstance;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    private Context currentContext;

//    public Context getSyncContext() {
//        return syncContext;
//    }
//
//    public void setSyncContext(Context syncContext) {
//        this.syncContext = syncContext;
//    }
//
//    private Context syncContext;
    private appManager() {
        this.appSetupInstance = new AppSettings();

    }

    private appManager(Context context) {
        this.currentContext = context;
        this.appSetupInstance = new AppSettings();
        loadSettingsFromDb();
    }
    private void loadSettingsFromDb()
    {
        this.appSetupInstance.readSetup(getCurrentContext());
    }
//    public static void initInstance()
//    {
//        if (ourInstance == null)
//        {
//            // Create the instance
//            ourInstance = new appManager();
//        }
//    }


//    public OutletObject getActiveOutletObject() {
//        return activeOutletObject;
//    }
//
//    public void setActiveOutletObject(OutletObject activeOutletObject) {
//        this.activeOutletObject = activeOutletObject;
//    }

    //private  OutletObject activeOutletObject ;

    public void showOrderList(OutletObject outletObject, Context context)
    {
        Intent intent = new Intent(context, ActivityOrderList.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("outletid", outletObject.outletId.toString());
        context.startActivity(intent);
    }

    public void showOrderList(OutletObject outletObject, Context context, int orderType)
    {
        Intent intent = new Intent(context, ActivityOrderList.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("outletid", outletObject.outletId.toString());
        intent.putExtra("orderType", orderType);
        intent.putExtra("outletCategoty", outletObject.Category);
        context.startActivity(intent);
    }

    public void showDebtList(OutletObject outletObject, Context context)
    {
        Intent intent = new Intent(context, ActivityDebt.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("customerid", outletObject.customerId.toString());
        intent.putExtra("onlyCustomer", "1");
        context.startActivity(intent);
    }

    public void showPayList( Context context)
    {
        Intent intent = new Intent(context, ActivityDebt.class);
        intent.putExtra("onlyCustomer", "0");
        context.startActivity(intent);
    }

    public void showOrderListByDay(Context context, int orderType )
    {
        Intent intent = new Intent(context, ActivityOrderList.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("outletid", "");
        intent.putExtra("orderType", orderType);
        context.startActivity(intent);
    }

    public Intent getOrderActivityIntent(Order order, Context context, String outletId)
    {
        Intent intent = new Intent(context, ActivityOrder.class);

        intent.putExtra("ORDER_OBJECT", order);
        intent.putExtra("OUTLETID", outletId);
        return intent;
    }

    public ArrayList<priceType> getPriceType(Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct PriceId, PriceName from contracts", null);
        cursor.moveToFirst();
        ArrayList<priceType> result = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            result.add(new priceType(cursor.getString(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

    public String addNewOrder(Context context, String outletid, int orderNumber, Calendar orderDate, int orderType)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String orderUUID = UUID.randomUUID().toString();
        values.put("orderUUID",orderUUID );
        values.put("outletId", outletid);
        values.put("orderNumber", orderNumber);
        values.put("orderDate", wputils.getDateTime(orderDate));
        Calendar deliveryDate=(Calendar) orderDate.clone();
        //deliveryDate.add(Calendar.DATE, 1);
        values.put("deliveryDate", wputils.getDateTime(deliveryDate));
        //values.putNull("deliveryDate");
        if (orderType==AppSettings.ORDER_TYPE_ORDER)
            values.put("_send",2);
        else
            values.put("_send",0);
        values.put("orderType", orderType);
        long headerid = db.insert("orderHeader", null, values);
        db.close();
        if (orderType == AppSettings.ORDER_TYPE_STORECHECK || appManager.getOurInstance().appSetupInstance.getRouteType()==1)
        {
            fillStorecheck(context, outletid, orderUUID, headerid);
        }
        else
        if (orderType == AppSettings.ORDER_TYPE_ORDER || appManager.getOurInstance().appSetupInstance.getRouteType()==1)
        {
            fillStorecheck(context, outletid, orderUUID, headerid);
        }
        else if (orderType == AppSettings.ORDER_TYPE_STOCK_TEMPLATE)
        {

        }
        else
        {
            fillDefaultOrder(context, outletid, orderUUID, headerid);
        }
        return orderUUID;
    }

    public void fillStorecheck(Context context, String outletid, String orderUUID, long headerid)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        OutletObject outletObject= OutletObject.getInstance(UUID.fromString(outletid), context);
        Cursor cursor =db.rawQuery("select skuid from specification where outletid=?", new String[]{outletid});
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++)
        {
            ContentValues values = new ContentValues();
            values.put("SkuId", cursor.getString(0));
            values.put("headerId", headerid);
            values.put("orderUUID", orderUUID);
            values.put("priceId", outletObject.priceId.toString());
            values.put("qty1", 0);
            values.put("qty2", 0);
            values.put("_send", 0);
            db.insert("orderDetail", null, values);
            cursor.moveToNext();
        }
        db.close();
    }

    public void fillDefaultOrder(Context context, String outletid, String orderUUID, long headerid)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        OutletObject outletObject= OutletObject.getInstance(UUID.fromString(outletid), context);
        Cursor cursor =db.rawQuery("select skuid from ClientCardSku where outletid=?", new String[]{outletid});
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++)
        {
            ContentValues values = new ContentValues();
            values.put("SkuId", cursor.getString(0));
            values.put("headerId", headerid);
            values.put("orderUUID", orderUUID);
            values.put("priceId", outletObject.priceId.toString());
            values.put("qty1", 0);
            values.put("qty2", 0);
            values.put("availableInStore", 0);
            values.put("_send", 0);
            db.insert("orderDetail", null, values);
            cursor.moveToNext();
        }
        db.close();
    }
    public void sendDataToServer(Context context, Activity owner)
    {
        sendOrders so = new  sendOrders(context, owner);
        so.execute();
        sendPays pays = new sendPays(context);
        pays.execute();
        new sendTask(context).execute();
    }

    public double getOverdueSum(Context context,String customerId, Calendar date)
    {
        double result=0;
        SQLiteDatabase db = new DbOpenHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("select sum(d.overdueDebt - coalesce(p.paySum,0)) overSum from debts d " +
                "left join pays p on p.transactionId = d.transactionId and p.payDate = ?" +
                "where d.customerid= ?  and  d.overdueDebt > 0",
                new String[] { wputils.getDateTime(date), customerId});
        if (cursor.moveToFirst()) {
            result = cursor.getDouble(0);
        }
        if (result < 0 ) result = 0;
        db.close();
        return result;
    }


    public Boolean checkAnnouncedSum(Context context,String customerId, Calendar date)
    {
        double paySum=0;
        SQLiteDatabase db = new DbOpenHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("select coalesce(sum(p.paySum), 0) overSum, coalesce(sum(d.debt), 0) alldebt from debts d " +
                        "left join pays p on p.transactionId = d.transactionId and p.payDate = ?" +
                        "where d.customerid= ? ",
                new String[] { wputils.getDateTime(date), customerId});
        cursor.moveToFirst();
        paySum = cursor.getDouble(0);
        double alldebt =  cursor.getDouble(1);
        db.close();
        if (paySum > 0 || alldebt == 0) return true;
        return false;
    }

    public String getCustomerName(String customerId, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct CustomerName from route where customerid = ?", new String[]  {customerId});
        cursor.moveToFirst();
        String result = cursor.getString(0);
        db.close();
        return result;
    }

    public IManagementGPSLogger gpsLoggerManager;

    private  boolean mResult = false;

    public boolean getYesNoWithExecutionStop(String title, String message, Context context, int iconId, boolean showNoButton) {

        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        //alert.setIcon(R.drawable.placeholder);
        if (iconId > 0)
                    alert.setIcon(iconId);
        alert.setMessage(message);

        alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        if (showNoButton) {
            alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mResult = false;
                    handler.sendMessage(handler.obtainMessage());
                }
            });
        }
        alert.show();

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        return mResult;
    }

}

