package interfaces;

import Entitys.OutletObject;

/**
 * Created by shestakov.g on 13.07.2015.
 */
public interface IOrderControlParams {
     int getMinOrderRowsQty();
     double getMinOrderSum();
     double getMinOrderSumByCategory(OutletObject ob);
}
