package Entitys;

import android.content.Context;
import android.util.Log;

import com.uni.mvpu.R;

import java.util.GregorianCalendar;

import interfaces.IRouteDay;

/**
 * Created by g.shestakov on 26.08.2015.
 */
public class RouteDays implements IRouteDay {

    private static  RouteDays routeDay;

    private Context context;

    private RouteDays(Context context) {
        this.context = context;
    }

    public static RouteDays openRouteDay(GregorianCalendar calendar,Context context) {
        if (routeDay == null)
        {
          //  throw new Exception( appl .getString(R.string.outdoor_route_does_not_exist);
            routeDay =  new RouteDays(context);
        }
        return routeDay;
    }

    public static RouteDays getRouteDay() throws Exception {
        if (routeDay == null) {
            Log.d("ROUTE_DAYS","Нет открытого маршрута");
            throw new Exception("Нет открытого маршрута");

        }
        return routeDay;
    }

    public static void closeRoute(GregorianCalendar calendar, Context context) throws Exception {
        if (routeDay == null) {
            Log.d("ROUTE_DAYS","Нет открытого маршрута");
            throw new Exception("Нет открытого маршрута");
        }
        routeDay = null;
        /*Close route into db*/
    }

    public static Boolean existsOpenRoute(GregorianCalendar calendar, Context context)
    {
        openRouteDay(calendar, context);
        return true;
    }

    private int routeId;


    @Override
    public int getRouteId() {
        return routeId;
    }
}
