package sync;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by g.shestakov on 05.06.2015.
 */
public class syncStock extends AsyncTask<String, Void, List<JSONObject>> {
    private Context context;

    public syncStock(Context context) {
        this.context = context;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);
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
            syncSaveData.saveStock(jsonObjects, context);
        }

    }
}
