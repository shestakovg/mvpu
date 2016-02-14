package sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Gennadiy Shestakov on 2/14/2016.
 */
public class syncSpecification extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;
    ProgressDialog pd;

    public syncSpecification(Context context, ProgressDialog pd) {
        this.context = context;
        this.pd = pd;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);
        publishProgress(1);
        return serviceManager.CallDataServiceMultiply(params[1]);
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
            syncSaveData.saveSpecification(jsonObjects, context);
        }
        if (pd.getProgress() == pd.getMax())
            this.pd.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.pd.incrementProgressBy(values[0]);
        this.pd.incrementSecondaryProgressBy(values[0]);
        this.pd.setMessage("���������� ������������ ���������");
    }
}
