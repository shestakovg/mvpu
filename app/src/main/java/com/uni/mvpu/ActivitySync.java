package com.uni.mvpu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import Entitys.priceType;
import core.TouchActivity;
import core.appManager;
import sync.syncContracts;
import sync.syncDebt;
import sync.syncDebtParams;
import sync.syncPrice;
import sync.syncRoute;
import sync.syncSku;
import sync.syncSkuFact;
import sync.syncSkuGroup;
import sync.syncStock;


public class ActivitySync extends TouchActivity {
    private ListView listSyncOptions;
    private final String IDLI_ROUTE = "Маршрут";
    private final String IDLI_PRODUCT = "Товары";
    private final String IDLI_PRICE = "Цены";
    private final String IDLI_DOCS = "Договора";
    private final String IDLI_STOCK = "Остатки";
    private final String IDLI_DEBT = "Долги";
    private final String IDLI_SPECIFICATION = "Спецификации";
    private String[] mSyncOptions = {IDLI_ROUTE, IDLI_PRODUCT,  IDLI_DOCS, IDLI_STOCK, IDLI_DEBT};
    private ArrayAdapter listAdapter;
    private ArrayList<priceType> priceList;
    private ArrayList<String> priceNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        priceList = appManager.getOurInstance().getPriceType(this);
        ArrayList<String> arrList = new ArrayList<>();
        priceNameList = new ArrayList<>();
        for (String curStr: mSyncOptions){
            arrList.add(curStr);
        }

        for (priceType price : priceList)
        {
            arrList.add("Прайс: "+price.getPriceName());
            priceNameList.add("Прайс: "+price.getPriceName());
        }

        if (appManager.getOurInstance().appSetupInstance.getRouteType() ==1 )
        {
            arrList.add(IDLI_SPECIFICATION);
        }
        String[] syncOptions =        arrList.toArray(new String[arrList.size()] );
        setContentView(R.layout.activity_sync);
        listSyncOptions = (ListView) findViewById(R.id.listViewSyncOptions);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, syncOptions);
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

    private int getCheckedItems()
    {
        int result =0;
        for (int i=0; i<listSyncOptions.getCheckedItemPositions().size();i++)
        {
            int position = listSyncOptions.getCheckedItemPositions().keyAt(i);
            boolean checked = listSyncOptions.getCheckedItemPositions().valueAt(i);
            if (checked) result++;
        }
        return result;
    }
    public void onBtnClickStartSync(View view)
    {
        SparseBooleanArray checkedItems = listSyncOptions.getCheckedItemPositions();
        ProgressDialog pd = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        int syncQty = getCheckedItems();
        if (syncQty>0) {
            pd.setMax(syncQty + 1);
            pd.setTitle("Синхронизация");
            pd.setMessage("Синхронизация");
           // pd.setCancelable(false);
            pd.setProgress(0);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
            syncDebtParams debtParams = new syncDebtParams(this, pd);
            debtParams.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getdebtparams/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
        }

        for (int i=0; i<checkedItems.size();i++)
        {
            int position = checkedItems.keyAt(i);
            boolean checked = checkedItems.valueAt(i);
            if (checked)
            {
                String cuurentItem =  listAdapter.getItem(position).toString();
                int itemIndex = priceNameList.indexOf(cuurentItem);
                if (itemIndex>=0)
                {
                    Toast.makeText(this, "Обновление цен по прайс листу "+priceList.get(itemIndex).getPriceName(), Toast.LENGTH_SHORT).show();
                    syncPrice syncPrice = new syncPrice(this, priceList.get(itemIndex), pd);
                    syncPrice.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getprice/" + priceList.get(itemIndex).getPriceId()});
                }
                switch (listAdapter.getItem(position).toString())
                {
                    case IDLI_ROUTE:
                        //appManager.getOurInstance().setCurrentContext(this);
                        syncRoute syncr = new syncRoute(this, pd);
                        syncr.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(),"dictionary/getrouteset/"+appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break; //Add other menu items
                    case IDLI_DOCS:
                        //appManager.getOurInstance().setCurrentContext(this);
                        syncContracts sync = new syncContracts(this, pd);
                        sync.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getcontract/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_PRODUCT:
                        syncSkuGroup syncGroup = new syncSkuGroup(this, pd);
                        syncGroup.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getskugroup/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        syncSku syncSku = new syncSku(this, pd);
                        syncSku.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getsku"});
                        syncSkuFact skuFact = new syncSkuFact(this, pd);
                        skuFact.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getskufact"});
                        break;
//                    case IDLI_PRICE:
//                        //appManager.getOurInstance().setCurrentContext(this);
//                        //ArrayList<priceType> priceList = appManager.getOurInstance().getPriceType(this);
//                        Toast.makeText(this, "В маршруте "+appManager.getOurInstance().appSetupInstance.getRouteName()+" используется "+priceList.size()+" вида цен. " +
//                                "Обновление займет длительное время", Toast.LENGTH_SHORT).show();
//                        for (priceType price : priceList) {
//                            Toast.makeText(this, "Обновление цен по прайс листу "+price.getPriceName(), Toast.LENGTH_SHORT).show();
//                            syncPrice syncPrice = new syncPrice(this, price);
//                            syncPrice.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getprice/" + price.getPriceId()});
//                        }
//                        break;
                    case IDLI_STOCK:
                        syncStock syncSt= new syncStock(this, pd);
                        syncSt.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getbalancesku/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break; //Add other menu items
                    case IDLI_DEBT:
                        syncDebt sncDept = new syncDebt(this, pd);
                        sncDept.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getdebt/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                }
            }
        }
    }
}
