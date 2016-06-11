package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.adapters.ConfirmOrderAdapter;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.services.ProductsService;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ConfirmOrderFragment";
    private static final String ARG_DELIVER_ADDRESS = "arg_deliver_address";
    private static final int LOADER_ITEMS = 1;
    private static final int LOADER_TOTALS = 2;
    private RecyclerView mRecyclerView;
    private TextView mTextAddress;
    private TextView mTextSubtotal;
    private TextView mTextDelivery;
    private TextView mTextTotalOrder;
    private TextView mTextTotalItems;
    private ConfirmOrderAdapter mListAdapter;
    private String mAddress;
    private double mDelivery;
    private double mSubtotal;
    private double mTotalOrder;
    private double mTotalItems;

    public ConfirmOrderFragment() {}

    public static Bundle newInstanceBundle(String address) {
        Bundle args = new Bundle();
        args.putString(ARG_DELIVER_ADDRESS, address);
        return args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");

        getActivity().setTitle(getString(R.string.title_fragment_confirm));

        // get products on the background
        Intent productsIntent = new Intent(getActivity(), ProductsService.class);
        productsIntent.setAction(ProductsService.ACTION_GET);
        getActivity().startService(productsIntent);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_current_order);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // specify an adapter (see also next example)
        mListAdapter = new ConfirmOrderAdapter(getActivity());
        mRecyclerView.setAdapter(mListAdapter);

        mTextAddress = (TextView) rootView.findViewById(R.id.text_address);
        mTextSubtotal = (TextView) rootView.findViewById(R.id.text_subtotal);
        mTextDelivery = (TextView) rootView.findViewById(R.id.text_delivery);
        mTextTotalOrder = (TextView) rootView.findViewById(R.id.text_total_order);
        mTextTotalItems = (TextView) rootView.findViewById(R.id.text_total_items);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated ");
        getLoaderManager().initLoader(LOADER_ITEMS, null, this);
        getLoaderManager().initLoader(LOADER_TOTALS, null, this);
        if (savedInstanceState == null) {
            mAddress = getArguments().getString(ARG_DELIVER_ADDRESS);
        } else {
            mAddress = savedInstanceState.getString(ARG_DELIVER_ADDRESS);
        }
        if (mAddress != null) {
            if (mTextAddress != null && mAddress != null) {
                mTextAddress.setText(mAddress);
            }
            if (mTextDelivery!=null) {
                mDelivery = getDeliveryCost();
                mTextDelivery.setText(Utils.getCurrencyFormatted(mDelivery));
                if (mSubtotal > 0) {
                    mTotalOrder = mDelivery + mSubtotal;
                    mTextTotalOrder.setText(Utils.getCurrencyFormatted(mTotalOrder));
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ITEMS:
                return new CursorLoader(getActivity(),
                    DBContract.CurrentOrderEntry.CONTENT_URI,
                    DBContract.CurrentOrderEntry.DETAIL_COLUMNS,
                    null,
                    null,
                    null);
            case LOADER_TOTALS:
                return new CursorLoader(getActivity(),
                        DBContract.CurrentOrderEntry.CONTENT_URI,
                        DBContract.CurrentOrderEntry.TOTAL_ORDER_COLUMN,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ITEMS:
                if (mListAdapter != null) {
                    mListAdapter.swapCursor(data);
                }
                break;
            case LOADER_TOTALS:
                if (data != null && data.moveToFirst()) {
                    mSubtotal = data.getDouble(1);
                    mTotalItems = data.getInt(0);
                    if (mTextSubtotal!=null) {
                        mTextSubtotal.setText(Utils.getCurrencyFormatted(mSubtotal));
                        if (mDelivery > 0) {
                            mTotalOrder = mDelivery + mSubtotal;
                            mTextTotalOrder.setText(Utils.getCurrencyFormatted(mTotalOrder));
                        }
                    }
                    if (mTextTotalItems != null) {
                        mTextTotalItems.setText(String.format("%.0f", mTotalItems));
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ITEMS:
                if (mListAdapter != null) {
                    mListAdapter.swapCursor(null);
                }
                break;
        }
    }

    private double getDeliveryCost() {
        return 2000;
    }
}