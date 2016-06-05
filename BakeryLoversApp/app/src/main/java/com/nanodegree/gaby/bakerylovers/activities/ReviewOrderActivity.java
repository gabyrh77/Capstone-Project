package com.nanodegree.gaby.bakerylovers.activities;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.adapters.CurrentOrderAdapter;
import com.nanodegree.gaby.bakerylovers.fragments.ConfirmOrderFragment;
import com.nanodegree.gaby.bakerylovers.fragments.ProductDetailFragment;
import com.nanodegree.gaby.bakerylovers.fragments.ReviewOrderFragment;
import com.nanodegree.gaby.bakerylovers.fragments.UpdateAmountDialogFragment;
import com.nanodegree.gaby.bakerylovers.services.CurrentOrderService;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by goropeza on 04/06/16.
 */

public class ReviewOrderActivity extends AppCompatActivity implements  CurrentOrderAdapter.CurrentOrderAdapterOnClickHandler, UpdateAmountDialogFragment.UpdateAmountDialogListener{
    private static final String TAG = "ReviewOrderActivity";
    private static final int REQUEST_LOCATION = 0;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String ARG_SELECTED_FRAGMENT = "ARG_SF";
    private static final String TAG_FRAGMENT_CONFIRM_ORDER = "TAG_CONFIRM_ORDER";
    private static final String TAG_FRAGMENT_REVIEW_ORDER = "TAG_REVIEW_ORDER";
    private static final String TAG_DIALOG_AMOUNT_ORDER = "TAG_DIALOG_ORDER_AMOUNT";
    private String mSelectedFragment;
    private String mDeliverLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.main_content) != null) {

            if(savedInstanceState != null) {
                mSelectedFragment = savedInstanceState.getString(ARG_SELECTED_FRAGMENT);
            } else {
                mSelectedFragment = null;
                setFragment(TAG_FRAGMENT_REVIEW_ORDER, null);
            }
        }
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
        if (mDeliverLocation == null) {
            placeItem.setVisible(false);
        } else {
            placeItem.setVisible(true);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>1){
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    private void setFragment(String idFragment, Bundle args) {
        Fragment nextFragment;
        int titleId;

        switch (idFragment) {
            case TAG_FRAGMENT_CONFIRM_ORDER:
                titleId = R.string.title_fragment_product;
                nextFragment = new ConfirmOrderFragment();
                break;
            case TAG_FRAGMENT_REVIEW_ORDER:
            default:
                titleId = R.string.title_fragment_review_order;
                nextFragment = new ReviewOrderFragment();
                break;
        }

        if(args!=null){
            nextFragment.setArguments(args);
        }

        setTitle(titleId);
        FragmentManager fragmentManager = getFragmentManager();

        if (mSelectedFragment == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.main_content, nextFragment, idFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, nextFragment, idFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(idFragment)
                    .commit();
        }

        mSelectedFragment = idFragment;
        invalidateOptionsMenu();
    }

    @Override
    public void onProductItemClick(long productId) {
        Bundle args = ProductDetailFragment.newInstance(productId);

       /* if(findViewById(R.id.main_detail_content) != null){
            id = R.id.main_detail_content;
            setFragment(R.id.nav_product_detail, id, args);
        }*/

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
        Log.d(TAG, "Remove from cart the item: " + String.valueOf(productId));
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
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_LOCATION);
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
                setFragment(TAG_FRAGMENT_CONFIRM_ORDER, null);
            }else {
                Toast.makeText(getApplicationContext(), "Please choose you deliver location", Toast.LENGTH_LONG).show();
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
}
