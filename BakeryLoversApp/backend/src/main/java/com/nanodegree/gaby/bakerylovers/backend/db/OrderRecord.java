package com.nanodegree.gaby.bakerylovers.backend.db;

import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

import java.util.List;

/** The Objectify object model for orders we are persisting */
@Entity
public class OrderRecord {

    @Id
    Long id;
    @Index
    private Long userId;
    @Serialize
    private List<OrderDetailObject> details;
    private DateTime delivered;
    private DateTime placed;
    private double totalOrder;
    private double totalDelivery;

    public OrderRecord() {}

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public List<OrderDetailObject> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailObject> details) {
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