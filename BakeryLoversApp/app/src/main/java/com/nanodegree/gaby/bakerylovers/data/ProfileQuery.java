package com.nanodegree.gaby.bakerylovers.data;

import android.provider.ContactsContract;

/**
 * Created by gaby_ on 5/7/2016.
 */

public interface ProfileQuery {
    String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
    int IS_PRIMARY = 1;
}
