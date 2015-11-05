package Entitys;

/**
 * Created by shestakov.g on 04.11.2015.
 */
public class priceTypeObject {
    public priceTypeObject(String priceType, String priceName) {
        this.priceType = priceType;
        this.priceName = priceName;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    private String priceType;
    private String priceName;
}
