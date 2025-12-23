package sync;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import core.AppSettings;
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
            HttpURLConnection connection = getHttpURLConnection();
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());

            Cursor cursor = db.rawQuery("select _id , DATETIME(payDate) payDate, transactionId, customerid, paySum from pays  where  _send=0 and paySum > "+ AppSettings.PARAM_EMPTY_PAYMENT, null);
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

                streamWriter.write(orderHeader.toString());
                streamWriter.flush();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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

    @NonNull
    private static HttpURLConnection getHttpURLConnection() throws IOException {
        URL myurl=new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/savepay");
        HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
//            String auth =new String(appManager.getOurInstance().appSetupInstance.getBasLogin() + ":" + appManager.getOurInstance().appSetupInstance.getBasPassword());
//            byte[] data1 = auth.getBytes(UTF_8);
//            String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
//            connection.setRequestProperty("Authorization", "Basic "+base64);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private void markpay(int id)
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getWritableDatabase();
        db.execSQL("update pays set _send = 1 where _id="+Integer.toString(id));
        db.close();
    }
}