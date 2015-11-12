package interfaces;

import core.ResultObject;

/**
 * Created by shestakov.g on 29.06.2015.
 */
public interface IInputCustomerPay {
    void processPay(double paySum );
    ResultObject checkAllowInputPay(double paySum);
}
