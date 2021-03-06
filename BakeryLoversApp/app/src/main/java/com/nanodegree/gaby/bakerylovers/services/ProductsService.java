package com.nanodegree.gaby.bakerylovers.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.nanodegree.gaby.bakerylovers.MainApplication;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.CollectionResponseProductRecord;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.ProductRecord;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.UserRecord;
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
    public static final String ACTION_DELETE = "com.nanodegree.gaby.bakerylovers.services.action.PRODUCT_DELETE";
    public static final String PRODUCT_ID = "com.nanodegree.gaby.bakerylovers.services.extra.PRODUCT_ID";
    private SharedPreferences mSharedPref;

    public ProductsService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mSharedPref = getApplicationContext().getSharedPreferences(
                    getApplicationContext().getString(R.string.preference_session_file_key), Context.MODE_PRIVATE);
            final String action = intent.getAction();
            if (ACTION_GET.equals(action)) {
                getProducts();
            }
        }
    }

    private void getProducts() {
        try {
            CollectionResponseProductRecord productsWrapper = ((MainApplication)getApplication()).getAPIService().product().list().execute();
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
                    Log.d(TAG, "BULK INSERT PRODUCTS " + String.valueOf(contentValuesVector.size()));
                    ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
                    contentValuesVector.toArray(cvArray);
                    getContentResolver().bulkInsert(DBContract.ProductEntry.CONTENT_URI, cvArray);
                }
            }
            savePendingUpdateStatus(false);
        } catch (IOException e) {
            Log.e(TAG, "Error trying to get products");
            Log.e(TAG, e.getMessage());
            savePendingUpdateStatus(true);
        }
    }

    private void savePendingUpdateStatus(boolean status) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(getApplicationContext().getString(R.string.pref_pending_products_update_key), status);
        editor.commit();
    }

}
