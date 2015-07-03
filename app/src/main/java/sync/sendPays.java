package sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.List;

import core.appManager;
import db.DbOpenHelper;

/**
 * Created by g.shestakov on 03.07.2015.
 */
public class sendPays  extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;

    public sendPays(Context context) {
        this.context = context;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        this.send();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private void send()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        try {
            HttpPost request = new HttpPost(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/savepay");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            Cursor cursor = db.rawQuery("select _id , DATETIME(payDate) payDate, transactionId, customerid, paySum from pays  where  _send=0", null);
            cursor.moveToFirst();

            for (int i=0;i<cursor.getCount();i++)
            {
                JSONStringer orderHeader = new JSONStringer()
                        .object().key("clamedPays").
                                object().
                                  key("routeId").value(appManager.getOurInstance().appSetupInstance.getRouteId()).
                                  key("payDate").value(cursor.getString(cursor.getColumnIndex("payDate"))).
                                  key("transactionId").value(cursor.getString(cursor.getColumnIndex("transactionId"))).
                                  key("paySum").value(cursor.getDouble(cursor.getColumnIndex("paySum"))).
                                endObject().
                        endObject();
                StringEntity entity = new StringEntity(orderHeader.toString());
                request.setEntity(entity);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                int responceCode=response.getStatusLine().getStatusCode();
                if (responceCode!= HttpStatus.SC_OK) {
                }
                else
                {
                    markpay(cursor.getInt(cursor.getColumnIndex("_id")));
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    private void markpay(int id)
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getWritableDatabase();
        db.execSQL("update pays set _send = 1 where _id="+Integer.toString(id));
        db.close();
    }
}
