package Entitys;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by shestakov.g on 02.06.2015.
 */
public class Order {
    public String orderDescription;
    public int _id;
    public int orderNumber;
    public String orderUUID;
    public Date orderDate;
    public double orderSum;


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
}
