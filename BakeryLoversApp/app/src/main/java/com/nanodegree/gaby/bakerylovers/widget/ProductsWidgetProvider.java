package com.nanodegree.gaby.bakerylovers.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.activities.MainActivity;
import com.nanodegree.gaby.bakerylovers.data.DBContract;

/**
 * Created by gaby_ on 25/6/2016.
 */

public class ProductsWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "WidgetProvider";
    public static final String EXTRA_ID = DBContract.CONTENT_AUTHORITY + ".WIDGET_EXTRA_ID";
    public static final String ACTION_PRODUCT_DETAIL = DBContract.CONTENT_AUTHORITY + ".ACTION_PRODUCT_DETAIL";
    private static final String ACTION_DATA_UPDATED =
            DBContract.CONTENT_AUTHORITY + "." + DBContract.ProductEntry.TABLE_NAME + ".ACTION_DATA_UPDATED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            Intent intentService = new Intent(context, ProductsWidgetRemoteViewsService.class);
            intentService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intentService.setData(Uri.parse(intentService.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(),  R.layout.products_widget_layout);
            // Set up the collection
            views.setRemoteAdapter(R.id.products_widget_list, intentService);
            views.setEmptyView(R.id.products_widget_list, R.id.products_widget_empty);

            // Create an Intent to launch MainActivity from the widget title view
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.products_widget, pendingIntent);

            //set up pending intent for child views
            Intent detailIntent = new Intent(context, ProductsWidgetProvider.class);
            detailIntent.setAction(ProductsWidgetProvider.ACTION_PRODUCT_DETAIL);
            detailIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent detailPendingIntent = PendingIntent.getBroadcast(context, 0, detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.products_widget_list, detailPendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.products_widget_list);
        } else if (ACTION_PRODUCT_DETAIL.equals(intent.getAction())) {
            long productId = intent.getLongExtra(EXTRA_ID, 0);
            Intent detailIntent = new Intent(context, MainActivity.class);
            detailIntent.putExtra(MainActivity.ARG_PRODUCT_ID, productId);
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(detailIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
}
