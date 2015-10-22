package core;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import Dialogs.DlgInputPay;
import Dialogs.DlgLockApp;

/**
 * Created by shestakov.g on 20.10.2015.
 */
public class TouchActivity extends ActionBarActivity {
    Timer t;
    TimerTask task;
    protected final TouchActivity TouchActivityInstance = this;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(getApplicationContext(), "dispatchTouchEvent"+wputils.getCurrentDate(), Toast.LENGTH_SHORT).show();
               // AppLocker.getInstance().ResetTimer(this);
                appManager.getOurInstance().appSetupInstance.setActiveWindow(this);
                //appManager.getOurInstance().appSetupInstance.firtsStart = false;
                ResetTimer();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    protected void ResetTimer(){

        appManager.getOurInstance().appSetupInstance.setLastTouch();
        if (t != null)
        {

        }
        else {
            t = new Timer();
            createTimerTask();
            try {
                t.scheduleAtFixedRate(task, 100, 10000);//appManager.getOurInstance().appSetupInstance.getLockTimeOut()*60*1000);

            }
            catch (Exception e)
            {
                Toast.makeText(TouchActivityInstance, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }


    }

    private boolean allowLockApp()
    {
        Calendar calendar= Calendar.getInstance(TimeZone.getDefault());
        long diffInMillisec =   calendar.getTimeInMillis() - appManager.getOurInstance().appSetupInstance.getLastTouch().getTimeInMillis();
        long diffInMin = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec) / 60;
        if (
                (diffInMin >= appManager.getOurInstance().appSetupInstance.getLockTimeOut()) &&
                    !appManager.getOurInstance().appSetupInstance.isAppLocked()
                )
            return true;
        else
            return false;
    }

    private void createTimerTask()
    {
            if (this.task != null)
            {

                return;
            }
            this.task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (allowLockApp()) LockApp();

                    }
                });
            }
        };
    }

    public void LockApp()
    {
        //Toast.makeText(TouchActivityInstance, "Timer", Toast.LENGTH_SHORT).show();
        DlgLockApp dlg = new DlgLockApp(appManager.getOurInstance().appSetupInstance.getActiveWindow());
        dlg.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        appManager.getOurInstance().appSetupInstance.setActiveWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appManager.getOurInstance().appSetupInstance.setActiveWindow(this);
        if (appManager.getOurInstance().appSetupInstance.isAppLocked() || appManager.getOurInstance().appSetupInstance.firtsStart)
            LockApp();
    }
}
