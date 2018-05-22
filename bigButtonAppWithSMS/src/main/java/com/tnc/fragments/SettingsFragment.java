package com.tnc.fragments;

import java.util.ArrayList;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.SettingMenuAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
/**
 * class to display app settings screen
 *  @author a3logics
 */
public class SettingsFragment extends BaseFragmentTabs
{
    private FrameLayout flBackArrow;
    private TextView tvTitle,tvSettings;
    private Button btnBack;
    private ListView lvSettingMenu;
    private SettingMenuAdapter adapterSettingMenu;
    private ArrayList<String> listSettingMenu;
    private ArrayList<String> listSettingMenuDetail;
    private int adapterSelected_position = 0;
    private INotifyGalleryDialog iNotifyRefreshSelectedTab;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((HomeScreenActivity) activity).setINotifySettings(iNotifyGalleryDialog);
    }
    public SettingsFragment newInstance(INotifyGalleryDialog iNotifyRefreshSelectedTab)
    {
        SettingsFragment frag = new SettingsFragment();
        this.iNotifyRefreshSelectedTab=iNotifyRefreshSelectedTab;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }
    public void onDestroy() {
        super.onDestroy();
        if(mActivityTabs instanceof HomeScreenActivity)
        {
            ((HomeScreenActivity)mActivityTabs).getUnreadNotificationCount();
            if(iNotifyRefreshSelectedTab!=null)
                iNotifyRefreshSelectedTab.yes();
        }
        if(HomeScreenActivity.btnNotification!=null  &&
                HomeScreenActivity.btnAddTile!=null &&
                HomeScreenActivity.btnCallEmergency!=null)
        {
            HomeScreenActivity.btnNotification.setClickable(true);
            HomeScreenActivity.btnNotification.setEnabled(true);
            HomeScreenActivity.btnAddTile.setClickable(true);
            HomeScreenActivity.btnAddTile.setEnabled(true);
            HomeScreenActivity.btnCallEmergency.setClickable(true);
            HomeScreenActivity.btnCallEmergency.setEnabled(true);
        }
    };
    public void onResume() {
        super.onResume();
        if(HomeScreenActivity.btnNotification!=null  &&
                HomeScreenActivity.btnAddTile!=null &&
                HomeScreenActivity.btnCallEmergency!=null)
        {
            HomeScreenActivity.btnNotification.setClickable(false);
            HomeScreenActivity.btnNotification.setEnabled(false);
            HomeScreenActivity.btnAddTile.setClickable(false);
            HomeScreenActivity.btnAddTile.setEnabled(false);
            HomeScreenActivity.btnCallEmergency.setClickable(false);
            HomeScreenActivity.btnCallEmergency.setEnabled(false);
        }
        // As onDestroy() of User Info is not working properly So we handled the value reset over here
        MainBaseActivity._bitmap = null;
        UserRegistration1.isOnUserRegistration = false;
    };
    /**
     * interface to notify the list of deselecting the selected position
     */
    INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            adapterSettingMenu.setRowColor(adapterSelected_position, false);
        }
        @Override
        public void no() {
            adapterSettingMenu.setRowColor(adapterSelected_position, false);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.settings, container, false);
        idInitialization(view);
        return view;
    }
    // Method to initialize views/widgets
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvSettings=(TextView) view.findViewById(R.id.tvSettings);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        lvSettingMenu=(ListView) view.findViewById(R.id.lvSettingMenu);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        //		CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/StencilStd.otf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvSettings, "fonts/Roboto-Bold_1.ttf");

//        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

        listSettingMenu=new ArrayList<String>();
        listSettingMenuDetail=new ArrayList<String>();
        //		listSettingMenu.add("Notifications");
        listSettingMenu.add(getResources().getString(R.string.txtChangeButtonSettings));
        listSettingMenu.add(getResources().getString(R.string.txtContactSharing));
        listSettingMenu.add(getResources().getString(R.string.txtConfigureMessagesSettings));
        listSettingMenu.add(getResources().getString(R.string.txtCloudBackup));
        if(!saveState.isRegistered(mActivityTabs)){
            listSettingMenu.add(getResources().getString(R.string.txtAppRegistration));
        }
        /*if(saveState.isRegistered(mActivityTabs))
            listSettingMenu.add(getResources().getString(R.string.txtUserInfoCamelCase));
        else{
            listSettingMenu.add(getResources().getString(R.string.txtAppRegistration));
        }*/
        if(!(saveState.getISPREMIUMUSER(getActivity())) &&
                (saveState.isRegistered(getActivity())))
            listSettingMenu.add(getResources().getString(R.string.txtPremiumUser));

        listSettingMenu.add(getResources().getString(R.string.txtUserSettings));

        adapterSettingMenu=new SettingMenuAdapter(mActivityTabs, listSettingMenu,
                listSettingMenuDetail,saveState);
        lvSettingMenu.setAdapter(adapterSettingMenu);

        btnBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mActivityTabs instanceof MainBaseActivity)
                {
                    ((MainBaseActivity)mActivityTabs).fragmentManager.popBackStack();
                }
                else if(mActivityTabs instanceof HomeScreenActivity)
                {
                    ((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
                }
            }
        });

        lvSettingMenu.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View rowView, int position,
                                    long arg3){
                if(position==0)
                {  // In case of change tile / contact button
                    MainBaseActivity.selectedPrefixCodeForTileDetails="";
                    MainBaseActivity.selectedCountryCodeForTileDetails="";
                    MainBaseActivity.isIsdCodeFlagChecked=false;
                    MainBaseActivity.contactNameForTile="";
                    MainBaseActivity.contactNumberForTile="";
                    //In case of tile edit
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);

                    TileContactListFragment tileContactListFragment=new TileContactListFragment();
                    ((HomeScreenActivity)mActivityTabs).setFragment(tileContactListFragment);
                }
                else if(position==1)
                {
                    //In case of contact Sharing
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);
                    if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        if(!saveState.isRegistered(mActivityTabs))
                        {
                            ImageRequestDialog dialog=new ImageRequestDialog();
                            dialog.setCancelable(false);
                            dialog.newInstance("", mActivityTabs,Html.fromHtml("Please create profile <br>"+
                                    "to use this feature").toString(),"",null,iNotifyGalleryDialog);
                            dialog.show(getChildFragmentManager(),"test");
                        }
                        else
                        {
                            if(DBQuery.getAllTiles(mActivityTabs).size()==0){
                                ImageRequestDialog dialog=new ImageRequestDialog();
                                dialog.setCancelable(false);
                                dialog.newInstance("", mActivityTabs,"No Contacts to share","",null,iNotifyGalleryDialog);
                                dialog.show(getChildFragmentManager(),"test");
                            }
                            else{
                                TileContacts tileContacts=new TileContacts();
                                tileContacts.newInstance("ShareContactsSelection",null,null,null);
                                ((HomeScreenActivity)mActivityTabs).setFragment(tileContacts);
                            }
                        }
                    }
                }
                else if(position==2)
                {
                    //In case of configure messages
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);
                    if(mActivityTabs instanceof HomeScreenActivity)
                    {

                        //((HomeScreenActivity)mActivityTabs).setFragment(new InitiationResponseMessages());
                        /**
                         * changes for UAT-442
                         * Devanshu nath Tripathi
                         *
                         */

                        if(!saveState.isRegistered(mActivityTabs)){
                            ImageRequestDialog dialog=new ImageRequestDialog();
                            dialog.setCancelable(false);
                            dialog.newInstance("", mActivityTabs,Html.fromHtml("Please create profile <br>"+
                                    "to use this feature").toString(),"",null,iNotifyGalleryDialog);
                            dialog.show(getChildFragmentManager(),"test");
                        }
                        else{
                            ((HomeScreenActivity)mActivityTabs).setFragment(new InitiationResponseMessages());
                        }
                    }
                }
                else if(position==3)
                {
                    //In case of cloud backup(manual backup/restore)
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);
                    if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        if(!saveState.isRegistered(mActivityTabs))
                        {
                            ImageRequestDialog dialog=new ImageRequestDialog();
                            dialog.setCancelable(false);
                            dialog.newInstance("", mActivityTabs,Html.fromHtml("Please create profile<br>"+
                                    "to use this feature").toString(),"",null,iNotifyGalleryDialog);
                            dialog.show(getChildFragmentManager(),"test");
                        }
                        else
                        {
                            ((HomeScreenActivity)mActivityTabs).setFragment(new CloudBackupFragment());
                        }
                    }
                }
                else if(listSettingMenu.get(position).trim().equalsIgnoreCase(getResources().getString(R.string.txtAppRegistration)))//(position==4)
                {
                    //In case of User Registration
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);
                    if(mActivityTabs instanceof HomeScreenActivity)
                    {
                        if(!saveState.isRegistered(mActivityTabs))
                        {
                            //((HomeScreenActivity)mActivityTabs).setFragment(new RegistrationFeatures());
                            /*if(mActivityTabs instanceof MainBaseActivity){
                                ((MainBaseActivity)mActivityTabs).setFragment(new UserRegistration());
                            }else if(mActivityTabs instanceof HomeScreenActivity){
                                ((HomeScreenActivity)mActivityTabs).setFragment(new UserRegistration());
                            }*/

                            if(mActivityTabs instanceof MainBaseActivity){
                                ((MainBaseActivity)mActivityTabs).setFragment(new UserRegistrationFragment());
                            }else if(mActivityTabs instanceof HomeScreenActivity){
                                ((HomeScreenActivity)mActivityTabs).setFragment(new UserRegistrationFragment());
                            }
                        }
                       /* else{
                            ((HomeScreenActivity)mActivityTabs).setFragment(new UserInfoFragment());
                        }*/
                    }
                }else if(listSettingMenu.get(position).trim().equalsIgnoreCase(getResources().getString(R.string.txtPremiumUser))){
                    //In case of PremiumFeatures Screen
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);
                    // In case of premium features

                    // go to google play store to download Premium Version of the app
                    GlobalConfig_Methods.gotoPremiumVersionPlayStore(getActivity());

                   /* PremiumFeaturesFragment mObjectPremiumFeaturesFragment = new PremiumFeaturesFragment();
                    mObjectPremiumFeaturesFragment.newInstance("","",iNotifyGalleryDialog);
                    ((HomeScreenActivity)mActivityTabs).setFragment(mObjectPremiumFeaturesFragment);*/
                } else if(listSettingMenu.get(position).trim().equalsIgnoreCase(getResources().getString(R.string.txtUserSettings))){
                    adapterSelected_position = position;
                    adapterSettingMenu.setRowColor(adapterSelected_position, true);

                    // Go to User Settings Screen
                    UserSettings mUserSettings = new UserSettings();
                    mUserSettings.newInstance(getActivity(), getResources().getString(R.string.txtUserSettings));

                    if(mActivityTabs instanceof MainBaseActivity){
                        ((MainBaseActivity)mActivityTabs).setFragment(mUserSettings);
                    }else if(mActivityTabs instanceof HomeScreenActivity){
                        ((HomeScreenActivity)mActivityTabs).setFragment(mUserSettings);
                    }
                }
            }
        });

        HomeScreenActivity.btnBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mActivityTabs instanceof HomeScreenActivity)
                {
                    HomeScreenActivity.setBackButtonFunctionality("settingfragment");
                }
            }
        });
    }
}
