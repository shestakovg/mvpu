package Entitys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shest on 2/4/2018.
 */

//        Крупный опт e3c64316-daa6-11e4-826d-240a64c9314e
//        РЫНОК ОПТОВЫЙ b07c23b6-ed8d-11e4-9bea-3640b58dd6a2
//        Оптовый рынок(КОНТЕЙНЕРА) 52edf0c8-cd87-11e4-826a-240a64c9314e
//        Вид 1 75a9d60f-cd75-11e4-826a-240a64c9314e
//        Вид 3 75a9d611-cd75-11e4-826a-240a64c9314e
//        Вид 5 75a9d613-cd75-11e4-826a-240a64c9314e
public class OrderSumControl {
    private final double OPT = 1000;
    private final double Type5 = 800;
    private final double Type3 = 600;
    private final double Type1 = 500;
    private final double Mixed = 800;
    private ArrayList<OrderSumControlRow> rows = new ArrayList<OrderSumControlRow>();

    public boolean isAllowed() {
        return allowed;
    }

    public String getMessage() {
        return message;
    }

    private boolean allowed = false;
    private String message = "";
    public void AddRow(double amount, String priceId) {
        rows.add(new OrderSumControlRow(amount, priceId));
        checkOrder();
    }

    public void clear() {
        rows.clear();
    }
    public double getOrderAmount() {
        double res = 0;
        for (OrderSumControlRow row : rows) {
            res+= row.getAmount();
        }
        return res;
    }

    public void checkOrder() {
        double minAmount = Mixed;
        double amount = this.getOrderAmount();
        if (rows.size() == 1) {
            if  (rows.get(0).getPriceId().equals("b07c23b6-ed8d-11e4-9bea-3640b58dd6a2")) minAmount = OPT;
            if  (rows.get(0).getPriceId().equals("75a9d613-cd75-11e4-826a-240a64c9314e")) minAmount = Type5;
            if  (rows.get(0).getPriceId().equals("75a9d611-cd75-11e4-826a-240a64c9314e")) minAmount = Type3;
            if  (rows.get(0).getPriceId().equals("75a9d60f-cd75-11e4-826a-240a64c9314e")) minAmount = Type1;
        }
        if (amount >= minAmount) {
            message = "Отгрузка разрешена";
            allowed = true;
        }
        else
        {
            message = "Отгрузка запрещена. Сумма заказа: "+String.format("%.2f",amount)+" Минимальная сумма: "+String.format("%.2f",minAmount);
            allowed = false;
        }
    }

}
