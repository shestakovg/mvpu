package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import Entitys.DeliveryArea;
import Entitys.priceTypeObject;
import db.DbOpenHelper;

/**
 * Created by shest on 11/17/2016.
 */

public class PriceTypeController {
    public static final String DEFAULT_VALUE = "” ¿∆»“≈ “»œ ÷≈Õ€!!!";
    private Context context;
    private ArrayList<priceTypeObject> lstPriceTypes = new ArrayList<priceTypeObject>();

    public PriceTypeController(Context context) {
        this.context = context;
        fillList();

    }

    private void fillList()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select PriceId , PriceName from PriceNames", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            lstPriceTypes.add(
                    new priceTypeObject(
                            cursor.getString(0),
                            cursor.getString(1)
                    )
            );
            cursor.moveToNext();
        }
        lstPriceTypes.add(new priceTypeObject(UUID.randomUUID().toString(),PriceTypeController.DEFAULT_VALUE));
        db.close();
    }

    public int GetDefaultMemeberId()
    {
        return lstPriceTypes.size()-1;
    }

    public priceTypeObject getItem(int index)
    {
        return lstPriceTypes.get(index);
    }

    public String[] getNames()
    {
        String[] res = new String[lstPriceTypes.size()];
        int i = 0;
        for (priceTypeObject ar : lstPriceTypes)
        {
            res[i++] = ar.getPriceName();
        }
        return res;
    }
}
