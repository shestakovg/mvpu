package Entitys;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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
                " where _id = "+_id);
        if (dbNotExist) db.close();
    }
    public void deleteFromDB(SQLiteDatabase db)
    {
        db.execSQL("delete from orderDetail where _id = "+_id);
    }
    public void saveDb(Context context)
    {
        if (qtyMWH<=0 && qtyRWH<=0) return;

        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (exist && !finalDateExists)
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
            if (finalDateExists)
                values.put("finalDate", wputils.getDateTime(finalDate));
            values.put("_send", 0);
            this._id = db.insert("orderDetail", null, values);
        }
        db.execSQL("update orderHeader " +
                        " set _send = 0 where _id = "+headerId);
        exist = true;
        db.close();
    }

    public void deleteDb(Context context)
    {
        updatePosition(context, null);
    }


}
