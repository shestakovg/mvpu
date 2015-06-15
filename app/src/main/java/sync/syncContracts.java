package sync;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by g.shestakov on 04.06.2015.
 */
public class syncContracts extends AsyncTask<String, Void, List<JSONObject>> {
    Context context;
    public syncContracts(Context context) {
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
        syncSaveData.saveContracts(jsonObjects, context);
    }
}
