package Helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import Entitys.Task;
import db.DbOpenHelper;

public class taskHelper extends baseHelper {

    public taskHelper(Context context, boolean isWriting) {
        super(context, isWriting);
    }

    public ArrayList<Task> getOutletTask(String outletId) {
        ArrayList<Task> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select _id, reference, outletId, number, Description, ResultDescription, status, _send " +
                " from tasks where outletId = ? order by number desc", new String[]{outletId});
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Task task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            task.setReference(cursor.getString(cursor.getColumnIndex("reference")));
            task.setOutletId(cursor.getString(cursor.getColumnIndex("outletId")));
            task.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            task.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
            task.setResultDescription(cursor.getString(cursor.getColumnIndex("ResultDescription")));
            task.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            task.send = cursor.getInt(cursor.getColumnIndex("_send"));
            list.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public void saveTask(Task task) {
       db.execSQL("update  tasks set status = ?, ResultDescription = ?  " +
               " where _id = ?", new String[]{ Integer.toString(task.getStatus()), task.getResultDescription(), Integer.toString(task.getId()) });
    }

    public boolean TaskExists(String outletId) {
        boolean result = false;
        Cursor cursor = db.rawQuery("select count(*) from tasks where outletId = ?", new String[]{outletId});
        cursor.moveToFirst();
        for (int i =0; i<cursor.getCount(); i++) {
            result = cursor.getInt(0) > 0 ;
            break;
        }
        cursor.close();
        return result;
    }

    public boolean UnresolvedTaskExists(String outletId) {
        boolean result = false;
        Cursor cursor = db.rawQuery("select count(*) from tasks where status = 0 and  outletId = ?", new String[]{outletId});
        cursor.moveToFirst();
        for (int i =0; i<cursor.getCount(); i++) {
            result = cursor.getInt(0) > 0 ;
            break;
        }
        cursor.close();
        return result;
    }
}
