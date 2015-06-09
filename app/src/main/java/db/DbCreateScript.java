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
            " orderDate DATETIME DEFAULT CURRENT_TIMESTAMP, orderNumber integer DEFAULT 0, notes text, responseText text, _1CDocNumber1 text,  _1CDocNumber2 text, _send integer DEFAULT 0)";
    private static String CREATE_ORDER_HEADER_IDX = "CREATE INDEX idx_orderHeader_UUID ON orderHeader (orderUUID)";

    private static String CREATE_ORDER_DETAIL = "create table orderDetail(_id integer primary key autoincrement, headerId integer DEFAULT 0, orderUUID text, skuId text, qty1 integer, qty2 integer, _send integer DEFAULT 0)";
    private static String CREATE_CREATE_ORDER_DETAIL_IDX = "CREATE UNIQUE INDEX idx_orderDetail_UUID ON orderDetail (orderUUID, skuId)";
    private static String CREATE_CREATE_ORDER_DETAIL2_IDX = "CREATE UNIQUE INDEX idx_orderDetail_ID ON orderDetail (headerId, skuId)";

    private static String CREATE_CONTRACT = "create table contracts (CustomerId text, PriceId text, PriceName text, LimitSum double, Reprieve text, PartnerId text)";
    private static String CREATE_CONTRACT_IDX ="CREATE INDEX idx_contracts ON contracts (PartnerId)";

    private static String CREATE_SKUGROUP = "create table skuGroup (GroupId text, GroupName text, GroupParentId text)";
    private static String CREATE_SKUGROUP_IDX1 = "CREATE INDEX idx_skuGroup1 ON skuGroup (GroupId)";
    private static String CREATE_SKUGROUP_IDX2 = "CREATE INDEX idx_skuGroup2 ON skuGroup (GroupParentId)";

    private static String CREATE_SKU = "create table sku(SkuId text, SkuName text, SkuParentId text, QtyPack double, Article text)";
    private static String CREATE_SKU_IDX1 = "CREATE INDEX idx_sku ON sku (SkuId)";
    private static String CREATE_SKU_IDX2 = "CREATE INDEX idx_sku2 ON sku (SkuParentId)";


    private static String CREATE_PRICE = "create table price (SkuId  text, PriceId text, Pric double)";
    private static String CREATE_PRICE_IDX1 = "CREATE INDEX idx_price1 ON price (PriceId)";
    private static String CREATE_PRICE_IDX2 = "CREATE INDEX idx_price2 ON price (SkuId, PriceId)";

    private static String CREATE_STOCK = "create table stock(SkuId text, StockG double, StockR double)";
    private static String CREATE_STOCK_IDX1 = "CREATE INDEX idx_stock ON stock (SkuId)";

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
        return list;
    }

 //Drop table
    private static String DROP_BASEPARAMS = "DROP TABLE IF EXISTS baseParams";
    private static String DROP_ROUTE = "DROP TABLE ROUTE";
    private static String DROP_ORDER_HEADER = "DROP TABLE orderHeader";
    private static String DROP_DETAIL = "DROP TABLE orderDetail";
    private static String DROP_CONTRACTS = "DROP TABLE contracts";
    private static String DROP_SKUGROUP = "DROP TABLE skuGroup";
    private static String DROP_SKU = "DROP TABLE sku";
    private static String DROP_PRICE = "DROP TABLE price";
    private static String DROP_STOCK = "DROP TABLE stock";
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
        return list;
    }
}
