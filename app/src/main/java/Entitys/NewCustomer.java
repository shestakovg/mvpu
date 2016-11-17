package Entitys;

import android.content.Context;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by shest on 11/13/2016.
 */

public class NewCustomer  implements Serializable {
    private Calendar registrationDate;
    private int id = -1;

    private String TerritoryId;
    private String TerritoryName;

    private String RouteId;
    private String CustomerName = "";
    private String DeliveryAddress = "";

    private int OutletCategotyId;
    private String OutletCategotyName;

    private String PriceTypeId;
    private String PriceTypeName;

    private int VisitDayId;
    private String VisitDayName;

    private int DeliveryDayId;
    private String DeliveryDayName;

    private String Manager1Name;
    private String Manager2Name;

    private String Manager1Phone;
    private String Manager2Phone;

    private Context context;

    public NewCustomer(Context context) {
        this.context = context;
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

    private String AdditionalInfo;

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    private boolean send;

    private boolean isNew()
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
    }

    private void insertNewCustomer()
    {

    }

    private void updateNewCustomer()
    {

    }
}
