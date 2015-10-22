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
public class DlgChangeLockPassword extends Dialog {
    private Context context;
    public DlgChangeLockPassword(final Context context) {
        super(context);
        this.context = context;
        this.setTitle("Изменение пароля блокировки");
        this.setContentView(R.layout.dialog_lockpassword_change);
        this.setCancelable(false);
        final Dialog dlg = this;
        Button btnOK =(Button) this.findViewById(R.id.dlgLockChangeBtnInputOK);
        Button btnCancel =(Button) this.findViewById(R.id.dlgLockChangeBtnInputCancel);
        final TextView oldPasswordTextView = (TextView) dlg.findViewById(R.id.dlgOldLockPaswordChange);
        final TextView newPasswordTextView = (TextView) dlg.findViewById(R.id.dlgNewLockPaswordChange);
        final TextView newPasswordConfirmTextView = (TextView) dlg.findViewById(R.id.dlgNewLockPaswordChangeConfirm);
       // final Context dlgContext = this.context;

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPassword(oldPasswordTextView.getText().toString(), newPasswordTextView.getText().toString(),newPasswordConfirmTextView.getText().toString() )) {
                    appManager.getOurInstance().appSetupInstance.saveSetup(context);
                    dlg.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }

    private boolean checkPassword(String oldPassword, String newPassword, String newPasswordConfirm)
    {

        if (!oldPassword.equals(appManager.getOurInstance().appSetupInstance.getLockPasswod())) {
            Toast.makeText(context, "Неверно указан старый пароль", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.isEmpty())
        {
            Toast.makeText(context, "Новый пароль не может быть пустым", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.equals(newPasswordConfirm))
        {
            Toast.makeText(context, "Новый пароль и подтверждение пароля не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }
        appManager.getOurInstance().appSetupInstance.setLockPasswod(newPassword);
        return true;
    }
}
