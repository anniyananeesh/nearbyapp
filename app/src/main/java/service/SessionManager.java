package service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "NearbyApp";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Email address (make variable public to access from outside)
    public static final String KEY_IMAGE = "profile_image";

    // Facebooko ID (make variable public to access from outside)
    public static final String KEY_FBID = "facebook_id";

    // Gender (make variable public to access from outside)
    public static final String KEY_GENDER = "gender";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(JSONObject object, String image){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        try {
            // Storing name in pref
            editor.putString(KEY_NAME, object.getString("name"));

            // Storing email in pref
            editor.putString(KEY_EMAIL, object.getString("email"));

            // Storing image in pref
            editor.putString(KEY_IMAGE, image);

            // Storing facebook ID in pref
            editor.putString(KEY_FBID, object.getString("id"));

            // Storing gender in pref
            editor.putString(KEY_GENDER, object.getString("gender"));

            // commit changes
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public boolean checkLogin(){

        return (!this.isLoggedIn()) ? false : true ;
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user email id
        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}