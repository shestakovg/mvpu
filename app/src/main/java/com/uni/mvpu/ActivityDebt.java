package com.uni.mvpu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Adapters.debtListAdapter;
import Adapters.orderListAdapter;
import Entitys.DebtData;
import db.DbOpenHelper;


public class ActivityDebt extends ActionBarActivity {
    private String customerid;
    private ListView lvDebt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Долги");
        setContentView(R.layout.activity_debt);
        customerid = getIntent().getStringExtra("customerid");
        lvDebt = (ListView) findViewById(R.id.lvDebtDataList);
        fillDebtList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_debt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillDebtList()
    {
        ArrayList<DebtData> debts = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select  d.transactionNumber  , d.transactionDate  , d.transactionSum  , "  +
                " d.paymentDate  , d.debt  , d.overdueDebt  , d.overdueDays  from debts d where d.customerId= ?"
                    , new String[] {customerid});
        cursor.moveToFirst();
        for (int i=0; i < cursor.getCount(); i++ )
        {
            DebtData debtData = new DebtData();
            debtData.debt = cursor.getDouble(cursor.getColumnIndex("debt"));
            debtData.overdueDebt = cursor.getDouble(cursor.getColumnIndex("overdueDebt"));
            debtData.transactionSum = cursor.getDouble(cursor.getColumnIndex("transactionSum"));
            debtData.overdueDays = cursor.getInt(cursor.getColumnIndex("overdueDays"));
            debtData.transactionNumber = cursor.getString(cursor.getColumnIndex("transactionNumber"));
            debtData.transactionDate = cursor.getString(cursor.getColumnIndex("transactionDate"));
            debtData.paymentDate = cursor.getString(cursor.getColumnIndex("paymentDate"));
            debts.add(debtData);
            cursor.moveToNext();
        }
        db.close();
        debtListAdapter debtAdapter = new debtListAdapter(this, debts);
        lvDebt.setAdapter(debtAdapter);

        ((TextView) findViewById(R.id.tvDeptTotal)).setText(String.format("%.2f", DebtData.getDebtSum(debts)));
        ((TextView) findViewById(R.id.tvOverdueDeptTotal)).setText(String.format("%.2f", DebtData.getOverdueDebtSum(debts)));
    }
}
