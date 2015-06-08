package Entitys;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class orderSku {
    private boolean isEdit = false;
    //************
    public String skuId;
    public String skuName;
    public int headerId;
    public String orderUUID;
    public int _id;
    public int qtyMWH;
    public int qtyRWH;
    public double rowSum;
    public double price;
    public double stockG;
    public double stockR;

    public orderSku( String skuName) {

        this.skuName = skuName;
    }

    public String getQtyMWHForEditText()
    {
        if (qtyMWH == 0) return "";
        else
            return Integer.toString(qtyMWH);
    }

    public String getQtyRWHForEditText()
    {
        if (qtyRWH == 0) return "";
        else
            return Integer.toString(qtyRWH);
    }

    public void setQtyMWH(int qty)
    {
        this.qtyMWH = qty;
        isEdit = true;
        calcRowSum();
    }
    public void setQtyRWH(int qty)
    {
        this.qtyRWH = qty;
        isEdit = true;
        calcRowSum();
    }

    private void calcRowSum()
    {
        this.rowSum = qtyMWH * price + qtyRWH * price;
    }

}
