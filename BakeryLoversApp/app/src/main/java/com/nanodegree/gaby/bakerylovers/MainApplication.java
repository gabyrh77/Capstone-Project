package com.nanodegree.gaby.bakerylovers;

import android.app.Application;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.nanodegree.gaby.bakerylovers.backend.myApi.MyApi;

/**
 * Created by goropeza on 11/06/16.
 */

public class MainApplication extends Application {
    private static final String API_URL = "https://capstone-project-8df9f.appspot.com/_ah/api/"; //http://10.0.2.2:8080/_ah/api/
    private static final String APP_NAME = "Bakery Lovers";
    private MyApi mApiService;
    @Override
    public void onCreate() {
        super.onCreate();

        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setApplicationName(APP_NAME)
                .setRootUrl(API_URL)
               /* .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                })*/;

        mApiService = builder.build();
    }

    public MyApi getAPIService() {
        return mApiService;
    }
}
