package sync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.uni.mvpu.R;

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
public class downloadNewVwersion extends AsyncTask<String, Integer, String> {
    private Activity owner;
    ProgressDialog pd = null;
    private boolean downLoadSuccess = false;
    private boolean versionActual = false;
    private String newVersionName = "";
    public
    downloadNewVwersion(Activity owner) {
        this.owner = owner;
    }

    @Override
    protected String doInBackground(String... params) {
        ServiceManager serviceManager = new ServiceManager(appManager.getOurInstance().appSetupInstance.getServiceUrl());
        JSONObject verOb = serviceManager.CallSingle("dictionary/getversion");

        if (verOb == null)
        {
            //Toast.makeText(owner,"Не удалось проверить версию", Toast.LENGTH_SHORT).show();
            return null;
        }
        else try {
            if (appManager.getOurInstance().appSetupInstance.version.equals(verOb.getString("ver")))
            {
                this.versionActual = true;
                return null;
            }
            else
                this.newVersionName = verOb.getString("ver");
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

        int fileLength = c.getContentLength();
        pd.setMax(fileLength);
        long total = 0;

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
                total += len1;
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) total * 100 / fileLength);
            }
            fos.close();
            is.close();
            this.downLoadSuccess = true;

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
        pd = new ProgressDialog(owner);
        pd.setMessage("Загрузка новой версии");
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.versionActual)
            Toast.makeText(owner,"Текущая версия актуальна. Обновление не требуется", Toast.LENGTH_SHORT).show();
        else
        if (this.downLoadSuccess)
            Toast.makeText(owner, "Новая версия загружена. Начинается установка", Toast.LENGTH_SHORT).show();
        else
            //Toast.makeText(owner, "Не удалось загрузить обновление\nПроверьте подключение Hamachi", Toast.LENGTH_LONG).show();
            appManager.getOurInstance().getYesNoWithExecutionStop("Ошибка загрузки","Не удалось загрузить обновление\nПроверьте подключение Hamachi", owner, R.drawable.unchecked,false);


        pd.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        pd.setIndeterminate(false);
        pd.setMax(100);
        pd.setProgress(values[0]);
        //Toast.makeText(this.owner, values[0], Toast.LENGTH_SHORT).show();
    }
}
