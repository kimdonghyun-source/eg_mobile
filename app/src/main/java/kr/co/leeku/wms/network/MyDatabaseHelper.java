package kr.co.leeku.wms.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //사용 DB  20220307
    private Context context;
    private static final String DATABASE_NAME = "ShipScan4.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "ShipScan";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_POSITION = "s_position";
    private static final String COLUMN_BARCODE = "s_barcode";
    private static final String COLUMN_PLTNO = "s_pltno";
    private static final String COLUMN_SCANQTY = "s_scanqty";
    private static final String COLUMN_FGNAME = "s_fgname";
    private static final String COLUMN_MAC = "s_mac";
    private static final String COLUMN_WGT = "s_wgt";
    private static final String COLUMN_PNO = "p_no";


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME
                //+ " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " (" + COLUMN_POSITION + " TEXT, "
                + COLUMN_BARCODE + " TEXT primary key, "
                + COLUMN_PLTNO + " TEXT, "
                + COLUMN_SCANQTY + " FLOAT, "
                + COLUMN_FGNAME + " TEXT, "
                + COLUMN_MAC + " TEXT, "
                + COLUMN_WGT + " INTEGER, "
                + COLUMN_PNO + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addBook(int position, String barcode, String plt, float scan, String fg, String mac, int wg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_POSITION, position);
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_PLTNO, plt);
        cv.put(COLUMN_SCANQTY, scan);
        cv.put(COLUMN_FGNAME, fg);
        cv.put(COLUMN_MAC, mac);
        cv.put(COLUMN_WGT, wg);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            //Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "데이터 추가 성공", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean insertData(int position, String barcode, String plt, float scan, String fg, String mac, float wg, String pno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_POSITION, position);
        contentValues.put(COLUMN_BARCODE, barcode);
        contentValues.put(COLUMN_PLTNO, plt);
        contentValues.put(COLUMN_SCANQTY, scan);
        contentValues.put(COLUMN_FGNAME, fg);
        contentValues.put(COLUMN_MAC, mac);
        contentValues.put(COLUMN_WGT, wg);
        contentValues.put(COLUMN_PNO, pno);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.d("데이터추가성공", "ㄴㄴㄴ");
            return false;
        } else {
            Log.d("데이터추가성공", "ㅇㅇㅇ");
            return true;
        }
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty, s_fgname, s_mac, s_position, s_wgt, p_no from ShipScan ", null);

        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getData(String fg_nm) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty, s_fgname, s_mac, s_position, s_wgt, p_no from ShipScan where s_fgname='" + fg_nm + "' order by s_barcode", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getwg() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno, s_wgt from ShipScan ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getbarplt() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_barcode, s_pltno from ShipScan ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getplt() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno from ShipScan ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getbarcode() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_barcode from ShipScan ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Pcode 개수 체크
    public Cursor getPcodeChk(String no) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty, s_fgname, s_mac, s_position, s_wgt, p_no from ShipScan where p_no='" + no + "' ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getQty(String fg_nm) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_scanqty from ShipScan where s_fgname='" + fg_nm + "' ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getChangeWgt() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno, s_wgt from ShipScan group by s_pltno, s_wgt ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getSum() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty, s_fgname, s_mac, s_position, s_wgt from ShipScan ", null);
        //
        return res;
    }

    //데이터베이스 항목 읽어오기 Read
    public Cursor getCount(String p_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select s_pltno, s_barcode, s_scanqty  from "+TABLE_NAME,null);
        Cursor res = db.rawQuery("select count(*) from ShipScan where p_no='" + p_no + "' ", null);
        //
        return res;
    }

    // 데이터베이스 삭제하기
    public Integer deleteData(String bar) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "s_barcode = ? ", new String[]{bar});
        //return db.delete("delete from ShipScan where s_barcode=" + COLUMN_BARCODE);
    }

    // 데이터베이스 삭제하기
    public Integer deleteDatas() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "", new String[]{});
        //return db.delete("delete from ShipScan");
    }

    // 데이터베이스 삭제하기(p_no 안맞으면 지우기)
    public Integer deleteData_pno(String pno) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "p_no = ? ", new String[]{pno});
        //return db.delete("delete from ShipScan");
    }

    //데이터베이스 수정하기
    public boolean updateData(String plt, int wg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_WGT, wg);

        db.update(TABLE_NAME, contentValues, "s_pltno = ?", new String[]{plt});
        return true;
    }


}