package interfaces;

import Entitys.OrderExtra;

/**
 * Created by shestakov.g on 07.06.2015.
 */
public interface IOrder {
    OrderExtra getOrderExtra();
    void refreshSku(String skuGroup);
}
