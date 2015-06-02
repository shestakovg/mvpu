package sync;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by g.shestakov on 26.05.2015.
 */
public class ServiceManager {
    private  String SERVICE_URI = "http://192.168.1.102:8100/dictionary/";

    public Exception getLastException() {
        return lastException;
    }

    private Exception lastException;

    public    ServiceManager(String serviceUri) {
        this.SERVICE_URI=serviceUri;

    }
    public List<JSONObject> CallDataServiceMultiply(String methodNameAndVariable)
    {
        try{
            HttpGet request = new HttpGet(SERVICE_URI + "/"+methodNameAndVariable);
            request.setHeader("Accept", "application/json");
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            params.setBooleanParameter("http.protocol.expect-continue",true);
            request.setParams(params);
            request.setHeader("Content-type", "application/json; charset=utf-8");
            //request.setHeader("Content-type", "application/json");
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity!=null)
            {
                InputStream instream = responseEntity.getContent();
                String result= convertStreamToString(instream);
                JSONArray jsonArray = new JSONArray(result);
                List<JSONObject> lst=new ArrayList<JSONObject>();
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject curJsonObject = new JSONObject(new String( jsonArray.getString(i)));
                    lst.add(curJsonObject);
                }
                return lst;
            }
            return null;


        } catch (Exception e) {
            e.printStackTrace();
            lastException=e;
            return null;}
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String GetValueFromJSONObject(JSONObject json, String valueKey)
    {
        try{
            return json.getString(valueKey);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public JSONObject CallSingle(String methodName)
    {
        try{
            HttpGet request = new HttpGet(SERVICE_URI + "/"+methodName);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json; charset=utf-8");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            char[] buffer = new char[(int)responseEntity.getContentLength()];
            InputStream stream = responseEntity.getContent();
            InputStreamReader reader = new InputStreamReader(stream);
            reader.read(buffer);
            stream.close();

            JSONObject custdt = new JSONObject(new String(buffer));
            return custdt;

        } catch (Exception e) {
            e.printStackTrace();}
        return null;
    }

}
