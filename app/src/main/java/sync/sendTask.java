package sync;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Entitys.Task;
import Helpers.taskHelper;
import core.appManager;
import core.wputils;

public class sendTask extends AsyncTask<String, Integer, List<JSONObject>> {
    Context context;
    taskHelper helper;
    ArrayList<Task> tasks;
    boolean success = false;
    public sendTask(Context context) {
        this.context = context;
        helper = new taskHelper(context, true);
        tasks =  helper.getTaskToSend();
    }

    @Override
    protected List<JSONObject> doInBackground(String... strings) {
        this.send();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        if (helper != null) {
            try {
                helper.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray getJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks)
        {
            JSONObject json  = new JSONObject();
            try{
                json.put("reference", task.getReference());
                json.put("number", task.getNumber());
                json.put("outletId", task.getOutletId());
                json.put("description", wputils.decodeCyrilicString(task.getDescription()));
                json.put("resultDescription", wputils.decodeCyrilicString(task.getResultDescription()));
                json.put("status",task.getStatus());
                jsonArray.put(json);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private void send() {
        if (tasks.size() == 0)
        {
            this.success = true;
            return ;
        }

        try {
            JSONArray jsonArray = this.getJsonArray();
            URL myurl=new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/savetask");
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            JSONObject taskObj = new JSONObject();
            taskObj.put("tasks", jsonArray);
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(taskObj.toString());
            streamWriter.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                this.success = true;
            }
            if (connection != null){
                connection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.success){
            this.markAsSend();
        }
    }

    private void markAsSend() {
        for (Task task : tasks) {
            helper.markAsSend(task.getId());
        }
    }
}
