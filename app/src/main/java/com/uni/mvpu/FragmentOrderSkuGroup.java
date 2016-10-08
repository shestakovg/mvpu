package com.uni.mvpu;

import android.app.Fragment;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Adapters.orderSkuGroupAdapter;
import Entitys.OutletObject;
import Entitys.groupSku;
import core.appManager;
import db.DbOpenHelper;
import interfaces.IOrder;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class FragmentOrderSkuGroup extends Fragment {
    private View parentView;
    private Button btnUpGroup;
    private TextView textCurrentGroup;
    private ListView lvGroup;

    private ArrayList<groupSku> skuGroupStack;
    //private SimpleAdapter saGroupSku;
    private orderSkuGroupAdapter groupAdapter;
    private List<groupSku> groupSkuList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_skugroup, null);
        btnUpGroup = (Button) parentView.findViewById(R.id.btnGroupUp);
        btnUpGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onBtnGroupUp(v);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
//        textCurrentGroup = (TextView) parentView.findViewById(R.id.tetxViewSelectedGroup);
//        textCurrentGroup.setText("");
        btnUpGroup.setText("");
        lvGroup = (ListView)  parentView.findViewById(R.id.listViewSku);

        skuGroupStack = new ArrayList<>();
        //listRoute.setItemsCanFocus(false);
        lvGroup.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (skuGroupStack.size()==0)
            skuGroupStack.add(
                    new groupSku(new UUID(0L, 0L).toString(), new UUID(0L, 0L).toString(), "", 0 , "#FFA29A9A", 0)
            );
        //fillListViewGroupSku();
        lvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                           @Override
                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                               try {
                                                   onItemClickGroup(parent, view, position, id);
                                               } catch (ParseException e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }

        );

        return parentView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("SELECTED_LEVEL", groupSkuList.size());
        outState.putString("LAST_LEVEL",groupSkuList.get(groupSkuList.size()-1).getParentId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void upToRootGroup() throws ParseException {
        skuGroupStack.clear();
        skuGroupStack.add(
                new groupSku(new UUID(0L, 0L).toString(), new UUID(0L, 0L).toString(), "", 0 , "#FFA29A9A", 0)
        );
        fillListViewGroupSku();
    }

    public void fillListViewGroupSku() throws ParseException {


//        saGroupSku = new SimpleAdapter(parentView.getContext(), fillGroupList(skuGroupStack.get(skuGroupStack.size()-1)), android.R.layout.simple_expandable_list_item_2,
//                new String[] {"name", "descr"},
//                new int[] {android.R.id.text1, android.R.id.text2}
//        );
        groupAdapter = new orderSkuGroupAdapter(parentView.getContext(), fillGroupList(skuGroupStack.get(skuGroupStack.size()-1)));
        //lvGroup.setAdapter(saGroupSku);
        lvGroup.setAdapter(groupAdapter);
        int itemId = 0;
//        lvGroup.getChildAt(0).setBackgroundColor(Color.parseColor("#FF040B97"));
//        for (groupSku currentGroup : skuGroupStack)
//        {
//            View v =lvGroup.getChildAt(itemId);
//            if (v!=null)
//             v.setBackgroundColor(Color.parseColor(currentGroup.getColor()));
//
//            itemId++;
//        }

        IOrder actOrder = (IOrder) getActivity();
        actOrder.refreshSku( skuGroupStack.get(skuGroupStack.size() - 1).getGroupId());
    }

    private void groupUp() throws ParseException {
        if (skuGroupStack.size()>1)
            skuGroupStack.remove(skuGroupStack.size()-1);
        //textCurrentGroup.setText(skuGroupStack.get(skuGroupStack.size()-1).getGroupName());
        btnUpGroup.setText(skuGroupStack.get(skuGroupStack.size()-1).getGroupName());
        fillListViewGroupSku();
    }
    private /*List<Map<String, ? >>*/  List<groupSku> fillGroupList(groupSku level)
    {
        List<Map<String, ? >> items = new ArrayList<Map<String, ? >>() ;
        groupSkuList = new ArrayList<>();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(parentView.getContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String parentWhere = level.getGroupId();
        Cursor cursor = db.rawQuery("select g.GroupId , g.GroupName , g.GroupParentId, g.OutletCount, g.Color, sf.FactOutletCount from skuGroup g" +
                                    "  left join salesfact sf on sf.GroupId = g.GroupId " +
                                "where g.GroupParentId = '"+parentWhere+"' order by g.GroupName", null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            groupSku ob = new groupSku(cursor.getString(cursor.getColumnIndex("GroupId")),
                                        cursor.getString(cursor.getColumnIndex("GroupParentId")),
                                        cursor.getString(cursor.getColumnIndex("GroupName")),
                                        cursor.getInt(cursor.getColumnIndex("OutletCount")),
                                        cursor.getString(cursor.getColumnIndex("Color")),
                                        cursor.getInt(cursor.getColumnIndex("FactOutletCount"))
                                        );
            groupSkuList.add(ob);
//            Map<String, Object> map= new HashMap<String, Object>();
//            map.put("name", ob.getGroupName());
//            map.put("descr", (ob.getOutletCount() > 0 ? this.getText(R.string.StringPlan)+": "+ob.getOutletCount().toString() : ""));
//            items.add(map);

            cursor.moveToNext();
        }
        //return items;
        return  groupSkuList;

    }

    private void onItemClickGroup(AdapterView<?> parent, View view, int position, long id) throws ParseException {
        skuGroupStack.add(groupSkuList.get(position));
            //textCurrentGroup.setText(groupSkuList.get(position).getGroupName());
            btnUpGroup.setText(groupSkuList.get(position).getGroupName());
            fillListViewGroupSku();
    }

    private void onBtnGroupUp(View v) throws ParseException {
        groupUp();
    }
}
