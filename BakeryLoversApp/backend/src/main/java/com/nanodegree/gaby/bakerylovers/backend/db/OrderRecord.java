package com.nanodegree.gaby.bakerylovers.backend.db;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

/** The Objectify object model for orders we are persisting */
@Entity
public class OrderRecord {

    @Id
    Long id;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserRecord> user;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Load List<Ref<OrderDetailRecord>> details = new ArrayList<>();
    private DateTime delivered;
    private DateTime placed;
    private double totalOrder;
    private double totalDelivery;

    public OrderRecord() {}

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return user==null?null:user.getId();
    }

    public void setUser(UserRecord user) {
        this.user = Key.create(user);
    }

    public List<Ref<OrderDetailRecord>> getDetails() {
        return details;
    }

    public void setDetails(List<Ref<OrderDetailRecord>> details) {
        this.details = details;
    }

    public DateTime getDelivered() {
        return delivered;
    }

    public void setDelivered(DateTime delivered) {
        this.delivered = delivered;
    }

    public DateTime getPlaced() {
        return placed;
    }

    public void setPlaced(DateTime placed) {
        this.placed = placed;
    }

    public double getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(double totalOrder) {
        this.totalOrder = totalOrder;
    }

    public double getTotalDelivery() {
        return totalDelivery;
    }

    public void setTotalDelivery(double totalDelivery) {
        this.totalDelivery = totalDelivery;
    }
}