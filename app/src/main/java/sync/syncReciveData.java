package sync;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entitys.syncParameter;

/**
 * Created by g.shestakov on 04.06.2015.
 */
public class syncReciveData extends AsyncTask<String, Void, List<JSONObject>> {
    private ArrayList<syncParameter> listParams;
    private Context context;

    public syncReciveData( ArrayList<syncParameter> listParams, Context context) {
        this.context = context;
        this.listParams = listParams;
    }

    @Override
    protected List<JSONObject> doInBackground(String... params) {
        return null;
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
    }
}
