package Entitys;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import core.AppSettings;
import core.wputils;
import db.DbOpenHelper;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class orderSku {
    private boolean isEdit = false;
    //************
    public String skuId;
    public String skuName;
    public int headerId;
    public String orderUUID;
    public long _id;
    public int qtyMWH;
    public int qtyRWH;
    public double rowSum;
    public double price;
    public double stockG;
    public double stockR;
    public String priceId = UUID.randomUUID().toString();
    public String priceName;
    public boolean checkMultiplicity = true;
    public boolean onlyFact = false;
    public int outletStock = 0;
    public String GroupName = "";
    public int PreviousOrderQty = 0;
    public String PreviousOrderDate = "";
    public int PreviousWarehouse = 0;
    public double OldPrice = 0;
    public double NewPrice = 0;
    public Boolean AvailiableInStore = true;
    public double getOldPrice() {
        return OldPrice;
    }

    public void setOldPrice(double oldPrice) {
        OldPrice = oldPrice;
    }

    public double getNewPrice() {
        return NewPrice;
    }

    public void setNewPrice(double newPrice) {
        NewPrice = newPrice;
    }

    public void setFinalDate(Calendar finalDate) {
        this.finalDate = finalDate;
        this.finalDateExists = true;
    }

    public Calendar finalDate = wputils.getCurrentDate();
    public boolean finalDateExists = false;
    public int getCountInBox() {
        return (countInBox == 0 ? 1 : countInBox);
    }

    public void setCountInBox(int countInBox) {
        this.countInBox = countInBox;
    }

    public int countInBox;

    public boolean isOnlyMWH() {
        return onlyMWH;
    }

    public void setOnlyMWH(boolean onlyMWH) {
        this.onlyMWH = onlyMWH;
    }

    private boolean onlyMWH;
    private boolean exist;
    public orderSku( String skuName, boolean exist) {

        this.skuName = skuName;
        this.exist = exist;
    }

    public String getQtyMWHForEditText()
    {
        if (qtyMWH == 0) return "";
        else
            return Integer.toString(qtyMWH);
    }

    public String getQtyRWHForEditText()
    {
        if (qtyRWH == 0) return "";
        else
            return Integer.toString(qtyRWH);
    }

    public void setQtyMWH(int qty)
    {
        this.qtyMWH = qty;
        isEdit = true;
        calcRowSum();
    }
    public void setQtyRWH(int qty)
    {
        this.qtyRWH = qty;
        isEdit = true;
        calcRowSum();
    }

    public void calcRowSum()
    {
        this.rowSum = qtyMWH * price + qtyRWH * price;
    }

    private void updatePosition(Context context, SQLiteDatabase db)
    {   boolean dbNotExist = db == null;
        if (db==null) {
            DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
            db = dbOpenHelper.getWritableDatabase();
        }
        db.execSQL("update orderDetail " +
                " set _send = 0, qty1 = "+qtyMWH+" , qty2 = "+qtyRWH+", priceId = '"+priceId+"' "+
                (finalDateExists ?
                    ",finalDate=(DATETIME('%Y-%m-%d',"+ finalDate.get(Calendar.YEAR)+","+finalDate.get(Calendar.MONTH)+", 1))"
                :"")+
                ", availableInStore = "+(AvailiableInStore ? 1 : 0)+
                " where _id = "+_id);
        //updateHeaderSend(AppSettings.ORDER_TYPE_ORDER, db);
        if (dbNotExist) db.close();
    }
    public void deleteFromDB(SQLiteDatabase db)
    {
        db.execSQL("delete from orderDetail where _id = "+_id);
    }
    public void saveDb(Context context, int orderType )
    {
        if (orderType != AppSettings.ORDER_TYPE_STOCK_TEMPLATE) {
            if (qtyMWH <= 0 && qtyRWH <= 0) return;
        }

        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (exist && (!finalDateExists || orderType == AppSettings.ORDER_TYPE_STOCK_TEMPLATE))
        {
            updatePosition(context, db);
        }
        else
        {

            deleteFromDB(db);
            ContentValues values = new ContentValues();
            values.put("SkuId", skuId);
            values.put("headerId", headerId);
            values.put("orderUUID", orderUUID);
            values.put("priceId", priceId);
            values.put("qty1", qtyMWH);
            values.put("qty2", qtyRWH);
            values.put("availableInStore", AvailiableInStore ? 1 : 0);
            if (finalDateExists)
                values.put("finalDate", wputils.getDateTime(finalDate));
            values.put("_send", 0);
            this._id = db.insert("orderDetail", null, values);
        }
        updateHeaderSend(orderType, db);
        exist = true;
        db.close();
    }

    private void updateHeaderSend(int orderType, SQLiteDatabase db) {
        if (orderType==AppSettings.ORDER_TYPE_ORDER)
            db.execSQL("update orderHeader " +
                    " set _send = 2 where _id = "+headerId);
        else if (orderType==AppSettings.ORDER_TYPE_STORECHECK)
            db.execSQL("update orderHeader " +
                    " set _send = 0 where _id = "+headerId);

    }


    public void deleteDb(Context context)
    {
        updatePosition(context, null);
    }

    public String Color;
    public String OutStockColor;

    public boolean isHoreca() {
        return IsHoreca;
    }

    public void setHoreca(boolean horeca) {
        IsHoreca = horeca;
    }

    public boolean IsHoreca;
}
