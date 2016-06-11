package com.nanodegree.gaby.bakerylovers.backend.db;

/**
 * Created by goropeza on 11/06/16.
 */

public class OrderDetailObject {
    private long productId;
    private int amount;
    private double price;
    private double subtotal;

    public OrderDetailObject(long prod, int amountProd, double priceProd){
        productId = prod;
        amount = amountProd;
        price = priceProd;
    }

    public long getProductId() {
        return productId;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
