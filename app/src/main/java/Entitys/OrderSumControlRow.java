package Entitys;

/**
 * Created by shest on 2/4/2018.
 */

public class OrderSumControlRow {
    private double Amount;

    public OrderSumControlRow(double amount, String priceId) {
        Amount = amount;
        this.priceId = priceId;
    }

    private String priceId;

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }
}
