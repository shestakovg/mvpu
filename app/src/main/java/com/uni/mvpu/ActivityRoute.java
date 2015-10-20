package com.uni.mvpu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.PopupMenu;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Entitys.Order;
import Entitys.OutletObject;
import core.TouchActivity;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;

public class ActivityRoute extends TouchActivity {
    private Context currentContext;
    private Spinner spinner;
    private ListView listRoute;
    private  SimpleAdapter sa;
    private List<OutletObject> outletsObjectList;
    //private String[] outletsId;
    private OutletObject selectedOutlet;
    private String routeWhere = "";
    private String[] daysString = {"Все торговые точки", "Понедельник", "Вторник", "Среда", "Четверг","Пятница","Суббота","Воскресенье"};
    private int[] daysInt = {-1,0,1,2,3,4,5,6};
//    public ActivityRoute() {
//    }

    private final int IDD_THREE_BUTTONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentContext = this;

//        fillWhereCondition(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -Calendar.getInstance().getFirstDayOfWeek());
        setContentView(R.layout.activity_route);
        listRoute = (ListView) findViewById(R.id.listViewRoute);
        sa = new SimpleAdapter(this, createRouteList(), android.R.layout.simple_expandable_list_item_2,
                new String[] {"name", "adress"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        listRoute.setAdapter(sa);
        //listRoute.setItemsCanFocus(false);
        listRoute.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listRoute.setOnItemLongClickListener(new OnItemLongClickListener() {
                                                 public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                     onItemClick(parent, view, position, id);
                                                     return true;
                                                 }
                                             }
        );

        spinner = (Spinner) findViewById(R.id.spinnerDays);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, daysString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Дни визита");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                fillWhereCondition(position);
                sa = new SimpleAdapter(getBaseContext(), createRouteList(), android.R.layout.simple_expandable_list_item_2,
                        new String[] {"name", "adress"},
                        new int[] {android.R.id.text1, android.R.id.text2}
                );
                listRoute.setAdapter(sa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_route, menu);
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
            appManager.getOurInstance().sendDataToServer(this, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Map<String, ? >> createRouteList()
    {
        List<Map<String, ? >> items = new ArrayList<Map<String, ? >>() ;
        outletsObjectList = new ArrayList<OutletObject>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select r.outletId , r.outletName , r.VisitDay  , r.VisitDayId ,r.VisitOrder , r.CustomerId , " +
                "r.CustomerName, r.partnerId, r.partnerName, r.address, con.PriceId, COALESCE(con.PriceName,'') as PriceName from route r   " +
                "left join contracts con on r.partnerId = con.partnerId  " + routeWhere + " order by VisitDayId,outletName ", null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            Map<String, Object> map= new HashMap<String, Object>();
            map.put("name", cursor.getString(cursor.getColumnIndex("outletName")));
            map.put("adress", cursor.getString(cursor.getColumnIndex("address")) + "  День: " + cursor.getString(cursor.getColumnIndex("VisitDay")));
            items.add(map);
            OutletObject ob = new OutletObject();
            ob.customerId = UUID.fromString(cursor.getString(cursor.getColumnIndex("CustomerId")));
            ob.outletId = UUID.fromString(cursor.getString(cursor.getColumnIndex("outletId")));
            ob.partnerId = UUID.fromString(cursor.getString(cursor.getColumnIndex("partnerId")));
            ob.customerName = cursor.getString(cursor.getColumnIndex("CustomerName"));
            ob.outletName = cursor.getString(cursor.getColumnIndex("outletName"));
            ob.partnerName = cursor.getString(cursor.getColumnIndex("partnerName"));
            ob.outletAddress = cursor.getString(cursor.getColumnIndex("address"));
            ob.priceName = cursor.getString(cursor.getColumnIndex("PriceName"));
            if (!ob.priceName.isEmpty())
            {
                ob.priceId = UUID.fromString(cursor.getString(cursor.getColumnIndex("PriceId")));
            }
            else
            {
                ob.priceId = UUID.fromString(appManager.getOurInstance().appSetupInstance.getDefaultPrice());
            }
            outletsObjectList.add(ob);
            cursor.moveToNext();
        }
        return items;

    }


    private void onItemClick(AdapterView<?> parent, View arg1, int position, long id)
    {
        if (outletsObjectList != null)
        {
            selectedOutlet = outletsObjectList.get(position);
            //appManager.getOurInstance().setActiveOutletObject(selectedOutlet);
          //  Toast.makeText(this, selectedOutlet.outletId.toString(), Toast.LENGTH_SHORT).show();
            showPopupMenu(arg1,outletsObjectList.get(position) );
        }
    }

    private void fillWhereCondition(int position)
    {
        if (daysInt[position] == -1) {
            routeWhere = "";
        }
        else {
            routeWhere = " where VisitDayId = "+ Integer.toString(daysInt[position]);
        }

    }

    private void showPopupMenu(View v, OutletObject outlet ) {
        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.getMenuInflater().inflate(R.menu.popupmenu_route, popupMenu.getMenu());
        popupMenu.getMenu().getItem(0).setTitle("Заказы: "+ outlet.outletName);
        //popupMenu.inflate(R.menu.popupmenu_route);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.menuOrders:
//                                Toast.makeText(getApplicationContext(),
//                                        "Вы выбрали PopupMenu 1",
//                                        Toast.LENGTH_SHORT).show();

                                Calendar currentDate = Calendar.getInstance();
                                currentDate.setTime(new Date());
                                boolean paymentExists = appManager.getOurInstance().checkAnnouncedSum(getBaseContext(), selectedOutlet.customerId.toString(), currentDate);
                                if (!paymentExists)
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
                                    builder.setTitle("Важное сообщение!")
                                            .setMessage("По клиенту " + selectedOutlet.customerName + " не заявлена оплата. Заявите сумму платежа!")
                                            .setIcon(R.drawable.hrn)
                                            .setCancelable(false)
                                            .setNegativeButton("ОК",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    return true;
                                }

                                double overdueSum =  appManager.getOurInstance().getOverdueSum(getBaseContext(),selectedOutlet.customerId.toString(), currentDate);
                                boolean OverdueExists = false;
                                OverdueExists =
                                        (overdueSum - appManager.getOurInstance().appSetupInstance.getAllowOverdueSum()) >0 && appManager.getOurInstance().appSetupInstance.isDebtControl() ;
                                if (OverdueExists)
                                {
                                    //Toast.makeText(getBaseContext(), "Просрочка "+Double.toString(overdueSum), Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
                                    builder.setTitle("Важное сообщение!")
                                            .setMessage("По клиенту " + selectedOutlet.customerName + " просрочка " + String.format("%.2f", overdueSum)
                                                    +" грн.\nОТГРУЗКА ЗАПРЕЩЕНА!")
                                            .setIcon(R.drawable.hrn)
                                            .setCancelable(false)
                                            .setNegativeButton("ОК",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    return true;
                                }

                                if (paymentExists && !OverdueExists)
                                {
                                    appManager.getOurInstance().showOrderList(selectedOutlet, ActivityRoute.this);
                                }
                                return true;
                            case R.id.menuDebt:
                                appManager.getOurInstance().showDebtList(selectedOutlet, ActivityRoute.this);
                                return true;
//                            case R.id.menu3:
//                                Toast.makeText(getApplicationContext(),
//                                        "Вы выбрали PopupMenu 3",
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
                            default:
                                return false;
                        }
                    }
                });
        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[] { boolean.class };
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            popupMenu.show();
            return;
        }

        popupMenu.show();
    }
}
