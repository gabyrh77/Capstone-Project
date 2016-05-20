package com.nanodegree.gaby.bakerylovers.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

public class DBProvider extends ContentProvider {
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;
    private SQLiteQueryBuilder mUserQueryBuilder;
    private SQLiteQueryBuilder mOrderQueryBuilder;
    private SQLiteQueryBuilder mOrderDetailQueryBuilder;
    private SQLiteQueryBuilder mProductQueryBuilder;
    private SQLiteQueryBuilder mCurrentOrderQueryBuilder;
    static final int USERS = 100;
    static final int USER = 101;
    static final int ORDERS = 200;
    static final int ORDER = 201;
    static final int PRODUCTS = 300;
    static final int PRODUCT = 301;
    static final int ORDER_DETAILS = 400;
    static final int ORDER_DETAIL = 401;
    static final int CURRENT_ORDER = 500;
    static final int CURRENT_DETAIL = 501;

    static UriMatcher buildUriMatcher() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.UserEntry.TABLE_NAME + "/" , USERS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.UserEntry.TABLE_NAME + "/#", USER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderEntry.TABLE_NAME + "/" , ORDERS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderEntry.TABLE_NAME + "/#", ORDER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.ProductEntry.TABLE_NAME + "/" , PRODUCTS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.ProductEntry.TABLE_NAME + "/#", PRODUCT);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderDetailEntry.TABLE_NAME + "/", ORDER_DETAILS);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.OrderDetailEntry.TABLE_NAME + "/#", ORDER_DETAIL);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.CurrentOrderEntry.TABLE_NAME + "/" , CURRENT_ORDER);
        sUriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.CurrentOrderEntry.TABLE_NAME + "/#", CURRENT_DETAIL);
        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());
        mUserQueryBuilder = new SQLiteQueryBuilder();
        mUserQueryBuilder.setTables(
                DBContract.UserEntry.TABLE_NAME);
        mProductQueryBuilder = new SQLiteQueryBuilder();
        mProductQueryBuilder.setTables(
                DBContract.ProductEntry.TABLE_NAME);
        mOrderDetailQueryBuilder = new SQLiteQueryBuilder();
        mOrderDetailQueryBuilder.setTables(
                DBContract.OrderDetailEntry.TABLE_NAME);
        mOrderQueryBuilder = new SQLiteQueryBuilder();
        mOrderQueryBuilder.setTables(
                DBContract.OrderEntry.TABLE_NAME);
        mCurrentOrderQueryBuilder = new SQLiteQueryBuilder();
        mCurrentOrderQueryBuilder.setTables(
                DBContract.CurrentOrderEntry.TABLE_NAME);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case USERS:
            case USER:
                retCursor = mUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ORDERS:
            case ORDER:
                retCursor = mOrderQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCTS:
            case PRODUCT:
                retCursor = mProductQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ORDER_DETAILS:
            case ORDER_DETAIL:
                retCursor = mOrderDetailQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CURRENT_ORDER:
            case CURRENT_DETAIL:
                retCursor = mCurrentOrderQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
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
        }
        return rowsUpdated;
    }
}
