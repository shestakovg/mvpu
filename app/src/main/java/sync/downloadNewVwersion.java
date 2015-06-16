package sync;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import core.appManager;

/**
 * Created by g.shestakov on 16.06.2015.
 */
public class downloadNewVwersion extends AsyncTask<String, String, String> {
    private Activity owner;

    public downloadNewVwersion(Activity owner) {
        this.owner = owner;
    }

    @Override
    protected String doInBackground(String... params) {
        ServiceManager serviceManager = new ServiceManager(appManager.getOurInstance().appSetupInstance.getServiceUrl());
        JSONObject verOb = serviceManager.CallSingle("dictionary/getversion");
        if (verOb == null)
        {
            publishProgress("Не удалось проверить версию");
        }
        else try {
            if (appManager.getOurInstance().appSetupInstance.version.equals(verOb.getString("ver")))
            {
                publishProgress("Текущая версия актуальна. Обновление не требуется");
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        URL url = null;
        try {
            url = new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/getapk");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) url.openConnection();
            c.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // c.setRequestMethod("GET");
        //c.setDoOutput(true);


        File file = this.owner.getExternalFilesDir("download");
        File outputFile = new File(file, "app.apk");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStream is = null;
        try {
            is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
            publishProgress("Новая версия загружена. Начинается установка");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(outputFile),
                    "application/vnd.android.package-archive");
            this.owner.startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(this.owner, values[0], Toast.LENGTH_SHORT).show();
    }
}
