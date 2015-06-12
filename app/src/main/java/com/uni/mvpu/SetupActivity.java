package com.uni.mvpu;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import core.appManager;


public class SetupActivity extends ActionBarActivity {
    private EditText txtServiceLink;
    private EditText txtRouteName;
    private EditText txtEmployeeName;
    private appManager m_appManager = appManager.getOurInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        txtServiceLink = (EditText) findViewById(R.id.editTextServiceLink);
        txtServiceLink.setText(m_appManager.appSetupInstance.getServiceUrl());
        txtRouteName = (EditText) findViewById(R.id.editTextRouteName);
        txtEmployeeName  = (EditText) findViewById(R.id.editTextEmployeeName);
        refreshSetup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshSetup();
    }

    private void refreshSetup()
    {
        txtEmployeeName.setText(m_appManager.appSetupInstance.getEmployeeName());
        txtRouteName.setText(m_appManager.appSetupInstance.getRouteName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
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

    private void saveSetup()
    {
        Toast.makeText(this, "SAVE", Toast.LENGTH_SHORT).show();

        m_appManager.appSetupInstance.setServiceUrl(txtServiceLink.getText().toString().trim());
        m_appManager.appSetupInstance.setRouteName(txtRouteName.getText().toString());
        m_appManager.appSetupInstance.setEmployeeName(txtEmployeeName.getText().toString());
        m_appManager.appSetupInstance.saveSetup(this);
        m_appManager.appSetupInstance.readSetup(this);
    }

    public void onClickBtnSave(View v)
    {
        saveSetup();
        finish();
    }

    public void onClickSelectRoute(View v)
    {
        saveSetup();
        Intent intent = new Intent(this, ActivitySelectRoute.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("w", selectedValue);
        startActivity(intent);
        refreshSetup();
    }
}
