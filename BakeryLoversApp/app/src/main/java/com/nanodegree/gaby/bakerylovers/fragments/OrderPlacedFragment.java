package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.gaby.bakerylovers.R;

/**
 * Created by gaby_ on 5/7/2016.
 */

public class OrderPlacedFragment extends Fragment {

    public OrderPlacedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_fragment_order_placed));
        return inflater.inflate(R.layout.fragment_order_placed, container, false);
    }
}
