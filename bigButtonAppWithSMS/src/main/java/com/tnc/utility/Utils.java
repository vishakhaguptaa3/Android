/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tnc.utility;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tnc.fragments.ContactListFragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * This class contains static utility methods.
 */
public class Utils {

    // Prevents instantiation.
    private Utils() {}

    /**
     * Enables strict mode. This should only be called when debugging the application and is useful
     * for finding some potential bugs or best practice violations.
     */
    @TargetApi(11)
    public static void enableStrictMode() {
        // Strict mode is only available on gingerbread or later
        if (Utils.hasGingerbread()) {

            // Enable all thread strict mode policies
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            // Enable all VM strict mode policies
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            // Honeycomb introduced some additional strict mode features
            if (Utils.hasHoneycomb()) {
                // Flash screen when thread policy is violated
                threadPolicyBuilder.penaltyFlashScreen();
                // For each activity class, set an instance limit of 1. Any more instances and
                // there could be a memory leak.
                vmPolicyBuilder
                        .setClassInstanceLimit(ContactListFragment.class, 1);
//                        .setClassInstanceLimit(ContactDetailActivity.class, 1);
            }

            // Use builders to enable strict mode policies
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    /**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or
     * later.
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Uses static final constants to detect if the device's platform version is ICS or
     * later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /** -- Hide Keyboard --
     *
     * @param view          any view object use to hide keyboard.
     */
    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


    /**
     * Method to check if google play service is available or not
     * @param mContext
     * @return
     */
    public static boolean isGooglePlayServicesAvailable(FragmentActivity mContext) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(mContext, resultCode, 9000)
                        .show();
            } else {
                Log.i("isGooglePlayServ_Avai_", "This device is not supported.");
                mContext.finish();
            }
            return false;
        }
        return true;

//        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
//        if (ConnectionResult.SUCCESS == status) {
//            return true;
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(status, mContext, 0).show();
//            return false;
//        }
    }

    /*
	 * Name: getCropAddress This function is use to crop the
	 */

    public static String getCropCustomerCompleteAddress(String addressStr) throws Exception {
        String cropedAddress = "";
        String splitAddressArr[];
        String seprate = "";
        if (addressStr.contains("-")) {
            seprate = "- ";
            splitAddressArr = addressStr.split("-");
        } else {
            seprate = ", ";
            splitAddressArr = addressStr.split(",");
        }

        if (splitAddressArr != null && splitAddressArr.length > 0) {
            cropedAddress = splitAddressArr[0];
            if (splitAddressArr.length > 1) {
                String restAddressStr = splitAddressArr[1].trim();
                String[] splitedRestAddress = restAddressStr.split("\\s+");
                if (splitedRestAddress != null && splitedRestAddress.length > 0) {
                    if (!splitedRestAddress[0].contains("Bogotá") && !splitedRestAddress[0].contains("Colombia")) {
                        splitedRestAddress[0] = splitedRestAddress[0].replace(",", "");
                        cropedAddress = cropedAddress + " " + seprate + splitedRestAddress[0];
                    }
                }
            }

        }
        return cropedAddress;
    }

}
