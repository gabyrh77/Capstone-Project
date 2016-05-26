package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

public class ProductDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ProductDetailFragment";
    public static final String ARG_PRODUCT_ID = "param_id";
    private long mProductId;
    private ImageView mProductImage;
    private TextView mProductDescription;
    private TextView mProductPrice;
    private TextView mProductCalories;

    public ProductDetailFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param productId Product Id.
     * @return A new bundle.
     */
    public static Bundle newInstance(long productId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mProductId = getArguments().getLong(ARG_PRODUCT_ID);
        } else {
            mProductId = savedInstanceState.getLong(ARG_PRODUCT_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_PRODUCT_ID, mProductId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        mProductDescription = (TextView) rootView.findViewById(R.id.product_detail_description);
        mProductPrice = (TextView) rootView.findViewById(R.id.product_detail_price);
        mProductCalories = (TextView) rootView.findViewById(R.id.product_detail_calories);
        mProductImage = (ImageView) rootView.findViewById(R.id.product_detail_image);
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
                DBContract.ProductEntry.buildProductUri(mProductId),
                DBContract.ProductEntry.DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(data.getString(DBContract.ProductEntry.COLUMN_NAME_INDEX));
            if (mProductImage !=null && !data.isNull(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX)) {
                Glide.with(getActivity())
                        .load(data.getString(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX))
                        .placeholder(R.drawable.no_image)
                        .into(mProductImage);
            }

            if (mProductCalories !=null && !data.isNull(DBContract.ProductEntry.COLUMN_NUTRITIONAL_VALUE_INDEX)) {
                mProductCalories.setText(String.valueOf(data.getInt(DBContract.ProductEntry.COLUMN_NUTRITIONAL_VALUE_INDEX)));
            }

            if (mProductPrice !=null && !data.isNull(DBContract.ProductEntry.COLUMN_PRICE_INDEX)) {
                mProductPrice.setText(Utils.getCurrencyFormatted(data.getDouble(DBContract.ProductEntry.COLUMN_PRICE_INDEX)));
            }

            if (mProductDescription !=null && !data.isNull(DBContract.ProductEntry.COLUMN_DESCRIPTION_INDEX)) {
                mProductDescription.setText(data.getString(DBContract.ProductEntry.COLUMN_DESCRIPTION_INDEX));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
