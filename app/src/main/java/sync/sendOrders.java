package sync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

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
//        Toast.makeText(this.context,
//                            this.context.getString(R.string.send_current_order_dialog) +" � " + values[0].toString()
//                        , Toast.LENGTH_SHORT).show();
    }

    private sendResult sendHeaders()
    {
        sendResult result=new sendResult();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        try {
            HttpPost request = new HttpPost(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/saveheader");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            Cursor cursor = db.rawQuery("select  h._id,  h.orderUUID,DATETIME(h.orderDate) as orderDate,DATETIME(h.deliveryDate) as deliveryDate," +
                    "  h.outletId,  h.orderNumber ,coalesce( h.notes,' ') notes," +
                                "  coalesce(h.payType,0) payType ,coalesce(h.autoLoad,0) autoLoad, h.orderType from orderHeader h where  h._send=0", null);
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
                StringEntity entity = new StringEntity(orderHeader.toString());
                request.setEntity(entity);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                int responceCode=response.getStatusLine().getStatusCode();
                if (responceCode!= HttpStatus.SC_OK) {
                    result.setFail();

                    //Toast.makeText(context, response.getStatusLine().getReasonPhrase(), Toast.LENGTH_LONG).show();
                }
                else
                {
//                    //???????? ???????
                    sendResult detailResult = sendOrderDetail(cursor.getInt(cursor.getColumnIndex("_id")),cursor.getInt(cursor.getColumnIndex("orderType")));
                    if (detailResult.getResult())
                    {
                        ServiceManager wcf=new ServiceManager(appManager.getOurInstance().appSetupInstance.getServiceUrl());
                        wcf.CallSingle("/dictionary/marcorder/" + cursor.getString(cursor.getColumnIndex("orderUUID")));
                        setOrderAsSend(cursor.getInt(cursor.getColumnIndex("_id")));
                        result.incRecordTransfer();
                        publishProgress(i+1);
                       // pd.setMessage("?????????? ???????: "+i);
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
            HttpPost request = new HttpPost(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/savedetail");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            Cursor cursor = db.rawQuery("select orderUUID, skuId, qty1, qty2, PriceId, DATETIME(finalDate) finalDate " +
                    " from orderDetail where headerId = "+orderId+
                            (orderType == AppSettings.ORDER_TYPE_STORECHECK ?
                                                " and finalDate is not null":""),
                    null);
            cursor.moveToFirst();
            for (int i=0;i<cursor.getCount();i++) {
                String finalDate = cursor.getString(cursor.getColumnIndex("finalDate"));
                if (orderType != AppSettings.ORDER_TYPE_STORECHECK)
                    finalDate = "02/28/2016";
                JSONStringer orderHeader = new JSONStringer()
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
                StringEntity entity = new StringEntity(orderHeader.toString());
                request.setEntity(entity);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                int responceCode=response.getStatusLine().getStatusCode();
                if (responceCode!= HttpStatus.SC_OK) {
                    result.setFail();
                }
                else
                {
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

    private void setOrderAsSend(int orderId)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update orderHeader set  _send = 1  where  _id = "+orderId);
        db.close();
    }
}
