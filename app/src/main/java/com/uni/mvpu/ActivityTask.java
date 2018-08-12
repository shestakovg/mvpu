package com.uni.mvpu;

import android.os.Bundle;
import android.widget.ListView;

import java.io.IOException;
import java.util.UUID;

import Adapters.taskAdapter;
import Entitys.OutletObject;
import core.TouchActivity;

public class ActivityTask extends TouchActivity {
    private OutletObject currentOutlet;
    ListView lvMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Задачи");
        setContentView(R.layout.activity_task);
        currentOutlet = OutletObject.getInstance(UUID.fromString(getIntent().getStringExtra("OUTLETID")), this);
        lvMain = (ListView) findViewById(R.id.lvTaskList);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Toast.makeText(this,"onPostResume", Toast.LENGTH_SHORT).show();
        try {
            fillListList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void fillListList() throws IOException {
        lvMain.setAdapter(new taskAdapter(this, currentOutlet));
    }
}
