package Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import Entitys.Task;
import Helpers.taskHelper;
import core.ResultObject;

public class DlgEditTask extends Dialog {
    Task task;

    public DlgEditTask(@NonNull Context context, Task task) {
        super(context);
        this.task = task;
        final Context _context = context;
        this.setTitle("Выполнить задачу");
        this.setContentView(R.layout.dlg_edit_task);
        this.setCancelable(true);
        ((TextView) this.findViewById(R.id.tvDescription)).setText(task.getDescription());
        ((TextView) this.findViewById(R.id.tvNumber)).setText("№ "+task.getNumber());
        final EditText resDescription = (EditText) this.findViewById(R.id.edResult);
        resDescription.setText(task.getResultDescription());
        Button btnOK =(Button) this.findViewById(R.id.btnOK);
        Button btnCancel =(Button) this.findViewById(R.id.btnCancel);
        final Dialog dlg = this;
        final Task taskObject = this.task;
        if (task.send == 0) {
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!resDescription.getText().toString().trim().equals("")) {
                        taskHelper helper = new taskHelper(_context, true);
                        taskObject.setStatus(1);
                        taskObject.setResultDescription(resDescription.getText().toString());
                        helper.saveTask(taskObject);
                    }
                    dlg.dismiss();
                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

    }
}
