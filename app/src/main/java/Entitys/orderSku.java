package Entitys;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class orderSku {
    public String skuId;
    public String skuName;
    public String headerId;
    public String orderUUID;
    public int _id;
    public int qty1;
    public int qty2;
    public double rowSum;
    public double stockG;
    public double stockR;
    public orderSku( String skuName) {

        this.skuName = skuName;
    }

}
