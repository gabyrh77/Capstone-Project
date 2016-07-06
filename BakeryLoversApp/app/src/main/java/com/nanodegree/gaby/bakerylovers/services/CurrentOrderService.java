package com.nanodegree.gaby.bakerylovers.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nanodegree.gaby.bakerylovers.MainApplication;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.OrderDetailObject;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.OrderDetailsWrapper;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.OrderRecord;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CurrentOrderService extends IntentService{
    private static final String TAG = "CurrentOrderService";
    public static final String ARG_STATUS = "com.nanodegree.gaby.bakerylovers.services.extra.CURRENT_ORDER_SERVICE_STATUS";
    public static final String ACTION_SERVICE_STATUS = "com.nanodegree.gaby.bakerylovers.services.action.CURRENT_ORDER_SERVICE_STATUS";
    public static final String ACTION_ADD = "com.nanodegree.gaby.bakerylovers.services.action.ADD_TO_ORDER";
    public static final String ACTION_UPDATE = "com.nanodegree.gaby.bakerylovers.services.action.UPDATE_ORDER";
    public static final String ACTION_PLACE = "com.nanodegree.gaby.bakerylovers.services.action.PLACE_ORDER";
    public static final String ACTION_DELETE = "com.nanodegree.gaby.bakerylovers.services.action.DELETE_FROM_ORDER";
    public static final String STR_ADDRESS = "com.nanodegree.gaby.bakerylovers.services.extra.STR_ADDRESS";
    public static final String PRODUCT_ID = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_ID_ORDER";
    public static final String PRODUCT_PRICE = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_PRICE_ORDER";
    public static final String PRODUCT_AMOUNT = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_AMOUNT_ORDER";
    private Long userId;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_NO_NETWORK, STATUS_ERROR, STATUS_OK})
    public @interface CurrentOrderServiceStatus {}
    public static final int STATUS_NO_NETWORK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_OK = 2;


    public CurrentOrderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            userId = UserService.getUserId(getApplicationContext());
            final String action = intent.getAction();
            if (ACTION_PLACE.equals(action)) {
                final String address = intent.getStringExtra(STR_ADDRESS);
                placeOrder(address);
            }
            else {
                final long productId = intent.getLongExtra(PRODUCT_ID, 0);
                if (productId > 0) {
                    if (ACTION_ADD.equals(action)) {
                        addToOrder(productId, intent.getDoubleExtra(PRODUCT_PRICE, 0));
                    } else if (ACTION_UPDATE.equals(action)) {
                        updateProductOrder(productId, intent.getIntExtra(PRODUCT_AMOUNT, 1));
                    } else if (ACTION_DELETE.equals(action)) {
                        deleteFromOrder(productId);
                    }
                }
            }
        }
    }

    private void placeOrder(String address) {
        boolean success = false;
        Cursor mCurrentOrder = getContentResolver().query(DBContract.CurrentOrderEntry.CONTENT_URI,
                DBContract.CurrentOrderEntry.DETAIL_COLUMNS,
                DBContract.CurrentOrderEntry.COLUMN_USER_ID + "=" + userId, null, null);
        if (mCurrentOrder != null && mCurrentOrder.getCount() > 0) {
            ArrayList<OrderDetailObject> detailList = new ArrayList<>();
            for (int i = 0; i < mCurrentOrder.getCount(); i++) {
                mCurrentOrder.moveToPosition(i);
                OrderDetailObject detail = new OrderDetailObject();
                detail.setAmount(mCurrentOrder.getInt(DBContract.CurrentOrderEntry.COLUMN_AMOUNT_INDEX));
                detail.setPrice(mCurrentOrder.getDouble(DBContract.CurrentOrderEntry.COLUMN_PRICE_UND_INDEX));
                detail.setProductId(mCurrentOrder.getLong(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID_INDEX));
                detailList.add(detail);
            }
            mCurrentOrder.close();
            OrderDetailsWrapper wrapper = new OrderDetailsWrapper();
            wrapper.setMylist(detailList);
            try {

                OrderRecord order = ((MainApplication)getApplication()).getAPIService().order().create(
                        UserService.getUserSessionId(getBaseContext()),
                        address, false, wrapper).execute();
                if (order != null) {
                    success = true;
                    deleteCurrentOrder();
                    addNewOrder(order);
                }
            }catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }

        if (success) {
            sendStatusBroadcast(STATUS_OK);
        } else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                sendStatusBroadcast(STATUS_ERROR);
            } else {
                sendStatusBroadcast(STATUS_NO_NETWORK);
            }
        }
    }

    private void deleteCurrentOrder() {
        try {
            getContentResolver().delete(DBContract.CurrentOrderEntry.CONTENT_URI, DBContract.CurrentOrderEntry.COLUMN_USER_ID + "=" + userId, null);
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void addNewOrder(OrderRecord order) {
        try {
            ContentValues values = new ContentValues();
            values.put(DBContract.OrderEntry.COLUMN_ORDER_ID, order.getId());
            values.put(DBContract.OrderEntry.COLUMN_TOTAL_DELIVERY, order.getTotalDelivery());
            values.put(DBContract.OrderEntry.COLUMN_USER_ID, order.getUserId());
            values.put(DBContract.OrderEntry.COLUMN_ADDRESS, order.getAddress());
            values.put(DBContract.OrderEntry.COLUMN_TOTAL_PRICE, order.getTotalOrder());
            values.put(DBContract.OrderEntry.COLUMN_PLACED_DATE, order.getPlaced().getValue());
            getContentResolver().insert(DBContract.OrderEntry.CONTENT_URI, values);

            List<OrderDetailObject> details = order.getDetails();
            if (details != null && details.size() > 0) {
                Vector<ContentValues> contentValuesVector = new Vector<>(details.size());
                for (OrderDetailObject detailResponse: details) {
                    ContentValues productValues = new ContentValues();
                    productValues.put(DBContract.OrderDetailEntry.COLUMN_PRODUCT_ID, detailResponse.getProductId());
                    productValues.put(DBContract.OrderDetailEntry.COLUMN_ORDER_ID, order.getId());
                    productValues.put(DBContract.OrderDetailEntry.COLUMN_AMOUNT, detailResponse.getAmount());
                    productValues.put(DBContract.OrderDetailEntry.COLUMN_PRICE_UND, detailResponse.getPrice());
                    productValues.put(DBContract.OrderDetailEntry.COLUMN_TOTAL_PRICE, detailResponse.getSubtotal());

                    //add to vector
                    contentValuesVector.add(productValues);
                }

                // perform bulk insert
                if ( contentValuesVector.size() > 0 ) {
                    Log.e(TAG, "BULK INSERT ORDER DETAILS " + String.valueOf(contentValuesVector.size()));
                    ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                    contentValuesVector.toArray(cvArray);
                    getContentResolver().bulkInsert(DBContract.OrderDetailEntry.CONTENT_URI, cvArray);
                }
            }
        }catch(Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void deleteFromOrder(long id) {
        try {
            getContentResolver().delete(DBContract.CurrentOrderEntry.CONTENT_URI,
                    DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID + " = ? AND " + DBContract.CurrentOrderEntry.COLUMN_USER_ID + "= ?",
                    new String[] {String.valueOf(id), userId.toString()});
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void addToOrder(long id, double price) {
        try {
            ContentValues values = new ContentValues();
            values.put(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID, id);
            values.put(DBContract.CurrentOrderEntry.COLUMN_USER_ID, userId);
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
            values.put(DBContract.CurrentOrderEntry.COLUMN_AMOUNT, amount);
            getContentResolver().update(DBContract.CurrentOrderEntry.CONTENT_URI, values,
                    DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID + " = ? AND " + DBContract.CurrentOrderEntry.COLUMN_USER_ID + "= ?",
                    new String[] {String.valueOf(id), userId.toString()});
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void sendStatusBroadcast(@CurrentOrderServiceStatus int status){
        Intent messageIntent = new Intent(ACTION_SERVICE_STATUS);
        messageIntent.putExtra(ARG_STATUS, status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
    }
}
