package com.uni.mvpu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import core.AppSettings;
import core.LocationDatabase;
import core.RouteDay;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;
import sync.sendLocation;
import sync.syncRoute;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private View parentView ;
    Button btnSetup;
    Button btnRoute;
    Button btnSyncServer;
    Button btnBeginWork;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_main, container, false);
        btnBeginWork = (Button) parentView.findViewById(R.id.btnBeginWork);
        btnBeginWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBeginWork(v);
            }
        });

        btnRoute = (Button) parentView.findViewById(R.id.btnRouteDay);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRouteDay(v);
            }
        });
        if (LocationDatabase.getInstance()!=null)
        {
            RouteDay rd = LocationDatabase.getInstance().getActiveRouteDay(wputils.getCurrentDate());
            if (rd == null) {
                btnRoute.setText(RouteDay.getEmptyRouteDescription());
                btnBeginWork.setEnabled(false);}
            else
            {
                btnRoute.setText(rd.getRouteDescription());
                btnBeginWork.setEnabled(true);
            }
        }
        //*************************************************************
        btnBeginWork.setEnabled(true);
        btnRoute.setVisibility(View.INVISIBLE);
        //*************************************************************
        btnSetup = (Button) parentView.findViewById(R.id.btnSetup);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSetupClick(v);
            }
        });

        btnSyncServer = (Button) parentView.findViewById(R.id.btnSyncServer);
        btnSyncServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSyncServer(v);
            }
        });
        ((Button) parentView.findViewById(R.id.btnMyOrders)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMyOrders(v);
            }
        });

        ((Button) parentView.findViewById(R.id.btnMyStorecheck)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMyStorecheck(v);
            }
        });

//        if (appManager.getOurInstance().appSetupInstance.getRouteType() == 0 )
//            ((Button) parentView.findViewById(R.id.btnMyStorecheck)).setVisibility(View.INVISIBLE);

        ((Button) parentView.findViewById(R.id.btnMyPays)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appManager.getOurInstance().showPayList(parentView.getContext());
            }
        });
        ((Button) parentView.findViewById(R.id.btnTest)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation syncr = new sendLocation(parentView.getContext());
                syncr.execute(null, null);
            }
        });
        ((Button) parentView.findViewById(R.id.btnNewCustomers)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNewCustomers(v);
            }
        });

        String versionName ="Версия:  "+BuildConfig.VERSION_NAME;
        appManager.getOurInstance().appSetupInstance.version = BuildConfig.VERSION_NAME;
                //parentView.getContext().getPackageManager().getPackageInfo(parentView.getContext().getPackageName(), 0).versionName;
                ((TextView) parentView.findViewById(R.id.textViewVersion)).setText(versionName);
        renderRoute();
        return parentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        renderRoute();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) parentView.findViewById(R.id.textViewRoute)).setText(appManager.getOurInstance().appSetupInstance.getRouteName()+" - "+
                                                                appManager.getOurInstance().appSetupInstance.getRouteTypeDescription()
        );
        renderRoute();
    }

    private void renderRoute()
    {
        int noVisitCount = 0;
        int noResultCount = 0;
        int payCount = 0;
        int orderCount = 0;
        int completedCount = 0;
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        HashMap<String, Integer> map=new HashMap<String, Integer>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK) -2;
        if (dayOfWeek == -1) dayOfWeek=6;
        Cursor cursor = db.rawQuery("select r.outletId , r.CustomerId,  " +
                        " COALESCE(orders.orderCount,0) orderCount, COALESCE(pay.payCount,0) payCount, coalesce(noRes.nrcount, 0) nrcount   from route r   " +
                        " left join (select count(h._id) orderCount, h.outletId  from orderHeader h where  DATETIME(h.orderDate) = ? and _send=1   group by h.outletId) orders on orders.outletId = r.outletId " +
                        " left join (select count(p._id) payCount, p.customerid  from pays p where  DATETIME(p.payDate) = ? and p.paySum >= 10 group by p.customerid) pay on pay.customerid = r.CustomerId " +
                        " left join (select count(nrs._id) nrcount, outletid from No_result_storage nrs where DATETIME(nrs.Date) = ? group by outletid) noRes on noRes.outletId = r.outletId "
                        //" where r.VisitDayId = "+ dayOfWeek,
                ,new String[] { wputils.getDateTime(currentDate), wputils.getDateTime(currentDate), wputils.getDateTime(currentDate)});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            if (cursor.getInt(cursor.getColumnIndex("orderCount")) > 0  && cursor.getInt(cursor.getColumnIndex("payCount")) >0) {
                completedCount++;}
            else
            if (cursor.getInt(cursor.getColumnIndex("orderCount")) > 0) {
                orderCount++;
            }
            else
            if (cursor.getInt(cursor.getColumnIndex("payCount")) > 0) {
                payCount++;
            }
            else
            if (cursor.getInt(cursor.getColumnIndex("nrcount")) > 0) {
                noResultCount++;
            }
            else {
                noVisitCount++;
            }
            cursor.moveToNext();
        }
        double orderSum = getOrderSum(db, currentDate);
        db.close();
        ((TextView) parentView.findViewById(R.id.tvRouteDayNoVisit)).setText(Integer.toString(noVisitCount));
        ((TextView) parentView.findViewById(R.id.tvRouteDayNoResult)).setText(Integer.toString(noResultCount));
        ((TextView) parentView.findViewById(R.id.tvRouteDayPay)).setText(Integer.toString(payCount));
        ((TextView) parentView.findViewById(R.id.tvRouteDayOrder)).setText(Integer.toString(orderCount));
        ((TextView) parentView.findViewById(R.id.tvRouteDayCompleted)).setText(Integer.toString(completedCount));
        ((TextView) parentView.findViewById(R.id.tvRouteDay)).setText("Маршрут за "+ wputils.getDateTimeString(currentDate));
        ((TextView) parentView.findViewById(R.id.tvTotalAmount)).setText(String.format("%.2f",orderSum));
        float totalCount =  noResultCount+ payCount + orderCount + completedCount;
        //totalCount = 10;
        float Efficiency = 0;
        if (totalCount != 0)
             Efficiency = (completedCount + orderCount + payCount) / totalCount *100;
        ((TextView) parentView.findViewById(R.id.tvRouteDayPayEfficiency)).setText("Эффективность: "+ wputils.formatFloat(Efficiency)+" %");
        if (Efficiency >= 60)
            ((TextView) parentView.findViewById(R.id.tvRouteDayPayEfficiency)).setTextColor(Color.GREEN);
        else
            ((TextView) parentView.findViewById(R.id.tvRouteDayPayEfficiency)).setTextColor(Color.RED);
    }

    private double getOrderSum(SQLiteDatabase db , Calendar currentDate )
    {
        double result = 0;
        String query="select sum(coalesce(d.qty1,0) * coalesce(p.pric,0) + coalesce(d.qty2,0) * coalesce(p.pric,0)) as orderSumma from orderHeader h " +
                " inner join orderDetail d on d.headerid = h._id " +
                " inner join (select outletId outletId,max(partnerId) partnerId from  route group by outletId) r on r.outletId = h.outletId "+
                //" inner join (select outletId outletId,partnerId partnerId from  route) r on r.outletId = h.outletId "+
                " left  join contracts con on  con.PartnerId = r.partnerId "+
                //" inner join price p on p.priceId = coalesce(con.PriceId,'"+ AppSettings.PARAM_PRICEID_DEFAULT+"') and d.skuId = p.skuId" +
                " inner join price p on p.priceId = d.PriceId and d.skuId = p.skuId" +
                " where DATETIME(h.orderDate) = ? and h._send=1";
        try {
            Cursor cursor = db.rawQuery(query, new String[]{wputils.getDateTime(currentDate)});
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                result = cursor.getDouble(0);
                cursor.moveToNext();
            }
        }
        catch (Exception e) {
            result= 0;
        }
        return  result;
    }

    private void btnSetupClick(View v)
    {
        Intent intent = new Intent(getActivity(), SetupActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("w", selectedValue);
        startActivity(intent);
    }

    private void btnBeginWork(View v)
    {
        Intent intent = new Intent(getActivity(), ActivityRoute.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("w", selectedValue);
        startActivity(intent);
    }

    private void btnMyOrders(View v)
    {
        appManager.getOurInstance().showOrderListByDay(getActivity(), AppSettings.ORDER_TYPE_ORDER);

    }

    private void btnMyStorecheck(View v)
    {
        appManager.getOurInstance().showOrderListByDay(getActivity(), AppSettings.ORDER_TYPE_STORECHECK);

    }
    private void btnSyncServer(View v)
    {
        Intent intent = new Intent(getActivity(), ActivitySync.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("w", selectedValue);
        startActivity(intent);
    }

    private void btnNewCustomers(View v)
    {
        Intent intent = new Intent(getActivity(), NewCustomersList.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("w", selectedValue);
        startActivity(intent);
    }


    private void btnRouteDay(View v)
    {
        if (LocationDatabase.getInstance()!=null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(parentView.getContext());

            builder.setTitle("Confirm");
            if (LocationDatabase.getInstance().currentRoute == null) {
                builder.setMessage("Открыть маршрут за "+wputils.getDateTimeString(wputils.getCurrentDate())+" ?");}
            else
            {
                builder.setMessage("Закрыть маршрут за "+wputils.getDateTimeString(wputils.getCurrentDate())+"?");
            }

            builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    if (LocationDatabase.getInstance().currentRoute == null)
                    {
                         RouteDay rd = LocationDatabase.getInstance().openRouteDay(wputils.getCurrentDate());
                         btnRoute.setText(rd.getRouteDescription());
                        btnBeginWork.setEnabled(true);
                    }
                    else
                    {
                        LocationDatabase.getInstance().closeRouteDate(LocationDatabase.getInstance().currentRoute);
                        btnRoute.setText(RouteDay.getEmptyRouteDescription());
                        btnBeginWork.setEnabled(false);
                    }
                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
