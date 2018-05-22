package com.tnc.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.SocialNetwork.Facebook.FaceBookApiResponse;
import com.tnc.base.BaseFragment;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CancelRegistrationRequestBean;
import com.tnc.bean.CheckReturningUserBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.bean.CountryDetailsBean;
import com.tnc.bean.DefaultMessagesBeanDB;
import com.tnc.bean.InitResponseMessageBean;
import com.tnc.bean.ParentConsentStatusBean;
import com.tnc.bean.UserRegistrationBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.GetBBID;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.SlideAnimationUtils;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.database.StaticConfig;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.dialog.NotifyFriendsConfirmationDialog;
import com.tnc.dialog.SelectImagePopup;
import com.tnc.dialog.ShowDialog;
import com.tnc.dialog.TileUpdateSuccessDialog;
import com.tnc.dialog.WelcomeBackReg_RestoreDialog;
import com.tnc.dialog.WelcomeDialogRegistration;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.model.MPermission;
import com.tnc.model.PrefStore;
import com.tnc.model.User;
import com.tnc.preferences.SharedPreference;
import com.tnc.register.RegisterContract;
import com.tnc.register.RegisterPresenter;
import com.tnc.service.AppLocationService;
import com.tnc.service.GetContactService;
import com.tnc.service.RegistrationCheckService;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CheckReturningUserResponse;
import com.tnc.webresponse.DefaultMessagesResponse;
import com.tnc.webresponse.EmergencyNumberRespnseBean;
import com.tnc.webresponse.ParentConsentStatusResponse;
import com.tnc.webresponse.RegistrationResponse;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cz.msebera.android.httpclient.Header;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by a3logics on 6/12/17.
 */

public class UserRegistrationFragment extends BaseFragmentTabs implements View.OnClickListener,RegisterContract.View, AdapterView.OnItemSelectedListener {

    private String TAG = UserRegistrationFragment.class.getSimpleName();
    //Widget variable initialization and declaration
    private LinearLayout linear_Layout,mReg1Layout, mReg2Layout, mReg3Layout, mReg1SpLayout, mReg2SpLayout, mRegPhoneNumberAlertlayout, mRegAlertCloudBackUplayout;
    private LinearLayout mRegPhoneNumberAlertEdtphonelayout;
    private EditText mreg2EtOTPNumber, mReg1EtContactName, mReg2EtPhoneNumber, mRegPhoneNumberAlertEdtCloudKey, mRegPhoneNumberAlertEdtPhone;
    private Button btnHome;
    private Spinner mReg1SpAgeGroup, mReg2SpCountryRegion;
    private ImageView mReg1ImgHintAgeGroup, mReg2ImgHintReturningUser, mReg3ImViewUser, mReg3ImgHintDefaultImg;
    private ImageView mBtnBackRegLayoutImg, mBtnForwardRegLayoutImg;
    private CheckBox mReg2ChkBoxReturningUser, mReg3ChkBoxDefaultImg;
    private TextView mReg3TvAddPhoto, mBtnBackRegLayoutTxt, mBtnForwardRegLayoutTxt, mRegPhoneNumberAlertTxt, mRegAlertCloudBackUptxt,
            mPhoneNumberAlerttxt3, mPhoneNumberAlerttxt4;
    private RelativeLayout mBtnBackRegLayout, mBtnForwardRegLayout;
    private FrameLayout frame_layout;
    private TransparentProgressDialog progress;
    private ReceiverGetRegisteredUsers receiver;
    AppLocationService appLocationService;
    private RelativeLayout btnForwardRegLayouttextview;
    PrefStore mPrefrenceStore;
    private TextView mTvRegistration;
    AppCompatActivity appCompatActivity;
    private String picturePath = "";
    private Gson gson;
    int layoutFlag = 1;
    private Uri fileUri;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private final int PICK_IMAGE_REQUEST = 210;
    boolean isGoogleImageDialog = false;
    boolean isFaceBookImageDialog = false;
    FragmentActivity mActivity;
    private Bitmap bitmapUserImage = null;
    String[] ageGroupArray;
    boolean isParentalConsentComplete;
    boolean isSecondScreenAllowed = false;
    private Intent mIntentRegistrationService;
    private String imageURL = "", matchType = "", userType = "";
    private boolean isValidMatchType = false, isValidMatchTypeBackButton = false;
    private boolean isValidUserType = false;

    private String stringToSpan = "", stringToSpanSecond = "", strReturning = "",
            strNew = "", strUser = "", strReturningUser = "";

    private int indexOfReturningUser = 0;
    private RegisterPresenter mRegisterPresenter;
    public String number = ""; //"+18582606130";
    public String country_name = "USA";
    public String number_type = "dedicated";


    private String contactNumber = "";
    private String mCountryCode = "";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;


    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private String mVerificationId;
    // [START declare_auth]


    private AuthUtils authUtils;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean firstTimeAccess;
    private String email, phoneNumber;


    /*// values variable initialization and declaration
    String mUserName,mAgeGroup,mCountryCode,mPhoneNumber;
    boolean isReturningUser,isDefaultImage;*/


    DatabaseReference rootRef, demoRef;


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        //mAuth.addAuthStateListener(mAuthListener);

    }

    public static UserRegistrationFragment newInstance() {
        Bundle args = new Bundle();
        UserRegistrationFragment fragment = new UserRegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_registration1, container, false);

        init(view);


        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();
        //database reference pointing to demo node
        demoRef = rootRef.child("chatstasy");


        saveState.setFirstTime(mActivity, false);

        receiver = new ReceiverGetRegisteredUsers();

        firstTimeAccess = true;
//        initFirebase();
//        setPermission();
        return view;
    }

    private void initFirebase() {

        authUtils = new AuthUtils();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(mActivity, HomeScreenActivity.class));
                        mActivity.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                //paras

                firstTimeAccess = false;
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setPermission() {

        MPermission mPermission = new MPermission(getActivity());
        if (mPermission.checkMultiplePermission(getApplicationContext())) {
            appLocationService = new AppLocationService(getActivity());
            mPrefrenceStore = PrefStore.getInstance(getApplicationContext());
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
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(getWindow().getDecorView().getRootView(), "Permission Denied", Snackbar.LENGTH_SHORT).show();
                }

                break;

            case MPermission.REQUEST_ID_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_NUMBERS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.GET_TASKS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CALL_LOG, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED


                            ) {

                        //  Snackbar.make(getWindow().getDecorView().getRootView(), "Permission Granted", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Log.d("", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                            Toast.makeText(getActivity(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                        break;
                    }
                }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void SaveLocation() {

        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {

            double lat = gpsLocation.getLatitude();
            double longitute = gpsLocation.getLongitude();
            mPrefrenceStore.setString("latitute", String.valueOf(lat));
            mPrefrenceStore.setString("longitute", String.valueOf(longitute));

        } else {
            showSettingsAlert("GPS");
        }
    }

    private void showSettingsAlert(String gps) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    void init(){
        mRegisterPresenter = new RegisterPresenter(this);
    }

    void init(View view) {

        mActivity = getActivity();


        mReg1Layout = (LinearLayout) view.findViewById(R.id.reg1layout);
        mReg1SpLayout = (LinearLayout) view.findViewById(R.id.reg1SpLayout);
        mReg1EtContactName = (EditText) view.findViewById(R.id.reg1EtContactName);
        mReg1SpAgeGroup = (Spinner) view.findViewById(R.id.reg1SpAgeGroup);
        mReg1ImgHintAgeGroup = (ImageView) view.findViewById(R.id.reg1ImgHintAgeGroup);

        mReg2Layout = (LinearLayout) view.findViewById(R.id.reg2layout);
        mReg2SpLayout = (LinearLayout) view.findViewById(R.id.reg2SpLayout);
        mReg2EtPhoneNumber = (EditText) view.findViewById(R.id.reg2EtPhoneNumber);
        mReg2ImgHintReturningUser = (ImageView) view.findViewById(R.id.reg2ImgHintReturningUser);
        mReg2SpCountryRegion = (Spinner) view.findViewById(R.id.reg2SpCountryRegion);
        mReg2ChkBoxReturningUser = (CheckBox) view.findViewById(R.id.reg2ChkBoxReturningUser);
        frame_layout = (FrameLayout)view.findViewById(R.id.frame_layout);
        mReg3Layout = (LinearLayout) view.findViewById(R.id.reg3layout);
        mReg3TvAddPhoto = (TextView) view.findViewById(R.id.reg3TvAddPhoto);
        mReg3ImViewUser = (ImageView) view.findViewById(R.id.reg3ImViewUser);
        mReg3ImgHintDefaultImg = (ImageView) view.findViewById(R.id.reg3ImgHintDefaultImg);
        mReg3ChkBoxDefaultImg = (CheckBox) view.findViewById(R.id.reg3ChkBoxDefaultImg);

        mBtnBackRegLayout = (RelativeLayout) view.findViewById(R.id.btnBackRegLayout);
        mBtnBackRegLayoutTxt = (TextView) view.findViewById(R.id.btnBackRegLayoutTxt);
        mBtnBackRegLayoutImg = (ImageView) view.findViewById(R.id.btnBackRegLayoutImg);

        mBtnForwardRegLayout = (RelativeLayout) view.findViewById(R.id.btnForwardRegLayout);
        mBtnForwardRegLayoutTxt = (TextView) view.findViewById(R.id.btnForwardRegLayoutTxt);
        mBtnForwardRegLayoutImg = (ImageView) view.findViewById(R.id.btnForwardRegLayoutImg);

        mRegPhoneNumberAlertlayout = (LinearLayout) view.findViewById(R.id.regPhoneNumberAlertlayout);
        mRegPhoneNumberAlertTxt = (TextView) view.findViewById(R.id.regPhoneNumberAlertTxt);
        mRegPhoneNumberAlertEdtphonelayout = (LinearLayout) view.findViewById(R.id.regPhoneNumberAledtphonelayout);
        mRegPhoneNumberAlertEdtCloudKey = (EditText) view.findViewById(R.id.regPhoneNumberAlertEdtCloudKey);
        mRegPhoneNumberAlertEdtPhone = (EditText) view.findViewById(R.id.regPhoneNumberAlertEdtPhone);


        mRegAlertCloudBackUplayout = (LinearLayout) view.findViewById(R.id.regAlertCloudBackUplayout);
        mRegAlertCloudBackUptxt = (TextView) view.findViewById(R.id.regAlertCloudBackUptxt);

        mPhoneNumberAlerttxt3 = (TextView) view.findViewById(R.id.txtPhoneNumberAlerttext3);
        mPhoneNumberAlerttxt4 = (TextView) view.findViewById(R.id.txtPhoneNumberAlerttext4);

        mTvRegistration = (TextView) view.findViewById(R.id.tvRegistration);

        btnHome = (Button) view.findViewById(R.id.btnHome);
        btnHome.setVisibility(View.VISIBLE);

        // Set Font style to views
        GlobalConfig_Methods.setRobotoBoldFontStyle(getActivity(), null, mTvRegistration);
        GlobalConfig_Methods.setRobotoBoldFontStyle(getActivity(), mRegPhoneNumberAlertEdtCloudKey, null);
        GlobalConfig_Methods.setRobotoBoldFontStyle(getActivity(), mRegPhoneNumberAlertEdtPhone, null);

        progress = new TransparentProgressDialog(mActivityTabs, R.drawable.customspinner);
        ageGroupArray = getResources().getStringArray(R.array.ageGroupArray);
        saveState = new SharedPreference();

        //Set Text for Alert/Warning Screen TextView
        stringToSpan = getResources().getString(R.string.txtPhoneNumberAlerttext3);
        stringToSpanSecond = getResources().getString(R.string.txtPhoneNumberAlerttext4);
        strReturning = getResources().getString(R.string.txtReturning);
        strReturningUser = getResources().getString(R.string.txtReturningUser);
        strNew = getResources().getString(R.string.txtNew).toLowerCase();
        strUser = getResources().getString(R.string.txtUser);

        indexOfReturningUser = stringToSpan.indexOf(strReturningUser);

        mAuth = FirebaseAuth.getInstance();

        // returning user to span & Returning User in bold style
        StyleTextAsyncTask styleTask = new StyleTextAsyncTask(mPhoneNumberAlerttxt3, stringToSpan, stringToSpan.indexOf(strReturning),
                stringToSpan.indexOf(strUser) + strUser.length(),
                true,
                indexOfReturningUser, indexOfReturningUser + strReturningUser.length());
        styleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //new user to span
        styleTask = new StyleTextAsyncTask(mPhoneNumberAlerttxt4, stringToSpanSecond, stringToSpanSecond.indexOf(strNew),
                stringToSpanSecond.indexOf(strUser) + strUser.length(),
                false,
                0, 0);
        styleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mBtnBackRegLayout.setOnClickListener(this);
        mBtnBackRegLayoutTxt.setOnClickListener(this);

        mBtnForwardRegLayout.setOnClickListener(this);

        mBtnForwardRegLayoutTxt.setOnClickListener(this);


        mReg1ImgHintAgeGroup.setOnClickListener(this);

        mReg2ImgHintReturningUser.setOnClickListener(this);

        mReg3ImgHintDefaultImg.setOnClickListener(this);
        mReg3ImViewUser.setOnClickListener(this);

        btnHome.setOnClickListener(this);
        frame_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegPhoneNumberAlertlayout.setVisibility(View.VISIBLE);
            }
        });

        setSpCountryRegion();
        setSpAgeGroup();
    }

    @Override
    public void onResume() {

//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiverz, new IntentFilter("otp"));

        super.onResume();

        IntentFilter filter = new IntentFilter("com.bigbutton.receiver_contacts");
        getActivity().registerReceiver(receiver, filter);

        if (isGoogleImageDialog) {
            displayAlertDialog(getResources().getString(R.string.txtNoGoogleImage));
            return;
        }
        if (isFaceBookImageDialog) {
            displayAlertDialog(getResources().getString(R.string.txtNoFacebookImage));
            return;
        }

        if (!saveState.getIS_UNDERAGE(getActivity())) {
            if (getAgeGroup().equalsIgnoreCase(ageGroupArray[1])) {
//                btnRegister.setEnabled(false);
//                btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
            } else {
//                btnRegister.setEnabled(true);
//                btnRegister.setBackgroundResource(R.drawable.button_bg_yes_green);
            }

        } else if (saveState.getIS_UNDERAGE(getActivity())) {
//            btnRegister.setEnabled(false);
//            btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
//            chkBoxUnderAge.setChecked(true);
            if (!saveState.getParentEmailId(getActivity()).equalsIgnoreCase(""))
                checkparentalStatus();
        }


        if (MainBaseActivity.isCameraCanceled) {
            MainBaseActivity._bitmap = null;
            MainBaseActivity.isImageSelected = false;
            MainBaseActivity.isImageRequested = false;
            if (mActivity instanceof MainBaseActivity)
                ((MainBaseActivity) mActivity).objFragment = null;
            else if (mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity) mActivity).objFragment = null;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = GlobalConfig_Methods.getOutputImageUri();
            if (fileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
            MainBaseActivity.isCameraCanceled = false;
        }
        if (MainBaseActivity._bitmap != null) {
            MainBaseActivity._bitmap = GlobalConfig_Methods.getResizedBitmap(
                    MainBaseActivity._bitmap, 250, 250);
            mReg3ImViewUser.setImageBitmap(MainBaseActivity._bitmap);
            //enable/disable create profile button
            enableDisable(true);
        }
        setCheckBoxClickability();
        if (HomeScreenActivity.btnDisable != null)
            HomeScreenActivity.btnDisable.setEnabled(false);
    }

    @Override
    public void onPause() {
        GlobalConfig_Methods.hideKeyBoard(mActivity, mReg1EtContactName);
        GlobalConfig_Methods.hideKeyBoard(mActivity, mReg2EtPhoneNumber);
        GlobalConfig_Methods.hideKeyBoard(mActivity, mRegPhoneNumberAlertEdtCloudKey);
        GlobalConfig_Methods.hideKeyBoard(mActivity, mRegPhoneNumberAlertEdtPhone);

        getActivity().unregisterReceiver(receiver);

        super.onPause();


//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiverz);
        if (mActivity instanceof MainBaseActivity) {
            if (((MainBaseActivity) mActivity) != null) {
                if (NetworkConnection.isNetworkAvailable(mActivity)) {
                    if (saveState.getGCMRegistrationId(getActivity())
                            .equals("")) {
                        ((MainBaseActivity) mActivity).setGCMRegID();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBackRegLayout) {
            setLayoutVisiblity(v);
        } else if (v.getId() == R.id.btnForwardRegLayout || v.getId() == R.id.btnForwardRegLayoutTxt) {
            if (v.getId() == R.id.btnForwardRegLayout) {


                if (mReg2Layout.getVisibility() == View.VISIBLE) {
                    number = mReg2EtPhoneNumber.getText().toString();
                    Log.e(number, "Number");
                    mRegisterPresenter.register(getActivity(), number + "@chatstasy.com", number);


                }

                if (validatingScreen()) {
                    setLayoutVisiblity(v);

                }
            }


        } else if (v.getId() == R.id.reg1ImgHintAgeGroup) {
            GlobalConfig_Methods.displayAlertDialog(getResources().getString(R.string.txtHintAgeGroup), getActivity());
        } else if (v.getId() == R.id.reg2ImgHintReturningUser) {
            GlobalConfig_Methods.displayAlertDialog(getResources().getString(R.string.txtHintReturningUser), getActivity());

        } else if (v.getId() == R.id.reg3ImgHintDefaultImg) {
            GlobalConfig_Methods.displayAlertDialog(getResources().getString(R.string.txtHintDefaultImage), getActivity());

        } else if (v.getId() == R.id.reg3ImViewUser) {
            SelectImagePopup dialogSelectimage = new SelectImagePopup();
            dialogSelectimage.newInstance("Choose Image", (mActivityTabs), iNotifyImagePopSelection, this);

        } else if (v.getId() == R.id.btnBackRegLayoutTxt) {

            ImageRequestDialog mDialog = new ImageRequestDialog();
            mDialog.newInstance("", getActivity(), getString(R.string.txtPopUpCancel), "", new BaseFragment.AlertCallAction() {
                @Override
                public void isAlert(boolean isOkClikced) {
                    getActivity().finish();
                }
            });
            mDialog.setCancelable(false);
            mDialog.show(getChildFragmentManager(), TAG);
        } else if (v.getId() == R.id.btnHome) {

            saveState.setRegistered(getActivity(), false);
            saveState.setPublicKey(getActivity(), "");
            saveState.setCountryCode(mActivityTabs, "");
            saveState.setCountryname(mActivityTabs, "");
            saveState.setBBID(mActivityTabs, "");
            saveState.setUserName(mActivityTabs, "");
            saveState.setUserMailID(mActivityTabs, "");
            saveState.setUserPhoneNumber(mActivityTabs, "");
            saveState.setCountryidd(mActivityTabs, "");
            saveState.setIsVerified(mActivityTabs, false);
            saveState.setIS_DEACTIVATED(mActivityTabs, false);

            GlobalConfig_Methods.clearAllPreferences(getActivity());

            if (GlobalConfig_Methods.isValidString(saveState.getPassCode(mActivityTabs))) {
                CancelRegistrationRequestBean objCancelRegistration = null;
                if (mActivityTabs instanceof MainBaseActivity) {
                    objCancelRegistration = new CancelRegistrationRequestBean();
                    objCancelRegistration.setPasscode(saveState.getPassCode(mActivityTabs));
                    ((MainBaseActivity) mActivityTabs)
                            .cancelRegistration(objCancelRegistration);
                    startActivity(new Intent(getActivity(),
                            HomeScreenActivity.class));
                    ((MainBaseActivity) mActivityTabs).finish();
                } else if (mActivityTabs instanceof HomeScreenActivity) {
                    objCancelRegistration = new CancelRegistrationRequestBean();
                    objCancelRegistration.setPasscode(saveState.getPassCode(mActivityTabs));
                    ((HomeScreenActivity) mActivityTabs)
                            .cancelRegistration(objCancelRegistration);
                    startActivity(new Intent(getActivity(),
                            HomeScreenActivity.class));
                    ((HomeScreenActivity) mActivityTabs).finish();
                }

                saveState.setPassCode(mActivityTabs, "");

                /**
                 * CALL METHOD TO CLEAR DATABSE
                 */

                GlobalConfig_Methods.clearDataBaseValues(getActivity());
            }
        }


    }






//    private boolean validate(String email, String phoneNumber) {
//            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
//            return (phoneNumber.length() > 0 || phoneNumber.equals(";")) && matcher.find();
//        }


    //<---------------------------widget getter - setter starts----------------------->

    /**
     * Method to get user name
     *
     * @return
     */
    public String getUserName() {
        return mReg1EtContactName.getText().toString();
    }

    /**
     * Method to get age group
     *
     * @return
     */
    public String getAgeGroup() {
        return mReg1SpAgeGroup.getSelectedItem().toString();
    }

    /**
     * Method to get country code
     *
     * @return
     */
    public String getCountryCode() {
        String countryCode = "";
        countryCode = mReg2SpCountryRegion.getSelectedItem().toString();
        return countryCode.replaceAll("[^-?0-9]+", " ").trim();
    }

    /**
     * Method to get country Name
     *
     * @return
     */
    private String getCountryName() {
        String countryName = "";
        countryName = mReg2SpCountryRegion.getSelectedItem().toString().split("\\+")[0];
        return countryName.trim();
    }

    /**
     * Method to get phone number
     *
     * @return
     */
    public String getPhoneNumber() {
        return mReg2EtPhoneNumber.getText().toString();
    }

    public boolean isReturningUser() {
        return mReg2ChkBoxReturningUser.isChecked();
    }

    public String isDefaultImage() {
        String isDefaultImage = "1";

        if (mReg3ChkBoxDefaultImg.isChecked()) {
            isDefaultImage = "2";
        }
        return isDefaultImage;
    }

    //<---------------------------widget getter - setter ends----------------------->


    //<--------------------------Image handling code for camera/gallery starts ------------------------>
    //interface to handle user action i.e. open camera/gallery
    INotifyGalleryDialog iNotifyImagePopSelection = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // In case of Camera option is selected
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = GlobalConfig_Methods.getOutputImageUri();
            if (fileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        }

        @Override
        public void no() {
            // In case of Gallery option is selected
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_IMAGE_REQUEST);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                try {
                    Uri selectedImage = fileUri;
                    picturePath = selectedImage.getPath();
                    MainBaseActivity.selectedImagepath = picturePath;
                    cropImage(true);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                @SuppressWarnings("unused")
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                MainBaseActivity.selectedImagepath = picturePath;
                Intent cropIntent = new Intent(getActivity(), CropImage.class);
                cropIntent.putExtra(CropImage.IMAGE_PATH, MainBaseActivity.selectedImagepath);
                cropIntent.putExtra(CropImage.SCALE, true);
                cropIntent.putExtra(CropImage.ASPECT_X, 1);
                cropIntent.putExtra(CropImage.ASPECT_Y, 1);
                getActivity().startActivity(cropIntent);
            } else if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
                authUtils.createUser(data.getStringExtra(StaticConfig.STR_EXTRA_USERNAME), data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD));
            }
        }
    }

    /**
     * Method to perform image cropping
     */

    private void cropImage(boolean isCamera) {
        if (!MainBaseActivity.selectedImagepath.equals("")) {
            MainBaseActivity._bitmap = null;
            Intent intent = new Intent(getActivity(), CropImage.class);
            if (isCamera)
                intent.putExtra(CropImage.IMAGE_PATH, picturePath);
            else
                intent.putExtra(CropImage.IMAGE_PATH, MainBaseActivity.selectedImagepath);
            intent.putExtra(CropImage.SCALE, true);
            intent.putExtra(CropImage.ASPECT_X, 1);
            intent.putExtra(CropImage.ASPECT_Y, 1);
            getActivity().startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        }
    }

    //<--------------------------Image handling code for camera/gallery ends ------------------------>

    /**
     * Method to set checkbox clickability
     */
    private void setCheckBoxClickability() {
        if (((BitmapDrawable) mReg3ImViewUser.getDrawable()) == null || ((BitmapDrawable) mReg3ImViewUser.getDrawable()).getBitmap() == null) {
            bitmapUserImage = null;
        } else {
            bitmapUserImage = ((BitmapDrawable) mReg3ImViewUser.getDrawable())
                    .getBitmap();
        }
        if (bitmapUserImage == null) {
            mReg3ChkBoxDefaultImg.setClickable(false);
        } else if (bitmapUserImage != null) {
            mReg3ChkBoxDefaultImg.setClickable(true);
            if (mReg3ChkBoxDefaultImg.isChecked())
                mReg3ChkBoxDefaultImg.setChecked(true);
            else
                mReg3ChkBoxDefaultImg.setChecked(false);
        }
    }

    //<-------------------------- getVisiblity and setVisibility code starts ---------------------------->

    /**
     * Following function is used to set the visibility of layout
     *
     * @param v view on which user click
     */
    void setLayoutVisiblity(View v) {

        LinearLayout linearLayout = getLayoutVisiblity();

        if (linearLayout != null && v != null) {


            String mobilenumber = mReg2EtPhoneNumber.getText().toString();
            String countryCodez = mReg2SpCountryRegion.getSelectedItem().toString();
            //push creates a unique id in database

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("Email", mobilenumber + "@chatstasy.com");
            childUpdates.put("Password", mobilenumber);

            demoRef.setValue(childUpdates);


            if (linearLayout == mReg1Layout && v.getId() == R.id.btnForwardRegLayout && getAgeGroup().equalsIgnoreCase(ageGroupArray[1]) && saveState.getParentConsentStatus(getActivity()).equalsIgnoreCase("")) {
                actionOnUnderAge();
            } else if (linearLayout == mReg1Layout && v.getId() == R.id.btnForwardRegLayout) {
                mReg1Layout.setVisibility(View.GONE);
                mReg2Layout.setVisibility(View.VISIBLE);
                mReg3Layout.setVisibility(View.GONE);
                mBtnBackRegLayoutTxt.setVisibility(View.INVISIBLE);
                mBtnBackRegLayoutImg.setVisibility(View.VISIBLE);
                SlideAnimationUtils.slideOutToLeft(getActivity(), mReg1Layout);
                SlideAnimationUtils.slideInFromRight(getActivity(), mReg2Layout);
                mBtnBackRegLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_blue));

            } else if (linearLayout == mReg2Layout && v.getId() == R.id.btnBackRegLayout) {
                mReg1Layout.setVisibility(View.VISIBLE);
                mReg2Layout.setVisibility(View.GONE);
                mReg3Layout.setVisibility(View.GONE);
                mBtnBackRegLayoutTxt.setVisibility(View.VISIBLE);
                mBtnBackRegLayoutImg.setVisibility(View.INVISIBLE);
                SlideAnimationUtils.slideInFromLeft(getActivity(), mReg1Layout);
                SlideAnimationUtils.slideOutToRight(getActivity(), mReg2Layout);
                mBtnBackRegLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_red));
                mBtnBackRegLayoutTxt.setText(getResources().getString(R.string.cancel));

            } else if (linearLayout == mReg2Layout && v.getId() == R.id.btnForwardRegLayout) {

                if (!isSecondScreenAllowed) {
                    checkInternetConnectionUserRegistration(false);
                } else {
                    if (isValidUserType && userType.equalsIgnoreCase(GlobalCommonValues.USER_TYPE_NON_EXISTING)) {
                        // image upload screen
                        mReg1Layout.setVisibility(View.GONE);
                        mReg2Layout.setVisibility(View.GONE);
                        mReg3Layout.setVisibility(View.VISIBLE);
                        mBtnForwardRegLayoutTxt.setVisibility(View.VISIBLE);
                        mBtnForwardRegLayoutImg.setVisibility(View.INVISIBLE);
                        SlideAnimationUtils.slideInFromRight(getActivity(), mReg3Layout);
                        SlideAnimationUtils.slideOutToLeft(getActivity(), mReg2Layout);
                        mBtnBackRegLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_blue));
                        mBtnForwardRegLayoutTxt.setText(getResources().getString(R.string.txtCreateProfile));



//                        if (mBtnForwardRegLayoutTxt.getText().toString().equals("Create Profile")) {
//                            mRegPhoneNumberAlertlayout.setVisibility(View.VISIBLE);
//                        }

                        //enable/disable create profile button
                        enableDisable(GlobalConfig_Methods.isValidString(imageURL));

                        //Set Image of the user
                        if (mReg3ImViewUser != null && mReg3ImViewUser.getVisibility() == View.VISIBLE) {
                            if (GlobalConfig_Methods.isValidString(imageURL))
                                Picasso.with(getActivity()).load(imageURL).into(mReg3ImViewUser);
                            Log.e(imageURL,"imageURL");
                        }

//                        if (v.getId() == R.id.btnForwardRegLayoutTxt) {
////                            if (mBtnForwardRegLayoutTxt.getVisibility() == View.GONE) {
////                                Toast.makeText(mActivity, "Create Profile Gone", Toast.LENGTH_SHORT).show();
//
//                                mBtnForwardRegLayoutTxt.setVisibility(View.VISIBLE);
//                                if (mBtnForwardRegLayoutTxt.getVisibility() == View.VISIBLE) {
//
//                                    if (mBtnForwardRegLayoutTxt.getText().toString().equals("Create Profile")) {
//                                        mRegPhoneNumberAlertlayout.setVisibility(View.VISIBLE);
//
//                                    }
//                                }
//
//                            } else {
//                                if (mBtnForwardRegLayoutTxt.getText().toString().equals("Create Profile")) {
//                                    mRegPhoneNumberAlertlayout.setVisibility(View.VISIBLE);
//
//                                }
//                            }



                        } else if (isValidMatchType && matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_PHONE_NUMBER)) {
                        //go to warning screen

                        if (isReturningUser()) {
                            //go to cloud backup key screen
                            mReg1Layout.setVisibility(View.GONE);
                            mReg2Layout.setVisibility(View.GONE);
                            mReg3Layout.setVisibility(View.GONE);
                            mRegAlertCloudBackUplayout.setVisibility(View.VISIBLE);
                            SlideAnimationUtils.slideOutToRight(getActivity(), mReg2Layout);
                            SlideAnimationUtils.slideInFromLeft(getActivity(), mRegAlertCloudBackUplayout);

                            mBtnForwardRegLayoutTxt.setVisibility(View.VISIBLE);
                            mBtnForwardRegLayoutImg.setVisibility(View.INVISIBLE);

                            //Show Phone Number Views
                            mRegPhoneNumberAlertEdtphonelayout.setVisibility(View.VISIBLE);

                            mBtnForwardRegLayoutTxt.setText(getResources().getString(R.string.txtSubmit).toUpperCase());

                            /*mReg1Layout.setVisibility(View.GONE);
                            mReg2Layout.setVisibility(View.GONE);
                            mReg3Layout.setVisibility(View.GONE);
                            mRegAlertCloudBackUplayout.setVisibility(View.VISIBLE);

                            //Hide Phone Number Views
                            mRegPhoneNumberAlertEdtphonelayout.setVisibility(View.GONE);

                            SlideAnimationUtils.slideOutToRight(getActivity(),mReg2Layout);
                            SlideAnimationUtils.slideInFromLeft(getActivity(),mRegAlertCloudBackUplayout);

                            mBtnForwardRegLayoutTxt.setVisibility(View.VISIBLE);
                            mBtnForwardRegLayoutImg.setVisibility(View.INVISIBLE);

                            mBtnForwardRegLayoutTxt.setText(getResources().getString(R.string.txtSubmit).toUpperCase());*/

                        } else {
                            // Make warning screen visible to the user
                            mReg1Layout.setVisibility(View.GONE);
                            mReg2Layout.setVisibility(View.GONE);
                            mReg3Layout.setVisibility(View.GONE);
                            mRegPhoneNumberAlertlayout.setVisibility(View.VISIBLE);
                            SlideAnimationUtils.slideOutToRight(getActivity(), mReg3Layout);
                            SlideAnimationUtils.slideInFromLeft(getActivity(), mRegPhoneNumberAlertlayout);

                            isValidMatchTypeBackButton = isValidMatchType;

                            if (isValidMatchType)
                                isValidMatchType = false;
                            if (isValidUserType)
                                isValidUserType = false;
                            mRegPhoneNumberAlertTxt.setText(getCountryCode() + " " + getPhoneNumber());

                            mBtnForwardRegLayoutTxt.setVisibility(View.VISIBLE);
                            mBtnForwardRegLayoutImg.setVisibility(View.INVISIBLE);
                            mBtnForwardRegLayoutTxt.setText(getResources().getString(R.string.txtSendSms).toUpperCase());
                        }
                        //check with the match types
                    } else if (isValidMatchType && matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_DEVICE)) {
                        //go to cloud backup key screen irrespective of returning user is checked or not
                        mReg1Layout.setVisibility(View.GONE);
                        mReg2Layout.setVisibility(View.GONE);
                        mReg3Layout.setVisibility(View.VISIBLE);
                        mRegAlertCloudBackUplayout.setVisibility(View.VISIBLE);
                        SlideAnimationUtils.slideOutToRight(getActivity(), mReg2Layout);
                        SlideAnimationUtils.slideInFromLeft(getActivity(), mRegAlertCloudBackUplayout);

                        mBtnForwardRegLayoutTxt.setVisibility(View.VISIBLE);
                        mBtnForwardRegLayoutImg.setVisibility(View.INVISIBLE);

                        //Show Phone Number Views
                        mRegPhoneNumberAlertEdtphonelayout.setVisibility(View.VISIBLE);

                        mBtnForwardRegLayoutTxt.setText(getResources().getString(R.string.txtSubmit).toUpperCase());

                    } else if (isValidMatchType && matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_BOTH)) {

                        //Display welcome back alert dialog
                        GlobalConfig_Methods.displayWelcomeBackDialog(getActivity());
                        MainBaseActivity.isReturningUser = true;

                        VerifyingRegistrationFragment.isAppUserRegistered = true;

                        //Stop Registration completion check service
                        stopRegistrationService();
                    }
                }
            } else if (linearLayout == mReg3Layout && v.getId() == R.id.btnBackRegLayout) {
                mReg1Layout.setVisibility(View.GONE);
                mReg2Layout.setVisibility(View.VISIBLE);
                mReg3Layout.setVisibility(View.GONE);
                mBtnForwardRegLayoutTxt.setVisibility(View.INVISIBLE);
                mBtnForwardRegLayoutImg.setVisibility(View.VISIBLE);
                SlideAnimationUtils.slideOutToRight(getActivity(), mReg1Layout);
                SlideAnimationUtils.slideInFromLeft(getActivity(), mReg2Layout);
                mBtnBackRegLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_blue));

                //enable/disable create profile button
                enableDisable(true);

            } else if (linearLayout == mReg3Layout && v.getId() == R.id.btnForwardRegLayoutTxt) {
                checkInternetConnectionUserRegistration(true); // registration with image on 3rd screen in a flow

            } else if (linearLayout == mRegPhoneNumberAlertlayout && v.getId() == R.id.btnBackRegLayout) {
                isValidMatchType = isValidMatchTypeBackButton;
                mReg1Layout.setVisibility(View.GONE);
                mReg2Layout.setVisibility(View.VISIBLE);
                mReg3Layout.setVisibility(View.GONE);
                mBtnForwardRegLayoutTxt.setVisibility(View.INVISIBLE);
                mBtnForwardRegLayoutImg.setVisibility(View.VISIBLE);
                SlideAnimationUtils.slideOutToRight(getActivity(), mReg1Layout);
                SlideAnimationUtils.slideInFromLeft(getActivity(), mReg2Layout);
                mRegPhoneNumberAlertlayout.setVisibility(View.GONE);
                mBtnBackRegLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_blue));

            } else if (linearLayout == mReg3Layout && v.getId() == R.id.btnForwardRegLayout && isValidMatchType && isValidUserType
                    && (matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_PHONE_NUMBER) ||
                    matchType.trim().equalsIgnoreCase(GlobalCommonValues.USER_TYPE_NON_EXISTING))) {
                // Make warning screen visible to the user
                mReg1Layout.setVisibility(View.GONE);
                mReg2Layout.setVisibility(View.GONE);
                mReg3Layout.setVisibility(View.GONE);
                mRegPhoneNumberAlertlayout.setVisibility(View.VISIBLE);
                SlideAnimationUtils.slideOutToRight(getActivity(), mRegPhoneNumberAlertlayout);
                SlideAnimationUtils.slideInFromLeft(getActivity(), mReg3Layout);

                isValidMatchTypeBackButton = isValidMatchType;

                if (isValidMatchType)
                    isValidMatchType = false;
                if (isValidUserType)
                    isValidUserType = false;
                mRegPhoneNumberAlertTxt.setText(getCountryCode() + " " + getPhoneNumber());

                mBtnForwardRegLayoutTxt.setText(getResources().getString(R.string.txtSendSms).toUpperCase());
            } else if (linearLayout == mRegPhoneNumberAlertlayout && v.getId() == R.id.btnForwardRegLayoutTxt && mBtnForwardRegLayoutTxt.getText().toString().equals(getResources().getString(R.string.txtSendSms).toUpperCase())) {
                // When SEND SMS IS CLICKED
                setSMSDetails();

                //Clear preferences and database values
//                GlobalConfig_Methods.clearDataBaseValues(getActivity());
//                GlobalConfig_Methods.clearRegsitrationPreferences(getActivity());
            } else if (linearLayout == mRegAlertCloudBackUplayout && v.getId() == R.id.btnForwardRegLayoutTxt) {
                displayConfirmationPopup(GlobalCommonValues.WARNING);
                /*if(validateBackupRequest()){
                }*/
            } else if (linearLayout == mRegAlertCloudBackUplayout && v.getId() == R.id.btnBackRegLayout) {

                isValidMatchType = isValidMatchTypeBackButton;

                mReg1Layout.setVisibility(View.GONE);
                mReg2Layout.setVisibility(View.VISIBLE);
                mRegAlertCloudBackUplayout.setVisibility(View.GONE);
                mBtnForwardRegLayoutTxt.setVisibility(View.INVISIBLE);
                mBtnForwardRegLayoutImg.setVisibility(View.VISIBLE);
                SlideAnimationUtils.slideOutToLeft(getActivity(), mRegAlertCloudBackUplayout);
                SlideAnimationUtils.slideInFromRight(getActivity(), mReg2Layout);
                mRegAlertCloudBackUplayout.setVisibility(View.GONE);
                mBtnBackRegLayout.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_blue));
            }
        }
    }//batao

    /**
     * Method to enable/disable the create profile button
     *
     * @param isEnabled
     */
    private void enableDisable(boolean isEnabled) {
        if (isEnabled) {
            mBtnForwardRegLayout.setAlpha(1.0f);
            mBtnForwardRegLayout.setEnabled(true);
        } else {
            mBtnForwardRegLayout.setAlpha(0.5f);
            mBtnForwardRegLayout.setEnabled(false);
        }
    }

    //<-------------------------- getVisiblity and setVisibility code starts ---------------------------->
    LinearLayout getLayoutVisiblity() {

        LinearLayout returnLinearLayout = null;
        if (mReg1Layout.getVisibility() == View.VISIBLE) {
            returnLinearLayout = mReg1Layout;
        } else if (mReg2Layout.getVisibility() == View.VISIBLE) {
            returnLinearLayout = mReg2Layout;
        } else if (mReg3Layout.getVisibility() == View.VISIBLE) {
            returnLinearLayout = mReg3Layout;
        } else if (mRegPhoneNumberAlertlayout.getVisibility() == View.VISIBLE) {
            returnLinearLayout = mRegPhoneNumberAlertlayout;
        } else if (mRegAlertCloudBackUplayout.getVisibility() == View.VISIBLE) {
            returnLinearLayout = mRegAlertCloudBackUplayout;
        }
        return returnLinearLayout;
    }

    //<-------------------------- getVisiblity and setVisibility code ends ---------------------------->


    //<--------------------- image setting via social networking starts ------------------------------->

    /**
     * function to which react to facebook login response data
     * .
     *
     * @param faceBookApiResponse
     */

    public void loginFaceBook(FaceBookApiResponse faceBookApiResponse) {
        if (!faceBookApiResponse.getPicture().getData().isIs_silhouette()) {
            setProfilePic(faceBookApiResponse.getPicture().getData().getUrl());
            isFaceBookImageDialog = false;
        } else {
            isFaceBookImageDialog = true;
            displayAlertDialog(getResources().getString(R.string.txtNoFacebookImage));
        }
    }

    void displayAlertDialog(String messageToDisplay) {
        if (isGoogleImageDialog)
            isGoogleImageDialog = false;
        if (isFaceBookImageDialog)
            isFaceBookImageDialog = false;
        TileUpdateSuccessDialog dialog = new TileUpdateSuccessDialog();
        dialog.setCancelable(false);
        dialog.newInstance("", mActivityTabs, messageToDisplay);
        dialog.show(getActivity().getSupportFragmentManager(), TAG);
    }

    /**
     * function is used to set user profile image on given url
     *
     * @param url user image web url
     */
    public void setProfilePic(String url) {

        if (url.length() > 0) {
            if (GlobalConfig_Methods.isValidString(url)) {
                Picasso.with(getActivity())
                        .load(url)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(mReg3ImViewUser);

                //enable/disable create profile button
                enableDisable(GlobalConfig_Methods.isValidString(url));
            }
        } else {
            isGoogleImageDialog = true;
        }
    }

    //<--------------------- image setting via social networking ends ------------------------------->


    /**
     * Method to display welcome message
     *
     * @param backupKey
     */
    private void displayWelComeMessagePopup(String backupKey) {
        String title = getResources().getString(R.string.txtCongratulations);

        String message = getActivity().getResources().getString(R.string.txtUserProfileCreatedMessage);
        message = message.replace("=\"backup_key\"", backupKey);


        WelcomeDialogRegistration dialogNewRegistration = new WelcomeDialogRegistration();
        dialogNewRegistration.setCancelable(false);
        if (mActivity instanceof MainBaseActivity) {
            dialogNewRegistration.newInstance(title,
                    ((MainBaseActivity) mActivity),
                    message, "", null,
                    iNotify);
            dialogNewRegistration.show(((MainBaseActivity) mActivity)
                    .getSupportFragmentManager(), TAG);
        } else if (mActivity instanceof HomeScreenActivity) {
            dialogNewRegistration.newInstance(title,
                    ((HomeScreenActivity) mActivity),
                    message, "", null,
                    iNotify);
            dialogNewRegistration.show(((HomeScreenActivity) mActivity)
                    .getSupportFragmentManager(), TAG);
        }
    }

    /**
     * interface to handle call to //Notify friends about our availabitity on Tnc App if registered for the fIrst time
     */
    INotifyGalleryDialog iNotify = new INotifyGalleryDialog() {
        @Override
        public void yes() {
        }

        @Override
        public void no() {
            //Notify friends about our availabitity on Tnc App if registered for the first time
            if (saveState.isNewRegistration(mActivity)) {
                String message = getString(R.string.txtNotifyUsersProfileCreation);
                message = message.replace("=\"app_users\"", getResources().getString(R.string.app_name));

                NotifyFriendsConfirmationDialog dialog = new NotifyFriendsConfirmationDialog();
                dialog.newInstance("", mActivity, message, "", iNotifyTncContacts);
                dialog.setCancelable(false);
                dialog.show(getChildFragmentManager(), TAG);
            }
        }
    };

    //Interface to handle ,whether user want to notify users for the availability of him on TnC/not
    INotifyGalleryDialog iNotifyTncContacts = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            if (progress != null) {
                progress.show();
            }
            saveState.setRefrehContactList(getActivity(), true);
            saveState.setIS_FROM_HOME(getActivity(), false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    new StartServiceClass().execute();
                }
            }, 100);
        }

        @Override
        public void no() {
            // In case user selected no for not being to notified to friends for the number changed
            //then go to home screen
            gotoHomeScreen(true);
        }
    };

    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        Toast.makeText(getActivity(), "Registration Successful!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegistrationFailure(String message) {
        Log.e(TAG, "onRegistrationFailure: " + message);
//        Toast.makeText(getActivity(), "Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
    }

    //async task to call service to send the contacts to the server
    class StartServiceClass extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            saveState.setRefrehContactList(getActivity(), true);
            saveState.setIS_FROM_HOME(getActivity(), false);
            VerifyingRegistrationFragment.isNotify = true;

            if (GlobalCommonValues.listContacts != null &&
                    GlobalCommonValues.listContacts.size() > 0) {
                GlobalCommonValues.listContactsSendToServer = new
                        ArrayList<ContactDetailsBean>();
                for (int i = 0; i < GlobalCommonValues.listContacts.size(); i++)
                    GlobalCommonValues.listContactsSendToServer.add(GlobalCommonValues.listContacts.get(i));
            }

            Intent mainIntent = new Intent(getActivity(), GetContactService.class);
            getActivity().startService(mainIntent);
        }
    }

    /**
     * Method to go to home screen
     */
    private void gotoHomeScreen(boolean isCreateChatButton) {
        //go to home screen
        Intent intent = new Intent(getActivity(), HomeScreenActivity.class);
        if (isCreateChatButton) {
            intent.putExtra(GlobalCommonValues.IS_CREATE_CHAT_BUTTON, true);
        }

        if (mActivity instanceof MainBaseActivity) {
            startActivity(intent);
            ((MainBaseActivity) mActivity).finish();
        } else if (mActivity instanceof HomeScreenActivity) {
            startActivity(intent);
            ((HomeScreenActivity) mActivity).finish();
        }
    }


    /**
     * get emergency numbers & default messages
     */

    void getEmergencyNumber_DefaultMessages() {
        //call the web service to fetch latest emergency numbers from the webservice
        checkInternetConnectionEmergencyNumber();

        //call the web service to fetch latest default messages from the webservice
        checkInternetConnectionDefaultMessages();
    }


    //<------------------ Code block Get Emergency Numbers start------------------------------->

    /**
     * check availabitiy of internet connection for emergency number
     */
    public void checkInternetConnectionEmergencyNumber() {
        if (NetworkConnection.isNetworkAvailable(getActivity())) {
            getEmergencyNumbersRequest();
        }
    }

    // method to call web service to get update emergency numbers from the web service
    private void getEmergencyNumbersRequest() {
        MyHttpConnection.getWithoutPara(getActivity(), GlobalCommonValues.GET_EMERGENCY_NUMBERS,
                getActivity().getResources().getString(R.string.private_key), emergencyNumbersResponsehandler);
    }

    // Async task to call web service to get Emergency Numbers
    AsyncHttpResponseHandler emergencyNumbersResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            //			if ((!progress.isShowing()))
            //				progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("Emergency Numbers", "OnSuccess", response.toString());
                    getResponseEmergencyNumbers(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("Emergency Numbers", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            //			if (progress.isShowing())
            //				progress.dismiss();
        }
    };

    /*
     * Handling Response from the Server for the request being sent to get Emergency Numbers
     */
    private void getResponseEmergencyNumbers(String response) {
        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
                ArrayList<CountryDetailsBean> listCountries = new ArrayList<CountryDetailsBean>();
                try {
                    gson = new Gson();
                    EmergencyNumberRespnseBean get_Response = gson.fromJson(response, EmergencyNumberRespnseBean.class);
                    if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) {
                        if (get_Response != null && get_Response.getData() != null && get_Response.getData().size() > 0) {
                            CountryDetailsBean mObjCountryDetailBean;
                            for (int i = 0; i < get_Response.getData().size(); i++) {
                                mObjCountryDetailBean = new CountryDetailsBean();
                                mObjCountryDetailBean.setCountryName(get_Response.getData().get(i).getCountry());
                                mObjCountryDetailBean.setEmergency(get_Response.getData().get(i).getEmergency());
                                listCountries.add(mObjCountryDetailBean);
                            }
                            saveState.setIS_EMERGENCY_NUMBER_VERSION_UPDATED(mActivity, false);

                            //Delete the existing Emergency Number table from the database
                            if (listCountries != null && listCountries.size() > 0)
                                DBQuery.deleteTable("EmergencyNumbers", "", null, getActivity().getApplicationContext());
                            // Insert Emergency Number in the Database
                            DBQuery.insertAllCountryEmergencyNumbers(mActivity, listCountries);
                        }
                    } else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)) {


                    }
                } catch (Exception e) {
                    e.getMessage();
                }

            } else {
                //ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //<------------------ Code block Get Emergency Numbers ends ------------------------------->


    //<------------------ Code block Get Default Message starts ------------------------------->

    /**
     * check availability of internet connection for default messages
     */
    public void checkInternetConnectionDefaultMessages() {
        if (NetworkConnection.isNetworkAvailable(mActivity)) {
            getInitMessagesRequest();
        }
    }

    //Method to call web service to get configured mesages from the server
    private void getInitMessagesRequest() {
        MyHttpConnection.getWithoutPara(getActivity(), GlobalCommonValues.GET_DEFAULT_MESSAGES,
                mActivity.getResources().getString(R.string.private_key), defaultMessagesResponsehandler);
    }

    // Async task to call web service to get configured mesages
    AsyncHttpResponseHandler defaultMessagesResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            //			if ((!progress.isShowing()))
            //				progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("Default Mesasges", "OnSuccess", response.toString());
                    getResponseDefaultMessages(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("Default Mesasges", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            //			if (progress.isShowing())
            //				progress.dismiss();
        }
    };

    /*
     * Handling Response from the Server for the request being sent to get configured mesages
     */
    private void getResponseDefaultMessages(String response) {
        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
                ArrayList<InitResponseMessageBean> listInitMessages = new ArrayList<InitResponseMessageBean>();
                try {
                    Gson gson = new Gson();
                    DefaultMessagesResponse get_Response = gson.fromJson(response, DefaultMessagesResponse.class);
                    if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) {

                        ArrayList<DefaultMessagesBeanDB> mListDefaultMessages = new ArrayList<DefaultMessagesBeanDB>();
                        if (get_Response != null && get_Response.getData() != null && get_Response.getData().size() > 0) {
                            DefaultMessagesBeanDB mObjDefaultMessagesBeanDB;

                            int maxID = DBQuery.getConfigMessagesMaxCount(getActivity());

                            if (maxID == -1 || maxID == 0) {
                                maxID = 1;
                            } else {
                                maxID = maxID + 1;
                            }
                            for (int i = 0; i < get_Response.getData().size(); i++) {
                                mObjDefaultMessagesBeanDB = new DefaultMessagesBeanDB();
                                mObjDefaultMessagesBeanDB.setId(maxID + i);
                                mObjDefaultMessagesBeanDB.setMessage(get_Response.getData().get(i).getMessage());
                                mObjDefaultMessagesBeanDB.setIsLocked(1);

                                String mType = get_Response.getData().get(i).getType();
                                int mTypeMessage = (mType.equals("initiation") ? 0 : 1);  // 0- initiation  1- response

                                mObjDefaultMessagesBeanDB.setType(mTypeMessage);
                                mListDefaultMessages.add(mObjDefaultMessagesBeanDB);
                            }
                            saveState.setIS_DEFAULT_MESSAGES_VERSION_UPDATED(mActivity, false);

                            //Delete all the default messages of older version
                            if (mListDefaultMessages != null && mListDefaultMessages.size() > 0)
                                DBQuery.deleteConfigMessageFromDB(getActivity(), 1);

                            // Insert Emergency Number in the Database
                            DBQuery.insertConfigMessageFromWebService(mActivity, mListDefaultMessages);
                        }
                    } else if (get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_1) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_2) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_5) ||
                            get_Response.getResponse_code().equals(GlobalCommonValues.FAILURE_CODE_6)) {

                    }
                } catch (Exception e) {
                    e.getMessage();
                }

            } else {
                //ShowDialog.alert(mActivity, "",getResources().getString(R.string.improper_response));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //<------------------ Code block Get Default Message ends ------------------------------->


    //<-------------------Country Code block starts ------------------------------------------>

    /**
     * Function is used to set country name and country code to spinner
     */
    void setSpCountryRegion() {
        ArrayList<CountryDetailsBean> listCountries = DBQuery.getAllCountryCode(mActivity);
        ArrayList<String> countryNameAndCodeList = new ArrayList<>();
//        int i=0;
        int spnPosition = 0;
        for (CountryDetailsBean countryDetailsBean : listCountries) {
            countryNameAndCodeList.add(countryDetailsBean.getCountryName() + " +" + countryDetailsBean.getCountryCode());
            /*if(countryDetailsBean.getCountryName().equalsIgnoreCase("usa"))spnPosition = i;
            i++;*/
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, countryNameAndCodeList);
        mReg2SpCountryRegion.setAdapter(spinnerArrayAdapter);
        mReg2SpCountryRegion.setSelection(spnPosition);
        mReg2SpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReg2SpCountryRegion.performClick();
            }
        });
        mReg2SpCountryRegion.setOnItemSelectedListener(this);
    }

    //<-------------------  Country Code block ends ------------------------------------------>

    //<-------------------  Age group block starts ------------------------------------------->

    /**
     * Function used to set age group to spinner
     */

    void setSpAgeGroup() {
        List<String> ageGrpStringArrayList = Arrays.asList(getResources().getStringArray(R.array.ageGroupArray));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, ageGrpStringArrayList);
        mReg1SpAgeGroup.setAdapter(spinnerArrayAdapter);
        mReg1SpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReg1SpAgeGroup.performClick();
            }
        });
        mReg1SpAgeGroup.setOnItemSelectedListener(this);
    }

    //<-------------------  Age group block ends ------------------------------------------->


    //<------------------- Update image block starts --------------------------------------->
    /**
     * check internet availability
     */
    /*public void checkInternetConnectionUpdateImage() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            String isDefault = "1";// No
            if (isDefaultImage())
                isDefault = "2"; // yes
            else
                isDefault = "1";
            String image = "";
            if ((mReg3ImViewUser.getDrawable() != null) && (((BitmapDrawable) ((ImageView) mReg3ImViewUser).getDrawable()).getBitmap() != null)) {
                image = GlobalConfig_Methods
                        .encodeTobase64(((BitmapDrawable)((ImageView)mReg3ImViewUser).getDrawable()).getBitmap());

                //enable/disable create profile button
//                enableDisable(GlobalConfig_Methods.isValidString(image));
            }
            UpdateUserInfoBean userInfoUpdateBean = new UpdateUserInfoBean(Uri.encode(""), "",image,isDefault);
            updateUserInfoWithImage(userInfoUpdateBean);


        } else {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }*/

    //send query to server with tyhe data to update User info
    /*private void updateUserInfoWithImage(UpdateUserInfoBean userInfoUpdateBean)
    {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(userInfoUpdateBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs, GlobalCommonValues.UPDATEUSERINFO, stringEntity,
                    updateUserInfoResponseHandler, mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        }catch (Exception e) {
            e.getMessage();
        }
    }*/

    //Async task to handle update user Info
    /*AsyncHttpResponseHandler updateUserInfoResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            if ((!progress.isShowing()))
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("UserRegistration", "OnSuccess", response.toString());
                    getResponseUpdatedUserInfo(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("UserRegistration", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    public void onStop() {
        super.onStop();
    }

    *//**
     * handle response for the request being made for updating user info
     *
     * @param response
     *//*
    private void getResponseUpdatedUserInfo(String response)
    {
        boolean isEmailAvailable = false;
        if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
            gson = new Gson();
            ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
            dialogErrorMessage.setCancelable(false);
            UpdateUserInfoResponse get_Response = gson.fromJson(response,UpdateUserInfoResponse.class);
            if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE))
            {

                if((mActivityTabs!=null) && !(mActivityTabs.isFinishing())){
                    TileUpdateSuccessDialog dialog=new TileUpdateSuccessDialog();
                    dialog.setCancelable(false);

                    String messageToDisplay = getResources().getString(R.string.txtUserInfoUpdateMessage);


                    if(isEmailAvailable){
                        messageToDisplay = messageToDisplay +  " If you provided a valid email ID, an email will be sent to you. This might be in "
                                +"your Spam folder. It is important that you open the email and click the \'verify\' "
                                +"link to verify your email ID.";
                    }

                    dialog.newInstance("",mActivityTabs,messageToDisplay);
                    dialog.show(getChildFragmentManager(), TAG);
                }

            } else if (get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE)
                    || get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_1)
                    || get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_5)
                    || get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_2)
                    || get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_3)
                    || get_Response.getReponseCode().equals(GlobalCommonValues.FAILURE_CODE_4)){
                dialogErrorMessage.newInstance("",((HomeScreenActivity) mActivityTabs),
                        get_Response.getMessage(), "", null);
                dialogErrorMessage.show(((HomeScreenActivity) mActivityTabs).getSupportFragmentManager(),
                        TAG);
            }
        }else {
            Log.d(TAG,response);
            ShowDialog.alert(
                    mActivityTabs,"",getResources().getString(R.string.improper_response_network));
        }
    }*/

    //<------------------- Update image block ends --------------------------------------->


    //<------------------- User Registration block starts--------------------------------->

    /**
     * check internet availability
     */
    public void checkInternetConnectionUserRegistration(boolean isImageUplaoded) {
        if (NetworkConnection.isNetworkAvailable(mActivity)) {
            if (mActivity instanceof MainBaseActivity) {
                if (((MainBaseActivity) mActivity) != null) {
                    if (NetworkConnection.isNetworkAvailable(mActivity)) {
                        if (saveState.getGCMRegistrationId(getActivity())
                                .equals("")) {
                            ((MainBaseActivity) mActivity).setGCMRegID();
                        }
                    }
                }
            } else if (mActivity instanceof HomeScreenActivity) {
                if (((HomeScreenActivity) mActivity) != null) {
                    if (NetworkConnection.isNetworkAvailable(mActivity)) {
                        if (saveState.getGCMRegistrationId(getActivity())
                                .equals("")) {
                            ((HomeScreenActivity) mActivity).setGCMRegID();
                        }
                    }
                }
            }

//			if ((imViewUser.getDrawable() != null)
//					&& (((BitmapDrawable) ((ImageView) imViewUser)
//					.getDrawable()).getBitmap() != null)) {
//				image = GlobalConfig_Methods
//						.encodeTobase64(MainBaseActivity._bitmap);
//			}


            UserRegistrationBean userRegistrationBean = new UserRegistrationBean(
                    Uri.encode(getUserName()),
                    getPhoneNumber(),
                    "",
                    saveState.getDeviceId(getActivity()),
                    getAgeGroup(),
                    getCountryCode(),
                    saveState.getGCMRegistrationId(getActivity()),
                    "2",
                    isDefaultImage() + "",
                    "", "");// Uri.encode(image)

            if (isSecondScreenAllowed) {
                userRegistrationBean.setName(Uri.encode(getUserName()));
                userRegistrationBean.setNumber(getPhoneNumber());
                //send email id of parent if it get confirmed from parents
                if (isParentalConsentComplete)
                    userRegistrationBean.setEmail(saveState.getParentEmailId(getActivity()));
                userRegistrationBean.setDevice_id(saveState.getDeviceId(getActivity()));

                userRegistrationBean.setCountry_code(getCountryCode());
                userRegistrationBean.setPush_notification_id(saveState.getGCMRegistrationId(getActivity()));
                userRegistrationBean.setDevice("2");
                userRegistrationBean.setIs_default_image("" + isDefaultImage());
                userRegistrationBean.setImage(GlobalConfig_Methods.encodeTobase64(((BitmapDrawable) ((ImageView) mReg3ImViewUser).getDrawable()).getBitmap()));
                userRegistrationBean.setGroup_code("");

            }

            if (isImageUplaoded) {
                String passCode = saveState.getPassCode(getActivity());
                if (GlobalConfig_Methods.isValidString(passCode))
                    userRegistrationBean.setPasscode(passCode);
            }

            /*String email = "";
            String isDefault = "";
            String image = "";
*/
           /* if(chkBoxUnderAge.isChecked() &&(saveState.getIS_UNDERAGE(getActivity()))
                    &&  (saveState.getParentEmailId(getActivity())!=null
                    && !saveState.getParentEmailId(getActivity()).equalsIgnoreCase(""))){

                email = saveState.getParentEmailId(getActivity());
            }*/

            /*UserRegistrationBean userRegistrationBean = new UserRegistrationBean(
                    Uri.encode(getUserName()), email
                    , saveState.getDeviceId(getActivity()),
                    saveState.getGCMRegistrationId(getActivity()), "2",
                    isDefault, image,"");*/
            userRegistration(userRegistrationBean);
        } else {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
        }
    }


    /**
     * request for registration
     *
     * @param userRegistrationBean
     */
    private void userRegistration(UserRegistrationBean userRegistrationBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(userRegistrationBean);
            System.out.println("Request" + userRegistrationBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivity,
                    GlobalCommonValues.USER_REGISTRATION, stringEntity,
                    userRegistrationResponseHandler,
                    mActivity.getString(R.string.private_key), "");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    AsyncHttpResponseHandler userRegistrationResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            if ((!progress.isShowing()))
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("UserRegistration", "OnSuccess", response.toString());
                    getResponseRegistration(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("UserRegistration", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };


    /**
     * handle response for the request being made for the registration
     *
     * @param response
     */
    private void getResponseRegistration(String response) {
        try {
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>") || response.contains("php")) {
                response2 = response.substring(response.indexOf("response_code") - 2, response.length());
            } else {
                response2 = response;
            }

            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
                dialogErrorMessage.setCancelable(false);
                RegistrationResponse get_Response = gson.fromJson(response2, RegistrationResponse.class);
                if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE)) {

                    // in case of first time data sent response received
                    if (get_Response.getData.getUser_type() != null && !get_Response.getData.getUser_type().isEmpty()) {
                        //2nd screen

                        //Set match Type
                        matchType = get_Response.getData.getMatch_type();

                        isValidMatchType = GlobalConfig_Methods.isValidString(matchType);

                        isValidMatchTypeBackButton = isValidMatchType;

                        userType = get_Response.getData.getUser_type();

                        isValidUserType = GlobalConfig_Methods.isValidString(userType);

                        // checking app registration to prevent backdoor entry of the registration
                        VerifyingRegistrationFragment.isAppUserRegistered = false;

                        saveState.setBackupKey(getActivity(), get_Response.getData.getBackup_key());

                        if (matchType.equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_BOTH)) {
                            // save values in preferences as it will be premanent registration
                            saveState.setPublicKey(getActivity(), get_Response.getData.getPublic_key());

                            // Set Premium user Value in preferences
                            if (get_Response.getData.getIs_premium_user().equalsIgnoreCase(GlobalCommonValues.YES)) {
                                get_Response.getData.setIs_premium_user("true");
                                saveState.setISPREMIUMUSER(getActivity(), true);
                            } else {
                                get_Response.getData.setIs_premium_user("false");
                                saveState.setISPREMIUMUSER(getActivity(), false);
                            }

                            //Set IsActivated value in preferences
                            get_Response.getData.setIs_activate(get_Response.getData.getIs_activate());

                            //Set IsVerified value in preferences
                            get_Response.getData.setIs_verified(get_Response.getData.getIs_verified());

                            //Save values in preferences
                            saveState.setCountryCode(getActivity(), get_Response.getData.getCountry_code());
                            saveState.setIS_RecentRegistration(getActivity(), true);
                            saveState.setIS_DEACTIVATED(getActivity(), false);
                            saveState.setPassCode(getActivity(), get_Response.getData.getPasscode());
                            saveState.setPublicKey(getActivity(), get_Response.getData.getPublic_key());

                            //clear temp username
                            GlobalConfig_Methods.clearTempUserName(getActivity());

                            //Upload tile backup to the server
                            uploadTilesBackUp();

                            //get latest emergency numbers & default messages list from the server on successful registration
                            getEmergencyNumber_DefaultMessages();

                            saveState.setRegistered(getActivity(), true);

                            saveState.setNewRegistration(mActivity, true);

                            //display welcome message for newly registered user on first time
                            //displayWelComeMessagePopup(get_Response.getData.getBackup_key());

                            VerifyingRegistrationFragment.isAppUserRegistered = true;

                            MainBaseActivity.isReturningUser = false;

                        } else {
                            //start a service to check if the app is removed from the recent app list
                            mIntentRegistrationService = new Intent(getActivity(), RegistrationCheckService.class);
                            if (mIntentRegistrationService != null)
                                getActivity().startService(mIntentRegistrationService);
                        }

                        if (isValidUserType && userType.equalsIgnoreCase(GlobalCommonValues.USER_TYPE_EXISTING)) {
                            /*saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivity, true);
                            saveState.setPublicKey(getActivity(), get_Response.getData.getPublic_key());*/
                            saveState.setPassCode(getActivity(), get_Response.getData.getPasscode());
                            /*saveState.setRegistered(getActivity(), false);
                            saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivity, true);
                            if (isReturningUser()) {
                                saveState.setISRETURNINGUSER(mActivity, true);
                            } else {
                                saveState.setISRETURNINGUSER(mActivity, false);
                            }*/

                            //Set Image URL in a String
                            imageURL = get_Response.getData.getImage();

                            isSecondScreenAllowed = true;

                            mBtnForwardRegLayout.performClick();

                            // Set Default Image Value in preferences
                            if (get_Response != null
                                    && get_Response.getData.getIs_default_image()
                                    .equalsIgnoreCase(GlobalCommonValues.NO)
                                    && !get_Response.getData.getImage().trim().equals("")
                                    && !get_Response.getData.getImage().trim().equalsIgnoreCase(
                                    GlobalCommonValues.NULL)) {
                                saveState.setDisplayISDEFAULTIMAGEString(getActivity(), "true");
                                saveState.setDefaultImage(mActivityTabs, false);
                            } else if (get_Response != null
                                    && get_Response.getData.getIs_default_image()
                                    .equalsIgnoreCase(GlobalCommonValues.YES)
                                    && !get_Response.getData.getImage().trim().equals("")
                                    && !get_Response.getData.getImage().trim().equalsIgnoreCase(
                                    GlobalCommonValues.NULL)) {
                                saveState.setDefaultImage(mActivityTabs, true);
                            }

                        } else if (isValidUserType && userType.equalsIgnoreCase(GlobalCommonValues.USER_TYPE_NON_EXISTING)) {
                            // if nothing gets matched from the database i.e. in case of non-existing

                            saveState.setPassCode(getActivity(), get_Response.getData.getPasscode());

                            isSecondScreenAllowed = true;

                            // go to warning screen
                            mBtnForwardRegLayout.performClick();

                            //Stop Registration completion check service
//                            stopRegistrationService();
                        }
                    } else { // in case of image update response received
                        //3rd screen

                        // Set Premium user Value in preferences
                        if (get_Response.getData.getIs_premium_user().equalsIgnoreCase(GlobalCommonValues.YES)) {
                            get_Response.getData.setIs_premium_user("true");
                            saveState.setISPREMIUMUSER(getActivity(), true);
                        } else {
                            get_Response.getData.setIs_premium_user("false");
                            saveState.setISPREMIUMUSER(getActivity(), false);
                        }

                        //Set IsActivated value in preferences
                        get_Response.getData.setIs_activate(get_Response.getData.getIs_activate());

                        //Set IsVerified value in preferences
                        get_Response.getData.setIs_verified(get_Response.getData.getIs_verified());

                        // Set Default Image Value in preferences
                        if (get_Response != null
                                && get_Response.getData.getIs_default_image()
                                .equalsIgnoreCase(GlobalCommonValues.NO)
                                && !get_Response.getData.getImage().trim().equals("")
                                && !get_Response.getData.getImage().trim().equalsIgnoreCase(
                                GlobalCommonValues.NULL)) {
                            saveState.setDisplayISDEFAULTIMAGEString(getActivity(), "true");
                            saveState.setDefaultImage(mActivityTabs, false);
                        } else if (get_Response != null
                                && get_Response.getData.getIs_default_image()
                                .equalsIgnoreCase(GlobalCommonValues.YES)
                                && !get_Response.getData.getImage().trim().equals("")
                                && !get_Response.getData.getImage().trim().equalsIgnoreCase(
                                GlobalCommonValues.NULL)) {
                            saveState.setDefaultImage(mActivityTabs, true);
                        }

                        //Save values in preferences
                        saveState.setCountryCode(getActivity(), get_Response.getData.getCountry_code());
                        saveState.setIS_RecentRegistration(getActivity(), true);
                        saveState.setIS_DEACTIVATED(getActivity(), false);
                        saveState.setPassCode(getActivity(), get_Response.getData.getPasscode());
                        saveState.setBackupKey(getActivity(), get_Response.getData.getBackup_key());
                        saveState.setPublicKey(getActivity(), get_Response.getData.getPublic_key());

                        //clear temp username
                        GlobalConfig_Methods.clearTempUserName(getActivity());

                        //Upload tile backup to the server
                        uploadTilesBackUp();

                        //get latest emergency numbers & default messages list from the server on successful registration
                        getEmergencyNumber_DefaultMessages();

                        saveState.setRegistered(getActivity(), true);

                        saveState.setNewRegistration(mActivity, true);

                        //display welcome message for newly registered user on first time
                        displayWelComeMessagePopup(get_Response.getData.getBackup_key());

                        //check with the match types
                        /*if(isValidMatchType && matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_DEVICE)){
                            //go to cloud backup key screen

                            mBtnForwardRegLayout.performClick();

                        }else if(isValidMatchType && matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_PHONE_NUMBER)){
                            // go to warning screen
                            mBtnForwardRegLayout.performClick();

                        }else if(isValidMatchType && matchType.trim().equalsIgnoreCase(GlobalCommonValues.MATCH_TYPE_BOTH)){
                            //Display welcome back alert dialog
                            GlobalConfig_Methods.displayWelcomeBackDialog(getActivity());
                            MainBaseActivity.isReturningUser=true;
                        }*/

                        // Call getBBID service to fetch user details
                        /*GetBBID mBBID = new GetBBID(getActivity(), true);
                        mBBID.getBBID();*/

                        VerifyingRegistrationFragment.isAppUserRegistered = true;

                        MainBaseActivity.isReturningUser = false;

                        //Stop Registration completion check service
                        stopRegistrationService();

                    }
                    //////////////////---------------
                } else if (get_Response.getReponseCode().equals(
                        GlobalCommonValues.FAILURE_CODE)
                        || get_Response.getReponseCode().equals(
                        GlobalCommonValues.FAILURE_CODE_1)
                        || get_Response.getReponseCode().equals(
                        GlobalCommonValues.FAILURE_CODE_5)
                        || get_Response.getReponseCode().equals(
                        GlobalCommonValues.FAILURE_CODE_2)
                        || get_Response.getReponseCode().equals(
                        GlobalCommonValues.FAILURE_CODE_3)
                        || get_Response.getReponseCode().equals(
                        GlobalCommonValues.FAILURE_CODE_4)) {
                    if (mActivity instanceof MainBaseActivity) {
                        dialogErrorMessage.newInstance("", ((MainBaseActivity) mActivity), get_Response.getMessage(), "", null);
                        dialogErrorMessage.show(((MainBaseActivity) mActivity).getSupportFragmentManager(), TAG);
                    } else if (mActivity instanceof HomeScreenActivity) {
                        dialogErrorMessage.newInstance("", ((HomeScreenActivity) mActivity), get_Response.getMessage(), "", null);
                        dialogErrorMessage.show(((HomeScreenActivity) mActivity).getSupportFragmentManager(), TAG);
                    }
                }
            } else {
                Log.d(TAG, response);
                ShowDialog.alert(mActivity, "", getResources().getString(R.string.improper_response_network));
            }
        } catch (Exception e) {
            e.getMessage();
            GlobalConfig_Methods.cancelRegistration(getContext(), saveState.getPassCode(getActivity()));
        }
    }


    //<------------------- User Registration block ends  --------------------------------->

    //<------------------- Parental Consent block starts --------------------------------->

    /**
     * function to check
     */

    void actionOnUnderAge() {

        if ((saveState.getIS_UNDERAGE(getActivity())) && !(saveState.getParentEmailId(getActivity()).
                trim().equalsIgnoreCase(""))) {

            // call web service to check parental consent status
            checkparentalStatus();

        } else {
            String mUserName = getUserName();
            if (mUserName != null && !mUserName.trim().equalsIgnoreCase("")) {
                saveState.setUSER_NAME_TEMP(getActivity(), mUserName);
            }

            if (mActivity instanceof MainBaseActivity) {
                ((MainBaseActivity) mActivity).setFragment(new ParentalConsentFragmentScreen());
            } else if (mActivity instanceof HomeScreenActivity) {
                ((HomeScreenActivity) mActivity).setFragment(new ParentalConsentFragmentScreen());
            }
        }
    }


    //Method to check parental consent status
    private void checkparentalStatus() {
        ParentConsentStatusBean mObjParentalConsent = new ParentConsentStatusBean();
        mObjParentalConsent.setDevice("android");
        mObjParentalConsent.setDevice_id(saveState.getDeviceId(mActivity));
        parentalConsentStatus(mObjParentalConsent);
    }

    /**
     * request for Parent consent status bean
     *
     * @param :ParentConsentStatusBean
     */
    private void parentalConsentStatus(ParentConsentStatusBean mObjParentalConsent) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(mObjParentalConsent);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivity,
                    GlobalCommonValues.PARENT_CONSENT_STATUS, stringEntity,
                    parentConsentStatusResponseHandler,
                    mActivity.getString(R.string.private_key), "");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // async task to check parental consent status
    AsyncHttpResponseHandler parentConsentStatusResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            if ((!progress.isShowing()))
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("parentConsentStatus", "OnSuccess", response.toString());
                    getResponseParentConsentStatus(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
                if (progress != null && progress.isShowing())
                    progress.dismiss();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("parentConsentStatus", "OnFailure", response);
            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /**
     * handle response for the request being made to check parent consent status
     *
     * @param response
     */
    private void getResponseParentConsentStatus(String response) {

        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
                gson = new Gson();
                ParentConsentStatusResponse get_Response = gson.fromJson(response, ParentConsentStatusResponse.class);
                if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE)) {

                    if (get_Response.getData().get(get_Response.getData().size() - 1).is_consent.equalsIgnoreCase("agree")) {

//                        btnRegister.setEnabled(true);
//                        btnRegister.setBackgroundResource(R.drawable.button_bg_yes_green);
//                        btnParentalConsentStatus.setBackgroundResource(R.drawable.check);
                    } else if (get_Response.getData().get(get_Response.getData().size() - 1).is_consent.equalsIgnoreCase("pending")) {

//                        btnRegister.setEnabled(false);
//                        btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
//                        btnParentalConsentStatus.setBackgroundResource(R.drawable.xclaimation);
                    } else if (get_Response.getData().get(get_Response.getData().size() - 1).is_consent.equalsIgnoreCase("disagree")) {
                        {

//                        btnRegister.setEnabled(false);
//                        btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
//                        btnParentalConsentStatus.setBackgroundResource(R.drawable.cross_red);
                        }
                    }

                } else if (get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_1)) {

                    if (progress != null && progress.isShowing())
                        progress.dismiss();
                }
            } else {
                Log.d("improper_response", response);
                ShowDialog.alert(
                        mActivity,
                        "",
                        getResources().getString(
                                R.string.improper_response_network));
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }


    //<------------------- Parental Consent block end    ---------------------------------->


    //<------------------- Screen Validation block starts --------------------------------->

    boolean validatingScreen() {
        LinearLayout returnLinearLayout = getLayoutVisiblity();
        if (returnLinearLayout == mReg1Layout) {
            if (getUserName().length() <= 0) {
                GlobalConfig_Methods.displayAlertDialog("Please enter following field:\nName", getActivity());
                return false;
            } else if (getAgeGroup().equalsIgnoreCase(ageGroupArray[0])) {
                GlobalConfig_Methods.displayAlertDialog("Please select age group", getActivity());
                return false;
            }
        } else if (returnLinearLayout == mReg2Layout) {
            if (getPhoneNumber().length() <= 0) {
                GlobalConfig_Methods.displayAlertDialog("Please enter following field:\nPhone Number", getActivity());
                return false;
            }

        } else if (returnLinearLayout == mReg3Layout) {
            if (mReg3ImViewUser.getDrawable() == null) {
                enableDisable(false);// disable create profile button
                GlobalConfig_Methods.displayAlertDialog(getResources().getString(R.string.txtPopupImage), getActivity());
                return false;
            }
        } else if (returnLinearLayout == mRegAlertCloudBackUplayout) {
            if (!validateBackupRequest()) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    //<------------------- Screen Validation block starts --------------------------------->


    //<------------------- Spinner item click listener implementation block starts --------->

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.reg1SpAgeGroup) {
           /*if(((String)parent.getSelectedItem()).equalsIgnoreCase(ageGroupArray[1]) && !isParentalConsentComplete){
               actionOnUnderAge();
               isParentalConsentComplete = true;
           }*/
        }
        if (parent.getId() == R.id.reg2SpCountryRegion) {
            System.out.println("position " + position + " data" + parent.getSelectedItem());
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //<------------------- Spinner item click listener implementation block ends ---------------------->

    /**
     * Method to stop the registration service
     */
    private void stopRegistrationService() {
        boolean isServiceRunning = GlobalConfig_Methods.isServiceRunning(mActivity, "com.tnc.service.RegistrationCheckService");
        if (mIntentRegistrationService != null && isServiceRunning)
            getActivity().stopService(mIntentRegistrationService);
    }

    /**
     * Method to go to send sms screen
     */
    private void setSMSDetails() {

        //Clear preferences and database values
//        GlobalConfig_Methods.clearDataBaseValues(getActivity());
//        GlobalConfig_Methods.clearRegsitrationPreferences(getActivity());

        String mCountryName = getCountryName();

        if (mCountryName.equalsIgnoreCase("USA") || mCountryName.equalsIgnoreCase("Canada")) {
            number = "+18582017500"; //"+18582606130";
            number_type = "dedicated";
            country_name = "US";
        } else if (mCountryName.equalsIgnoreCase("India")) {
            number = "+919810805053";
            number_type = "shared";
            country_name = "INDIA";
        } else {
            number = "+18582017500"; //"+18582606130";
            number_type = "dedicated";
            country_name = "OTHER";
        }

        // call method to send sms
        goToSendSMSScreen();
    }

    /**
     * Method to send sms
     */
    private void goToSendSMSScreen() {
        boolean isAirplaneModeON = false;

        try {
            isAirplaneModeON = GlobalConfig_Methods.isAirplaneModeOn(mActivity);
        } catch (Exception e) {
            e.getMessage();
        }

        if (isAirplaneModeON) {
            GlobalConfig_Methods.showSimErrorDialog(mActivity,
                    getResources().getString(R.string.txtAirplaneModeAlert));
        } else {
            String mSimState = GlobalConfig_Methods.checkSimState(mActivity);
            if (mSimState.equalsIgnoreCase("success") || !mSimState.equalsIgnoreCase("success")) {
                SendSMSFullScreenDialogFragment dialog = null;
                if (mActivity instanceof MainBaseActivity) {
                    dialog = new SendSMSFullScreenDialogFragment();
                    dialog.newInstance((MainBaseActivity) mActivity, "", number,
                            number_type, country_name, true);
                    ((MainBaseActivity) mActivity).setFragment(dialog);
                } else if (mActivity instanceof HomeScreenActivity) {
                    dialog = new SendSMSFullScreenDialogFragment();
                    dialog.newInstance((HomeScreenActivity) mActivity, "", number,
                            number_type, country_name, true);
                    ((HomeScreenActivity) mActivity).setFragment(dialog);
                }
            } else {
                GlobalConfig_Methods.showSimErrorDialog(mActivity, mSimState);
            }
        }
    }

    private class StyleTextAsyncTask extends AsyncTask<Void, Void, SpannableString> {

        private String stringToUpdate = "";
        private int startIndex, endIndex, startIndex2, endIndex2;
        private boolean isDoubleStyle = false;
        private TextView textView;

        StyleTextAsyncTask(TextView textView, String stringToUpdate, int startIndex, int endIndex, boolean isDoubleStyle,
                           int startIndex2, int endIndex2) {
            this.textView = textView;
            this.stringToUpdate = stringToUpdate;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.startIndex2 = startIndex2;
            this.endIndex2 = endIndex2;
            this.isDoubleStyle = isDoubleStyle;
        }

        @Override
        protected SpannableString doInBackground(Void... params) {
            SpannableString styledText = new SpannableString(stringToUpdate);

            styledText.setSpan(new UnderlineSpan(), startIndex, endIndex, 0);//where first 0 shows the starting and stringToUpdate.length() shows the ending span.if you want to span only part of it than you can change these values like 5,8 then it will underline part of it.
            if (isDoubleStyle) {
                styledText.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return styledText;
        }

        @Override
        protected void onPostExecute(SpannableString styledText) {
            textView.setText(styledText);
        }
    }

    /**
     * Method to upload tiles backup
     */
    private void uploadTilesBackUp() {
        // in case of guest user registration

        if (DBQuery.getAllTiles(getActivity()).size() > 0) {
            saveState.setUploadBackupRequested(mActivity, true);
        }
    }

    /**
     * BroadcastReceiver gets fired when we got a response from the server after sending the contacts
     *
     * @author a3logics
     */
    public class ReceiverGetRegisteredUsers extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {

            if (!(getUserVisibleHint()) || !(UserRegistrationFragment.this.isVisible())) {
                return;
            }

            VerifyingRegistrationFragment.isNotify = false;
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            ArrayList<BBContactsBean> listBBContacts = DBQuery.getAllBBContactsOrdered(mActivity);
            if (listBBContacts.isEmpty()) {
                Intent intent = new Intent(getActivity(), HomeScreenActivity.class);
                intent.putExtra(GlobalCommonValues.IS_CREATE_CHAT_BUTTON, true);
                // go to home screen
                if (mActivity instanceof MainBaseActivity) {
                    startActivity(intent);
                    ((MainBaseActivity) mActivity).finish();
                } else if (mActivity instanceof HomeScreenActivity) {
                    startActivity(intent);
                    ((HomeScreenActivity) mActivity).finish();
                }
            } else if (listBBContacts.size() > 0) {
                // In case user selected yes to notify friends for the your availability on TnC
                TnCUsers_NotifyFragmentRegistration objFragment = new TnCUsers_NotifyFragmentRegistration();
                objFragment.newInstance("new registration");
                if (mActivity instanceof MainBaseActivity)
                    ((MainBaseActivity) mActivity).setFragment(objFragment);
                if (mActivity instanceof HomeScreenActivity)
                    ((HomeScreenActivity) mActivity).setFragment(objFragment);
            }
        }
    }

    /**
     * Method to validate backup request
     */
    private boolean validateBackupRequest() {

        boolean idValidCloudRequest = false;

        ImageRequestDialog dialogImageRequest = new ImageRequestDialog();

        if (isReturningUser()) {
            if (mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")) {
                dialogImageRequest.setCancelable(false);
                dialogImageRequest.newInstance("", mActivityTabs, Html
                                .fromHtml(
                                        "Please provide backup key").toString(),
                        "", null);
                dialogImageRequest.show(getChildFragmentManager(), "test");
                idValidCloudRequest = false;

            } else {
                idValidCloudRequest = true;
            }
        } else {
            // Set values
            String strPhone = mRegPhoneNumberAlertEdtPhone.getText().toString();
            String[] arrayNumber = new String[25];

            arrayNumber = strPhone.split(GlobalCommonValues.HYPHEN);

            mCountryCode = "";
            contactNumber = "";

            for (int i = 0; i < arrayNumber.length; i++) {
                if (i == 0) {
                    mCountryCode = arrayNumber[0];
                } else if (i == 1) {
                    contactNumber = arrayNumber[1];
                }
            }

            if (!mCountryCode.trim().equals("") && !contactNumber.trim().equals("")
                    && mCountryCode.concat(contactNumber).trim().length() > 7
                    && mCountryCode.concat(contactNumber).trim().length() <= 15
                    && !mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")) {
                /*displayConfirmationPopup("warning");*/
                idValidCloudRequest = true;
            } else {
                if (mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && mCountryCode.trim().equals("")
                        && contactNumber.trim().equals("")) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs, Html
                                        .fromHtml(
                                                "Please provide backup key, country code "
                                                        + "& Telephone Number").toString(),
                                "", null);
                        dialogImageRequest.show(getChildFragmentManager(), "test");
                        idValidCloudRequest = false;
                    }
                } else if (mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && mCountryCode.equals("")) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs, Html
                                        .fromHtml(
                                                "Please provide backup key<br>"
                                                        + "& country code").toString(),
                                "", null);
                        dialogImageRequest.show(getChildFragmentManager(), "test");
                        idValidCloudRequest = false;
                    }
                } else if (mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && contactNumber.trim().equals("")) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs, Html
                                        .fromHtml(
                                                "Please provide backup key<br>"
                                                        + "& Telephone Number").toString(),
                                "", null);
                        dialogImageRequest.show(getChildFragmentManager(), "test");
                        idValidCloudRequest = false;
                    }
                } else if (mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && !(mCountryCode.concat(contactNumber).equals(""))) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs,
                                "Please provide backup key", "", null);
                        dialogImageRequest.show(getChildFragmentManager(),
                                "test");
                        idValidCloudRequest = false;
                    }
                } else if (!mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && mCountryCode.concat(contactNumber).trim().equals("")) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs,
                                "Please enter Telephone Number", "", null);
                        dialogImageRequest.show(getChildFragmentManager(), "test");
                        idValidCloudRequest = false;
                    }
                } else if (!mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && mCountryCode.trim().equals("") && !contactNumber.trim().equals("")) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs,
                                "Please enter country code", "", null);
                        dialogImageRequest.show(getChildFragmentManager(), "test");
                        idValidCloudRequest = false;
                    }
                } else if (!mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().equals("")
                        && !mCountryCode.trim().equals("") && contactNumber.trim().equals("")) {
                    if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                        dialogImageRequest.setCancelable(false);
                        dialogImageRequest.newInstance("", mActivityTabs,
                                "Please enter Telephone Number", "", null);
                        dialogImageRequest.show(getChildFragmentManager(), "test");
                        idValidCloudRequest = false;
                    }
                } else {
                    if (mCountryCode.concat(contactNumber).trim().length() < 7) {
                        if (dialogImageRequest != null && !dialogImageRequest.isVisible()) {
                            dialogImageRequest.setCancelable(false);
                            dialogImageRequest.newInstance("", mActivityTabs,
                                    "Contact No length cannot be less than 7 digits", "",
                                    null);
                            dialogImageRequest.show(getChildFragmentManager(), "test");
                            idValidCloudRequest = false;
                        }
                    } else {
                        /*displayConfirmationPopup("warning");*/
                        idValidCloudRequest = true;
                    }
                }
            }
        }
        return idValidCloudRequest;
    }

    /**
     * check internet connection
     */
    public void checkInternetCloudBackup() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            CheckReturningUserBean cloudRecoverSamePhone = null;
            if (isReturningUser()) {
                cloudRecoverSamePhone = new CheckReturningUserBean(
                        mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().toUpperCase(),
                        "", "");
            } else {
                cloudRecoverSamePhone = new CheckReturningUserBean(
                        mRegPhoneNumberAlertEdtCloudKey.getText().toString().trim().toUpperCase(),
                        mCountryCode, contactNumber);
            }
            checkReturningUser(cloudRecoverSamePhone);
        } else {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);

        }
    }

    /**
     * request to the server
     *
     * @param cloudRecoverSamePhone
     */
    private void checkReturningUser(CheckReturningUserBean cloudRecoverSamePhone) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(cloudRecoverSamePhone);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.check_returning_user, stringEntity,
                    checkReturningUserResponseHandle,
                    mActivityTabs.getString(R.string.private_key), "");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // async task to check returning user details
    AsyncHttpResponseHandler checkReturningUserResponseHandle = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            if ((!progress.isShowing()))
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("CheckReturningUserResponseHandle", "OnSuccess", response.toString());
                    getResponseBackupSamePhone(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("CheckReturningUserResponseHandle", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /**
     * handling response from the server for the request being sent to check the returning user
     *
     * @param response
     */
    private void getResponseBackupSamePhone(String response) {
        try {
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>") || response.contains("php")) {
                response2 = response.substring(response.indexOf("response_code") - 2, response.length());
            } else {
                response2 = response;
            }

            gson = new Gson();
            CheckReturningUserResponse get_Response = gson.fromJson(response2, CheckReturningUserResponse.class);

            // In case of success/failure in the response send the response to the
            // back screen

            if (get_Response.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)) {

                //set premium user value
                if (get_Response.getData().is_premium_user.trim().equalsIgnoreCase(GlobalCommonValues.YES)) {
                    saveState.setISPREMIUMUSER(mActivityTabs, true);
                } else if (get_Response.getData().is_premium_user.trim().equalsIgnoreCase(GlobalCommonValues.NO)) {
                    saveState.setISPREMIUMUSER(mActivityTabs, false);
                }

                if (mReturningUser != null) {
                    mReturningUser.setAction(response2);
                }
            } else if (get_Response.getResponse_code().equals(
                    GlobalCommonValues.FAILURE_CODE)) {
                displayConfirmationPopup(GlobalCommonValues.RETRY);
            } else if (get_Response.getResponse_code().equals(
                    GlobalCommonValues.FAILURE_CODE_1)) {
                displayConfirmationPopup(GlobalCommonValues.RETRY);
            } else if (get_Response.getResponse_code().equals(
                    GlobalCommonValues.FAILURE_CODE_6)) {
                displayConfirmationPopup(GlobalCommonValues.RETRY);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to display confirmation popup
     *
     * @param message
     */
    private void displayConfirmationPopup(String message) {
        MessageDeleteConfirmation dialog = new MessageDeleteConfirmation();
        if (message.equals("warning")) {
            dialog.newInstance("", mActivityTabs, "Please be aware that if the user at " +
                    mCountryCode + " " + contactNumber + " starts using the App, your account will be deactivated without notice. Do you like to proceed", "", iConfirmDeactivate);
        } else if (message.equals("retry")) {
            dialog.newInstance("", mActivityTabs, "Either the Backup Key or the Phone Number is incorrect. Do you like to try again?", "", iConfirmDeactivate);
        }
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "test");
    }

    /**
     * Interface to send the request to the server for checking returning user case
     */
    private INotifyGalleryDialog iConfirmDeactivate = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            checkInternetCloudBackup();
        }

        @Override
        public void no() {
            /*if(mReturningUser!=null){
                mReturningUser.setAction("new_user");
            }*/
            //then go to home screen
            gotoHomeScreen(false);
        }
    };

    /**
     * interface to handle call to returning user case ,if user clicked the returning user
     */
    INotifyAction mReturningUser = new INotifyAction() {
        @Override
        public void setAction(String action) {
            if (action.contains(GlobalCommonValues.SUCCESS_CODE)) {
                // checking app registration to prevent backdoor entry of the registration
                VerifyingRegistrationFragment.isAppUserRegistered = true;

                stopRegistrationService();

                //set premium user value
                //saveState.setISPREMIUMUSER(getActivity(), IS_PREMIUM_USER);
                saveState.setISRETURNINGUSER(getActivity(), false);
                saveState.setIS_RecentRegistration(getActivity(), true);
                saveState.setIS_NUMBER_CHANGED(getActivity(), true);
                saveState.setUPDATE_EMERGENCY(getActivity(), true);

                //clear temp username
                GlobalConfig_Methods.clearTempUserName(getActivity());

                if (progress != null && progress.isShowing())
                    progress.dismiss();

                String _title = "";
                String _message = "Welcome Back to " + getResources().getString(R.string.app_name) + "!\nDo you want to recover your cloud backup?";
                WelcomeBackReg_RestoreDialog welcomeBackRegistrationDialog =
                        new WelcomeBackReg_RestoreDialog();
                welcomeBackRegistrationDialog.setCancelable(false);
                welcomeBackRegistrationDialog.newInstance(_title, getActivity(), _message, "");
                if (getActivity() instanceof MainBaseActivity) {
                    welcomeBackRegistrationDialog.show(((MainBaseActivity) getActivity()).getSupportFragmentManager(), "test");
                } else if (getActivity() instanceof HomeScreenActivity) {
                    welcomeBackRegistrationDialog.show(((HomeScreenActivity) getActivity()).getSupportFragmentManager(), "test");
                }

                MainBaseActivity.isReturningUser = true;
                if (saveState.isRegistered(getActivity()) && !saveState.getPublicKey(getActivity()).trim().equals("")) {
                    // Call getBBID service to fetch user details
                    GetBBID mBBID = new GetBBID(getActivity(), true);
                    mBBID.getBBID();
                }
            }
        }
    };


    private BroadcastReceiver receiverz = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                mreg2EtOTPNumber.setText(message);
                //Do whatever you want with the code here
            }
        }
    };

    class AuthUtils {
        /**
         * Action register
         *
         * @param email
         * @param phoneNumber
         */
        void createUser(String email, String phoneNumber) {
            mAuth.createUserWithEmailAndPassword(email, phoneNumber)
                    .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {


                            } else {
                                initNewUserInfo();
                                Toast.makeText(mActivity, "Register and Login success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(mActivity, HomeScreenActivity.class));
                                mActivity.finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
            ;
        }


        /**
         * Khoi tao thong tin mac dinh cho tai khoan moi
         */
        void initNewUserInfo() {
            User newUser = new User();
            newUser.email = user.getEmail();
            newUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
//            newUser.phoneNumber = StaticConfig.STR_DEFAULT_BASE64;
            FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
        }


    }
}


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//         appCompatActivity=(AppCompatActivity)activity;
//    }

