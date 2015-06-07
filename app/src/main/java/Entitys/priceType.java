package Entitys;

/**
 * Created by shestakov.g on 06.06.2015.
 */
public class priceType {
    public String getPriceId() {
        return priceId;
    }

    public String getPriceName() {
        return priceName;
    }

    private String priceId;
    private String priceName;

    public priceType(String priceId, String priceName) {
        this.priceId = priceId;
        this.priceName = priceName;
    }
}
