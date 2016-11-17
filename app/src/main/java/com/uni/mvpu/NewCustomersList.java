package com.uni.mvpu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import Adapters.NewCustomerAdapter;

public class NewCustomersList extends AppCompatActivity {
    private NewCustomerAdapter newCustomerAdapter;
    private ListView lvNewCustomersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customers_list);
        ((Button) this.findViewById(R.id.btnAddNewCustomer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCustomer(v);
            }
        });
        this.lvNewCustomersList = (ListView) findViewById(R.id.lvNewCustomerList);

        this.newCustomerAdapter = new NewCustomerAdapter(this);
        this.lvNewCustomersList.setAdapter(this.newCustomerAdapter);
    }

    public void createNewCustomer(View v)
    {

    }
}
