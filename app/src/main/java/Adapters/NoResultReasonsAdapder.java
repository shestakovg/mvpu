package Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.uni.mvpu.R;

import java.util.ArrayList;
import java.util.Calendar;

import Entitys.NoResultReason;
import Entitys.OutletObject;
import core.wputils;
import db.DbOpenHelper;

/**
 * Created by shest on 3/19/2017.
 */

public class NoResultReasonsAdapder extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    ArrayList<NoResultReason> reasons;
    String outletId;
    Calendar routeDate;
    boolean rendering = true;

    public NoResultReasonsAdapder(Context context, ArrayList<NoResultReason> reasons, String outletId, Calendar routeDate) {
        this.context = context;
        this.reasons = reasons;
        this.lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.outletId = outletId;
        this.routeDate = routeDate;
    }


    @Override
    public int getCount() {
        return reasons.size();
    }

    @Override
    public Object getItem(int position) {
        return reasons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        rendering = true;
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.lv_item_noresult_reason, parent, false);
        }
        NoResultReason reason =(NoResultReason) getItem(position);
        CheckBox chb =(CheckBox) view.findViewById(R.id.chbReason);
        chb.setChecked(reason.isTrue());
        chb.setText(reason.getDescription());
        chb.setTag(position);

        chb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rendering) return;
                NoResultReason reason =(NoResultReason) getItem(((int) buttonView.getTag()));
                //Toast.makeText(context, reason.getDescription(), Toast.LENGTH_SHORT).show();
                saveReasonToDb(reason, isChecked);
            }
        });
        rendering = false;
        return view;
    }

    private void saveReasonToDb(NoResultReason reason,  boolean isChecked ) {
        if (rendering) return;
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (!isChecked) {
            db.execSQL("delete from No_result_storage where  DATETIME(Date) = ? and outletId = ? and reasonId = ?",new String[] { wputils.getDateTime(routeDate), outletId, String.valueOf(reason.getId())});
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put("outletid", outletId);
            values.put("reasonId", reason.getId());
            values.put("Date", wputils.getDateTime(routeDate));
            db.insert("No_result_storage", null, values);

        }
        db.close();
    }


}
