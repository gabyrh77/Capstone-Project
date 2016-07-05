package com.nanodegree.gaby.bakerylovers.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.ProfileQuery;
import com.nanodegree.gaby.bakerylovers.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements UserService.UserServiceListener,
        LoaderCallbacks<Cursor>, OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private static final String UI_MODE_TAG = "ui_mode_tag";
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_GOOGLE_SIGN_IN = 1;
    private static final int LOGIN_MODE = 0;
    private static final int REGISTER_MODE = 1;

    private UserService mUserService;
    private int mUIMode;

    // UI references.
    private Button mEmailSignInButton;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPassword2View;
    private EditText mFullNameView;
    private EditText mPhoneNumberView;
    private LinearLayout mRegisterView;
    private View mProgressView;
    private View mLoginFormView;
    private View mCoordinatorView;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mUserService = new UserService(this);
        // Set up the login form.
        mCoordinatorView = findViewById(R.id.coordinator_main_view);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    registerAccount();
                    return true;
                }
                return false;
            }
        });
        mFullNameView = (EditText) findViewById(R.id.full_name);
        mPassword2View = (EditText) findViewById(R.id.password2);
        mRegisterView = (LinearLayout) findViewById(R.id.register_view);
        TextView mRegisterClick = (TextView) findViewById(R.id.register_click);
        if (mRegisterClick!=null) {
            mRegisterClick.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleUIMode();
                }
            });
        }

        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        if (googleSignInButton!=null) {
            googleSignInButton.setSize(SignInButton.SIZE_WIDE);
            googleSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInGoogle();
                }
            });
        }

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (savedInstanceState != null) {
            mUIMode = savedInstanceState.getInt(UI_MODE_TAG, LOGIN_MODE);
        } else {
            mUIMode = LOGIN_MODE;
        }

        updateUIMode();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUserService.isLoggedIn()) {
            openMainActivity();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mUIMode == REGISTER_MODE) {
            toggleUIMode();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(UI_MODE_TAG, mUIMode);
    }

    private void toggleUIMode() {
        switch (mUIMode){
            case REGISTER_MODE:
                mUIMode = LOGIN_MODE;
                break;
            case LOGIN_MODE:
                mUIMode = REGISTER_MODE;
                break;
        }
        hideKeyboard();
        updateUIMode();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateUIMode() {
        if (mUIMode == REGISTER_MODE) {
            mPassword2View.setVisibility(View.VISIBLE);
            mPhoneNumberView.setVisibility(View.VISIBLE);
            mFullNameView.setVisibility(View.VISIBLE);
            mEmailSignInButton.setText(getString(R.string.action_register));
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerAccount();
                }
            });
            mRegisterView.setVisibility(View.GONE);
            mPasswordView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            mPhoneNumberView.setVisibility(View.GONE);
            mFullNameView.setVisibility(View.GONE);
            mPassword2View.setVisibility(View.GONE);
            mEmailSignInButton.setText(getString(R.string.action_sign_in));
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            mRegisterView.setVisibility(View.VISIBLE);
            mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void registerAccount() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPassword2View.setError(null);

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPassword2View.getText().toString();
        String name = mFullNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email))

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid repeated password.
        if (TextUtils.isEmpty(password2)) {
            mPassword2View.setError(getString(R.string.error_field_required));
            focusView = mPassword2View;
            cancel = true;
        } else if (!password2.equals(password)) {
            mPassword2View.setError(getString(R.string.error_repeated_password));
            focusView = mPassword2View;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            mFullNameView.setError(getString(R.string.error_field_required));
            focusView = mFullNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mUserService.register(mEmailView.getText().toString(), mPasswordView.getText().toString(),
                    mFullNameView.getText().toString(), mPhoneNumberView.getText().toString());
            hideKeyboard();
        }
    }
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mUserService.login(email, password);
            hideKeyboard();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect google services");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mUserService.loginGoogle(acct.getEmail(), acct.getDisplayName(),acct.getIdToken(), "");
        } else {
            Snackbar.make(mCoordinatorView, R.string.error_unable_to_register, Snackbar.LENGTH_LONG);
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPreExecute(@UserService.UserServiceType int serviceType) {
       showProgress(true);
    }

    @Override
    public void onPostExecute(@UserService.UserServiceType int serviceType, Boolean success) {
        showProgress(false);
        if (serviceType == UserService.USER_SERVICE_LOGIN || serviceType == UserService.USER_SERVICE_LOGIN_GOOGLE ) {
            if (success) {
                openMainActivity();
            } else {
                Snackbar.make(mCoordinatorView, R.string.error_unable_to_login, Snackbar.LENGTH_LONG).show();
            }
        } else  if (serviceType == UserService.USER_SERVICE_REGISTER) {
            if (success) {
                toggleUIMode();
            } else {
                Snackbar.make(mCoordinatorView, R.string.error_unable_to_register, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCancelled(@UserService.UserServiceType int serviceType) {
        showProgress(false);
    }
}

