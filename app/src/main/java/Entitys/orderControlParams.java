package Entitys;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import core.wputils;
import db.DbOpenHelper;
import interfaces.IOrder;
import interfaces.IOrderControlParams;

/**
 * Created by shestakov.g on 13.07.2015.
 */
public class orderControlParams {
    private int orderRows;
    private double orderSum;
    private boolean onlyFactSku = true;
    private boolean financeControl = true;
    private IOrderControlParams params;
    private OrderExtra order;
    public orderControlParams(int orderRows, double orderSum, IOrderControlParams params) {
        this.orderRows = orderRows;
        this.orderSum = orderSum;
        this.params = params;
    }

    public orderControlParams(int orderRows, double orderSum, IOrderControlParams params, OrderExtra order) {
        this.orderRows = orderRows;
        this.orderSum = orderSum;
        this.params = params;
        this.order = order;
    }
    private void loadSumByOutlet(OrderExtra orderExtra,OutletObject currentOutlet,  Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String query="select sum(coalesce(d.qty1,0) * coalesce(p.pric,0) + coalesce(d.qty2,0) * coalesce(p.pric,0)) as orderSumma," +
                " count(d._id) rowCount from orderHeader h " +
                " inner join orderDetail d on d.headerid= h._id" +
                " left join price p on d.skuId = p.skuId" +
                " where DATETIME(h.orderDate) = ? and  h.outletId = ? and p.priceId= ? and (coalesce(d.qty1,0)>0 or coalesce(d.qty2,0)>0)";
        Cursor cursor = db.rawQuery(query, new String[] {wputils.getDateTime(orderExtra.orderDateCalendar), orderExtra.outletId,
                currentOutlet.priceId.toString()});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            this.orderSum = cursor.getDouble(0);
            this.orderRows = cursor.getInt(1);
            cursor.moveToNext();
        }
        db.close();
    }

    private void checkOnlyFactSku(OrderExtra orderExtra, Context context)
    {
        if (order.payType !=0)
        {
            this.onlyFactSku =true;
            return;
        }
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String query="select count(*) from orderDetail d inner join skuFact s on s.SkuId = d.skuid and d.PriceId=s.PriceId where d.orderUUID = ? and (coalesce(d.qty1,0)+coalesce(d.qty2,0))<>0";
        Cursor cursor = db.rawQuery(query,new String[] {orderExtra.orderUUID});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (cursor.getInt(0)>0) this.onlyFactSku = false;
            cursor.moveToNext();
        }
        db.close();
    }
    public Boolean allowOrderToSave(OrderExtra orderExtra,OutletObject currentOutlet, Context context)
    {
        if (this.orderRows ==0 && this.orderSum == 0) return true;
        loadSumByOutlet(orderExtra, currentOutlet, context);
        checkOnlyFactSku(orderExtra,context);
        if (//this.orderRows < params.getMinOrderRowsQty() &&
                this.orderSum < this.params.getMinOrderSum())
        {
            this.financeControl = false;
            return false;
        }
       // checkOnlyFactSku(context);
        if (!this.onlyFactSku) return false;
        return true;
    }

    public String getControlMessage()
    {
        String result = "";
//        if (this.orderRows < params.getMinOrderRowsQty() && !this.financeControl)
//        {
//            result+="В заказе должно быть минимум "+Integer.toString(this.params.getMinOrderRowsQty())+" строк. В заказе: "+Integer.toString(this.orderRows) +" стр\n";
//        }
        if (this.orderSum < this.params.getMinOrderSum() && !this.financeControl)
        {
            result+="Сумма заказа должна быть не меньше "+String.format("%.2f", this.params.getMinOrderSum())+" грн. Сумма заказа: "+String.format("%.2f", this.orderSum)+"\n";
        }
        if (!this.onlyFactSku)
            result+="В заказе есть позиции, которые можно отгружать только по факту!";

        return result;
    }


}
