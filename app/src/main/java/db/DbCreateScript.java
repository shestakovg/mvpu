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
    private static String CREATE_ROUTE="create table if not exists route (outletId text, outletName text, VisitDay text , VisitDayId int,VisitOrder int, CustomerId text,CustomerName text, partnerId text, partnerName text, address text, IsRoute integer, CustomerClass text)";
    private static String CREATE_ROUTE_IDX = "CREATE  INDEX idx_route_outlet ON route (outletId)";
    private static String CREATE_ROUTE_IDX_DAY = "CREATE  INDEX idx_route_day ON route (VisitDay)";

    private static String CREATE_ORDER_HEADER = "create table orderHeader(_id integer primary key autoincrement, orderUUID text, outletId text, " +
                " orderDate DATETIME DEFAULT CURRENT_TIMESTAMP,deliveryDate DATETIME DEFAULT CURRENT_TIMESTAMP, orderNumber integer DEFAULT 0, notes text, responseText text, _1CDocNumber1 text,  _1CDocNumber2 text, payType integer DEFAULT 0, autoLoad integer DEFAULT 0, orderType integer default 0, _send integer DEFAULT 0,deliveryDateInitialized integer DEFAULT 0)";
    private static String CREATE_ORDER_HEADER_IDX = "CREATE INDEX idx_orderHeader_UUID ON orderHeader (orderUUID)";

    private static String CREATE_ORDER_DETAIL = "create table orderDetail(_id integer primary key autoincrement, headerId integer DEFAULT 0, orderUUID text, skuId text, qty1 integer, qty2 integer, PriceId text,finalDate DATETIME, availableInStore int DEFAULT 0, _send integer DEFAULT 0)";
    private static String CREATE_CREATE_ORDER_DETAIL_IDX = "CREATE UNIQUE INDEX idx_orderDetail_UUID ON orderDetail (orderUUID, skuId)";
    private static String CREATE_CREATE_ORDER_DETAIL2_IDX = "CREATE UNIQUE INDEX idx_orderDetail_ID ON orderDetail (headerId, skuId)";

    private static String CREATE_CONTRACT = "create table contracts (CustomerId text, PriceId text, PriceName text, LimitSum double, Reprieve text, PartnerId text)";
    private static String CREATE_CONTRACT_IDX ="CREATE INDEX idx_contracts ON contracts (PartnerId)";

    private static String CREATE_SKUGROUP = "create table skuGroup (GroupId text, GroupName text, GroupParentId text, Amount real, OutletCount integer, Color text, DontUseAmountValidation integer)";
    private static String CREATE_SKUGROUP_IDX1 = "CREATE INDEX idx_skuGroup1 ON skuGroup (GroupId)";
    private static String CREATE_SKUGROUP_IDX2 = "CREATE INDEX idx_skuGroup2 ON skuGroup (GroupParentId)";

    private static String CREATE_SKU = "create table sku(SkuId text, SkuName text, SkuParentId text, QtyPack double, Article text, OnlyFact integer,CheckCountInBox integer, onlyMWH integer, Color text, OutStockColor text, MinOrderQty integer default 3, IsHoreca integer,  MaxOrderQty integer default 1000000)";
    private static String CREATE_SKU_IDX1 = "CREATE INDEX idx_sku ON sku (SkuId)";
    private static String CREATE_SKU_IDX2 = "CREATE INDEX idx_sku2 ON sku (SkuParentId)";


    private static String CREATE_PRICE = "create table price (SkuId  text, PriceId text, Pric double)";
    private static String CREATE_PRICE_IDX1 = "CREATE INDEX idx_price1 ON price (PriceId)";
    private static String CREATE_PRICE_IDX2 = "CREATE INDEX idx_price2 ON price (SkuId, PriceId)";

    private static String CREATE_STOCK = "create table stock(SkuId text, StockG double, StockR double)";
    private static String CREATE_STOCK_IDX1 = "CREATE INDEX idx_stock ON stock (SkuId)";

    private static String CREATE_DEBT = "create table IF NOT EXISTS debts(partnerId text, customerId text,transactionId text,  transactionNumber text, transactionDate text, transactionSum double," +
            "paymentDate text, debt double, overdueDebt double, overdueDays int, color text)";
    private static String CREATE_DEBT_IDX1 = "CREATE INDEX idx_debt ON debts (customerId)";

    private static String CREATE_PAY = "create table pays (_id integer primary key autoincrement, payDate  DATETIME DEFAULT CURRENT_TIMESTAMP, transactionId text, customerid text, paySum double,  _send integer DEFAULT 0)";
    private static String CREATE_PAY_IDX1 = "CREATE INDEX idx_pays ON pays (payDate,transactionId )";

    private static String CREATE_GPS_LOG = "create table gpsLog (  _id integer  primary key, routeDayId integer, longtitude real, latitude real, logDate DATETIME DEFAULT CURRENT_TIMESTAMP, sateliteTime real,  _send integer DEFAULT 0 )";
    private static String CREATE_GPS_LOG_IDX1 = "CREATE INDEX idx1_gpsLog ON gpsLog (_send)";

    private static String CREATE_OUTLET_CHECKIN = "create table outletCheckIn (  _id integer  primary key, outletId text, longtitude real, latitude real, logDate DATETIME DEFAULT CURRENT_TIMESTAMP, sateliteTime real, _send integer DEFAULT 0 )";
    private static String CREATE_OUTLET_CHECKIN_IDX1 = "CREATE INDEX idx1_outletCheckIn ON gpsLog (_send)";


    private static String CREATE_ROUTE_DAY = "create table routesDay (_id integer primary key autoincrement, routeDay DATETIME DEFAULT CURRENT_TIMESTAMP, timeBeginning DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "timeCompletion DATETIME DEFAULT CURRENT_TIMESTAMP, status integer DEFAULT 0, AutomaticClosed integer DEFAULT 0, _send integer DEFAULT 0)";

    private static String CREATE_ROUTE_DAY_IDX1 ="CREATE INDEX idx1_routesDay ON routesDay (_send)";
    private static String CREATE_ROUTE_DAY_IDX2 ="CREATE INDEX idx2_routesDay ON routesDay (status)";

    private static String CREATE_PRICE_NAMES = "create table PriceNames(PriceId text primary key, PriceName text)";
    private static String CREATE_SKU_FACT = "create table skuFact(skuId text, priceId text)";

    private static String CREATE_SPECIFICATION = "create table Specification(outletId text, skuId text)";
    private static String CREATE_OUTLETINFO ="create table OutletInfo(OutletId text, Category text,Manager1 text,Manager2 text,Phone1 text,Phone2 text,DeliveryDay text,ManagerTime text,ReciveTime text,ContactPerson text)";
    private static String CREATE_OUTLETINFO_IDX1 = "CREATE INDEX idx1_OutletInfo ON routesDay (OutletId)";

    private static String CREATE_CLIENT_CARD_SKU ="create table ClientCardSku(OutletId text, SkuId text, Warehouse integer, LastDate text, Qty integer)";
    private static String CREATE_CLIENT_CARD_SKU_IDX1 = "CREATE INDEX idx1_CLIENT_CARD_SKU ON ClientCardSku(OutletId)";

    private static  String CREATE_SALESFACT = "create table salesfact(GroupId text, FactAmount real, FactOutletCount integer)";

    private static String CREATE_NEW_CUSTOMERS = "create table NewCustomers(_id integer primary key autoincrement, RegistrationDate DATETIME DEFAULT CURRENT_TIMESTAMP, Territory text, RouteId text, CustomerName text, " +
            " DeliveryAddress text, OutletCategoty int, PriceType text, VisitDay int, DeliveryDay int, Manager1Name text, Manager1Phone text, Manager2Name text, Manager2Phone text, AdditionalInfo text,  _send integer DEFAULT 0)";


    public static String CREATE_OUTLET_CATEGORYES = "create table OutletCategoryes( categoryName text, categoryOrder int)";
    public static String CREATE_DAY_OF_WEEK = "create table dayOfWeek(dayName text, dayOrder int)";

    public static String CREATE_DELIVERY_AREA = "create table DeliveryArea(idRef text, Description text)";
    public static String CREATE_NO_RESULT_REASONS = "create table no_result_reasons(_id integer, description text)";
    public static String CREATE_NO_RESULT_STORAGE = "create table No_result_storage(_id integer primary key autoincrement, Date DATETIME DEFAULT CURRENT_TIMESTAMP, outletid text, reasonId int,  _send integer DEFAULT 0)";

    public static String CREATE_TASKS = "create table tasks(_id integer primary key autoincrement, taskDate DATETIME DEFAULT CURRENT_TIMESTAMP, reference text, outletId text, number text, Description text, ResultDescription text, status integer DEFAULT 0, _send integer DEFAULT 0)";
    public static String CREATE_TASKS_IDX1 = "CREATE INDEX idx1_TASKS ON tasks(outletId)";
    public static String CREATE_TASKS_IDX2 = "CREATE INDEX idx2_TASKS ON tasks(status, _send)";

    public static String CREATE_PRICE_ÑHANGES = "create table priceChanges (_id integer primary key autoincrement, Date text, OldPrice real, NewPrice real, priceId text, skuId text)";
    public static String CREATE_ORDER_STOCK_TEMPLATE = "create table orderStockTemplateHeader(_id integer primary key autoincrement, headerId text, orderHeaderId text, Date DATETIME DEFAULT CURRENT_TIMESTAMP,outletId text, number int, _send integer DEFAULT 0)";
    public static String CREATE_ORDER_STOCK_TEMPLATE_LINES  = "create table orderStockTemplateLines(_id integer primary key autoincrement, headerId text, skuId text, exist int, _send integer DEFAULT 0)";
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
        list.add(CREATE_CREATE_ORDER_DETAIL2_IDX);
        list.add(CREATE_CONTRACT);
        list.add(CREATE_CONTRACT_IDX);
        list.add(CREATE_SKUGROUP);
        list.add(CREATE_SKUGROUP_IDX1);
        list.add(CREATE_SKUGROUP_IDX2);
        list.add(CREATE_SKU);
        list.add(CREATE_SKU_IDX1);
        list.add(CREATE_SKU_IDX2);
        list.add(CREATE_PRICE);
        list.add(CREATE_PRICE_IDX1);
        list.add(CREATE_PRICE_IDX2);
        list.add(CREATE_STOCK);
        list.add(CREATE_STOCK_IDX1);
        list.add(CREATE_DEBT);
        list.add(CREATE_DEBT_IDX1);
        list.add(CREATE_PAY);
        list.add(CREATE_PAY_IDX1);
        list.add(CREATE_GPS_LOG);
        list.add(CREATE_GPS_LOG_IDX1);
        list.add(CREATE_ROUTE_DAY);
        list.add(CREATE_ROUTE_DAY_IDX1);
        list.add(CREATE_ROUTE_DAY_IDX2);
        list.add(CREATE_PRICE_NAMES);
        list.add(CREATE_SKU_FACT);
        list.add(CREATE_SPECIFICATION);
        list.add(CREATE_OUTLETINFO);
        list.add(CREATE_OUTLETINFO_IDX1);
        list.add(CREATE_CLIENT_CARD_SKU);
        list.add(CREATE_CLIENT_CARD_SKU_IDX1);
        list.add(CREATE_OUTLET_CHECKIN);
        list.add(CREATE_OUTLET_CHECKIN_IDX1);
        list.add(CREATE_SALESFACT);
        list.add(CREATE_NEW_CUSTOMERS);
        list.add(CREATE_OUTLET_CATEGORYES);
        list.add(CREATE_DAY_OF_WEEK);
        list.add(CREATE_DELIVERY_AREA);
        list.add(CREATE_NO_RESULT_REASONS);
        list.add(CREATE_NO_RESULT_STORAGE);
        list.add(CREATE_TASKS);
        list.add(CREATE_TASKS_IDX1);
        list.add(CREATE_TASKS_IDX2);
        list.add("insert into no_result_reasons values (1,'Îòñóòñòâèå ËÏÐ')");
        list.add("insert into no_result_reasons values (2,'Äåáåòîðêà')");
        list.add("insert into no_result_reasons values (3,'Íåäîáîð')");
        list.add(CREATE_PRICE_ÑHANGES);
        list.add(CREATE_ORDER_STOCK_TEMPLATE);
        list.add(CREATE_ORDER_STOCK_TEMPLATE_LINES);
        return list;
    }

 //Drop tables
    private static String DROP_BASEPARAMS = "DROP TABLE IF EXISTS baseParams";
    private static String DROP_ROUTE = "DROP TABLE ROUTE";
    private static String DROP_ORDER_HEADER = "DROP TABLE orderHeader";
    private static String DROP_DETAIL = "DROP TABLE orderDetail";
    private static String DROP_CONTRACTS = "DROP TABLE contracts";
    private static String DROP_SKUGROUP = "DROP TABLE skuGroup";
    private static String DROP_SKU = "DROP TABLE sku";
    private static String DROP_PRICE = "DROP TABLE price";
    private static String DROP_STOCK = "DROP TABLE stock";
    private static String DROP_DEBT = "DROP TABLE debts";
    private static String DROP_PAYS  = "DROP TABLE pays";
    private static String DROP_GPS_LOG  = "DROP TABLE gpsLog";
    private static String DROP_ROUTE_DAY  = "DROP TABLE routesDay";
    private static String DROP_PRICE_NAMES ="DROP TABLE PriceNames";
    private static String DROP_SKU_FACT="DROP TABLE skuFact";
    private static String DROP_SPECIFICATION="DROP TABLE Specification";
    private static String DROP_OUTLETINFO="DROP TABLE OutletInfo";
    private static String DROP_CLIENT_CARD_SKU="DROP TABLE ClientCardSku";
    private static String DROP_OUTLET_CHECKIN = "DROP TABLE outletCheckIn";
    private static String DROP_SALESFACT = "DROP TABLE salesfact";
    private static String DROP_NEW_CUSTOMERS = "drop table NewCustomers";
    private static String DROP_OUTLET_CATEGORYES = "drop table OutletCategoryes";
    private static String DROP_DAY_OF_WEEK ="drop table dayOfWeek";
    private static String DROP_DELIVERY_AREA="drop table DeliveryArea";
    private static String DROP_NO_RESULT_REASONS="drop table no_result_reasons";
    private static String DROP_NO_NO_RESULT_STORAGE="drop table no_result_reasons";
    private static String DROP_TASKS="drop table tasks";
    private static String DROP_PRICE_CHANGES="drop table priceChanges";
    private static String DROP_ORDER_STOCK_TEMPLATE = "drop table orderStockTemplateHeader";
    private static String DROP_ORDER_STOCK_TEMPLATE_LINES  = "drop table orderStockTemplateLines";

    public static ArrayList<String>  getDropTableScripts()
    {
        ArrayList<String> list = new ArrayList<String>();
        //list.add(DROP_BASEPARAMS);
        list.add(DROP_ROUTE);
        list.add(DROP_ORDER_HEADER);
        list.add(DROP_DETAIL);
        list.add(DROP_CONTRACTS);
        list.add(DROP_SKUGROUP);
        list.add(DROP_SKU);
        list.add(DROP_PRICE);
        list.add(DROP_STOCK);
        list.add(DROP_DEBT);
        list.add(DROP_PAYS);
        list.add(DROP_GPS_LOG);
        list.add(DROP_ROUTE_DAY);
        list.add(DROP_PRICE_NAMES);
        list.add(DROP_SKU_FACT);
        list.add(DROP_SPECIFICATION);
        list.add(DROP_OUTLETINFO);
        list.add(DROP_CLIENT_CARD_SKU);
        list.add(DROP_OUTLET_CHECKIN);
        list.add(DROP_SALESFACT);
        list.add(DROP_NEW_CUSTOMERS);
        list.add(DROP_OUTLET_CATEGORYES);
        list.add(DROP_DAY_OF_WEEK);
        list.add(DROP_DELIVERY_AREA);
        list.add(DROP_NO_RESULT_REASONS);
        list.add(DROP_NO_NO_RESULT_STORAGE);
        list.add(DROP_TASKS);
        list.add(DROP_PRICE_CHANGES);
        list.add(DROP_ORDER_STOCK_TEMPLATE);
        list.add(DROP_ORDER_STOCK_TEMPLATE_LINES);
        return list;
    }
}

