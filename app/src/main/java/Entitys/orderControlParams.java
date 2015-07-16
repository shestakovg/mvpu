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
    private IOrderControlParams params;

    public orderControlParams(int orderRows, double orderSum, IOrderControlParams params) {
        this.orderRows = orderRows;
        this.orderSum = orderSum;
        this.params = params;
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

    public Boolean allowOrderToSave(OrderExtra orderExtra,OutletObject currentOutlet, Context context)
    {
        if (this.orderRows ==0 && this.orderSum == 0) return true;
        loadSumByOutlet(orderExtra, currentOutlet, context);
        if (this.orderRows < params.getMinOrderRowsQty() && this.orderSum < this.params.getMinOrderSum())
        {
            return false;
        }
        return true;
    }

    public String getControlMessage()
    {
        String result = "";
        if (this.orderRows < params.getMinOrderRowsQty())
        {
            result+="� ������ ������ ���� ������� "+Integer.toString(this.params.getMinOrderRowsQty())+" �����. � ������: "+Integer.toString(this.orderRows) +" ���\n";
        }
        if (this.orderSum < this.params.getMinOrderSum())
        {
            result+="����� ������ ������ ���� �� ������ "+String.format("%.2f", this.params.getMinOrderSum())+" ���. ����� ������: "+String.format("%.2f",this.orderSum);
        }
        return result;
    }
}
