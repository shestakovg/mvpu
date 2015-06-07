package core;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.uni.mvpu.ActivityOrder;
import com.uni.mvpu.ActivityOrderList;
import com.uni.mvpu.ActivitySync;

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

/**
 * Created by g.shestakov on 26.05.2015.
 */
public class appManager {
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


    public OutletObject getActiveOutletObject() {
        return activeOutletObject;
    }

    public void setActiveOutletObject(OutletObject activeOutletObject) {
        this.activeOutletObject = activeOutletObject;
    }

    private  OutletObject activeOutletObject ;

    public void showOrderList(OutletObject outletObject, Context context)
    {
        Intent intent = new Intent(context, ActivityOrderList.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("outletid", outletObject.outletId);
        context.startActivity(intent);
    }

    public Intent getOrderActivityIntent(Order order, Context context)
    {
        Intent intent = new Intent(context, ActivityOrder.class);

        intent.putExtra("ORDER_OBJECT", order);
        return intent;
    }

    public ArrayList<priceType> getPriceType(Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
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

    public void addNewOrder(Context context, String outletid, int orderNumber, Calendar orderDate)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("orderUUID", UUID.randomUUID().toString());
        values.put("outletId", outletid);
        values.put("orderNumber", orderNumber);
        values.put("orderDate", wputils.getDateTime(orderDate));
        values.put("_send",0);
        db.insert("orderHeader", null, values);
        db.close();
    }

}

