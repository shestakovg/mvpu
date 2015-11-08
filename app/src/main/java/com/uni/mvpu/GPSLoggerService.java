package com.uni.mvpu;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by g.shestakov on 25.08.2015.
 */
public class GPSLoggerService  extends Service {
    private static final String TAG = "mvpuGpsLog";
    public class LocalBinder extends Binder {
        GPSLoggerService getService() {
            return GPSLoggerService.this;
        }
    }

    private LocationManager lm;
    private LocationListener locationListener;

    private int lastStatus = 0;
    private static boolean showingDebugToast = true;

    private static final String tag = "GPSLoggerService";

    private final DecimalFormat sevenSigDigits = new DecimalFormat("0.#######");
    private final DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static long minTimeMillis = 2000;
    private static long minDistanceMeters = 10;
    private static float minAccuracyMeters = 35;

    /** Called when the activity is first created. */
    private void startLoggerService() {

        // ---use the LocationManager class to obtain GPS locations---
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTimeMillis,
                minDistanceMeters,
                locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTimeMillis,
                minDistanceMeters,
                locationListener);

    }


    private void shutdownLoggerService() {
        lm.removeUpdates(locationListener);
    }

    private NotificationManager mNM;
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                boolean pointIsRecorded = false;
                if (loc.hasAccuracy() && loc.getAccuracy() <= minAccuracyMeters) {
                    pointIsRecorded = true;
                    //Write to DB
                    Log.d(TAG, " loc.getLatitude() "+ loc.getLatitude());
                    Log.d(TAG, " loc.getLongitude() "+ loc.getLongitude());
                }

                if (pointIsRecorded) {
                    if (showingDebugToast) Toast.makeText(
                            getBaseContext(),
                            "Location stored: \nLat: " + sevenSigDigits.format(loc.getLatitude())
                                    + " \nLon: " + sevenSigDigits.format(loc.getLongitude())
                                    + " \nAlt: " + (loc.hasAltitude() ? loc.getAltitude()+"m":"?")
                                    + " \nAcc: " + (loc.hasAccuracy() ? loc.getAccuracy()+"m":"?"),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (showingDebugToast) Toast.makeText(
                            getBaseContext(),
                            "Location not accurate enough: \nLat: " + sevenSigDigits.format(loc.getLatitude())
                                    + " \nLon: " + sevenSigDigits.format(loc.getLongitude())
                                    + " \nAlt: " + (loc.hasAltitude() ? loc.getAltitude()+"m":"?")
                                    + " \nAcc: " + (loc.hasAccuracy() ? loc.getAccuracy()+"m":"?"),
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
        public void onProviderDisabled(String provider) {
            if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
                    Toast.LENGTH_SHORT).show();

        }

        public void onProviderEnabled(String provider) {
            if (showingDebugToast) Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
                    Toast.LENGTH_SHORT).show();

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            String showStatus = null;
            if (status == LocationProvider.AVAILABLE)
                showStatus = "Available";
            if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
                showStatus = "Temporarily Unavailable";
            if (status == LocationProvider.OUT_OF_SERVICE)
                showStatus = "Out of Service";
            if (status != lastStatus && showingDebugToast) {
                Toast.makeText(getBaseContext(),
                        "new status: " + showStatus,
                        Toast.LENGTH_SHORT).show();
            }
            lastStatus = status;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        startLoggerService();

        // Display a notification about us starting. We put an icon in the
        // status bar.
        showNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shutdownLoggerService();

        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped,
                Toast.LENGTH_SHORT).show();
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the
        // expanded notification
//        CharSequence text = getText(R.string.local_service_started);
//
//        // Set the icon, scrolling text and timestamp
//        Notification notification = new Notification(R.drawable.gpslogger16,
//                text, System.currentTimeMillis());
//
//        // The PendingIntent to launch our activity if the user selects this
//        // notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, GPSLoggerService.class), 0);
//
//        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.service_name),
//                text, contentIntent);
//
//        // Send the notification.
//        // We use a layout id because it is a unique number. We use it later to
//        // cancel.
//        mNM.notify(R.string.local_service_started, notification);
    }
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}