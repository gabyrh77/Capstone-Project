package com.nanodegree.gaby.bakerylovers.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.nanodegree.gaby.bakerylovers.backend.myApi.MyApi;

import java.io.IOException;

/**
 * Created by gaby_ on 15/5/2016.
 */
public class APIService {
    private static final String API_URL = "https://capstone-project-8df9f.appspot.com/_ah/api/"; //http://10.0.2.2:8080/_ah/api/
    private static final String APP_NAME = "Bakery Lovers";

    public static MyApi buildAPIService(){
        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setApplicationName(APP_NAME)
                .setRootUrl(API_URL)
               /* .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                })*/;

        return builder.build();
    }
}
