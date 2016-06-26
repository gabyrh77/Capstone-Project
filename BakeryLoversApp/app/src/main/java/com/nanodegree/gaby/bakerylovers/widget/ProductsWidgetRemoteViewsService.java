package com.nanodegree.gaby.bakerylovers.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by gaby_ on 25/6/2016.
 */

public class ProductsWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ProductsWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
