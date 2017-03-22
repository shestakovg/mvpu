package com.uni.mvpu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import Adapters.NoResultReasonsAdapder;
import Entitys.NoResultReason;
import Entitys.Order;
import core.wputils;
import db.DbOpenHelper;

public class ActivityNoResult extends AppCompatActivity {
    private String outletId;
    private String outletName;
    private Calendar routeDate;
    private NoResultReasonsAdapder adapter;

    private ListView listReasons;
    private TextView tvOutletName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_result);

        //Bundle data = getIntent().getExtras();

        Intent intent = getIntent();
        routeDate = (Calendar) intent.getSerializableExtra("routeDate");
        outletId = intent.getStringExtra("outletId");
        outletName = intent.getStringExtra("outletName");

        listReasons = (ListView) findViewById(R.id.lvReasons);

        tvOutletName = (TextView)  findViewById(R.id.tvOutletName);
        tvOutletName.setText(outletName);

        adapter = new NoResultReasonsAdapder(this,  getReasons(), outletId, routeDate);
        listReasons.setAdapter(adapter);
    }

    private ArrayList<NoResultReason> getReasons()
    {
        ArrayList<NoResultReason> list = new ArrayList<NoResultReason>();

        DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select r._id reasonId, r.description, COALESCE(rs.reasonId, -1) reasonVal from no_result_reasons r" +
                                    " left join No_result_storage rs on r._id = rs.reasonId and  DATETIME(rs.Date) = ? and rs.outletid= ?",
                new String[] { wputils.getDateTime(routeDate), outletId} );
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
             list.add(
                     new NoResultReason(
                            cursor.getInt(0),
                            cursor.getString(1),
                            (cursor.getInt(2) >=0 ? true :false)
                    )
             );
            cursor.moveToNext();
        }
        db.close();
        return list;
    }
}
