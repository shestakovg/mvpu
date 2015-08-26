package Entitys;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import interfaces.IRouteDay;

/**
 * Created by g.shestakov on 26.08.2015.
 */
public class GpsLoggerData {
    private static GpsLoggerData ourInstance  ;

    public static GpsLoggerData getInstance(IRouteDay routeDay) {
        if (ourInstance == null)
        {
            ourInstance =  new  GpsLoggerData(routeDay);
        }
        return ourInstance;
    }

    private GpsLoggerData(IRouteDay routeDay) {
        this.routeDay = routeDay;
    }

    private IRouteDay routeDay;

    public void saveGpsLog(gpsData gps)
    {

    }

    public ArrayList<gpsData> getUnsentObjects()
    {
        return null;
    }

    public void markGpsObject(gpsData gps)
    {

    }
}
