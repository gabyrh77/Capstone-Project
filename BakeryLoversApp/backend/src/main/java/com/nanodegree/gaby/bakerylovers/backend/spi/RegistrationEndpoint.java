/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.googlecode.objectify.Key;
import com.nanodegree.gaby.bakerylovers.backend.db.RegistrationRecord;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.nanodegree.gaby.bakerylovers.backend.db.UserRecord;

import java.util.List;
import java.util.logging.Logger;
import javax.inject.Named;

import static com.nanodegree.gaby.bakerylovers.backend.db.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
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
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    /**
     * Register a device to the backend
     *
     * @param regId The Google Cloud Messaging registration Id to add
     * @param token The token of the user
     */
    @ApiMethod(name = "device.register")
    public void registerDevice(@Named("regId") String regId, @Named("token") String token) {
        UserRecord user = findUserByToken(token);
        if (user==null) {
            log.info("registerDevice: Invalid token " + token + ", returning null");
            return;
        }
        if(findRecord(regId) != null) {
            log.info("Device " + regId + " already registered, skipping register");
            return;
        }
        RegistrationRecord record = new RegistrationRecord();
        record.setRegId(regId);
        record.setUserId(user.getId());
        ofy().save().entity(record).now();
        log.info("Device " + regId + " registered was successful");
        log.info("Device " + regId + " linked to user " + user.getId());
    }

    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "device.unregister")
    public void unregisterDevice(@Named("regId") String regId) {
        RegistrationRecord record = findRecord(regId);
        if(record == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
        log.info("Device " + regId + " unregistered was successful");
    }

    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "device.list")
    public CollectionResponse<RegistrationRecord> listDevices(@Named("count") int count) {
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(count).list();
        log.info("listDevices: Returned a collection of " + records.size() + " devices");
        return CollectionResponse.<RegistrationRecord>builder().setItems(records).build();
    }

    private UserRecord findUserByToken(String token) {
        return ofy().load().type(UserRecord.class).filter("loginToken", token).first().now();
    }

    private RegistrationRecord findRecord(String regId) {
        return ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
    }

}
