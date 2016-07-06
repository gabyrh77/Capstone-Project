package com.nanodegree.gaby.bakerylovers.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gaby_ on 25/5/2016.
 */

public class Utils {

    public static String getCurrencyFormatted(double number){
        NumberFormat mCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));
        return mCurrencyFormat.format(number);
    }

    public static String getDateFormatted(Long datetime) {
        if (datetime == null || datetime == 0) {
            return null;
        }
        Date date = new Date(datetime);
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
    }
}
