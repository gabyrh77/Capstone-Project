package com.nanodegree.gaby.bakerylovers.services;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import com.nanodegree.gaby.bakerylovers.MainApplication;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.backend.myApi.MyApi;
import com.nanodegree.gaby.bakerylovers.backend.myApi.model.UserRecord;
import com.nanodegree.gaby.bakerylovers.data.DBContract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {
    private static final String TAG = "UserService";
    private Activity mContext;
    private MyApi myApiService;
    private SharedPreferences mSharedPrefLogin;
    private UserServiceListener mListener;
    @IntDef({USER_SERVICE_LOGIN, USER_SERVICE_LOGOUT, USER_SERVICE_REGISTER, USER_SERVICE_LOGIN_GOOGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserServiceType {}

    public static final int USER_SERVICE_LOGIN = 0;
    public static final int USER_SERVICE_LOGOUT = 1;
    public static final int USER_SERVICE_REGISTER = 2;
    public static final int USER_SERVICE_LOGIN_GOOGLE = 3;

    public interface UserServiceListener {
        void onPreExecute(@UserServiceType int serviceType);
        void onPostExecute(@UserServiceType int serviceType, Boolean result);
        void onCancelled(@UserServiceType int serviceType);
    }

    public UserService(Activity context) {
        mContext = context;
        mSharedPrefLogin = context.getSharedPreferences(
                context.getString(R.string.preference_session_file_key), Context.MODE_PRIVATE);
        mListener = (UserServiceListener)context;

        myApiService = ((MainApplication)context.getApplication()).getAPIService();
    }

    public void login(String userEmail, String userPassword) {
        UserServiceTask task = new UserServiceTask(USER_SERVICE_LOGIN, mListener);
        task.execute(userEmail, userPassword);
    }

    public void loginGoogle(String userEmail, String name, String token, String phone) {
        UserServiceTask task = new UserServiceTask(USER_SERVICE_LOGIN_GOOGLE, mListener);
        task.execute(userEmail, name, token, phone);
    }

    public void logout() {
        if (isLoggedIn()) {
            UserServiceTask task = new UserServiceTask(USER_SERVICE_LOGOUT, mListener);
            task.execute(getUserSessionId());
        } else {
            mListener.onPostExecute(USER_SERVICE_LOGOUT, false);
        }
    }

    public void register(String userEmail, String userPassword, String name, String phone) {
        UserServiceTask task = new UserServiceTask(USER_SERVICE_REGISTER, mListener);
        task.execute(userEmail, userPassword, name, phone);
    }

    public boolean isLoggedIn() {
        return (mSharedPrefLogin.getString(mContext.getString(R.string.pref_session_id_key), null)!=null);
    }

    public String getUserEmail() {
        return mSharedPrefLogin.getString(mContext.getString(R.string.pref_logged_email_key), "");
    }

    public String getUserName() {
        return mSharedPrefLogin.getString(mContext.getString(R.string.pref_logged_name_key), "");
    }

    public Long getUserId() {
        return mSharedPrefLogin.getLong(mContext.getString(R.string.pref_user_id_key), 0);
    }

    public String getUserSessionId() {
        return mSharedPrefLogin.getString(mContext.getString(R.string.pref_session_id_key), "");
    }

    public static String getUserSessionId(Context context) {
        SharedPreferences sharedPrefLogin = context.getSharedPreferences(
                context.getString(R.string.preference_session_file_key), Context.MODE_PRIVATE);
        return sharedPrefLogin.getString(context.getString(R.string.pref_session_id_key), "");
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserServiceTask extends AsyncTask<String, Void, Boolean> {

        private UserServiceListener mListener;
        private @UserServiceType int mServiceType;

        UserServiceTask(@UserServiceType int serviceType, UserServiceListener listener) {
            mListener = listener;
            mServiceType = serviceType;
        }

        @Override
        protected void onPreExecute() {
            mListener.onPreExecute(mServiceType);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "user service Task called with service " + String.valueOf(USER_SERVICE_LOGIN));
            switch (mServiceType) {
                case USER_SERVICE_LOGIN:
                    try {
                        String password = md5(params[1]);
                        UserRecord user = myApiService.user().login(params[0], password).execute();
                        if (user!=null) {
                            saveLoginPref(user);
                            return true;
                        }
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case USER_SERVICE_LOGOUT:
                    try {
                        myApiService.user().logout(params[0]).execute();
                        saveLogoutPref();
                        return true;
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case USER_SERVICE_REGISTER:
                    try {
                        String password = md5(params[1]);
                        UserRecord user = myApiService.user().register(params[0], params[2], password, params[3]).execute();
                        if (user!=null) {
                            return true;
                        }
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case USER_SERVICE_LOGIN_GOOGLE:
                    try {
                        UserRecord user = myApiService.user().loginGoogle(params[0], params[1], params[2]).setPhone(params[3]).execute();
                        if (user!=null) {
                            saveLoginPref(user);
                            return true;
                        }
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                    break;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mListener.onPostExecute(mServiceType, success);
        }

        @Override
        protected void onCancelled() {
            mListener.onCancelled(mServiceType);
        }

        private void saveLoginPref(UserRecord user) {
            SharedPreferences.Editor editor = mSharedPrefLogin.edit();
            editor.putString(mContext.getString(R.string.pref_session_id_key), user.getLoginToken());
            editor.putString(mContext.getString(R.string.pref_logged_name_key), user.getFullName());
            editor.putLong(mContext.getString(R.string.pref_user_id_key), user.getId());
            editor.putString(mContext.getString(R.string.pref_logged_email_key), user.getEmail());
            editor.commit();
            addUserToDB(user);
        }

        private void saveLogoutPref() {
            Long id = getUserId();
            if (id>0) {
                deleteUserFromDB(id);
            }
            SharedPreferences.Editor editor = mSharedPrefLogin.edit();
            editor.clear();
            editor.commit();
        }
    }

    private String md5(String s) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(s.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void deleteUserFromDB(long id) {
        try {
            String[] args = new String[] {String.valueOf(id)};
            mContext.getContentResolver().delete(DBContract.UserEntry.CONTENT_URI, DBContract.UserEntry.COLUMN_USER_ID + " = ? ", args);
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void addUserToDB(UserRecord user) {
        try {
            ContentValues values = new ContentValues();
            values.put(DBContract.UserEntry.COLUMN_USER_ID, user.getId());
            values.put(DBContract.UserEntry.COLUMN_EMAIL, user.getFullName());
            values.put(DBContract.UserEntry.COLUMN_FULL_NAME, user.getId());
            values.put(DBContract.UserEntry.COLUMN_ADDRESS, user.getAddress());
            values.put(DBContract.UserEntry.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
            mContext.getContentResolver().insert(DBContract.UserEntry.CONTENT_URI, values);
        }catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }
}
