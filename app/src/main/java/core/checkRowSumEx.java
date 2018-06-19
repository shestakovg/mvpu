package core;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import db.DbOpenHelper;

/**
 * Created by shest on 2/14/2017.
 */

public class checkRowSumEx{

    private String skuId;
    private Context context;
    private int minOrderQty = 100;

    public int getMaxOrderQty() {
        return maxOrderQty;
    }

    private int maxOrderQty = 1000000;
    public int getMinOrderQty() {
        return minOrderQty;
    }

    private checkRowSumEx(String skuId, Context context) {
        this.skuId = skuId;
        this.context = context;
        initializeDataFromDb();
    }

    public String getSkuPriceTitle()
    {
        int minOrder = getMinOrderQty();
        String result = "Минимальный заказ - "+minOrder+" шт"+" Максимальный - "+(maxOrderQty == 1000000 ? "Неограничен" : maxOrderQty +" шт");
        if (minOrder<0) result = "";
        return result;
    }

    private void initializeDataFromDb()
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select MinOrderQty, MaxOrderQty from sku where SkuId = ?", new String[] {this.skuId});
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            this.minOrderQty = cursor.getInt(0);
            this.maxOrderQty = cursor.getInt(1);
        }
        db.close();
    }

    public static checkRowSumEx GetInstance(String skuId, Context context)
    {
        return new checkRowSumEx(skuId, context);
    }
}
