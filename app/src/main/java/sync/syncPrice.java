package sync;

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
public class syncPrice extends AsyncTask<String, Void, List<JSONObject>> {
    private Context context;
    private priceType price;

    public syncPrice(Context context, priceType price) {
        this.context = context;
        this.price = price;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
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
            syncSaveData.savePrice(jsonObjects, context, price);
        }
    }
}
