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
import Entitys.OutletObject;
import Entitys.orderSku;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IOrder;

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
        final OutletObject locOutlet = ((IOrder) getActivity()).getOutletObject();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select s.SkuId, s.SkuName, st.StockG, st.StockR, COALESCE( p.Pric,0) pric, COALESCE(od.qty1, 0) as QtyMWH, " +
                        "  COALESCE(od.qty2, 0) as QtyRWH from sku as s" +
                        "            left join  stock st on s.skuId = st.skuId  " +
                        "            left join price p on s.skuId = p.skuId and p.PriceId = '" +locOutlet.priceId.toString()+"' "+
                        "            left join orderDetail od on od.skuId= s.skuId and od.headerId = ?  "+
                        "where s.skuParentId = ?   order by s.SkuName",
                new String[] {Integer.toString(((IOrder) getActivity()).getOrderExtra()._id),
                                    skuGroup});
        //, wputils.getDateTime(orderDate)
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++)
        {
            orderSku sku =  new orderSku(cursor.getString(cursor.getColumnIndex("SkuName")));
            sku.skuId = cursor.getString(cursor.getColumnIndex("SkuId"));
            sku.stockG = cursor.getDouble(cursor.getColumnIndex("StockG"));
            sku.stockR = cursor.getDouble(cursor.getColumnIndex("StockR"));
            sku.price = cursor.getDouble(cursor.getColumnIndex("pric"));
            sku.orderUUID  = ((IOrder) getActivity()).getOrderExtra().orderUUID;
            sku.headerId = ((IOrder) getActivity()).getOrderExtra()._id;
            sku.setQtyMWH(cursor.getInt(cursor.getColumnIndex("QtyMWH")));
            sku.setQtyRWH(cursor.getInt(cursor.getColumnIndex("QtyRWH")));
            skuList.add(sku);
            cursor.moveToNext();
        }
        db.close();

        skuAdapter = new orderSkuAdapter(parentView.getContext(), skuList,((IOrder) getActivity()).getOutletObject(),((IOrder) getActivity()).getOrderExtra());
        lvSku.setAdapter(skuAdapter);
    }
}
