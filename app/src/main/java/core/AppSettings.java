package core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uni.mvpu.R;

import java.util.UUID;

import db.DbOpenHelper;
import interfaces.IOrderControlParams;

/**
 * Created by g.shestakov on 26.05.2015.
 */



public class AppSettings implements IOrderControlParams {

    private final String PARAM_SERVICE_URL = "SERVICE_URL";
    private final String PARAM_ROUTE_ID = "ROUTE_ID";
    private final String PARAM_ROUTE_NAME = "ROUTE_NAME";
    private final String PARAM_EMPLOYEE_ID = "EMPLOYEE_ID";
    private final String PARAM_EMPLOYEE_NAME = "EMPLOYEE_NAME";
    private final String PARAM_debtControl = "debtControl";
    private final String PARAM_allowOverdueSum = "allowOverdueSum";
    private final String PARAM_minOrderRowsQty = "skuQty";
    private final String PARAM_minOrderSum = "minOrderSum";

    public static final String PARAM_PRICEID_DEFAULT = "75a9d60f-cd75-11e4-826a-240a64c9314e";

    public String getServiceUrl() {
        return serviceUrl;
    }


    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    private String serviceUrl;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    private String routeName;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    private String employeeName;

    public UUID getRouteId() {
        return routeId;
    }

    public void setRouteId(UUID routeId) {
        this.routeId = routeId;
    }

    private UUID   routeId ;

    public UUID getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(UUID employeeID) {
        this.employeeID = employeeID;
    }

    private UUID employeeID;

    public boolean isDebtControl() {
        return debtControl;
    }

    public double getAllowOverdueSum() {
        return allowOverdueSum;
    }

    private boolean debtControl;
    private double  allowOverdueSum;

    private int minOrderRowsQty;
    private double minOrderSum;

    public int getMinOrderRowsQty() {
        return minOrderRowsQty;
    }

    public double getMinOrderSum() {
        return minOrderSum;
    }

    public String version;
    public AppSettings(String serviceUrl, String routeName, String employeeName, UUID routeId, UUID employeeID) {
        this.serviceUrl = serviceUrl;
        this.routeName = routeName;
        this.employeeName = employeeName;
        this.routeId = routeId;
        this.employeeID = employeeID;
    }

    public AppSettings() {
        this.serviceUrl = "http://25.106.227.22:8100";
        this.routeName =  "Выберите маршрут";
        this.routeId = UUID.randomUUID();
        this.employeeName = "Неизвестный сотрудник";
        this.employeeID = UUID.randomUUID();
      //  readSetup(null);
    }

    public void saveSetup(Context context)
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//        db.execSQL("delete from baseParams where ParamId='"+PARAM_SERVICE_URL+"'");
//        db.execSQL("delete from baseParams where ParamId='"+PARAM_ROUTE_ID+"'");
//        db.execSQL("delete from baseParams where ParamId='"+PARAM_ROUTE_NAME+"'");

        saveParamSetup(db, PARAM_SERVICE_URL, getServiceUrl().trim());
        saveParamSetup(db, PARAM_ROUTE_ID, routeId.toString());
        saveParamSetup(db, PARAM_ROUTE_NAME, getRouteName());
        saveParamSetup(db, PARAM_EMPLOYEE_ID, employeeID.toString());
        saveParamSetup(db, PARAM_EMPLOYEE_NAME, employeeName);
        db.close();
    }

    public void saveParamSetup(SQLiteDatabase db, String paramName, String paramValue)
    {
        db.execSQL("delete from baseParams where ParamId='" + paramName + "'");
        ContentValues values = new ContentValues();
        values.put("ParamId", paramName);
        values.put("ParamValue", paramValue);
        db.insert("baseParams", null, values);
    }

    public void readSetup(Context context )
    {
        if (context == null ) context =appManager.getOurInstance().getCurrentContext();
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select ParamId, ParamValue from baseParams", null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            String paramName = cursor.getString(0);
            switch (paramName)
            {
                case PARAM_ROUTE_NAME: routeName = cursor.getString(1); break;
                case PARAM_ROUTE_ID: routeId = UUID.fromString(cursor.getString(1)); break;
                case PARAM_SERVICE_URL: serviceUrl = cursor.getString(1); break;
                case PARAM_EMPLOYEE_ID: employeeID = UUID.fromString(cursor.getString(1)); break;
                case PARAM_EMPLOYEE_NAME: employeeName = cursor.getString(1); break;
                case PARAM_debtControl: debtControl = cursor.getString(1).equals("1"); break;
                case PARAM_allowOverdueSum: allowOverdueSum = Double.valueOf(cursor.getString(1)); break;
                case PARAM_minOrderRowsQty: minOrderRowsQty = Integer.valueOf(cursor.getString(1)); break;
                case PARAM_minOrderSum: minOrderSum = Double.valueOf(cursor.getString(1)); break;
            }
            cursor.moveToNext();
        }
        db.close();
    }

    public String getDefaultPrice(){
        return PARAM_PRICEID_DEFAULT;
    }

}

