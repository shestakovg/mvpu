package Entitys;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import core.wputils;
import db.DbOpenHelper;

/**
 * Created by g.shestakov on 04.06.2015.
 */
public class OrderExtra extends Order {

    public boolean autoLoad = false;
    public int payType = 0;
    public OrderExtra() {

    }



    public OrderExtra(Order order, Context context) {
        //super(order._id, );
        this._id = order._id;
        fillOrderExtra(context);
    }

    public void fillOrderExtra(Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select  h.orderUUID, h.outletId, h.orderNumber, h.notes, h._1CDocNumber1, h._1CDocNumber2, " +
                " h.responseText, h.payType , h.autoLoad ,(strftime('%s', h.deliveryDate) * 1000)  deliveryDate  " +
            " from orderHeader h where h._id = ?", new String[]{Integer.toString(this._id)});
        cursor.moveToFirst();
        for (int i=0; i<cursor.getCount(); i++)
        {
            this.orderUUID = cursor.getString(cursor.getColumnIndex("orderUUID"));
            this.orderNumber = cursor.getInt(cursor.getColumnIndex("orderNumber"));
            this.notes = cursor.getString(cursor.getColumnIndex("notes"));
            this._1CDocNumber1 = cursor.getString(cursor.getColumnIndex("_1CDocNumber1"));
            this._1CDocNumber2 = cursor.getString(cursor.getColumnIndex("_1CDocNumber2"));
            this.responseText = cursor.getString(cursor.getColumnIndex("responseText"));
            this.outletId =  cursor.getString(cursor.getColumnIndex("outletId"));
            this.payType = cursor.getInt(cursor.getColumnIndex("payType"));
            this.autoLoad = (cursor.getInt(cursor.getColumnIndex("autoLoad")) > 0 ? true : false);
            this.deliveryDate = wputils.getCalendarFromDate(new Date(cursor.getLong((cursor.getColumnIndex("deliveryDate")))));
            cursor.moveToNext();
        }
        db.close();
    }

    public static OrderExtra intInstanceFromDb(Order order, Context context)
    {
        return new OrderExtra(order, context);
    }

    public void saveOrderParamsDb(Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        String dmlQuery = "update orderHeader set _send =0, payType =  "+this.payType+", autoLoad = "+(this.autoLoad ? 1 :0)+
                ", notes = '"+this.notes+"', deliveryDate = '"+wputils.getDateTime(deliveryDate)+"' "+
                " where _id = "+this._id;
        //db.execSQL(dmlQuery, new String[] {wputils.getDateTime(wputils.getCalendarFromDate(deliveryDate))});
        db.execSQL(dmlQuery);
        db.close();
    }
}
