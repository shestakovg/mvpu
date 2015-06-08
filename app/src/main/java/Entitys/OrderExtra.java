package Entitys;

/**
 * Created by g.shestakov on 04.06.2015.
 */
public class OrderExtra extends Order {

    public OrderExtra() {

    }

    public OrderExtra(Order order) {
        //super(order._id, );
        _id = order._id;
        fillOrderExtra();
    }

    public void fillOrderExtra()
    {

    }

    public static OrderExtra intInstanceFromDb(Order order)
    {
        return new OrderExtra(order);
    }
}
