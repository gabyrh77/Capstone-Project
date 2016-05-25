package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.adapters.CurrentOrderAdapter;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.services.ProductsService;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewOrderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ReviewOrderFragment";
    private RecyclerView mRecyclerView;
    private CurrentOrderAdapter mListAdapter;

    public ReviewOrderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");

        // get products on the background
        Intent productsIntent = new Intent(getActivity(), ProductsService.class);
        productsIntent.setAction(ProductsService.ACTION_GET);
        getActivity().startService(productsIntent);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_review_order, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_order_items);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });

        int choiceMode = getResources().getInteger(R.integer.item_choice_mode);

        // specify an adapter (see also next example)
        mListAdapter = new CurrentOrderAdapter(getActivity(), null, choiceMode);
        mRecyclerView.setAdapter(mListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated ");
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DBContract.CurrentOrderEntry.CONTENT_URI,
                DBContract.CurrentOrderEntry.DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mListAdapter != null) {
            mListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mListAdapter != null) {
            mListAdapter.swapCursor(null);
        }
    }
}