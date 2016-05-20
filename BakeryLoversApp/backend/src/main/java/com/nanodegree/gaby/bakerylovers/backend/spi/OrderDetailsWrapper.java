package com.nanodegree.gaby.bakerylovers.backend.spi;

import java.util.ArrayList;

class OrderDetailObject {
    long product;
    int amount;
    double price;
    double total;
}
/** The object model for the data we are sending through endpoints */
public class OrderDetailsWrapper {

    private ArrayList<OrderDetailObject> mylist;

    public OrderDetailsWrapper(ArrayList<OrderDetailObject> sData){
        mylist = sData;
    }

    public ArrayList<OrderDetailObject> getMylist() {
        return mylist;
    }

    public void setMylist(ArrayList<OrderDetailObject> mylist) {
        this.mylist = mylist;
    }
}
