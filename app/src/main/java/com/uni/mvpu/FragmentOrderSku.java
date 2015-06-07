package com.uni.mvpu;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import Adapters.orderSkuAdapter;
import Entitys.OrderExtra;
import Entitys.orderSku;
import core.wputils;
import db.DbOpenHelper;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class FragmentOrderSku extends Fragment {
    private View parentView;
    private ArrayList<orderSku> skuList;
    private orderSkuAdapter skuAdapter;
    private ListView lvSku;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_skuorder, null);
        lvSku = (ListView) parentView.findViewById(R.id.listViewSkuOrder);
        //fillSku();

        return parentView;
    }

    public void fillSku(OrderExtra orderExtra, String skuGroup)
    {
        skuList = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select s.SkuId, s.SkuName, st.StockG, st.StockR from sku as s" +
                        "            left join  stock st on s.skuId = st.skuId  " +
                        "where s.skuParentId = ?",
                new String[] {skuGroup});
        //, wputils.getDateTime(orderDate)
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++)
        {
            orderSku sku =  new orderSku(cursor.getString(cursor.getColumnIndex("SkuName")));
            sku.stockG = cursor.getDouble(cursor.getColumnIndex("StockG"));
            sku.stockR = cursor.getDouble(cursor.getColumnIndex("StockR"));
            skuList.add(sku);
            cursor.moveToNext();
        }
        db.close();
        skuAdapter = new orderSkuAdapter(parentView.getContext(), skuList);
        lvSku.setAdapter(skuAdapter);
    }
}
