package sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Entitys.LocationEntitys.OutletCheckIn;
import Entitys.LocationEntitys.TrackingEntity;
import Entitys.NoResultForSend;
import core.appManager;
import db.DbOpenHelper;

/**
 * Created by shest on 3/21/2017.
 */

public class sendNoResult extends AsyncTask<String, Integer, List<JSONObject>> {
    Context context;
    boolean success = false;

    public sendNoResult(Context context) {
        this.context = context;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ArrayList<NoResultForSend>  arr = getData();
        if (arr.size() > 0) {
            JSONArray jsarr = getJson(getData());
            if (this.send(jsarr)) {
                    this.markAsSend(arr);
            }

        }
        return null;
    }

    private void markAsSend(ArrayList<NoResultForSend> arr)
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getWritableDatabase();
        for (NoResultForSend item : arr)
        {
            db.execSQL("update No_result_storage set _send =1 where _id = "+String.valueOf(item.getId()));
        }
        db.close();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
    }

    private JSONArray getJson(ArrayList<NoResultForSend> arr)
    {
        JSONArray jsonArray = new JSONArray();
        for (NoResultForSend item : arr)
        {
            JSONObject json  = new JSONObject();
            try {
                json.put("date", item.getDate());
                json.put("outletId", item.getOutletId());
                json.put("reasonId", item.getReasonId());
                jsonArray.put(json);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return  jsonArray;
    }

    private ArrayList<NoResultForSend> getData()
    {
        ArrayList<NoResultForSend> res = new ArrayList<NoResultForSend>();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select _id, DATETIME(Date) as Date, outletid, reasonId from No_result_storage where _send = 0", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            NoResultForSend item = new NoResultForSend();
            item.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            item.setReasonId(cursor.getInt(cursor.getColumnIndex("reasonId")));
            item.setDate(cursor.getString(cursor.getColumnIndex("Date")));
            item.setOutletId(cursor.getString(cursor.getColumnIndex("outletid")));
            res.add(item);
            cursor.moveToNext();
        }
        db.close();
        return res;
    }

    private Boolean send(JSONArray jsonArray) {

        try {
            URL myurl=new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/savenoresult");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            JSONObject checkInObj = new JSONObject();
            checkInObj.put("noresult", jsonArray);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(checkInObj.toString());
            streamWriter.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                this.success = true;
            }
            if (connection != null){
                connection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.success;

    }

}
