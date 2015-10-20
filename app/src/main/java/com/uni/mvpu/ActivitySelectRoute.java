package com.uni.mvpu;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Entitys.branch;
import Entitys.routeObject;
import core.TouchActivity;
import core.appManager;
import sync.ServiceManager;
import sync.ServiceManager4;

public class ActivitySelectRoute extends TouchActivity {
    private ListView listView;
    protected boolean branchMode = true;
    protected ProgressDialog progressDialog;
    protected List<routeObject> routeObjectList;
    protected List<branch> branchList;
    private String currentBranch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        listView = (ListView) findViewById(R.id.listViewSelectRoute);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClick(parent, view, position, id);
                return true;
            }
        });
        //если выбор организации
        if (branchMode)
        {

            new setupRoute().execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(),"dictionary/getbranch"});
        }
        else //Если выбор маршрута
        {
            new setupRoute().execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(),"dictionary/getroute/"+currentBranch});
        }
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (branchMode)
        {
            Toast.makeText(this, branchList.get(position).description,Toast.LENGTH_SHORT).show();
            currentBranch = branchList.get(position).id;
            branchMode = false;
            new setupRoute().execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(),"dictionary/getroute/"+currentBranch});
        }
        else
        {
            routeObject r =routeObjectList.get(position);
            Toast.makeText(this, routeObjectList.get(position).description,Toast.LENGTH_SHORT).show();
            appManager.getOurInstance().appSetupInstance.setRouteId(r.id);
            appManager.getOurInstance().appSetupInstance.setRouteName(r.description);
            appManager.getOurInstance().appSetupInstance.setEmployeeID(r.employeeId);
            appManager.getOurInstance().appSetupInstance.setEmployeeName(r.employee);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_select_route, menu);
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

    protected void refreshList()
    {
        List <Map<String, ?>> items = new ArrayList<Map<String, ?>>();
        if (branchMode)
        {
            for (branch cbranch:branchList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("description", cbranch.description);
                map.put("other", "");
                items.add(map);
            }
        }
        else
        {
            for (routeObject r:routeObjectList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("description", r.description);
                map.put("other", r.employee);
                items.add(map);
            }
        }
        SimpleAdapter sa = new SimpleAdapter(this, items, android.R.layout.simple_expandable_list_item_2,
                                             new String[] {"description", "other"},
                                             new int[] {android.R.id.text1, android.R.id.text2}
                );
        listView.setAdapter(sa);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    class setupRoute extends AsyncTask<String, Void, List<JSONObject>> {
        private ProgressDialog dialog;
        @Override
        protected List<JSONObject> doInBackground(String... params) {
            ServiceManager4 serviceManager = new ServiceManager4(params[0]);
            return serviceManager.CallDataServiceMultiply(params[1]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(ActivitySelectRoute.this, ,"Подождите, пожалуйста", true, false);
//            this.dialog = progressDialog;
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsonObjects) {
            super.onPostExecute(jsonObjects);
            if (jsonObjects == null)
            {
                Toast.makeText(ActivitySelectRoute.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            if (branchMode)
            {
                branchList= new ArrayList<branch>();
                for (JSONObject jsonObject: jsonObjects) {
                    try {
                            branchList.add( new branch(jsonObject.getString("Id"), jsonObject.getString("Description")));
                    } catch (Exception e) {

                    }

                }

            }
            else
            {
                routeObjectList= new ArrayList<routeObject>();
                for (JSONObject jsonObject: jsonObjects) {
                    try {
                         routeObject r = new routeObject();
                         r.id = UUID.fromString(jsonObject.getString("Id"));
                         r.description = jsonObject.getString("Description");
                         r.code = jsonObject.getString("Code");
                         r.employee = jsonObject.getString("Employee");
                         r.employeeId = UUID.fromString(jsonObject.getString("EmployeeId"));
                         routeObjectList.add(r);
                    } catch (Exception e) {

                    }
                }
            }
            refreshList();
    //        this.dialog.dismiss();
        }
    }

}
