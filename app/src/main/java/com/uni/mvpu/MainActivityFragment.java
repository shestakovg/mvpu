package com.uni.mvpu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import core.AppSettings;
import core.LocationDatabase;
import core.RouteDay;
import core.appManager;
import core.wputils;
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
        String versionName ="Версия:  "+BuildConfig.VERSION_NAME;
        appManager.getOurInstance().appSetupInstance.version = BuildConfig.VERSION_NAME;
                //parentView.getContext().getPackageManager().getPackageInfo(parentView.getContext().getPackageName(), 0).versionName;
                ((TextView) parentView.findViewById(R.id.textViewVersion)).setText(versionName);

        return parentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) parentView.findViewById(R.id.textViewRoute)).setText(appManager.getOurInstance().appSetupInstance.getRouteName()+" - "+
                                                                appManager.getOurInstance().appSetupInstance.getRouteTypeDescription()
        );
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
