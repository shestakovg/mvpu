package com.uni.mvpu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;

import core.TouchActivity;
import core.appManager;


public class ActivitygetVersion extends TouchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_version);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activityget_version, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onGetVersion(View v)  {
        try {
            dowloadVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dowloadVersion() throws IOException {
        URL url = new URL(appManager.getOurInstance().appSetupInstance.getServiceUrl()+"/dictionary/getapk");
        HttpURLConnection c;
        c = (HttpURLConnection) url.openConnection();
       // c.setRequestMethod("GET");
        //c.setDoOutput(true);
        c.connect();

        File file = this.getExternalFilesDir("download");
        File outputFile = new File(file, "app.apk");
        FileOutputStream fos = new FileOutputStream(outputFile);
        InputStream is = c.getInputStream();

        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }
        fos.close();
        is.close();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(outputFile),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
