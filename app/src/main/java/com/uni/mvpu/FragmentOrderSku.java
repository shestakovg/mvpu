package com.uni.mvpu;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Adapters.orderSkuAdapter;
import Entitys.OrderExtra;
import Entitys.OutletObject;
import Entitys.orderControlParams;
import Entitys.orderSku;
import core.appManager;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IOrder;
import interfaces.IOrderTotal;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class FragmentOrderSku extends Fragment implements IOrderTotal{
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
        ((Button) parentView.findViewById(R.id.btnOrderDisplayAllRows)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fillSku("");
                    }
                }
        );

        ((Button) parentView.findViewById(R.id.btnOrderParamsDialog)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((IOrder) getActivity()).showOrderParams();
                    }
                }
        );
        return parentView;
    }

    public void fillSku( String skuGroup)
    {
        String orderByClause = "s.SkuName";
        String whereClause = "where s.skuParentId = ?";
        String joinKind = "left";
        String[] params= new String[] {Integer.toString(((IOrder) getActivity()).getOrderExtra()._id),
                skuGroup};
        if (skuGroup.isEmpty()) {
            orderByClause="od._id";
            joinKind="inner";
            whereClause=" where od.qty1>0 or od.qty2>0 ";
            params= new String[] {Integer.toString(((IOrder) getActivity()).getOrderExtra()._id)};
        }

        skuList = new ArrayList<>();
        final OutletObject locOutlet = ((IOrder) getActivity()).getOutletObject();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select  s.SkuId, s.SkuName, st.StockG, st.StockR,COALESCE(pOrder.Pric, COALESCE( p.Pric,0)) pric, COALESCE(od.qty1, 0) as QtyMWH, " +
                        "  COALESCE(od.qty2, 0) as QtyRWH, case when od.skuId is null then 0 else 1 end existPosition, od._id as detailId , s.QtyPack," +
                        " coalesce(od.PriceId,'"+locOutlet.priceId.toString()+"') PriceId,  "+" pn.PriceName  from sku as s" +
                        "            left join  stock st on s.skuId = st.skuId  " +
                        "            left join price p on s.skuId = p.skuId and p.PriceId = '" +locOutlet.priceId.toString()+"' "+
                        joinKind + " join orderDetail od on od.skuId= s.skuId and od.headerId = ?  "+
                        " left join price pOrder on s.skuId = pOrder.skuId and pOrder.PriceId = od.PriceId "+
                        " left join PriceNames pn on pn.PriceId = coalesce(od.PriceId,'"+locOutlet.priceId.toString()+"') "+
                        whereClause + "   order by " + orderByClause, params
                );
        //, wputils.getDateTime(orderDate)
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++)
        {
            boolean exist = false;
            if (cursor.getInt(cursor.getColumnIndex("existPosition"))==1) exist = true;
            orderSku sku =  new orderSku(cursor.getString(cursor.getColumnIndex("SkuName")), exist);
            sku.skuId = cursor.getString(cursor.getColumnIndex("SkuId"));
            sku.stockG = cursor.getDouble(cursor.getColumnIndex("StockG"));
            sku.stockR = cursor.getDouble(cursor.getColumnIndex("StockR"));
            sku.price = cursor.getDouble(cursor.getColumnIndex("pric"));
            sku.orderUUID  = ((IOrder) getActivity()).getOrderExtra().orderUUID;
            sku.headerId = ((IOrder) getActivity()).getOrderExtra()._id;
            sku.setQtyMWH(cursor.getInt(cursor.getColumnIndex("QtyMWH")));
            sku.setQtyRWH(cursor.getInt(cursor.getColumnIndex("QtyRWH")));
            sku._id = cursor.getLong(cursor.getColumnIndex("detailId"));
            sku.setCountInBox(cursor.getInt(cursor.getColumnIndex("QtyPack")));
            sku.priceId = cursor.getString(cursor.getColumnIndex("PriceId"));
            sku.priceName = cursor.getString(cursor.getColumnIndex("PriceName"));
            skuList.add(sku);
            cursor.moveToNext();
        }
        db.close();

        skuAdapter = new orderSkuAdapter(parentView.getContext(), skuList,((IOrder) getActivity()).getOutletObject(),((IOrder) getActivity()).getOrderExtra(), this);
        lvSku.setAdapter(skuAdapter);
        displayTotal();
    }

    public orderControlParams displayTotal()
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        double orderSum =0;
        int rowCount =0;
        String query="select sum(coalesce(d.qty1,0) * coalesce(p.pric,0) + coalesce(d.qty2,0) * coalesce(p.pric,0)) as orderSumma," +
                " count(d._id) rowCount from orderHeader h " +
                " inner join orderDetail d on d.headerid= h._id" +
                " left join price p on d.skuId = p.skuId and p.priceId = d.priceId " +
                " where h._id = ? and (coalesce(d.qty1,0)>0 or coalesce(d.qty2,0)>0)";
        Cursor cursor = db.rawQuery(query, new String[] {Integer.toString(((IOrder) getActivity()).getOrderExtra()._id)});
                //((IOrder) getActivity()).getOutletObject().priceId.toString()});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            orderSum = cursor.getDouble(0);
            rowCount = cursor.getInt(1);
            cursor.moveToNext();
        }
        db.close();
        ((TextView) getView().findViewById(R.id.tvOrderSum)).setText(String.format("%.2f", orderSum));
        ((TextView) getView().findViewById(R.id.tvOrderRowCount)).setText(Integer.toString(rowCount));
        return new orderControlParams(rowCount, orderSum, appManager.getOurInstance().appSetupInstance);
    }

    @Override
    public Boolean allowCloseOrder() {
        return null;
    }



}

