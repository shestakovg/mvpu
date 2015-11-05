package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import Entitys.priceTypeObject;
import db.DbOpenHelper;

/**
 * Created by shestakov.g on 04.11.2015.
 */
public class priceTypeManager {
    private static priceTypeManager ourInstance = new priceTypeManager();

    public static priceTypeManager getInstance()
    {
        return ourInstance;
    }

    public ArrayList<priceTypeObject> getPriceList() {
        return priceList;
    }

    private ArrayList<priceTypeObject> priceList = new ArrayList<priceTypeObject>();

    private priceTypeManager() {
        fillPriceTypes();
    }


    private  void fillPriceTypes()
    {
        this.priceList.add(new priceTypeObject("75a9d60f-cd75-11e4-826a-240a64c9314e", "Вид 1"));
        this.priceList.add(new priceTypeObject("75a9d611-cd75-11e4-826a-240a64c9314e", "Вид 3"));
        this.priceList.add(new priceTypeObject("75a9d613-cd75-11e4-826a-240a64c9314e", "Вид 5"));
    }

    public int getIdexByPriceName(String priceName)
    {
        int result = -1;
        for (priceTypeObject curPrice:priceList) {
            result++;
            if (curPrice.getPriceName().equals(priceName))
            {
                return result;
            }
        }
        return result;
    }

    public String[] getPriceNameArray(String priceName)
    {
        ArrayList<String> result = new ArrayList<String>();
        int i =0;
        boolean priceNameExist = false;
        for (priceTypeObject curPrice:priceList) {
            if (!priceNameExist)
                priceNameExist = curPrice.getPriceName().equals(priceName);
            result.add(curPrice.getPriceName());
        }
        if (!priceNameExist) result.add(priceName);

        String[] stringArray = new String[result.size()]; // to get same size array
        result.toArray(stringArray);
        return stringArray;
    }

    public String getPriceIdByPriceName(String priceName, String defaultValue)
    {
        for (priceTypeObject curPrice:priceList) {
            if (curPrice.getPriceName().equals(priceName))
            {
                return curPrice.getPriceType();
            }
        }
        return defaultValue;
    }

    public double getPriceValue(String skuId, String priceId)
    {
        Context context =appManager.getOurInstance().getCurrentContext();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select Pric from price where SkuId=? and  PriceId= ?", new String[]{skuId,priceId });
        cursor.moveToFirst();
        //price (SkuId  text, PriceId text, Pric double)
        double result = 0;
        for (int i = 0; i < cursor.getCount(); i++)
        {
            result = cursor.getDouble(0);
        }
        return result;
    }
}
