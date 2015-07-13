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
            result+="� ������ ������ ���� ������� "+Integer.toString(this.params.getMinOrderRowsQty())+" �����. � ������: "+Integer.toString(this.orderRows) +" ���\n";
        }
        if (this.orderSum < this.params.getMinOrderSum())
        {
            result+="����� ������ ������ ���� �� ������ "+String.format("%.2f", this.params.getMinOrderSum())+" ���. ����� ������: "+String.format("%.2f",this.orderSum);
        }
        return result;
    }
}
