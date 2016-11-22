package Entitys;

/**
 * Created by shest on 11/17/2016.
 */

public class DeliveryArea {
    private String idRef;
    private String  Description;

    public DeliveryArea(String idRef, String description) {
        this.idRef = idRef;
        Description = description;
    }

    public String getIdRef() {
        return idRef;
    }

    public void setIdRef(String idRef) {
        this.idRef = idRef;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
