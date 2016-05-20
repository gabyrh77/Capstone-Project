package com.nanodegree.gaby.bakerylovers.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.gaby.bakerylovers.adapters.MenuListAdapter;
import com.nanodegree.gaby.bakerylovers.R;


public class MenuListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MenuListAdapter mListAdapter;

    public MenuListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_menu_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_menu_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        int choiceMode = getResources().getInteger(R.integer.item_choice_mode);

        // specify an adapter (see also next example)
        mListAdapter = new MenuListAdapter(getActivity(), choiceMode);
        mRecyclerView.setAdapter(mListAdapter);

        return rootView;
    }
}
