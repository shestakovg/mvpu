package core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public void SaveLocationData(double latitude, double longtitude,long  time)
    {
        Date date = new Date(time);
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
