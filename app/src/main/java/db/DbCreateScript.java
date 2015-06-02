package db;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by g.shestakov on 26.05.2015.
 */
public class DbCreateScript {
    // Create table
    private static String CREATE_BASEPARAMS="create table IF NOT EXISTS baseParams (ParamId text, paramValue text, PRIMARY KEY(ParamId ASC)) ";
    private static String CREATE_ROUTE="create table if not exists route (outletId text, outletName text, VisitDay text , VisitDayId int,VisitOrder int, CustomerId text,CustomerName text, partnerId text, partnerName text, address text)";
    private static String CREATE_ROUTE_IDX = "CREATE INDEX idx_route_outlet ON route (outletId)";
    private static String CREATE_ROUTE_IDX_DAY = "CREATE INDEX idx_route_day ON route (VisitDay)";

    private static String CREATE_ORDER_HEADER = "create table orderHeader(_id integer primary key autoincrement, orderUUID text, outletId text, " +
            " orderDate DATETIME DEFAULT CURRENT_TIMESTAMP, orderNumber int, notes text, responseText text, _1CDocNumber1 text,  _1CDocNumber2 text, _send int DEFAULT 0)";
    private static String CREATE_ORDER_HEADER_IDX = "CREATE INDEX idx_orderHeader_UUID ON orderHeader (orderUUID)";

    private static String CREATE_ORDER_DETAIL = "create table orderDetail(_id integer primary key autoincrement, headerId int, orderUUID text, skuId text, qty1 int, qty2 int, _send int DEFAULT 0)";
    private static String CREATE_CREATE_ORDER_DETAIL_IDX = "CREATE INDEX idx_orderDetail_UUID ON orderDetail (orderUUID, skuId)";
    public static  ArrayList<String> getCreateDataBaseScripts()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(CREATE_BASEPARAMS);
        list.add(CREATE_ROUTE);
        list.add(CREATE_ROUTE_IDX);
        list.add(CREATE_ROUTE_IDX_DAY);
        list.add(CREATE_ORDER_HEADER);
        list.add(CREATE_ORDER_HEADER_IDX);
        list.add(CREATE_ORDER_DETAIL);
        list.add(CREATE_CREATE_ORDER_DETAIL_IDX);
        return list;
    }

 //Drop table
    private static String DROP_BASEPARAMS = "DROP TABLE IF EXISTS baseParams";
    private static String DROP_ROUTE = "DROP TABLE ROUTE";
    private static String DROP_ORDER_HEADER = "DROP TABLE orderHeader";
    public static ArrayList<String>  getDropTableScripts()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(DROP_BASEPARAMS);
        list.add(DROP_ROUTE);
        list.add(DROP_ORDER_HEADER);
        return list;
    }
}
