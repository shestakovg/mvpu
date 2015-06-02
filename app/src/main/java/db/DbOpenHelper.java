package db;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by g.shestakov on 26.05.2015.
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    private DbSettings dbSettings;

    private SQLiteDatabase database;
    private final Context context;

    public SQLiteDatabase getDb() {
        return database;
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = dbSettings.DB_PATH + dbSettings.DB_NAME;
            checkDb = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.e(this.getClass().toString(), "Error while checking db");
        }

        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }
    public SQLiteDatabase openDataBase() throws SQLException {
        String path = this.dbSettings.DB_PATH + this.dbSettings.DB_NAME;
        if (database == null) {
            //createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    public void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getWritableDatabase();
        } else {
            Log.i(this.getClass().toString(), "Database already exists");
        }
    }

    public DbOpenHelper(Context context) {
        super(context, DbSettings.getInstance().DB_NAME, null, DbSettings.getInstance().getDB_VERSION());
        this.context =context;
        this.dbSettings = DbSettings.getInstance();
        String packageName = context.getPackageName();
        this.dbSettings.DB_PATH = String.format("//data//data//%s//databases//", packageName);
        //createDataBase();
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String currentScript : DbCreateScript.getCreateDataBaseScripts())
        {
            db.execSQL(currentScript);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<newVersion) {
            for (String currentScript : DbCreateScript.getDropTableScripts())
            {
               try {
                   db.execSQL(currentScript);
               }
               catch (Exception e)
                {

                }
            }
            onCreate(db);
        }
    }


}
