package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import Entitys.DeliveryArea;
import Entitys.OutletCategory;
import db.DbOpenHelper;

/**
 * Created by shest on 11/17/2016.
 */

public class DeliveryAreaController {
    public static final String DEFAULT_VALUE = "” ¿∆»“≈ “≈––»“Œ–»ﬁ!!!";
    private Context context;
    private ArrayList<DeliveryArea> lstDeliveryArea = new ArrayList<DeliveryArea>();

    public DeliveryAreaController(Context context) {
        this.context = context;
        fillList();
    }

    private void fillList()
    {
        DbOpenHelper dbOpenHelper=new DbOpenHelper(context);
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select idRef, Description from DeliveryArea", null);
        cursor.moveToFirst();

        for (int i=0;i<cursor.getCount();i++) {
            lstDeliveryArea.add(
                    new DeliveryArea(
                            cursor.getString(0),
                            cursor.getString(1)
                    )
            );
            cursor.moveToNext();
        }
        lstDeliveryArea.add(new DeliveryArea(UUID.randomUUID().toString(),DeliveryAreaController.DEFAULT_VALUE));
        db.close();
    }

    public String[] getDeliveryAreaNames()
    {
        String[] res = new String[lstDeliveryArea.size()];
        int i = 0;
        for (DeliveryArea ar : lstDeliveryArea)
        {
            res[i++] = ar.getDescription();
        }
        return res;
    }

    public int GetDefaultMemeberId()
    {
        return lstDeliveryArea.size()-1;
    }

    public DeliveryArea getItem(int index)
    {
        return lstDeliveryArea.get(index);
    }
}
