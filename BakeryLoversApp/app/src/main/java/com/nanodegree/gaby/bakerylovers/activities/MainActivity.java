package com.nanodegree.gaby.bakerylovers.activities;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nanodegree.gaby.bakerylovers.adapters.CurrentOrderAdapter;
import com.nanodegree.gaby.bakerylovers.adapters.MenuListAdapter;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.fragments.MenuListFragment;
import com.nanodegree.gaby.bakerylovers.fragments.OrdersFragment;
import com.nanodegree.gaby.bakerylovers.fragments.ProductDetailFragment;
import com.nanodegree.gaby.bakerylovers.fragments.ReviewOrderFragment;
import com.nanodegree.gaby.bakerylovers.fragments.UpdateAmountDialogFragment;
import com.nanodegree.gaby.bakerylovers.services.CurrentOrderService;
import com.nanodegree.gaby.bakerylovers.services.GCMRegistrationService;
import com.nanodegree.gaby.bakerylovers.services.UserService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserService.UserServiceListener, MenuListAdapter.MenuListAdapterOnClickHandler,
        CurrentOrderAdapter.CurrentOrderAdapterOnClickHandler, UpdateAmountDialogFragment.UpdateAmountDialogListener{

    private static final String TAG = "MainActivity";
    private static final String ARG_SELECTED_FRAGMENT = "ARG_SF";
    private static final String TAG_FRAGMENT_LIST_MENU = "TAG_LIST_MENU";
    private static final String TAG_FRAGMENT_ORDERS = "TAG_ORDERS";
    private static final String TAG_FRAGMENT_DETAIL = "TAG_DETAIL";
    private static final String TAG_FRAGMENT_REVIEW_ORDER = "TAG_REVIEW_ORDER";
    private static final String TAG_DIALOG_AMOUNT_ORDER = "TAG_DIALOG_ORDER_AMOUNT";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int mSelectedFragment;
    private UserService mUserService;
    private NavigationView mNavigationView;
    private CoordinatorLayout mCoordinatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserService = new UserService(this, this);
        mCoordinatorView = (CoordinatorLayout) findViewById(R.id.main_coordinator_view);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState!=null) {
            mSelectedFragment = savedInstanceState.getInt(ARG_SELECTED_FRAGMENT);
        } else {
            mSelectedFragment = R.id.nav_main;
        }

        setFragment(mSelectedFragment, R.id.main_content, null);
        mNavigationView.setCheckedItem(mSelectedFragment);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, GCMRegistrationService.class);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void setTitle(int titleId) {
        if(titleId!=0) {
            String title = getString(titleId);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    private void setFragment(int idFragment,int container, Bundle args) {
        Fragment nextFragment;
        int titleId;
        String tag;

        switch (idFragment) {

            case R.id.nav_orders:
                tag = TAG_FRAGMENT_ORDERS;
                titleId = R.string.title_fragment_order;
                nextFragment = new OrdersFragment();
                break;
            case R.id.nav_product_detail:
                tag = TAG_FRAGMENT_DETAIL;
                titleId = R.string.title_fragment_product;
                nextFragment = new ProductDetailFragment();
                break;
            case R.id.nav_review_order:
                tag = TAG_FRAGMENT_REVIEW_ORDER;
                titleId = R.string.title_fragment_review_order;
                nextFragment = new ReviewOrderFragment();
                break;
            case R.id.nav_main:
            default:
                tag = TAG_FRAGMENT_LIST_MENU;
                titleId = R.string.title_fragment_main;
                nextFragment = new MenuListFragment();
                break;
        }

        if(args!=null){
            nextFragment.setArguments(args);
        }

        mSelectedFragment = idFragment;
        setTitle(titleId);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(container, nextFragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  else {
            if(getFragmentManager().getBackStackEntryCount()>1){
                getFragmentManager().popBackStack();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mUserService.isLoggedIn()) {
            openLoginActivity();
            finish();
        } else {
            View headerView = mNavigationView.getHeaderView(0);
            if (headerView!=null) {
                TextView navNameText = (TextView) headerView.findViewById(R.id.nav_name_text);
                TextView navEmailText = (TextView) headerView.findViewById(R.id.nav_email_text);
                if (navNameText != null) {
                    navNameText.setText(mUserService.getUserName());
                }
                if (navEmailText!=null){
                    navEmailText.setText(mUserService.getUserEmail());
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_FRAGMENT, mSelectedFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            setFragment(R.id.nav_main, R.id.main_content, null);
        } else if (id == R.id.nav_orders) {
            setFragment(R.id.nav_orders, R.id.main_content, null);
        } else if (id == R.id.nav_logout) {
            mUserService.logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPreExecute(@UserService.UserServiceType int serviceType) {

    }

    @Override
    public void onPostExecute(@UserService.UserServiceType int serviceType, Boolean result) {
        if (serviceType == UserService.USER_SERVICE_LOGOUT ) {
            if (result) {
                openLoginActivity();
                finish();
            } else {
                Snackbar.make(mCoordinatorView, R.string.error_unable_to_login, Snackbar.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onCancelled(@UserService.UserServiceType int serviceType) {

    }

    @Override
    public void onProductItemClick(long productId) {
        Bundle args = ProductDetailFragment.newInstance(productId);
        int id = R.id.main_content;
      /*  if(findViewById(R.id.main_detail_content) != null){
            id = R.id.main_detail_content;
        }*/

        setFragment(R.id.nav_product_detail, id, args);
    }

    @Override
    public void onAmountItemClick(long productId, int amount) {
        DialogFragment newFragment = UpdateAmountDialogFragment.newInstance(productId, amount);
        newFragment.show(getFragmentManager(), TAG_DIALOG_AMOUNT_ORDER);
    }

    public void reviewOrderClick(View view) {
        setFragment(R.id.nav_review_order, R.id.main_content, null);
    }

    @Override
    public void onToggleOrderItemClick(boolean added, long productId, double price) {
        if (!added) {
            Toast.makeText(this, "Add to cart", Toast.LENGTH_SHORT).show();
            Intent bookIntent = new Intent(this, CurrentOrderService.class);
            bookIntent.putExtra(CurrentOrderService.PRODUCT_ID, productId);
            bookIntent.putExtra(CurrentOrderService.PRODUCT_PRICE, price);
            bookIntent.setAction(CurrentOrderService.ACTION_ADD);
            startService(bookIntent);
        } else {
            Toast.makeText(this, "Remove from cart", Toast.LENGTH_SHORT).show();
            Intent bookIntent = new Intent(this, CurrentOrderService.class);
            bookIntent.putExtra(CurrentOrderService.PRODUCT_ID, productId);
            bookIntent.setAction(CurrentOrderService.ACTION_DELETE);
            startService(bookIntent);
        }
    }

    @Override
    public void onUpdateAmountClick(long productId, int amount) {
        Intent bookIntent = new Intent(this, CurrentOrderService.class);
        bookIntent.putExtra(CurrentOrderService.PRODUCT_ID, productId);
        bookIntent.putExtra(CurrentOrderService.PRODUCT_AMOUNT, amount);
        bookIntent.setAction(CurrentOrderService.ACTION_UPDATE);
        startService(bookIntent);
    }
}
