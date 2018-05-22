package com.tnc.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
/*import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;*/
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.R;
import com.tnc.adapter.InfoMenuAdapter;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.PrivacyTermsOfDialog;
//import com.tnc.dialog.ShareSocialSiteDialog;
import com.tnc.dialog.ShareSocialSiteDialog;
import com.tnc.dialog.TweetFbPostSuccessfulDialog;
import com.tnc.fragments.HowToFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.linkedin.Config;
import com.tnc.linkedin.LinkedinDialog;
import com.tnc.linkedin.LinkedinDialog.OnVerifyListener;
import com.tnc.preferences.SharedPreference;
import com.tnc.twitter.WebViewActivity;
import com.tnc.utility.Logs;
import com.tnc.webresponse.HowtoReponse;
import com.tnc.webresponse.HowtoReponseDataBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import cz.msebera.android.httpclient.Header;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class InfoActivity extends FragmentActivity{

    private SharedPreference saveState;
    private TransparentProgressDialog progress;
    private Gson gson;
    private TextView tvTitle,tvInfo;
    private ListView lvInfoMenu;
    private FrameLayout flBackArrow;
    private Button btnBack;
    private Context mActivity;
    private INotifyGalleryDialog iNotifyRefreshSelectedTab;
    private InfoMenuAdapter adapterInfoMenu;
    private int adapterSelected_position = 0;
    private ArrayList<String> listInfoMenu=new ArrayList<String>();
    private String token="",tokenSecret="";

    //Facebook Details
    public String APP_ID = "149166565537841";//<-chatstasy       //tnc -> "573431386152424"; //"806021519408188"; //1615491192063580
    /*public Facebook facebook;
    public AsyncFacebookRunner mAsyncRunner;*/
    String FILENAME = "AndroidSSO_data";
    public final String[] PERMISSIONS = new String[] { "publish_actions" , "public_profile"};
    /*public final String[] PERMISSIONS = new String[] { "publish_actions" , "public_profile"};
    public String fbPostImageURL="";
    public String fbUsername="",strAnimalViewingName="";*/
    //	public int countFbPopup=0;
    public SharedPreferences mPrefs;

    //Twitter Details
	/* Shared preference keys */
    private final String PREF_NAME = "sample_twitter_pref";
    private final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private final String PREF_USER_NAME = "twitter_user_name";

    /* Any number for uniquely distinguish your request */
    public final int WEBVIEW_REQUEST_CODE = 100;
    private ProgressDialog pDialog;
    private Twitter twitter;
    private RequestToken requestToken;
    private SharedPreferences mSharedPreferences;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    // Linkedin Details
    public final String OAUTH_CALLBACK_HOST = "litestcalback";

    final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
            .getInstance().createLinkedInOAuthService(
                    Config.LINKEDIN_CONSUMER_KEY,Config.LINKEDIN_CONSUMER_SECRET);
    final LinkedInApiClientFactory factory = LinkedInApiClientFactory
            .newInstance(Config.LINKEDIN_CONSUMER_KEY,
                    Config.LINKEDIN_CONSUMER_SECRET);
    LinkedInRequestToken liToken;
    LinkedInApiClient client;
    LinkedInAccessToken accessToken = null;
    boolean isPostedFirstTime=true;
    public FragmentManager fragmentManager = null;
    public FragmentTransaction fragmentTransaction = null;
    public Fragment currentFragment;
    ArrayList<HowtoReponseDataBean> listHowToDatabase =
            new ArrayList<HowtoReponseDataBean>();

    //	ArrayList<HowtoReponse> listHowToResponse1 =
    //			new ArrayList<HowtoReponse>();

    ArrayList<HowtoReponseDataBean> listHowToResponseData =
            new ArrayList<HowtoReponseDataBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.infoscreen);
        mActivity=InfoActivity.this;
        idInitialization();
    }

    private void idInitialization() {
        saveState = new SharedPreference();
        progress = new TransparentProgressDialog(mActivity,R.drawable.customspinner);
        gson = new Gson();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvInfo=(TextView)findViewById(R.id.tvInfo);
        lvInfoMenu=(ListView) findViewById(R.id.lvInfoMenu);
        flBackArrow = (FrameLayout) findViewById(R.id.flBackArrow);
        btnBack = (Button) findViewById(R.id.btnBack);
        flBackArrow.setVisibility(View.VISIBLE);
        CustomFonts.setFontOfTextView(mActivity, tvTitle,
                "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(mActivity,tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(mActivity,tvInfo, "fonts/Roboto-Bold_1.ttf");
		/* Enabling strict mode */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize Facebook SDK
//        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(com.facebook.AccessToken oldToken, com.facebook.AccessToken newToken) {
//                Toast.makeText(InfoActivity.this, "onCurrentAccessTokenChanged", Toast.LENGTH_LONG).show();
                //postOnFbWall();
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                Toast.makeText(InfoActivity.this, "onCurrentProfileChanged", Toast.LENGTH_LONG).show();
//                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

//        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        if(listInfoMenu.isEmpty())
        {
            String arrMenu[] = getResources().getStringArray(R.array.txtInfoActivityOptionMenuArray);
            listInfoMenu.add(arrMenu[0]);
            listInfoMenu.add(arrMenu[1]);
            listInfoMenu.add(arrMenu[2]);
            listInfoMenu.add(arrMenu[3]);
            listInfoMenu.add(arrMenu[4]);
            listInfoMenu.add(arrMenu[5]);
            //			listInfoMenu.add("Terms of Use");
            listInfoMenu.add(arrMenu[6]);
        }

        adapterInfoMenu=new InfoMenuAdapter(mActivity,listInfoMenu);
        lvInfoMenu.setAdapter(adapterInfoMenu);

        lvInfoMenu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                adapterSelected_position = position;
                adapterInfoMenu.setRowColor(adapterSelected_position, true);
                if(position==0){  // How to

                    //fetch how to from database
                    listHowToDatabase =
                            new ArrayList<HowtoReponseDataBean>();
                    //					listHowToDatabase = DBQuery.getAllHowTo(mActivity);

                    if(NetworkConnection.isNetworkAvailable(mActivity)){
                        checkInternetConnectionHowTo();
                    }else{
                        GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
                    }


                    /**
                     * Devanshu Nath Tripathi
                     * As per client UAT-442 i comment this code
                     * now every user can get Update Dynamic Content
                     */
					/*	if(saveState.getIS_HOWTO_VERSION_UPDATED(mActivity)){
						checkInternetConnectionHowTo();
					}else{
						displayHowToFromDatabase();
					}*/

                }else if(position==1){ //FAQ

                    //fetch how to from database
                    listHowToDatabase = new ArrayList<HowtoReponseDataBean>();
                    //					listHowToDatabase = DBQuery.getAllFAQ(mActivity);

                    if(NetworkConnection.isNetworkAvailable(mActivity)){
                        checkInternetConnectionHowTo();
                    }else{
                        GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
                    }
                    /**
                     * Devanshu Nath Tripathi
                     * As per client UAT-442 i comment this code
                     * now every user can get Update Dynamic Content
                     */
                    //					if(saveState.getIS_FAQ_VERSION_UPDATED(mActivity)){
                    //						checkInternetConnectionHowTo();
                    //					}else{
                    //						displayHowToFromDatabase();
                    //					}


                }
                else if(position==2){ //Rate the App
                    if(!NetworkConnection.isNetworkAvailable(mActivity)){
                        GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
                    }else{
                        if(!saveState.isRegistered(mActivity)){
                            // ALLOW TO RATE ONLY IF THE USER IS NOT UNDERAGE AND IS REGISTERED
                            try {
                                Intent viewIntent =
                                        new Intent("android.intent.action.VIEW",
                                                Uri.parse("market://details?id=com.tnc"));
                                startActivity(viewIntent);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else if(!saveState.getIS_UNDERAGE(mActivity) && saveState.isRegistered(mActivity)){
                            // ALLOW TO RATE ONLY IF THE USER IS NOT UNDERAGE AND IS REGISTERED
                            try {
                                Intent viewIntent =
                                        new Intent("android.intent.action.VIEW",
                                                Uri.parse("market://details?id=com.tnc"));
                                startActivity(viewIntent);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                else if(position==3){// International Emergency NUmber
                    startActivity(new Intent(mActivity,InternationalEmergencyNumbersActivity.class));
                }
                else if(position==4){ // Social Sharing
                    if(NetworkConnection.isNetworkAvailable(mActivity)){
                        if(!saveState.isRegistered(mActivity)){
							/*ImageRequestDialog dialog = new ImageRequestDialog();
							dialog.newInstance("",InfoActivity.this,"Unable to share since this is beta version of the app.","",null, iNotifyGalleryDialog);
							dialog.setCancelable(false);
							dialog.show(getSupportFragmentManager(), "test");*/
                           /* ShareSocialSiteDialog dialogSelectimage = new ShareSocialSiteDialog();
                            dialogSelectimage.setCancelable(false);
                            dialogSelectimage.newInstance(getResources().getString(R.string.txtShareSocialMediaMessage),
                                    mActivity,iObjectSocialSitePost,iNotifyGalleryDialog);
                            dialogSelectimage.show(getSupportFragmentManager(),"test");*/
                            ShareSocialSiteDialog dialogSelectimage = new ShareSocialSiteDialog();
//                            dialogSelectimage.setCancelable(false);
//                            dialogSelectimage.show();
                            dialogSelectimage.setCancelable(false);
                            dialogSelectimage.newInstance(getResources().getString(R.string.txtShareSocialMediaMessage)
                                    ,mActivity,iObjectSocialSitePost,iNotifyGalleryDialog);
                            dialogSelectimage.show(getSupportFragmentManager(),"test");

                        }else if(!saveState.getIS_UNDERAGE(mActivity) && saveState.isRegistered(mActivity)){
                            // ALLOW TO SHARE ONLY IF THE USER IS NOT UNDERAGE AND IS REGISTERED
							/*ImageRequestDialog dialog = new ImageRequestDialog();
							dialog.newInstance("",InfoActivity.this,"Unable to share since this is beta version of the app.","",null, iNotifyGalleryDialog);
							dialog.setCancelable(false);
							dialog.show(getSupportFragmentManager(), "test");*/
                            //Share on Social Media
                            ShareSocialSiteDialog dialogSelectimage = new ShareSocialSiteDialog();
                            dialogSelectimage.setCancelable(false);
//                            dialogSelectimage.show();
                            dialogSelectimage.newInstance(getResources().getString(R.string.txtShareSocialMediaMessage)
                                    ,mActivity,iObjectSocialSitePost,iNotifyGalleryDialog);
                            dialogSelectimage.show(getSupportFragmentManager(),"test");

                        }else if(saveState.getIS_UNDERAGE(mActivity) && saveState.isRegistered(mActivity)){
                            ImageRequestDialog dialog = new ImageRequestDialog();
                            dialog.newInstance("",InfoActivity.this,getResources().getString(R.string.txtUnderAgeShareProhibitionMessage),
                                    "",null, iNotifyGalleryDialog);
                            dialog.setCancelable(false);
                            dialog.show(getSupportFragmentManager(), "test");
                        }
                    }else{
                        GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
                    }
                }
                else if(position==5){// Privacy Policy / Terms Of Use

                    PrivacyTermsOfDialog dialog = new PrivacyTermsOfDialog();
                    dialog.newInstance(getResources().getString(R.string.txtChooseOption), mActivity, iObjectOptionSelected);
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "test");
                }

                else if(position==6)  // Contact Us
                {
                    if(!saveState.isRegistered(mActivity)){
                        if(NetworkConnection.isNetworkAvailable(mActivity)){
                            startActivity(new Intent(mActivity,ContacUsActivity.class));
                        }else{
                            GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
                        }
                    }else if(saveState.isRegistered(mActivity)){
                        startActivity(new Intent(mActivity,ContacUsActivity.class));
                    }
                }
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // In case user selected the option of privacy policy/terms of use
    INotifyAction iObjectOptionSelected = new INotifyAction() {

        @Override
        public void setAction(String action) {
            if(NetworkConnection.isNetworkAvailable(mActivity)){
                if(action.equalsIgnoreCase("privacy_policy")){
                    Intent myIntent = new Intent(mActivity,PrivacyPolicyActivity.class);
                    myIntent.putExtra("title",getResources().getString(R.string.txtPrivacyPolicyCamelCase));
                    startActivity(myIntent);
                }else if(action.equalsIgnoreCase("terms_of_use")){
                    Intent myIntent = new Intent(mActivity,PrivacyPolicyActivity.class);
                    myIntent.putExtra("title",getResources().getString(R.string.txtTermsOfUseCamelCase));
                    startActivity(myIntent);
                }
            }else{
                GlobalConfig_Methods.displayNoNetworkAlert(mActivity);
            }
        }
    };

    protected void onResume() {
        super.onResume();
        adapterInfoMenu.setRowColor(adapterSelected_position, false);
//        Profile profile = Profile.getCurrentProfile();
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    /**
     * Method to post on Facebook wall
     */
    private void postOnFbWall(){

        ShareDialog shareDialog= new ShareDialog(this);

        // Register CallBack (this part is optional)
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
                dialog.setCancelable(false);
                dialog.newInstance("",mActivity,getResources().getString(R.string.txtFbShareSuccessMessage),"",iNotifyGalleryDialog);
                try{
                    dialog.show(getSupportFragmentManager(), "test");
                }catch(Exception e){
                    e.getMessage();
                }
            }

            @Override
            public void onCancel() {
                Log.i("Log","onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                ImageRequestDialog dialog = new ImageRequestDialog();
                dialog.setCancelable(false);
                dialog.newInstance("",InfoActivity.this, error.getMessage(),"", null, iNotifyGalleryDialog);
                dialog.show(getSupportFragmentManager(), "Test");
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentDescription(
                            getResources().getString(R.string.txtFbMessagePostTnC))
                    .setContentUrl(Uri.parse("http://www.chatstasy.com"))
                    .build();

            shareDialog.show(linkContent);
        }

        /*ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentDescription(getResources().getString(R.string.txtFbMessagePostTnC))
                .setContentUrl(Uri.parse("http://www.chatstasy.com"))
                .build();*/

        /*TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
        dialog.newInstance("",mActivity,getResources().getString(R.string.txtFbShareSuccessMessage),"",iNotifyGalleryDialog);
        dialog.show(getSupportFragmentManager(), "test");*/

        /**
         * if(!fbUsername.equals(""))
         {
         Bundle params = new Bundle();
         params.putString("name",getResources().getString(R.string.txtFbMessagePostTnC));
         params.putString("link","http://www.chatstasy.com" );

         facebook.dialog(this, "feed", params,new DialogListener() {

        @Override
        public void onFacebookError(FacebookError e) {
        }

        @Override
        public void onError(DialogError e) {
        }

        @Override
        public void onComplete(Bundle values) {
        try {
        if(values.get("post_id")!=null && !values.get("post_id").toString().trim().equals("")){
        TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
        dialog.newInstance("",mActivity,getResources().getString(R.string.txtFbShareSuccessMessage),"",iNotifyGalleryDialog);
        dialog.show(getSupportFragmentManager(), "test");
        }
        } catch (Exception e) {
        e.getMessage();
        }
        }

        @Override
        public void onCancel() {
        }
        });
         }
         */
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(accessTokenTracker!=null)
            accessTokenTracker.stopTracking();
        if(profileTracker!=null)
            profileTracker.stopTracking();
    }

    //Session session;
    /**
     * In case user selected the option to share on social site i.e. fb/twitter/linkedin
     */
    INotifyAction iObjectSocialSitePost=new INotifyAction() {

        @Override
        public void setAction(String action) {
            //if user selected the action to post on facebook
            if(action.equalsIgnoreCase("facebook"))
            {

                // In case of user selected to post on facebook
                /*FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();*/

                // Login and post on Fb Wall
//                LoginManager.getInstance().logInWithReadPermissions(InfoActivity.this, Arrays.asList("user_photos", "email", "public_profile", "user_posts" , "AccessToken"));
                LoginManager.getInstance().logInWithReadPermissions(InfoActivity.this, Arrays.asList("public_profile"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                // Toast.makeText(InfoActivity.this, "Login onSuccess", Toast.LENGTH_LONG).show();
//                                LoginManager.getInstance().logInWithPublishPermissions(InfoActivity.this, Arrays.asList("publish_actions"));
                                if(com.facebook.AccessToken.getCurrentAccessToken()!=null && !com.facebook.AccessToken.getCurrentAccessToken().isExpired()){
                                    postOnFbWall();
                                }
                            }

                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                new AlertDialog.Builder(InfoActivity.this)
                                        .setTitle(R.string.cancelled)
                                        .setMessage(exception.getMessage())
                                        .setPositiveButton(R.string.txtOk, null)
                                        .show();
                            }

                            private void showAlert() {
                                new AlertDialog.Builder(InfoActivity.this)
                                        .setTitle(R.string.cancelled)
                                        .setMessage(R.string.permission_not_granted)
                                        .setPositiveButton(R.string.txtOk, null)
                                        .show();
                            }
                        });

                /*facebook=new Facebook(APP_ID);
                mAsyncRunner = new AsyncFacebookRunner(facebook);
                loginToFacebook();
                SessionStore.restore(facebook, mActivity);
                if (facebook.isSessionValid())
                {
                    getProfileInformation();
                    postOnWall();
                }
                else
                {
                    facebook.authorize(InfoActivity.this, PERMISSIONS, -1, new FbLoginDialogListener());
                    postOnWall();
                }*/
            }
            //if user selected the action to post on twitter
            else if(action.equalsIgnoreCase("twitter"))
            {
                // In case of user selected to post on twitter
                initTwitterConfigs();
				/* Check if required twitter keys are set */
                if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
                    TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
                    dialog.newInstance("",mActivity,getResources().getString(R.string.txtTwitterKeysNotConfigured),"",iNotifyGalleryDialog);
                    dialog.show(getSupportFragmentManager(), "test");
                    return;
                }
				/* Initialize application preferences */
                mSharedPreferences = getSharedPreferences(PREF_NAME, 0);
                boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
				/*  if already logged in, then hide login layout and show share layout */
                if (isLoggedIn) {
                    @SuppressWarnings("unused")
                    String username = mSharedPreferences.getString(PREF_USER_NAME, "");
                    new updateTwitterStatus().execute(getResources().getString(R.string.txtTwitterCheckOuttapnChat));

                } else {
                    loginToTwitter();
                }
            }
            //if user selected the action to post on linkedin
			/*else if(action.equalsIgnoreCase("linkedin"))
			{
				token=saveState.getAccessTokenLinkedin(mActivity);
				tokenSecret=saveState.getAccessTokenSecretLinkedin(mActivity);
				//check if linkedin is login or not, if it's logged in the post to wall
				if((token!=null && !token.trim().equals("")) && tokenSecret!=null && !tokenSecret.trim().equals(""))
				{
					postOnLinkedIn();
				}
				else{
					loginLinkedIn();
				}
			}*/
        }
    };

    // Interface to handle linkedin login
    OnVerifyListener onVerifyLinkedin=new OnVerifyListener() {
        @Override
        public void onVerify(String verifier) {
            try {
                accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken,
                        verifier);
                LinkedinDialog.factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                postOnLinkedIn();

            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    // FbLoginDialog handler
   /* public final class FbLoginDialogListener implements DialogListener, RequestListener {
        public void onComplete(Bundle values) {
            SessionStore.save(facebook, mActivity);
            getProfileInformation();
        }

        public void onFacebookError(FacebookError error) {
        }
        public void onError(DialogError error) {
        }

        public void onCancel(){
        }

        @Override
        public void onComplete(String response){
        }

        @Override
        public void onIOException(IOException e){
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e){
        }

        @Override
        public void onMalformedURLException(MalformedURLException e){
        }
    }*/

    // get facebook user information
    /*int what = 1;
    public void getProfileInformation()
    {
        if(fbUsername.equals(""))
        {
            mAsyncRunner.request("me", new RequestListener() {
                @Override
                public void onComplete(String response)
                {
                    Log.d("Profile", response);
                    String json = response;
                    try {
                        // Facebook Profile JSON data
                        JSONObject profile = new JSONObject(json);
                        // getting name of the user
                        fbUsername = profile.getString("name");
                        // getting email of the user
                        //					fbUserEmail = profile.getString("email");
                        what=0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFbHandler.sendMessage(mFbHandler.obtainMessage(what, fbUsername));
                }

                @Override
                public void onIOException(IOException e) {
                }

                @Override
                public void onFileNotFoundException(FileNotFoundException e) {
                }

                @Override
                public void onMalformedURLException(MalformedURLException e) {
                }

                @Override
                public void onFacebookError(FacebookError e) {
                }
            });
        }
    }*/

    //facebook post handler
    /*private Handler mFbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //			mProgressDialog.dismiss();
            if (msg.what == 0) {
                String username = (String) msg.obj;
                username = (username.equals("")) ? "No Name" : username;
            } else {
            }
            postOnWall();
        }
    };*/

    // method to post on the facebook
    /*public void postOnWall()
    {
        if(!fbUsername.equals(""))
        {
            Bundle params = new Bundle();
            params.putString("name",getResources().getString(R.string.txtFbMessagePostTnC));
            params.putString("link","http://www.chatstasy.com" );

            facebook.dialog(this, "feed", params,new DialogListener() {

                @Override
                public void onFacebookError(FacebookError e) {
                }

                @Override
                public void onError(DialogError e) {
                }

                @Override
                public void onComplete(Bundle values) {
                    try {
                        if(values.get("post_id")!=null && !values.get("post_id").toString().trim().equals("")){
                            TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
                            dialog.newInstance("",mActivity,getResources().getString(R.string.txtFbShareSuccessMessage),"",iNotifyGalleryDialog);
                            dialog.show(getSupportFragmentManager(), "test");
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }*/

    /**
     * interface to notify the list of deselecting the selected position
     */
    INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            adapterInfoMenu.setRowColor(adapterSelected_position, false);
        }

        @Override
        public void no() {
            adapterInfoMenu.setRowColor(adapterSelected_position, false);
        }
    };

    // method to login to the facebook
   /* public void loginToFacebook(){
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);

        if (access_token != null) {
            facebook.setAccessToken(access_token);
            Log.d("FB Sessions", "" + facebook.isSessionValid());
        }

        if (expires != 0)
        {
            facebook.setAccessExpires(expires);
        }

        if (!facebook.isSessionValid())
        {
            facebook.authorize(this,new String[] {"publish_actions","public_profile" },
                    new DialogListener()
                    {
                        @Override
                        public void onCancel(){
                        }

                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",facebook.getAccessToken());
                            editor.putLong("access_expires",facebook.getAccessExpires());
                            editor.commit();
                        }

                        @Override
                        public void onError(DialogError error) {
                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                        }
                    });
        }
    }*/

    /* Reading twitter essential configuration parameters from strings.xml */
    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    // method to login into the twitter
    private void loginToTwitter() {
        boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);
                /**
                 *  Loading twitter login page on webview for authorization
                 *  Once authorized, results are received at onActivityResult
                 *  */
                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
            new updateTwitterStatus().execute(getResources().getString(R.string.txtTwitterCheckOuttapnChat));
        }
    }

    // method to login to the linkedIn
    private void loginLinkedIn() {
        ProgressDialog progressDialog = new ProgressDialog(
                InfoActivity.this);

        LinkedinDialog d = new LinkedinDialog(InfoActivity.this,
                progressDialog);
        d.show();

        // set call back listener to get oauth_verifier value
        d.setVerifierListener(new OnVerifyListener() {
            @SuppressLint("NewApi")
            public void onVerify(String verifier) {
                try {
                    Log.i("LinkedinSample", "verifier: " + verifier);
                    accessToken = LinkedinDialog.oAuthService
                            .getOAuthAccessToken(LinkedinDialog.liToken,
                                    verifier);
                    LinkedinDialog.factory.createLinkedInApiClient(accessToken);
                    client = factory.createLinkedInApiClient(accessToken);
                    Log.i("LinkedinSample",
                            "ln_access_token: " + accessToken.getToken());
                    Log.i("LinkedinSample",
                            "ln_access_token: " + accessToken.getTokenSecret());
                    //Fri Sep 18 13:28:24 IST 2015
                    saveState.setAccessTokenLinkedin(mActivity,accessToken.getToken());
                    saveState.setAccessTokenSecretLinkedin(mActivity,accessToken.getTokenSecret());
                    //set login & access retrieving date
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    saveState.setLINKEDIN_POST_DATE(mActivity,dateFormat.format(date));
                    postOnLinkedIn();
                } catch (Exception e) {
                    Log.i("LinkedinSample", "error to get verifier");
                    e.printStackTrace();
                }
            }
        });
        // set progress dialog
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    // method to post on the linkedIn
    private void postOnLinkedIn()
    {
        String share =getResources().getString(R.string.txtTwitterCheckOuttapnChat);
        if (null != share && !share.equalsIgnoreCase("")) {
            OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);
            if((token!=null && !token.trim().equals("")) && tokenSecret!=null && !tokenSecret.trim().equals(""))
            {
                consumer.setTokenWithSecret(token,tokenSecret);
            }
            else{
                consumer.setTokenWithSecret(accessToken.getToken(), accessToken.getTokenSecret());
            }
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://api.linkedin.com/v1/people/~/shares");
            try {
                consumer.sign(post);
            } catch (OAuthMessageSignerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // here need the consumer for sign in for post the share
            post.setHeader("content-type", "text/XML");
            String myEntity = "<share><comment>"+ share +"</comment><visibility><code>anyone</code></visibility></share>";
            try {
                post.setEntity(new StringEntity(myEntity));
                org.apache.http.HttpResponse response = httpclient.execute(post);
                if(Integer.parseInt(String.valueOf(response.getStatusLine().getStatusCode()))==201)
                {
                    TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
                    dialog.newInstance("",mActivity,getResources().getString(R.string.txtLinkedInSuccesfullyShared),"",iNotifyGalleryDialog);
                    dialog.show(getSupportFragmentManager(), "test");
                }
                else if(Integer.parseInt(String.valueOf(response.getStatusLine().getStatusCode()))==400)
                {
                    ImageRequestDialog dialog = new ImageRequestDialog();
                    dialog.newInstance("",mActivity,getResources().getString(R.string.txtLinkedInAlreadyShared),"",null);
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "test");
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                String verifier = data.getExtras().getString(oAuthVerifier);
                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                    long userID = accessToken.getUserId();
                    @SuppressWarnings("unused")
                    final User user = twitter.showUser(userID);
                    //				String username = user.getName();
                    saveTwitterInfo(accessToken);
                    new updateTwitterStatus().execute(getResources().getString(R.string.txtTwitterCheckOuttapnChat));

                } catch (Exception e) {
                    //					Log.e("Twitter Login Failed", e.getMessage());
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        //		facebook.authorizeCallback(requestCode, resultCode, data);
    }

    // async task to post on twitter
    class updateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage(getResources().getString(R.string.txtPostToTwitter));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);
                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
                //				InputStream is = getResources().openRawResource(R.drawable.ic_delete);
                //				statusUpdate.setMedia("test.jpg", is);
                twitter4j.Status response = twitter.updateStatus(statusUpdate);
                Log.d("Status", response.getText());
            } catch (TwitterException e) {
                isPostedFirstTime=false;
                if(e.getMessage().toLowerCase().contains("duplicate") || e.getMessage().toLowerCase().contains("status is a duplicate"))
                {
                    ImageRequestDialog dialog = new ImageRequestDialog();
                    dialog.newInstance("",mActivity,getResources().getString(R.string.txtTwitterAlreadySharedMessage),"",null);
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "test");
                }
                Log.d("Failed to post!", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();
            if(isPostedFirstTime)
            {
                TweetFbPostSuccessfulDialog dialog=new TweetFbPostSuccessfulDialog();
                dialog.newInstance("",mActivity,getResources().getString(R.string.txtTwitterPostSuccessMessage),"",iNotifyGalleryDialog);
                dialog.show(getSupportFragmentManager(), "test");
            }
        }
    }

    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access token
     */
    private void saveTwitterInfo(AccessToken accessToken) {
        long userID = accessToken.getUserId();
        User user;
        try {
            user = twitter.showUser(userID);
            String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
            Editor e = mSharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.commit();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * set fragment on home screen,not on the tab screen
     *
     * @param currentFragment
     */
    public void setFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, currentFragment);
        fragmentTransaction.addToBackStack(currentFragment.toString());//.substring(0, currentFragment.toString().indexOf("ent") + 3));
        fragmentTransaction.commit();
    }

    /**
     * check availability of internet connection for calling faq/howTo Web service
     */
    public void checkInternetConnectionHowTo() {
        if (NetworkConnection.isNetworkAvailable(mActivity)) {
            if(adapterSelected_position==0)
                getHowToRequest();
            else if(adapterSelected_position==1){
                getFaqRequest();
            }
        }
        else{
            //display how to from database
            displayHowToFromDatabase();
        }
    }

    // method to call web service to get how to
    private void getHowToRequest()
    {
        MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_HOWTO,
                mActivity.getResources().getString(R.string.private_key),howtoResponsehandler);
    }

    // method to call web service to get faq
    private void getFaqRequest()
    {
        MyHttpConnection.getWithoutPara(mActivity,GlobalCommonValues.GET_FAQ,
                mActivity.getResources().getString(R.string.private_key),howtoResponsehandler);
    }

    //asyc task to handle howto request to the server
    AsyncHttpResponseHandler howtoResponsehandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
            if (progress!=null && !(progress.isShowing()))
                progress.show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if(response!=null){
                    Logs.writeLog("HowToWebService", "OnSuccess",response.toString());
                    getResponseHowTo(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if(response!=null)
                Logs.writeLog("HowToWebService", "OnFailure",response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            if (progress!=null && progress.isShowing())
                progress.dismiss();
        }
    };

    /*
     * Handling Response from the Server for the request being sent to get faq/howto
     */
    private void getResponseHowTo(String response) {
        Gson gson = new Gson();
        try {
            if (!TextUtils.isEmpty(response)&& GlobalConfig_Methods.isJsonString(response)) {
                listHowToResponseData=new ArrayList<HowtoReponseDataBean>();
                try {
                    HowtoReponse get_Response = gson.fromJson(
                            response, HowtoReponse.class);
                    if (get_Response.getResponse_code().equals(
                            GlobalCommonValues.SUCCESS_CODE)){
                        listHowToResponseData = get_Response.getData;
                        if(listHowToResponseData.size()>0){
                            if(adapterSelected_position==0){  // For HowTo
                                saveState.setIS_HOWTO_VERSION_UPDATED(InfoActivity.this, false);
                                DBQuery.deleteTable("HowTo", "", null, getApplicationContext());
                                DBQuery.insertHowTo(mActivity, listHowToResponseData);
                                saveState.setIS_HOWTO_VERSION_UPDATED(mActivity, false);
                            }else if(adapterSelected_position==1){  // For FAQ
                                saveState.setIS_FAQ_VERSION_UPDATED(InfoActivity.this, false);
                                DBQuery.deleteTable("Faq", "", null, getApplicationContext());
                                DBQuery.insertFAQ(mActivity, listHowToResponseData);
                                saveState.setIS_FAQ_VERSION_UPDATED(mActivity, false);
                            }
                            displayHowToFromDatabase();
                        }else{
                            //display how to from database
                            displayHowToFromDatabase();
                            iNotifyGalleryDialog.yes();
                        }
                    }
                    else{
                        //display how to from database
                        iNotifyGalleryDialog.yes();
                        displayHowToFromDatabase();
                    }
                }
                catch (Exception e) {
                    iNotifyGalleryDialog.yes();
                    e.getMessage();
                }

            } else {
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //method to display faq/howto from the database
    private void displayHowToFromDatabase(){
        iNotifyGalleryDialog.yes();
        String title="";
        //display how to from database
        listHowToDatabase =
                new ArrayList<HowtoReponseDataBean>();
        if(adapterSelected_position==0){
            listHowToDatabase = DBQuery.getAllHowTo(mActivity);
            title = "How To";
        }else if(adapterSelected_position==1){
            listHowToDatabase = DBQuery.getAllFAQ(mActivity);
            title = "FAQ";
        }
        if(listHowToDatabase!=null && listHowToDatabase.size()>0){
            HowToFragment objHomeFragment = new HowToFragment();
            objHomeFragment.newInstance(title,listHowToDatabase);
            setFragment(objHomeFragment);
        }else if(listHowToDatabase.size() == 0){
            if (NetworkConnection.isNetworkAvailable(mActivity))
                checkInternetConnectionHowTo();
        }
    }
}
