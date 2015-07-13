package Entitys;

import interfaces.IOrderControlParams;

/**
 * Created by shestakov.g on 13.07.2015.
 */
public class orderControlParams {
    private int orderRows;
    private double orderSum;
    private IOrderControlParams params;

    public orderControlParams(int orderRows, double orderSum, IOrderControlParams params) {
        this.orderRows = orderRows;
        this.orderSum = orderSum;
        this.params = params;
    }

    public Boolean allowOrderToSave()
    {
        if (this.orderRows ==0 && this.orderSum == 0) return true;
        if (this.orderRows < params.getMinOrderRowsQty() && this.orderSum < this.params.getMinOrderSum())
        {
            return false;
        }
        return true;
    }

    public String getControlMessage()
    {
        String result = "";
        if (this.orderRows < params.getMinOrderRowsQty())
        {
            result+="В заказе должно быть минимум "+Integer.toString(this.params.getMinOrderRowsQty())+" строк. В заказе: "+Integer.toString(this.orderRows) +" стр\n";
        }
        if (this.orderSum < this.params.getMinOrderSum())
        {
            result+="Сумма заказа должна быть не меньше "+String.format("%.2f", this.params.getMinOrderSum())+" грн. Сумма заказа: "+String.format("%.2f",this.orderSum);
        }
        return result;
    }
}
