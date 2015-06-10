package Entitys;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.UUID;

import db.DbOpenHelper;

/**
 * Created by shestakov.g on 01.06.2015.
 */
public class OutletObject {

    public UUID outletId;
    public String outletName;
    public int dayOfWeek;
    public String dayOfWeekStr;
    public UUID customerId;
    public String customerName;
    public UUID priceId;
    public String priceName;
    public String outletAddress;
    public UUID partnerId;
    public String partnerName;

    public static OutletObject getInstance( UUID outletId, Context context)
    {
        OutletObject ob = new OutletObject();
        ob.outletId = outletId;
        //(CustomerId text, PriceId text, PriceName text, LimitSum double, Reprieve text, PartnerId text
        String query = "select r.outletId , r.outletName , r.VisitDay, r.VisitDayId ,r.VisitOrder, r.CustomerId,r.CustomerName," +
                " r.partnerId, r.partnerName, r.address, con.PriceId , con.PriceName from route r" +
                " left join contracts con on con.PartnerId =  r.partnerId " +
                " where r.outletId = ?";
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {outletId.toString()});
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount(); i++) {
            ob.outletName = cursor.getString(cursor.getColumnIndex("outletName"));
            ob.dayOfWeek = cursor.getInt(cursor.getColumnIndex("VisitDayId"));
            ob.dayOfWeekStr = cursor.getString(cursor.getColumnIndex("VisitDay"));
            ob.customerId =UUID.fromString(cursor.getString(cursor.getColumnIndex("CustomerId")));
            ob.customerName = cursor.getString(cursor.getColumnIndex("CustomerName"));
            ob.priceId =UUID.fromString(cursor.getString(cursor.getColumnIndex("PriceId")));
            ob.priceName = cursor.getString(cursor.getColumnIndex("PriceName"));
            ob.outletAddress= cursor.getString(cursor.getColumnIndex("address"));
            ob.partnerId =UUID.fromString(cursor.getString(cursor.getColumnIndex("partnerId")));
            ob.partnerName = cursor.getString(cursor.getColumnIndex("partnerName"));
            cursor.moveToNext();
        }
        db.close();
        return ob;
    }
}

