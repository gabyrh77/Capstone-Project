package com.nanodegree.gaby.bakerylovers.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nanodegree.gaby.bakerylovers.data.DBContract;

public class CurrentOrderService extends IntentService{
    private static final String TAG = "CurrentOrderService";
    public static final String ACTION_ADD = "com.nanodegree.gaby.bakerylovers.services.action.ADD_TO_ORDER";
    public static final String ACTION_DELETE = "com.nanodegree.gaby.bakerylovers.services.action.DELETE_FROM_ORDER";
    public static final String PRODUCT_ID = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_ID_ORDER";
    public static final String PRODUCT_PRICE = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_PRICE_ORDER";
    public CurrentOrderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final long productId = intent.getLongExtra(PRODUCT_ID, 0);
            if (productId > 0) {
                if (ACTION_ADD.equals(action)) {
                    addToOrder(productId, intent.getDoubleExtra(PRODUCT_PRICE, 0));
                } else if (ACTION_DELETE.equals(action)) {
                    deleteFromOrder(productId);
                }
            }
        }
    }

    private void deleteFromOrder(long id) {
        try {
            getContentResolver().delete(DBContract.CurrentOrderEntry.CONTENT_URI, DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID + " = ?", new String[] {String.valueOf(id)});
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void addToOrder(long id, double price) {
        try {
            ContentValues values = new ContentValues();
            values.put(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID, id);
            values.put(DBContract.CurrentOrderEntry.COLUMN_AMOUNT, 1);
            values.put(DBContract.CurrentOrderEntry.COLUMN_PRICE_UND, price);
            getContentResolver().insert(DBContract.CurrentOrderEntry.CONTENT_URI, values);
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void updateProductOrder(long id, int amount){
        try {
            ContentValues values = new ContentValues();
            values.put(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID, id);
            values.put(DBContract.CurrentOrderEntry.COLUMN_AMOUNT, amount);
            getContentResolver().update(DBContract.CurrentOrderEntry.CONTENT_URI, values, DBContract.CurrentOrderEntry._ID + " = ?", new String[] {String.valueOf(id)});
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }
}
