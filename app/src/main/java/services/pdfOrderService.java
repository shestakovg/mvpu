package services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;

import Entitys.Order;
import Helpers.pdfService;
import db.DbOpenHelper;

public class pdfOrderService {
    private final Context context;
    private final Order order;
    private static final SimpleDateFormat FORMATTER =
            new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    public pdfOrderService(Context context, Order order) {
        this.context = context;
        this.order = order;
    }

    public pdfService.Order getOrder() {
        return new pdfService.Order(getOutletName(), order.orderDescription + "  â³ä   " + FORMATTER.format(order.orderDate), order.orderUUID, getOrderItems());
    }

    private String getOutletName() {
        String sql = "select oh.outletId, r.outletName from OrderHeader oh" +
                " left join route r on r.outletId = oh.outletId "+
                " where oh._id = ?";
        SQLiteDatabase db =  new DbOpenHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[] {Integer.toString(order._id)} );
        String outletName = "";
        cursor.moveToFirst();
        outletName = cursor.getString(cursor.getColumnIndex("outletName"));
        db.close();
        return outletName;
    }

    private List<pdfService.OrderItem> getOrderItems() {
        String sql = "select s.skuId, s.SkuName, COALESCE(od.qty1, 0) as qty1, COALESCE(od.qty2, 0) as qty2, COALESCE(p.Pric,0) pric " +
                     " from orderDetail od " +
                     "left join sku as s on s.skuId = od.skuId " +
                     "left join price p on od.skuId = p.skuId and  p.PriceId = od.PriceId "+
                     "where od.headerId= ?";

        SQLiteDatabase db =  new DbOpenHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[] {Integer.toString(order._id)} );
        cursor.moveToFirst();
        ArrayList<pdfService.OrderItem> items = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            items.add(new pdfService.OrderItem(
                    cursor.getString(cursor.getColumnIndex("SkuName")),
                    cursor.getInt(cursor.getColumnIndex("qty1")),
                    cursor.getInt(cursor.getColumnIndex("qty2")),
                    cursor.getFloat(cursor.getColumnIndex("pric"))
            ));
            cursor.moveToNext();
        }
        db.close();
        return items;
    }
}
