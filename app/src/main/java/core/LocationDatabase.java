package core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import db.DbOpenHelper;

/**
 * Created by shestakov.g on 29.11.2015.
 */

public class LocationDatabase {
    private Context context;

    public static LocationDatabase locDb;

    public static LocationDatabase getInstance()
    {
         //throw new Exception("Location Database instance is null");
                return locDb;
    }

    public LocationDatabase(Context context) {
        this.context = context;
    }

    private void deleteOldData()
    {

    }

    private double latitude;

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.isLocated = true;
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.isLocated = true;
        this.latitude = latitude;
    }

    private double longtitude;

    private boolean isLocated = false;

    public boolean isLocated() {
        return isLocated;
    }

    public void setLocated(boolean located) {
        isLocated = located;
    }

    private long  sateliteTime;

    public long getSateliteTime() {
        return sateliteTime;
    }

    public void setSateliteTime(long sateliteTime) {
        this.sateliteTime = sateliteTime;
    }

    public void SaveLocationData(double latitude, double longtitude, long  time)
    {
        Date date = new Date(time);
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("routeDayId", (this.currentRoute == null ? -1 : this.currentRoute.getId()));
        values.put("longtitude", longtitude);
        values.put("latitude", latitude);
        values.put("logDate", wputils.getDateTime(wputils.getCalendarFromDate(date)));
        values.put("sateliteTime", time);
        db.insert("gpsLog", null, values);
        db.close();
        //Toast.makeText(this.context, "location saved. Date: "+ wputils.getDateStringFromLong(time)+" "+DateFormat.getTimeInstance().format(time), Toast.LENGTH_LONG).show();
    }

    public void SaveOutletCheckIn(String outletId, Calendar date)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("outletId", outletId);
        values.put("longtitude", this.longtitude);
        values.put("latitude", this.latitude);
        values.put("logDate",  wputils.getDateTime(date));
        values.put("sateliteTime", this.sateliteTime);
        db.insert("outletCheckIn", null, values);
        db.close();
        //Toast.makeText(this.context, "Saved", Toast.LENGTH_LONG).show();
    }

    public boolean IsOutletCheckIn(String outletId,  Calendar date)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) cnt from outletCheckIn where outletId = ? and DATETIME(logDate) = ?", new String[] {outletId, wputils.getDateTime(date)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        db.close();
        return (count > 0 ? true : false);
        //where outletId = ? and DATETIME(orderDate) = ?
    }

    public RouteDay currentRoute = null;
    public RouteDay getActiveRouteDay(Calendar routeDate)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select _id, status from routesDay where DATETIME(routeDay) = ? and status=0 order by _id asc", new String[]{wputils.getDateTime(routeDate)});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            this.currentRoute = new RouteDay(cursor.getInt(0), routeDate, cursor.getInt(1));
            cursor.moveToNext();
        }
        db.close();
        return this.currentRoute;
    }

    public RouteDay openRouteDay(Calendar routeDate)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("routeDay", wputils.getDateTime(routeDate));
        db.insert("routesDay", null, values);
        db.close();
        return getActiveRouteDay(routeDate);
    }

    public void closeRouteDate(RouteDay route)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update routesDay set status = 1 where _id = ?", new String[]{Integer.toString(route.getId())});
        db.close();
        this.currentRoute = null;
    }
}
