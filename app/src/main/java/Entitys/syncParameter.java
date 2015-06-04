package Entitys;

import core.AppSettings;
import core.appManager;

/**
 * Created by g.shestakov on 04.06.2015.
 */
public class syncParameter {
    public final static  int PARAM_SYNC_ROUTE =1 ;
    public final static  int PARAM_SYNC_DOCS  = 2 ;
    public final static  int PARAM_SYNC_SKUGROUP  = 3 ;
    public final static  int PARAM_SYNC_SKU  = 4 ;
    public final static  int PARAM_SYNC_PRICE  = 5 ;
    public final static  int PARAM_SYNC_STOCK  = 6 ;

    public String getUrl() {
        return url;
    }

    private String url;

    public String getDescription() {
        return description;
    }

    private String description;

    public String getArgument() {
        return argument;
    }

    private String argument;
    private int paramID;

    public syncParameter(String url, String argument) {
        this.url = url;
        //this.description = description;
        this.argument = argument;

    }

    public syncParameter(int paramId) {
        this.url = appManager.getOurInstance().appSetupInstance.getServiceUrl();
        this.paramID = paramId;

        switch (paramId) {
            case PARAM_SYNC_ROUTE:
                this.description = "Обновление маршрута";
                this.argument = "dictionary/getrouteset/"+appManager.getOurInstance().appSetupInstance.getRouteId();

                break;
        }

    }
}
