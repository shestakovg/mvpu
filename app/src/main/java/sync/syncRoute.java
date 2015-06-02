package sync;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import Entitys.routeObject;
import core.appManager;
import db.DbOpenHelper;

/**
 * Created by g.shestakov on 02.06.2015.
 */
public class syncRoute extends AsyncTask<String, Void, List<JSONObject>> {
    private Context context;
    @Override
    protected List<JSONObject> doInBackground(String... params) {
        context = appManager.getOurInstance().getCurrentContext();
        ServiceManager serviceManager = new ServiceManager(params[0]);
        return serviceManager.CallDataServiceMultiply(params[1]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        if (jsonObjects == null)
        {
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        else
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
    }
}
