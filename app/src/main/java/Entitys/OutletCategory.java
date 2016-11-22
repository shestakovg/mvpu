package Entitys;

/**
 * Created by shest on 11/17/2016.
 */

public class OutletCategory {
    private int categoryOrder;
    private String categoryName;

    public int getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(int categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public OutletCategory(int categoryOrder, String categoryName) {
        this.categoryOrder = categoryOrder;
        this.categoryName = categoryName;
    }
}
