package com.uni.mvpu;

import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

//import core.MyApplication;
import Service.GPSLoggerService;
import core.appManager;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appManager.getOurInstance(this);
        startGPSLogger();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGPSLogger();
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

    private void startGPSLogger()
    {
        startService(new Intent(MainActivity.this,
                GPSLoggerService.class));
    }

    private void stopGPSLogger()
    {
        stopService(new Intent(MainActivity.this,
                GPSLoggerService.class));
    }
}
