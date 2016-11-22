package Entitys;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import core.DayOfWeekController;
import core.DeliveryAreaController;
import core.OutletCategoryController;
import core.PriceTypeController;
import db.DbOpenHelper;

/**
 * Created by shest on 11/13/2016.
 */

public class NewCustomer  implements Serializable {
    private Calendar registrationDate;
    private int id = -1;

    private String TerritoryId = "";
    private String TerritoryName = "";

    private String RouteId;
    private String CustomerName = "";
    private String DeliveryAddress = "";

    private int OutletCategotyId = -1;
    private String OutletCategotyName = "";

    private String PriceTypeId = "";
    private String PriceTypeName = "";

    private int VisitDayId = -1;
    private String VisitDayName = "";

    private int DeliveryDayId = -1;
    private String DeliveryDayName = "";

    private String Manager1Name = "";
    private String Manager2Name = "";

    private String Manager1Phone = "";
    private String Manager2Phone = "";

    private Context context;

    public NewCustomer(Context context) {
        this.context = context;
    }

    public NewCustomer(Context context, String routeId) {
        this.context = context;
        this.RouteId = routeId;
    }

    public String getAdditionalInfo() {
        return AdditionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        AdditionalInfo = additionalInfo;
    }

    public Calendar getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Calendar registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerritoryId() {
        return TerritoryId;
    }

    public void setTerritoryId(String territoryId) {
        TerritoryId = territoryId;
    }

    public String getTerritoryName() {
        return TerritoryName;
    }

    public void setTerritoryName(String territoryName) {
        TerritoryName = territoryName;
    }

    public String getRouteId() {
        return RouteId;
    }

    public void setRouteId(String routeId) {
        RouteId = routeId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        DeliveryAddress = deliveryAddress;
    }

    public int getOutletCategotyId() {
        return OutletCategotyId;
    }

    public void setOutletCategotyId(int outletCategotyId) {
        OutletCategotyId = outletCategotyId;
    }

    public String getOutletCategotyName() {
        return OutletCategotyName;
    }

    public void setOutletCategotyName(String outletCategotyName) {
        OutletCategotyName = outletCategotyName;
    }

    public String getPriceTypeId() {
        return PriceTypeId;
    }

    public void setPriceTypeId(String priceTypeId) {
        PriceTypeId = priceTypeId;
    }

    public String getPriceTypeName() {
        return PriceTypeName;
    }

    public void setPriceTypeName(String priceTypeName) {
        PriceTypeName = priceTypeName;
    }

    public int getVisitDayId() {
        return VisitDayId;
    }

    public void setVisitDayId(int visitDayId) {
        VisitDayId = visitDayId;
    }

    public String getVisitDayName() {
        return VisitDayName;
    }

    public void setVisitDayName(String visitDayName) {
        VisitDayName = visitDayName;
    }

    public int getDeliveryDayId() {
        return DeliveryDayId;
    }

    public void setDeliveryDayId(int deliveryDayId) {
        DeliveryDayId = deliveryDayId;
    }

    public String getDeliveryDayName() {
        return DeliveryDayName;
    }

    public void setDeliveryDayName(String deliveryDayName) {
        DeliveryDayName = deliveryDayName;
    }

    public String getManager1Name() {
        return Manager1Name;
    }

    public void setManager1Name(String manager1Name) {
        Manager1Name = manager1Name;
    }

    public String getManager2Name() {
        return Manager2Name;
    }

    public void setManager2Name(String manager2Name) {
        Manager2Name = manager2Name;
    }

    public String getManager1Phone() {
        return Manager1Phone;
    }

    public void setManager1Phone(String manager1Phone) {
        Manager1Phone = manager1Phone;
    }

    public String getManager2Phone() {
        return Manager2Phone;
    }

    public void setManager2Phone(String manager2Phone) {
        Manager2Phone = manager2Phone;
    }

    private String AdditionalInfo = "";

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;

    }

    public void markAsSend()
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update NewCustomers set _send = 1 where _id="+ this.getId());
        db.close();
    }

    private boolean send;

    public boolean isNew()
    {
        if (this.id < 0)
            return true;
        else
            return false;
    }

    public void Save()
    {
        if (this.id<0)
            insertNewCustomer();
        else
            updateNewCustomer();
        this.hasChanged = false;
    }

    public boolean isHasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    private boolean hasChanged = false;

    private void insertNewCustomer()
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Territory",this.getTerritoryId());
        values.put("RouteId",this.getRouteId());
        values.put("CustomerName",this.getCustomerName());
        values.put("DeliveryAddress",this.getDeliveryAddress());
        values.put("OutletCategoty",this.getOutletCategotyId());
        values.put("PriceType",this.getPriceTypeId());
        values.put("VisitDay",this.getVisitDayId());
        values.put("DeliveryDay",this.getDeliveryDayId());
        values.put("Manager1Name",this.getManager1Name());
        values.put("Manager2Name",this.getManager2Name());
        values.put("Manager1Phone",this.getManager1Phone());
        values.put("Manager2Phone",this.getManager2Phone());
        values.put("AdditionalInfo",this.getAdditionalInfo());
        db.insert("NewCustomers", null, values);
        db.close();
    }

    private void updateNewCustomer()
    {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        String query =
                "UPDATE NewCustomers\n" +
                "   SET \n" +
                "      Territory = '" +this.getTerritoryId()+"'"+
                "      ,CustomerName = '" +this.getCustomerName()+"'"+
                "      ,DeliveryAddress = '" +this.getDeliveryAddress()+"'"+
                "      ,OutletCategoty = " +this.getOutletCategotyId()+
                "      ,PriceType = '" +this.getPriceTypeId()+"'"+
                "      ,VisitDay = " +this.getVisitDayId()+
                "      ,DeliveryDay = " +this.getDeliveryDayId()+
                "      ,Manager1Name = '" +this.getManager1Name()+"'"+
                "      ,Manager1Phone = '" +this.getManager1Phone()+"'"+
                "      ,Manager2Name = '" +this.getManager2Name()+"'"+
                "      ,Manager2Phone = '" +this.getManager2Phone()+"'"+
                "      ,AdditionalInfo = '" +this.getAdditionalInfo()+"'"+
                "      ,_send = 0 " +
                " WHERE _id = "+this.getId();
        db.execSQL(query);
        db.close();
    }

    public boolean isValid()
    {
        boolean result = true;
        this.validationMessage = "";
        if (CustomerName.isEmpty()) {
            result = false;
            this.validationMessage += "Не указано наименование ЧП/ФОП\n";
        }

        if (DeliveryAddress.isEmpty()) {
            result = false;
            this.validationMessage += "Не указан адрес доставки\n";
        }

        if (TerritoryId.isEmpty() || TerritoryName.equals(DeliveryAreaController.DEFAULT_VALUE)) {
            result = false;
            this.validationMessage += "Не указана территория\n";
        }

        if (PriceTypeId.isEmpty() || PriceTypeName.equals(PriceTypeController.DEFAULT_VALUE)) {
            result = false;
            this.validationMessage += "Не указан тип цен\n";
        }

        if (OutletCategotyId < 0  || OutletCategotyName.equals(OutletCategoryController.DEFAULT_VALUE)) {
            result = false;
            this.validationMessage += "Не указана категория торговой точки\n";
        }
        if (DeliveryDayId < 0  || DeliveryDayName.equals(DayOfWeekController.DEFAULT_VALUE)) {
            result = false;
            this.validationMessage += "Не указан день доставки\n";
        }
        if (VisitDayId < 0  || VisitDayName.equals(DayOfWeekController.DEFAULT_VALUE)) {
            result = false;
            this.validationMessage += "Не указан день визита\n";
        }

        if (Manager1Name.isEmpty() && Manager2Name.isEmpty())
        {
            result = false;
            this.validationMessage += "Не указано ни одного ЛПР\n";
        }

        if (Manager1Phone.isEmpty() && Manager2Phone.isEmpty())
        {
            result = false;
            this.validationMessage += "Не указано ни одного телефона ЛПР\n";
        }
        return result;
    }

    private String validationMessage = "";

    public String getValidationMessage( )
    {
        return this.validationMessage;
    }

    public void showSendError()
    {
        Toast.makeText(context, "Не удалось отправить: "+this.getCustomerName(), Toast.LENGTH_LONG).show();
    }

}
