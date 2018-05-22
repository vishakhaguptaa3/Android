package com.tnc.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.SocialNetwork.Facebook.FaceBookApiResponse;
import com.tnc.activities.UserLocationDisplayActivity;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.bean.ImageRequestBean;
import com.tnc.bean.UpdateUserInfoBean;
import com.tnc.bean.UserInfoBeanWithoutImage;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.BackupConfirmationDialog;
import com.tnc.dialog.DeleteChatConfirmationDialog;
import com.tnc.dialog.EmailVerificationDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.ImageUploadConfirmation;
import com.tnc.dialog.NotifyImageDialog;
import com.tnc.dialog.SelectCountryDialog;
import com.tnc.dialog.SelectImagePopup;
import com.tnc.dialog.ShowDialog;
import com.tnc.dialog.TileUpdateSuccessDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.interfaces.INotifyImageNotifyDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.utility.RegexValidationUtility;
import com.tnc.webresponse.CheckNumberResponseBean;
import com.tnc.webresponse.GetBBIDResponseBeanData;
import com.tnc.webresponse.ReConfirmEmailResponse;
import com.tnc.webresponse.ResendEmailResponseBean;
import com.tnc.webresponse.UpdateUserInfoResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

/**
 * class to display user info in settings 
 *  @author a3logics
 */
public class UserInfoFragment extends BaseFragmentTabs implements OnClickListener
{
    private INotifyGalleryDialog iNotify;
    private SharedPreference saveState;
    private FrameLayout flBackArrow,flInformationButton;
    private TextView tvTitle;
    private Button btnBack,btnHome;
    private TextView tvUserInfo,tvAddPhoto,tvContactName,tvContactNumber,tvEmailId;//,tvChangeNumber,
    //	tvNotifyContacts;
    GoogleSignInClient mGoogleSignInClient;
    private EditText etContactName,etContactNumber,etEmailId;
    private Button btnUpdate, btnLocationDisplay;
    private CheckBox chkBoxDefaultImage;
    private ImageView imViewUser,imViewPremiumUserIcon;
    private TransparentProgressDialog progress;
    private Gson gson;
    private String picturePath = "";
    private Uri fileUri;
    private Bitmap bitmapUserImage = null;
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    boolean isImageChanged=false;
    private String userImageURL="";
    private String filePath="";
    private String fileName="";
    private SharedPreference mSharedPreference;
    private Bitmap savedBitmap=null;
    boolean isCheckedStateChanged=false;
    private int count =0;
    private Button btnEmailVerificationStatus,btnChangeNumber,btnNotifyContacts;
    private LinearLayout llNumberHolder;
    private TextView tvCountryName;
    private ImageView imViewFlag,imagebroadcastview;
    //	RelativeLayout rlKeyHolder;
    private Button btnKey;
    private final int PICK_IMAGE_REQUEST = 210;
    boolean mIsDisplayProgress=true;
    private CallbackManager callbackManager;
    int RC_SIGN_IN = 23;
    String TAG = UserInfoFragment.class.getSimpleName();
    boolean isGoogleImageDialog = false;
    boolean isFaceBookImageDialog = false;

    public UserInfoFragment newInstance(INotifyGalleryDialog iNotify)
    {
        UserInfoFragment frag = new UserInfoFragment();
        this.iNotify=iNotify;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.userinfo, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view){
        saveState=new SharedPreference();
        progress = new TransparentProgressDialog(mActivityTabs,
                R.drawable.customspinner);
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvAddPhoto=(TextView) view.findViewById(R.id.tvAddPhoto);
        tvContactName=(TextView) view.findViewById(R.id.tvContactName);
        tvContactNumber=(TextView) view.findViewById(R.id.tvContactNumber);
        tvEmailId=(TextView) view.findViewById(R.id.tvEmailId);
        tvCountryName=(TextView) view.findViewById(R.id.tvCountryName);
        llNumberHolder=(LinearLayout) view.findViewById(R.id.llNumberHolder);
        etContactName=(EditText) view.findViewById(R.id.etContactName);
        etContactNumber=(EditText) view.findViewById(R.id.etContactNumber);
        etContactNumber.setEnabled(false);
        etEmailId=(EditText) view.findViewById(R.id.etEmailId);
        btnUpdate=(Button) view.findViewById(R.id.btnUpdate);
        btnLocationDisplay= (Button) view.findViewById(R.id.btnLocationDisplay);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
        btnHome=(Button) view.findViewById(R.id.btnHome);
        tvUserInfo=(TextView) view.findViewById(R.id.tvUserInfo);
        chkBoxDefaultImage=(CheckBox) view.findViewById(R.id.chkBoxDefaultImage);
        imViewUser=(ImageView) view.findViewById(R.id.imViewUser);
        imViewPremiumUserIcon=(ImageView) view.findViewById(R.id.imViewPremiumUserIcon);
        btnEmailVerificationStatus=(Button) view.findViewById(R.id.btnEmailVerificationStatus);
        btnChangeNumber=(Button) view.findViewById(R.id.btnChangeNumber);
        btnNotifyContacts=(Button) view.findViewById(R.id.btnNotifyContacts);
        imViewFlag=(ImageView)view.findViewById(R.id.imViewFlag);
        imagebroadcastview = (ImageView)view.findViewById(R.id.imagebroadcastview);
        mSharedPreference = new SharedPreference();
        callbackManager = ((HomeScreenActivity)getActivity()).getCallbackManager();
        //		rlKeyHolder = (RelativeLayout) view.findViewById(R.id.rlKeyHolder);

        if(mSharedPreference.isImageChange(getActivity())){
            imagebroadcastview.setEnabled(true);
            imagebroadcastview.setImageDrawable(getActivity().getDrawable(R.drawable.image_broadcast_icon));
        }else{
            imagebroadcastview.setEnabled(false);
            imagebroadcastview.setImageDrawable(getActivity().getDrawable(R.drawable.image_broadcast_icon_grey));
        }
        btnKey = (Button) view.findViewById(R.id.btnKey);
        btnKey.setVisibility(View.GONE);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvUserInfo, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(), etContactName,
                "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(), etEmailId,
                "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfButton(getActivity(), btnUpdate,
                "fonts/Roboto-Regular_1.ttf");
        tvAddPhoto.setText(Html.fromHtml("Add<br>Photo").toString());
        btnBack.setOnClickListener(this);
        imViewUser.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        //		rlKeyHolder.setOnClickListener(this);
        btnKey.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        imagebroadcastview.setOnClickListener(this);

        UserRegistration1.isOnUserRegistration = true;
        filePath=String.valueOf(Environment.getExternalStorageDirectory()+File.separator+"TNC/images/");
        fileName="userimage.jpg";
        btnChangeNumber.setOnClickListener(this);
        btnNotifyContacts.setOnClickListener(this);
        btnLocationDisplay.setOnClickListener(this);
        btnNotifyContacts.setVisibility(View.GONE);

        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);
        btnHome.setBackgroundResource(R.drawable.refresh);

        LinearLayout.LayoutParams paramsWeight= new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,84);
        etContactNumber.setLayoutParams(paramsWeight);

        if(saveState.getISPREMIUMUSER(mActivityTabs)){
            imViewPremiumUserIcon.setVisibility(View.VISIBLE);
        }else{
            imViewPremiumUserIcon.setVisibility(View.GONE);
        }

        if(saveState.isRegistered(mActivityTabs)){
            String countryNameFromDB = DBQuery.getCountryFlag(mActivityTabs,saveState.getCountryName(mActivityTabs));
            if(countryNameFromDB!=null && !countryNameFromDB.trim().equals("")){
                if(countryNameFromDB.contains("-")){
                    countryNameFromDB=countryNameFromDB.replaceAll("-","");
                }
                if(countryNameFromDB.contains(" ")){
                    countryNameFromDB=countryNameFromDB.replaceAll(" ","");
                }
                if(countryNameFromDB.contains("'")){
                    countryNameFromDB=countryNameFromDB.replaceAll("'","");
                }
                try {
                    int res = getActivity().getResources().getIdentifier(countryNameFromDB.substring(0,countryNameFromDB.indexOf(".")),"drawable", getActivity().getApplicationContext().getPackageName());
                    imViewFlag.setBackgroundResource(res);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }


        if(NetworkConnection.isNetworkAvailable(mActivityTabs)){
            //Calling webservice to check is the contact number being changed/modified
            checkInternetConnectionCheckNumber();
        }

        else if(!NetworkConnection.isNetworkAvailable(mActivityTabs)){
            // In case internet connection is not available
            if(saveState.getIS_NUMBER_CHANGED(mActivityTabs)){
                btnNotifyContacts.setVisibility(View.VISIBLE);
            }else{
                btnNotifyContacts.setVisibility(View.GONE);
            }
        }

        LinearLayout.LayoutParams paramsWeightTextBoxNumber= new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,84);
        etContactNumber.setLayoutParams(paramsWeightTextBoxNumber);

        savedBitmap=GlobalConfig_Methods.getBitmapFromFile(filePath,fileName);
        if(savedBitmap!=null){

            imViewUser.setImageBitmap(savedBitmap);
        }
		/*if(savedBitmap!=null){
			imViewUser.setImageBitmap(savedBitmap);
		}*/

        if(saveState.getCountryName(mActivityTabs)!=null && !saveState.getCountryName(mActivityTabs).equalsIgnoreCase("null"))
        {
            if(saveState.getCountryName(mActivityTabs).toUpperCase().contains("COTE")){
                String mCountryname = "";
                mCountryname = saveState.getCountryName(mActivityTabs).substring(0,7)+ saveState.getCountryName(mActivityTabs).substring(8,saveState.getCountryName(mActivityTabs).length());
                tvCountryName.setText(mCountryname);
            }else{
                tvCountryName.setText(saveState.getCountryName(mActivityTabs));
            }
        }

        if(saveState.getIsVerified(mActivityTabs)){
            btnEmailVerificationStatus.setBackgroundResource(R.drawable.check);
            btnEmailVerificationStatus.setEnabled(false);
            btnKey.setVisibility(View.VISIBLE);
        }
        else if(!saveState.getIsVerified(mActivityTabs)){
            btnEmailVerificationStatus.setBackgroundResource(R.drawable.xclaimation);
            btnEmailVerificationStatus.setEnabled(true);
            btnKey.setVisibility(View.GONE);
        }

        etContactName.setText(Uri.decode(saveState.getUserName(mActivityTabs)));
        //			String mNumber=saveState.getUserPhoneNumber(mActivityTabs);
        String mNumberDisplay=saveState.getCountryCode(mActivityTabs)+" "+saveState.getUserPhoneNumber(mActivityTabs);
        etContactNumber.setText(mNumberDisplay);
        if(saveState.isDefaultImage(mActivityTabs))
        {
            chkBoxDefaultImage.setChecked(true);
        }
        else{
            chkBoxDefaultImage.setChecked(false);
        }
        isCheckedStateChanged=false;
        if(!saveState.getUserMailID(getActivity()).trim().equals(""))
        {
            etEmailId.setText(Uri.decode(saveState.getUserMailID(getActivity())));
        }

        btnEmailVerificationStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=etEmailId.getText().toString();
                String message="";
                EmailVerificationDialog dialog = new EmailVerificationDialog();
                if(email.trim().equals(""))
                {
                    message="Email is not found.Please update email";
                    dialog.newInstance("",mActivityTabs,message,"",iNotifyAction);
                    dialog.setCancelable(false);
                    dialog.show(getChildFragmentManager(), "test");
                }
                else if(!email.trim().equals("")){
                    if(!RegexValidationUtility.isValidEmail(etEmailId.getText()
                            .toString().trim()))
                    {
                        ImageRequestDialog dialogAlertNotify = null;
                        dialogAlertNotify = new ImageRequestDialog();
                        dialogAlertNotify.setCancelable(false);
                        dialogAlertNotify.newInstance("", mActivityTabs,
                                "Please enter valid E-mail Id", "", null);
                        dialogAlertNotify.show(getChildFragmentManager(), "test");
                    }
                    else{
                        message="Your email not verified.Do you want to resend verification link?";
                        dialog.newInstance("",mActivityTabs,message,"",iNotifyAction);
                        dialog.setCancelable(false);
                        dialog.show(getChildFragmentManager(), "test");
                    }
                }
            }
        });

        chkBoxDefaultImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(NetworkConnection.isNetworkAvailable(mActivityTabs))
                {
                    if(count>0)
                        isCheckedStateChanged=true;
                    count++;

                }
                else{
                    isCheckedStateChanged=true;
                }
            }
        });

        ((HomeScreenActivity)getActivity()).getFragmentInstance(this);
        // Commeneted as a part of UAT # 591
		/*if(saveState.getIS_UNDERAGE(mActivityTabs)){
			etEmailId.setEnabled(false);
		}else{
			etEmailId.setEnabled(true);
		}*/

        //		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
    }

    /**
     * interface to handle call to web service to verify email
     */
    INotifyAction iNotifyAction = new INotifyAction() {
        @Override
        public void setAction(String action) {
            if(action.equals("verify"))
            {
                //call verify email webservice
                if(NetworkConnection.isNetworkAvailable(mActivityTabs))
                {
                    verifyEmail();
                } else {
                    GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
                }
            }
        }
    };

    //enable/disable check box
    private void setCheckBoxClickability() {
        if (((BitmapDrawable) imViewUser.getDrawable()) == null
                || ((BitmapDrawable) imViewUser.getDrawable()).getBitmap() == null) {
            bitmapUserImage = null;
        } else {
            bitmapUserImage = ((BitmapDrawable) imViewUser.getDrawable())
                    .getBitmap();
        }
        if (bitmapUserImage == null) {
            chkBoxDefaultImage.setClickable(false);
        } else if (bitmapUserImage != null) {
            chkBoxDefaultImage.setClickable(true);
            if (chkBoxDefaultImage.isChecked())
                chkBoxDefaultImage.setChecked(true);
            else
                chkBoxDefaultImage.setChecked(false);
        }

        if(!NetworkConnection.isNetworkAvailable(mActivityTabs))
        {
            if(saveState.isDefaultImage(mActivityTabs))
            {
                chkBoxDefaultImage.setChecked(true);
            }
            else{
                chkBoxDefaultImage.setChecked(false);
            }
        }
    }

    //method to handle call to the web service to reconfirmEmail
    private void verifyEmail(){
        MyHttpConnection.postWithoutHeaderWithoutJsonEntity(mActivityTabs,GlobalCommonValues.REEMAILCONFIRMATION,
                verifyEmailResponsehandler,mActivityTabs.getResources().getString(R.string.private_key),
                saveState.getPublicKey(mActivityTabs));
    }

    //Async task to reconfirmEmail
    AsyncHttpResponseHandler verifyEmailResponsehandler = new JsonHttpResponseHandler() {
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
                if(response!=null)
                {
                    Logs.writeLog("verifyEmailResponsehandler", "OnSuccess",response.toString());
                    getResponseReConfirmEmail(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("verifyEmailResponsehandler", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /**
     * handle response for the request being made to reconfirmemail
     *
     * @param response
     */
    private void getResponseReConfirmEmail(String response)
    {
        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response))
            {
                gson = new Gson();
                ImageRequestDialog dialog=new ImageRequestDialog();
                dialog.setCancelable(false);
                ReConfirmEmailResponse getResponse = gson.fromJson(response,ReConfirmEmailResponse.class);
                if (getResponse.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE))
                {
                    if(!etEmailId.getText().toString().trim().equals("")){
                        saveState.setUserMailID(getActivity(),Uri.decode(etEmailId.getText().toString()));
                    }

                    String messageText = "Successfully resent email link. " + " If you provided a valid email ID, an email will be sent to you. This might be in "
                            +"your Spam folder. It is important that you open the email and click the \'verify\' "
                            +"link to verify your email ID.";

                    dialog.newInstance("",mActivityTabs,messageText,"",null);
                }
                else{
                    dialog.newInstance("",mActivityTabs,getResponse.getResponse_message(),"",null);
                }
                dialog.show(getChildFragmentManager(), "test");
            }
        }catch (Exception e) {
            e.getMessage();
        }
    }
    /**
     * check availability of internet connection
     */
    public void checkInternetConnectionCheckNumber() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            checkNumberRequest();
        }
    }

    private void checkNumberRequest()
    {
        MyHttpConnection.getWithoutHeaderWithoutJsonEntity(mActivityTabs,GlobalCommonValues.CHECKNUMBER,
                checkNumberResponsehandler,mActivityTabs.getResources().getString(R.string.private_key),
                saveState.getPublicKey(mActivityTabs));
    }

    //Async task to checkNumber
    AsyncHttpResponseHandler checkNumberResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null){
                    Logs.writeLog("CheckNumberChange", "OnSuccess",response.toString());
                    getResponseCheckNumber(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("CheckNumberChange", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    /**
     * handle response for the request being made to check the number
     *
     * @param response
     */
    private void getResponseCheckNumber(String response){
        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)){
                gson = new Gson();
                CheckNumberResponseBean getResponse = gson.fromJson(response,CheckNumberResponseBean.class);
                if (getResponse.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
                    if(getResponse.data.is_number_change.equalsIgnoreCase("yes")){
                        btnNotifyContacts.setVisibility(View.VISIBLE);
                        saveState.setIS_NUMBER_CHANGED(mActivityTabs, true);
                        getBBID();
                    }else{
                        if(getResponse.data.is_number_change.equalsIgnoreCase("no")){
                            if(saveState.getIS_NUMBER_CHANGED(mActivityTabs)){
                                saveState.setIS_NUMBER_CHANGED(mActivityTabs, true);
                                btnNotifyContacts.setVisibility(View.VISIBLE);
                            }else{
                                saveState.setIS_NUMBER_CHANGED(mActivityTabs, false);
                                btnNotifyContacts.setVisibility(View.GONE);
                            }
                        }
                    }

					/*if(saveState.getIS_NUMBER_CHANGED(mActivityTabs)){
						btnNotifyContacts.setVisibility(View.VISIBLE);
					}*//*else{
						btnNotifyContacts.setVisibility(View.GONE);
					}*/
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * check availability of internet connection
     */
    public void checkInternetConnectionResendBackupKey() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            resendBackupKey();
        }else{
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }

    private void resendBackupKey(){
        MyHttpConnection.postHeaderWithoutJsonEntity(mActivityTabs,GlobalCommonValues.RESEND_BACKUP_KEY,
                resendBackupKeyResponsehandler,mActivityTabs.getResources().getString(R.string.private_key),
                saveState.getPublicKey(mActivityTabs));
    }

    //Async task to checkNumber
    AsyncHttpResponseHandler resendBackupKeyResponsehandler = new JsonHttpResponseHandler() {
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
                if(response!=null){
                    Logs.writeLog("ResendBackupKey", "OnSuccess",response.toString());
                    getResponseResendEmail(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("ResendBackupKey", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress.isShowing())
                progress.dismiss();
        }
    };

    /**
     * handle response for the request being made to check the number
     *
     * @param response
     */
    private void getResponseResendEmail(String response){
        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)){
                gson = new Gson();
                ImageRequestDialog dialog  = new ImageRequestDialog();;
                ResendEmailResponseBean getResponse = gson.fromJson(response,ResendEmailResponseBean.class);
                if (getResponse.getResponse_code().equals(GlobalCommonValues.SUCCESS_CODE)){
                    if(!etEmailId.getText().toString().trim().equals("")){
                        saveState.setUserMailID(getActivity(),Uri.decode(etEmailId.getText().toString()));
                    }
                    dialog.newInstance("",mActivityTabs,"Backup key sent successfully","",null);
                    dialog.setCancelable(false);
                    dialog.show(getChildFragmentManager(), "test");
                }else if (getResponse.getResponse_code().equals(
                        GlobalCommonValues.FAILURE_CODE)){
                    dialog.newInstance(getResponse.getResponse_message(),mActivityTabs," message","",null);
                    dialog.setCancelable(false);
                    dialog.show(getChildFragmentManager(), "test");
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    //interface to handle user action i.e. open camera/gallery
    INotifyGalleryDialog iNotifyImagePopSelection=new INotifyGalleryDialog() {
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
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(
                    Intent.createChooser(intent, "Complete action using"),
                    PICK_IMAGE_REQUEST);
			/*if (mActivityTabs instanceof MainBaseActivity) {
				((MainBaseActivity) mActivityTabs)
				.setFragment(new GalleryFragment());
			} else if (mActivityTabs instanceof HomeScreenActivity) {
				((HomeScreenActivity) mActivityTabs)
				.setFragment(new GalleryFragment());
			}*/
        }
    };

    /*INotifyGalleryDialog iNotifyImageConfirmation = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            NotifyImageDialog imageDialog =new NotifyImageDialog();
            imageDialog.setCancelable(true);
            imageDialog.newInstance(getActivity().getResources().getString(R.string.txtNotifyUser),getActivity(),iNotifyImageNotifyDialog);
            imageDialog.show(((HomeScreenActivity) mActivityTabs).getSupportFragmentManager(), "test");
        }

        @Override
        public void no() {

        }
    };*/

    /**
     * Interface to handle UI to display groups in popup (in case of image broadcast)
     */
    INotifyImageNotifyDialog iNotifyImageNotifyDialog = new INotifyImageNotifyDialog() {
        @Override
        public void yes(String categoryId) {

            ArrayList<Integer> listBBID = DBQuery.getBBIDforCategory(getActivity(), categoryId);
            //system.out.println("list of BBID"+ listBBID.toString());
            String mUserIds = "";
            int i=0;
            for(Integer userId : listBBID){
                if(i==0){
                    mUserIds = mUserIds + userId;
                    i++;
                }else{
                    mUserIds = mUserIds+","+userId;
                }
            }

            if(GlobalConfig_Methods.isValidList(listBBID)){
                checkInternetConnectionImageBroadCast(mUserIds);
            }else{
                String categoryStr  = "";
                String[] userIdStrArray = categoryId.split(",");
                if(userIdStrArray.length>1){
                    categoryStr = " in Selected groups";
                }else{
                    categoryStr = " in Selected group";
                }

                ImageRequestDialog mDialog = new ImageRequestDialog();
                mDialog.newInstance("", getActivity(), getResources().getString(R.string.txtimageupdate)+categoryStr, "", null);
                mDialog.setCancelable(false);
                mDialog.show(getActivity().getSupportFragmentManager(), "test");
            }
        }

        @Override
        public void no() {
            //Toast.makeText(mActivityTabs, "Press No", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * handle image cropping
     * @param isCamera Mode/Or From Gallery
     */
    private void cropImage(boolean isCamera) {
        if (!MainBaseActivity.selectedImagepath.equals("")) {
            MainBaseActivity._bitmap = null;
            Intent intent = new Intent(getActivity(), CropImage.class);
            if (isCamera)
                intent.putExtra(CropImage.IMAGE_PATH, picturePath);
            else
                intent.putExtra(CropImage.IMAGE_PATH,
                        MainBaseActivity.selectedImagepath);
            intent.putExtra(CropImage.SCALE, true);
            intent.putExtra(CropImage.ASPECT_X, 1);
            intent.putExtra(CropImage.ASPECT_Y, 1);
            getActivity().startActivityForResult(intent,
                    REQUEST_CODE_CROP_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                try {
                    Uri selectedImage = fileUri;
                    picturePath = selectedImage.getPath();
                    MainBaseActivity.selectedImagepath = picturePath;
                    if(GlobalConfig_Methods.isValidString(picturePath)){
                        cropImage(true);
                    }else{
                     displayAlertDialog(getResources().getString(R.string.txtValidImageAlert));
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }else if(requestCode==PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };

                if(GlobalConfig_Methods.isValidString(selectedImage.toString()) &&
                        GlobalConfig_Methods.isValidString(filePath[0])){
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    @SuppressWarnings("unused")
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    MainBaseActivity.selectedImagepath=picturePath;
                    Intent cropIntent=new Intent(getActivity(),CropImage.class);
                    cropIntent.putExtra(CropImage.IMAGE_PATH,MainBaseActivity.selectedImagepath);
                    cropIntent.putExtra(CropImage.SCALE, true);
                    cropIntent.putExtra(CropImage.ASPECT_X, 1);
                    cropIntent.putExtra(CropImage.ASPECT_Y, 1);
                    getActivity().startActivity(cropIntent);
                }else{
                    displayAlertDialog(getResources().getString(R.string.txtValidImageAlert));
                }


            }
        }
        setCheckBoxClickability();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isGoogleImageDialog){
            displayAlertDialog(getResources().getString(R.string.txtNoGoogleImage));
            return;
        }
        if(isFaceBookImageDialog){
            displayAlertDialog(getResources().getString(R.string.txtNoFacebookImage));
            return;
        }
        try {
            if (MainBaseActivity.isCameraCanceled) {
                MainBaseActivity._bitmap = null;
                MainBaseActivity.isImageSelected = false;
                MainBaseActivity.isImageRequested = false;
                ((HomeScreenActivity) mActivityTabs).objFragment = null;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri =GlobalConfig_Methods.getOutputImageUri();
                if (fileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                }
                startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
                MainBaseActivity.isCameraCanceled = false;
            }
            if (MainBaseActivity._bitmap != null) {
                MainBaseActivity._bitmap = GlobalConfig_Methods.getResizedBitmap(
                        MainBaseActivity._bitmap, 250, 250);
                isImageChanged=true;
                imViewUser.setImageBitmap(MainBaseActivity._bitmap);
            }
            setCheckBoxClickability();
        } catch (Exception e) {
            e.getMessage();
        }
//        getBBID();
    }

    //Method to call webservice to get the userInfo
    private void getBBID() {
        try {
            gson = new Gson();
            MyHttpConnection.postHeaderWithoutJsonEntity(mActivityTabs,
                    GlobalCommonValues.GET_BBID, getBBIDResponseHandler,
                    mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to get User Details
    AsyncHttpResponseHandler getBBIDResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            if ((!progress.isShowing())){
                if(mIsDisplayProgress)
                    progress.show();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("getBBIDUserInfoResponseHandler", "OnSuccess",
                            response.toString());
                    getResponseBBID(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("getBBIDUserInfoResponseHandler", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    /*  @param response
     * method to handle response of fetching the user Details/Info
     */
    private void getResponseBBID(String response) {
        try {
            String response2="";
            if(response.contains("</div>") || response.contains("<h4>") || response.contains("php")){
                response2=response.substring(response.indexOf("user_id")-2,response.length());
            }
            else{
                response2=response;
            }
            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                GetBBIDResponseBeanData get_Response = gson.fromJson(response2,GetBBIDResponseBeanData.class);
                saveState.setCountryCode(mActivityTabs,get_Response.country_code.toUpperCase());
                saveState.setUserName(mActivityTabs, Uri.decode(get_Response.name));

                //Set Premium User value
                if((get_Response.getIs_premium_user()!=null) && (get_Response.getIs_premium_user().equalsIgnoreCase("yes"))){
                    saveState.setISPREMIUMUSER(getActivity(), true);
                }else{
                    saveState.setISPREMIUMUSER(getActivity(), false);
                }

                if(mIsDisplayProgress)
                    etContactName.setText(Uri.decode(saveState.getUserName(mActivityTabs)));

                //				String number=get_Response.number.substring(get_Response.number.length()-10,get_Response.number.length());
                saveState.setUserPhoneNumber(mActivityTabs,get_Response.number);

                String countryName = get_Response.country_name.toUpperCase();
                if(countryName.contains("COTE")){
                    saveState.setCountryname(mActivityTabs,"COTE D''IVOIRE");
                }else{
                    saveState.setCountryname(mActivityTabs,get_Response.country_name.toUpperCase());
                }

                //				String mNumber=saveState.getUserPhoneNumber(mActivityTabs);
                String mNumberDisplay=saveState.getCountryCode(mActivityTabs)+" "+saveState.getUserPhoneNumber(mActivityTabs);
                if(mIsDisplayProgress)
                    etContactNumber.setText(Uri.decode(mNumberDisplay));

                if(saveState.getCountryName(mActivityTabs)!=null && !saveState.getCountryName(mActivityTabs).equalsIgnoreCase("null")){
                    //					if(mIsDisplayProgress)
                    //					{
                    if(saveState.getCountryName(mActivityTabs).toUpperCase().contains("COTE")){
                        String mCountryname = "";
                        mCountryname = saveState.getCountryName(mActivityTabs).substring(0,7)+
                                saveState.getCountryName(mActivityTabs).substring(8,saveState.getCountryName(mActivityTabs).length());
                        tvCountryName.setText(Uri.decode(mCountryname));
                    }else{
                        tvCountryName.setText(Uri.decode(saveState.getCountryName(mActivityTabs)));
                    }
                    //					}
                }

                if(get_Response.is_email_verified==1)
                    saveState.setIsVerified(mActivityTabs,true);
                else{
                    saveState.setIsVerified(mActivityTabs,false);
                }
                //				if(mIsDisplayProgress)
                //				{
                if(saveState.getIsVerified(mActivityTabs)){
                    btnEmailVerificationStatus.setBackgroundResource(R.drawable.check);
                    btnEmailVerificationStatus.setEnabled(false);
                    btnKey.setVisibility(View.VISIBLE);
                }
                else if(!saveState.getIsVerified(mActivityTabs)){
                    btnEmailVerificationStatus.setBackgroundResource(R.drawable.xclaimation);
                    btnEmailVerificationStatus.setEnabled(true);
                    btnKey.setVisibility(View.GONE);
                }
                //				}

                if(get_Response.email!=null && !get_Response.email.trim().equals("") &&
                        !get_Response.email.trim().equalsIgnoreCase("null")){
                    saveState.setUserMailID(getActivity(),Uri.decode(get_Response.email));
                    //					if(mIsDisplayProgress)
                    etEmailId.setText(Uri.decode(saveState.getUserMailID(getActivity())));
                }

                if(saveState.getEmergency(mActivityTabs).trim().equals("")){
                    String emergency = DBQuery.getEmergency(mActivityTabs,saveState.getCountryName(mActivityTabs));
                    saveState.setEmergency(mActivityTabs,emergency);
                }
                //				if(mIsDisplayProgress)
                //				{
                String flagIcon = DBQuery.getCountryFlag(mActivityTabs,saveState.getCountryName(mActivityTabs));
                if(flagIcon!=null && !flagIcon.trim().equals("")){
                    if(flagIcon.contains("-")){
                        flagIcon=flagIcon.replaceAll("-","");
                    }
                    if(flagIcon.contains(" ")){
                        flagIcon=flagIcon.replaceAll(" ","");
                    }
                    try {
                        int res = getActivity().getResources().getIdentifier(flagIcon.substring(0,flagIcon.indexOf(".")),"drawable", getActivity().getApplicationContext().getPackageName());
                        imViewFlag.setBackgroundResource(res);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                if(get_Response.image!=null && !get_Response.image.trim().equals("") && !get_Response.image.trim().equalsIgnoreCase("null")){
                    userImageURL=get_Response.image;
                    DownloadUserImageAsyncTask downloadUserImage = new DownloadUserImageAsyncTask();
                    downloadUserImage.execute();
                }
                else{
                    if(mIsDisplayProgress)
                        if (progress!=null && progress.isShowing())
                            progress.dismiss();
                }
                if (get_Response != null
                        && get_Response.is_default_image
                        .equalsIgnoreCase("no")
                        && !get_Response.image.trim().equals("")
                        && !get_Response.image.trim().equalsIgnoreCase(
                        "NULL")) {
                    saveState.setDisplayISDEFAULTIMAGEString(getActivity(), "true");
                    saveState.setDefaultImage(mActivityTabs, false);
                    chkBoxDefaultImage.setChecked(false);
                    count++;
                }
                else if (get_Response != null
                        && get_Response.is_default_image
                        .equalsIgnoreCase("yes")
                        && !get_Response.image.trim().equals("")
                        && !get_Response.image.trim().equalsIgnoreCase(
                        "NULL")) {
                    saveState.setDefaultImage(mActivityTabs, true);
                    chkBoxDefaultImage.setChecked(true);
                }
                //				}
            }
            mIsDisplayProgress = false;
        } catch (Exception e) {
            if(mIsDisplayProgress)
                if(progress!=null && progress.isShowing())
                    progress.dismiss();
            e.getMessage();
        }
    }

    //Download Updated File From the Server & Save it to the local SDCard
    class DownloadUserImageAsyncTask extends AsyncTask<Void, Void, Void>    {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progress!=null && progress.isShowing())
                progress.dismiss();
            try {
                savedBitmap=GlobalConfig_Methods.getBitmapFromFile(filePath,fileName);
                if(savedBitmap!=null){
                    imViewUser.setImageBitmap(savedBitmap);
                    mSharedPreference.setImageChange(getActivity(), true);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            chkBoxDefaultImage.setClickable(true);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Void doInBackground(Void... arg0) {
            bitmapUserImage = GlobalConfig_Methods.loadBitmap(userImageURL);
            String extStorageDirectory= String.valueOf(Environment.getExternalStorageDirectory()+File.separator+"TNC/images/");
            try {
                GlobalConfig_Methods.savebitmap(bitmapUserImage,filePath,fileName.substring(0, fileName.indexOf(".jpg")));
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }
    }

    /**
     * check internet availability
     */
    public void checkInternetConnection() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            if(isImageChanged)
            {
                String isDefault = "1";// No
                if (chkBoxDefaultImage.isChecked())
                    isDefault = "2"; // yes
                else
                    isDefault = "1";
                String image = "";
                if ((imViewUser.getDrawable() != null)
                        && (((BitmapDrawable) ((ImageView) imViewUser)
                        .getDrawable()).getBitmap() != null)) {
                    image = GlobalConfig_Methods
                            .encodeTobase64(((BitmapDrawable)((ImageView)imViewUser).getDrawable()).getBitmap());
                }
                UpdateUserInfoBean userInfoUpdateBean = new UpdateUserInfoBean(
                        Uri.encode(etContactName.getText().toString().trim()), etEmailId.getText()
                        .toString().trim(),image,isDefault);// Uri.encode(image)
                updateUserInfoWithImage(userInfoUpdateBean);
            }
            else if(!isImageChanged)
            {
                String isDefault = "1";// No
                if (chkBoxDefaultImage.isChecked())
                    isDefault = "2"; // yes
                else
                    isDefault = "1";
                UserInfoBeanWithoutImage userInfoUpdateBean = new UserInfoBeanWithoutImage(
                        Uri.encode(etContactName.getText().toString().trim()), etEmailId.getText()
                        .toString().trim(),isDefault);// Uri.encode(image)
                updateUserInfoWitoutImage(userInfoUpdateBean);
            }

        } else {
            GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
        }
    }

    private void updateUserInfoWitoutImage(UserInfoBeanWithoutImage userInfoUpdateBean)
    {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(userInfoUpdateBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.UPDATEUSERINFO, stringEntity,
                    updateUserInfoResponseHandler,
                    mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //send query to server with tyhe data to update User info
    private void updateUserInfoWithImage(UpdateUserInfoBean userInfoUpdateBean)
    {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(userInfoUpdateBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.UPDATEUSERINFO, stringEntity,
                    updateUserInfoResponseHandler,
                    mActivityTabs.getString(R.string.private_key),saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //Async task to handle update user Info
    AsyncHttpResponseHandler updateUserInfoResponseHandler = new JsonHttpResponseHandler() {
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

    /**
     * handle response for the request being made for updating user info
     *
     * @param response
     */
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
                //setting the IS_IMAGE_CHANGE flag in shared preference on image change
                if(isImageChanged){
                    mSharedPreference.setImageChange(getActivity(),isImageChanged);
                    imagebroadcastview.setImageDrawable(getActivity().getDrawable(R.drawable.image_broadcast_icon));
                }

                //Fetch User Details From The Server
                mIsDisplayProgress = true;
                getBBID();

                if(!etEmailId.getText().toString().trim().equals("")){
                    isEmailAvailable  =true;
                    saveState.setUserMailID(getActivity(),Uri.decode(etEmailId.getText().toString()));
                }
                //				mIsDisplayProgress =false;
                //				getBBID();
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
                    dialog.show(getChildFragmentManager(), "test");
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
                        "test");
            }
        }else {
            Log.d("improper_response",response);
            ShowDialog.alert(
                    mActivityTabs,"",getResources().getString(R.string.improper_response_network));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imViewUser)
        {
            SelectImagePopup dialogSelectimage = new SelectImagePopup();
           /* dialogSelectimage.setCancelable(false);*/
            dialogSelectimage.newInstance("Choose Image",
                    ((HomeScreenActivity) mActivityTabs), iNotifyImagePopSelection,this);
//            dialogSelectimage.show(((HomeScreenActivity) mActivityTabs)
//                    .getSupportFragmentManager(), "test");
        }

        if(v.getId()==R.id.imagebroadcastview){

           /* ImageUploadConfirmation mImageNotifyConfirmation = new ImageUploadConfirmation();
            mImageNotifyConfirmation.newInstance("", getActivity(), getResources().getString(R.string.txtImageBroadcast),"",
                    iNotifyImageConfirmation);
            mImageNotifyConfirmation.setCancelable(false);
            mImageNotifyConfirmation.show(((HomeScreenActivity) mActivityTabs)
                    .getSupportFragmentManager(), TAG);*/

            NotifyImageDialog imageDialog =new NotifyImageDialog();
            imageDialog.setCancelable(true);
            imageDialog.newInstance(getActivity().getResources().getString(R.string.txtImageBroadcast),getActivity(),iNotifyImageNotifyDialog);
            imageDialog.show(((HomeScreenActivity) mActivityTabs).getSupportFragmentManager(), "test");
        }

        if(v.getId()==R.id.btnBack)
        {
            ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
        }
        else if(v.getId()==R.id.btnChangeNumber)
        {
            SelectCountryDialog dialogSelectCountry = new SelectCountryDialog();
            dialogSelectCountry.setCancelable(true);
            dialogSelectCountry.newInstance("Choose country",
                    ((HomeScreenActivity) mActivityTabs), iObject);
            dialogSelectCountry.show(((HomeScreenActivity) mActivityTabs)
                    .getSupportFragmentManager(), "test");

        }
        else if(v.getId()==R.id.btnNotifyContacts)
        {
            //check if the user is a premium user
//            if(saveState.getISPREMIUMUSER(getActivity())){
                if(DBQuery.getAllBBContacts(mActivityTabs).size()>0){
                    //navigate to Tnc users contact screen to select the contacts you want to notify
                    ((HomeScreenActivity)mActivityTabs).setFragment(new TncUsers_NotifyFragment());
                }else{
                    ImageRequestDialog dialog = new ImageRequestDialog();
                    dialog.newInstance("",mActivityTabs,"No "+getResources().getString(R.string.app_name)+" user in your contact list to notify","",null,null);
                    dialog.setCancelable(false);
                    dialog.show(getChildFragmentManager(), "test");
                }
            /*}else{
                gotoBackScreen();
            }*/
        }
        else if(v.getId()==R.id.btnUpdate){
            if(!etEmailId.getText().toString().trim().equals("") && !RegexValidationUtility.isValidEmail(etEmailId.getText()
                    .toString().trim())){
                ImageRequestDialog dialogAlertNotify = null;
                dialogAlertNotify = new ImageRequestDialog();
                dialogAlertNotify.setCancelable(false);
                dialogAlertNotify.newInstance("", mActivityTabs,
                        "Please enter valid E-mail Id", "", null);
                dialogAlertNotify.show(getChildFragmentManager(), "test");
            }
            else{
                //Update User Info
                checkInternetConnection();
            }
        }else if(v.getId()==R.id.btnKey){
            DeleteChatConfirmationDialog dialog = new DeleteChatConfirmationDialog();
            dialog.setCancelable(false);
            dialog.newInstance("",mActivityTabs,"Do you like to send your Backup Key to your email ID?","",iNotifyResendBackupKey);
            dialog.show(getChildFragmentManager(), "test");
        }else if(v.getId()==R.id.btnHome){
            //Check Availability of the network
            if(NetworkConnection.isNetworkAvailable(mActivityTabs)){
                //Fetch User Details From The Server
                mIsDisplayProgress = true;
                getBBID();
            }else{
                GlobalConfig_Methods.displayNoNetworkAlert(mActivityTabs);
            }
        }else if(v.getId() == R.id.btnLocationDisplay){
            // Check if gps is enabled or not and display pop-up if gps is not enabled
            if(GlobalConfig_Methods.isGPSEnabled(getActivity()) && GlobalConfig_Methods.isServiceRunning(getActivity(), "com.tnc.service.SendLocationService")){
                getActivity().startActivity(new Intent(getActivity(), UserLocationDisplayActivity.class));
            }else{
                ImageRequestDialog mDialog = new ImageRequestDialog();
                mDialog.setCancelable(false);
                mDialog.newInstance("", getActivity(), getResources().getString(R.string.txtGpsDisabledAlertMessage), "", null);
                mDialog.show(getChildFragmentManager(), "test");
            }
        }
    }

    /**
     * Method to go to back screen
     */
    private void gotoBackScreen(){
        ImageRequestDialog mDialog = new ImageRequestDialog();
        mDialog.setCancelable(false);
        mDialog.newInstance("",getActivity(),getResources().getString(R.string.txtPremiumFeaturePopupMessage),"",mActionGoToBackScreen);
        mDialog.show(getChildFragmentManager(),"Test");
		/*PremiumFeaturesFragment mObjectPremiumFeaturesFragment = new PremiumFeaturesFragment();
		mObjectPremiumFeaturesFragment.newInstance("","",null);
		// In case of user is not the premium user
		if(mActivityTabs instanceof MainBaseActivity){
			((MainBaseActivity)mActivityTabs).setFragment(mObjectPremiumFeaturesFragment);

		}
		else if(mActivityTabs instanceof HomeScreenActivity){
			((HomeScreenActivity)mActivityTabs).setFragment(mObjectPremiumFeaturesFragment);
		}*/
    }

    AlertCallAction mActionGoToBackScreen = new AlertCallAction() {
        @Override
        public void isAlert(boolean isOkClicked)
        {

            // go to google play store to download Premium Version of the app
            GlobalConfig_Methods.gotoPremiumVersionPlayStore(getActivity());

            if(mActivityTabs instanceof MainBaseActivity){
                ((MainBaseActivity)mActivityTabs).fragmentManager.popBackStack();
            }else if(mActivityTabs instanceof HomeScreenActivity){
                ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
            }
        }
    };

    INotifyGalleryDialog iNotifyResendBackupKey = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // in case user selected yes to resend the backup key on verified email Id
            checkInternetConnectionResendBackupKey();
        }

        @Override
        public void no() {

        }
    };

    INotifyAction iObject=new INotifyAction() {

        @Override
        public void setAction(String action) {
            String number = "+18582017500"; //"+18582606130";
            String country_name = "USA";
            String number_type  = "dedicated";

            if (action.equalsIgnoreCase("USA/Canada")) {
                number = "+18582606130";//"+18582606130";
                number_type  = "dedicated";
                country_name = "US";
            }else if (action.equalsIgnoreCase("USA/Canada") || action.equalsIgnoreCase("Other Countries")) {
                number = "+18582606130";//"+18582606130";
                number_type  = "dedicated";
                country_name = "OTHER";
            }
            else if(action.equalsIgnoreCase("India") ){
                number = "+919810805053";//"+919711532380";
                number_type  = "shared";
                country_name = "INDIA";
            }
//			String message = getResources().getString(R.string.app_name)+"\nwould like to use your text messaging service.You may incur text messaging charges from your carrier.";
//			String messageSub = "Would you like to Continue?";
//			SendSMSConfirmationUserInfoDialog sendSMSDialog = new SendSMSConfirmationUserInfoDialog();
//			sendSMSDialog.newInstance("", mActivityTabs, message, messageSub,number,
//					number_type, country_name);
//			sendSMSDialog.setCancelable(false);
//			sendSMSDialog.show(getChildFragmentManager(), "test");
            boolean isAirplaneModeON = false;

            try{
                isAirplaneModeON = GlobalConfig_Methods.isAirplaneModeOn(mActivityTabs);
            }catch(Exception e){
                e.getMessage();
            }

            if(isAirplaneModeON){
                GlobalConfig_Methods.showSimErrorDialog(mActivityTabs,
                        "The device is in Airplane mode or has no sim card or outside of cellular service range");
            }else{
                String mSimState = GlobalConfig_Methods.checkSimState(mActivityTabs);
                if(mSimState.equalsIgnoreCase(GlobalCommonValues.SUCCESS)){
                    SendSMSFullScreenUserInfoDialog dialog=null;
                    if(mActivityTabs instanceof MainBaseActivity)
                    {
                        dialog = new SendSMSFullScreenUserInfoDialog();//new SendSMSFullScreenDialogFragment();
                        dialog.newInstance((MainBaseActivity)mActivityTabs, "",number,null,
                                number_type,country_name);
                        ((MainBaseActivity)mActivityTabs).setFragment(dialog);
                    }
                    else if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        dialog = new SendSMSFullScreenUserInfoDialog();
                        dialog.newInstance((HomeScreenActivity)mActivityTabs, "",number,null,
                                number_type,country_name);
                        ((HomeScreenActivity)mActivityTabs).setFragment(dialog);
                    }
                }else{
                    GlobalConfig_Methods.showSimErrorDialog(mActivityTabs, mSimState);
                }
            }
        }
    };

    /**
     * function is used to set user profile image on given url
     * @param url user image web url
     */
    public void setProfilePic(String url){

        if(url.length()>0) {
            if (GlobalConfig_Methods.isValidString(url)) {
                isImageChanged = true;
                Picasso.with(getActivity())
                        .load(url)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(imViewUser);
            }
        }else{
            isGoogleImageDialog = true;
        }
    }


    public CallbackManager getCallback(){
        return callbackManager;
    }


//    public void loginFacebook(){
//        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        //system.out.println("login Result"+loginResult);
//                        if(com.facebook.AccessToken.getCurrentAccessToken()!=null && !com.facebook.AccessToken.getCurrentAccessToken().isExpired()){
//                            GraphRequest request = GraphRequest.newMeRequest(
//                                    loginResult.getAccessToken(),
//                                    new GraphRequest.GraphJSONObjectCallback() {
//
//                                        @Override
//                                        public void onCompleted(JSONObject object, GraphResponse response) {
//                                            Log.v("Main", response.toString());
//                                            Log.e("Main json",object.toString());
//                                            JSONObject profile_pic_data = null;
//                                            JSONObject profile_pic_url = null;
//                                            try {
//                                                profile_pic_data = new JSONObject(object.get("picture").toString());
//                                                profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
//                                                String mImageUrl = profile_pic_url.getString("url");
//                                                Boolean is_silhouette = profile_pic_url.getBoolean("is_silhouette");
//                                                if(!is_silhouette) {
//                                                    setProfilePic(mImageUrl);
//                                                }else {
//                                                    isFaceBookImageDialog = true;
//                                                }
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                            Bundle parameters = new Bundle();
//                            parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
//                            request.setParameters(parameters);
//                            request.executeAsync();
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        new AlertDialog.Builder(getActivity())
//                                .setTitle(R.string.cancelled)
//                                .setMessage(exception.getMessage())
//                                .setPositiveButton(R.string.txtOk, null)
//                                .show();
//                    }
//
//                    private void showAlert() {
//                        new AlertDialog.Builder(getActivity())
//                                .setTitle(R.string.cancelled)
//                                .setMessage(R.string.permission_not_granted)
//                                .setPositiveButton(R.string.txtOk, null)
//                                .show();
//                    }
//                });
//    }

    public void loginFaceBook(FaceBookApiResponse faceBookApiResponse){
        if(!faceBookApiResponse.getPicture().getData().isIs_silhouette()) {
            setProfilePic(faceBookApiResponse.getPicture().getData().getUrl());
            isFaceBookImageDialog = false;
        }else {
            isFaceBookImageDialog = true;
            displayAlertDialog(getResources().getString(R.string.txtNoFacebookImage));
        }
    }

    void displayAlertDialog(String messageToDisplay){
        if(isGoogleImageDialog)
            isGoogleImageDialog = false;
        if(isFaceBookImageDialog)
            isFaceBookImageDialog = false;
        TileUpdateSuccessDialog dialog = new TileUpdateSuccessDialog();
        dialog.setCancelable(false);
        dialog.newInstance("", mActivityTabs, messageToDisplay);
        dialog.show(getActivity().getSupportFragmentManager(), "test");
    }

    //    /**
//     * Method to check internet connection availability
//     */
    public void checkInternetConnectionImageBroadCast(String to_user_ids)
    {
        int is_image_requested = 0;
        int is_emergency = 0;
        int is_image_broadcast = 1;

        if (NetworkConnection.isNetworkAvailable(getActivity())){
            ImageRequestBean imageRequestBean = new ImageRequestBean(to_user_ids,
                    is_image_requested, is_emergency, is_image_broadcast);
            imageRequest(imageRequestBean);
        }else{
            GlobalConfig_Methods.displayNoNetworkAlert(getActivity());
        }
    }


    /**
     * Method to send image request
     */
    private void imageRequest(ImageRequestBean imageRequestBean)
    {
        if ((!progress.isShowing()))
            progress.show();

        try
        {
            gson=new Gson();
            String stingGson = gson.toJson(imageRequestBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity=new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(getActivity(),
                    GlobalCommonValues.IMAGE_REQUEST,
                    stringEntity, sendImageRequestHandle,
                    getActivity().getString(R.string.private_key),saveState.getPublicKey(getActivity()));
        }
        catch (Exception e)
        {
            e.getMessage();
            if ((progress.isShowing()))
                progress.dismiss();
        }
    }

    //asyc task for the image request made to the TnC user
    AsyncHttpResponseHandler sendImageRequestHandle = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            if ((progress.isShowing()))
                progress.dismiss();

            try {
                if(response!=null)
                {
                    Logs.writeLog("ChooseImageFragment","OnSuccess",response.toString());
                    //Toast.makeText(getActivity(), "Success"+response.getString("response_message"), Toast.LENGTH_SHORT).show();
                    displayAlertDialog(getResources().getString(R.string.txtimagebroadcastsuccessfully));
                    //getResponseImageRequest(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("ChooseImageFragment","OnFailure",response);

            if ((progress.isShowing()))
                progress.dismiss();
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    @Override
    public void onDestroy() {

        if(MainBaseActivity._bitmap!=null){
            if(!MainBaseActivity._bitmap.isRecycled())
                MainBaseActivity._bitmap.recycle();
            MainBaseActivity._bitmap = null;
        }

        super.onDestroy();
    }

    private void displayErrorAlert(String message){
        ImageRequestDialog mAlert = new ImageRequestDialog();
        mAlert.setCancelable(false);
        mAlert.newInstance("", getActivity(), message, "", null);
        mAlert.show(getChildFragmentManager(), TAG);
    }
}
