package sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Entitys.priceType;
import core.appManager;
import db.DbOpenHelper;

/**
 * Created by g.shestakov on 04.06.2015.
 */
public class syncSaveData {

    public static void saveRoute(List<JSONObject> jsonObjects, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from route");
        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("outletId", jsonObject.getString("OutletId"));
                values.put("outletName", jsonObject.getString("OutletName"));
                values.put("VisitDay", jsonObject.getString("VisitDay"));
                values.put("VisitDayId", jsonObject.getInt("VisitDayId"));
                values.put("VisitOrder", jsonObject.getInt("VisitOrder"));
                values.put("CustomerId", jsonObject.getString("CustomerId"));
                values.put("CustomerName", jsonObject.getString("CustomerName"));
                values.put("partnerId", jsonObject.getString("PartnerId"));
                values.put("partnerName", jsonObject.getString("PartnerName"));
                values.put("address", jsonObject.getString("address"));
                db.insert("route", null, values);
            } catch (Exception e) {

            }
        }
        db.close();
        Toast.makeText(context, "Обновление маршрута завершено", Toast.LENGTH_SHORT).show();
    }

    public static void saveContracts(List<JSONObject> jsonObjects, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from contracts");
        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("CustomerId", jsonObject.getString("CustomerId"));
                values.put("PriceId", jsonObject.getString("PriceId"));
                values.put("PriceName", jsonObject.getString("PriceName"));
                values.put("LimitSum", jsonObject.getDouble("LimitSum"));
                values.put("Reprieve", jsonObject.getInt("Reprieve"));
                values.put("PartnerId", jsonObject.getString("PartnerId"));
                db.insert("contracts", null, values);
            } catch (Exception e) {

            }
        }
        db.close();
        Toast.makeText(context, "Обновление договоров завершено", Toast.LENGTH_SHORT).show();
    }

    public static void saveSkuGroup(List<JSONObject> jsonObjects, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from skuGroup");
        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("GroupId", jsonObject.getString("GroupId"));
                values.put("GroupName", jsonObject.getString("GroupName"));
                values.put("GroupParentId", jsonObject.getString("GroupParentId"));
                db.insert("skuGroup", null, values);
            } catch (Exception e) {

            }
        }
        db.close();
        Toast.makeText(context, "Обновление номенклатурных групп завершено", Toast.LENGTH_SHORT).show();
    }

    public static void saveSku(List<JSONObject> jsonObjects, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from sku");
        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("SkuId", jsonObject.getString("SkuId"));
                values.put("SkuName", jsonObject.getString("SkuName"));
                values.put("SkuParentId", jsonObject.getString("SkuParentId"));
                values.put("QtyPack", jsonObject.getDouble("QtyPack"));
                values.put("Article", jsonObject.getString("Article"));
                db.insert("sku", null, values);
            } catch (Exception e) {

            }
        }
        db.close();
        Toast.makeText(context, "Обновление номенклатуры завершено", Toast.LENGTH_SHORT).show();
    }

    public static void savePrice(List<JSONObject> jsonObjects, Context context, priceType price)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from price where PriceId='" + price.getPriceId() + "'");

        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("SkuId", jsonObject.getString("SkuId"));
                values.put("PriceId", jsonObject.getString("PriceId"));
                values.put("Pric", jsonObject.getDouble("Pric"));
                db.insert("price", null, values);
            } catch (Exception e) {

            }
        }
        db.execSQL("insert into PriceNames " +
                "select distinct PriceId , PriceName from contracts con where not exists (select * from PriceNames pn where pn.PriceId = con.PriceId)");
        db.close();
        Toast.makeText(context, "Обновление прайса "+price.getPriceName()+" завершено", Toast.LENGTH_LONG).show();
    }

    public static void saveStock(List<JSONObject> jsonObjects, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from stock");
        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("SkuId", jsonObject.getString("SkuId"));
                values.put("StockG", jsonObject.getDouble("StockG"));
                values.put("StockR", jsonObject.getDouble("StockR"));
                db.insert("stock", null, values);
            } catch (Exception e) {

            }
        }
        db.close();
        Toast.makeText(context, "Обновление остатков завершено", Toast.LENGTH_SHORT).show();
    }

    public static void saveDebt(List<JSONObject> jsonObjects, Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from debts");

        for (JSONObject jsonObject: jsonObjects) {
            try {
                ContentValues values = new ContentValues();
                values.put("partnerId", jsonObject.getString("partnerId"));
                values.put("customerId", jsonObject.getString("customerId"));
                values.put("transactionId", jsonObject.getString("transactionId"));
                values.put("transactionNumber", jsonObject.getString("transactionNumber"));
                values.put("transactionDate", jsonObject.getString("transactionDate"));
                values.put("transactionSum", jsonObject.getDouble("transactionSum"));
                values.put("paymentDate", jsonObject.getString("paymentDate"));
                values.put("debt", jsonObject.getDouble("debt"));
                values.put("overdueDebt", jsonObject.getDouble("overdueDebt"));
                values.put("overdueDays", jsonObject.getInt("overdueDays"));
                db.insert("debts", null, values);
            } catch (Exception e) {

            }
        }
        db.close();
        Toast.makeText(context, "Обновление долгов завершено", Toast.LENGTH_LONG).show();
    }

    public static void saveDebtParams(List<JSONObject> jsonObjects, Context context) {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        for (JSONObject jsonObject: jsonObjects) {

            try {
                appManager.getOurInstance().appSetupInstance.saveParamSetup(db, "debtControl",Integer.toString(jsonObject.getInt("debtControl")));
                appManager.getOurInstance().appSetupInstance.saveParamSetup(db, "allowOverdueSum", jsonObject.getString("allowOverdueSum"));
                appManager.getOurInstance().appSetupInstance.saveParamSetup(db, "skuQty", jsonObject.getString("skuQty"));
                appManager.getOurInstance().appSetupInstance.saveParamSetup(db, "minOrderSum", jsonObject.getString("minOrderSum"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        db.close();
        appManager.getOurInstance().appSetupInstance.readSetup(context);
    }


}
