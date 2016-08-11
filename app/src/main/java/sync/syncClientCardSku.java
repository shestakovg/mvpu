package sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shest on 8/7/2016.
 */
public class syncClientCardSku extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;
    private ProgressDialog pd;

    public syncClientCardSku(Context context, ProgressDialog pd) {
        this.context = context;
        this.pd = pd;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);
        //publishProgress(1);
        return serviceManager.CallDataServiceMultiply(params[1]);
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
            syncSaveData.saveClientCardSku(jsonObjects, context);
        }
        if (pd.getProgress() == pd.getMax())
            this.pd.dismiss();
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.pd.incrementProgressBy(values[0]);
        this.pd.incrementSecondaryProgressBy(values[0]);
        this.pd.setMessage("Обновление sku клиента завершено");
    }


}
