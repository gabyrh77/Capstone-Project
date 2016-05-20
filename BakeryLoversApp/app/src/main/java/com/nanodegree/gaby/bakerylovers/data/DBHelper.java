package com.nanodegree.gaby.bakerylovers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "bakery_lovers.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(DBContract.UserEntry.SQL_CREATE_USER_TABLE);
        db.execSQL(DBContract.ProductEntry.SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(DBContract.OrderEntry.SQL_CREATE_ORDER_TABLE);
        db.execSQL(DBContract.OrderDetailEntry.SQL_CREATE_ORDER_DETAIL_TABLE);
        db.execSQL(DBContract.CurrentOrderEntry.SQL_CREATE_CURRENT_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
