package Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import core.appManager;

/**
 * Created by shestakov.g on 22.10.2015.
 */
public class DlgLockApp extends Dialog {
    private Context context;
    public DlgLockApp(Context context) {
        super(context);
        appManager.getOurInstance().appSetupInstance.firtsStart = false;
        this.context = context;
        this.setTitle("Приложение заблокировано. Введите пароль");
        this.setContentView(R.layout.dialog_lockpassword);
        this.setCancelable(false);
        final Dialog dlg = this;
        Button btnOK =(Button) this.findViewById(R.id.dlgLockBtnInputOK);
        final TextView passwordTextView = (TextView) dlg.findViewById(R.id.dlgLockPasword);
        appManager.getOurInstance().appSetupInstance.setAppLocked(true);
        final Context dlgContext = this.context;

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPassword(passwordTextView.getText().toString())) {
                    appManager.getOurInstance().appSetupInstance.setLastTouch();
                    appManager.getOurInstance().appSetupInstance.setAppLocked(false);
                    dlg.dismiss();
                }
                else
                {
                    Toast.makeText(dlgContext, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    passwordTextView.setText("");
                }
            }
        });
    }

    private boolean checkPassword(String password)
    {
        if (password.equals(appManager.getOurInstance().appSetupInstance.getLockPasswod()))
            return true;
        else
            return false;
    }
}
