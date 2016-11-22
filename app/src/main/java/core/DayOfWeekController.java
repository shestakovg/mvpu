package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import Entitys.OutletCategory;
import Entitys.VisitDay;
import db.DbOpenHelper;

/**
 * Created by shest on 11/17/2016.
 */

public class DayOfWeekController {
    public static final String DEFAULT_VALUE = "” ¿∆»“≈ ƒ≈Õ‹!!!";

    private Context context;

    public DayOfWeekController(Context context) {
        this.context = context;
        fillList();
    }

    private ArrayList<VisitDay> lstDayOfWeek = new ArrayList<VisitDay>();

    private void fillList()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select dayOrder, dayName from dayOfWeek", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            lstDayOfWeek.add(
                    new VisitDay(
                            cursor.getInt(0),
                            cursor.getString(1)
                    )
            );
            cursor.moveToNext();
        }
        lstDayOfWeek.add(new VisitDay(-1,DayOfWeekController.DEFAULT_VALUE));
        db.close();
    }

    public String[] getNames()
    {
        String[] res = new String[lstDayOfWeek.size()];
        int i = 0;
        for (VisitDay ar : lstDayOfWeek)
        {
            res[i++] = ar.getDayName();
        }
        return res;
    }

    public int GetDefaultMemeberId()
    {
        return lstDayOfWeek.size()-1;
    }

    public VisitDay getItem(int index)
    {
        return lstDayOfWeek.get(index);
    }
}
