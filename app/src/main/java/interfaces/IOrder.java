package interfaces;

import java.text.ParseException;

import Entitys.OrderExtra;
import Entitys.OutletObject;

/**
 * Created by shestakov.g on 07.06.2015.
 */
public interface IOrder {
    OrderExtra getOrderExtra();
    void refreshSku(String skuGroup) throws ParseException;
    OutletObject getOutletObject();
    void showOrderParams();
}
