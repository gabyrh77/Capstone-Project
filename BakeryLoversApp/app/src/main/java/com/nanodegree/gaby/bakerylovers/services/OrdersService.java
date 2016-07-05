package com.nanodegree.gaby.bakerylovers.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.nanodegree.gaby.bakerylovers.MainApplication;
import com.nanodegree.gaby.bakerylovers.backend.myApi.MyApi;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.CollectionResponseOrderRecord;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.OrderDetailObject;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.OrderRecord;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by gaby_ on 18/6/2016.
 */

public class OrdersService extends IntentService {
    private static final String TAG = "OrdersService";
    public static final String ACTION_GET = "com.nanodegree.gaby.bakerylovers.services.action.GET_ORDERS";
    public static final String ACTION_DELETE = "com.nanodegree.gaby.bakerylovers.services.action.ORDER_DELETE";
    public OrdersService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "called onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET.equals(action)) {
                getOrders();
            }
        }
    }

    private void getOrders() {
        Log.d(TAG, "called get orders");
        MyApi myApiService = ((MainApplication)getApplication()).getAPIService();
        try {
           CollectionResponseOrderRecord ordersWrapper = myApiService.order().list(UserService.getUserSessionId(getBaseContext())).execute();
            Log.d(TAG, "called get orders");
            if (ordersWrapper != null &&  ordersWrapper.getItems() != null) {
                List<OrderRecord> orders = ordersWrapper.getItems();
                Vector<ContentValues> contentValuesVector = new Vector<>(orders.size());
                ArrayList<ContentValues> detailsValuesArray = new ArrayList<>();
                for (OrderRecord orderResponse: orders) {
                    ContentValues orderValues = new ContentValues();
                    orderValues.put(DBContract.OrderEntry.COLUMN_ORDER_ID, orderResponse.getId());
                    orderValues.put(DBContract.OrderEntry.COLUMN_USER_ID, orderResponse.getUserId());
                    if (orderResponse.getDelivered() != null) {
                        orderValues.put(DBContract.OrderEntry.COLUMN_DELIVERED_DATE, orderResponse.getDelivered().getValue());
                    }
                    orderValues.put(DBContract.OrderEntry.COLUMN_PLACED_DATE, orderResponse.getPlaced().getValue());
                    orderValues.put(DBContract.OrderEntry.COLUMN_TOTAL_DELIVERY, orderResponse.getTotalDelivery());
                    orderValues.put(DBContract.OrderEntry.COLUMN_TOTAL_PRICE, orderResponse.getTotalOrder());
                    orderValues.put(DBContract.OrderEntry.COLUMN_ADDRESS, orderResponse.getAddress());

                    //add to vector
                    contentValuesVector.add(orderValues);

                    //content values details
                    if (orderResponse.getDetails() != null) {
                        for (OrderDetailObject detail : orderResponse.getDetails()) {
                            ContentValues detailsValues = new ContentValues();
                            detailsValues.put(DBContract.OrderDetailEntry.COLUMN_ORDER_ID, orderResponse.getId());
                            detailsValues.put(DBContract.OrderDetailEntry.COLUMN_PRODUCT_ID, detail.getProductId());
                            detailsValues.put(DBContract.OrderDetailEntry.COLUMN_AMOUNT, detail.getAmount());
                            detailsValues.put(DBContract.OrderDetailEntry.COLUMN_PRICE_UND, detail.getPrice());
                            detailsValues.put(DBContract.OrderDetailEntry.COLUMN_TOTAL_PRICE, detail.getSubtotal());
                            //add to array list
                            detailsValuesArray.add(detailsValues);
                        }
                    }
                }

                // perform bulk insert
                if ( contentValuesVector.size() > 0 ) {
                    Log.e(TAG, "BULK INSERT ORDERS " + String.valueOf(contentValuesVector.size()));
                    ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                    contentValuesVector.toArray(cvArray);
                    getContentResolver().bulkInsert(DBContract.OrderEntry.CONTENT_URI, cvArray);

                    //order details
                    Log.e(TAG, "BULK INSERT ORDERS DETAILS" + String.valueOf(detailsValuesArray.size()));
                    ContentValues[] cvDetailsArray = new ContentValues[detailsValuesArray.size()];
                    detailsValuesArray.toArray(cvDetailsArray);
                    getContentResolver().bulkInsert(DBContract.OrderDetailEntry.CONTENT_URI, cvDetailsArray);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error trying to get products");
            Log.e(TAG, e.getMessage());
            //SEND BROADCAST CONNECTION FAILED
        }
    }
}
