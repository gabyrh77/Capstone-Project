package com.nanodegree.gaby.bakerylovers.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

/**
 * Created by gaby_ on 26/6/2016.
 */

public class ProductsWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "WidgetRemoteVFactory";
    private Cursor mCursor;
    private Context mContext;
    private int mAppWidgetId;

    public ProductsWidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mCursor = null;
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(DBContract.ProductEntry.CONTENT_URI,
                DBContract.ProductEntry.DETAIL_COLUMNS,
                DBContract.ProductEntry.COLUMN_AVAILABLE + " = 1",
                null,
                null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        final RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.item_widget_list);
        views.setTextViewText(R.id.widget_item_product_name_text, mCursor.getString(DBContract.ProductEntry.COLUMN_NAME_INDEX));
        views.setTextColor(R.id.widget_item_product_name_text, mContext.getResources().getColor(R.color.textPrimary));
        views.setTextViewText(R.id.widget_item_product_price_text,
                mContext.getString(R.string.label_price_with_var, Utils.getCurrencyFormatted(mCursor.getDouble(DBContract.ProductEntry.COLUMN_PRICE_INDEX))));
        views.setTextColor(R.id.widget_item_product_price_text, mContext.getResources().getColor(R.color.textGray));
        views.setTextViewText(R.id.widget_item_product_nutritional_text,
                mContext.getString(R.string.label_calories_with_var, String.format("%,d%n", mCursor.getInt(DBContract.ProductEntry.COLUMN_PRICE_INDEX))));
        views.setTextColor(R.id.widget_item_product_nutritional_text, mContext.getResources().getColor(R.color.textGray));

        String photoUrl = (mCursor.getString(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX));
        if(photoUrl != null) {
            try {
                Bitmap imageBitmap = Glide.with(mContext)
                        .load(photoUrl)
                        .asBitmap()
                        .fitCenter()
                        .into(150, 150)
                        .get();
                views.setImageViewBitmap(R.id.widget_item_product_image, imageBitmap);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        Bundle extras = new Bundle();
        extras.putLong(ProductsWidgetProvider.EXTRA_ID, mCursor.getLong(DBContract.ProductEntry.COLUMN_PRODUCT_ID_INDEX));
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_item_view, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(0);
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
