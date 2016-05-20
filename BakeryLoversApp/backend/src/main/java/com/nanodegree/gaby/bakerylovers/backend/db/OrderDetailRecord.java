package com.nanodegree.gaby.bakerylovers.backend.db;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/** The Objectify object model for order details we are persisting */
@Entity
public class OrderDetailRecord {

    @Id
    Long id;

    @Index
    private String generatedId;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<OrderRecord> order;
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<ProductRecord> product;
    private int amount;
    private double price;
    private double total;

    public OrderDetailRecord() {}

    public Long getId() {
        return id;
    }

    public Key<OrderRecord> getOrder() {
        return order;
    }

    public void setOrder(Key<OrderRecord> order) {
        this.order = order;
    }

    public String getGeneratedId() {
        return generatedId;
    }

    public void setGeneratedId() {
        this.generatedId = String.valueOf(getOrderId()) + "-" + String.valueOf(getProductId());
    }

    public Long getProductId() {
        return product==null?null:product.getId();
    }

    public Long getOrderId() {
        return  order==null? null:order.getId();
    }

    public void setProduct(Key<ProductRecord> product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}