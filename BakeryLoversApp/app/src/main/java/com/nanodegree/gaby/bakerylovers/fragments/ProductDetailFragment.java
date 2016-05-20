package com.nanodegree.gaby.bakerylovers.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.gaby.bakerylovers.R;

public class ProductDetailFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "param_id";
    private int mProductId;

    public ProductDetailFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param productId Product Id.
     * @return A new bundle.
     */
    public static Bundle newBundleInstance(long productId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mProductId = getArguments().getInt(ARG_PRODUCT_ID);
        } else {
            mProductId = savedInstanceState.getInt(ARG_PRODUCT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }
}
