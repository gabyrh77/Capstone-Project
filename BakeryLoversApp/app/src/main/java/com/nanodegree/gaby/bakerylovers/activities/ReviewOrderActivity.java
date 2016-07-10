package com.nanodegree.gaby.bakerylovers.activities;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.adapters.CurrentOrderAdapter;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.fragments.ConfirmOrderFragment;
import com.nanodegree.gaby.bakerylovers.fragments.OrderPlacedFragment;
import com.nanodegree.gaby.bakerylovers.fragments.ProductDetailFragment;
import com.nanodegree.gaby.bakerylovers.fragments.ReviewOrderFragment;
import com.nanodegree.gaby.bakerylovers.fragments.UpdateAmountDialogFragment;
import com.nanodegree.gaby.bakerylovers.services.CurrentOrderService;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by goropeza on 04/06/16.
 */

public class ReviewOrderActivity extends AppCompatActivity implements  CurrentOrderAdapter.CurrentOrderAdapterOnClickHandler,
        UpdateAmountDialogFragment.UpdateAmountDialogListener, FragmentManager.OnBackStackChangedListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ReviewOrderActivity";
    private static final int LOADER_CART = 0;
    private static final int REQUEST_LOCATION = 0;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String ARG_SELECTED_FRAGMENT = "ARG_SF";
    private static final String ARG_ORDER_PLACED = "ARG_ORDER_PLACED";
    private static final String ARG_CART_COUNT = "ARG_CART_COUNT";
    private static final String ARG_DELIVER_ADDRESS = "ARG_ADDRESS";
    private static final String TAG_FRAGMENT_CONFIRM_ORDER = "TAG_CONFIRM_ORDER";
    private static final String TAG_FRAGMENT_REVIEW_ORDER = "TAG_REVIEW_ORDER";
    private static final String TAG_FRAGMENT_ORDER_PLACED = "TAG_ORDER_PLACED";
    private static final String TAG_DIALOG_AMOUNT_ORDER = "TAG_DIALOG_ORDER_AMOUNT";
    private CoordinatorLayout mCoordinatorLayout;
    private String mSelectedFragment;
    private String mDeliverLocation;
    private boolean mOrderPlaced;
    private int mCartCount;
    private BroadcastReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_main_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(this);
        getLoaderManager().initLoader(LOADER_CART, null, this);

        if (findViewById(R.id.main_content) != null) {

            if(savedInstanceState != null) {
                mSelectedFragment = savedInstanceState.getString(ARG_SELECTED_FRAGMENT);
                mDeliverLocation = savedInstanceState.getString(ARG_DELIVER_ADDRESS);
                mOrderPlaced = savedInstanceState.getBoolean(ARG_ORDER_PLACED);
                mCartCount = savedInstanceState.getInt(ARG_CART_COUNT, 0);
            } else {
                mSelectedFragment = null;
                mOrderPlaced = false;
                setFragment(TAG_FRAGMENT_REVIEW_ORDER, null);
            }
        }

        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(CurrentOrderService.ACTION_SERVICE_STATUS);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_order, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem placeItem = menu.findItem(R.id.action_place_order);
        MenuItem locationItem = menu.findItem(R.id.action_location);
        if (!mOrderPlaced && (mDeliverLocation == null || mCartCount == 0)) {
            placeItem.setVisible(false);
        } else {
            placeItem.setVisible(true);
        }

        if (mOrderPlaced || mCartCount == 0) {
            locationItem.setVisible(false);
        } else {
            locationItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_location:
                getUserLocation();
                return true;
            case R.id.action_place_order:
                if (mOrderPlaced) {
                    finish();
                } else {
                    handleContinueAction();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mOrderPlaced) {
            finish();
        }
        if(getFragmentManager().getBackStackEntryCount()>1){
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SELECTED_FRAGMENT, mSelectedFragment);
        outState.putString(ARG_DELIVER_ADDRESS, mDeliverLocation);
        outState.putBoolean(ARG_ORDER_PLACED, mOrderPlaced);
        outState.putInt(ARG_CART_COUNT, mCartCount);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_CART:
                return new CursorLoader(this,
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
            case LOADER_CART:
                if (data.moveToFirst()){
                    mCartCount = data.getInt(0);
                } else {
                    mCartCount = 0;
                }
                invalidateOptionsMenu();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void setFragment(String idFragment, Bundle args) {
        Fragment nextFragment;
        int titleId;

        switch (idFragment) {
            case TAG_FRAGMENT_CONFIRM_ORDER:
                titleId = R.string.title_fragment_product;
                nextFragment = new ConfirmOrderFragment();
                break;
            case TAG_FRAGMENT_ORDER_PLACED:
                titleId = R.string.title_fragment_order_placed;
                nextFragment = new OrderPlacedFragment();
                break;
            case TAG_FRAGMENT_REVIEW_ORDER:
            default:
                titleId = R.string.title_fragment_review_order;
                nextFragment = new ReviewOrderFragment();
                break;
        }

        if(args!=null) {
            nextFragment.setArguments(args);
        }

        setTitle(titleId);
        FragmentManager fragmentManager = getFragmentManager();

        if (mSelectedFragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.main_content, nextFragment, idFragment)
                    .commit();
            mSelectedFragment = idFragment;
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, nextFragment, idFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(idFragment)
                    .commit();
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onProductItemClick(long productId) {
        Bundle args = ProductDetailFragment.newInstance(productId);
        Intent productDetail = new Intent(this, ProductDetailActivity.class);
        productDetail.putExtras(args);
        startActivity(productDetail);
    }

    @Override
    public void onAmountItemClick(long productId, int amount) {
        DialogFragment newFragment = UpdateAmountDialogFragment.newInstance(productId, amount);
        newFragment.show(getFragmentManager(), TAG_DIALOG_AMOUNT_ORDER);
    }

    @Override
    public void onDeleteProductClick(long productId) {
        Intent bookIntent = new Intent(this, CurrentOrderService.class);
        bookIntent.putExtra(CurrentOrderService.PRODUCT_ID, productId);
        bookIntent.setAction(CurrentOrderService.ACTION_DELETE);
        startService(bookIntent);
    }

    @Override
    public void onUpdateAmountClick(long productId, int amount) {
        Intent bookIntent = new Intent(this, CurrentOrderService.class);
        bookIntent.putExtra(CurrentOrderService.PRODUCT_ID, productId);
        bookIntent.putExtra(CurrentOrderService.PRODUCT_AMOUNT, amount);
        bookIntent.setAction(CurrentOrderService.ACTION_UPDATE);
        startService(bookIntent);
    }

    private void handleContinueAction() {
        if (mSelectedFragment.equals(TAG_FRAGMENT_REVIEW_ORDER)) {
            if (mDeliverLocation == null) {
                Snackbar.make(mCoordinatorLayout, getString(R.string.text_select_address), Snackbar.LENGTH_LONG).show();
            } else {
                setFragment(TAG_FRAGMENT_CONFIRM_ORDER, ConfirmOrderFragment.newInstanceBundle(mDeliverLocation));
            }
        } else {
            Snackbar.make(mCoordinatorLayout, "Placing order", Snackbar.LENGTH_LONG).show();
            Intent bookIntent = new Intent(this, CurrentOrderService.class);
            bookIntent.putExtra(CurrentOrderService.STR_ADDRESS, mDeliverLocation);
            bookIntent.setAction(CurrentOrderService.ACTION_PLACE);
            startService(bookIntent);
        }
    }

    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            AlertDialog dialog = new AlertDialog.Builder(ReviewOrderActivity.this)
                    .setMessage( R.string.permission_location)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mDeliverLocation = place.getName().toString();
                setFragment(TAG_FRAGMENT_CONFIRM_ORDER, ConfirmOrderFragment.newInstanceBundle(mDeliverLocation));
            }else {
                Snackbar.make(mCoordinatorLayout, getString(R.string.text_select_address), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void getUserLocation() {
        if (!checkLocationPermission()) {
            return;
        }
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackStackChanged() {
        if (mSelectedFragment.equals(TAG_FRAGMENT_CONFIRM_ORDER)) {
            mSelectedFragment = TAG_FRAGMENT_REVIEW_ORDER;
        } else {
            mSelectedFragment = TAG_FRAGMENT_CONFIRM_ORDER;
        }
    }

    //Receive status from Service
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int response = intent.getIntExtra(CurrentOrderService.ARG_STATUS, -1);
            switch (response) {
                case CurrentOrderService.STATUS_NO_NETWORK:
                    Snackbar.make(mCoordinatorLayout, getString(R.string.text_network), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.action_settings), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                }
                            }).show();
                    break;
                case CurrentOrderService.STATUS_ERROR:
                    Snackbar.make(mCoordinatorLayout, getString(R.string.error_order_not_placed), Snackbar.LENGTH_LONG).show();
                    break;
                case CurrentOrderService.STATUS_OK:
                    mOrderPlaced = true;
                    setFragment(TAG_FRAGMENT_ORDER_PLACED, null);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;
            }
        }
    }
}
