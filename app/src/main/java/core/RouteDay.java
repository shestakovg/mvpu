package core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shestakov.g on 29.11.2015.
 */
public class RouteDay {
    public RouteDay(int id, Calendar routeDate, int status) {
        this.id = id;
        this.routeDate = routeDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getRouteDate() {
        return routeDate;
    }

    public void setRouteDate(Calendar routeDate) {
        this.routeDate = routeDate;
    }

    private int id;
    private Calendar routeDate;
    private int status = 0;

    public String getRouteDescription()
    {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String formatted = format.format(this.routeDate.getTime());
        return "Текущий маршрут за "+formatted+(this.status == 0 ? " открыт" : "закрыт");
    }

    public static  String getEmptyRouteDescription()
    {
        return "Маршрут не открыт";
    }
}
