/*
package kr.co.leeku.wms.network;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE if not exists scna ("
                + "s_position text,"
                + "s_barcode text,"
                + "s_pltno text,"
                + "s_scanqty text,"
                + "s_fgname text primary key);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists scantable";

        db.execSQL(sql);
        onCreate(db);
    }
}


*/
