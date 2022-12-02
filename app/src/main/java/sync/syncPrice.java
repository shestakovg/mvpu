package sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.List;

import Entitys.priceType;
import core.appManager;

/**
 * Created by g.shestakov on 05.06.2015.
 */
public class syncPrice extends AsyncTask<String, Integer, List<JSONObject>> {
    private Context context;
    private priceType price;
    private ProgressDialog pd;

    public syncPrice(Context context, priceType price, ProgressDialog pd) {
        this.context = context;
        this.price = price;
        this.pd = pd;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        ServiceManager4 serviceManager = new ServiceManager4(params[0]);

        List<JSONObject> lst = serviceManager.CallDataServiceMultiply(params[1]);
        publishProgress(1);
        return lst;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //this.pd.setMessage("Обновление цен: " + price.getPriceName());
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        if (jsonObjects == null)
        {
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
            this.pd.setMessage(context.getString(R.string.no_connection));
            return;
        }
        else
        {
            syncSaveData.savePrice(jsonObjects, context, price);
        }
        //this.pd.setProgress(1);
        //this.pd.setMessage("Обновление цен: " + price.getPriceName() + " завершено");

        if (pd.getProgress() == pd.getMax())
            this.pd.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this.pd.incrementProgressBy(values[0]);
        this.pd.incrementSecondaryProgressBy(values[0]);
        this.pd.setMessage("Обновление цен: " + price.getPriceName() + " завершено");
    }


}
