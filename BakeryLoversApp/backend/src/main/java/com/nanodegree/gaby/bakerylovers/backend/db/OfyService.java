package com.nanodegree.gaby.bakerylovers.backend.db;

import com.nanodegree.gaby.bakerylovers.backend.db.OrderDetailRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.OrderRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.ProductRecord;
import com.nanodegree.gaby.bakerylovers.backend.db.RegistrationRecord;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.nanodegree.gaby.bakerylovers.backend.db.UserRecord;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {

    static {
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(UserRecord.class);
        ObjectifyService.register(ProductRecord.class);
        ObjectifyService.register(OrderRecord.class);
        ObjectifyService.register(OrderDetailRecord.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
