package com.uni.mvpu;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import core.appManager;
import sync.syncContracts;
import sync.syncRoute;
import sync.syncSku;
import sync.syncSkuGroup;


public class ActivitySync extends ActionBarActivity {
    private ListView listSyncOptions;
    private final String IDLI_ROUTE = "Маршрут";
    private final String IDLI_PRODUCT = "Товары";
    private final String IDLI_PRICE = "Цены";
    private final String IDLI_DOCS = "Договора";
    private final String IDLI_STOCK = "Остатки";
    private String[] mSyncOptions = {IDLI_ROUTE, IDLI_PRODUCT, IDLI_PRICE, IDLI_DOCS, IDLI_STOCK};
    private ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        listSyncOptions = (ListView) findViewById(R.id.listViewSyncOptions);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mSyncOptions);
        listSyncOptions.setAdapter(listAdapter);
        listSyncOptions.setItemsCanFocus(false);
        listSyncOptions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_sync, menu);
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

    public void onBtnClickStartSync(View view)
    {
        SparseBooleanArray checkedItems = listSyncOptions.getCheckedItemPositions();
        for (int i=0; i<checkedItems.size();i++)
        {
            int position = checkedItems.keyAt(i);
            boolean checked = checkedItems.valueAt(i);
            if (checked)
            {
                //String cuurentItem =  listAdapter.getItem(position).toString();
                switch (listAdapter.getItem(position).toString())
                {
                    case IDLI_ROUTE:
                        appManager.getOurInstance().setCurrentContext(this);
                        new syncRoute().execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(),"dictionary/getrouteset/"+appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break; //Add other menu items
                    case IDLI_DOCS:
                        //appManager.getOurInstance().setCurrentContext(this);
                        syncContracts sync = new syncContracts(this);
                        sync.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getcontract/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_PRODUCT:
                        syncSkuGroup syncGroup = new syncSkuGroup(this);
                        syncGroup.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getskugroup"});
                        syncSku syncSku = new syncSku(this);
                        syncSku.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getsku"});
                        break;
                }
            }
        }
    }
}
