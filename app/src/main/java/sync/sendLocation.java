package sync;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.widget.Toast;

//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import Entitys.LocationEntitys.OutletCheckIn;
import Entitys.LocationEntitys.TrackingEntity;
import Entitys.sendResult;
import core.AppSettings;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;

/**
 * Created by shest on 9/18/2016.
 */
public class sendLocation extends AsyncTask<String, Integer, List<JSONObject>> {
    Context context;
    boolean success = false;
    boolean successTracking = false;
    private ArrayList<OutletCheckIn> sheckInList = new ArrayList<OutletCheckIn>();
    private ArrayList<TrackingEntity> trackingList = new ArrayList<TrackingEntity>();

    public sendLocation(Context context) {
        this.context = context;
        this.sheckInList = this.getSheckInList();
        this.trackingList = this.getTrackingOutletCheckInList();
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        this.send();
        if (!this.success)        this.sendMailCheckIn();
        this.sendTracking();
        if (!this.successTracking) this.sendMailLocation();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);

//        if (this.success && this.successTracking)
//            Toast.makeText(this.context, "Send location success", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(this.context, "Send location fail", Toast.LENGTH_SHORT).show();
    }

    private JSONArray getJSONCheckIn(ArrayList<OutletCheckIn>  checkInArrayList)
    {
        JSONArray jsonArray = new JSONArray();
        for (OutletCheckIn chk : checkInArrayList)
        {
            JSONObject jsonCheckIn  = new JSONObject();
            try{
                jsonCheckIn.put("routeId", chk.getRouteId());
                jsonCheckIn.put("outletId", chk.getOutletId());
                jsonCheckIn.put("longitude", chk.getLongtitude());
                jsonCheckIn.put("latitude",chk.getLatitude());
                //jsonCheckIn.put("sateliteTime", chk.getSateliteTime());
                jsonCheckIn.put("satelliteDate", chk.getSateliteTime());
                jsonCheckIn.put("date", chk.getLogDate());
                jsonArray.put(jsonCheckIn);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private void send()
    {
        ArrayList<OutletCheckIn> checkInArrayList = this.sheckInList;
        if (checkInArrayList.size()==0 )
        {
            this.success = true;
            return ;
        }
        try {
            JSONArray jsonArray = getJSONCheckIn(checkInArrayList);
            URL myurl=new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl1c()+"/dictionary/savecheckin");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            String auth =new String(appManager.getOurInstance().appSetupInstance.getBasLogin() + ":" + appManager.getOurInstance().appSetupInstance.getBasPassword());
            byte[] data1 = auth.getBytes(UTF_8);
            String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic "+base64);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Accept", "application/json");
            JSONObject checkInObj = new JSONObject();
            checkInObj.put("checkInArray", jsonArray);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            //streamWriter.write(checkInObj.toString());
            streamWriter.write(jsonArray.toString());
            streamWriter.flush();
           // StringBuilder stringBuilder = new StringBuilder();
            //http://stackoverflow.com/questions/34977911/android-post-request-to-wcf-service
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                this.success = true;
            }
            if (connection != null){
                connection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.success)
        {
            this.markAsSend();
        }
    }

    private JSONArray getJSONLocation( ArrayList<TrackingEntity> trackList)
    {
        JSONArray jsonArray = new JSONArray();
        for (TrackingEntity chk : trackList)
        {
            JSONObject jsonCheckIn  = new JSONObject();
            try{
                jsonCheckIn.put("routeId", chk.getRouteId());
                jsonCheckIn.put("longtitude", chk.getLongtitude());
                jsonCheckIn.put("latitude",chk.getLatitude());
                jsonCheckIn.put("sateliteTime", chk.getSateliteTime());
                jsonArray.put(jsonCheckIn);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private void sendTracking()
    {
        ArrayList<TrackingEntity> trackList =  this.trackingList;
        if (trackList.size()==0 ) {
            this.successTracking = true;
            return;
        }
        try {
            JSONArray jsonArray = getJSONLocation(trackList);
            URL myurl=new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/tracking");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            JSONObject checkInObj = new JSONObject();
            checkInObj.put("checkInArray", jsonArray);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(checkInObj.toString());
            streamWriter.flush();
            // StringBuilder stringBuilder = new StringBuilder();
            //http://stackoverflow.com/questions/34977911/android-post-request-to-wcf-service
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                this.successTracking = true;
            }
            if (connection != null){
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.successTracking)
        {
            this.markTrakingAsSend();
        }
    }

    private ArrayList<OutletCheckIn> getSheckInList()
    {
        sendResult result=new sendResult();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select DATETIME(logDate) logDate,  _id, outletId, longtitude, latitude,  sateliteTime from outletCheckIn where _send = 0", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            OutletCheckIn chk = new OutletCheckIn();
            chk.setRouteId(appManager.getOurInstance().appSetupInstance.getRouteId().toString());
            chk.setOutletId(cursor.getString(cursor.getColumnIndex("outletId")));
            chk.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            chk.setLongtitude(cursor.getDouble(cursor.getColumnIndex("longtitude")));
            chk.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            long time = cursor.getLong(cursor.getColumnIndex("sateliteTime"));
            //chk.setSateliteTime( wputils.getDateStringFromLong2(time)+" "+ DateFormat.getTimeInstance().format(time));
            chk.setSateliteTime( wputils.getDateStringFromLong2(time));
            chk.setLogDate(cursor.getString(cursor.getColumnIndex("logDate")));
            sheckInList.add(chk);
            cursor.moveToNext();
        }

        db.close();
        return this.sheckInList;
    }

    private ArrayList<TrackingEntity> getTrackingOutletCheckInList()
    {
        sendResult result=new sendResult();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from gpsLog where _send = 0 limit 150", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            TrackingEntity chk = new TrackingEntity();
            chk.setRouteId(appManager.getOurInstance().appSetupInstance.getRouteId().toString());
            chk.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            chk.setLongtitude(cursor.getDouble(cursor.getColumnIndex("longtitude")));
            chk.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            long time = cursor.getLong(cursor.getColumnIndex("sateliteTime"));
            chk.setSateliteTime( wputils.getDateStringFromLong(time)+" "+ DateFormat.getTimeInstance().format(time));
            trackingList.add(chk);
            cursor.moveToNext();
        }

        db.close();
        return this.trackingList;
    }

    private void markTrakingAsSend()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getWritableDatabase();
        for (TrackingEntity checkIn : this.trackingList)
        {
            db.execSQL("delete from  gpsLog where _id = "+String.valueOf(checkIn.getId()));
        }
        db.close();
    }

    private void markAsSend()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getWritableDatabase();
        for (OutletCheckIn checkIn : this.sheckInList)
        {
            db.execSQL("update outletCheckIn set _send = 1 where _id = "+String.valueOf(checkIn.getId()));
        }
        db.close();
    }

    private void sendMailCheckIn()
    {
        if (this.sheckInList.size()==0)
        {
            this.success = true;
            return;
        }

        try {
            JSONArray jsonArray = getJSONCheckIn(this.sheckInList);
            JSONObject checkInObj = new JSONObject();
            checkInObj.put("checkInArray", jsonArray);
            Mail m = new Mail(AppSettings.FROM_EMAIL, AppSettings.EMAIL_PASSWORD);

            String[] toArr = {AppSettings.TO_EMAIL};
            m.setTo(toArr);
            m.setFrom(AppSettings.FROM_EMAIL);
            m.setSubject("checkin");

            m.setBody(jsonArray.toString());
            if (m.send())
            {
                this.markAsSend();
                this.success = true;
            }
        } catch (Exception e) {
            this.success = false;
            e.printStackTrace();
        }
    }

    private void sendMailLocation()
    {
        if (this.trackingList.size()==0)
        {
            this.successTracking = true;
            return;
        }

        try {
            JSONArray jsonArray = getJSONLocation(this.trackingList);
            JSONObject checkInObj = new JSONObject();
            checkInObj.put("checkInArray", jsonArray);
            Mail m = new Mail(AppSettings.FROM_EMAIL, AppSettings.EMAIL_PASSWORD);

            String[] toArr = {AppSettings.TO_EMAIL};
            m.setTo(toArr);
            m.setFrom(AppSettings.FROM_EMAIL);
            m.setSubject("location");
            m.setBody(jsonArray.toString());
            if (m.send())
            {
                this.markTrakingAsSend();
                this.successTracking = true;
            }
        } catch (Exception e) {
            this.successTracking = false;
            e.printStackTrace();
        }
    }
}

