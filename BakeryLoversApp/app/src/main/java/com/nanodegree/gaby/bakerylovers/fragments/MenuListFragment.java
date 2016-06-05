package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.gaby.bakerylovers.adapters.MenuListAdapter;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.services.ProductsService;


public class MenuListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MenuListFragment";
    private static final int LOADER_PRODUCTS = 0;
    private static final int LOADER_CART = 1;
    private RecyclerView mRecyclerView;
    private MenuListAdapter mListAdapter;
    private FloatingActionButton mReviewOrderButton;

    public MenuListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");

        // get products on the background
        Intent productsIntent = new Intent(getActivity(), ProductsService.class);
        productsIntent.setAction(ProductsService.ACTION_GET);
        getActivity().startService(productsIntent);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu_list, container, false);
        mReviewOrderButton = (FloatingActionButton) rootView.findViewById(R.id.review_order_button);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_menu_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        int choiceMode = getResources().getInteger(R.integer.item_choice_mode);

        //TODO: add empty view

        // specify an adapter (see also next example)
        mListAdapter = new MenuListAdapter(getActivity(), null, choiceMode);
        mRecyclerView.setAdapter(mListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated ");
        getLoaderManager().initLoader(LOADER_PRODUCTS, null, this);
        getLoaderManager().initLoader(LOADER_CART, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO: register receiver here
        //TODO: if the products were not fetched, try to fetch them again
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO: unregister receiver here
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_PRODUCTS:
                return new CursorLoader(getActivity(),
                        DBContract.ProductEntry.buildProductCurrentUri(),
                        DBContract.ProductEntry.DETAIL_COLUMNS_WITH_CURRENT,
                        null,
                        null,
                        null);
            case LOADER_CART:
                return new CursorLoader(getActivity(),
                        DBContract.CurrentOrderEntry.CONTENT_URI,
                        new String[] {"count(*)"},
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
            case LOADER_PRODUCTS:
                Log.d(TAG, "loading items: " + String.valueOf(data.getCount()));
                if (mListAdapter!=null) {
                    Log.d(TAG, "swapping cursor to adapter");
                    mListAdapter.swapCursor(data);
                }
                break;
            case LOADER_CART:
                if (data.moveToFirst() && mReviewOrderButton != null){
                    int count = data.getInt(0);
                    Log.d(TAG, "current items: " + String.valueOf(count));
                    if (count > 0) {
                        mReviewOrderButton.setVisibility(View.VISIBLE);
                    } else {
                        mReviewOrderButton.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_PRODUCTS:
                if (mListAdapter != null) {
                    mListAdapter.swapCursor(null);
                }
                break;
        }
    }
}
