package sync;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shest on 11/17/2016.
 */

public class syncDeliveryArea  extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;

    public syncDeliveryArea(Context context) {
        this.context = context;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);
        List<JSONObject> lst = serviceManager.CallDataServiceMultiply(params[1]);
        return    lst;
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
            syncSaveData.saveDeliveryArea(jsonObjects, context);
        }
    }
}
