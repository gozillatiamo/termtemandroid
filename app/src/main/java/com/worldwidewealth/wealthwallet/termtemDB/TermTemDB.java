package com.worldwidewealth.wealthwallet.termtemDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.worldwidewealth.wealthwallet.model.NotiPayRequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;

/**
 * Created by user on 18-Jan-17.
 */

public class TermTemDB extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "termtem_db.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase sqLiteDatabase;
    public static final String TAG = TermTemDB.class.getSimpleName();

    public TermTemDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_UPLOAD_CASH_IN = String.format("CREATE TABLE %s " +
                        "(ID integer PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",
                TermTemTable.UploadCashIn.TABLE_NAME,
                TermTemTable.UploadCashIn.Column.REQUEST,
                TermTemTable.UploadCashIn.Column.RESPONSE,
                TermTemTable.UploadCashIn.Column.RETRY);
        db.execSQL(TABLE_UPLOAD_CASH_IN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long InsertLogCashIn(NotiPayRequestModel model){
        String request = new Gson().toJson(model);
        Log.e(TAG, "cash in request: "+request);
        sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TermTemTable.UploadCashIn.Column.REQUEST, request);
        values.put(TermTemTable.UploadCashIn.Column.RESPONSE, "");
        values.put(TermTemTable.UploadCashIn.Column.RETRY, 0);
        long id = sqLiteDatabase.insert(TermTemTable.UploadCashIn.TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return id;
    }

    public void UpdateResponseLogCashIn(long id, ResponseModel model){
        String request = new Gson().toJson(model);
        Log.e(TAG, "cash in response: "+request);
        sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TermTemTable.UploadCashIn.Column.REQUEST, request);
        values.put(TermTemTable.UploadCashIn.Column.RESPONSE, "");
        values.put(TermTemTable.UploadCashIn.Column.RETRY, 0);
        sqLiteDatabase.update(TermTemTable.UploadCashIn.TABLE_NAME, values, "ID = ?",new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
    }

}
