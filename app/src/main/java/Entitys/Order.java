package Entitys;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import core.AppSettings;

/**
 * Created by shestakov.g on 02.06.2015.
 */
public class Order implements Parcelable {
    private static int idOrder;
    public String orderDescription;
    public int _id;
    public int orderNumber;
    public String orderUUID;
    public Calendar orderDateCalendar;
    public Date orderDate;
    public Calendar deliveryDate;
    public double orderSum;
    public String responseText="";
    public String _1CDocNumber1="";
    public String _1CDocNumber2="";
    public String  notes="";
    public String outletId;
    public Boolean sended = false;
    public Boolean underSumLimit = true;

    public int orderType = AppSettings.ORDER_TYPE_ORDER;
    public Order(int _id, int orderNumber, String orderUUID, Date orderDate, double orderSum) {
        //this.orderDescription = orderDescription;
        this._id = _id;
        this.orderNumber = orderNumber;
        this.orderUUID = orderUUID;
        this.orderDate = orderDate;
        this.orderSum = orderSum;
        generateDescription();
    }

    public Order() {

    }

    private void generateDescription()
    {
        this.orderDescription = "Заказ № "+this.orderNumber+" от "+ DateFormat.format("dd.MM.yyyy", this.orderDate);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this._id);

//        dest.writeInt(this.orderNumber);
//        dest.writeString(this.orderDescription);
//        //dest.writeDouble(this.orderSum);
//        dest.writeString(this.orderUUID.toString());
        //dest.writeString(DateFormat.format("dd.MM.yyyy", this.orderDate).toString());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    private Order(Parcel parcel) {

        this._id = parcel.readInt();
        this.orderDescription = "Order with _id "+ this._id;
        this.orderSum = 0;

//        this.orderUUID = parcel.readString();
    }

    public static double initOrderById(int idOrder)
    {
        return 0;
    }

//    public static double getOrderSum(int orderId)
//    {
//        return 0;
//    }
//
//    public static int getOrderRows(int orderId)
//    {
//        return 0;
//    }

}
