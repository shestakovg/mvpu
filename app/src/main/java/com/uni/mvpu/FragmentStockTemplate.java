package com.uni.mvpu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import Adapters.orderSkuAdapter;
import Adapters.stockTemplateAdapter;
import Adapters.storecheckSkuAdapter;
import Entitys.OutletObject;
import Entitys.orderControlParams;
import Entitys.orderSku;
import core.AppSettings;
import core.wputils;
import db.DbOpenHelper;
import interfaces.IOrder;
import interfaces.IOrderTotal;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentStockTemplate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentStockTemplate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStockTemplate extends Fragment implements IOrderTotal {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View parentView;
    private ArrayList<orderSku> skuList;
    private stockTemplateAdapter skuAdapter;
    private ListView lvSku;
    private Button btnGenerateOrder;
    private Button btnAllList;
    private String headerId;
    private OnFragmentInteractionListener mListener;
    private TextView tvMessage;

    private Boolean orderHasBeenGenerated = false;

    public Boolean getOrderHasBeenGenerated() {
        return orderHasBeenGenerated;
    }

    public FragmentStockTemplate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStockTemplate.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStockTemplate newInstance(String param1, String param2) {
        FragmentStockTemplate fragment = new FragmentStockTemplate();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_stock_template, container, false);
        lvSku = (ListView) parentView.findViewById(R.id.listPrevOrder);
        btnGenerateOrder = (Button) parentView.findViewById(R.id.btnGenerateOrder);
        btnGenerateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Подтверждение");
                builder.setMessage("Сгенерировать новый заказ?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        generateOrder();
                        getActivity().onBackPressed();
                    }
                });

                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        btnAllList = (Button) parentView.findViewById(R.id.btnAllList);
        btnAllList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fillSku("");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        headerId = ((IOrder) getActivity()).getOrderExtra().orderUUID;
        tvMessage = (TextView)   parentView.findViewById(R.id.txtStockTemlateMessage);
        try {
            fillSku("");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public orderControlParams displayTotal() {
        if (skuList == null) {
            return null;
        }
        int outOfStockQty = 0;
        for (orderSku sku : skuList) {
            if (!sku.AvailiableInStore) {
                outOfStockQty++;
            }
        }
        String message = "Нет "+Integer.toString(outOfStockQty)+" позиций из "+skuList.size();
        tvMessage.setText(message);
        return null;
    }

    @Override
    public Boolean allowCloseOrder() {
        return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void fillSku( String skuGroup) throws ParseException {
        String orderByClause = "s.SkuName";
        String whereClause = "where s.skuParentId = ?";
        String joinKind = "left";
        String[] params= new String[] {  skuGroup};
        if (skuGroup.isEmpty()) {
            orderByClause=" s.skuParentId, s.SkuName";
            joinKind=" inner ";
            whereClause="";
            params= new String[] {};
        } else {
            whereClause = " where s.skuParentId = ? ";
        }

        skuList = new ArrayList<>();
        final OutletObject locOutlet = ((IOrder) getActivity()).getOutletObject();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String sqlStatement=
                "select s.SkuId, s.SkuName,coalesce(st.StockG, 0) StockG,coalesce(st.StockR, 0) StockR, coalesce(ccs.LastDate,'') as PreviousOrderDate,coalesce(ccs.Qty ,0) as PreviousOrderQty, COALESCE(od.availableInStore,1) availableInStore, " +
                        " ccs.Warehouse, od._id as detailId, case when od.skuId is null then 0 else 1 end existPosition "+
                        "  from sku as s"+
                        "  left join  stock st on s.skuId = st.skuId  " +
                        " inner join ClientCardSku ccs on ccs.outletId= '"+ ((IOrder) getActivity()).getOrderExtra().outletId+"'  and s.skuid = ccs.SkuId "+
                        " left join orderDetail od on od.skuId= s.skuId  and od.headerId =   "+
                        Integer.toString(((IOrder) getActivity()).getOrderExtra()._id)+
                        whereClause+
                        "   order by " + orderByClause;
        Cursor cursor = db.rawQuery(sqlStatement, params);
        cursor.moveToFirst();
        int _id = ((IOrder) getActivity()).getOrderExtra()._id;
        for (int i=0; i<cursor.getCount(); i++) {
            boolean exist = false;
            String lastDate = cursor.getString(cursor.getColumnIndex("PreviousOrderDate"));
            Calendar calendarLastDate = wputils.getDateFromString(lastDate);
            Calendar currentDate = wputils.getCurrentDate();
            //only last 3 month
            if ( wputils.getTimeDifferenceInMonth(calendarLastDate, currentDate)<= 3 ) {
                if (cursor.getInt(cursor.getColumnIndex("existPosition")) == 1) exist = true;
                orderSku sku = new orderSku(cursor.getString(cursor.getColumnIndex("SkuName")), exist);
                sku.skuId = cursor.getString(cursor.getColumnIndex("SkuId"));
                sku.orderUUID = headerId;
                sku.headerId = _id;
                sku.stockG = cursor.getDouble(cursor.getColumnIndex("StockG"));
                sku.stockR = cursor.getDouble(cursor.getColumnIndex("StockR"));
                sku.PreviousOrderQty = cursor.getInt(cursor.getColumnIndex("PreviousOrderQty"));
                sku.PreviousOrderDate = cursor.getString(cursor.getColumnIndex("PreviousOrderDate"));
                sku.AvailiableInStore = cursor.getInt(cursor.getColumnIndex("availableInStore")) == 1;
                sku.PreviousWarehouse = cursor.getInt(cursor.getColumnIndex("Warehouse"));
                sku.priceId = locOutlet.priceId.toString();
                sku._id = cursor.getLong(cursor.getColumnIndex("detailId"));
                if ( (sku.stockG + sku.stockR) > 0) {
                    skuList.add(sku);
                }
            }
            cursor.moveToNext();
        }

        db.close();

        skuAdapter = new stockTemplateAdapter(parentView.getContext(),
                skuList,((IOrder) getActivity()).getOutletObject(),
                ((IOrder) getActivity()).getOrderExtra(),
                this
                );
        lvSku.setAdapter(skuAdapter);
        displayTotal();
    }

    private void generateOrder() {
        if (this.orderHasBeenGenerated) {
            Toast.makeText(getActivity(), "Автозаказ уже сформирован",Toast.LENGTH_SHORT).show();
            return;
        }
        Boolean atLeastOneLineExist = false;
        for (orderSku sku : skuList) {
            Double  orderQTY= sku.PreviousOrderQty * 1.0;
            if (!sku.AvailiableInStore && (sku.stockG + sku.stockR) >= orderQTY) {
                if (sku.PreviousWarehouse != 2) {
                    sku.setQtyMWH(orderQTY.intValue());
                } else {
                    sku.setQtyRWH(orderQTY.intValue());
                }
                sku.saveDb(getActivity(), AppSettings.ORDER_TYPE_ORDER);
                atLeastOneLineExist = true;
            }
        }
        if (atLeastOneLineExist) {
            DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            db.execSQL("update orderHeader set orderType = " + AppSettings.ORDER_TYPE_ORDER + " where orderUUID = ?", new String[]{((IOrder) getActivity()).getOrderExtra().orderUUID});
            db.close();
        }
        this.orderHasBeenGenerated = true;
    }
}
