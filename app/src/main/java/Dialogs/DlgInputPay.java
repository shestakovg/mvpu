package Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.R;

import core.ResultObject;
import core.TouchActivity;
import interfaces.IInputCustomerPay;

/**
 * Created by shestakov.g on 29.06.2015.
 */
public class DlgInputPay extends Dialog {
    private String customerName;
    private IInputCustomerPay inputCustomerPay;
    private EditText edPaySum;

    public DlgInputPay(Context context, String customerName,  final IInputCustomerPay inputCustomerPay) {
        super(context);
        final Context _context = context;
        this.customerName = customerName;
        this.inputCustomerPay = inputCustomerPay;
        this.setTitle("¬ведите сумму платежа");
        this.setContentView(R.layout.dialog_input_pay);
        this.setCancelable(true);
        edPaySum = (EditText) this.findViewById(R.id.edPaySum);
        ((TextView) this.findViewById(R.id.tvDlgPayCustomer)).setText(customerName);
        //edPaySum.setText("0");
        final Dialog dlg = this;
        Button btnCancel =(Button) this.findViewById(R.id.btnInputPayCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        Button btnOK =(Button) this.findViewById(R.id.btnInputPayOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultObject checkResult = inputCustomerPay.checkAllowInputPay(Double.valueOf(edPaySum.getText().toString()));
                if (checkResult.isResult()) {
                    inputCustomerPay.processPay(Double.valueOf(edPaySum.getText().toString()));
                }
                else
                {
                    Toast.makeText(_context, checkResult.getResultMessage(), Toast.LENGTH_LONG).show();
                }
                dlg.dismiss();
            }
        });
    }
}
