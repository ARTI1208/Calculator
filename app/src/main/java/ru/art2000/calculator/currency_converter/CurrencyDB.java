package ru.art2000.calculator.currency_converter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ru.art2000.extensions.CurrencyValues;

public class CurrencyDB extends SQLiteOpenHelper {

    public CurrencyDB(Context context) {
        super(context, "currency.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS currency");
        onCreate(db);
    }

    void writeUpdatedValuesToDB(){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("position", -1);
        db.update("currency", cv, null, null);
        for (int i = 0, size = CurrencyValues.visibleList.size(); i < size; i++) {
            cv = new ContentValues();
            Log.d(CurrencyValues.visibleList.get(i).code +" v", String.valueOf(CurrencyValues.visibleList.get(i).position));
            cv.put("position", CurrencyValues.visibleList.get(i).position);
            db.update("currency", cv, "codeLetter = ?", new String[]{CurrencyValues.visibleList.get(i).code});
        }
    }

    public int getSize(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cc = db.query("info", null, null, null, null, null, null);
        int ret = 0;
        if (cc.moveToFirst())
            ret = cc.getInt(cc.getColumnIndex("size"));
        cc.close();
        return ret;
    }

    public String getRefreshDate(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cc = db.query("info", null, null, null, null, null, null);
        String date = "";
        if (cc.moveToFirst())
            date = cc.getString(cc.getColumnIndex("date"));
        cc.close();
        return date;
    }

    public void putRefreshDate(String date){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        db.update("info", cv, "size = ?", new String[]{String.valueOf(getSize())});
    }
}