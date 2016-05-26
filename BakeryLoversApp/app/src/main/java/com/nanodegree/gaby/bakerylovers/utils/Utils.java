package com.nanodegree.gaby.bakerylovers.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by gaby_ on 25/5/2016.
 */

public class Utils {

    public static String getCurrencyFormatted(double number){
        NumberFormat mCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));
        return mCurrencyFormat.format(number);
    }
}
