package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.googlecode.objectify.Key;
import com.nanodegree.gaby.bakerylovers.backend.db.RegistrationRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.UserRecord;

import java.io.IOException;
import java.util.logging.Logger;

import static com.nanodegree.gaby.bakerylovers.backend.db.OfyService.ofy;

/**
 * Created by gaby_ on 16/5/2016.
 */
public class GCMMessageSender {
    private static final Logger log = Logger.getLogger(GCMMessageSender.class.getName());
    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");
    private static final String GLOBAL_TOPIC = "global";
    private Sender mSender;

    public GCMMessageSender(){
        mSender = new Sender(API_KEY);
    }

    public void sendMessageToAll(String message) throws IOException{
        sendMessageToTopic(GLOBAL_TOPIC, message);
    }

    public void sendMessageToUser(String message, Long userId) throws IOException{
        Message msg = new Message.Builder().addData("message", message).build();
        RegistrationRecord record = ofy().load().type(RegistrationRecord.class).filter("user", Key.create(UserRecord.class, userId)).first().now();
        if (record != null) {
            Result result = mSender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                }
                else {
                    log.warning("Error when sending message : " + error);
                }
            }
        } else {
            log.warning("Error sending message to the user id : " + userId + ", not found");
        }
    }

    public void sendMessageToTopic(String topic, String message) throws IOException{
        Message msg = new Message.Builder().addData("message", message).build();
        Result result = mSender.send(msg, "/topics/" + topic, 5);
        if (result.getMessageId() != null) {
            log.info("Message sent to topic: " + topic);
        } else {
            String error = result.getErrorCodeName();
            log.warning("Error when sending message : " + error);

        }
    }
}
