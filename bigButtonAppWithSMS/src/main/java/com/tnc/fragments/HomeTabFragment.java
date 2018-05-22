package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;

import android.Manifest;

import com.tnc.R;
import com.tnc.activities.PrivacyTermsOfUseActivity;
import com.tnc.adapter.TileAdapterHomeScreen;
import com.tnc.base.AppTabsConstants;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CallDetailsBean;
import com.tnc.bean.CategoryBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.bean.MessageSendBean;
import com.tnc.bean.MessageStatusUpdateBean;
import com.tnc.bean.NotificationBean;
import com.tnc.bean.NotificationReponseDataBean;
import com.tnc.bean.NotificationResponseTimer;
import com.tnc.bean.NotificationUpdateBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.DefaultImageConfirmation;
import com.tnc.dialog.DeleteChatConfirmationDialog;
import com.tnc.dialog.EmergencyCall;
import com.tnc.dialog.EmergencyNumberUpdateConfirmationDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.dialog.RegistrationCheckDialog;
import com.tnc.dialog.TileDeleteSuccessDialog;
import com.tnc.draggablegridviewpager.DraggableGridViewPager;
import com.tnc.draggablegridviewpager.DraggableGridViewPager.OnPageChangeListener;
import com.tnc.draggablegridviewpager.DraggableGridViewPager.OnRearrangeListener;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyActionUpdate;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.receivers.AlarmReceiver;
import com.tnc.utility.Logs;
import com.tnc.utility.Utils;
import com.tnc.webresponse.GetAllMessageResponseBean;
import com.tnc.webresponse.GetBBIDResponseBeanData;
import com.tnc.webresponse.NotificationImageStatusResponse;
import com.tnc.webresponse.NotificationResponse;
import com.tnc.webresponse.SendMessageReponseDataBean;
import com.tnc.webresponse.SendMessageResponse;
import com.tnc.widgets.CustomSpinner;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cz.msebera.android.httpclient.Header;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils;

import static android.content.Context.AUDIO_SERVICE;
import static com.tnc.common.GlobalCommonValues.NO;
import static com.tnc.common.GlobalCommonValues.YES;

/**
 * class to display home screen view of tiles
 *
 * @author a3logics
 */

public class HomeTabFragment extends BaseFragmentTabs implements
        OnClickListener {
    private ImageView imViewCall, imViewMessage;
    private ToggleButton toggleCallMessage;
    public CustomSpinner spinnerCategory;
    private DraggableGridViewPager gridview;
    public TileAdapterHomeScreen adapterTiles;
    private Display display;
    private int height = 0, width = 0;
    private int view_item_width = 0;
    private ArrayList<ContactTilesBean> listTiles;
    public Context mActivity;
    // private String CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY";
    private Gson gson;
    private int notificationID = 0;
    private int count = -1;
    private String tilesType = "call";
    private ArrayList<ContactTilesBean> listTilesBBUsers = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private final int refresh = 1;
    final int stoprefresh = 2;
    final int donothing = 3;
    private boolean webservices_oncall;
    private String imageUrl = "";
    private byte[] arrayImage = null;//,arrayUserImage=null;
    private ArrayList<BBContactsBean> listBBContacts = null;
    private LinearLayout llIndicatorHolder, llGridViewBackgroundHolder;
    private ImageView imViewIndicator;
    private TextView mTextPageNumber;
    private Button mBtnPreviousPage, mBtnNextPage, mBtnProfileMode, mBtnVoiceMail, mButtonDialPad, mContactSharing,
            mBtnSearch;
    private int selectedPagePosition = 0;
    int item = -1;
    boolean isPageSelected = false;
    float x1, x2;
    float y1, y2;
    int listsize = -1;
    public static Rect rectTile, rectEditButton, rectDeleteButton, rectCallButton;
    int TilePosition = -1;
    public static boolean isLongClicked = false;
    private String userImageURL = "";
    //	File fileUserImage=null;
    private Bitmap bitmapImage, bitmapUserImage;
    public static View viewDragged = null;
    private boolean isTimeOut = false;
    private ContactTilesBean objContactTileDelete = null;
    public static int X_Edit, Y_Edit, Top, Right, Left, Bottom, WIDTH, HEIGHT,
            X_Delete, Y_Delete, TopDelete, RightDelete, LeftDelete, BottomDelete, WIDTH_DELETE, HEIGHT_DELETE,
            X_Call, Y_Call, TopCall, RightCall, LeftCall, BottomCall, WIDTH_CALL, HEIGHT_CALL;
    private Receiver receiver;
    private MyCount counter;
    private int selectedTilePosition = -1;
    private String mEmergency;
    private String mEmergencyCallNumber = "";
    private ImageRequestDialog dialogDeactivation;
    public TransparentProgressDialog dialogProgress;
    private int BBID_Emergency;
    private String mScreenResolution = "";
    private int mHeightScreen = -1, mWidthScreen = -1;
    private String mCategoryID = "0";
    private ArrayList<CallDetailsBean> mListCallDetails = new ArrayList<CallDetailsBean>();
    private AudioManager mAudioManager = null;

    private Handler handler;
    public String mCurrentTab = AppTabsConstants.HOME;
    public HomeTabFragment newInstance(Context mActivity) {
        HomeTabFragment frag = new HomeTabFragment();
        this.mActivity = mActivity;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.hometabfragment, container, false);
//        GlobalConfig_Methods.testCopy();
        idInitialization(view);
        //GlobalConfig_Methods.getHashKey(mActivityTabs, "com.tnc");
        //				DBQuery.deleteTable("BBContacts", "", null, getActivity());

        //open elderly Mode Pop-up if user hasn't taken action yet
        if (saveState.getIsElderlyMode(getActivity()).trim().equalsIgnoreCase("")) {
            openElderlyModePopup();
        }

        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        dialogDeactivation = new ImageRequestDialog();
        saveState = new SharedPreference();
        dialogProgress = new TransparentProgressDialog(getActivity(), R.drawable.customspinner);

        mAudioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);

        HomeScreenActivity.btnNotification = (Button) view
                .findViewById(R.id.btnNotification);
        HomeScreenActivity.btnAddTile = (Button) view
                .findViewById(R.id.btnAddTile);
        imViewCall = (ImageView) view.findViewById(R.id.imViewCall);
        imViewMessage = (ImageView) view.findViewById(R.id.imViewMessage);
        HomeScreenActivity.tvUnreadNotificationCount = (TextView) view
                .findViewById(R.id.tvUnreadNotificationCount);
        toggleCallMessage = (ToggleButton) view.findViewById(R.id.toggleCallMessage);
        spinnerCategory = (CustomSpinner) view.findViewById(R.id.spinnerCategory);
//        llIndicatorHolder = (LinearLayout) view.findViewById(R.id.llIndicatorHolder);
        llGridViewBackgroundHolder = (LinearLayout) view.findViewById(R.id.llGridViewBackgroundHolder);

        gridview = (DraggableGridViewPager) view.findViewById(R.id.gridview);

        mTextPageNumber = (TextView) view.findViewById(R.id.textPageNumber);

        mBtnPreviousPage = (Button) view.findViewById(R.id.btnPreviousPage);
        mBtnNextPage = (Button) view.findViewById(R.id.btnNextPage);

        mBtnProfileMode = (Button) view.findViewById(R.id.btnProfileMode);
        mBtnVoiceMail = (Button) view.findViewById(R.id.btnVoiceMail);
        mButtonDialPad = (Button) view.findViewById(R.id.btnDialPad);
        mContactSharing = (Button) view.findViewById(R.id.btnContactSharing);

        mBtnSearch = (Button) view.findViewById(R.id.btnSearch);

        mBtnPreviousPage.setVisibility(View.GONE);
        mBtnNextPage.setVisibility(View.GONE);

        mBtnProfileMode.setOnClickListener(this);
        mBtnVoiceMail.setOnClickListener(this);
        mButtonDialPad.setOnClickListener(this);
        mContactSharing.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);

        gridview.setInterfarce(iNotifyTileEdit);
        HomeScreenActivity.btnEdit = (Button) view.findViewById(R.id.btnEdit);
        HomeScreenActivity.btnDelete = (Button) view.findViewById(R.id.btnDelete);
        HomeScreenActivity.btnCallEmergency = (ImageView) view.findViewById(R.id.btnCall);
        HomeScreenActivity.btnContactSharing = (Button) view.findViewById(R.id.btnContactSharing);
        HomeScreenActivity.btnDailPad = (Button) view.findViewById(R.id.btnDialPad);
        HomeScreenActivity.btnDisable = (Button) view.findViewById(R.id.btnDisable);
        HomeScreenActivity.btnProfileMode = (Button) view.findViewById(R.id.btnProfileMode);
        HomeScreenActivity.btnVoiceMail = (Button) view.findViewById(R.id.btnVoiceMail);
        HomeScreenActivity.btnEdit.setVisibility(View.INVISIBLE);
        HomeScreenActivity.btnDelete.setVisibility(View.INVISIBLE);

        HomeScreenActivity.btnAddTile.setBackgroundResource(R.drawable.add_new);

        setDimensions();
        //Phase -4
        HomeScreenActivity.flSettingButton.setVisibility(View.GONE);
        HomeScreenActivity.btnInformation.setVisibility(View.GONE);
        HomeScreenActivity.btnNotification.setVisibility(View.VISIBLE);


        //fetch category from database to display it in the dropdown
        ArrayList<CategoryBean> mListCategoryFromDB = new ArrayList<CategoryBean>();
        mListCategoryFromDB = DBQuery.getCategoriesForTiles(getActivity());

        ArrayList<String> mListCategory = new ArrayList<String>();

        for (CategoryBean mCategoryBean : mListCategoryFromDB) {
            mListCategory.add(mCategoryBean.getCategoryName());
        }

        //set dropdown adapter
        spinnerCategory.setListItems(mListCategory);

        //Set interface to handle click event of Custom Spinner
        spinnerCategory.setInterface(mINotifyCategorySelected);

        if (mListCategory != null && mListCategory.size() > 0)
            spinnerCategory.setText(mListCategory.get(0));

        listTiles = new ArrayList<ContactTilesBean>();

        mBtnPreviousPage.setOnClickListener(this);
        mBtnNextPage.setOnClickListener(this);

        //fetch chat buttons/tiles list from the database
        //listTiles = DBQuery.getAllTiles(mActivityTabs);// Fetching All Tiles - old

        /*if(spinnerCategory.mListItems!=null && spinnerCategory.mListItems.size()>0){
            mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(GlobalCommonValues.ButtonTypeAll);
        }else{
            mCategoryID = "0"; // for all
        }*/

        /*listTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(), mCategoryID,
                GlobalCommonValues.ButtonModecall);

        //set tiles gridview adapter
        adapterTiles = new TileAdapterHomeScreen(mActivityTabs, listTiles,
                view_item_width, tilesType);
        adapterTiles.notifyDataSetChanged();
        gridview.setAdapter(adapterTiles);*/

        mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(getActivity(), GlobalCommonValues.ButtonTypeRecentCalls);

        if (!saveState.getSCREEN_RESOLUTION(getActivity()).trim().equalsIgnoreCase("")) {
            mScreenResolution = saveState.getSCREEN_RESOLUTION(getActivity());
            mWidthScreen = Integer.parseInt(mScreenResolution.split(",")[0]);

            mHeightScreen = Integer.parseInt(mScreenResolution.split(",")[1]);
        }

        HomeScreenActivity.flBackArrow.setVisibility(View.GONE);
        if (saveState.isRegistered(getActivity())) {
            HomeScreenActivity.flInformationButton.setVisibility(View.VISIBLE);
            HomeScreenActivity.btnHome.setVisibility(View.VISIBLE);
            HomeScreenActivity.btnHome.setBackgroundResource(R.drawable.refresh);
        }
        if (!saveState.isRegistered(getActivity())) {
            HomeScreenActivity.flInformationButton.setVisibility(View.GONE);
            HomeScreenActivity.btnHome.setVisibility(View.GONE);
        }
        try {
            HomeScreenActivity.btnHome.setClickable(true);
            HomeScreenActivity.btnHome.setEnabled(true);
            HomeScreenActivity.btnNotification.setClickable(true);
            HomeScreenActivity.btnNotification.setEnabled(true);
            HomeScreenActivity.btnAddTile.setClickable(true);
            HomeScreenActivity.btnAddTile.setEnabled(true);
            HomeScreenActivity.btnCallEmergency.setClickable(true);
            HomeScreenActivity.btnCallEmergency.setEnabled(true);
            HomeScreenActivity.btnDisable.setClickable(true);
            HomeScreenActivity.btnDisable.setEnabled(true);
        } catch (Exception e) {
            e.getMessage();
        }

        displayTiles(toggleCallMessage.isChecked());

		/*adapterTiles = new TileAdapterHomeScreen(mActivityTabs, listTiles,
				view_item_width, tilesType);
		adapterTiles.notifyDataSetChanged();
		gridview.setAdapter(adapterTiles);*/
        toggleCallMessage.setChecked(false);
        HomeScreenActivity.btnDisable.setVisibility(View.VISIBLE);
        setUnreadNotificationCount();
        receiver = new Receiver();
        //Get User Details by Tnc ID and if the user is registered
        if (saveState.isRegistered(mActivityTabs) && !saveState.getPublicKey(mActivityTabs).trim().equals("")) {
            getBBID();
            //checkUserActivation();
        }

        HomeScreenActivity.btnEdit.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                HomeScreenActivity.btnEdit.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                HomeScreenActivity.btnEdit.getLocationOnScreen(locations);
                X_Edit = locations[0];
                Y_Edit = locations[1];
                Top = HomeScreenActivity.btnEdit.getTop();
                Right = HomeScreenActivity.btnEdit.getRight();
                Left = HomeScreenActivity.btnEdit.getLeft();
                Bottom = HomeScreenActivity.btnEdit.getBottom();
                WIDTH = HomeScreenActivity.btnEdit.getWidth();
                HEIGHT = HomeScreenActivity.btnEdit.getHeight();
                rectEditButton = new Rect(Left, Top, Right, Bottom);
                //				Toast.makeText(mActivityTabs, "X_Add--"+X_Remove+"  Y_Add--"+Y_Remove, Toast.LENGTH_LONG).show();
            }
        });

        HomeScreenActivity.btnDelete.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                HomeScreenActivity.btnDelete.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                HomeScreenActivity.btnDelete.getLocationOnScreen(locations);
                X_Delete = locations[0];
                Y_Delete = locations[1];
                TopDelete = HomeScreenActivity.btnDelete.getTop();
                RightDelete = HomeScreenActivity.btnDelete.getRight();
                LeftDelete = HomeScreenActivity.btnDelete.getLeft();
                BottomDelete = HomeScreenActivity.btnDelete.getBottom();
                WIDTH_DELETE = HomeScreenActivity.btnDelete.getWidth();
                HEIGHT_DELETE = HomeScreenActivity.btnDelete.getHeight();
                rectDeleteButton = new Rect(LeftDelete, TopDelete, RightDelete, BottomDelete);
                //				Toast.makeText(mActivityTabs, "X_Add--"+X_Remove+"  Y_Add--"+Y_Remove, Toast.LENGTH_LONG).show();
            }
        });

        HomeScreenActivity.btnCallEmergency.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                HomeScreenActivity.btnDelete.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                HomeScreenActivity.btnDelete.getLocationOnScreen(locations);
                X_Call = locations[0];
                Y_Call = locations[1];
                TopCall = HomeScreenActivity.btnDelete.getTop();
                RightCall = HomeScreenActivity.btnDelete.getRight();
                LeftCall = HomeScreenActivity.btnDelete.getLeft();
                BottomCall = HomeScreenActivity.btnDelete.getBottom();
                WIDTH_CALL = HomeScreenActivity.btnDelete.getWidth();
                HEIGHT_CALL = HomeScreenActivity.btnDelete.getHeight();
                rectCallButton = new Rect(LeftDelete, TopDelete, RightDelete, BottomDelete);
                //				Toast.makeText(mActivityTabs, "X_Add--"+X_Remove+"  Y_Add--"+Y_Remove, Toast.LENGTH_LONG).show();
            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                MainBaseActivity.isTileCreated = false;
                selectedTilePosition = -1;
                if (toggleCallMessage.isChecked()) { // In case of toggle is on
                    // Message Side
                    BBID_Emergency = 0;
                    try {
                        if (saveState.isRegistered(mActivityTabs)

                               && !saveState.getPublicKey(mActivityTabs)
                                .trim().equals("")) {

                            Toast.makeText(getActivity(), "Clicked On Vishakha", Toast.LENGTH_LONG).show();
                            MessagePredefinedComposeFragment messagePredefinedComposeFragment = new MessagePredefinedComposeFragment();
                            android.support.v4.app.FragmentManager fragmentManager=getFragmentManager();
                            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_chat,messagePredefinedComposeFragment);
                            fragmentTransaction.commit();

//                            HomeTabFragment objHomeFragment;
//
//                            MainBaseActivity.objTileEdit=null;
                            /* Set current tab.. */
//                            mCurrentTab = tabId;
//                            if (tabId.equals(AppTabsConstants.HOME)) {
//                                mCurrentTab_pos = 0;
//                                mCurrentTab = AppTabsConstants.HOME;
//                                objHomeFragment = new HomeTabFragment();
//                                objHomeFragment.newInstance(HomeScreenActivity.this);
//                                if (!isAlreadyHomeSelected) {
//                                    isAlreadyHomeSelected = true;
//                                    pushFragments(tabId, objHomeFragment);
//                                }
//                            }



                            if (counter == null) {
                                selectedTilePosition = position;
                                counter = new MyCount(500, 1000);
                                counter.start();
                            } else if (counter != null) {
                                counter.cancel();
                                counter = null;
                                String phoneNumber =
                                "";
                                BBID_Emergency = 0;
                                phoneNumber = listTiles.get(position).getPhoneNumber();

                                BBID_Emergency = DBQuery.getBBIDFromPhoneNumber(
                                        mActivityTabs, phoneNumber);


                                mEmergencyCallNumber = listTiles.get(position).getPrefix() + listTiles.get(position).getCountryCode() + phoneNumber;

                                Toast.makeText(getActivity(), "selectedPosition........." + selectedTilePosition, Toast.LENGTH_LONG).show();

                                DeleteChatConfirmationDialog dialog = new DeleteChatConfirmationDialog();
                                dialog.newInstance("", mActivityTabs, "DO YOU WANT TO INITIATE AN EMERGENCY CALL?", "", iNotifyCallEmergency);
                                dialog.setCancelable(false);
                                dialog.show(getChildFragmentManager(), "test");

                            } else if (!saveState.isRegistered(mActivityTabs)
                                    && saveState.getPublicKey(mActivityTabs).trim()
                                    .equals("")) {
                            RegistrationCheckDialog dialog = new RegistrationCheckDialog();
                            dialog.setCancelable(false);
                            dialog.newInstance("", mActivityTabs, Html
                                    .fromHtml(
                                            "Please create profile <br>"
                                                    + "to use this feature")
                                    .toString(), "", null, null);
                            dialog.show(getChildFragmentManager(), "test");


                                Toast.makeText(getActivity(), "CLICKED ON NAVEEN", Toast.LENGTH_LONG).show();
                                Toast.makeText(getActivity(), "selectedPosition........." + selectedTilePosition, Toast.LENGTH_LONG).show();
                            }



                        } else if (!saveState.isRegistered(mActivityTabs)
                                && saveState.getPublicKey(mActivityTabs).trim()
                                .equals("")) {
                            RegistrationCheckDialog dialog = new RegistrationCheckDialog();
                            dialog.setCancelable(false);
                            dialog.newInstance("", mActivityTabs, Html
                                    .fromHtml(
                                            "Please create profile <br>"
                                                    + "to use this feature")
                                    .toString(), "", null, null);
                            dialog.show(getChildFragmentManager(), "test");
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else { // In case of toggle is on Call Side
                    BBID_Emergency = 0;
                    if (counter == null) {
                        selectedTilePosition = position;
                        counter = new MyCount(500, 1000);
                        counter.start();
                    } else if (counter != null) {
                        counter.cancel();
                        counter = null;
                        if (saveState.isRegistered(mActivityTabs)) {
                            String phoneNumber = "";
                            BBID_Emergency = 0;
                            phoneNumber = listTiles.get(position)
                                    .getPhoneNumber();
                            BBID_Emergency = DBQuery.getBBIDFromPhoneNumber(
                                    mActivityTabs, phoneNumber);

                            //							makeCall(position);
                            selectedTilePosition = position;

                            DeleteChatConfirmationDialog dialog = new DeleteChatConfirmationDialog();
                            dialog.newInstance("", mActivityTabs, "DO YOU WANT TO INITIATE AN EMERGENCY CALL?", "", iNotifyCallEmergency);
                            dialog.setCancelable(false);
                            dialog.show(getChildFragmentManager(), "test");

                        } else if (!saveState.isRegistered(mActivityTabs)) {
                            RegistrationCheckDialog dialog = new RegistrationCheckDialog();
                            dialog.setCancelable(false);
                            dialog.newInstance("", mActivityTabs, Html
                                    .fromHtml(
                                            "Please create profile <br>"
                                                    + "to use this feature")
                                    .toString(), "", null, null);
                            dialog.show(getChildFragmentManager(), "test");
                        }
                    }
                }
            }
        });

        gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                boolean returnBoolean = false;

                if (!saveState.getIsDisableLongTap(mActivityTabs)) {
                    TilePosition = position;
                    isLongClicked = true;
                    viewDragged = view;
                    if (tilesType.equalsIgnoreCase("call")) {
                        view.setAlpha(.8f);
                        if (HomeScreenActivity.btnEdit.getVisibility() == View.INVISIBLE) {
                            HomeScreenActivity.btnEdit.setVisibility(View.VISIBLE);
                            HomeScreenActivity.btnContactSharing.setVisibility(View.GONE);
                            HomeScreenActivity.btnDailPad.setVisibility(View.GONE);
                            HomeScreenActivity.btnVoiceMail.setVisibility(View.GONE);
                            HomeScreenActivity.btnProfileMode.setVisibility(View.GONE);
                        }
                        if (HomeScreenActivity.btnDelete.getVisibility() == View.INVISIBLE) {
                            HomeScreenActivity.btnDelete.setVisibility(View.VISIBLE);
                        }
                    }
                    objContactTileDelete = new ContactTilesBean();
                    objContactTileDelete = listTiles.get(position);
                    rectTile = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    MainBaseActivity.isTileCreated = false;

                    if (toggleCallMessage.isChecked()) { // In case of toggle is on
                        // Message Side or the app is in elderly mode
                        returnBoolean = false; //  return false to disable long click on message side
                    } else { // In case of toggle is on Call Side
                        returnBoolean = true;
                    }
                }

                return returnBoolean;

                /*TilePosition=position;
                isLongClicked=true;
                viewDragged=view;
                if(tilesType.equalsIgnoreCase("call")){
                    view.setAlpha(.8f);
                    if(HomeScreenActivity.btnEdit.getVisibility()==View.INVISIBLE){
                        HomeScreenActivity.btnEdit.setVisibility(View.VISIBLE);
                    }
                    if(HomeScreenActivity.btnDelete.getVisibility()==View.INVISIBLE){
                        HomeScreenActivity.btnDelete.setVisibility(View.VISIBLE);
                    }
                }
                objContactTileDelete=new ContactTilesBean();
                objContactTileDelete=listTiles.get(position);
                rectTile = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                MainBaseActivity.isTileCreated = false;

                if (toggleCallMessage.isChecked() || saveState.getIsDisableLongTap(mActivityTabs)){ // In case of toggle is on
                    // Message Side or the app is in elderly mode
                    return false; //  return false to disable long click on message side
                } else{ // In case of toggle is on Call Side
                    return true;
                }*/
            }
        });

        gridview.setOnRearrangeListener(new OnRearrangeListener() {
            @Override
            public void onRearrange(int oldIndex, int newIndex) {
                MainBaseActivity.isTileCreated = false;
                int position;
                if (HomeScreenActivity.btnEdit.getVisibility() == View.VISIBLE) {
                    HomeScreenActivity.btnEdit.setVisibility(View.INVISIBLE);
                    HomeScreenActivity.btnContactSharing.setVisibility(View.VISIBLE);
                    HomeScreenActivity.btnDailPad.setVisibility(View.VISIBLE);
                    HomeScreenActivity.btnVoiceMail.setVisibility(View.VISIBLE);
                    HomeScreenActivity.btnProfileMode.setVisibility(View.VISIBLE);
                }
                if (HomeScreenActivity.btnDelete.getVisibility() == View.VISIBLE) {
                    HomeScreenActivity.btnDelete.setVisibility(View.INVISIBLE);
                }
                if (oldIndex < newIndex) {
                    for (int i = oldIndex + 1; i <= newIndex; i++) {
                        position = Integer.parseInt(listTiles.get(i)
                                .getTilePosition()) - 1;
                        DBQuery.updateTilePositionOnRearrange(mActivityTabs,
                                position, listTiles.get(i).getPhoneNumber());
                    }

                    DBQuery.updateTilePositionOnRearrange(mActivityTabs,
                            newIndex, listTiles.get(oldIndex).getPhoneNumber());

                } else if (newIndex < oldIndex) {
                    for (int i = newIndex; i < oldIndex; i++) {
                        position = Integer.parseInt(listTiles.get(i)
                                .getTilePosition()) + 1;
                        DBQuery.updateTilePositionOnRearrange(mActivityTabs,
                                position, listTiles.get(i).getPhoneNumber());
                    }
                    DBQuery.updateTilePositionOnRearrange(mActivityTabs,
                            newIndex, listTiles.get(oldIndex).getPhoneNumber());
                }
                /*//				setTilesAdapter();*/
                new DisplayTilesASynTask().execute();
            }
        });

        toggleCallMessage
                .setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (saveState.isRegistered(mActivityTabs)) {// in case of registered user
                            try {
                                gridview.AnimationEnd();
                            } catch (Exception e) {
                                e.getMessage();
                            }

                            mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(getActivity(), spinnerCategory.getText().toString());

                            displayTiles(isChecked);
                        } else { // in case of non-registered user
                            if (!saveState.isRegistered(mActivityTabs)) {
                                ImageRequestDialog dialog = new ImageRequestDialog();
                                dialog.setCancelable(false);
                                dialog.newInstance("", mActivityTabs, Html.fromHtml(
                                        "Please create profile <br>"
                                                + "to use this feature").toString(), "",
                                        null, null);
                                dialog.show(getChildFragmentManager(), "test");
                            }
                        }
                    }
                });

        toggleCallMessage.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent touchevent) {

                switch (touchevent.getAction()) {

                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN: {
                        x1 = touchevent.getX();
                        y1 = touchevent.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        x2 = touchevent.getX();
                        y2 = touchevent.getY();

                        if (x1 == x2) {
                            if (toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(false);
                            } else if (!toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(true);
                            }

                        } else if (y1 == y2) {
                            if (toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(false);
                            } else if (!toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(true);
                            }
                        }

                        // if left to right sweep event on screen
                        else if (x1 < x2) {
                            if (toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(false);
                            } else if (!toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(true);
                            }
                        }

                        // if right to left sweep event on screen
                        else if (x1 > x2) {
                            if (toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(false);
                            } else if (!toggleCallMessage.isChecked()) {
                                toggleCallMessage.setChecked(true);
                            }
                        }

                        // if UP to Down sweep event on screen
                        else if (y1 < y2) {
                        }

                        // if Down to UP sweep event on screen
                        else if (y1 > y2) {
                        }
                        break;
                    }
                }
                return true;
            }
        });

        imViewCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (saveState.isRegistered(mActivityTabs)) {
                    if (toggleCallMessage.isChecked()) {
                        toggleCallMessage.setChecked(false);
                    } else if (!toggleCallMessage.isChecked()) {
                        toggleCallMessage.setChecked(true);
                    }
                } else {
                    RegistrationCheckDialog dialog = new RegistrationCheckDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance("", mActivityTabs, Html
                            .fromHtml(
                                    "Please create profile <br>"
                                            + "to use this feature")
                            .toString(), "", null, null);
                    dialog.show(getChildFragmentManager(), "test");
                }
            }
        });

        imViewMessage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (saveState.isRegistered(mActivityTabs)) {
                    if (toggleCallMessage.isChecked()) {
                        toggleCallMessage.setChecked(false);
                    } else if (!toggleCallMessage.isChecked()) {
                        toggleCallMessage.setChecked(true);
                    }
                } else {

                    RegistrationCheckDialog dialog = new RegistrationCheckDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance("", mActivityTabs, Html
                            .fromHtml(
                                    "Please create profile <br>"
                                            + "to use this feature")
                            .toString(), "", null, null);
                    dialog.show(getChildFragmentManager(), "test");
                }
            }
        });

        HomeScreenActivity.btnAddTile.setOnClickListener(this);
        HomeScreenActivity.btnNotification.setOnClickListener(this);
        HomeScreenActivity.btnCallEmergency.setOnClickListener(this);
        HomeScreenActivity.btnDisable.setOnClickListener(this);
        HomeScreenActivity.btnHome.setOnClickListener(this);
        if (saveState.isTileCreated(mActivityTabs)) {
            isPageSelected = true;
            //Commented a part of New Functionality
            /*if((mHeightScreen>1 && mWidthScreen>1) &&
                    (mHeightScreen<=854 && mWidthScreen<=480)){
                //in case of hdpi
                if (listTiles.size() % 2 != 0) {
                    // int sizeOne=listTiles.size()%6;
                    int sizeTwo = listTiles.size() / 2 + 1;
                    listsize = sizeTwo;

                } else if (listTiles.size() % 2 == 0) {
                    listsize = listTiles.size() / 2;
                }
            }else{
                //in case of greater resolution device
                if (listTiles.size() % 6 != 0) {
                    // int sizeOne=listTiles.size()%6;
                    int sizeTwo = listTiles.size() / 6 + 1;
                    listsize = sizeTwo;

                } else if (listTiles.size() % 6 == 0) {
                    listsize = listTiles.size() / 6;
                }
            }
            if(listsize>1)
                displayIndicators("call", listsize-1);*/
            saveState.setTileCreated(mActivityTabs, false);
        } else {
            if (toggleCallMessage.isChecked()) {

                /* Commented a part of New Functionality
                if((mHeightScreen>1 && mWidthScreen>1) &&
                        (mHeightScreen<=854 && mWidthScreen<=480)){
                    // in case of hdpi
                    if(listTilesBBUsers.size()>2)
                        displayIndicators("message", 0);
                    else
                        llIndicatorHolder.removeAllViews();
                }else{
                    //in case of greater resolution device
                    if(listTilesBBUsers.size()>6)
                        displayIndicators("message", 0);
                    else
                        llIndicatorHolder.removeAllViews();
                }*/
            } else {
                /* Commented a part of New Functionality
                if((mHeightScreen>1 && mWidthScreen>1) &&
                        (mHeightScreen<=854 && mWidthScreen<=480)){
                    // in case of hdpi
                    if (listTiles.size() % 2 != 0) {
                        int sizeTwo = listTiles.size() / 2 + 1;
                        listsize = sizeTwo;

                    } else if (listTiles.size() % 2 == 0) {
                        listsize = listTiles.size() / 2;
                    }
                    if(listTiles.size()>2)
                        displayIndicators("call", 0);
                    else
                        llIndicatorHolder.removeAllViews();

                }else{
                    //in case of greater resolution device
                    if (listTiles.size() % 6 != 0) {
                        // int sizeOne=listTiles.size()%6;
                        int sizeTwo = listTiles.size() / 6 + 1;
                        listsize = sizeTwo;

                    } else if (listTiles.size() % 6 == 0) {
                        listsize = listTiles.size() / 6;
                    }
                    if(listTiles.size()>6)
                        displayIndicators("call", 0);
                    else
                        llIndicatorHolder.removeAllViews();
                }*/
            }
			/*if (listTiles.size() > 6) {
				//displayIndicators("call", 0);

				if (listTiles.size() % 6 != 0) {
					// int sizeOne=listTiles.size()%6;
					int sizeTwo = listTiles.size() / 6 + 1;
					listsize = sizeTwo;

				} else if (listTiles.size() % 6 == 0) {
					listsize = listTiles.size() / 6;
				}
				if(toggleCallMessage.isChecked())
					displayIndicators("message",listsize);
				else
					displayIndicators("call",listsize);

			} else {
				llIndicatorHolder.removeAllViews();
			}*/
        }
        gridview.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (!isPageSelected) {

                    // Call method to update page swipe arrow status and page number
                    updatePageNumberAndSwipeStatus();

                    //if (position < 10) {

                    selectedPagePosition = position;
                    if (toggleCallMessage.isChecked()) { // In case of toggle
                        // is on Message
                        // Side

                            /* Commented a aprt of New Functionality
                            if((mHeightScreen>1 && mWidthScreen>1) &&
                                    (mHeightScreen<=854 && mWidthScreen<=480)){
                                if(listTilesBBUsers.size()>2){
                                    displayIndicators("message", selectedPagePosition);
                                }else{
                                    llIndicatorHolder.removeAllViews();
                                }
                            }else{
                                if(listTilesBBUsers.size()>6){
                                    displayIndicators("message", selectedPagePosition);
                                }else{
                                    llIndicatorHolder.removeAllViews();
                                }
                            }*/
                    } else {
                        // In case of toggle is on Call Side
                            /* Commented a part of New Functionality
                            if((mHeightScreen>1 && mWidthScreen>1) &&
                                    (mHeightScreen<=854 && mWidthScreen<=480)){
                                if(listTiles.size()>2){
                                    displayIndicators("call", selectedPagePosition);
                                }else{
                                    llIndicatorHolder.removeAllViews();
                                }
                            }else{
                                if(listTiles.size()>6){
                                    displayIndicators("call", selectedPagePosition);
                                }else{
                                    llIndicatorHolder.removeAllViews();
                                }
                            }*/
                    }
                    //}
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isPageSelected = false;
            }
        });
    }

    /**
     * Method to set elderly mode on/off
     */
    private void openElderlyModePopup() {
        DefaultImageConfirmation mDialog = new DefaultImageConfirmation();
        mDialog.setCancelable(false);
        mDialog.newInstance("", getActivity(), getResources().getString(R.string.txtActivateElderlyMode),
                "", mActionElderlyMode);
        mDialog.show(getChildFragmentManager(), "Test");
    }

    /**
     * Interface to handle action taken to enable/disable elderly mode
     */
    INotifyGalleryDialog mActionElderlyMode = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // in case user clicks yes to activate elderly mode
            saveState.setIsElderlyMode(getActivity(),
                    YES);
            GlobalConfig_Methods.ConfigureElderlyModeOptions(getActivity(), true, true, true);
        }

        @Override
        public void no() {
            // in case user clicks no to de-activate elderly mode
            saveState.setIsElderlyMode(getActivity(),
                    NO);
            GlobalConfig_Methods.ConfigureElderlyModeOptions(getActivity(), false, false, false);
        }
    };


    /**
     * Interface to handle click action of category dropdown
     */
    INotifyAction mINotifyCategorySelected = new INotifyAction() {
        @Override
        public void setAction(String category) {

            // Clear Preferences value for Saved Selected Category at the time of Creating Tiles
            GlobalConfig_Methods.clearSelectedcategoryValueFromPreferences(getActivity());

            mCategoryID = GlobalConfig_Methods.getCategoryIdFromString(getActivity(), category);

            displayTiles(toggleCallMessage.isChecked());

        }
    };

    /**
     * interface to handle call to emergency nmber upon confirmation
     */
    INotifyGalleryDialog iNotifyCallEmergency = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // cancel the counter
            if (counter != null) {
                counter.cancel();
                counter = null;
            }

            if (tilesType.equalsIgnoreCase("call")) {
                saveState.setIS_EMERGENCY_NUMBER_DIALLED(getActivity(), true);
                makeCall(selectedTilePosition);
                if (BBID_Emergency >= 0) {
                    callWebServiceEmergency(String.valueOf(BBID_Emergency), saveState.getUserName(mActivityTabs).toUpperCase());
                }
            } else if (tilesType.equalsIgnoreCase("message")) {
                saveState.setIS_EMERGENCY_NUMBER_DIALLED(getActivity(), true);
                makeEmergencyCall();
            }
        }

        @Override
        public void no() {
            // cancel the counter
            if (counter != null) {
                counter.cancel();
                counter = null;
            }
        }
    };

    /**
     * Method to make an emergency call
     */
    @SuppressLint("MissingPermission")
    private void makeEmergencyCall() {

        /*int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CALL_PHONE,
                Manifest.permission.CALL_PRIVILEGED};

        if(!hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }*/

        // Make emergency Messsage
        if (BBID_Emergency >= 0) {
            callWebServiceEmergency(String.valueOf(BBID_Emergency), saveState.getUserName(mActivityTabs).toUpperCase());
        }

        //Make emergency Call
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mEmergencyCallNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        try {
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to make a call
     */
    private void makeCall(int position) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String num = listTiles.get(position).getPrefix() + listTiles.get(position).getCountryCode()
                + listTiles.get(position).getPhoneNumber();
        callIntent.setData(Uri.parse("tel:" + num.trim()));
        //update boolean value in preference as number is dialled from the app
        saveState.setIS_NUMBER_DIALLED(getActivity(), true);
        startActivity(callIntent);

        try {
            // update call log status as read if it is unread
            DBQuery.updateCallLogStatus(getActivity(), listTiles.get(position).getPhoneNumber());
        } catch (Exception e) {
            e.getMessage();
        }

        //update unread call log count on the tile on home screen
        if (adapterTiles != null && adapterTiles.getCount() > 0) {
            adapterTiles.notifyDataSetChanged();
        }

        // update unread call log count
        if (getActivity() instanceof HomeScreenActivity) {
            ((HomeScreenActivity) getActivity()).setUnreadMessageCount();
        }

        counter = null;
    }

    // Check Internet Connection
    // call web servicemakeCall
    private void callWebServiceEmergency(String bbid, String name) {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            MessageSendBean messageSendBean = new MessageSendBean(bbid, "EMERGENCY CALL FROM " + name);
            sendMessageRequestEmergency(messageSendBean);
            //getNotificationStatus(notificationBean);
        } else {
        }
    }

    /**
     * Method to call web service to send emergency message
     *
     * @param messageSendBean
     */
    private void sendMessageRequestEmergency(MessageSendBean messageSendBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(messageSendBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.SEND_MESSAGE, stringEntity,
                    sendMessageResponsehandlerEmergency,
                    mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //  async task to handle call to web service to send emergency message
    AsyncHttpResponseHandler sendMessageResponsehandlerEmergency = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("MessageGetResponse-HomeTabFragment", "OnSuccess", response.toString());
                    getResponseSendMessageEmergency(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null) {
                Logs.writeLog("MessageGetResponse-HomeTabFragment", "onFailure", response);
            }
        }

        @Override
        public void onFinish() {
        }
    };

    /**
     * method  to handle response we get from the server after we sent an emergency message
     *
     * @param response
     */
    private void getResponseSendMessageEmergency(String response) {
        try {
            if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response)) {
                gson = new Gson();
                SendMessageResponse get_Response = gson.fromJson(response, SendMessageResponse.class);
                if (get_Response.response_code.equals(GlobalCommonValues.SUCCESS_CODE)) {
                    ArrayList<SendMessageReponseDataBean> listMessageDetails = new ArrayList<SendMessageReponseDataBean>();
                    SendMessageReponseDataBean objSendMessage = new SendMessageReponseDataBean();
                    objSendMessage.setMessage_id(get_Response.getData.message_id);
                    objSendMessage.setFrom_user_id(get_Response.getData.from_user_id);
                    objSendMessage.setFrom_user_phone(get_Response.getData.from_user_phone);
                    objSendMessage.setTo_user_id(get_Response.getData.to_user_id);
                    objSendMessage.setMessage(Uri.decode(get_Response.getData.message));
                    objSendMessage.setStatus(get_Response.getData.status);
                    objSendMessage.setDatatime(get_Response.getData.datatime);
                    objSendMessage.setName(get_Response.getData.name);
                    listMessageDetails.add(objSendMessage);
                    DBQuery.insertMessage(mActivityTabs, listMessageDetails);

                } else if (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_1)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_2)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_5)) {
                }
            } else {
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mActivityTabs.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * interface to update unread notification count
     */
    INotifyGalleryDialog INotifyCount = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            enableRefreshing();
        }

        @Override
        public void no() {
        }
    };

    /**
     * Method to set unread notification count
     */
    private void setUnreadNotificationCount() {
        try {
            if (mActivityTabs instanceof HomeScreenActivity) {
                count = -1;
                count = ((HomeScreenActivity) mActivityTabs)
                        .getUnreadNotificationCount();
                if (count > 0) {
                    HomeScreenActivity.tvUnreadNotificationCount
                            .setVisibility(View.VISIBLE);
                    HomeScreenActivity.tvUnreadNotificationCount.setText(String
                            .valueOf(count));
                } else {
                    HomeScreenActivity.tvUnreadNotificationCount
                            .setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to set the tiles to be displayed on the home screen
     *
     * @param isChecked
     */
    private void displayTiles(boolean isChecked) {
        if (isChecked) // In case of toggle is on Message Side
        {
            //gridview.setBackgroundResource(R.drawable.background_textmode);
            llGridViewBackgroundHolder.setBackgroundResource(R.drawable.background_textmode);
            imViewMessage.setBackgroundResource(R.drawable.ic_chat_mode_enabled);
            imViewCall.setBackgroundResource(R.drawable.ic_call_mode_disabled);
            HomeScreenActivity.btnAddTile.setBackgroundResource(R.drawable.add_new_grey);
            HomeScreenActivity.btnAddTile.setEnabled(false);
            HomeScreenActivity.btnAddTile.setClickable(false);
            tilesType = "message";
        } else { // In case of toggle is on Call Side
            //gridview.setBackgroundResource(R.drawable.background_callmode);
            llGridViewBackgroundHolder.setBackgroundResource(R.drawable.background_callmode);
            imViewMessage.setBackgroundResource(R.drawable.ic_chat_mode_disabled);
            imViewCall.setBackgroundResource(R.drawable.ic_call_mode_enabled);
            HomeScreenActivity.btnAddTile.setBackgroundResource(R.drawable.add_new);
            HomeScreenActivity.btnAddTile.setEnabled(true);
            HomeScreenActivity.btnAddTile.setClickable(true);
            tilesType = "call";
        }
        gridview.tilesType = tilesType;
        new DisplayTilesASynTask().execute();
        /*//setTilesAdapter();*/
    }

    // set Tiles Adapter

    public void setTilesAdapter() {
        listTiles = new ArrayList<ContactTilesBean>();
        //listTiles = DBQuery.getAllTiles(mActivityTabs);// Fetching All Tiles - old

        // Get Call Details from the CallLogs Table
        mListCallDetails = new ArrayList<CallDetailsBean>();

        mListCallDetails = DBQuery.getCallLogsIncomingOutgoing(getActivity());

        // From DB
        if (tilesType.equals("call")) {  // start of call mode tile

            if ((saveState.getSELECTED_CATEGORY(getActivity()) != null) &&
                    !(saveState.getSELECTED_CATEGORY(getActivity()).trim().equalsIgnoreCase(""))) {
                // in case newly created tile

//                spinnerCategory.setSelection(1);

                String mCategoryName = saveState.getSELECTED_CATEGORY(getActivity());

                if (mCategoryName.contains(",")) {
                    mCategoryName = mCategoryName.split(",")[0];
                }

                spinnerCategory.setText(GlobalConfig_Methods.getCategoryNameFromId(getActivity(), mCategoryName));

                listTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(),
                        mCategoryName, tilesType);

            } else {
                // For Frequent Calls Category
                if (mCategoryID.equalsIgnoreCase(GlobalCommonValues.ButtonTypeRecentCalls) &&
                        mListCallDetails != null && !mListCallDetails.isEmpty()) {

                    // in case of any frequently called category selected
                    ArrayList<ContactTilesBean> mContactTiles = new ArrayList<ContactTilesBean>();

                    // fetch all the tiles
                    mContactTiles = DBQuery.getAllTiles(getActivity());

                    for (ContactTilesBean mContactTilesBean : mContactTiles) {
                        // iterate through tiles
                        tileLoopTag:
                        for (CallDetailsBean mCallDetailsBean : mListCallDetails) {
                            // iterate through call logs
                            if (mCallDetailsBean.getCallingNumber().contains(mContactTilesBean.getPhoneNumber()) ||
                                    mContactTilesBean.getPhoneNumber().contains(mCallDetailsBean.getCallingNumber())) {

                                // if number matches then add it into a new list of tiles
                                listTiles.add(mContactTilesBean);

                                break tileLoopTag;

                            }
                        }
                    }
                } else {

                    // if recent calls category is selected and no recent calls then make 'all' category selected
                    if (mCategoryID.equalsIgnoreCase("0") || mCategoryID.equalsIgnoreCase("-1")) {
                        mCategoryID = "all";
                        spinnerCategory.setSelection(1);
                        spinnerCategory.setText(spinnerCategory.mListItems.get(1));
                    }

                    // in case of any other category selected
                    listTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(), mCategoryID, tilesType);
                }
            }

            adapterTiles = new TileAdapterHomeScreen(mActivityTabs, listTiles,
                    view_item_width, tilesType, mActionUpdatePageArrowAndNumber);
            adapterTiles.notifyDataSetChanged();
            try {
                gridview.setAdapter(adapterTiles);
            } catch (Exception e) {
                e.getMessage();
            }

            // end of call mode tiles
        } else if (tilesType.equals("message")) { // start of chat mode tiles
            try {
                if ((saveState.getSELECTED_CATEGORY(getActivity()) != null) &&
                        !(saveState.getSELECTED_CATEGORY(getActivity()).trim().equalsIgnoreCase(""))) {
                    // in case newly created tile

//                    spinnerCategory.setSelection(1);

                    String mCategoryName = saveState.getSELECTED_CATEGORY(getActivity());

                    if (mCategoryName.contains(",")) {
                        mCategoryName = mCategoryName.split(",")[0];
                    }

                    spinnerCategory.setText(GlobalConfig_Methods.getCategoryNameFromId(getActivity(), mCategoryName));

                    listTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(),
                            mCategoryName, tilesType);
                } else {
                    // For Frequent Calls Category
                    if (mCategoryID.equalsIgnoreCase(GlobalCommonValues.ButtonTypeRecentCalls) && mListCallDetails != null && !mListCallDetails.isEmpty()) {

                        // in case of any frequently called category selected
                        ArrayList<ContactTilesBean> mContactTiles = new ArrayList<ContactTilesBean>();

                        // fetch all the tiles of Tnc User
                        mContactTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(),
                                GlobalCommonValues.ButtonTypeAll, "message");

                        for (ContactTilesBean mContactTilesBean : mContactTiles) {
                            // iterate through tiles
                            tileLoopTag:
                            for (CallDetailsBean mCallDetailsBean : mListCallDetails) {
                                // iterate through call logs
                                if (mCallDetailsBean.getCallingNumber().contains(mContactTilesBean.getPhoneNumber()) ||
                                        mContactTilesBean.getPhoneNumber().contains(mCallDetailsBean.getCallingNumber())) {

                                    // if number matches then add it into a new list of tiles
                                    listTiles.add(mContactTilesBean);

                                    break tileLoopTag;

                                }
                            }
                        }
                    } else {

                        // if recent calls category is selected and no recent calls then make 'all' category selected
                        if (mCategoryID.equalsIgnoreCase("0") || mCategoryID.equalsIgnoreCase("-1")) {
                            mCategoryID = "all";
                            spinnerCategory.setSelection(1);
                            spinnerCategory.setText(spinnerCategory.mListItems.get(1));
                        }

                        // in case of any other category selected
                        listTiles = DBQuery.getChatstasyTilesFromCategory(getActivity(), mCategoryID, tilesType);
                    }
                }
                adapterTiles = new TileAdapterHomeScreen(mActivityTabs,
                        listTiles, view_item_width, tilesType, mActionUpdatePageArrowAndNumber);
                adapterTiles.notifyDataSetChanged();
                gridview.setAdapter(adapterTiles);
            } catch (Exception e) {
                e.getMessage();
            }

        } // end of chat mode tiles

        if (listTiles != null && listTiles.size() == 0) {
            mBtnPreviousPage.setVisibility(View.GONE);
            mBtnNextPage.setVisibility(View.GONE);
        }
    }

    // Display Circle Indicators
    private void displayIndicators(String type, int selectedPosition) {

        if (saveState.getIS_EMERGENCY_CONTACT_CREATED(getActivity())) {
            saveState.setIS_EMERGENCY_CONTACT_CREATED(getActivity(), false);
        }

       /* Commented as a part of New Functionality
       listsize = -1;
        llIndicatorHolder.removeAllViews();

        try{
            if((mHeightScreen>1 && mWidthScreen>1) &&
                    (mHeightScreen<=854 && mWidthScreen<=480)){
                // In case of resolution is for lower quality devices/HDPI

                if (type.equalsIgnoreCase("call")) {
                    if (listTiles.size() % 2 != 0) {
                        // int sizeOne=listTiles.size()%6;
                        int sizeTwo = listTiles.size() / 2 + 1;
                        listsize = sizeTwo;

                    } else if (listTiles.size() % 2 == 0) {
                        listsize = listTiles.size() / 2;
                    }
                } else if (type.equalsIgnoreCase("message")) {
                    if (listTilesBBUsers.size() % 2 != 0) {
                        // int sizeOne=listBBContacts.size()%6;
                        int sizeTwo = listTilesBBUsers.size() / 2 + 1;
                        listsize = sizeTwo;
                    } else if (listTilesBBUsers.size() % 2 == 0) {
                        listsize = listTilesBBUsers.size() / 2;
                    }
                }

                // Make Circle Indicator Views
                for (int i = 0; i < listsize; i++) {
                    if (mActivityTabs instanceof MainBaseActivity)
                        imViewIndicator = new ImageView(
                                ((MainBaseActivity) mActivityTabs));
                    else if (mActivityTabs instanceof HomeScreenActivity)
                        imViewIndicator = new ImageView(
                                ((HomeScreenActivity) mActivityTabs));
                    LinearLayout.LayoutParams params;
                    if((mHeightScreen>1 && mWidthScreen>1) &&
                            (mHeightScreen<=854 && mWidthScreen<=480)){
                        params = new LinearLayout.LayoutParams(
                                20, 20);
                    }else{
                        params = new LinearLayout.LayoutParams(
                                32, 32);
                    }

                    params.setMargins(5, 5, 5, 5);
                    imViewIndicator.setTag(i);
                    imViewIndicator.setLayoutParams(params);
                    if(saveState.getIS_EMERGENCY_CONTACT_CREATED(getActivity())){
                        if (i != -1 && i == 0) {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_selected);
                        } else {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_unselected);
                        }
                    }else{
                        if (i != -1 && i == selectedPosition) {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_selected);
                        } else {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_unselected);
                        }
                    }

                    final int size = listsize;
                    imViewIndicator.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedItemToDisplay(size);
							*//*	// displayIndicators(tilesType,item);*//*
                        }
                    });
                    llIndicatorHolder.addView(imViewIndicator);
                }
            }else{
                //In case device resolution is better than hdpi
                if (type.equalsIgnoreCase("call")) {
                    if (listTiles.size() % 6 != 0) {
                        // int sizeOne=listTiles.size()%6;
                        int sizeTwo = listTiles.size() / 6 + 1;
                        listsize = sizeTwo;

                    } else if (listTiles.size() % 6 == 0) {
                        listsize = listTiles.size() / 6;
                    }
                } else if (type.equalsIgnoreCase("message")) {
                    if (listTilesBBUsers.size() % 6 != 0) {
                        // int sizeOne=listBBContacts.size()%6;
                        int sizeTwo = listTilesBBUsers.size() / 6 + 1;
                        listsize = sizeTwo;
                    } else if (listTilesBBUsers.size() % 6 == 0) {
                        listsize = listTilesBBUsers.size() / 6;
                    }
                }

                // Make Circle Indicator Views
                for (int i = 0; i < listsize; i++) {
                    if (mActivityTabs instanceof MainBaseActivity)
                        imViewIndicator = new ImageView(
                                ((MainBaseActivity) mActivityTabs));
                    else if (mActivityTabs instanceof HomeScreenActivity)
                        imViewIndicator = new ImageView(
                                ((HomeScreenActivity) mActivityTabs));
                    LinearLayout.LayoutParams params;
                    if((mHeightScreen>1 && mWidthScreen>1) &&
                            (mHeightScreen<=854 && mWidthScreen<=480)){
                        params = new LinearLayout.LayoutParams(
                                20, 20);
                    }else{
                        params = new LinearLayout.LayoutParams(
                                32, 32);
                    }
                    params.setMargins(5, 5, 5, 5);
                    imViewIndicator.setTag(i);
                    imViewIndicator.setLayoutParams(params);

                    if(saveState.getIS_EMERGENCY_CONTACT_CREATED(getActivity())){
                        if (i != -1 && i == 0) {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_selected);
                        } else {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_unselected);
                        }
                    }else{
                        if (i != -1 && i == selectedPosition) {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_selected);
                        } else {
                            imViewIndicator
                                    .setBackgroundResource(R.drawable.circle_indicator_unselected);
                        }
                    }

                    final int size = listsize;
                    imViewIndicator.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedItemToDisplay(size);
                        }
                    });
                    llIndicatorHolder.addView(imViewIndicator);
                }
            }
            if(saveState.getIS_EMERGENCY_CONTACT_CREATED(getActivity())){
                saveState.setIS_EMERGENCY_CONTACT_CREATED(getActivity(), false);
            }
        }catch(Exception e){
            e.getMessage();
            if(saveState.getIS_EMERGENCY_CONTACT_CREATED(getActivity())){
                saveState.setIS_EMERGENCY_CONTACT_CREATED(getActivity(), false);
            }
        }*/
    }

    private void selectedItemToDisplay(int size) {
        if (gridview.getCurrentItem() <= size) {
            isPageSelected = true;
            gridview.setCurrentItem(gridview.getCurrentItem() + 1);
            displayIndicators(tilesType, gridview.getCurrentItem());
        }
    }

    // Call a WebService to update message status from unread to read
    public void updateMessageStatus(MessageStatusUpdateBean updateStatusBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(updateStatusBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.UPDATE_MESSAGE_STATUS, stringEntity,
                    updateMessageResponseHandler,
                    mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    AsyncHttpResponseHandler updateMessageResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            if (response != null)
                Logs.writeLog("Update Message Status", "OnSuccess", response.toString());
            try {
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("Update Message Status", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
            /*
             * if (progress.isShowing()) progress.dismiss();
             */
        }
    };

    /**
     * request for getting notification list from the server
     *
     * @param notificationBean
     */
    private void getNotificationList(NotificationBean notificationBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(notificationBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.NOTIFICATION_LIST, stringEntity,
                    notificationResponseHandler,
                    mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to handle request made for getting the  notifications from the server if any
    AsyncHttpResponseHandler notificationResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("HomeTabFragment_Notification", "OnSuccess", response.toString());
                    getResponseNotification(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("HomeTabFragment_Notification", "OnFailure",
                        response);
        }

        @Override
        public void onFinish() {
        }
    };

    //handle response of notification from the server and insert in db
    private void getResponseNotification(String response) {
        try {
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>")
                    || response.contains("php")) {
                response2 = response.substring(
                        response.indexOf("response_code") - 2,
                        response.length());
            } else {
                response2 = response;
            }
            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                JSONObject jobj = new JSONObject(response2);
                Object mObjVersion = null;
                try {
                    mObjVersion = jobj.getJSONObject("version").get("clipart_version");
                    if (mObjVersion != null) {
                        downloadClipartsImages(String.valueOf(mObjVersion));
                    }
                } catch (Exception e) {
                    e.getMessage();
                }

                ArrayList<NotificationReponseDataBean> listDataNotifications = new ArrayList<NotificationReponseDataBean>();
                NotificationResponse get_Response = gson.fromJson(response2, NotificationResponse.class);

                if (get_Response.response_code
                        .equals(GlobalCommonValues.SUCCESS_CODE)) {
                    count = -1;
                    ((HomeScreenActivity) getActivity()).setAlarmTime(response);
                    //type 16 notification will not be added to database
                    for (NotificationReponseDataBean notificationReponseDataBean : get_Response.getData) {
                        if (!(notificationReponseDataBean.getType().equalsIgnoreCase("16"))) {
                            listDataNotifications.add(notificationReponseDataBean);
                        } else if ((notificationReponseDataBean.getType().equalsIgnoreCase("16"))) {
                            //system.out.println("IsImageNotification$$$--->"+DBQuery.getIsImageLockFromBBID(getActivity(),notificationReponseDataBean.getFrom_user_id()).equalsIgnoreCase("1"));
                            // To-Do - Update notifctn Api
                            //system.out.println("response------------>"+response.toString());
                            if (!DBQuery.getIsImageLockFromBBID(getActivity(), notificationReponseDataBean.getFrom_user_id()).equalsIgnoreCase("1")) {
                                getResponseNotificationImageNotification(notificationReponseDataBean);
                            }

                            if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
                                try {
                                    //notificationUpdateId=Integer.parseInt(listDataNotifications.get(mNotificationPosition).id);
                                    NotificationUpdateBean notificationUpdateBean = new NotificationUpdateBean(Integer.parseInt(notificationReponseDataBean.getId()), 2, "yes");
                                    updateNotification(notificationUpdateBean);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }

                        }/*else{
                           if (NetworkConnection.isNetworkAvailable(mActivityTabs))
                           {
                               try {
                                   //notificationUpdateId=Integer.parseInt(listDataNotifications.get(mNotificationPosition).id);
                                   NotificationUpdateBean notificationUpdateBean=new NotificationUpdateBean(Integer.parseInt(notificationReponseDataBean.getId()), 2,"yes");
                                   updateNotification(notificationUpdateBean);
                               } catch (Exception e) {
                                   e.getMessage();
                               }
                           }
                       }*/
                    }
                    //listDataNotifications = get_Response.getData;

                    DBQuery.insertNotification(mActivityTabs, listDataNotifications);

                    boolean isImageResponse = false;

                    tagloop:
                    for (int i = 0; i < listDataNotifications.size(); i++) {
                        if (listDataNotifications.get(i).getMessage().toLowerCase().contains("responded") || listDataNotifications.get(i).getType().equalsIgnoreCase("16")) {
                            isImageResponse = true;
                            break tagloop;
                        }
                    }
                    //call a method to update tile Image in case of Tnc User responded with the image
                    if (isImageResponse)
                        setResponseNotificationImage(listDataNotifications);
                    if (mActivityTabs instanceof HomeScreenActivity) {
                        count = ((HomeScreenActivity) mActivityTabs)
                                .getUnreadNotificationCount();
                        if (count > 0) {
                            HomeScreenActivity.tvUnreadNotificationCount
                                    .setVisibility(View.VISIBLE);
                            HomeScreenActivity.tvUnreadNotificationCount
                                    .setText(String.valueOf(count));
                        } else {
                            HomeScreenActivity.tvUnreadNotificationCount
                                    .setVisibility(View.GONE);
                        }
                    }
                } else if (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_1)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_2)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_3)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_5)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_6)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_601)) {

                    // To - Do
                    ((HomeScreenActivity) getActivity()).setAlarmTime(response);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (GlobalConfig_Methods.isValidString(response))
                ((HomeScreenActivity) getActivity()).setAlarmTime(response);
        }
    }


    //
    private void downloadClipartsImages(String version) {
        if (!version.equalsIgnoreCase("")
                && !(version.equalsIgnoreCase(saveState.getCLIPARTS_VERSION(mActivityTabs)))
                && saveState.getIS_FIRST_TIME_AFTER_REGISTRATION(mActivityTabs)) {
            saveState.setCLIPARTS_VERSION(mActivityTabs, version);

            //Run service to download all the clipart images in the cache
            GlobalConfig_Methods mDownloadCliparts = new
                    GlobalConfig_Methods();
            mDownloadCliparts.downloadClipArts(mActivityTabs);
        }
        saveState.setIS_FIRST_TIME_AFTER_REGISTRATION(mActivityTabs, false);
    }

    //Handle response from the server to update the Tile Image when got a response from the
    //Tnc User who uploaded the image on our request
    private void setResponseNotificationImage(
            ArrayList<NotificationReponseDataBean> listDataNotifications) {
        try {

            listBBContacts = new ArrayList<BBContactsBean>();
            // int BBID=-1;
            @SuppressWarnings("unused")
            int NotificationStatus = -1;
            String NotificationId = "";
            NotificationStatus = listDataNotifications.get(0).getStatus();
            NotificationId = listDataNotifications.get(0).getId(); // 1-request
            listDataNotifications.get(0).getMessage();
            listDataNotifications.get(0).getImage();
            String.valueOf(listDataNotifications.get(0).datetime);
            int matching_user_id = -1;
            if (Integer.parseInt(saveState.getBBID(mActivityTabs)) == Integer.parseInt(listDataNotifications.get(0).getTo_user_id())) {
                matching_user_id = Integer.parseInt(listDataNotifications.get(0).getFrom_user_id());
            } else if (Integer.parseInt(saveState.getBBID(mActivityTabs)) == Integer.parseInt(listDataNotifications.get(0).getFrom_user_id())) {
                matching_user_id = Integer.parseInt(listDataNotifications.get(0).getTo_user_id());
            }
            listBBContacts = DBQuery.checkBBContactExistence(mActivityTabs, matching_user_id);

            if ((listBBContacts != null && listBBContacts.size() > 0) &&
                    (listDataNotifications.get(0).getImage() != null && listDataNotifications.get(0).getImage().length() > 0)) {

                // update image in BBContacts table for the corresponding BBID
                DBQuery.updateBBContactsImage(mActivityTabs, listBBContacts.get(0).getBBID(),
                        listDataNotifications.get(0).getImage());
            }

            checkInternetConnectionNotificationResponseInsertImage(NotificationId);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // Check Internet Connection
    // will call method to update tile image
    private void checkInternetConnectionNotificationResponseInsertImage(
            String NotificationId) {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            NotificationBean notificationBean = new NotificationBean(
                    NotificationId);
            getNotificationStatus(notificationBean);
        } else {
        }
    }

    /**
     * request for getting image request status from the server
     *
     * @param notificationBean
     */
    private void getNotificationStatus(NotificationBean notificationBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(notificationBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.NOTIFICATION_CHECK_IMAGE_REQUEST_STATUS,
                    stringEntity, notificationStatusResponseHandler,
                    mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to handle response that we got from the server as an image response that is to be set on the user tile
    AsyncHttpResponseHandler notificationStatusResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("NotificationImageRequestStatus",
                            "OnSuccess", response.toString());
                    getResponseNotificationImageRequestStatus(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("NotificationImageRequestStatus", "OnFailure",
                        response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };


    //Handle response from the server to update the Tile Image when got a response from the
    //Tnc User who uploaded the image on our request
    private void getResponseNotificationImageRequestStatus(String response) {
        try {
            imageUrl = "";
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>")
                    || response.contains("php")) {
                try {
                    response2 = response.substring(
                            response.indexOf("response_code") - 2,
                            response.length());
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                response2 = response;
            }
            if (!TextUtils.isEmpty(response2)
                    && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                ImageRequestDialog dialogErrorMessage = new ImageRequestDialog();
                dialogErrorMessage.setCancelable(false);
                NotificationImageStatusResponse get_Response = gson.fromJson(
                        response2, NotificationImageStatusResponse.class);
                if (get_Response.response_code
                        .equals(GlobalCommonValues.SUCCESS_CODE)) {
                    if (get_Response.getData.type.equals("2") || get_Response.getData.type.equals("16")) {
                        imageUrl = "";
                        if (get_Response.getData.getImage() != null && !get_Response.getData.getImage().equalsIgnoreCase("null") &&
                                !get_Response.getData.getImage().equalsIgnoreCase("")) {
                            imageUrl = get_Response.getData.getImage();
                            new DownloadImageServiceClass(null).execute();

                        }
                    }
                    if (get_Response.getData.type.equals("3")) {
                        //						Toast.makeText(mActivityTabs, "Updated Number",1000).show();
                    }
                } else if (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_1)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_2)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_3)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_5)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_6)
                        || get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_601)) {
//                    dialogErrorMessage.newInstance("",
//                            ((HomeScreenActivity) mActivityTabs),
//                            get_Response.response_message, "", null);
//                    dialogErrorMessage.show(
//                            ((HomeScreenActivity) mActivityTabs)
//                                    .getSupportFragmentManager(), "test");
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    //Handle response from the server to update the Tile Image when got a response from the
    //Tnc User who uploaded the image on our request
    private void getResponseNotificationImageNotification(NotificationReponseDataBean notificationReponseDataBean) {
        try {
            imageUrl = notificationReponseDataBean.getImage();
            new DownloadImageServiceClass(notificationReponseDataBean).execute();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //	//async task to download user image from the url that we got from the server as an image response that is to be set on the user tile
    class DownloadImageServiceClass extends AsyncTask<Void, Void, Void> {

        NotificationReponseDataBean mNotificationReponseDataBean;

        DownloadImageServiceClass(NotificationReponseDataBean notificationReponseDataBean) {
            mNotificationReponseDataBean = notificationReponseDataBean;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                bitmapImage = GlobalConfig_Methods.loadBitmap(imageUrl);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmapImage.compress(CompressFormat.PNG, 100 /* ignored for PNG */, blob);
                arrayImage = blob.toByteArray();
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            imageUrl = null;
            try {
                if (arrayImage != null && listBBContacts != null && !listBBContacts.isEmpty()) {
                    DBQuery.updateBBImageFromResponse(mActivityTabs, arrayImage, listBBContacts.get(0).getPhoneNumber());
                    /*//					setTilesAdapter();*/
                    new DisplayTilesASynTask().execute();
                } else if (arrayImage != null && mNotificationReponseDataBean != null && !mNotificationReponseDataBean.getFrom_user_id().isEmpty()) {
                    DBQuery.updateBBImageFromNotification(mActivityTabs, arrayImage, mNotificationReponseDataBean.getFrom_user_id());
                    new DisplayTilesASynTask().execute();
                    if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
                        try {
                            //notificationUpdateId=Integer.parseInt(listDataNotifications.get(mNotificationPosition).id);
                            NotificationUpdateBean notificationUpdateBean = new NotificationUpdateBean(Integer.parseInt(mNotificationReponseDataBean.getId()), 2, "yes");
                            updateNotification(notificationUpdateBean);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }

                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private void updateNotification(NotificationUpdateBean notificationUpdateBean) {
        try {
            gson = new Gson();
            String stingGson = gson.toJson(notificationUpdateBean);
            cz.msebera.android.httpclient.entity.StringEntity stringEntity;
            stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
            MyHttpConnection.postWithJsonEntityHeader(mActivityTabs,
                    GlobalCommonValues.UPDATE_NOTIFICATION_STATUS,
                    stringEntity, notificationUpdateResponseHandler,
                    mActivityTabs.getString(R.string.private_key), saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to handle response for the request being made to update notification status
    AsyncHttpResponseHandler notificationUpdateResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            // Initiated the request
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            //			testCopy();
            try {
                if (response != null) {
                    Logs.writeLog("NotificationUpdate", "OnSuccess", response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("NotificationUpdate", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    /**
     * interface to set unread notifiaction count if any
     */

    INotifyGalleryDialog iNotifyObject = new INotifyGalleryDialog() {
        @Override
        public void yes() {

            count = -1;
            try {
                count = ((HomeScreenActivity) mActivityTabs)
                        .getUnreadNotificationCount();
                if (count > 0) {
                    HomeScreenActivity.tvUnreadNotificationCount
                            .setVisibility(View.VISIBLE);
                    HomeScreenActivity.tvUnreadNotificationCount.setText(String
                            .valueOf(count));
                } else {
                    HomeScreenActivity.tvUnreadNotificationCount
                            .setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        @Override
        public void no() {

        }
    };

    /**
     * interface to handle tile edit option
     */
    INotifyActionUpdate iNotifyTileEdit = new INotifyActionUpdate() {

        @Override
        public void actionFirst() {
            //			Toast.makeText(getActivity(), "1",1000).show();
            //In Case Of Edit Tile
            MainBaseActivity.objTileEdit = new ContactTilesBean();
            TileContactDetailsFragment objTileContactDetailsFragment;
            if (isLongClicked) {
                MainBaseActivity.selectedPrefixCodeForTileDetails = "";
                MainBaseActivity.selectedCountryCodeForTileDetails = "";
                MainBaseActivity.isIsdCodeFlagChecked = false;
                MainBaseActivity.contactNameForTile = "";
                MainBaseActivity.contactNumberForTile = "";

                MainBaseActivity.objTileEdit = listTiles.get(TilePosition);
                if (mActivity instanceof MainBaseActivity) {
                    objTileContactDetailsFragment = new TileContactDetailsFragment();
                    objTileContactDetailsFragment.newInstance(((MainBaseActivity) mActivity), MainBaseActivity.objTileEdit.isIsMobile(), null, MainBaseActivity.objTileEdit.getPrefix() + MainBaseActivity.objTileEdit.getCountryCode() + MainBaseActivity.objTileEdit.getPhoneNumber(), MainBaseActivity.objTileEdit.getName().trim(), MainBaseActivity.objTileEdit.getPrefix() + MainBaseActivity.objTileEdit.getCountryCode() + MainBaseActivity.objTileEdit.getPhoneNumber(),
                            iActionIsFromHomeScreen, true);
                    ((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
                } else if (mActivity instanceof HomeScreenActivity) {
                    objTileContactDetailsFragment = new TileContactDetailsFragment();
                    objTileContactDetailsFragment.newInstance(((HomeScreenActivity) mActivity), MainBaseActivity.objTileEdit.isIsMobile(),
                            null,
                            MainBaseActivity.objTileEdit.getPrefix() + MainBaseActivity.objTileEdit.getCountryCode() + MainBaseActivity.objTileEdit.getPhoneNumber(), MainBaseActivity.objTileEdit.getName().trim(), MainBaseActivity.objTileEdit.getPrefix() + MainBaseActivity.objTileEdit.getCountryCode() + MainBaseActivity.objTileEdit.getPhoneNumber(),
                            iActionIsFromHomeScreen, true);
                    ((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
                }
                gridview.rearrange();
                TilePosition = -1;
            }
        }

        @Override
        public void actionSecond() {
            //			Toast.makeText(getActivity(), "2",1000).show();
            //In case of delete Tile
            if (isLongClicked) {
                MessageDeleteConfirmation dialogConfirmation = new MessageDeleteConfirmation();
                dialogConfirmation.newInstance("", mActivity, "Are you sure you want to delete this chat button", "", iNotifyDelete);
                dialogConfirmation.setCancelable(false);
                dialogConfirmation.show(getChildFragmentManager(), "test");
            }
        }

        @Override
        public void actionThird() {
            //			Toast.makeText(getActivity(), "3",1000).show();
            //In case of Tile dropped on Emergency Call Button

            ContactTilesBean mObjTileEdit = new ContactTilesBean();
            listTiles.get(TilePosition);


            mObjTileEdit.setName(listTiles.get(TilePosition).getName());
            mObjTileEdit.setPrefix(listTiles.get(TilePosition).getPrefix());
            mObjTileEdit.setCountryCode(listTiles.get(TilePosition).getCountryCode());
            mObjTileEdit.setPhoneNumber(listTiles.get(TilePosition).getPhoneNumber());

            mObjTileEdit.setImage(listTiles.get(TilePosition).getImage());
            mObjTileEdit.setIsImagePending(listTiles.get(TilePosition).getIsImagePending());
            mObjTileEdit.setTilePosition(listTiles.get(TilePosition).getTilePosition());
            mObjTileEdit.setImageLocked(listTiles.get(TilePosition).isImageLocked());
            boolean mIsEmergency = listTiles.get(TilePosition).isIsEmergency();

            mObjTileEdit.setIsEmergency(!mIsEmergency);

            DBQuery.updateTile(mActivity, mObjTileEdit);
            gridview.rearrange();
            try {
                ((ContactTilesBean) adapterTiles.getItem(TilePosition)).setIsEmergency(!mIsEmergency);
            } catch (Exception e) {
                //system.out.println(e.getMessage());
            }
            adapterTiles.notifyDataSetChanged();
            TilePosition = -1;
        }

        @Override
        public void actionFourth() {
            int mPageIndicator = 0;
            //			Toast.makeText(getActivity(), "4",1000).show();
            if (toggleCallMessage.isChecked()) {
                // in case of message side
                //				if((mHeightScreen>1 && mWidthScreen>1) &&
                //						(mHeightScreen<=854 && mWidthScreen<=480)){

				/*if((mHeightScreen>1 && mWidthScreen>1) && 
						(mHeightScreen<=854 && mWidthScreen<=480)){
					if (listTilesBBUsers.size() % 2 != 0) {
						// int sizeOne=listBBContacts.size()%6;
						int sizeTwo = listTilesBBUsers.size() / 2 + 1;
						mPageIndicator = sizeTwo;
					} else if (listTilesBBUsers.size() % 2 == 0) {
						mPageIndicator = listTilesBBUsers.size() / 2;
					}
				}else{
					if (listTilesBBUsers.size() % 6 != 0) {
						// int sizeOne=listBBContacts.size()%6;
						int sizeTwo = listTilesBBUsers.size() / 6 + 1;
						mPageIndicator = sizeTwo;
					} else if (listTilesBBUsers.size() % 6 == 0) {
						mPageIndicator = listTilesBBUsers.size() / 6;
					}	
				}*/

                displayIndicators("message", gridview.getCurrentItem());
                //				}
            } else {
                // in case of call side
                //				if((mHeightScreen>1 && mWidthScreen>1) &&
                //						(mHeightScreen<=854 && mWidthScreen<=480)){

				/*if((mHeightScreen>1 && mWidthScreen>1) && 
						(mHeightScreen<=854 && mWidthScreen<=480)){
					if (listTiles.size() % 2 != 0) {
						// int sizeOne=listTiles.size()%6;
						int sizeTwo = listTiles.size() / 2 + 1;
						mPageIndicator = sizeTwo;

					} else if (listTiles.size() % 2 == 0) {
						mPageIndicator = listTiles.size() / 2;
					}
				}else{
					if (listTiles.size() % 6 != 0) {
						// int sizeOne=listTiles.size()%6;
						int sizeTwo = listTiles.size() / 6 + 1;
						mPageIndicator = sizeTwo;

					} else if (listTiles.size() % 6 == 0) {
						mPageIndicator = listTiles.size() / 6;
					}
				}*/
                displayIndicators("call", gridview.getCurrentItem());
                //				}
            }

        }
    };

    /**
     * interface to handle tile delete option
     */
    INotifyGalleryDialog iNotifyDelete = new INotifyGalleryDialog() {

        @SuppressWarnings("unused")
        @Override
        public void yes() {
            //	In case of user selected 'yes' to delete tile selected upon confirmation
            int count = -1;
            if (objContactTileDelete != null) {
                int _tilePosition = Integer.parseInt(objContactTileDelete.getTilePosition());
                count = DBQuery.deleteTile(mActivity, objContactTileDelete.getPrefix(), objContactTileDelete.getPhoneNumber(), Integer.parseInt(objContactTileDelete.getTilePosition()));
                if (count > 0) {
                    TileDeleteSuccessDialog dialogTiledeleteSuccess = new TileDeleteSuccessDialog();
                    if (mActivity instanceof MainBaseActivity) {
                        dialogTiledeleteSuccess.newInstance("", ((MainBaseActivity) mActivity), "Chat Button Deleted Successfully", "", false);
                    } else if (mActivity instanceof HomeScreenActivity) {
                        dialogTiledeleteSuccess.newInstance("", ((HomeScreenActivity) mActivity), "Chat Button Deleted Successfully", "", false);
                    }
                    dialogTiledeleteSuccess.setCancelable(false);
                    dialogTiledeleteSuccess.show(getFragmentManager(), "test");

                    listTiles.remove(TilePosition);
                    adapterTiles.notifyDataSetChanged();
                    TilePosition = -1;

                    //Update Circle Page Indicators at the bottom
                    listsize = -1;
                    if (toggleCallMessage.isChecked()) {
                        // Commented as a part of New Functionality
                        if ((mHeightScreen > 1 && mWidthScreen > 1) &&
                                (mHeightScreen <= 854 && mWidthScreen <= 480)) {

                            // In case of toggle
                            // is on Message
                            // Side
                            if (listTiles.size() > 2) {
                                //displayIndicators("call", 0);

                                if (listTiles.size() % 2 != 0) {
                                    // int sizeOne=listTiles.size()%6;
                                    int sizeTwo = listTiles.size() / 2 + 1;
                                    listsize = sizeTwo;

                                } else if (listTiles.size() % 2 == 0) {
                                    listsize = listTiles.size() / 2;
                                }
                                if (listsize > 1)
                                    displayIndicators("message", listsize - 1);

                            } else {
                                // Commented as a part of New Functionality
                                //llIndicatorHolder.removeAllViews();
                            }


                        } else {
                            // In case of toggle
                            // is on Message
                            // Side
                            if (listTiles.size() > 6) {
                                //displayIndicators("call", 0);

                                if (listTiles.size() % 6 != 0) {
                                    // int sizeOne=listTiles.size()%6;
                                    int sizeTwo = listTiles.size() / 6 + 1;
                                    listsize = sizeTwo;

                                } else if (listTiles.size() % 6 == 0) {
                                    listsize = listTiles.size() / 6;
                                }
                                if (listsize > 1)
                                    displayIndicators("message", listsize - 1);

                            } else {
                                // Commented as a part of New Functionality
                                //llIndicatorHolder.removeAllViews();
                            }
                        }
                    } else {
                        // In case of toggle
                        // is on Call
                        // Side
                        // Commented as a part of New Functionality
                        if ((mHeightScreen > 1 && mWidthScreen > 1) &&
                                (mHeightScreen <= 854 && mWidthScreen <= 480)) {
                            // In case of toggle is on Call Side
                            if (listTiles.size() > 2) {
                                //displayIndicators("call", 0);

                                if (listTiles.size() % 2 != 0) {
                                    // int sizeOne=listTiles.size()%6;
                                    int sizeTwo = listTiles.size() / 2 + 1;
                                    listsize = sizeTwo;

                                } else if (listTiles.size() % 2 == 0) {
                                    listsize = listTiles.size() / 2;
                                }
                                if (listsize > 1)
                                    displayIndicators("call", listsize - 1);

                            } else {
                                // Commented as a part of New Functionality
                                //llIndicatorHolder.removeAllViews();
                            }
                        } else {

                            // In case of toggle is on Call Side
                            if (listTiles.size() > 6) {
                                //displayIndicators("call", 0);

                                if (listTiles.size() % 6 != 0) {
                                    // int sizeOne=listTiles.size()%6;
                                    int sizeTwo = listTiles.size() / 6 + 1;
                                    listsize = sizeTwo;

                                } else if (listTiles.size() % 6 == 0) {
                                    listsize = listTiles.size() / 6;
                                }
                                if (listsize > 1)
                                    displayIndicators("call", listsize - 1);

                            } else {
                                // Commented as a part of New Functionality
                                // llIndicatorHolder.removeAllViews();
                            }
                        }
                    }

                    // Commented as a part of New phase Implementation - Start
                    /*if((mHeightScreen>1 && mWidthScreen>1) &&
                            (mHeightScreen<=854 && mWidthScreen<=480)){
                            // For Smaller Resolution devices
                        if(listsize>0 && listsize<10)
                        {
                            if(listTiles.size() == -1 || listTiles.size()<=2){
                                gridview.setCurrentItem(0);
                                // Commented as a part of New phase Implementation
                                //llIndicatorHolder.removeAllViews();
                            }
                            else{
                                gridview.setCurrentItem(listsize-1);
                            }
                        }else {
                            if(listTiles.size() == -1 || listTiles.size()<=2){
                                gridview.setCurrentItem(0);
                            }
                            // Commented as a part of New phase Implementation
//                            llIndicatorHolder.removeAllViews();
                        }
                    }else{ // For Greater Resolution devices
                        if(listsize>0 && listsize<10)
                        {
                            if(listTiles.size() == -1 || listTiles.size()<=6){
                                gridview.setCurrentItem(0);
                                // Commented as a part of New phase Implementation
//                                llIndicatorHolder.removeAllViews();
                            }
                            else{
                                gridview.setCurrentItem(listsize-1);
                            }
                        }else {
                            if(listTiles.size() == -1 || listTiles.size()<=6){
                                gridview.setCurrentItem(0);
                            }
                            // Commented as a part of New phase Implementation
//                            llIndicatorHolder.removeAllViews();
                        }
                    }*/
                    // Commented as a part of New phase Implementation - End


                    if ((mHeightScreen > 1 && mWidthScreen > 1) &&
                            (mHeightScreen <= 854 && mWidthScreen <= 480)) {
                        // For Smaller Resolution devices
                        if ((listsize > 0) && (listsize <= gridview.getPageCount())) {
                            if (listTiles.size() == -1 || listTiles.size() <= 2) {
                                gridview.setCurrentItem(0);
                            } else {
                                gridview.setCurrentItem(listsize - 1);
                            }
                        } else {
                            if (listTiles.size() == -1 || listTiles.size() <= 2) {
                                gridview.setCurrentItem(0);
                            }
                        }
                    } else { // For Greater Resolution devices
                        if ((listsize > 0) && (listsize <= gridview.getPageCount())) {
                            if (listTiles.size() == -1 || listTiles.size() <= 6) {
                                gridview.setCurrentItem(0);
                            } else {
                                gridview.setCurrentItem(listsize - 1);
                            }
                        } else {
                            if (listTiles.size() == -1 || listTiles.size() <= 6) {
                                gridview.setCurrentItem(0);
                            }
                        }
                    }

                    if ((listTiles != null) && (listTiles.size() == 0)) {
                        mTextPageNumber.setVisibility(View.GONE);
                    }
                    updatePageNumberAndSwipeStatus();

					/*if (toggleCallMessage.isChecked()) {
						// In case of toggle
						// is on Message
						// Side
						if (listTilesBBUsers.size() > 6) {
							//displayIndicators("call", 0);

							if (listTilesBBUsers.size() % 6 != 0) {
								// int sizeOne=listTiles.size()%6;
								int sizeTwo = listTilesBBUsers.size() / 6 + 1;
								listsize = sizeTwo;

							} else if (listTilesBBUsers.size() % 6 == 0) {
								listsize = listTilesBBUsers.size() / 6;
							}
							displayIndicators("message",listsize);

						} else {
							llIndicatorHolder.removeAllViews();
						}
					}
					else{
						// In case of toggle is on Call Side
						if (listTiles.size() > 6) {
							//displayIndicators("call", 0);

							if (listTiles.size() % 6 != 0) {
								// int sizeOne=listTiles.size()%6;
								int sizeTwo = listTiles.size() / 6 + 1;
								listsize = sizeTwo;

							} else if (listTiles.size() % 6 == 0) {
								listsize = listTiles.size() / 6;
							}
							displayIndicators("call",listsize);

						} else {
							llIndicatorHolder.removeAllViews();
						}
					}

					if(listsize>0 && listsize<10)
						gridview.setCurrentItem(_tilePosition);*/

                } else {
                    ImageRequestDialog dialogTileDelete = new ImageRequestDialog();
                    dialogTileDelete.newInstance("", mActivity, "Chat Button delete error occured", "", null);
                    dialogTileDelete.setCancelable(false);
                    dialogTileDelete.show(getChildFragmentManager(), "test");
                }
            }
        }

        @Override
        public void no() {
            gridview.rearrange();
        }
    };

    INotifyAction iActionIsFromHomeScreen = new INotifyAction() {

        @Override
        public void setAction(String action) {
            if (action.equals("refresh")) {
                new DisplayTilesASynTask().execute();
            }
        }
    };

    public void onPause() {
        super.onPause();
        try {
            HomeScreenActivity.btnNotification.setClickable(false);
            HomeScreenActivity.btnNotification.setEnabled(false);
            HomeScreenActivity.btnAddTile.setClickable(false);
            HomeScreenActivity.btnAddTile.setEnabled(false);
            HomeScreenActivity.btnCallEmergency.setClickable(false);
            HomeScreenActivity.btnCallEmergency.setEnabled(false);
            HomeScreenActivity.btnDisable.setClickable(false);
            HomeScreenActivity.btnDisable.setEnabled(false);
            HomeScreenActivity.btnHome.setClickable(false);
            HomeScreenActivity.btnHome.setEnabled(false);
            getActivity().unregisterReceiver(receiver);
            disableRefreshing();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    ;

    @Override
    public void onResume() {
        super.onResume();
        GlobalConfig_Methods.setHomeGridViewColumns(getActivity());
        HomeScreenActivity.btnNotification.setClickable(true);
        HomeScreenActivity.btnNotification.setEnabled(true);
        HomeScreenActivity.btnHome.setClickable(true);
        HomeScreenActivity.btnHome.setEnabled(true);
        HomeScreenActivity.btnCallEmergency.setClickable(true);
        HomeScreenActivity.btnCallEmergency.setEnabled(true);
        HomeScreenActivity.btnDisable.setClickable(true);
        HomeScreenActivity.btnDisable.setEnabled(true);
        MainBaseActivity.isBackButtonToDisplay = false;
        MainBaseActivity.isImageRequested = false;

        // Call Method to set icon on home screen bottom as per the current profile
        try {
            setProfileButtonIcon(GlobalConfig_Methods.getProfileRingerMode(getActivity()));
        } catch (Exception e) {
            e.getMessage();
        }

        if (toggleCallMessage.isChecked()) {  // On Message Side
            HomeScreenActivity.btnAddTile.setClickable(false);
            HomeScreenActivity.btnAddTile.setEnabled(false);
        } else {  // On Call Side
            HomeScreenActivity.btnAddTile.setClickable(true);
            HomeScreenActivity.btnAddTile.setEnabled(true);
        }

        IntentFilter filter = new IntentFilter("com.bigbutton.homereceiver");
        getActivity().registerReceiver(receiver, filter);
        HomeScreenActivity.btnEdit.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                HomeScreenActivity.btnEdit.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                HomeScreenActivity.btnEdit.getLocationOnScreen(locations);
                X_Edit = locations[0];
                Y_Edit = locations[1];
                Top = HomeScreenActivity.btnEdit.getTop();
                Right = HomeScreenActivity.btnEdit.getRight();
                Left = HomeScreenActivity.btnEdit.getLeft();
                Bottom = HomeScreenActivity.btnEdit.getBottom();
                WIDTH = HomeScreenActivity.btnEdit.getWidth();
                HEIGHT = HomeScreenActivity.btnEdit.getHeight();
                rectEditButton = new Rect(Left, Top, Right, Bottom);
            }
        });

        HomeScreenActivity.btnDelete.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                HomeScreenActivity.btnDelete.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                HomeScreenActivity.btnDelete.getLocationOnScreen(locations);
                X_Delete = locations[0];
                Y_Delete = locations[1];
                TopDelete = HomeScreenActivity.btnDelete.getTop();
                RightDelete = HomeScreenActivity.btnDelete.getRight();
                LeftDelete = HomeScreenActivity.btnDelete.getLeft();
                BottomDelete = HomeScreenActivity.btnDelete.getBottom();
                WIDTH_DELETE = HomeScreenActivity.btnDelete.getWidth();
                HEIGHT_DELETE = HomeScreenActivity.btnDelete.getHeight();
                rectDeleteButton = new Rect(LeftDelete, TopDelete, RightDelete, BottomDelete);
                //				Toast.makeText(mActivityTabs, "X_Add--"+X_Remove+"  Y_Add--"+Y_Remove, Toast.LENGTH_LONG).show();
            }
        });

        HomeScreenActivity.btnCallEmergency.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                HomeScreenActivity.btnDelete.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                HomeScreenActivity.btnDelete.getLocationOnScreen(locations);
                X_Call = locations[0];
                Y_Call = locations[1];
                TopCall = HomeScreenActivity.btnDelete.getTop();
                RightCall = HomeScreenActivity.btnDelete.getRight();
                LeftCall = HomeScreenActivity.btnDelete.getLeft();
                BottomCall = HomeScreenActivity.btnDelete.getBottom();
                WIDTH_CALL = HomeScreenActivity.btnDelete.getWidth();
                HEIGHT_CALL = HomeScreenActivity.btnDelete.getHeight();
                rectCallButton = new Rect(LeftDelete, TopDelete, RightDelete, BottomDelete);
                //				Toast.makeText(mActivityTabs, "X_Add--"+X_Remove+"  Y_Add--"+Y_Remove, Toast.LENGTH_LONG).show();
            }
        });

        if (saveState.isRegistered(mActivityTabs)) {
            // toggleCallMessage.setClickable(true);
            toggleCallMessage.setEnabled(true);
            HomeScreenActivity.btnDisable.setVisibility(View.GONE);
        } else {
            // toggleCallMessage.setClickable(true);
            toggleCallMessage.setEnabled(false);
            HomeScreenActivity.btnDisable.setVisibility(View.VISIBLE);
        }
        try {
            enableRefreshing();
        } catch (Exception e) {
            e.getMessage();
        }

        if (mActivityTabs instanceof HomeScreenActivity)
            ((HomeScreenActivity) mActivityTabs).setUnreadMessageCount();
        if (HomeScreenActivity.isFirsTimeHomeTabOpen
                && saveState.isRegistered(mActivityTabs)
                && !saveState.getPublicKey(mActivityTabs).trim().equals("")) {
            checkInternetConnectionMessageList();
        }
        if (saveState.isRegistered(mActivityTabs)
                && !saveState.getPublicKey(mActivityTabs).trim().equals("")) {
            checkInternetConnectionNotification();
        }
    }

    public void refreshTileAdapter() {
        //		if(null!=adapterTiles){
        //			adapterTiles.notifyDataSetChanged();
        //			displayIndicators(tilesType, gridview.getCurrentItem());
        //		}
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            saveState.setRefrehContactList(getActivity(), false);
            saveState.setIS_FROM_HOME(mActivityTabs, false);

            Fragment _mFragment = ((HomeScreenActivity) getActivity()).fragmentManager.getFragments().get(((HomeScreenActivity) getActivity()).fragmentManager.getBackStackEntryCount() - 1);

            if (_mFragment instanceof SearchChatButtonFragment) {

                // dismiss rogress dialog on search chat button screen list
                if (((SearchChatButtonFragment) _mFragment).dialogProgress != null && ((SearchChatButtonFragment) _mFragment).dialogProgress.isShowing()) {
                    ((SearchChatButtonFragment) _mFragment).dialogProgress.dismiss();
                }
                // loada tiles data again
                ((SearchChatButtonFragment) _mFragment).loadTilesData();
                return;
            }

            if (dialogProgress != null && dialogProgress.isShowing())
                dialogProgress.dismiss();
            displayTiles(toggleCallMessage.isChecked());
        }
    }

    /**
     * Method to run timer to call web service to get unread message/notification count
     */
    private void enableRefreshing() {
        try {
            if (timer != null) {
                if (timerTask != null) {
                    timer.schedule(timerTask, 0, 6000);
                } else {
                    timerTask.cancel();
                    timerTask = null;
                    timer.cancel();
                    timer = null;
                    initTimerTask();
                    timer.schedule(timerTask, 0, 6000);
                }

            } else {
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                initTimerTask();
                timer.schedule(timerTask, 0, 6000);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to disable timer to call web service to get unread message/notification count
     */
    private void disableRefreshing() {
        try {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to initialize timer to call web service to get unread message/notification count
     */
    public void initTimerTask() {
        try {
            if (timer == null)
                timer = new Timer();
            if (timerTask == null)
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (!webservices_oncall) {
                            mActivityTabs.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Message msg = Message.obtain(
                                            messagerefresh_handler, refresh,
                                            null);
                                    messagerefresh_handler.dispatchMessage(msg);
                                }
                            });

                        }
                    }
                };
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * handler for the timer to call web service to get unread message/notification count
     */
    @SuppressLint("HandlerLeak")
    final Handler messagerefresh_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {

                switch (msg.what) {
                    case refresh:
                        checkInternetConnectionNotification();
                        break;
                    case stoprefresh:
                        break;
                    case donothing:
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        HomeScreenActivity.isFirsTimeHomeTabOpen = true;
        HomeTabFragment.isLongClicked = false;
    }

    /**
     * check internet availability
     */
    private void checkInternetConnectionNotification() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            notificationID = DBQuery.getNotificationsMaxCount(mActivityTabs);
            if (notificationID == -1) {
                notificationID = 0;
            }
            NotificationBean notificationBean = new NotificationBean(
                    String.valueOf(notificationID));
            getNotificationList(notificationBean);
        } else {
        }
    }

    /**
     * check internet availability
     */
    private void checkInternetConnectionMessageList() {
        if (NetworkConnection.isNetworkAvailable(mActivityTabs)) {
            getMessageList();
        } else {
        }
    }

    /**
     * Method to send request to get the unread message list
     */
    private void getMessageList() {
        try {
            gson = new Gson();
            // String stingGson = gson.toJson(messageListBean);
            // StringEntity stringEntity;
            // stringEntity=new StringEntity(stingGson);
            MyHttpConnection.postHeaderWithoutJsonEntity(mActivityTabs,
                    GlobalCommonValues.GET_ALL_MESSAGE,
                    messagesAllResponseHandler,
                    mActivityTabs.getString(R.string.private_key),
                    saveState.getPublicKey(mActivityTabs));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Async task to get all messages sent from the sender
     */
    AsyncHttpResponseHandler messagesAllResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("HomeTabFragment_MessageList", "OnSuccess",
                            response.toString());
                    getResponseAllMessages(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("HomeTabFragment_MessageList", "OnFailure",
                        response);
        }

        @Override
        public void onFinish() {
        }
    };

    /**
     * Method to handle response we got from the server for the unread messages received by us
     *
     * @param response
     */
    private void getResponseAllMessages(String response) {
        try {
            if (!TextUtils.isEmpty(response)
                    && GlobalConfig_Methods.isJsonString(response)) {
                gson = new Gson();
                GetAllMessageResponseBean get_Response = gson.fromJson(
                        response, GetAllMessageResponseBean.class);
                if (get_Response.response_code
                        .equals(GlobalCommonValues.SUCCESS_CODE)) {
                    ArrayList<SendMessageReponseDataBean> data = get_Response
                            .getData();
                    ArrayList<SendMessageReponseDataBean> listMessageContacts = new ArrayList<SendMessageReponseDataBean>();

                    if ((data != null && data.isEmpty()) || data == null) {
                        SendMessageReponseDataBean dataBean = new SendMessageReponseDataBean();
                        dataBean.setMessage("No Message Found");
                        listMessageContacts.add(dataBean);
                    } else if (data != null && data.size() > 0) {
                        listMessageContacts = data;
                        ArrayList<SendMessageReponseDataBean> listMessageDetails = new ArrayList<SendMessageReponseDataBean>();
                        SendMessageReponseDataBean objResponseDataBean = null;
                        for (int i = 0; i < listMessageContacts.size(); i++) {
                            objResponseDataBean = new SendMessageReponseDataBean();
                            objResponseDataBean.setMessage_id(data.get(i)
                                    .getMessage_id());
                            objResponseDataBean.setFrom_user_id(data.get(i)
                                    .getFrom_user_id());
                            objResponseDataBean.setFrom_user_phone(data.get(i)
                                    .getFrom_user_phone());
                            objResponseDataBean.setTo_user_id(data.get(i)
                                    .getTo_user_id());
                            objResponseDataBean.setMessage(data.get(i)
                                    .getMessage());
                            objResponseDataBean.setStatus(data.get(i)
                                    .getStatus());
                            objResponseDataBean.setDatatime(data.get(i)
                                    .getDatatime());
                            objResponseDataBean.setName(data.get(i)
                                    .getDatatime());
                            listMessageDetails.add(objResponseDataBean);
                        }
                        DBQuery.insertMessage(mActivityTabs, listMessageDetails);
                        ((HomeScreenActivity) mActivityTabs)
                                .setUnreadMessageCount();
                        refreshTileAdapter();
                    }
                } else if ((get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE))
                        || (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_1))
                        || (get_Response.response_code
                        .equals(GlobalCommonValues.FAILURE_CODE_2))) {
                }
            } else {
                /*
                 * ShowDialog.alert(mActivityTabs, "",
                 * getResources().getString(R.string.improper_response));
                 */
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to get the registered user bbid
     */
    private void getBBID() {
        try {
            gson = new Gson();
            MyHttpConnection.postHeaderWithoutJsonEntity(mActivity,
                    GlobalCommonValues.GET_BBID, getBBIDResponseHandler,
                    mActivity.getString(R.string.private_key),
                    saveState.getPublicKey(mActivity));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to handle the request mage to get the bbid
    AsyncHttpResponseHandler getBBIDResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            // Successfully got a response
            try {
                if (response != null) {
                    Logs.writeLog("getBBIDResponseHandler", "OnSuccess",
                            response.toString());
                    getResponseBBID(response.toString());
                }
            } catch (JsonSyntaxException jsone) {
                jsone.toString();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
            // Response failed :(
            if (response != null)
                Logs.writeLog("getBBIDResponseHandler", "OnFailure", response);
        }

        @Override
        public void onFinish() {
            // Completed the request (either success or failure)
        }
    };

    /**
     * handle response for the request being made for getting BBID
     * <p>
     * {
     * "user_id": "6",
     * "email": "",
     * "image": "",
     * "is_default_image": "no",
     * "name": "ankur%20Paliwal",
     * "country_code": "91",
     * "country_name": "India",
     * "number": "9571617403",
     * "is_activate": 1,
     * "is_email_verified": 0,
     * "is_verified": "verified"
     * }
     *
     * @param response
     */
    private void getResponseBBID(String response) {
        try {
            String response2 = "";
            if (response.contains("</div>") || response.contains("<h4>") || response.contains("php")) {
                response2 = response.substring(response.indexOf("user_id") - 2, response.length());
            } else {
                response2 = response;
            }

            if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
                gson = new Gson();
                GetBBIDResponseBeanData get_Response = gson.fromJson(response2, GetBBIDResponseBeanData.class);
                if (get_Response.is_activate == 0) {
                    saveState.setRegistered(getActivity(), false);
                    saveState.setPublicKey(getActivity(), "");
                    GlobalCommonValues.listBBContacts = new ArrayList<BBContactsBean>();

                    // Update Tiles Table value on de-registration
                    DBQuery.updateTile(getActivity(), null);

                    //DBQuery.deleteTable("Tiles", "", null, getActivity());
                    DBQuery.deleteTable("BBContacts", "", null, getActivity());
                    DBQuery.deleteTable("Messages", "", null, getActivity());
                    DBQuery.deleteTable("Notifications", "", null,
                            getActivity());
                    if (toggleCallMessage.isChecked()) {
                        displayTiles(true);
                    } else if (!toggleCallMessage.isChecked()) {
                        displayTiles(false);
                    }
                    setUnreadNotificationCount();
                    if (mActivityTabs instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) mActivityTabs)
                                .setUnreadMessageCount();
                    }

                    // needs to alert the user as deactivated
                    saveState.setIS_DEACTIVATED(getActivity(), false);

                    HomeScreenActivity.flInformationButton.setVisibility(View.GONE);
                    HomeScreenActivity.btnHome.setVisibility(View.GONE);
                    toggleCallMessage.setClickable(false);
                    toggleCallMessage.setEnabled(false);
                    HomeScreenActivity.btnDisable.setVisibility(View.VISIBLE);

                    //if tiles exists then update tiles value and reset them as a non tnc-user
                    if (DBQuery.getAllTiles(mActivityTabs).size() > 0) {
                        DBQuery.updateTileResetTnCUser(mActivityTabs);
                    }
                } else if (get_Response.is_activate == 1) {
                    saveState.setRegistered(getActivity(), true);
                    saveState.setCountryCode(mActivityTabs, get_Response.country_code);
                    saveState.setBBID(mActivityTabs, get_Response.user_id);

                    //Set Premium User value
                    if ((get_Response.getIs_premium_user() != null) && (get_Response.getIs_premium_user().equalsIgnoreCase("yes"))) {
                        saveState.setISPREMIUMUSER(getActivity(), true);
                    } else {
                        saveState.setISPREMIUMUSER(getActivity(), false);
                    }

                    String countryName = get_Response.country_name.toUpperCase();
                    if (countryName.contains("COTE")) {
                        saveState.setCountryname(mActivityTabs, "COTE D'IVOIRE");
                    } else {
                        saveState.setCountryname(mActivityTabs, get_Response.country_name.toUpperCase());
                    }
                    saveState.setUserName(mActivityTabs, Uri.decode(get_Response.name));
                    saveState.setUserMailID(mActivityTabs, Uri.decode(get_Response.email));
                    String emergency = DBQuery.getEmergency(mActivityTabs, saveState.getCountryName(mActivityTabs));
                    saveState.setUserPhoneNumber(mActivityTabs, get_Response.number);
                    if (saveState.getEmergency(mActivityTabs).trim().equals("")) {
                        //String emergency = DBQuery.getEmergency(mActivityTabs,saveState.getCountryName(mActivityTabs));
                        saveState.setEmergency(mActivityTabs, emergency);
                        saveState.setIS_RecentRegistration(mActivityTabs, false);
                    } else if (!saveState.getEmergency(mActivityTabs).trim().equals("")) {
                        if (saveState.IS_RecentRegistration(getActivity())) {
                            if (saveState.getUPDATE_EMERGENCY(mActivityTabs) || (!saveState.getEmergency(mActivityTabs).trim().equals(emergency.trim()))) {
                                EmergencyNumberUpdateConfirmationDialog dialog = new EmergencyNumberUpdateConfirmationDialog();
                                dialog.setCancelable(false);
                                dialog.newInstance("", mActivityTabs, "Trying to configure Emergency button to country's default " + emergency + ". " + "Do you like to continue?", "", iNotifyUpdateEmergency);
                                dialog.show(getChildFragmentManager(), "test");
                                saveState.setIS_RecentRegistration(mActivityTabs, false);
                                saveState.setUPDATE_EMERGENCY(mActivityTabs, false);
                                mEmergency = emergency;
                            }
                        }
                    }

                    String iddCode = DBQuery.getIDDCodeDB(mActivityTabs, saveState.getCountryCode(mActivityTabs));
                    saveState.setCountryidd(mActivityTabs, iddCode);
                    if (get_Response.is_email_verified == 1)
                        saveState.setIsVerified(mActivityTabs, true);
                    else {
                        saveState.setIsVerified(mActivityTabs, false);
                    }
                    if (get_Response.email != null && !get_Response.email.trim().equals("") &&
                            !get_Response.email.trim().equalsIgnoreCase("null") && saveState.getUserMailID(getActivity()).trim().equals(""))

                    {
                        saveState.setUserMailID(getActivity(), Uri.decode(get_Response.email));
                    }
                    if (get_Response.image != null && !get_Response.image.trim().equals("") && !get_Response.image.trim().equalsIgnoreCase("null")) {
                        File imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + "TNC/images/userimage.jpg");
                        if (!imageFile.exists()) {
                            userImageURL = get_Response.image;
                            DownloadUserImageAsyncTask downloadUserImage = new DownloadUserImageAsyncTask();
                            downloadUserImage.execute();
                        }
                    }

                    if (get_Response != null
                            && get_Response.is_default_image
                            .equalsIgnoreCase("no")
                            && !get_Response.image.trim().equals("")
                            && !get_Response.image.trim().equalsIgnoreCase(
                            "NULL")) {
                        saveState.setDisplayISDEFAULTIMAGEString(getActivity(), "true");
                        saveState.setDefaultImage(mActivityTabs, false);
                    } else if (get_Response != null
                            && get_Response.is_default_image
                            .equalsIgnoreCase("yes")
                            && !get_Response.image.trim().equals("")
                            && !get_Response.image.trim().equalsIgnoreCase(
                            "NULL")) {
                        saveState.setDefaultImage(mActivityTabs, true);
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //async task to download the user image
    class DownloadUserImageAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {
            try {
                bitmapUserImage = GlobalConfig_Methods.loadBitmap(userImageURL);
                String extStorageDirectory = String.valueOf(
                        Environment.getExternalStorageDirectory() + File.separator + "TNC/images/");
                GlobalConfig_Methods.savebitmap(bitmapUserImage,
                        extStorageDirectory, "userimage");

                saveState.setImageChange(getActivity(), true);
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }
    }

    /**
     * interface to handle update emergency number on the home screen
     */

    INotifyGalleryDialog iNotifyUpdateEmergency = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            // in case of emergency number needs to be updated
            if (!mEmergency.trim().equals(""))
                saveState.setEmergency(mActivityTabs, mEmergency);
        }

        @Override
        public void no() {
        }
    };


    // method to check whether user is activated or automatically deactivated
	/*private void checkUserActivation() {
		try {
			CheckUserActivationBean objCheckUserActivationBean = new CheckUserActivationBean();
			objCheckUserActivationBean.setCountry_code(saveState.getCountryCode(mActivity));
			objCheckUserActivationBean.setNumber(saveState.getUserPhoneNumber(mActivity));
			objCheckUserActivationBean.setBackup_key(saveState.getBackupKey(mActivity));
			gson = new Gson();
			String stingGson = gson.toJson(objCheckUserActivationBean);
			StringEntity stringEntity;
			stringEntity = new StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivity,
					GlobalCommonValues.CHECK_USER_ACTIVATION, stringEntity,
					getUserActivationResponseHandler,
					mActivity.getString(R.string.private_key),saveState.getPublicKey(mActivity));
			gson = new Gson();
			MyHttpConnection.postHeaderWithoutJsonEntity(mActivity,
					GlobalCommonValues.CHECK_USER_ACTIVATION, getUserActivationResponseHandler,
					mActivity.getString(R.string.private_key),
					saveState.getPublicKey(mActivity));
		} catch (Exception e) {
			e.getMessage();
		}
	}*/

    // async task for handling check of whether user is activated or automatically deactivated
	/*AsyncHttpResponseHandler getUserActivationResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
		}

		@Override
		public void onSuccess(String response) {
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("getUserActivationResponseHandler", "OnSuccess",
							response);
					getResponseUserActivation(response);
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(Throwable e, String response) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("getUserActivationResponseHandler", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};*/

    /**
     * handle response for the request being made for checking activation status
     *
     * @param //response
     */
	/*private void getResponseUserActivation(String response) {
		try {
			String response2="";
			if(response.contains("</div>") || response.contains("<h4>") || response.contains("php")){
				response2=response.substring(response.indexOf("response_code")-2,response.length());
			}
			else{
				response2=response;
			}

			if (!TextUtils.isEmpty(response2) && GlobalConfig_Methods.isJsonString(response2)) {
				gson = new Gson();
				CancelRegistrationResponseBean get_Response = gson.fromJson(response2,CancelRegistrationResponseBean.class);
				if (get_Response.response_code
						.equals(GlobalCommonValues.SUCCESS_CODE)){
					if(dialogDeactivation == null){
						dialogDeactivation = new ImageRequestDialog();
					}
					else if(dialogDeactivation!=null && !dialogDeactivation.isVisible()){
						if(get_Response.getResponse_message().equalsIgnoreCase("USER DEACTIVATED")){
							dialogDeactivation.newInstance("",getActivity(),"Terms of Use violation: Your account has been deactivated.", "",null);
							dialogDeactivation.setCancelable(false);
							dialogDeactivation.show(getChildFragmentManager(), "test");
							saveState.setIS_DEACTIVATED(getActivity(),true);
						}
					}
				}else{
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}*/
    @SuppressWarnings("deprecation")
    private void setDimensions() {
        display = mActivityTabs.getWindow().getWindowManager()
                .getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        int display_width = width - 24;// 24
        if (display_width > 0) {
            int item_width = display_width / 3;
            if (item_width > 0) {
                view_item_width = item_width;
            }
        } else {
            view_item_width = (int) mActivityTabs.getResources().getDimension(
                    R.dimen.photo_side);
        }
        // gvTiles.setColumnWidth(view_item_width);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnAddTile) {
            MainBaseActivity.objTileEdit = null;
            MainBaseActivity.isTileCreated = false;
            ChooseContactFragment objChooseContact = null;
            if (mActivity instanceof MainBaseActivity) {
                objChooseContact = new ChooseContactFragment();
                objChooseContact.newInstance(((MainBaseActivity) mActivity));
                ((MainBaseActivity) mActivity).setFragment(objChooseContact);
            } else if (mActivity instanceof HomeScreenActivity) {
                objChooseContact = new ChooseContactFragment();
                objChooseContact.newInstance(((HomeScreenActivity) mActivity));
                ((HomeScreenActivity) mActivity).setFragment(objChooseContact);
            }
        } else if (v.getId() == R.id.btnContactSharing) {
            //To-Do

            if (mActivityTabs instanceof HomeScreenActivity) {
                if (!saveState.isRegistered(mActivityTabs)) {
                    ImageRequestDialog dialog = new ImageRequestDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance("", mActivityTabs, Html.fromHtml("Please create profile <br>" +
                            "to use this feature").toString(), "", null, null);
                    dialog.show(getChildFragmentManager(), "test");
                } else {
                    if (DBQuery.getAllTiles(mActivityTabs).size() == 0) {
                        ImageRequestDialog dialog = new ImageRequestDialog();
                        dialog.setCancelable(false);
                        dialog.newInstance("", mActivityTabs, "No Contacts to share", "", null, null);
                        dialog.show(getChildFragmentManager(), "test");
                    } else {
                        TileContacts tileContacts = new TileContacts();
                        tileContacts.newInstance("ShareContactsSelection", null, null, null);
                        ((HomeScreenActivity) mActivityTabs).setFragment(tileContacts);
                    }
                }
            }
        } else if (v.getId() == R.id.btnDialPad) {
            DialPad objDialPad = new DialPad();
            objDialPad.newInstance(getActivity(), null);
            ((HomeScreenActivity) getActivity()).setFragment(objDialPad);
        } else if (v.getId() == R.id.btnNotification) {
            MainBaseActivity.objTileEdit = null;
            MainBaseActivity.isTileCreated = false;
            if (mActivityTabs instanceof HomeScreenActivity) {
                if (!saveState.isRegistered(mActivityTabs)) {
                    ImageRequestDialog dialog = new ImageRequestDialog();
                    dialog.setCancelable(false);
                    dialog.newInstance("", mActivityTabs, Html.fromHtml(
                            "Please create profile <br>"
                                    + "to use this feature").toString(), "",
                            null, null);
                    dialog.show(getChildFragmentManager(), "test");
                } else {
                    if (mActivityTabs instanceof HomeScreenActivity) {
                        NotificationsFragment objNotificationFragment = new NotificationsFragment();
                        objNotificationFragment.newInstance(mActivityTabs,
                                iNotifyObject);
                        ((HomeScreenActivity) mActivityTabs)
                                .setFragment(objNotificationFragment);// pushFragments(AppTabsConstants.HOME,
                        // new
                        // NotificationsFragment());
                    }
                }
            }
        } else if (v.getId() == R.id.btnCall) {
            MainBaseActivity.objTileEdit = null;
            MainBaseActivity.isTileCreated = false;
            if (!saveState.getEmergency(mActivityTabs).trim().equals("")) {
                EmergencyCall dialogCalling = new EmergencyCall();
                dialogCalling.setCancelable(false);
                if (mActivity instanceof MainBaseActivity) {
                    dialogCalling.newInstance("", ((MainBaseActivity) mActivity),
                            saveState.getEmergency(mActivity), "");
                    dialogCalling.show(((MainBaseActivity) mActivity)
                            .getSupportFragmentManager(), "test");
                } else if (mActivity instanceof HomeScreenActivity) {
                    dialogCalling.newInstance("", ((HomeScreenActivity) mActivity),
                            saveState.getEmergency(mActivity), "");
                    dialogCalling.show(((HomeScreenActivity) mActivity)
                            .getSupportFragmentManager(), "test");
                }
            } else if (saveState.getEmergency(mActivityTabs).trim().equals("")) {
                ImageRequestDialog dialogConfigureEmergency = new ImageRequestDialog();
                dialogConfigureEmergency.setCancelable(false);
                dialogConfigureEmergency.newInstance("", mActivityTabs, "Please configure emergency number from settings", "", null);
                dialogConfigureEmergency.show(getChildFragmentManager(), "test");
            }
        } else if (v.getId() == R.id.btnDisable) {
            ImageRequestDialog dialog = new ImageRequestDialog();
            dialog.setCancelable(false);
            dialog.newInstance("", mActivityTabs, Html.fromHtml(
                    "Please create profile <br>"
                            + "to use this feature").toString(), "",
                    null, null);
            dialog.show(getChildFragmentManager(), "test");
        } else if ((v.getId() == R.id.btnHome) || (v.getId() == HomeScreenActivity.btnHome.getId())) {
            //Refresh List Here
            if (saveState.isRegistered(getActivity())) {

                if (dialogProgress == null) {
                    dialogProgress = new TransparentProgressDialog(getActivity(), R.drawable.customspinner);
                    dialogProgress.show();
                } else if (dialogProgress != null && !dialogProgress.isShowing()) {
                    dialogProgress.show();
                }
                saveState.setRefrehContactList(getActivity(), false);
                saveState.setIS_FROM_HOME(getActivity(), true);
                if (DBQuery.getAllTiles(mActivityTabs).size() > 0) {
                    ((HomeScreenActivity) getActivity()).sendContactsToServer();
                } else {
                    if (dialogProgress != null && dialogProgress.isShowing()) {
                        dialogProgress.dismiss();
                    }
                }
            }
        } else if (v.getId() == R.id.btnPreviousPage) {
            isPageSelected = false;
            // go to previous page
            gridview.setCurrentItem(gridview.getCurrentItem() - 1);
        } else if (v.getId() == R.id.btnNextPage) {
            isPageSelected = false;
            // go to next page
            gridview.setCurrentItem(gridview.getCurrentItem() + 1);
        } else if (v.getId() == R.id.btnProfileMode) {

            // Set profile as ringtone / vibrate / silent
            setProfile(v);

        } else if (v.getId() == R.id.btnVoiceMail) {
            /*if(saveState.isRegistered(getActivity())){
                ((HomeScreenActivity)getActivity()).setFragment(new VoiceMessageListFragment());
            }else{
                ImageRequestDialog dialog = new ImageRequestDialog();
                dialog.setCancelable(false);
                dialog.newInstance("", mActivityTabs, Html.fromHtml(
                        "Please create profile <br>"
                                + "to use this feature").toString(), "",
                        null, null);
                dialog.show(getChildFragmentManager(), "test");
            }*/
        } else if (v.getId() == R.id.btnSearch) {
            if (DBQuery.getAllTiles(getActivity()).size() > 0) {
                ((HomeScreenActivity) getActivity()).setFragment(new SearchChatButtonFragment());
            } else {
                GlobalConfig_Methods.showSimErrorDialog(getActivity(), getString(R.string.txtNoButtonAlert));
            }
        }
    }


    /**
     * Method to set profile as ringtone / vibrate / silent
     */
    private void setProfile(final View v) {

        final SimpleTooltip tooltip = new SimpleTooltip.Builder(getActivity())
                .anchorView(v)
                //.text("modal_custom")
                .gravity(Gravity.TOP)
                .dismissOnOutsideTouch(false)
                .dismissOnInsideTouch(false)
                .modal(true)
//                        .animated(true)
//                        .animationDuration(2000)
                .animationPadding(SimpleTooltipUtils.pxFromDp(50))
                .contentView(R.layout.tooltip_custom)
                .focusable(true)
                .build();

        final Button btnNormal = tooltip.findViewById(R.id.btnNormal);
        final Button btnVibrate = tooltip.findViewById(R.id.btnVibrate);
        final Button btnSilent = tooltip.findViewById(R.id.btnSilent);
        final Button btnAirplaneMode = tooltip.findViewById(R.id.btnAirplaneMode);

        // Get current profile mode
        String mCurrentAudioMode = GlobalConfig_Methods.getProfileRingerMode(getActivity());

        boolean isAirplaneMode = false;

        try {
            isAirplaneMode = GlobalConfig_Methods.isAirplaneModeOn(getActivity());
        } catch (Exception e) {
            e.getMessage();
        }

        // Set icon as per the current mode
        if (isAirplaneMode) {
            btnAirplaneMode.setBackgroundResource(R.drawable.ic_airplane_mode_enabled);
            btnNormal.setBackgroundResource(R.drawable.ic_normal_mode_disabled);
            btnVibrate.setBackgroundResource(R.drawable.ic_vibrate_mode_disabled);
            btnSilent.setBackgroundResource(R.drawable.ic_silent_mode_disabled);
        } else {
            if (mCurrentAudioMode.equals(GlobalCommonValues.PHONE_MODE_NORMAL)) {
                btnNormal.setBackgroundResource(R.drawable.ic_normal_mode_enabled);
                btnVibrate.setBackgroundResource(R.drawable.ic_vibrate_mode_disabled);
                btnSilent.setBackgroundResource(R.drawable.ic_silent_mode_disabled);
                btnAirplaneMode.setBackgroundResource(R.drawable.ic_airplane_mode_disabled);
            } else if (mCurrentAudioMode.equals(GlobalCommonValues.PHONE_MODE_VIBRATE)) {
                btnNormal.setBackgroundResource(R.drawable.ic_normal_mode_disabled);
                btnVibrate.setBackgroundResource(R.drawable.ic_vibrate_mode_enabled);
                btnSilent.setBackgroundResource(R.drawable.ic_silent_mode_disabled);
                btnAirplaneMode.setBackgroundResource(R.drawable.ic_airplane_mode_disabled);
            } else if (mCurrentAudioMode.equals(GlobalCommonValues.PHONE_MODE_SILENT)) {
                btnNormal.setBackgroundResource(R.drawable.ic_normal_mode_disabled);
                btnVibrate.setBackgroundResource(R.drawable.ic_vibrate_mode_disabled);
                btnSilent.setBackgroundResource(R.drawable.ic_silent_mode_enabled);
                btnAirplaneMode.setBackgroundResource(R.drawable.ic_airplane_mode_disabled);
            }
        }

        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                if (tooltip.isShowing())
                    tooltip.dismiss();

                // update profile button images
                toggleProfileMode(GlobalCommonValues.PHONE_MODE_NORMAL);

                // set button icons as per the selected profile
                setProfileButtonModeStatus(btnNormal, btnVibrate, btnSilent,
                        GlobalCommonValues.PHONE_MODE_NORMAL);

            }
        });

        btnVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                if (tooltip.isShowing())
                    tooltip.dismiss();

                // update profile button images
                toggleProfileMode(GlobalCommonValues.PHONE_MODE_VIBRATE);

                // set button icons as per the selected profile
                setProfileButtonModeStatus(btnNormal, btnVibrate, btnSilent,
                        GlobalCommonValues.PHONE_MODE_VIBRATE);

            }
        });

        btnSilent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                if (tooltip.isShowing())
                    tooltip.dismiss();

                // update profile button images
                toggleProfileMode(GlobalCommonValues.PHONE_MODE_SILENT);

                // set button icons as per the selected profile
                setProfileButtonModeStatus(btnNormal, btnVibrate, btnSilent,
                        GlobalCommonValues.PHONE_MODE_SILENT);

            }
        });

        btnAirplaneMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v2) {

                if (tooltip.isShowing())
                    tooltip.dismiss();

                // Go to Airplane Mode Settings
                startActivityForResult(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS), 0);
            }
        });

        tooltip.show();
    }

    /**
     * Method to change profile mode mode
     */
    private void toggleProfileMode(String mProfileMode) {
        if (mProfileMode.equals(GlobalCommonValues.PHONE_MODE_NORMAL)) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else if (mProfileMode.equals(GlobalCommonValues.PHONE_MODE_VIBRATE)) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if (mProfileMode.equals(GlobalCommonValues.PHONE_MODE_SILENT)) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    /**
     * Check if permission is granted to the app
     *
     * @param : context
     * @param : permissions
     * @return
     */
    /*public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * interface to handle Page left/right swipe arrow and page number
     */
    public INotifyAction mActionUpdatePageArrowAndNumber = new INotifyAction() {
        @Override
        public void setAction(String action) {
            if (action.equalsIgnoreCase(GlobalCommonValues.UPDATE_UI_STRING))
                // Call method to update page swipe arrow status and page number
                updatePageNumberAndSwipeStatus();
        }
    };

    /**
     * Method to update the page number and
     * visibility of next and back page arrow image as per the page number
     */
    private void updatePageNumberAndSwipeStatus() {
        if (gridview.getPageCount() == 0 || gridview.getPageCount() == 1) {
            //make visibilty of both the Arrow icons to gone in case of no chat buttons/ child/ items
            // in a grid
            mBtnPreviousPage.setVisibility(View.GONE);
            mBtnNextPage.setVisibility(View.GONE);

            // and in case there is only one page with an item
            if ((gridview.getChildCount() > 0) || (listTiles != null && listTiles.size() > 0)) {
                mTextPageNumber.setText(String.valueOf(gridview.getCurrentItem() + 1));
            }

        } else if (gridview.getCurrentItem() == gridview.getPageCount() - 1) {
            // Identifying last page to make the next arrow visibility gone on the last page
            mBtnPreviousPage.setVisibility(View.VISIBLE);
            mBtnNextPage.setVisibility(View.GONE);
            mTextPageNumber.setText(String.valueOf(gridview.getCurrentItem() + 1));
        } else if (gridview.getCurrentItem() == 0) {
            // Identifying first page to make the previous arrow visibility gone on the first page
            mBtnPreviousPage.setVisibility(View.GONE);
            mBtnNextPage.setVisibility(View.VISIBLE);
            mTextPageNumber.setText(String.valueOf(gridview.getCurrentItem() + 1));
        } else if ((gridview.getCurrentItem() > 0) && (gridview.getCurrentItem() < gridview.getPageCount() - 1)) {
            // in case current page is greater than minimum page and less than maximum page then make both arrows visible
            mBtnPreviousPage.setVisibility(View.VISIBLE);
            mBtnNextPage.setVisibility(View.VISIBLE);
            mTextPageNumber.setText(String.valueOf(gridview.getCurrentItem() + 1));
        }
    }

    // async task to setup the display tiles
    class DisplayTilesASynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            /*//			setTilesAdapter();*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                setTilesAdapter();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    /**
     * Method to go to the chat window screen
     *
     * @param position
     */
    private void goToChatWindow(int position) {
        try {
            String phoneNumber = "";
            int BBID = 0;
            BBContactsBean objContactDetail = null;
            ArrayList<BBContactsBean> listBBContacts = null;
            phoneNumber = listTiles.get(position)
                    .getPhoneNumber();

            BBID = listTiles.get(position).getBBID();
            if (!(BBID > 0)) {
                BBID = DBQuery.getBBIDFromPhoneNumber(
                        mActivityTabs, phoneNumber);
            }
            if (BBID != 0) {
                if (!saveState.getBBID(mActivityTabs).trim()
                        .equals("")) {
                    // Update Notification
                    DBQuery.updateMessageStatus(mActivityTabs,
                            BBID, Integer.parseInt(saveState
                                    .getBBID(mActivityTabs)));
                    MessageStatusUpdateBean updateStatusBean = new MessageStatusUpdateBean();
                    updateStatusBean.setBbid(String
                            .valueOf(BBID));
                    updateMessageStatus(updateStatusBean);
                    ((HomeScreenActivity) mActivityTabs)
                            .setUnreadMessageCount();

                    adapterTiles.notifyDataSetChanged();

                    // Open Chat Window
                    listBBContacts = new ArrayList<BBContactsBean>();
                    listBBContacts = DBQuery
                            .checkBBContactExistence(
                                    mActivityTabs, BBID);
                    objContactDetail = new BBContactsBean();
                    objContactDetail.setName(listBBContacts
                            .get(0).getName());
                    objContactDetail
                            .setPhoneNumber(listBBContacts.get(
                                    0).getPhoneNumber());
                    objContactDetail.setCountryCode(listBBContacts.get(
                            0).getCountryCode());
                    objContactDetail.setBBID(listBBContacts
                            .get(0).getBBID());
                    objContactDetail.setImage(listBBContacts
                            .get(0).getImage());
                    MessagePredefinedComposeFragment objMesssagePredefined = new MessagePredefinedComposeFragment();
                    objMesssagePredefined.newInstance(
                            objContactDetail, null,
                            Integer.parseInt(saveState
                                    .getBBID(mActivityTabs)),
                            BBID, INotifyCount);
                    if (mActivityTabs instanceof MainBaseActivity) {
                        ((MainBaseActivity) mActivityTabs)
                                .setFragment(objMesssagePredefined);
                    } else if (mActivityTabs instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) mActivityTabs)
                                .setFragment(objMesssagePredefined);
//
//                        Intent intent = new Intent(getActivity().getBaseContext(),
//                                MessagePredefinedComposeFragment.class);
//                        getActivity().startActivity(intent);

                    }
                    if (counter != null) {
                        counter.cancel();
                        counter = null;
                    }
                    disableRefreshing();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Counter used for initiating a call if the user taps on the tile
     *
     * @author a3logics
     */
    public class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (tilesType.equalsIgnoreCase("call")) {
                makeCall(selectedTilePosition);
            } else if (tilesType.equalsIgnoreCase("message")) {
                if (counter != null) {
                    goToChatWindow(selectedTilePosition);
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // "”Left: " + millisUntilFinished/1000);
        }
    }

    /**
     * Method to set status of  button on home screen bottom on the basis of phone profile
     */
    private void setProfileButtonModeStatus(final View normal, View vibrate, View silent,
                                            String mProfileMode) {
        try {
            // Call Method to set button icon on home screen bottom as per the current phone profile
            setProfileButtonIcon(mProfileMode);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * Method to set button icon on home screen bottom as per the current phone profile
     *
     * @param mProfileMode
     */
    private void setProfileButtonIcon(String mProfileMode) throws Exception {

        if (GlobalConfig_Methods.isAirplaneModeOn(getActivity())) {
            mBtnProfileMode.setBackgroundResource(R.drawable.ic_airplane_mode_enabled);
        } else if (mProfileMode.equalsIgnoreCase(GlobalCommonValues.PHONE_MODE_NORMAL)) {
            mBtnProfileMode.setBackgroundResource(R.drawable.ic_normal_mode_enabled);
        } else if (mProfileMode.equalsIgnoreCase(GlobalCommonValues.PHONE_MODE_VIBRATE)) {
            mBtnProfileMode.setBackgroundResource(R.drawable.ic_vibrate_mode_enabled);
        } else if (mProfileMode.equalsIgnoreCase(GlobalCommonValues.PHONE_MODE_SILENT)) {
            mBtnProfileMode.setBackgroundResource(R.drawable.ic_silent_mode_enabled);
        }
    }
}
