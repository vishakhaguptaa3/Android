package com.tnc;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tnc.activities.PrivacyTermsOfUseActivity;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.CopyDBFromAssets;
import com.tnc.database.DBManager;
import com.tnc.database.DBQuery;
import com.tnc.draggablegridviewpager.DraggableGridViewPager;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.model.MPermission;
import com.tnc.model.PrefStore;
import com.tnc.preferences.SharedPreference;
import com.tnc.service.AppLocationService;
import com.tnc.service.GetContactService;
import com.tnc.utility.CalculateDays;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SplashScreen extends FragmentActivity
{
    private static int SPLASH_TIME_OUT = 5000;
    private TextView txtBigButtonChatstasy,txtBigButtonFree,txtAppInformation,txtCopyRight;
    private SharedPreference saveState;
    private String mScreenResolution = "";
    int mWidth = -1, mHeight = -1;
    AppLocationService appLocationService;
    PrefStore mPrefrenceStore;
    // The request code used in ActivityCompat.requestPermissions()
    // and returned in the Activity's onRequestPermissionsResult()
    int PERMISSION_ALL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        setPermission();
        idInititialization();
        		//DBQuery.deleteTable("Tiles", "", null, SplashScreen.this);
        //		DBQuery.updateTileResetTnCUser(SplashScreen.this);
        //Run aync task to copy the database  from assets into the app folder.

       /* try{

            DBManager db = new DBManager(SplashScreen.this);
            db.open();
            // Check if Tile for this number exists then update it's IsTncUser field to 1 i.e it a TnC User
            db.updateIsTnCUserForTile(2701, "91","9314618580");
        if(db!=null){
            db.close();
        }
        }catch(Exception e){
            e.getMessage();
        }*/

        //To create new table for categories in case of updated version
        try{
            if (new File(CopyDBFromAssets.DB_PATH + CopyDBFromAssets.DATABASE_NAME).canRead()) {
                DBManager db = new DBManager(SplashScreen.this);
                db.open();
                db.close();
            }
        }catch(Exception e){
            e.getMessage();
        }

        mScreenResolution = GlobalConfig_Methods.getScreenResolution(getApplicationContext());

        if(!mScreenResolution.trim().equalsIgnoreCase("")){
            saveState.setSCREEN_RESOLUTION(SplashScreen.this,mScreenResolution);
        }

        GlobalConfig_Methods.setHomeGridViewColumns(getApplicationContext());

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                new StartServiceClass().execute();
            }
        }, 100);

        Thread welcomeThread = new Thread()
        {
            int wait = 0;
            @Override
            public void run(){
                try {
                    super.run();
                    while (wait < SPLASH_TIME_OUT){
                        sleep(100);
                        wait += 100;
                    }
                }
                catch (Exception e){
                    //system.out.println("EXc=" + e);
                }
                finally{
                    if (!isFinishing()){
                        //check if the application is opening the first time from the preference value
                        if(saveState.isFirst(SplashScreen.this)){
                            Intent myIntent=new Intent(SplashScreen.this,PrivacyTermsOfUseActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else{
                            Intent myIntent=new Intent(SplashScreen.this,MainBaseActivity.class);
                            if(!saveState.isRegistered(SplashScreen.this)){
                                saveState.setIsRegistrationPopupdisplayed(SplashScreen.this, false);
                            }
                            startActivity(myIntent);
                            finish();
                        }
                    }
                }
            }
        };
        welcomeThread.start();
    }

    private void setPermission() {

        MPermission mPermission = new MPermission(this);
        if (mPermission.checkMultiplePermission(getApplicationContext())) {
            appLocationService = new AppLocationService(this);
            mPrefrenceStore= PrefStore.getInstance(getApplicationContext());
            SaveLocation();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MPermission.PERMISSION_REQUEST_CODE_CONTACT:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.make(getWindow().getDecorView().getRootView(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(getWindow().getDecorView().getRootView(), "Permission Denied", Snackbar.LENGTH_SHORT).show();
                }

                break;

            case MPermission.REQUEST_ID_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.GET_TASKS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_CALL_LOG, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                            ) {

                        //  Snackbar.make(getWindow().getDecorView().getRootView(), "Permission Granted", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Log.d("", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    setPermission();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                        break;
                    }
                }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancle", okListener)
                .create()
                .show();
    }

    private void SaveLocation() {

        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {

            double lat=  gpsLocation.getLatitude();
            double longitute=gpsLocation.getLongitude();
            mPrefrenceStore.setString("latitute", String.valueOf(lat));
            mPrefrenceStore.setString("longitute", String.valueOf(longitute));

        } else {
            showSettingsAlert("GPS");
        }
    }

    private void showSettingsAlert(String gps) {

    }
    Handler handler = new Handler();

    class StartServiceClass extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            saveState.setRefrehContactList(SplashScreen.this,false);
            saveState.setIS_FROM_HOME(SplashScreen.this,false);
            //			saveState.setIS_FetchingContacts(getApplicationContext(), true);
            Intent mainIntent = new Intent(SplashScreen.this,GetContactService.class);
            startService(mainIntent);
        }
    }

    /*
     * Initialization of widgets/views
     */
    @SuppressWarnings({"deprecation"})
    public void idInititialization()
    {
        saveState=new SharedPreference();
        txtBigButtonChatstasy=(TextView) findViewById(R.id.txtBigButtonChatstasy);
        txtBigButtonFree=(TextView) findViewById(R.id.txtBigButtonFree);
        txtAppInformation=(TextView) findViewById(R.id.txtAppInformation);
        txtCopyRight=(TextView) findViewById(R.id.txtCopyRight);
        CustomFonts.setFontOfTextView(getApplicationContext(),txtBigButtonChatstasy, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getApplicationContext(),txtBigButtonFree, "fonts/comic_sans_ms_regular.ttf");
        //CustomFonts.setFontOfTextView(getApplicationContext(),txtBigButton, "fonts/Helvetica-Bold.otf");
        //		CustomFonts.setFontOfTextView(getApplicationContext(),txtBigButton,"fonts/StencilStd.otf");
        CustomFonts.setFontOfTextView(getApplicationContext(),txtAppInformation,"fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getApplicationContext(),txtCopyRight,"fonts/Roboto-Bold_1.ttf");

//		txtBigButton.setText(txtBigButton.getText().toString() + Html.fromHtml("&trade;"));

//        txtAppInformation.setText(txtAppInformation.getText().toString() + Html.fromHtml("&trade;"));

		/*DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		Date currentDate = new Date(dateFormat.format(date));
		if(!saveState.getLINKEDIN_POST_DATE(SplashScreen.this).trim().equals(""))
		{
			if (CalculateDays.daysBetween(new Date(saveState.getLINKEDIN_POST_DATE(SplashScreen.this)),currentDate) > 15) 
			{
				saveState.setAccessTokenLinkedin(SplashScreen.this,"");
				saveState.setAccessTokenSecretLinkedin(SplashScreen.this,"");
			}
		}*/
        MainBaseActivity.objTileEdit=null;
    }

    INotifyGalleryDialog iNotify = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // In case need to navigate to the main activity
            Intent myIntent=new Intent(SplashScreen.this,MainBaseActivity.class);
            startActivity(myIntent);
            finish();
        }

        @Override
        public void no() {
        }
    };
}