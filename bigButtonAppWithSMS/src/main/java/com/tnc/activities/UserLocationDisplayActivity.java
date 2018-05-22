package com.tnc.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.utility.GoogleMapUtils;
import com.tnc.utility.Utils;

/**
 * Created by a3logics on 5/1/17.
 */

public class UserLocationDisplayActivity extends FragmentActivity implements View.OnClickListener{

    Context mContext;

    private FrameLayout flBackArrow,flInformationButton;

    private TextView tvTitle;

    private Button btnBack,btnHome;

    private TextView tvUserLocationTitle;

    private GoogleMap mGoogleMap;

    private TransparentProgressDialog progress;

    private String address = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show error dialog if GoolglePlayServices not available
        //Method to check if google play service is available or not
        if (!Utils.isGooglePlayServicesAvailable(UserLocationDisplayActivity.this)) {
            finish();
        }

        setContentView(R.layout.user_location_display_activity);

        mContext = UserLocationDisplayActivity.this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        idInitialization();
    }


    // Method to initialize views/widgets
    private void idInitialization() {
        progress = new TransparentProgressDialog(mContext,
                R.drawable.customspinner);

        flBackArrow = (FrameLayout) findViewById(R.id.flBackArrow);
        flInformationButton = (FrameLayout) findViewById(R.id.flInformationButton);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnHome = (Button) findViewById(R.id.btnHome);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvUserLocationTitle = (TextView) findViewById(R.id.tvUserLocationTitle);

        //To get MapFragment reference from xml layout
        //To get MapFragment reference from xml layout
        /*MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);*/

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mGoogleMap = googleMap;

                //to show current location in the map
                mGoogleMap.setMyLocationEnabled(true);

                // call method to get address
                try {

                    if(getIntent()!=null && getIntent().getExtras()!=null &&
                            getIntent().getExtras().containsKey("latitude") && getIntent().getExtras().containsKey("longitude")){

                        double mLatitude  = getIntent().getExtras().getDouble("latitude");
                        double mLongitude = getIntent().getExtras().getDouble("longitude");

                        getCurrentAddress(mLatitude, mLongitude);
                    }else{
                        getCurrentAddress();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });



        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);

        CustomFonts.setFontOfTextView(mContext, tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(mContext, tvUserLocationTitle, "fonts/Roboto-Bold_1.ttf");

        btnBack.setOnClickListener(this);

        btnHome.setOnClickListener(this);

    }

    /**
     * Method to get current address from Lat., Long.
     */
    private void getCurrentAddress(double mLatitude, double mLongitude) throws Exception{

        double latitude   = mLatitude;
        double longitude  = mLongitude;

        if(!(latitude == 0 && longitude == 0)){
            address           = GoogleMapUtils.getAddressString(mContext, latitude, longitude);

            if(address!=null && !address.trim().equalsIgnoreCase("")){
                address       = Utils.getCropCustomerCompleteAddress(address);

                // call method to display place name on info-window
                displayMarkerAndPlaceName(latitude, longitude, address);
            }
        }
    }

    /**
     * Method to get current address from Lat., Long.
     */
    private void getCurrentAddress() throws Exception{

        double latitude   = GlobalCommonValues.mLatitude;
        double longitude  = GlobalCommonValues.mLongitude;

        if(!(latitude == 0 && longitude == 0)){
            address           = GoogleMapUtils.getAddressString(mContext, latitude, longitude);

            if(address!=null && !address.trim().equalsIgnoreCase("")){
                address       = Utils.getCropCustomerCompleteAddress(address);

                // call method to display place name on info-window
                displayMarkerAndPlaceName(latitude, longitude, address);
            }
        }
    }

    private void displayMarkerAndPlaceName(double latitude, double longitude, String address){
        //To hold location
        LatLng latLng = new LatLng(latitude, longitude);

        //To create marker in map
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(address);

        //adding marker to the map
        if(mGoogleMap!=null){
            mGoogleMap.addMarker(markerOptions).showInfoWindow();
            //opening position with some zoom level in the map
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnBack){
            finish();
        }else if(view.getId()==R.id.btnHome){
            startActivity(new Intent(UserLocationDisplayActivity.this,HomeScreenActivity.class));
            finish();
        }
    }
}
