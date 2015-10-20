package core;

import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

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
                ResetTimer();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void ResetTimer(){
        createTimerTask();
        if (t != null)
        {
            t.cancel(); t = null;
        }
        t = new Timer();

        t.schedule(task, appManager.getOurInstance().appSetupInstance.getLockTimeOut()*60*1000);
    }

    private void createTimerTask()
    {
            if (this.task != null) return;
            this.task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(TouchActivityInstance, "Timer", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
    }
}
