package sync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shestakov.g on 12.06.2015.
 */
public class ServiceManager4 {
    private  String SERVICE_URI = "";

    public ServiceManager4(String SERVICE_URI) {
        this.SERVICE_URI = SERVICE_URI;
    }

    public Exception getLastException() {
        return lastException;
    }

    private Exception lastException;

    public List<JSONObject> CallDataServiceMultiply(String methodNameAndVariable)
    {
        JSONArray jsonArray;
        try {
            URL url = new URL(SERVICE_URI + "/"+methodNameAndVariable);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            jsonArray = new JSONArray(responseStrBuilder.toString());
            List<JSONObject> listResult = new ArrayList<JSONObject>();
            for(int n = 0; n < jsonArray.length(); n++)
            {
                listResult.add(jsonArray.getJSONObject(n));
            }
            return listResult;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            lastException=e;
        }
        return null;
    }

    public JSONObject CallSingle(String methodNameAndVariable)
    {
        JSONObject jsonObj;
        try {
            URL url = new URL(SERVICE_URI + "/"+methodNameAndVariable);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            String auth =new String(appManager.getOurInstance().appSetupInstance.getBasLogin() + ":" + appManager.getOurInstance().appSetupInstance.getBasPassword());
//            byte[] data1 = auth.getBytes(UTF_8);
//            String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);
//            conn.setRequestProperty("Authorization", "Basic "+base64);
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            jsonObj = new JSONObject(responseStrBuilder.toString());

            return jsonObj;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            lastException=e;
        }
        return null;
    }
}
