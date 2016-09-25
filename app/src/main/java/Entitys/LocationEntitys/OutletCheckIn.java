package Entitys.LocationEntitys;

/**
 * Created by shest on 9/18/2016.
 */
public class OutletCheckIn {
    private String  routeId;
    private String outletId;
    private double longtitude;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getSateliteTime() {
        return sateliteTime;
    }

    public void setSateliteTime(String sateliteTime) {
        this.sateliteTime = sateliteTime;
    }

    private double latitude;
    private String sateliteTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
}
