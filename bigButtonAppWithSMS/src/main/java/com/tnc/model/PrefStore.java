package com.tnc.model;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefStore {

    private SharedPreferences mSharedPref = null;
    private static PrefStore mPrefStore = null;
    private static SharedPreferences.Editor ed;



    private PrefStore(Context context) {

        mSharedPref = context.getSharedPreferences("Pref",
                Context.MODE_PRIVATE);
        ed = mSharedPref.edit();

    }

    public static PrefStore getInstance(Context mContext) {
        if (mPrefStore == null) {
            mPrefStore = new PrefStore(mContext);
        }
        return mPrefStore;
    }

    public void clearPrefStore() {
        if (mSharedPref != null) {
            mSharedPref.edit().clear().commit();

        }
    }


    public void setString(String key,String value){
        ed.putString(key, value);
        ed.commit();
    }
    public String getStringIMg(String key){
        return mSharedPref.getString(key,"0");
    }
    public String getString(String key){
        return mSharedPref.getString(key,null);
    }
}
