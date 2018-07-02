package sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class syncTask extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;
    private ProgressDialog pd;

    public syncTask(Context context, ProgressDialog pd) {
        this.context = context;
        this.pd = pd;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        //context = appManager.getOurInstance().getCurrentContext();
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);
        publishProgress(1);
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
            this.pd.setMessage(context.getString(R.string.no_connection));
            this.pd.setMax(0);
            return;
        }
        else
        {
            try {
                syncSaveData.saveTasks(jsonObjects, context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (pd.getProgress() == pd.getMax())
            this.pd.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.pd.incrementProgressBy(values[0]);
        this.pd.incrementSecondaryProgressBy(values[0]);
    }
}
