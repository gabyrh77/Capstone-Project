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
        record.setActive(true);
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
        boolean created = false;
        ProductRecord record;
        //moka torte
        if (findProductByName("Moka Torte") == null) {
            record = new ProductRecord();
            record.setDescription("Delicious chocolate and coffee cake");
            record.setName("Moka Torte");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/moka_torte.jpg");
            record.setCalories(300);
            record.setPrice(5000);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        //key lime pie
        if (findProductByName("Key Lime Pie") == null) {
            record = new ProductRecord();
            record.setDescription("Delightfully sweet with the perfect amount of tartness, this creamy bright key lime pie recipe is indulgent comfort in every bite.");
            record.setName("Key Lime Pie");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/key_lime_pie.jpg");
            record.setCalories(265);
            record.setPrice(4000);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        //black forest cake
        if (findProductByName("Black Forest Cake") == null) {
            record = new ProductRecord();
            record.setDescription("A filling of cherries and kirsch-flavored whipped cream is standard in this classic German cake.");
            record.setName("Black Forest Cake");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/selva_negra.jpg");
            record.setCalories(400);
            record.setPrice(6000);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        //cinnamon roll
        if (findProductByName("Cinnamon Roll") == null) {
            record = new ProductRecord();
            record.setDescription("Fluffy, soft, doughy, and bursting with buttery cinnamon swirls.");
            record.setName("Cinnamon Roll");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/cinnamon_roll.jpg");
            record.setCalories(200);
            record.setPrice(1000);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        //white bread
        if (findProductByName("White Bread") == null) {
            record = new ProductRecord();
            record.setDescription("A delicious bread with a very light center with crunchy crust.");
            record.setName("White Bread");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/white_bread.jpg");
            record.setCalories(400);
            record.setPrice(2000);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        //passion fruit mousse
        if (findProductByName("Passion Fruit Mousse") == null) {
            record = new ProductRecord();
            record.setDescription("Curtis Stone combines seductively fragrant passion fruit with light-as-air meringue to make mouthwateringly creamy mousses.");
            record.setName("Passion Fruit Mousse");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/maracuya_mousse.jpg");
            record.setCalories(400);
            record.setPrice(2000);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        //cachitos
        if (findProductByName("Ham Horn Bread") == null) {
            record = new ProductRecord();
            record.setDescription("Pillowy soft and sweetish yeasted bread filled with delicious minced ham.");
            record.setName("Ham Horn Bread");
            record.setPhotoUrl("https://dl.dropboxusercontent.com/u/92082104/cahitos.jpg");
            record.setCalories(300);
            record.setPrice(1500);
            record.setAvailable(true);
            record.setActive(true);
            ofy().save().entity(record).now();
            created = true;
        }

        log.info("Created default products");
        if (created) {
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withPayload(new TopicNotificationTask()));
        }
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
