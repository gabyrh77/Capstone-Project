/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Named;

import static com.nanodegree.gaby.bakerylovers.backend.db.OfyService.ofy;

/**
 * An endpoint to send messages to devices registered with the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
  name = "myApi",
  version = "v1",
  clientIds = {Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID},
  audiences = {Ids.ANDROID_AUDIENCE},
  namespace = @ApiNamespace(
    ownerDomain = "backend.bakerylovers.gaby.nanodegree.com",
    ownerName = "backend.bakerylovers.gaby.nanodegree.com",
    packagePath=""
  )
)
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());

    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    @ApiMethod(name = "message.send")
    public void sendMessage(@Named("message") String message) throws IOException {
        if(message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        GCMMessageSender sender = new GCMMessageSender();
        sender.sendMessageToAll(message);
    }
}
