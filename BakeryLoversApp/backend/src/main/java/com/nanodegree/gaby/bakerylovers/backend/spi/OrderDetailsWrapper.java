package com.nanodegree.gaby.bakerylovers.backend.spi;

import com.nanodegree.gaby.bakerylovers.backend.db.OrderDetailObject;

import java.util.ArrayList;

/** The object model for the data we are sending through endpoints */
public class OrderDetailsWrapper {

    public OrderDetailsWrapper() {
    }

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
