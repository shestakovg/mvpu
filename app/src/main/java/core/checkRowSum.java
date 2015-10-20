package core;

/**
 * Created by shestakov.g on 20.10.2015.
 */
public class checkRowSum {
    private double skuPrice = 0;

    public checkRowSum(double skuPrice) {
        this.skuPrice = skuPrice;
    }

    public int getMinOrderQty()
    {
        if (this.skuPrice <= 30) return 3;
        else if (this.skuPrice > 30 && this.skuPrice <= 50) return 2;
        else  return -1;
    }

    public String getSkuPriceTitle()
    {
        int minOrder = getMinOrderQty();
        String result = "Минимальный заказ - "+minOrder+" шт";
        if (minOrder<0) result = "";
        return result;
    }

}
