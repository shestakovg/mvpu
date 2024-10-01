package sync;

import static android.provider.CalendarContract.CalendarCache.URI;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;

import com.uni.mvpu.R;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import Entitys.sendResult;
import core.AppSettings;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IUpdateOrderList;

/**
 * Created by g.shestakov on 11.06.2015.
 */
public class sendOrders  extends AsyncTask<String, Integer, List<JSONObject>> {
    Context context;
    View view;
    String message="";
    private ProgressDialog pd;
    private Activity ownerActivity;
    public sendOrders(Context context, Activity ownerActivity) {
        this.context = context;
        this.ownerActivity = ownerActivity;
    }

    public sendOrders(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {

        sendHeaders();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sendLocation syncr = new sendLocation(appManager.getOurInstance().appSetupInstance.getActiveWindow());
        syncr.execute(null, null);
        sendNoResult sendNores = new sendNoResult(appManager.getOurInstance().appSetupInstance.getActiveWindow());
        sendNores.execute();
        pd = new ProgressDialog(context);
        pd.setTitle(this.context.getString(R.string.send_order_dialog));
        pd.setMessage(this.context.getString(R.string.send_order_dialog));
        pd.show();
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        pd.setMessage(this.context.getString(R.string.send_order_dialog_finish));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pd.dismiss();
        if (this.ownerActivity!=null)
            if (this.ownerActivity instanceof IUpdateOrderList)
                ((IUpdateOrderList) this.ownerActivity).UpdateList();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.pd.setMessage(this.context.getString(R.string.send_current_order_dialog)+"  " + values[0].toString());
    }

    private sendResult sendHeaders() {
        sendResult result = new sendResult();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        try {
            URL myurl=new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/saveheader");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            String auth =new String(appManager.getOurInstance().appSetupInstance.getBasLogin() + ":" + appManager.getOurInstance().appSetupInstance.getBasPassword());
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic "+base64);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());

            Cursor cursor = db.rawQuery("select  h._id,  h.orderUUID,DATETIME(h.orderDate) as orderDate,DATETIME(h.deliveryDate) as deliveryDate," +
                    "  h.outletId,  h.orderNumber ,coalesce( h.notes,' ') notes," +
                                "  coalesce(h.payType,0) payType ,coalesce(h.autoLoad,0) autoLoad, h.orderType from orderHeader h where  h._send=0 and h.orderType <> 2", null);
            cursor.moveToFirst();

            for (int i=0;i<cursor.getCount();i++)
            {
                //.getBytes(Charset.forName("Cp1251");
                JSONStringer orderHeader = new JSONStringer()
                        .object()
                        .key("orderHeader")
                        .object()
                        .key("orderUUID").value(cursor.getString(cursor.getColumnIndex("orderUUID")))
                            .key("outletId").value(cursor.getString(cursor.getColumnIndex("outletId")))
                        .key("orderDate").value(cursor.getString(cursor.getColumnIndex("orderDate")))
                        .key("deliveryDate").value(cursor.getString(cursor.getColumnIndex("deliveryDate")))
                        .key("orderNumber").value(cursor.getInt(cursor.getColumnIndex("orderNumber")))
                        .key("notes").value(wputils.decodeCyrilicString(cursor.getString(cursor.getColumnIndex("notes"))))
                        .key("payType").value(cursor.getInt(cursor.getColumnIndex("payType")))
                            .key("autoLoad").value(cursor.getInt(cursor.getColumnIndex("autoLoad")))
                            .key("routeId").value(appManager.getOurInstance().appSetupInstance.getRouteId())
                            .key("orderType").value(cursor.getInt(cursor.getColumnIndex("orderType")))
                        .endObject()
                        .endObject();
                streamWriter.write(orderHeader.toString());
                streamWriter.flush();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    result.setFail();
                }
                else
                {
                    sendResult detailResult = sendOrderDetail(cursor.getInt(cursor.getColumnIndex("_id")),cursor.getInt(cursor.getColumnIndex("orderType")));
                    if (detailResult.getResult())
                    {
                        ServiceManager4 wcf = new ServiceManager4(appManager.getOurInstance().appSetupInstance.getServiceUrl());
                        wcf.CallSingle("/dictionary/marcorder/" + cursor.getString(cursor.getColumnIndex("orderUUID")));
                        setOrderAsSend(cursor.getInt(cursor.getColumnIndex("_id")));
                        result.incRecordTransfer();
                        publishProgress(i+1);
                    }

                }
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace(); result.setFail();
        }
        db.close();
        return result;
    }

    private sendResult sendOrderDetail(int orderId, int orderType)
    {
        sendResult result=new sendResult();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select orderUUID, skuId, qty1, qty2, PriceId, DATETIME(finalDate) finalDate " +
                    " from orderDetail where (qty1+qty2)>0 and  headerId = "+orderId+
                            (orderType == AppSettings.ORDER_TYPE_STORECHECK ?
                                                " and finalDate is not null":""),
                    null);
            cursor.moveToFirst();
            for (int i=0;i<cursor.getCount();i++) {
                String finalDate = cursor.getString(cursor.getColumnIndex("finalDate"));
                if (orderType != AppSettings.ORDER_TYPE_STORECHECK)
                    finalDate = "02/28/2030";
                JSONStringer detailRow = new JSONStringer()
                        .object()
                        .key("orderDetail")
                        .object().
                            key("orderUUID").value(cursor.getString(cursor.getColumnIndex("orderUUID"))).
                            key("skuId").value(cursor.getString(cursor.getColumnIndex("skuId"))).
                            key("qty1").value(cursor.getInt(cursor.getColumnIndex("qty1"))).
                            key("qty2").value(cursor.getInt(cursor.getColumnIndex("qty2"))).
                            key("priceType").value(cursor.getString(cursor.getColumnIndex("PriceId"))).
                            key("finalDate").value(finalDate)
                        .endObject()
                        .endObject();

                if (!sendOrderDetailOneRow(detailRow)) {
                    result.setFail();
                }
                else {
                    result.incRecordTransfer();
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace(); result.setFail();
        }
        db.close();
        return result;
    }

    private boolean sendOrderDetailOneRow(JSONStringer detailRow) {
        try {
            URL myurl = new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl() + "/dictionary/savedetail");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            String auth = new String(appManager.getOurInstance().appSetupInstance.getBasLogin() + ":" + appManager.getOurInstance().appSetupInstance.getBasPassword());
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + base64);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(detailRow.toString());
            streamWriter.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void setOrderAsSend(int orderId)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update orderHeader set  _send = 1  where  _id = "+orderId);
        db.close();
    }


}
