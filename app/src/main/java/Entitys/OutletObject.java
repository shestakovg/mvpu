package Entitys;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.UUID;

import core.appManager;
import db.DbOpenHelper;

/**
 * Created by shestakov.g on 01.06.2015.
 */
public class OutletObject {

    public UUID outletId;
    public String outletName;
    public int dayOfWeek;
    public String dayOfWeekStr;
    public UUID customerId = new UUID(0L, 0L);
    public String customerName;
    public UUID priceId = new UUID(0L, 0L);
    public String priceName;
    public String outletAddress;
    public UUID partnerId = new UUID(0L, 0L);
    public String partnerName;
    public boolean IsRoute = false;
    public static OutletObject getInstance( UUID outletId, Context context)
    {
        OutletObject ob = new OutletObject();
        ob.outletId = outletId;
        //(CustomerId text, PriceId text, PriceName text, LimitSum double, Reprieve text, PartnerId text
        String query = "select r.outletId , r.outletName , r.VisitDay, r.VisitDayId ,r.VisitOrder, r.CustomerId,r.CustomerName," +
                " r.partnerId, r.partnerName, r.address, con.PriceId , con.PriceName, r.IsRoute from route r" +
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
            if  (cursor.getString(cursor.getColumnIndex("PriceId"))!=null)
                ob.priceId =UUID.fromString(cursor.getString(cursor.getColumnIndex("PriceId")));
            else
                ob.priceId = UUID.fromString(appManager.getOurInstance().appSetupInstance.getDefaultPrice());
            if  (cursor.getString(cursor.getColumnIndex("PriceName"))!=null)
                ob.priceName = cursor.getString(cursor.getColumnIndex("PriceName"));
            else
                ob.priceName = "Вид 1";
            ob.outletAddress= cursor.getString(cursor.getColumnIndex("address"));
            ob.partnerId =UUID.fromString(cursor.getString(cursor.getColumnIndex("partnerId")));
            ob.partnerName = cursor.getString(cursor.getColumnIndex("partnerName"));
            ob.IsRoute = (cursor.getInt(cursor.getColumnIndex("IsRoute")) == 1 ? true : false);
            cursor.moveToNext();
        }
        db.close();
        return ob;
    }
}

