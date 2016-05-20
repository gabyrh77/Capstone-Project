package com.nanodegree.gaby.bakerylovers.services;

import android.app.IntentService;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by gaby_ on 15/5/2016.
 */
public class GCMInstanceIDListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }
}
