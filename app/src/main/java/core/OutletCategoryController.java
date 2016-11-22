package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import Entitys.DeliveryArea;
import Entitys.OutletCategory;
import Entitys.priceTypeObject;
import db.DbOpenHelper;

/**
 * Created by shest on 11/17/2016.
 */

public class OutletCategoryController {
    public static final String DEFAULT_VALUE = "” ¿∆»“≈ “»œ!!!";

    private Context context;
    private ArrayList<OutletCategory> lstOutletCategory = new ArrayList<OutletCategory>();


    public OutletCategoryController(Context context) {
        this.context = context;
        fillList();
    }

    private void fillList()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select categoryOrder, categoryName from OutletCategoryes", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            lstOutletCategory.add(
                    new OutletCategory(
                            cursor.getInt(0),
                            cursor.getString(1)
                    )
            );
            cursor.moveToNext();
        }
        lstOutletCategory.add(new OutletCategory(-1,OutletCategoryController.DEFAULT_VALUE));
        db.close();
    }

    public String[] getNames()
    {
        String[] res = new String[lstOutletCategory.size()];
        int i = 0;
        for (OutletCategory ar : lstOutletCategory)
        {
            res[i++] = ar.getCategoryName();
        }
        return res;
    }

    public int GetDefaultMemeberId()
    {
        return lstOutletCategory.size()-1;
    }

    public OutletCategory getItem(int index)
    {
        return lstOutletCategory.get(index);
    }
}
