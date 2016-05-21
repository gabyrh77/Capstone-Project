package com.nanodegree.gaby.bakerylovers.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class DBProvider extends ContentProvider {
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;
    private SQLiteQueryBuilder mQueryBuilder;
    static final int USERS = 100;
    static final int USER = 101;
    static final int ORDERS = 200;
    static final int ORDER = 201;
    static final int PRODUCTS = 300;
    static final int PRODUCT = 301;
    static final int PRODUCTS_CURRENT_ORDER = 302;
    static final int ORDER_DETAILS = 400;
    static final int ORDER_DETAIL = 401;
    static final int CURRENT_ORDER = 500;
    static final int CURRENT_DETAIL = 501;

    private static UriMatcher buildUriMatcher() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.UserEntry.TABLE_NAME, USERS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.UserEntry.TABLE_NAME + "/#", USER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderEntry.TABLE_NAME, ORDERS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderEntry.TABLE_NAME + "/#", ORDER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.ProductEntry.TABLE_NAME, PRODUCTS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.ProductEntry.TABLE_NAME + "/" + DBContract.ProductEntry.CURRENT_PATH , PRODUCTS_CURRENT_ORDER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.ProductEntry.TABLE_NAME + "/#", PRODUCT);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderDetailEntry.TABLE_NAME, ORDER_DETAILS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderDetailEntry.TABLE_NAME + "/#", ORDER_DETAIL);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.CurrentOrderEntry.TABLE_NAME, CURRENT_ORDER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.CurrentOrderEntry.TABLE_NAME + "/#", CURRENT_DETAIL);
        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        mQueryBuilder = new SQLiteQueryBuilder();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case USERS:
            case USER:
                mQueryBuilder.setTables(DBContract.UserEntry.TABLE_NAME);
                break;
            case ORDERS:
            case ORDER:
                mQueryBuilder.setTables(DBContract.OrderEntry.TABLE_NAME);
                break;
            case PRODUCTS:
            case PRODUCT:
                mQueryBuilder.setTables(DBContract.ProductEntry.TABLE_NAME);
                break;
            case PRODUCTS_CURRENT_ORDER:
                mQueryBuilder.setTables(DBContract.ProductEntry.PRODUCT_CURRENT_JOIN);
                break;
            case ORDER_DETAILS:
            case ORDER_DETAIL:
                mQueryBuilder.setTables(DBContract.OrderDetailEntry.TABLE_NAME);
                break;
            case CURRENT_ORDER:
            case CURRENT_DETAIL:
                mQueryBuilder.setTables(DBContract.CurrentOrderEntry.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor = mQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USERS:
                return  DBContract.UserEntry.CONTENT_TYPE;
            case USER:
                return DBContract.UserEntry.CONTENT_ITEM_TYPE;
            case ORDERS:
                return  DBContract.OrderEntry.CONTENT_TYPE;
            case ORDER:
                return DBContract.OrderEntry.CONTENT_ITEM_TYPE;
            case PRODUCTS:
                return  DBContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT:
                return DBContract.ProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCTS_CURRENT_ORDER:
                return  DBContract.ProductEntry.CONTENT_TYPE;
            case ORDER_DETAILS:
                return  DBContract.OrderDetailEntry.CONTENT_TYPE;
            case ORDER_DETAIL:
                return DBContract.OrderDetailEntry.CONTENT_ITEM_TYPE;
            case CURRENT_ORDER:
                return  DBContract.CurrentOrderEntry.CONTENT_TYPE;
            case CURRENT_DETAIL:
                return DBContract.CurrentOrderEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case USERS: {
                long _id = db.insert(DBContract.UserEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.UserEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ORDERS: {
                long _id = db.insert(DBContract.OrderEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.OrderEntry.buildOrderUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PRODUCTS: {
                long _id = db.insert(DBContract.ProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.ProductEntry.buildProductUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ORDER_DETAILS: {
                long _id = db.insert(DBContract.OrderDetailEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.OrderDetailEntry.buildOrderDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CURRENT_ORDER: {
                long _id = db.insert(DBContract.CurrentOrderEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DBContract.CurrentOrderEntry.buildCurrentOrderByIdUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (returnUri!=null) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (match == CURRENT_ORDER) {
                getContext().getContentResolver().notifyChange(DBContract.ProductEntry.buildProductCurrentUri(), null);
            }
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case USERS:
                rowsDeleted = db.delete(
                        DBContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ORDERS:
                rowsDeleted = db.delete(
                        DBContract.OrderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS:
                rowsDeleted = db.delete(
                        DBContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ORDER_DETAILS:
                rowsDeleted = db.delete(
                        DBContract.OrderDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CURRENT_ORDER:
                rowsDeleted = db.delete(
                        DBContract.CurrentOrderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (match == CURRENT_ORDER) {
                getContext().getContentResolver().notifyChange(DBContract.ProductEntry.buildProductCurrentUri(), null);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        selectionArgs = new String[] {uri.getLastPathSegment()};

        switch (match) {
            case USER:
                selection = DBContract.UserEntry._ID + " = ? ";
                rowsUpdated = db.update(DBContract.UserEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ORDER:
                selection = DBContract.OrderEntry._ID + " = ? ";
                rowsUpdated = db.update(DBContract.OrderEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PRODUCT:
                selection = DBContract.ProductEntry._ID + " = ? ";
                rowsUpdated = db.update(DBContract.ProductEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ORDER_DETAIL:
                selection = DBContract.OrderDetailEntry._ID + " = ? ";
                rowsUpdated = db.update(DBContract.OrderDetailEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CURRENT_DETAIL:
                selection = DBContract.CurrentOrderEntry._ID + " = ? ";
                rowsUpdated = db.update(DBContract.CurrentOrderEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (match == CURRENT_DETAIL) {
                getContext().getContentResolver().notifyChange(DBContract.ProductEntry.buildProductCurrentUri(), null);
            }
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName = null, idColumn = null;

        //determinate table
        switch (match) {
            case USERS:
                tableName = DBContract.UserEntry.TABLE_NAME;
                //idColumn = DBContract.UserEntry.COLUMN_USER_ID;
                break;
            case ORDERS:
                tableName = DBContract.OrderEntry.TABLE_NAME;
               // idColumn = DBContract.OrderEntry.COLUMN_ORDER_ID;
                break;
            case PRODUCTS:
                tableName = DBContract.ProductEntry.TABLE_NAME;
               // idColumn = DBContract.ProductEntry.COLUMN_PRODUCT_ID;
                break;
            case ORDER_DETAILS:
                tableName = DBContract.OrderDetailEntry.TABLE_NAME;
                // idColumn = DBContract.ProductEntry.COLUMN_PRODUCT_ID;
                break;
            case CURRENT_DETAIL:
                tableName = DBContract.CurrentOrderEntry.TABLE_NAME;
                // idColumn = DBContract.ProductEntry.COLUMN_PRODUCT_ID;
                break;
        }

        //perform bulk insert
        if(tableName != null){
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {

                    long _id = db.replace(tableName, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            if (tableName.equals(DBContract.ProductEntry.TABLE_NAME)) {
                getContext().getContentResolver().notifyChange(DBContract.ProductEntry.buildProductCurrentUri(), null);
            }
            return returnCount;
        }else{
            return super.bulkInsert(uri, values);
        }
    }
}
