package com.uni.mvpu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import Adapters.RouteAdapter;
import Entitys.DeliveryArea;
import Entitys.NoResultReason;
import Entitys.Order;
import Entitys.OutletObject;
import core.AppSettings;
import core.LocationDatabase;
import core.TouchActivity;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;

public class ActivityRoute extends TouchActivity {
    private Context currentContext;
    private Spinner spinner;
    private ListView listRoute;
    private  SimpleAdapter sa;
    private ArrayList<OutletObject> outletsObjectList;
    //private String[] outletsId;
    private OutletObject selectedOutlet;
    private RouteAdapter ra;
    private String routeWhere = "";
    private String[] daysString = {"Все торговые точки", "Понедельник", "Вторник", "Среда", "Четверг","Пятница","Суббота","Воскресенье"};
    private int[] daysInt = {-1,0,1,2,3,4,5,6};

    private Calendar orderDate;
    private final int IDD_THREE_BUTTONS = 0;
    private List<DeliveryArea> deliveryAreas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentContext = this;

        orderDate = Calendar.getInstance();
        orderDate.setTime(new Date());

//        fillWhereCondition(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -Calendar.getInstance().getFirstDayOfWeek());
        setContentView(R.layout.activity_route);
        listRoute = (ListView) findViewById(R.id.listViewRoute);
        createRouteList();
//        sa = new SimpleAdapter(this, createRouteList(), android.R.layout.simple_expandable_list_item_2,
//                new String[] {"name", "adress"},
//                new int[] {android.R.id.text1, android.R.id.text2}
//        );


        spinner = (Spinner) findViewById(R.id.spinnerDays);
        deliveryAreas = appManager.getOurInstance().getDeliveryAreas(this);
        String[] areas = new String[deliveryAreas.size()];
        int i = 0;
        for (DeliveryArea a : deliveryAreas) {
            areas[i++] = a.getDescription();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Район");

        int currentSpinnerId = orderDate.get(Calendar.DAY_OF_WEEK) - 1;
        //if (currentSpinnerId == 0) currentSpinnerId=7;
        //spinner.setSelection(currentSpinnerId);
        //fillWhereCondition(currentSpinnerId);
        fillWhereCondition(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                fillWhereCondition(position);
//                sa = new SimpleAdapter(getBaseContext(), createRouteList(), android.R.layout.simple_expandable_list_item_2,
//                        new String[] {"name", "adress"},
//                        new int[] {android.R.id.text1, android.R.id.text2}
//                );
                createRouteList();
                ra = new RouteAdapter(currentContext, outletsObjectList );
                listRoute.setAdapter(ra);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ra = new RouteAdapter(this, outletsObjectList );
        listRoute.setAdapter(ra);
        //listRoute.setItemsCanFocus(false);
        listRoute.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listRoute.setOnItemLongClickListener(new OnItemLongClickListener() {
                                                 public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                     onItemClick(parent, view, position, id);
                                                     return true;
                                                 }
                                             }
        );
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
                "r.CustomerName, r.partnerId, r.partnerName, r.address, con.PriceId, COALESCE(con.PriceName,'') as PriceName, r.IsRoute, " +
                " COALESCE(orders.orderCount,0) orderCount, COALESCE(pay.payCount,0) payCount, coalesce(noRes.nrcount, 0) nrcount   from route r   " +
                " left join (select count(h._id) orderCount, h.outletId  from orderHeader h where  DATETIME(h.orderDate) = ? and _send=1   group by h.outletId) orders on orders.outletId = r.outletId " +
                " left join (select count(p._id) payCount, p.customerid  from pays p where  DATETIME(p.payDate) = ? and p.paySum >= 10 group by p.customerid) pay on pay.customerid = r.CustomerId " +
                " left join (select count(nrs._id) nrcount, outletid from No_result_storage nrs where DATETIME(nrs.Date) = ? group by outletid) noRes on noRes.outletId = r.outletId "+
                " left join contracts con on r.partnerId = con.partnerId  " + routeWhere + " order by VisitDayId, VisitOrder, outletName ",
                new String[] { wputils.getDateTime(orderDate), wputils.getDateTime(orderDate), wputils.getDateTime(orderDate)});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            Map<String, Object> map= new HashMap<String, Object>();
            map.put("name", cursor.getString(cursor.getColumnIndex("outletName")));
            map.put("adress", cursor.getString(cursor.getColumnIndex("address")) + "  День: " + cursor.getString(cursor.getColumnIndex("VisitDay")));
            items.add(map);
            UUID outletUuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("outletId")));
            OutletObject ob = OutletObject.getInstance(outletUuid, this);
            ob.customerId = UUID.fromString(cursor.getString(cursor.getColumnIndex("CustomerId")));
            ob.outletId = outletUuid;
            ob.partnerId = UUID.fromString(cursor.getString(cursor.getColumnIndex("partnerId")));
            ob.customerName = cursor.getString(cursor.getColumnIndex("CustomerName"));
            ob.outletName = cursor.getString(cursor.getColumnIndex("outletName"));
            ob.partnerName = cursor.getString(cursor.getColumnIndex("partnerName"));
            ob.outletAddress = cursor.getString(cursor.getColumnIndex("address"));
            ob.priceName = cursor.getString(cursor.getColumnIndex("PriceName"));
            ob.dayOfWeekStr = cursor.getString(cursor.getColumnIndex("VisitDay"));
            ob.OrderCount = cursor.getInt(cursor.getColumnIndex("orderCount"));
            ob.PayCount = cursor.getInt(cursor.getColumnIndex("payCount"));
            ob.FailVisit = (cursor.getInt(cursor.getColumnIndex("nrcount")) > 0 ? true : false);
            if (!ob.priceName.isEmpty())
            {
                ob.priceId = UUID.fromString(cursor.getString(cursor.getColumnIndex("PriceId")));
            }
            else
            {
                ob.priceId = UUID.fromString(appManager.getOurInstance().appSetupInstance.getDefaultPrice());
            }
            ob.IsRoute = (cursor.getInt(cursor.getColumnIndex("IsRoute")) == 1 ? true : false);
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
//        if (daysInt[position] == -1) {
//            routeWhere = "";
//        }
//        else {
//            routeWhere = " where VisitDayId = "+ Integer.toString(daysInt[position]);
//        }
          routeWhere = position == 0 ? "" : " where DeliveryAreaId = '"+ deliveryAreas.get(position).getIdRef() + "'";
    }

    private void askForCheckIn()
    {
        final Calendar checkInDate = Calendar.getInstance();
        checkInDate.setTime(new Date());

        if (appManager.getOurInstance().appSetupInstance.getAllowGpsLog() && LocationDatabase.getInstance() != null && LocationDatabase.getInstance().isLocated()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setTitle("Отметка в торговой точке")
                    .setMessage("Отметить посещение " + selectedOutlet.outletName)
                    .setIcon(R.drawable.placeholder)
                    .setCancelable(false)
                    .setPositiveButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    GPSLoggerService.updateLastLocation();
                                    LocationDatabase.getInstance().SaveOutletCheckIn(selectedOutlet.outletId.toString(), checkInDate);
                                    showOrders(AppSettings.ORDER_TYPE_ORDER);
                                }
                            });
            if (LocationDatabase.getInstance().IsOutletCheckIn(selectedOutlet.outletId.toString(), checkInDate)) {
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        showOrders(AppSettings.ORDER_TYPE_ORDER);
                    }
                });
            }
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            showOrders(AppSettings.ORDER_TYPE_ORDER);
        }
    }

    private void showOrders(int orderType)
    {
//        if (!LocationDatabase.getInstance().isLocated())
//        {
//            Toast.makeText(this,"Геолокация невозможна\nВыключена геолокация на устройстве\nВключите в настройках планшета/смартфона",Toast.LENGTH_LONG).show();
//            return;
//        }
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        boolean paymentExists = appManager.getOurInstance().checkAnnouncedSum(getBaseContext(), selectedOutlet.customerId.toString(), currentDate);
//        try {
//            if (appManager.getOurInstance().appSetupInstance.getAllowGpsLog() && LocationDatabase.getInstance() != null && LocationDatabase.getInstance().isLocated()) {
//                if (appManager.getOurInstance().getYesNoWithExecutionStop("Отметка в торговой точке", "Отметить посещение " + selectedOutlet.outletName, currentContext, R.drawable.placeholder,
//                        LocationDatabase.getInstance().IsOutletCheckIn(selectedOutlet.outletId.toString(), checkInDate))) {
//
//                    LocationDatabase.getInstance().SaveOutletCheckIn(selectedOutlet.outletId.toString(), checkInDate);
//                }
//            }
//        }
//        catch (Exception e)   { Toast.makeText(currentContext,e.getMessage(),Toast.LENGTH_SHORT).show();      }

        try {
                if (appManager.getOurInstance().appSetupInstance.isDebtControl() && !paymentExists && appManager.getOurInstance().appSetupInstance.getRouteType()!=1)
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
                    return ;
                }
        }
        catch (Exception e)   { Toast.makeText(currentContext,"paymentExists  \n"+e.getMessage(),Toast.LENGTH_SHORT).show();      }

        double overdueSum =  appManager.getOurInstance().getOverdueSum(getBaseContext(),selectedOutlet.customerId.toString(), currentDate);
        boolean OverdueExists = false;
        OverdueExists =
                (overdueSum - appManager.getOurInstance().appSetupInstance.getAllowOverdueSum()) >0 && appManager.getOurInstance().appSetupInstance.isDebtControl() ;

         if (OverdueExists && orderType== AppSettings.ORDER_TYPE_ORDER &&  appManager.getOurInstance().appSetupInstance.getRouteType()!=1)
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
            return;
        }
        if (orderType== AppSettings.ORDER_TYPE_STORECHECK) {
            appManager.getOurInstance().showOrderList(selectedOutlet, ActivityRoute.this, orderType);
        }

        if (
                (!appManager.getOurInstance().appSetupInstance.isDebtControl() || (paymentExists && !OverdueExists && orderType== AppSettings.ORDER_TYPE_ORDER))
                ||
                        appManager.getOurInstance().appSetupInstance.getRouteType()==1
                )
        {
            appManager.getOurInstance().showOrderList(selectedOutlet, ActivityRoute.this, orderType);
        }
    }

    private void showPopupMenu(View v, OutletObject outlet ) {
        PopupMenu popupMenu = new PopupMenu(this, v);

        popupMenu.getMenuInflater().inflate(R.menu.popupmenu_route, popupMenu.getMenu());
        popupMenu.getMenu().getItem(0).setTitle("Заказы: "+ outlet.outletName);
        if (!selectedOutlet.IsRoute)
        {
            popupMenu.getMenu().getItem(0).setEnabled(false);
            popupMenu.getMenu().getItem(0).setTitle(outlet.outletName + " - не по маршруту");
        }
        if (appManager.getOurInstance().appSetupInstance.getRouteType()!=1)
        {
            popupMenu.getMenu().getItem(2).setVisible(false);
        }
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
                                //showOrders(AppSettings.ORDER_TYPE_ORDER);
                                askForCheckIn();
                                return true;
                            case R.id.menuStorecheck:
                                showOrders(AppSettings.ORDER_TYPE_STORECHECK);
                                return true;
                            case R.id.menuDebt:
                                appManager.getOurInstance().showDebtList(selectedOutlet, ActivityRoute.this);
                                return true;
//                            case R.id.menu3:
//                                Toast.makeText(getApplicationContext(),
//                                        "Вы выбрали PopupMenu 3",
//                                        Toast.LENGTH_SHORT).show();
//                                return true;
                            case R.id.menuOutletInfo:
                                showOutletInfo(selectedOutlet);
                                return true;
                            case R.id.menuNoResult:
                                showNoResultActivity(selectedOutlet);
                                return true;
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

    private void showNoResultActivity(OutletObject outlet) {
        Intent intent = new Intent(this, ActivityNoResult.class);
        intent.putExtra("outletId",outlet.outletId.toString() );
        intent.putExtra("outletName",outlet.outletName.toString());
        intent.putExtra("routeDate",orderDate);
        startActivity(intent);
    }

    private void showOutletInfo(OutletObject outlet)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select oi.Category ,oi.Manager1 ,oi.Manager2 ,oi.Phone1 ,oi.Phone2 ,oi.DeliveryDay ,oi.ManagerTime ,oi.ReciveTime ,oi.ContactPerson, r.CustomerClass  from OutletInfo oi " +
                " inner join route r on r.outletId = oi.OutletId"+
                " where oi.OutletId=?", new String[] {outlet.outletId.toString()});
        cursor.moveToFirst();
        String Category ="";
        String Manager1="";
        String Manager2="";
        String Phone1="";
        String Phone2="";
        String DeliveryDay="";
        String ManagerTime="";
        String ReciveTime="";
        String ContactPerson="";
        String CustomerClass = "";
        for (int i = 0; i < cursor.getCount(); i++) {
            Category = cursor.getString(cursor.getColumnIndex("Category"));
            Manager1 = cursor.getString(cursor.getColumnIndex("Manager1"));
            Manager2 = cursor.getString(cursor.getColumnIndex("Manager2"));
            Phone1 = cursor.getString(cursor.getColumnIndex("Phone1"));
            Phone2 = cursor.getString(cursor.getColumnIndex("Phone2"));
            DeliveryDay = cursor.getString(cursor.getColumnIndex("DeliveryDay"));
            ManagerTime = cursor.getString(cursor.getColumnIndex("ManagerTime"));
            ReciveTime = cursor.getString(cursor.getColumnIndex("ReciveTime"));
            ContactPerson = cursor.getString(cursor.getColumnIndex("ContactPerson"));
            CustomerClass = cursor.getString(cursor.getColumnIndex("CustomerClass"));
            cursor.moveToNext();
        }
        db.close();
        String msg = "Категория: "+Category+"\n"+
                     "ЛПР1: "+ (Manager1=="" ? ContactPerson : Manager1)+"\n"+
                     "ЛПР2: "+ Manager2+"\n"+
                     "Телефон ЛПР1: "+Phone1+"\n"+
                     "Телефон ЛПР2: "+Phone2+"\n"+
                     "День доставки: "+DeliveryDay+"\n"+
                     "Время работы ЛПР: "+ManagerTime+"\n"+
                     "Время приемки товара: "+ReciveTime+"\n"+
                     "Класс: "+CustomerClass   ;
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle("Клиент: !"+selectedOutlet.customerName)
                .setMessage(msg)
                //.setIcon(R.drawable.hrn)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onResume() {
        createRouteList();
        ra = new RouteAdapter(this, outletsObjectList );
        listRoute.setAdapter(ra);
        super.onResume();
    }
}
