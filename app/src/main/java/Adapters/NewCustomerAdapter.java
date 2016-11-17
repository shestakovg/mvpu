package Adapters;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Date;

import Entitys.NewCustomer;
import core.wputils;
import db.DbOpenHelper;

/**
 * Created by shest on 11/13/2016.
 */

public class NewCustomerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<NewCustomer> customerList = new ArrayList<NewCustomer>() ;

    public NewCustomerAdapter(Context context) {
        this.context = context;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GetDataFromDB();
    }

    public void GetDataFromDB()
    {
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
                    " order by h.RegistrationDate ", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            NewCustomer nc = new NewCustomer(this.context);
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
        return view;
    }
}
