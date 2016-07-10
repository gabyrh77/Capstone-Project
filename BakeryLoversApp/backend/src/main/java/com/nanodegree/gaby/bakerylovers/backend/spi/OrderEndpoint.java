/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.google.android.gcm.server.Message;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.nanodegree.gaby.bakerylovers.backend.db.OrderDetailObject;
import com.nanodegree.gaby.bakerylovers.backend.db.OrderRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.UserRecord;

import java.util.Calendar;
import java.util.List;
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
  clientIds = {Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID, Ids.ANDROID_CLIENT_ID_RELEASE},
  audiences = {Ids.ANDROID_AUDIENCE},
  namespace = @ApiNamespace(
    ownerDomain = "backend.bakerylovers.gaby.nanodegree.com",
    ownerName = "backend.bakerylovers.gaby.nanodegree.com",
    packagePath=""
  )
)
public class OrderEndpoint {
    private static final Logger log = Logger.getLogger(OrderEndpoint.class.getName());
    private static final int DELAY_DELIVERY_NOTIFICATION = 60000;
    private static final String ORDER_TOPIC = "orders";
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
        record.setUserId(user.getId());
        record.setAddress(address);

        Calendar cal = Calendar.getInstance();
        record.setPlaced(cal.getTime());
        ofy().save().entity(record).now();

        double orderTotal = 0;
        for (OrderDetailObject detail:details){
            detail.setSubtotal(detail.getPrice() * detail.getAmount());
            orderTotal += detail.getSubtotal();
        }
        record.setDetails(details);
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
        queue.add(TaskOptions.Builder.withPayload(new DeliveryNotificationTask(record.getId()))
                .etaMillis(System.currentTimeMillis() + DELAY_DELIVERY_NOTIFICATION));

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
                .filter("userId", record.getId()).list();
        log.info("listOrders: Returned a collection of " + records.size() + " orders for the user " + record.getEmail());
        return CollectionResponse.<OrderRecord>builder().setItems(records).build();
    }

    /**
     * Delete all orders
     *
     */
    @ApiMethod(name = "order.deleteAll")
    public void deleteOrders() {
        Iterable<Key<OrderRecord>> allKeys = ofy().load().type(OrderRecord.class).keys();
        ofy().delete().keys(allKeys);
    }

    private UserRecord findUserByToken(String token) {
        return ofy().load().type(UserRecord.class).filter("loginToken", token).first().now();
    }

    private double setDeliveryCost(String address){
        // for now just return an static amount
        return 2000;
    }

    private static class DeliveryNotificationTask implements DeferredTask {
        private Long mOrderId;

        public DeliveryNotificationTask(Long orderId){
            mOrderId = orderId;
        }

        @Override
        public void run() {
            try{
                OrderRecord orderUpdated = ofy().load().type(OrderRecord.class).id(mOrderId).now();
                if (orderUpdated != null) {
                    //Updated order as delivered
                    Calendar cal = Calendar.getInstance();
                    orderUpdated.setDelivered(cal.getTime());
                    ofy().save().entity(orderUpdated).now();

                    Message msg = new Message.Builder()
                            .addData("message", "Your order will arrive soon. Enjoy our delicious products and come back soon.")
                            .addData("needsFetch", "true")
                            .addData("endpoint", ORDER_TOPIC)
                            .build();
                    GCMMessageSender sender = new GCMMessageSender();
                    sender.sendMessageToUser(msg, orderUpdated.getUserId());
                    log.info("Sent delivery notification for the userId " + orderUpdated.getUserId().toString());
                } else {
                    log.warning("Error sending order notifications, order not found");
                }
            }catch (Exception e) {
                log.warning("Error sending order notifications: " + e.getMessage());
            }
        }
    }
}
