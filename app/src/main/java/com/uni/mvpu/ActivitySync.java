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
import sync.syncClientCardSku;
import sync.syncContracts;
import sync.syncDebt;
import sync.syncDebtParams;
import sync.syncDeliveryArea;
import sync.syncOutletCategory;
import sync.syncOutletInfo;
import sync.syncPlanFact;
import sync.syncPrice;
import sync.syncPriceChanges;
import sync.syncRoute;
import sync.syncRouteDays;
import sync.syncSku;
import sync.syncSkuFact;
import sync.syncSkuGroup;
import sync.syncSpecification;
import sync.syncStock;
import sync.syncTask;


public class ActivitySync extends TouchActivity {
    private ListView listSyncOptions;
    private final String IDLI_ROUTE = "�������";
    private final String IDLI_PRODUCT = "������";
    private final String IDLI_PRICE = "����";
    private final String IDLI_DOCS = "��������";
    private final String IDLI_STOCK = "�������";
    private final String IDLI_DEBT = "�����";
    private final String IDLI_OUTLETINFO = "�������� �������";
    private final String IDLI_SPECIFICATION = "������������";
    private final String IDLI_PLANFACT = "����/����";
    private final String IDLI_TASKS = "������";
    private String[] mSyncOptions = {IDLI_ROUTE, IDLI_PRODUCT,  IDLI_DOCS, IDLI_STOCK, IDLI_DEBT, IDLI_OUTLETINFO, IDLI_PLANFACT, IDLI_TASKS};
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
            arrList.add("�����: "+price.getPriceName());
            priceNameList.add("�����: "+price.getPriceName());
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
            pd.setTitle("�������������");
            pd.setMessage("�������������");
           // pd.setCancelable(false);
            pd.setProgress(0);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
            syncDebtParams debtParams = new syncDebtParams(this, pd);
            debtParams.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getdebtparams/" + appManager.getOurInstance().appSetupInstance.getRouteId()});

            syncOutletCategory soc = new syncOutletCategory(this);
            soc.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getoutletcategory"});

            syncRouteDays srd = new syncRouteDays(this);
            srd.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getroutedays"});

            syncDeliveryArea sda = new syncDeliveryArea(this);
            sda.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getdeliveryarea"});

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
                    Toast.makeText(this, "���������� ��� �� ����� ����� "+priceList.get(itemIndex).getPriceName(), Toast.LENGTH_SHORT).show();
                    syncPrice syncPrice = new syncPrice(this, priceList.get(itemIndex), pd);
                    syncPrice.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getprice/" + priceList.get(itemIndex).getPriceId()});


                }
                switch (listAdapter.getItem(position).toString())
                {
                    case IDLI_ROUTE:
                        //appManager.getOurInstance().setCurrentContext(this);
                        syncRoute syncr = new syncRoute(this, pd);
                        syncr.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(),"dictionary/getrouteset/"+appManager.getOurInstance().appSetupInstance.getRouteId()});
                        syncPriceChanges syncPc =  new syncPriceChanges(this, pd);
                        syncPc.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(),"dictionary/getpricechanges"});
                        break; //Add other menu items
                    case IDLI_DOCS:
                        //appManager.getOurInstance().setCurrentContext(this);
                        syncContracts sync = new syncContracts(this, pd);
                        sync.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getcontract/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_PRODUCT:
                        syncSkuGroup syncGroup = new syncSkuGroup(this, pd);
                        syncGroup.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getskugroup/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        syncSku syncSku = new syncSku(this, pd);
                        syncSku.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getskuext/"+ appManager.getOurInstance().appSetupInstance.getRouteId()});
                        syncSkuFact skuFact = new syncSkuFact(this, pd);
                        skuFact.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getskufact"});
                        break;
//                    case IDLI_PRICE:
//                        //appManager.getOurInstance().setCurrentContext(this);
//                        //ArrayList<priceType> priceList = appManager.getOurInstance().getPriceType(this);
//                        Toast.makeText(this, "� �������� "+appManager.getOurInstance().appSetupInstance.getRouteName()+" ������������ "+priceList.size()+" ���� ���. " +
//                                "���������� ������ ���������� �����", Toast.LENGTH_SHORT).show();
//                        for (priceType price : priceList) {
//                            Toast.makeText(this, "���������� ��� �� ����� ����� "+price.getPriceName(), Toast.LENGTH_SHORT).show();
//                            syncPrice syncPrice = new syncPrice(this, price);
//                            syncPrice.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl(), "dictionary/getprice/" + price.getPriceId()});
//                        }
//                        break;
                    case IDLI_STOCK:
                        syncStock syncSt= new syncStock(this, pd);
                        syncSt.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getbalancesku/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break; //Add other menu items
                    case IDLI_DEBT:
                        syncDebt sncDept = new syncDebt(this, pd);
                        sncDept.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getdebt/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_SPECIFICATION:
                        syncSpecification sncSpec = new syncSpecification(this, pd);
                        sncSpec.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getspecification/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_OUTLETINFO:
                        syncOutletInfo syncInfo = new syncOutletInfo(this, pd);
                        syncInfo.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getoutletinfo/" + appManager.getOurInstance().appSetupInstance.getRouteId()});

                        syncClientCardSku syncInfoClientCard = new syncClientCardSku(this, pd);
                        syncInfoClientCard.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getclientcardsku/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_PLANFACT:
                        syncPlanFact planFact = new syncPlanFact(this, pd);
                        planFact.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/getsalesfact/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                    case IDLI_TASKS:
                        syncTask synctask = new syncTask(this, pd);
                        synctask.execute(new String[]{appManager.getOurInstance().appSetupInstance.getServiceUrl1c(), "dictionary/gettasks/" + appManager.getOurInstance().appSetupInstance.getRouteId()});
                        break;
                }
            }
        }
    }
}
