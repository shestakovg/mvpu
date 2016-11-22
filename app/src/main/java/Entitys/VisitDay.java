package Entitys;

/**
 * Created by shest on 11/17/2016.
 */

public class VisitDay {
    public VisitDay(int dayOrder, String dayName) {
        this.dayOrder = dayOrder;
        this.dayName = dayName;
    }

    private int dayOrder;

    public int getDayOrder() {
        return dayOrder;
    }

    public void setDayOrder(int dayOrder) {
        this.dayOrder = dayOrder;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    private String dayName;
}
