package com.nanodegree.gaby.bakerylovers.backend.db;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
import java.util.List;

/** The Objectify object model for orders we are persisting */
@Entity
public class OrderRecord {

    @Id
    Long id;
    @Index
    private Long userId;
    private List<OrderDetailObject> details;
    private Date delivered;
    private Date placed;
    private double totalOrder;
    private double totalDelivery;
    private String address;

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

    public Date getDelivered() {
        return delivered;
    }

    public void setDelivered(Date delivered) {
        this.delivered = delivered;
    }

    public Date getPlaced() {
        return placed;
    }

    public void setPlaced(Date placed) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}