package com.uni.mvpu;

import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//import core.MyApplication;
import Dialogs.DlgLockApp;
import core.LocationDatabase;
import core.TouchActivity;
import core.appManager;
import interfaces.IManagementGPSLogger;


public class MainActivity extends TouchActivity implements IManagementGPSLogger {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appManager.getOurInstance(this);
        appManager.getOurInstance().gpsLoggerManager = this;
        ResetTimer();
        ResetSendLocationTimer();
//        if ( !appManager.getOurInstance().appSetupInstance.isAppLocked()) {
//            DlgLockApp dlg = new DlgLockApp(this);
//            dlg.show();
//        }
         if (appManager.getOurInstance().appSetupInstance.getAllowGpsLog())
         {
             //appManager.getOurInstance().gpsLoggerManager.startGPSLogger();
         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // stopGPSLogger();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Application app = (Application) getApplication();
        appManager.getOurInstance(this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Создаем и добавляем первый фрагмент
        MainActivityFragment mainFrag = new MainActivityFragment();
        ft.add(R.id.container, mainFrag, "mainFragment");
        ft.addToBackStack(null);
        // Подтверждаем операцию
        ft.commit();
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
        if (id == R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startGPSLogger()
    {
        turnGPSOn();
        if (LocationDatabase.locDb == null)
            LocationDatabase.locDb = new LocationDatabase(this);
        appManager.getOurInstance().appSetupInstance.setGpsServiceIntent(new Intent(MainActivity.this, GPSLoggerService.class));
        startService(appManager.getOurInstance().appSetupInstance.getGpsServiceIntent());

    }

    public void stopGPSLogger()
    {
        if (appManager.getOurInstance().appSetupInstance.getGpsServiceIntent()!=null)
            stopService(appManager.getOurInstance().appSetupInstance.getGpsServiceIntent());
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
            Toast.makeText(this, "Геолокация выключена. Включите в настройках планшета/смартфона",Toast.LENGTH_LONG).show();
        }
    }

}

