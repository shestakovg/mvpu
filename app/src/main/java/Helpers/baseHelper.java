package Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;

import db.DbOpenHelper;

public class baseHelper implements Closeable {
    protected Context context;
    protected SQLiteDatabase db;

    public baseHelper(Context context, boolean isWriting) {
        this.context = context;
        DbOpenHelper dbOpenHelper = new DbOpenHelper(this.context);
        if (isWriting) {
            this.db = dbOpenHelper.getWritableDatabase();
        } else {
            this.db = dbOpenHelper.getReadableDatabase();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.db != null) {
            if (db.isOpen()) {
                db.close();
            }
        }
    }
}
