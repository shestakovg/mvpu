package sync;

import android.content.Context;
import android.os.AsyncTask;

import com.uni.mvpu.ActivityOrder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class syncOrderStatus  extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;

    public syncOrderStatus(Context context) {
        this.context = context;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);
        return serviceManager.CallDataServiceMultiply(params[1]);
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        if (jsonObjects == null) return;

        try {
            syncSaveData.saveOrderStatusChanges(jsonObjects, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
