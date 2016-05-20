/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.nanodegree.gaby.bakerylovers.backend.db.UserRecord;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.nanodegree.gaby.bakerylovers.backend.db.OfyService.ofy;

/**
 * A users endpoint class we are exposing for authentication on the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
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
public class UserEndpoint {
    private SecureRandom random = new SecureRandom();
    private static final Logger log = Logger.getLogger(UserEndpoint.class.getName());

    /**
     * Register a user to the backend
     *
     * @param email The email to register
     * @param name The name to register
     * @param password The password to register
     * @param phone The prone to register
     * @return The registered user, or null of the user already exists
     */
    @ApiMethod(name = "user.register", httpMethod = ApiMethod.HttpMethod.POST)
    public UserRecord register(@Named("email") String email, @Named("name") String name,
                               @Named("password") String password, @Named("phone") String phone) {
        if(findUserByEmail(email) != null) {
            log.info("User " + email + " already registered, skipping register");
            return null;
        }
        UserRecord user = new UserRecord();
        user.setEmail(email);
        user.setFullName(name);
        user.setPhoneNumber(phone);
        user.setPassword(password);
        ofy().save().entity(user).now();
        log.info("Created a new user id" + user.getId() + " with the email " + user.getEmail());
        return user;
    }

    /**
     * Login a user from the backend
     *
     * @param email The email to login
     * @param password The password to login
     * @return The Id and token of the logged in user, or null of the login was not successful
     */
    @ApiMethod(name = "user.login", httpMethod = ApiMethod.HttpMethod.POST)
    public UserRecord login(@Named("email") String email, @Named("password") String password) {
        UserRecord user = findUserByEmail(email);
        if(user == null) {
            log.info("User " + email + " not registered, skipping unregister");
            return null;
        } else if (user.getPassword().equals(password)) {
            user.setLoginToken(generateSessionId());
            ofy().save().entity(user).now();
            log.info("User " + email + " logged in");
            return user;
        } else {
            log.info("User " + email + " not logged in, password incorrect");
            return null;
        }
    }

    /**
     * Login a user google account from the backend
     *
     * @param email The email from the google account
     * @param name The name to login
     * @param token The name to login
     * @param phone The name to login
     * @return The logged in user, or null of the login was not successful
     */
    @ApiMethod(name = "user.loginGoogle", httpMethod = ApiMethod.HttpMethod.POST)
    public UserRecord loginGoogle(@Named("email") String email, @Named("name") String name,
                                 @Named("token") String token, @Named("phone") String phone) {
        UserRecord user = findUserByEmail(email);
        if(user == null) {
            log.info("User " + email + " not registered, register as a google account");
            user = new UserRecord();
            user.setEmail(email);
            user.setFullName(name);
            user.setPhoneNumber(phone);
            user.setLoginToken(token);
            user.setGoogleAccount(true);
        } else {
            log.info("User " + email + " already exists, update as a google account");
            user.setGoogleAccount(true);
            user.setLoginToken(token);
        }
        ofy().save().entity(user).now();
        return user;
    }

    /**
     * Logout a user from the backend
     *
     * @param token The user's token
     * @return the user logout or null
     */
    @ApiMethod(name = "user.logout")
    public UserRecord logout(@Named("token") String token) {
        UserRecord user = findUserByToken(token);
        if(user == null) {
            log.info("User logout: " + token + " token was invalid");
        } else {
            user.setLoginToken(null);
            log.info("User logout was successful for " + user.getEmail());
            ofy().save().entity(user).now();
        }
        return user;
    }

    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of registered users
     */
    @ApiMethod(name = "user.list")
    public CollectionResponse<UserRecord> listUsers(@Named("count") int count) {
        List<UserRecord> records = ofy().load().type(UserRecord.class).limit(count).list();
        log.info("listUsers: Returned a collection of " + records.size() + " users");
        return CollectionResponse.<UserRecord>builder().setItems(records).build();
    }

    private UserRecord findUserByEmail(String email) {
        return ofy().load().type(UserRecord.class).filter("email", email).first().now();
    }

    private UserRecord findUserByToken(String token) {
        return ofy().load().type(UserRecord.class).filter("loginToken", token).first().now();
    }

    private String generateSessionId() {
        return new BigInteger(130, random).toString(32);
    }

}
