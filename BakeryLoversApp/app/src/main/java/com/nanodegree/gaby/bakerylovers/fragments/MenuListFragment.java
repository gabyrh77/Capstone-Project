package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.nanodegree.gaby.bakerylovers.adapters.MenuListAdapter;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;

public class MenuListFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MenuListFragment";
    private static final int LOADER_PRODUCTS = 0;
    private static final int LOADER_CART = 1;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private SearchView mSearchView;
    private MenuListAdapter mListAdapter;
    private FloatingActionButton mReviewOrderButton;
    private String mSearchString;

    public MenuListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the options menu from XML
        inflater.inflate(R.menu.menu_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_list, container, false);
        mEmptyView = rootView.findViewById(R.id.empty_menu_view);
        mReviewOrderButton = (FloatingActionButton) rootView.findViewById(R.id.review_order_button);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_menu_view);
        mRecyclerView.setHasFixedSize(true);

        int columnCount = getResources().getInteger(R.integer.grid_columns);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnCount));

        mListAdapter = new MenuListAdapter(getActivity(), mEmptyView);
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
                String whereString = null;
                String[] whereArgs = null;
                if (mSearchString != null && !mSearchString.isEmpty()) {
                    whereString = DBContract.ProductEntry.COLUMN_NAME +" LIKE ? OR " + DBContract.ProductEntry.COLUMN_DESCRIPTION + " LIKE ? ";
                    whereArgs = new String[]{"%"+mSearchString+"%", "%"+mSearchString+"%"};
                }
                return new CursorLoader(getActivity(),
                        DBContract.ProductEntry.buildProductCurrentUri(),
                        DBContract.ProductEntry.DETAIL_COLUMNS_WITH_CURRENT,
                        whereString,
                        whereArgs,
                        DBContract.ProductEntry.DEFAULT_ORDER);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchString = newText;
        getLoaderManager().restartLoader(LOADER_PRODUCTS, null, this);
        return true;
    }
}
