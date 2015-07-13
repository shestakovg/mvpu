package interfaces;

import Entitys.orderControlParams;

/**
 * Created by shestakov.g on 10.06.2015.
 */
public interface IOrderTotal {
    orderControlParams displayTotal();
    Boolean allowCloseOrder();
}
