package com.tnc.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.tnc.dialog.PremiumFeaturesDialog;
import com.tnc.homescreen.HomeScreenActivity;

/**
 * Created by a3logics on 17/11/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

        // onReceive must be very quick and not block, so it just fires up a Service
        @Override
        public void onReceive(Context context, Intent intent) {
            //system.out.println("AlarmStart");

        }
}
