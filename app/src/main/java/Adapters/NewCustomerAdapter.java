package Adapters;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uni.mvpu.ActivityEditNewCustomer;
import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Date;

import Entitys.NewCustomer;
import core.wputils;
import db.DbOpenHelper;
import sync.sendNewCustomer;

/**
 * Created by shest on 11/13/2016.
 */

public class NewCustomerAdapter extends BaseAdapter {
    Context context;
    NewCustomerAdapter currentAdapter;
    LayoutInflater lInflater;
    ArrayList<NewCustomer> customerList = new ArrayList<NewCustomer>() ;

    public NewCustomerAdapter(Context context) {
        this.context = context;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.customerList = NewCustomerAdapter.GetDataFromDB(this.context, -1);
        currentAdapter = this;
    }

    public static ArrayList<NewCustomer>  GetDataFromDB(Context context, int defaultId)
    {
        ArrayList<NewCustomer> customerList = new ArrayList<NewCustomer>();
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select h._id, (strftime('%s', h.RegistrationDate) * 1000) RegistrationDate , h.Territory , da.Description as TerritoryName," +
                    " h.RouteId , h.CustomerName , " +
                    " h.DeliveryAddress , h.OutletCategoty, oc.categoryName OutletCategotyName,  h.PriceType , pn.PriceName as PriceName," +
                    " h.VisitDay , dw.dayName as VisitDayName, " +
                    " h.DeliveryDay , dw1.dayName as DeliveryDayName,  h.Manager1Name , h.Manager1Phone , h.Manager2Name , h.Manager2Phone , h.AdditionalInfo , h._send  " +
                    " from NewCustomers h " +
                    " left join DeliveryArea da on da.idRef = h.Territory" +
                    " left join OutletCategoryes oc on oc.categoryOrder = h.OutletCategoty " +
                    " left join dayOfWeek dw on dw.dayOrder = h.VisitDay " +
                    " left join dayOfWeek dw1 on dw1.dayOrder =  h.DeliveryDay " +
                    " left join PriceNames pn on pn.PriceId = h.PriceType " +
                    (defaultId>=0 ? " where h._id = "+defaultId : " ")+
                    "  order by h._id desc", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            NewCustomer nc = new NewCustomer(context);
            nc.setCustomerName(cursor.getString(cursor.getColumnIndex("CustomerName")));
            nc.setDeliveryAddress(cursor.getString(cursor.getColumnIndex("DeliveryAddress")));
            nc.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            nc.setRegistrationDate(wputils.getCalendarFromDate(new Date(cursor.getLong(cursor.getColumnIndex("RegistrationDate")))));

            nc.setTerritoryName(cursor.getString(cursor.getColumnIndex("TerritoryName")));
            nc.setTerritoryId(cursor.getString(cursor.getColumnIndex("Territory")));

            nc.setRouteId(cursor.getString(cursor.getColumnIndex("RouteId")));

            nc.setOutletCategotyId(cursor.getInt(cursor.getColumnIndex("OutletCategoty")));
            nc.setOutletCategotyName(cursor.getString(cursor.getColumnIndex("OutletCategotyName")));

            nc.setPriceTypeName(cursor.getString(cursor.getColumnIndex("PriceName")));
            nc.setPriceTypeId(cursor.getString(cursor.getColumnIndex("PriceType")));

            nc.setVisitDayId(cursor.getInt(cursor.getColumnIndex("VisitDay")));
            nc.setVisitDayName(cursor.getString(cursor.getColumnIndex("VisitDayName")));

            nc.setDeliveryDayId(cursor.getInt(cursor.getColumnIndex("DeliveryDay")));
            nc.setDeliveryDayName(cursor.getString(cursor.getColumnIndex("DeliveryDayName")));

            nc.setManager1Name(cursor.getString(cursor.getColumnIndex("Manager1Name")));
            nc.setManager2Name(cursor.getString(cursor.getColumnIndex("Manager2Name")));
            nc.setManager1Phone(cursor.getString(cursor.getColumnIndex("Manager1Phone")));
            nc.setManager2Phone(cursor.getString(cursor.getColumnIndex("Manager2Phone")));

            nc.setAdditionalInfo(cursor.getString(cursor.getColumnIndex("AdditionalInfo")));

            nc.setSend((cursor.getInt(cursor.getColumnIndex("_send")) == 0 ? false : true));

            customerList.add(nc);
            cursor.moveToNext();
        }
        db.close();
        return customerList;
    }

    @Override
    public int getCount() {
        return customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_newcustomer, parent, false);
        }
        NewCustomer customer =(NewCustomer) this.getItem(position);
        ((TextView) view.findViewById(R.id.tvNewCustomerName)).setText(customer.getCustomerName());
        ((TextView) view.findViewById(R.id.tvNewCustomerAddress)).setText(customer.getDeliveryAddress());
        ((TextView) view.findViewById(R.id.tvNewCustomerDate)).setText(wputils.getDateTimeString(customer.getRegistrationDate()));

        Button editBtn = (Button) view.findViewById(R.id.btnNewCustomerEdit);
        editBtn.setTag(position);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickBtnEdit(v);
            }
        });

        Button sendBtn = (Button) view.findViewById(R.id.btnNewCustomerSend);
        sendBtn.setTag(position);
        if (customer.isSend())
            sendBtn.setVisibility(View.GONE);
        else
        {
            sendBtn.setVisibility(View.VISIBLE);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Button) v).setEnabled(false);
                    int _id =(int) v.getTag();
                    sendNewCustomer snc = new sendNewCustomer((NewCustomer)  currentAdapter.getItem(_id), currentAdapter);
                    snc.execute();
                }
            });
        }

        Button sendedBtn = (Button) view.findViewById(R.id.btnNewCustomerSended);
        sendedBtn.setTag(position);
        if (!customer.isSend())
            sendedBtn.setVisibility(View.GONE);
        else
            sendedBtn.setVisibility(View.VISIBLE);
        return view;
    }

    private void OnClickBtnEdit(View v)
    {
        int _id =(int) v.getTag();
       // Toast.makeText(context,"Id = " + _id , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ActivityEditNewCustomer.class);
        intent.putExtra("newCustomer",((NewCustomer)  currentAdapter.getItem(_id)).getId());
        context.startActivity(intent);
    }
}
