package com.uni.mvpu;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;

import Adapters.orderSkuAdapter;
import Adapters.storecheckSkuAdapter;
import Entitys.OutletObject;
import Entitys.orderControlParams;
import Entitys.orderSku;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IOrder;
import interfaces.IOrderTotal;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmenStorecheckSku.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmenStorecheckSku#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmenStorecheckSku extends Fragment implements IOrderTotal {
    private View parentView;

    private ArrayList<orderSku> skuList;
    private storecheckSkuAdapter skuAdapter;
    private ListView lvSku;
    private Button btnAllStorecheck;
//    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
//    public static FragmenStorecheckSku newInstance(String param1, String param2) {
//        FragmenStorecheckSku fragment = new FragmenStorecheckSku();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public FragmenStorecheckSku() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragmen_storecheck_sku, container, false);
        lvSku = (ListView) parentView.findViewById(R.id.listViewSkuStorecheck);
        btnAllStorecheck = (Button) parentView.findViewById(R.id.btnStorechekDisplayAllRows);
        btnAllStorecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCkickShowAll(v);
            }
        });
        fillSku("");
        return parentView;

    }

    // TODO: Rename method, update argument and hook method into UI event


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public orderControlParams displayTotal() {
        return null;
    }

    @Override
    public Boolean allowCloseOrder() {
        return null;
    }

    public void fillSku( String skuGroup)  {
        String orderByClause = "s.SkuName";
        String whereClause = "where s.skuParentId = ?";
        String joinKind = "left";
        String[] params= new String[] {  skuGroup};
        if (skuGroup.isEmpty()) {
            orderByClause=" s.skuParentId, s.SkuName";
            joinKind="inner";
            whereClause="";//" where od.qty1>0 or od.qty2>0 ";
            //params= new String[] {Integer.toString(((IOrder) getActivity()).getOrderExtra()._id)};
            params= new String[] {};
        }

        skuList = new ArrayList<>();
        final OutletObject locOutlet = ((IOrder) getActivity()).getOutletObject();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String sqlStatement=//"select  s.SkuId, s.SkuName, st.StockG, st.StockR,COALESCE(pOrder.Pric, COALESCE( p.Pric,0)) pric, COALESCE(od.qty1, 0) as QtyMWH, " +
                "select s.SkuId, s.SkuName,COALESCE(od.qty1, 0) as QtyMWH, case when od.skuId is null then 0 else 1 end existPosition," +
                        " od._id as detailId, DATETIME(od.finalDate) finalDate from sku as s"+// , s.QtyPack" +
                        //" coalesce(od.PriceId,'"+locOutlet.priceId.toString()+"') PriceId,  "+" pn.PriceName,"+
                        //" s.CheckCountInBox, case when sf.skuId is null then 0 else 1 end OnlyFact  from sku as s" +
                        //"            left join  stock st on s.skuId = st.skuId  " +
                        //"            left join price p on s.skuId = p.skuId and p.PriceId = '" +locOutlet.priceId.toString()+"' "+
                        " inner join orderDetail od on od.skuId= s.skuId  and od.headerId =   "+
                        Integer.toString(((IOrder) getActivity()).getOrderExtra()._id)+"  "
                        //" left join price pOrder on s.skuId = pOrder.skuId and pOrder.PriceId = od.PriceId "+
                        //" left join PriceNames pn on pn.PriceId = coalesce(od.PriceId,'"+locOutlet.priceId.toString()+"') "+
                        //" left join skuFact sf on sf.skuId =s.skuId and sf.priceId = coalesce(od.PriceId,'"+locOutlet.priceId.toString()+"') "+
                        + whereClause + "   order by " + orderByClause;
        Cursor cursor = db.rawQuery(sqlStatement, params);
        //, wputils.getDateTime(orderDate)
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++)
        {
            boolean exist = false;
            if (cursor.getInt(cursor.getColumnIndex("existPosition"))==1) exist = true;
            orderSku sku =  new orderSku(cursor.getString(cursor.getColumnIndex("SkuName")), exist);
            sku.skuId = cursor.getString(cursor.getColumnIndex("SkuId"));
//            sku.stockG = cursor.getDouble(cursor.getColumnIndex("StockG"));
//            sku.stockR = cursor.getDouble(cursor.getColumnIndex("StockR"));
//            sku.price = cursor.getDouble(cursor.getColumnIndex("pric"));
            sku.orderUUID  = ((IOrder) getActivity()).getOrderExtra().orderUUID;
            sku.headerId = ((IOrder) getActivity()).getOrderExtra()._id;
            sku.setQtyMWH(cursor.getInt(cursor.getColumnIndex("QtyMWH")));

//            sku.setQtyRWH(cursor.getInt(cursor.getColumnIndex("QtyRWH")));
            sku._id = cursor.getLong(cursor.getColumnIndex("detailId"));
            if (!cursor.isNull(cursor.getColumnIndex("finalDate")))
            {
                sku.finalDateExists = true;
                try {
                    sku.finalDate =  //wputils.getCalendarFromDate(wputils.LoadDate(cursor, cursor.getColumnIndex("finalDate")));
                            wputils.getCalendarFromString(cursor.getString(cursor.getColumnIndex("finalDate")));
                }
                catch (ParseException e)
                {

                }
            }
//            sku.setCountInBox(cursor.getInt(cursor.getColumnIndex("QtyPack")));
//            sku.priceId = cursor.getString(cursor.getColumnIndex("PriceId"));
//            sku.priceName = cursor.getString(cursor.getColumnIndex("PriceName"));
//            sku.onlyFact = cursor.getInt(cursor.getColumnIndex("OnlyFact")) == 1;
//            sku.checkMultiplicity = cursor.getInt(cursor.getColumnIndex("CheckCountInBox")) == 1;
            skuList.add(sku);
            cursor.moveToNext();
        }
        db.close();

        skuAdapter = new storecheckSkuAdapter(parentView.getContext(),
                skuList,((IOrder) getActivity()).getOutletObject(),
                ((IOrder) getActivity()).getOrderExtra());
        lvSku.setAdapter(skuAdapter);

    }

    public void btnCkickShowAll(View v)
    {
        fillSku("");
    }
}

