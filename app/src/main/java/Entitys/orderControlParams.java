package Entitys;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import core.AppSettings;
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
    private OutletObject _outlet;
    private OrderSumControl orderSumControl = new OrderSumControl();
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
        String query="select sum(coalesce(d.qty1,0) * coalesce(p.pric,0) + coalesce(d.qty2,0) * coalesce(p.pric,0)) as orderSumma,  p.priceId as priceId " +
                "  from orderHeader h " +
                //" count(d._id) rowCount from orderHeader h " +
                " inner join orderDetail d on d.headerid= h._id" +
                " left join price p on d.skuId = p.skuId and p.priceId = d.PriceId " +
                " where DATETIME(h.orderDate) = ? and  h.outletId = ?  and (coalesce(d.qty1,0)>0 or coalesce(d.qty2,0)>0) "+
                " group by p.priceId";
        Cursor cursor = db.rawQuery(query, new String[] {wputils.getDateTime(orderExtra.orderDateCalendar), orderExtra.outletId});
                //currentOutlet.priceId.toString()});
        cursor.moveToFirst();
        orderSumControl.clear();
        for (int i = 0; i < cursor.getCount(); i++) {
//            this.orderSum = cursor.getDouble(0);
//            this.orderRows = cursor.getInt(1);
            orderSumControl.AddRow(cursor.getDouble(0), cursor.getString(1));
            cursor.moveToNext();
        }
        this.orderSum = orderSumControl.getOrderAmount();
        db.close();
    }

    private boolean existsSpecialPrices = false;
    //75a9d60f-cd75-11e4-826a-240a64c9314e Вид 1

    private void fillSpecialPriceFlag(OrderExtra orderExtra, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        //and PriceId = ?
        String query="select  count(*) from  orderDetail   " +
                    " where  orderUUID= ?  and PriceId = ? ";

        Cursor cursor = db.rawQuery(query, new String[] {orderExtra.orderUUID, "75a9d60f-cd75-11e4-826a-240a64c9314e"});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            this.existsSpecialPrices = cursor.getInt(0) > 0;
            //String s = cursor.getString(0);
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
        if (_outlet.CustomerClass.equals(AppSettings.CUSTOMER_CLASS_CREDIT))
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

    private boolean checkDontUseAmountValidation(OrderExtra orderExtra, Context context) {
        if (order.payType != 0) {
            boolean res = true;
            DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
            SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
            String query="select count(*) from orderDetail d " +
                    " inner join sku s on s.SkuId = d.skuid " +
                    " inner join skuGroup g on s.SkuParentId = g.GroupId " +
                    "where d.orderUUID = ? and g.DontUseAmountValidation <> 1 and (coalesce(d.qty1,0)+coalesce(d.qty2,0))<>0";
            Cursor cursor = db.rawQuery(query,new String[] {orderExtra.orderUUID});
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                if (cursor.getInt(0)>0) res = false;
                cursor.moveToNext();
            }
            db.close();
            return res;
        }

        return false;
    }

    public Boolean allowOrderToSave(OrderExtra orderExtra,OutletObject currentOutlet, Context context)
    {
       this._outlet = currentOutlet;
        //if (this.orderRows ==0 && this.orderSum == 0) return true;
        loadSumByOutlet(orderExtra, currentOutlet, context);
        checkOnlyFactSku(orderExtra,context);
        fillSpecialPriceFlag(orderExtra,  context);

        //don't check men sum when payTYpe is fact
        if (order.payType == 1) {
            return true;
        }

        if (!this.onlyFactSku) return false;
        if (checkDontUseAmountValidation(orderExtra,context)) return true;
        return orderSumControl.isAllowed();
    }

    public String getControlMessage(OutletObject currentOutlet)
    {
        String result = orderSumControl.getMessage();
//        if (this.orderRows < params.getMinOrderRowsQty() && !this.financeControl)
//        {
//            result+="В заказе должно быть минимум "+Integer.toString(this.params.getMinOrderRowsQty())+" строк. В заказе: "+Integer.toString(this.orderRows) +" стр\n";
//        }
//        if (this.orderSum < this.params.getMinOrderSum() && !this.financeControl)
//        {
//            result+="Сумма заказа должна быть не меньше "+String.format("%.2f", this.params.getMinOrderSumByCategory(currentOutlet))+" грн. Сумма заказа: "+String.format("%.2f", this.orderSum)+"\n";
//        }

        if (!this.onlyFactSku)
            result+="\nВ заказе есть позиции, которые можно отгружать только по факту!";
        return result;
    }


}
