package db;

/**
 * Created by g.shestakov on 26.05.2015.
 */
public class DbSettings {
    public int getDB_VERSION() {
        return DB_VERSION;
    }

    private   int DB_VERSION = 6;

    private static DbSettings ourInstance = new DbSettings();

    public static DbSettings getInstance() {
        if (ourInstance == null)
        {
            ourInstance = new DbSettings();
        }

        return ourInstance;
    }

    private DbSettings() {
    }

    public  String DB_NAME="unicom.db";
    public  String DB_PATH;

}
