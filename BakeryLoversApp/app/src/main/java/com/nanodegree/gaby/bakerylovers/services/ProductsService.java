package com.nanodegree.gaby.bakerylovers.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.nanodegree.gaby.bakerylovers.MainApplication;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.CollectionResponseProductRecord;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.ProductRecord;
import com.nanodegree.gaby.bakerylovers.data.DBContract;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created by gaby_ on 21/5/2016.
 */

public class ProductsService extends IntentService {

    private static final String TAG = "ProductsService";
    public static final String ACTION_GET = "com.nanodegree.gaby.bakerylovers.services.action.GET_PRODUCTS";
    public static final String ACTION_DELETE = "com.nanodegree.gaby.bakerylovers.services.action.DELETE";

    public static final String PRODUCT_ID = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_ID";

    public ProductsService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "called onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET.equals(action)) {
                getProducts();
            }
        }
    }

    private void getProducts() {
        Log.d(TAG, "called get products");
        try {
            CollectionResponseProductRecord productsWrapper = ((MainApplication)getApplication()).getAPIService().product().list().execute();
            Log.d(TAG, "called get products");
            if (productsWrapper != null &&  productsWrapper.getItems() != null) {
                List<ProductRecord> products = productsWrapper.getItems();
                Vector<ContentValues> contentValuesVector = new Vector<>(products.size());
                for (ProductRecord productResponse: products) {
                    ContentValues productValues = new ContentValues();
                    productValues.put(DBContract.ProductEntry.COLUMN_PRODUCT_ID, productResponse.getId());
                    productValues.put(DBContract.ProductEntry.COLUMN_NAME, productResponse.getName());
                    productValues.put(DBContract.ProductEntry.COLUMN_DESCRIPTION, productResponse.getDescription());
                    productValues.put(DBContract.ProductEntry.COLUMN_PHOTO_URL, productResponse.getPhotoUrl());
                    productValues.put(DBContract.ProductEntry.COLUMN_PRICE, productResponse.getPrice());
                    productValues.put(DBContract.ProductEntry.COLUMN_NUTRITIONAL_VALUE, productResponse.getCalories());
                    productValues.put(DBContract.ProductEntry.COLUMN_AVAILABLE, productResponse.getAvailable() ? 1 : 0);

                    //add to vector
                    contentValuesVector.add(productValues);
                }

                // perform bulk insert
                if ( contentValuesVector.size() > 0 ) {
                    Log.e(TAG, "BULK INSTERT PRODUCTS " + String.valueOf(contentValuesVector.size()));
                    ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                    contentValuesVector.toArray(cvArray);
                    getContentResolver().bulkInsert(DBContract.ProductEntry.CONTENT_URI, cvArray);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error trying to get products");
            Log.e(TAG, e.getMessage());
            //SEND BROADCAST CONNECTION FAILED
        }
    }
}
