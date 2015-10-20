package core;

import android.content.Context;
import android.widget.Toast;

import java.util.TimerTask;
import java.util.Timer;

/**
 * Created by shestakov.g on 20.10.2015.
 */
public class AppLocker {
    private static AppLocker appLocker;

    private  Context context = null;
    private lockScreenTimer lockScreenTimerInstance;
    private Timer timer;
    public AppLocker() {

    }

    public static AppLocker getInstance()
    {
        if (appLocker == null) appLocker = new AppLocker();
        return appLocker;
    }

    public void ResetTimer(Context context)
    {
        if (context != this.context) {
            this.lockScreenTimerInstance = null;
            this.lockScreenTimerInstance = new lockScreenTimer(context);
        }
        this.context = context;

        if (timer == null)      timer = new Timer();

        timer.cancel();
        timer.schedule(lockScreenTimerInstance, 1*60*1000);
    }

    public Context getContext() {
        return context;
    }


}

class lockScreenTimer extends TimerTask
{
    private  Context context;

    public lockScreenTimer(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Toast.makeText(this.context, "Timer", Toast.LENGTH_LONG).show();
    }
}
