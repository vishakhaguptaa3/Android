package com.tnc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tnc.MainBaseActivity;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Class Name: SendLocationService Class
 *
 * @author a3logics
 *
 */

public class SendLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private double mLongitude = 0.0, mLatitude = 0.0, mSpeed = 0.0;
    private Long mGpsTime;
    private int mEventCode, mHeading, mAltitude;
    private int mIterator = 0;
    private int mID = -1;
    private Context mContext;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static int UPDATE_INTERVAL = 2 * 1000; // 5 sec
    private static int FATEST_INTERVAL = 1000; // 1 sec
    private static int DISPLACEMENT = 0; // 10 meters
    private Timer timer = null;
    private TimerTask doAsynchronousTask = null;
    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service started", "service strted");
        mContext = SendLocationService.this;

        buildGoogleApiClient();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        resetValuesService();
        super.onDestroy();
        Log.e("on Destroy", "OnDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        resetValuesService();
    }

    private void resetValuesService() {
        stopSelf();
        stopLocationUpdates();
        if (doAsynchronousTask != null) {
            doAsynchronousTask.cancel();
            doAsynchronousTask = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            createLocationRequest();

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                GlobalCommonValues.mLatitude = mLastLocation.getLatitude();
                GlobalCommonValues.mLongitude = mLastLocation.getLongitude();
                try {
                    Log.e("Location_bgservice", mLastLocation.getLatitude() + "////" + mLastLocation.getLongitude());
                    startLocationUpdates();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                startLocationUpdates();
                try {
                    if(!GlobalConfig_Methods.isServiceRunning(mContext, "com.tnc.service.SendLocationService"))
                        startService(new Intent(mContext,SendLocationService.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            startLocationUpdates();
        }
//        mGoogleApiClient = new GoogleApiClient.Builder(MainBaseActivity.this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void stopLocationUpdates() {
        try {
            if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, listener);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                if (location != null) {
                    GlobalCommonValues.mLatitude = location.getLatitude();
                    GlobalCommonValues.mLongitude = location.getLongitude();
                    Log.e("Locchangedbgservice", location.getLatitude() + "////" + location.getLongitude());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}