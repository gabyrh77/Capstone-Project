package com.nanodegree.gaby.bakerylovers.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.activities.MainActivity;

/**
 * Created by gaby_ on 15/5/2016.
 */
public class GCMListenerService extends GcmListenerService {
    private static final String TAG = "GCMListenerService";
    private static final String ORDER_TOPIC = "orders";
    private static final String PRODUCT_TOPIC = "products";
    private static final String GLOBAL_TOPIC = "global";
    private static final String TOPICS_PREFIX = "/topics/";
    private static final String ARG_FETCH = "needsFetch";
    private static final String ARG_ENDPOINT = "endpoint";
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith(TOPICS_PREFIX)) {
            String topicName = from.substring(8);
            Log.d(TAG, "A notification arrived for the topic "+ topicName);
            if (topicName.equals(GLOBAL_TOPIC)){
                sendNotification(message);
            } else if (topicName.equals(PRODUCT_TOPIC)){
                Intent productsIntent = new Intent(getApplicationContext(), ProductsService.class);
                productsIntent.setAction(ProductsService.ACTION_GET);
                getApplicationContext().startService(productsIntent);
            } else if (topicName.equals(ORDER_TOPIC)){
                Intent ordersIntent = new Intent(getApplicationContext(), OrdersService.class);
                ordersIntent.setAction(OrdersService.ACTION_GET);
                getApplicationContext().startService(ordersIntent);
            }
        } else {
            sendNotification(message);
            if (data.getString(ARG_FETCH, "").equals("true")) {
                if (data.getString(ARG_ENDPOINT, "").equals(PRODUCT_TOPIC)){
                    Intent productsIntent = new Intent(getApplicationContext(), ProductsService.class);
                    productsIntent.setAction(ProductsService.ACTION_GET);
                    getApplicationContext().startService(productsIntent);
                } else if (data.getString(ARG_ENDPOINT, "").equals(ORDER_TOPIC)){
                    Intent ordersIntent = new Intent(getApplicationContext(), OrdersService.class);
                    ordersIntent.setAction(OrdersService.ACTION_GET);
                    getApplicationContext().startService(ordersIntent);
                }
            }
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_orders)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
