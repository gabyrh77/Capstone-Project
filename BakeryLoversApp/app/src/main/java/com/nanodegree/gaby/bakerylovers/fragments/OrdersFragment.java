package com.nanodegree.gaby.bakerylovers.fragments;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.adapters.OrderListAdapter;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.services.UserService;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private OrderListAdapter mListAdapter;
    private View mEmptyView;
    private Long userID;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        userID = UserService.getUserId(getActivity());
        mEmptyView = rootView.findViewById(R.id.empty_order_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_orders);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListAdapter = new OrderListAdapter(getActivity(), mEmptyView);
        mRecyclerView.setAdapter(mListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DBContract.OrderEntry.CONTENT_URI,
                DBContract.OrderEntry.DETAIL_COLUMNS,
                DBContract.OrderEntry.COLUMN_USER_ID + " = ?",
                new String[] {userID.toString()},
                DBContract.OrderEntry.COLUMN_PLACED_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }
}
