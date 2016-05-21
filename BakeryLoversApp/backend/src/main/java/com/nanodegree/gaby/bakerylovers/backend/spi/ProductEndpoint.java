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
import com.googlecode.objectify.Key;
import com.nanodegree.gaby.bakerylovers.backend.db.ProductRecord;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.nanodegree.gaby.bakerylovers.backend.db.OfyService.ofy;

/**
 * A products endpoint class we are exposing on the backend
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
public class ProductEndpoint {
    private static final Logger log = Logger.getLogger(ProductEndpoint.class.getName());
    private static final String PRODUCT_TOPIC = "products";

    /**
     * Register a new product to the backend
     *
     * @param name The product's unique name
     * @param description The product's description
     * @param photoUrl The url of the photo for the new product
     * @param price The product's price
     * @param calories The product's description
     * @param available The product's availability
     * @return The product created or null
     */
    @ApiMethod(name = "product.create", httpMethod = ApiMethod.HttpMethod.POST)
    public ProductRecord createProduct(@Named("name") String name, @Named("description") String description, @Named("photoUrl") String photoUrl,
                                     @Named("price") Double price, @Named("calories") Double calories, @Named("available") Boolean available){

        ProductRecord record = findProductByName(name);
        if(record != null) {
            log.info("Product " + name + " already exists, returning null");
            return null;
        }
        record = new ProductRecord();
        record.setDescription(description);
        record.setName(name);
        record.setPhotoUrl(photoUrl);
        record.setCalories(calories);
        record.setPrice(price);
        record.setAvailable(available);
        ofy().save().entity(record).now();
        log.info("Created a new product id" + record.getId());

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new TopicNotificationTask()));

        return record;
    }

    /**
     * Update a product to the backend
     *
     * @param name The product's unique name
     * @param description The product's description
     * @param photoUrl The url of the photo for the new product
     * @param price The product's price
     * @param calories The product's description
     * @param available The product's availability
     * @return The product updated or null
     */
    @ApiMethod(name = "product.update", httpMethod = ApiMethod.HttpMethod.POST)
    public ProductRecord updateProduct(@Named("id") Long id, @Named("name") String name, @Named("description") String description, @Named("photoUrl") String photoUrl,
                                     @Named("price") Double price, @Named("calories") Double calories, @Named("available") Boolean available){

        ProductRecord record = findProductById(id);
        if (record == null) {
            log.info("Product id" + id + " not found, returning null");
            return null;
        } else if (!name.equals(record.getName())){
            ProductRecord productByName = findProductByName(name);
            if(productByName != null) {
                log.info("Product " + name + " already exists, returning null");
                return null;
            }
        }

        record.setDescription(description);
        record.setName(name);
        record.setPhotoUrl(photoUrl);
        record.setCalories(calories);
        record.setPrice(price);
        record.setAvailable(available);
        ofy().save().entity(record).now();
        log.info("Updated product id" + record.getId());

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new TopicNotificationTask()));

        return record;
    }

    /**
     * Return a collection of products
     *
     * @return a list of products
     */
    @ApiMethod(name = "product.list")
    public CollectionResponse<ProductRecord> listProducts() {
        List<ProductRecord> records = ofy().load().type(ProductRecord.class).list();
        log.info("Returned a collection of " + records.size() + " products");
        return CollectionResponse.<ProductRecord>builder().setItems(records).build();
    }

    /**
     * Create default list of products
     *
     */
    @ApiMethod(name = "product.load")
    public void loadProducts() {
        List<Key<ProductRecord>> keys = ofy().load().type(ProductRecord.class).keys().list();
        ofy().delete().keys(keys).now();

        //moka torte
        ProductRecord record1 = new ProductRecord();
        record1.setDescription("Delicious chocolate and coffee cake");
        record1.setName("Moka Torte");
        record1.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/moka_torte.jpg");
        record1.setCalories(300);
        record1.setPrice(5000);
        record1.setAvailable(true);

        //key lime pie
        ProductRecord record2 = new ProductRecord();
        record2.setDescription("Delightfully sweet with the perfect amount of tartness, this creamy bright key lime pie recipe is indulgent comfort in every bite.");
        record2.setName("Key Lime Pie");
        record2.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/key_lime_pie.jpg");
        record2.setCalories(265);
        record2.setPrice(4000);
        record2.setAvailable(true);

        //black forest cake
        ProductRecord record3 = new ProductRecord();
        record3.setDescription("A filling of cherries and kirsch-flavored whipped cream is standard in this classic German cake.");
        record3.setName("Black Forest Cake");
        record3.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/selva_negra.jpg");
        record3.setCalories(400);
        record3.setPrice(6000);
        record3.setAvailable(true);

        ofy().save().entities(record1, record2, record3).now();
        log.info("Created default products");

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new TopicNotificationTask()));
    }

    private ProductRecord findProductByName(String name) {
        return ofy().load().type(ProductRecord.class).filter("name", name).first().now();
    }

    private ProductRecord findProductById(Long id) {
        return ofy().load().type(ProductRecord.class).id(id).now();
    }

    private static class TopicNotificationTask implements DeferredTask {
        @Override
        public void run() {
            try{
                GCMMessageSender sender = new GCMMessageSender();
                sender.sendMessageToTopic(PRODUCT_TOPIC, "Updates on products");
                log.info("Sent products updated notifications");
            }catch (Exception e) {
                log.warning("Error sending product notifications: " + e.getMessage());
            }
        }
    }
}
