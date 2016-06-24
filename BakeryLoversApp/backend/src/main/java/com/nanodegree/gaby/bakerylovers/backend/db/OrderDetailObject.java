package com.nanodegree.gaby.bakerylovers.backend.db;

/**
 * Created by goropeza on 11/06/16.
 */

public class OrderDetailObject {
    private long productId;
    private int amount;
    private double price;
    private double subtotal;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
