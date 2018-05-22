package com.tnc.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.tnc.model.User;
import com.tnc.service.FirebaseIDService;


public class SharedPreferenceHelper {
    public static final Object KEY_GCM_ID = 0;
    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_USER_INFO = "userinfo";
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";


    private SharedPreferenceHelper() {}

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User user) {
        editor.putString(SHARE_KEY_NAME, user.name);
        editor.putString(SHARE_KEY_EMAIL, user.email);
        editor.putString(SHARE_KEY_AVATA, user.phoneNumber);
        editor.putString(SHARE_KEY_UID, StaticConfig.UID);
        editor.apply();
    }

    public User getUserInfo(){
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_EMAIL, "");
        String phoneNumber = preferences.getString(SHARE_KEY_AVATA, "default");

        User user = new User();
        user.name = userName;
        user.email = email;
        user.phoneNumber = phoneNumber;

        return user;
    }

    public String getUID(){
        return preferences.getString(SHARE_KEY_UID, "");
    }

    public static void setStringPref(Object keyGcmId, FirebaseIDService firebaseIDService, String refreshedToken) {

    }
}
