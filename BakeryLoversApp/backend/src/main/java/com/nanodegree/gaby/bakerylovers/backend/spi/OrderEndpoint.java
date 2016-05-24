/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.nanodegree.gaby.bakerylovers.backend.db.OrderDetailRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.OrderRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.ProductRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.UserRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.nanodegree.gaby.bakerylovers.backend.db.OfyService.ofy;

/**
 * A orders endpoint class we are exposing on the backend
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
public class OrderEndpoint {
    private static final Logger log = Logger.getLogger(OrderEndpoint.class.getName());
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);

    /**
     * Register an order to the backend
     *
     * @param token The user's login token
     * @param detailRecords The details of the order to create
     * @param address The address of the order to create
     * @param save The address will be save to the user profile
     * @return The new order created, or null
     */
    @ApiMethod(name = "order.create", httpMethod = ApiMethod.HttpMethod.POST)
    public OrderRecord createOrder(@Named("token") String token, OrderDetailsWrapper detailRecords,
                                   @Named("address") String address, @Named("save") Boolean save) {
        UserRecord user = findUserByToken(token);
        if (user == null) {
            log.info("createOrder: Invalid token " + token + ", returning null");
            return null;
        }
        List<OrderDetailObject> details = detailRecords.getMylist();
        if (details==null || details.size()==0) {
            log.info("createOrder: Empty details, returning null");
            return null;
        }
        OrderRecord record = new OrderRecord();
        record.setUser(user);
        //get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        record.setPlaced(new DateTime(cal.getTime(), cal.getTimeZone()));
        ofy().save().entity(record).now();

        List<Ref<OrderDetailRecord>> listRefDetails = new ArrayList<>();
        double orderTotal = 0;
        for (OrderDetailObject detail:details){
            OrderDetailRecord detailRecord = new OrderDetailRecord();
            detailRecord.setOrder(Key.create(OrderRecord.class, record.getId()));
            detailRecord.setProduct(Key.create(ProductRecord.class, detail.product));
            detailRecord.setPrice(detail.price);
            detailRecord.setAmount(detail.amount);
            detailRecord.setTotal(detail.price * detail.amount);
            detailRecord.setGeneratedId();
            ofy().save().entity(detailRecord).now();
            listRefDetails.add(Ref.create(detailRecord));
            orderTotal += detailRecord.getTotal();
        }
        record.setDetails(listRefDetails);
        record.setTotalDelivery(setDeliveryCost(address));
        record.setTotalOrder(orderTotal + record.getTotalDelivery());
        ofy().save().entity(record).now();
        log.info("Created a new order id" + record.getId() + " for the user " + user.getEmail());

        if (save) {
            user.setAddress(address);
            ofy().save().entity(user).now();
            log.info("Updated the address for the user " + user.getEmail());
        }

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new DeliveryNotificationTask(user, record))
                .etaMillis(System.currentTimeMillis() + 5000));

        return record;
    }

    /**
     * Return a collection of orders for a user
     *
     * @param token The user's login token
     * @return a list of user orders or null
     */
    @ApiMethod(name = "order.list")
    public CollectionResponse<OrderRecord> listOrders(@Named("token") String token) {
        UserRecord record = findUserByToken(token);
        if (record==null) {
            log.info("listOrders: Invalid token " + token + ", returning null");
            return null;
        }
        List<OrderRecord> records = ofy().load().type(OrderRecord.class)
                .filter("user", Key.create(record)).list();
        log.info("listOrders: Returned a collection of " + records.size() + " orders for the user " + record.getEmail());
        return CollectionResponse.<OrderRecord>builder().setItems(records).build();
    }

    private UserRecord findUserByToken(String token) {
        return ofy().load().type(UserRecord.class).filter("loginToken", token).first().now();
    }

    private double setDeliveryCost(String address){
        // for now just return an static amount
        return 2000;
    }

    private static class DeliveryNotificationTask implements DeferredTask {
        private UserRecord mUser;
        private OrderRecord mOrder;

        public DeliveryNotificationTask(UserRecord user, OrderRecord orderRecord){
            mOrder = orderRecord;
            mUser = user;
        }

        @Override
        public void run() {
            try{
                GCMMessageSender sender = new GCMMessageSender();
                sender.sendMessageToUser("Your order will arrive in less than 5 minutes. Enjoy our delicious products and come back soon.", mUser.getId());
                OrderRecord orderUpdated = ofy().load().type(OrderRecord.class).id(mOrder.getId()).now();
                //get current date time with Calendar()
                Calendar cal = Calendar.getInstance();
                orderUpdated.setDelivered(new DateTime(cal.getTime(), cal.getTimeZone()));
                ofy().save().entity(orderUpdated).now();
                log.info("Sent delivery notification for the user " + mUser.getEmail());
            }catch (Exception e) {
                log.warning("Error sending product notifications: " + e.getMessage());
            }
        }
    }
}