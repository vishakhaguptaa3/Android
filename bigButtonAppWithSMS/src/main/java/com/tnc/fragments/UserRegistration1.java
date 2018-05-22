package com.tnc.fragments;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.bean.ParentConsentStatusBean;
import com.tnc.bean.UserRegistrationBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.SMSSendConfirmationDialog;
import com.tnc.dialog.SelectCountryDialog;
import com.tnc.dialog.SelectImagePopup;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.utility.RegexValidationUtility;
import com.tnc.webresponse.ParentConsentStatusResponse;
import com.tnc.webresponse.RegistrationResponse;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;


/**
 * class to display form at the time of new registration 
 *  @author a3logics
 */
public class UserRegistration1 extends BaseFragment implements OnClickListener {
    private TextView tvTitle, tvRegistration,tvGroupCode;
    private FrameLayout flBackArrow;
    private Button btnBack, btnRegister,btnCancel;
    private EditText etContactName, etEmailId,etGroupCode;
    private String title = "";
    private TransparentProgressDialog progress;
    private Gson gson;
    private String message = "";
    private String picturePath = "";
    private TextView tvAddPhoto;
    private Uri fileUri;
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private ImageView imViewUser;
    public static boolean isOnUserRegistration = false;
    private CheckBox chkBoxDefaultImage,chkBoxReturningUser,chkBoxUnderAge;
    private Bitmap bitmapUserImage = null;
    private Button btnCountryregion, btnParentalConsentStatus;
    //Spinner spCountryRegion;
    //ArrayList<String> listAdapter;

    public String number = "+18582017500"; //"+18582606130";
    public String country_name = "USA";
    public String number_type  = "dedicated";

    private final int PICK_IMAGE_REQUEST = 210;

    public UserRegistration1 newInstance(String title) {
        this.title = title;
        UserRegistration1 frag = new UserRegistration1();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userregistration, container,
                false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvRegistration = (TextView) view.findViewById(R.id.tvRegistration);
        tvGroupCode=(TextView) view.findViewById(R.id.tvGroupCode);
        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        tvAddPhoto = (TextView) view.findViewById(R.id.tvAddPhoto);
        etContactName = (EditText) view.findViewById(R.id.etContactName);
        etEmailId = (EditText) view.findViewById(R.id.etEmailId);
        etGroupCode=(EditText) view.findViewById(R.id.etGroupCode);
        imViewUser = (ImageView) view.findViewById(R.id.imViewUser);
        chkBoxDefaultImage = (CheckBox) view
                .findViewById(R.id.chkBoxDefaultImage);
        chkBoxReturningUser= (CheckBox) view
                .findViewById(R.id.chkBoxReturningUser);
        chkBoxReturningUser.setVisibility(View.VISIBLE);
        chkBoxUnderAge= (CheckBox) view.findViewById(R.id.chkBoxUnderAge);
        setCheckBoxClickability();
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCountryregion = (Button) view.findViewById(R.id.btnCountryRegion);
        btnParentalConsentStatus = (Button) view.findViewById(R.id.btnParentalConsentStatus);
        iCountrySelect.setAction("USA/Canada");
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //spCountryRegion = (Spinner) view.findViewById(R.id.spCountryRegion);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(), tvRegistration,
                "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(), etContactName,
                "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(), etEmailId,
                "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfEditText(getActivity(), etGroupCode,
                "fonts/Roboto-Light_1.ttf");
        CustomFonts.setFontOfButton(getActivity(), btnRegister,
                "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfButton(getActivity(), btnCancel,
                "fonts/Roboto-Regular_1.ttf");
        progress = new TransparentProgressDialog(mActivity,
                R.drawable.customspinner);

        saveState = new SharedPreference();


        if(!saveState.getUSER_NAME_TEMP(getActivity()).equalsIgnoreCase("")){
            etContactName.setText(saveState.getUSER_NAME_TEMP(getActivity()));
        }

        /*if(!saveState.getIS_UNDERAGE(getActivity()) && !chkBoxUnderAge.isChecked()){
            btnRegister.setEnabled(true);
            btnRegister.setBackgroundResource(R.drawable.button_bg_skip);
        }else  if(saveState.getIS_UNDERAGE(getActivity())){
            btnRegister.setEnabled(false);
            btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
            chkBoxUnderAge.setChecked(true);
            checkparentalStatus();
        }*/

        btnBack.setOnClickListener(this);
        flBackArrow.setVisibility(View.VISIBLE);
        UserRegistration1.isOnUserRegistration = true;
        tvAddPhoto.setText(Html.fromHtml("Add<br>Photo").toString());
        imViewUser.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCountryregion.setOnClickListener(this);
        btnParentalConsentStatus.setOnClickListener(this);

		/*if(saveState.getIS_UNDERAGE(mActivity) &&
				saveState.getIS_PARENTAL_CONSENT_RECEIVED(mActivity) &&
				!saveState.getParentEmailId(mActivity).trim().equals("")){
			etEmailId.setText(saveState.getParentEmailId(mActivity));
			etEmailId.setEnabled(false);
		}*/

        chkBoxUnderAge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton)view).isChecked()){
                    if((saveState.getIS_UNDERAGE(getActivity())) && !(saveState.getParentEmailId(getActivity()).
                            trim().equalsIgnoreCase(""))){

                        // call web service to check parental consent status
                        checkparentalStatus();

                    }else{
                        String mUserName = etContactName.getText().toString();
                        if(mUserName!=null && !mUserName.trim().equalsIgnoreCase("")){
                            saveState.setUSER_NAME_TEMP(getActivity(), mUserName);
                        }

                        if(mActivity instanceof MainBaseActivity){
                            ((MainBaseActivity)mActivity).setFragment(new ParentalConsentFragmentScreen());
                        }
                        else if(mActivity instanceof HomeScreenActivity){
                            ((HomeScreenActivity)mActivity).setFragment(new ParentalConsentFragmentScreen());
                        }
                    }
                }else{
                    btnRegister.setEnabled(true);
                    btnRegister.setBackgroundResource(R.drawable.button_bg_yes_green);
                    btnParentalConsentStatus.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

//        chkBoxUnderAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if(isChecked){
//                    if(saveState.getIS_UNDERAGE(getActivity()) && saveState.getIS_PARENTAL_CONSENT_RECEIVED(getActivity())){
//
//                        // call web service to check parental consent status
//                        checkparentalStatus();
//
//                        /*if(saveState.getParentConsentStatus(getActivity()).equalsIgnoreCase("agree")){
//
//                            btnRegister.setEnabled(true);
//                            btnRegister.setBackgroundResource(R.drawable.button_bg_skip);
//                            btnParentalConsentStatus.setBackgroundResource(R.drawable.check);
//                        }else if(saveState.getParentConsentStatus(getActivity()).equalsIgnoreCase("pending")){
//
//                            btnRegister.setEnabled(false);
//                            btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
//                            btnParentalConsentStatus.setBackgroundResource(R.drawable.xclaimation);
//                        }else if(saveState.getParentConsentStatus(getActivity()).equalsIgnoreCase("disagree")){{
//
//                            btnRegister.setEnabled(false);
//                            btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
//                            btnParentalConsentStatus.setBackgroundResource(R.drawable.cross_red);
//                        }}*/
//
//                    }else{
//                        if(mActivity instanceof MainBaseActivity){
//                            ((MainBaseActivity)mActivity).setFragment(new ParentalConsentFragmentScreen());
//                        }
//                        else if(mActivity instanceof HomeScreenActivity){
//                            ((HomeScreenActivity)mActivity).setFragment(new ParentalConsentFragmentScreen());
//                        }
//                    }
//                }else{
//                    btnRegister.setEnabled(true);
//                    btnRegister.setBackgroundResource(R.drawable.button_bg_skip);
//                }
//            }
//        });

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
    }

    //Method to check parental consent status
    private void checkparentalStatus(){
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
                if (progress!=null && progress.isShowing())
                    progress.dismiss();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response,Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("parentConsentStatus", "OnFailure", response);
            if (progress!=null && progress.isShowing())
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

                    if(get_Response.getData().get(get_Response.getData().size()-1).is_consent.equalsIgnoreCase("agree")){

                        btnRegister.setEnabled(true);
                        btnRegister.setBackgroundResource(R.drawable.button_bg_yes_green);
                        btnParentalConsentStatus.setBackgroundResource(R.drawable.check);
                    }else if(get_Response.getData().get(get_Response.getData().size()-1).is_consent.equalsIgnoreCase("pending")){

                        btnRegister.setEnabled(false);
                        btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
                        btnParentalConsentStatus.setBackgroundResource(R.drawable.xclaimation);
                    }else if(get_Response.getData().get(get_Response.getData().size()-1).is_consent.equalsIgnoreCase("disagree")){{

                        btnRegister.setEnabled(false);
                        btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
                        btnParentalConsentStatus.setBackgroundResource(R.drawable.cross_red);
                    }}

                }else if (get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE) ||
                        get_Response.response_code.equals(GlobalCommonValues.FAILURE_CODE_1)){

                    if (progress!=null && progress.isShowing())
                        progress.dismiss();
                }
            } else{
                Log.d("improper_response",response);
                ShowDialog.alert(
                        mActivity,
                        "",
                        getResources().getString(
                                R.string.improper_response_network));
            }
        }catch(Exception e){
            e.getMessage();
        }

    }

    /**
     * Method to set checkbox clickability
     */
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
    }

    /**
     * Method to send sms via Api calls
     */

    private void sendSMS() {
//		String message = getResources().getString(R.string.app_name) +
//				"\nwould like to use your text messaging service.You may incur text messaging charges from your carrier.";
//		String messageSub = "Would you like to Continue?";
//		SMSSendConfirmationDialog imageGalleryDialog = new SMSSendConfirmationDialog();
//		imageGalleryDialog.newInstance("", mActivity, message, messageSub,
//				number,number_type,country_name);
//		imageGalleryDialog.setCancelable(false);
//		imageGalleryDialog.show(getChildFragmentManager(), "test");
        boolean isAirplaneModeON = false;

        try{
            isAirplaneModeON = GlobalConfig_Methods.isAirplaneModeOn(mActivity);
        }catch(Exception e){
            e.getMessage();
        }

        if(isAirplaneModeON){
            GlobalConfig_Methods.showSimErrorDialog(mActivity,
                    getResources().getString(R.string.txtAirplaneModeAlert));
        }else{

            if(!chkBoxUnderAge.isChecked()){
                saveState.setIS_UNDERAGE(mActivity, false);
                saveState.setParentEmailId(mActivity, "");
                saveState.setParentConsentStatus(mActivity, "");
                saveState.setIS_PARENTAL_OK_CLICKED(mActivity, false);
            }

            String mSimState = GlobalConfig_Methods.checkSimState(mActivity);
            if(mSimState.equalsIgnoreCase("success") || !mSimState.equalsIgnoreCase("success")){
                SendSMSFullScreenDialogFragment dialog=null;
                if(mActivity instanceof MainBaseActivity)
                {
                    dialog = new SendSMSFullScreenDialogFragment();
                    dialog.newInstance((MainBaseActivity)mActivity, message,number,
                            number_type,country_name, true);
                    ((MainBaseActivity)mActivity).setFragment(dialog);
                }
                else if(mActivity instanceof HomeScreenActivity)
                {
                    dialog = new SendSMSFullScreenDialogFragment();
                    dialog.newInstance((HomeScreenActivity)mActivity, message,number,
                            number_type,country_name, true);
                    ((HomeScreenActivity)mActivity).setFragment(dialog);
                }
            }else{
                GlobalConfig_Methods.showSimErrorDialog(mActivity, mSimState);
            }
        }
    }

    @Override
    public void onPause() {
        GlobalConfig_Methods.hideKeyBoard(mActivity, etContactName);
        GlobalConfig_Methods.hideKeyBoard(mActivity, etEmailId);
        super.onPause();
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
    public void onResume() {
        super.onResume();
        try {

            if(!saveState.getIS_UNDERAGE(getActivity())){
                if(chkBoxUnderAge.isChecked()){
                    btnRegister.setEnabled(false);
                    btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
                }else{
                    btnRegister.setEnabled(true);
                    btnRegister.setBackgroundResource(R.drawable.button_bg_yes_green);
                }

            }else  if(saveState.getIS_UNDERAGE(getActivity())){
                btnRegister.setEnabled(false);
                btnRegister.setBackgroundResource(R.drawable.bg_grey_button_disabled);
                chkBoxUnderAge.setChecked(true);
                if(!saveState.getParentEmailId(getActivity()).equalsIgnoreCase(""))
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
                imViewUser.setImageBitmap(MainBaseActivity._bitmap);
            }
            setCheckBoxClickability();
            if(HomeScreenActivity.btnDisable!=null)
                HomeScreenActivity.btnDisable.setEnabled(false);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                try {
                    Uri selectedImage = fileUri;
                    picturePath = selectedImage.getPath();
                    MainBaseActivity.selectedImagepath = picturePath;
                    cropImage(true);
                } catch (Exception e) {
                    e.getMessage();
                }
            }else if(requestCode==PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
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
            }
        }
        setCheckBoxClickability();
    }

    INotifyGalleryDialog iObject = new INotifyGalleryDialog() {
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
        }
    };

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
    public void onDestroy() {
        super.onDestroy();
        MainBaseActivity._bitmap = null;
        // imViewUser.setImageBitmap(null);
        UserRegistration1.isOnUserRegistration = false;
        if (GlobalCommonValues.TelephoneNumberTobeDisplayed != null
                && !GlobalCommonValues.TelephoneNumberTobeDisplayed.trim()
                .equals(""))
            GlobalCommonValues.TelephoneNumberTobeDisplayed = "";
    }

    /**
     * check internet availability
     */
    public void checkInternetConnection() {
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
            String isDefault = "1";// No
            if (chkBoxDefaultImage.isChecked())
                isDefault = "2"; // yes
            else
                isDefault = "1";
            String image = "";
//			if ((imViewUser.getDrawable() != null)
//					&& (((BitmapDrawable) ((ImageView) imViewUser)
//					.getDrawable()).getBitmap() != null)) {
//				image = GlobalConfig_Methods
//						.encodeTobase64(MainBaseActivity._bitmap);
//			}

			/*UserRegistrationBean userRegistrationBean = new UserRegistrationBean(
					Uri.encode(etContactName.getText().toString().trim()), etEmailId.getText()
					.toString().trim(), saveState.getDeviceId(getActivity()),
					saveState.getGCMRegistrationId(getActivity()), "2",
					isDefault, image,etGroupCode.getText().toString().toUpperCase());*/// Uri.encode(image)

            String email = "";

            if( chkBoxUnderAge.isChecked() &&(saveState.getIS_UNDERAGE(getActivity()))  &&
                    (saveState.getParentEmailId(getActivity())!=null && !saveState.getParentEmailId(getActivity()).equalsIgnoreCase(""))){

                email = saveState.getParentEmailId(getActivity());
            }

            UserRegistrationBean userRegistrationBean = new UserRegistrationBean(
                    Uri.encode(etContactName.getText().toString().trim()), email
                    , saveState.getDeviceId(getActivity()),
                    saveState.getGCMRegistrationId(getActivity()), "2",
                    isDefault, image,"");
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
     * handle response for the request being made for the registration
     *
     * @param response
     */
    private void getResponseRegistration(String response) {
        try {
            String response2="";
            if(response.contains("</div>") || response.contains("<h4>") || response.contains("php"))
            {
                response2=response.substring(response.indexOf("response_code")-2,response.length());
            }
            else{
                response2=response;
            }

            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
                dialogErrorMessage.setCancelable(false);
                RegistrationResponse get_Response = gson.fromJson(response2,RegistrationResponse.class);
                if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE))
                {
                    saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivity, true);
                    saveState.setPublicKey(getActivity(),get_Response.getData.getPublic_key());
                    saveState.setPassCode(getActivity(),get_Response.getData.getPasscode());
                    saveState.setRegistered(getActivity(), false);
                    saveState.set_IS_UPOLADCONTACTSREQUESTED(mActivity, true);
                    if(chkBoxReturningUser.isChecked()){
                        saveState.setISRETURNINGUSER(mActivity, true);
                    }else{
                        saveState.setISRETURNINGUSER(mActivity, false);
                    }

                    sendSMS();

                    if (get_Response.getData.getIs_default_image()
                            .equalsIgnoreCase("no")
                            && !get_Response.getData.getImage().trim()
                            .equals("")
                            && !get_Response.getData.getImage().trim()
                            .equalsIgnoreCase("NULL")) {
                        saveState.setDisplayISDEFAULTIMAGEString(getActivity(), "true");
                    }
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
                        dialogErrorMessage.newInstance("",
                                ((MainBaseActivity) mActivity),
                                get_Response.getMessage(), "", null);
                        dialogErrorMessage.show(((MainBaseActivity) mActivity)
                                .getSupportFragmentManager(), "test");
                    } else if (mActivity instanceof HomeScreenActivity) {
                        dialogErrorMessage.newInstance("",
                                ((HomeScreenActivity) mActivity),
                                get_Response.getMessage(), "", null);
                        dialogErrorMessage.show(
                                ((HomeScreenActivity) mActivity)
                                        .getSupportFragmentManager(), "test");
                    }
                }
            } else {
                Log.d("improper_response",response);
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

    /**
     * interface to handle call to country selection
     */


    INotifyAction iCountrySelect=new INotifyAction() {

        @Override
        public void setAction(String action) {
            btnCountryregion.setText(action);
            if (action.equalsIgnoreCase("USA/Canada")) {
                number = "+18582017500"; //"+18582606130";
                number_type  = "dedicated";
                country_name = "US";
            }else if(action.equalsIgnoreCase("Other Countries")){
                number = "+18582017500"; //"+18582606130";
                number_type  = "dedicated";
                country_name = "OTHER";
            }else if(action.equalsIgnoreCase("India")){
                number       = "+919810805053";
                number_type  = "shared";
                country_name = "INDIA";
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            if (mActivity instanceof MainBaseActivity)
                ((MainBaseActivity) mActivity).fragmentManager.popBackStack();
            if (mActivity instanceof HomeScreenActivity)
                ((HomeScreenActivity) mActivity).fragmentManager.popBackStack();
        }

        else if (v.getId() == R.id.imViewUser) {
            SelectImagePopup dialogSelectimage = new SelectImagePopup();
//            dialogSelectimage.setCancelable(false);
            if (mActivity instanceof MainBaseActivity) {
                dialogSelectimage.newInstance("Choose Image",
                        ((MainBaseActivity) mActivity), iObject,this);
//                dialogSelectimage.show(((MainBaseActivity) mActivity)
//                        .getSupportFragmentManager(), "test");
            } else if (mActivity instanceof HomeScreenActivity) {
                dialogSelectimage.newInstance("Choose Image",
                        ((HomeScreenActivity) mActivity), iObject,this);
//                dialogSelectimage.show(((HomeScreenActivity) mActivity)
//                        .getSupportFragmentManager(), "test");
            }
        }
        else if(v.getId() == R.id.btnCountryRegion){
            SelectCountryDialog dialogSelectCountry = new SelectCountryDialog();
            dialogSelectCountry.setCancelable(true);
            if(mActivity instanceof MainBaseActivity){
                dialogSelectCountry.newInstance("Choose country",
                        ((MainBaseActivity) mActivity),iCountrySelect );
                dialogSelectCountry.show(((MainBaseActivity) mActivity)
                        .getSupportFragmentManager(), "test");
            }else if(mActivity instanceof HomeScreenActivity){
                dialogSelectCountry.newInstance("Choose country",
                        ((HomeScreenActivity) mActivity),iCountrySelect );
                dialogSelectCountry.show(((HomeScreenActivity) mActivity)
                        .getSupportFragmentManager(), "test");
            }
        }else if(v.getId() == R.id.btnParentalConsentStatus){

            if(mActivity instanceof MainBaseActivity){
                ((MainBaseActivity)mActivity).setFragment(new ParentalConsentFragmentScreen());
            }
            else if(mActivity instanceof HomeScreenActivity){
                ((HomeScreenActivity)mActivity).setFragment(new ParentalConsentFragmentScreen());
            }

        }else if (v.getId() == R.id.btnRegister) {
            ImageRequestDialog dialogAlertNotify = null;
            if (etContactName.getText().toString().trim().equals("")) {
                dialogAlertNotify = new ImageRequestDialog();
                dialogAlertNotify.setCancelable(false);
                dialogAlertNotify.newInstance("", mActivity,
                        "Please enter following field:\nName", "", null);
                dialogAlertNotify.show(getChildFragmentManager(), "test");
            } else {
                //int groupLength=etGroupCode.getText().toString().length();
                if (!etEmailId.getText().toString().trim().equals("")
                        && !RegexValidationUtility.isValidEmail(etEmailId.getText()
                        .toString().trim())) {
                    dialogAlertNotify = new ImageRequestDialog();
                    dialogAlertNotify.setCancelable(false);
                    dialogAlertNotify.newInstance("", mActivity,
                            "Please enter valid E-mail Id", "", null);
                    dialogAlertNotify.show(getChildFragmentManager(), "test");

                }
				/*else if (!etGroupCode.getText().toString().trim().equals("")
						&& (groupLength<2 || groupLength>8)){
					dialogAlertNotify = new ImageRequestDialog();
					dialogAlertNotify.setCancelable(false);
					dialogAlertNotify.newInstance("", mActivity,
							"Please enter valid Group Code", "", null);
					dialogAlertNotify.show(getChildFragmentManager(), "test");
				}*/
                else{
                    checkInternetConnection();
                }
            }
        }else if (v.getId() == R.id.btnCancel){

            //clear temp username
            //GlobalConfig_Methods.clearTempUserName(getActivity());

            mActivity.startActivity(new Intent(getActivity(), HomeScreenActivity.class));
            mActivity.finish();
        }
    }
}
