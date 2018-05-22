package com.tnc.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.UserSettingsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import java.util.ArrayList;

/**
 * class : UserSettings
 * description : This class is used to display user setting options
 * Created by a3logics on 1/12/16.
 */

public class UserSettings extends BaseFragmentTabs {

    private FrameLayout flBackArrow;
    private TextView tvTitle,tvSettings;
    private Button btnBack;
    private ListView lvUserSetting;
    private UserSettingsAdapter adapterSettingMenu;
    private ArrayList<String> listUserSettingsMenu;
    private int adapterSelected_position = 0;
    private Context mContext;
    private String screenType = "";

    public UserSettings newInstance(Context mContext, String screenType){
        UserSettings frag = new UserSettings();
        this.mContext = mContext;
        this.screenType = screenType;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState = new SharedPreference();
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvSettings = (TextView) view.findViewById(R.id.tvSettings);
        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        lvUserSetting = (ListView) view.findViewById(R.id.lvSettingMenu);
        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvSettings, "fonts/Roboto-Bold_1.ttf");

        listUserSettingsMenu = new ArrayList<String>();

        tvSettings.setText(screenType);

        tvSettings.setAllCaps(true);

        if(screenType.equalsIgnoreCase(getResources().getString(R.string.txtUserSettings).toUpperCase())){
            // In case of it is User Settings screen
            /*if(saveState.isRegistered(mActivityTabs)){
                listUserSettingsMenu.add(getResources().getString(R.string.txtUserInfoCamelCase));
            }*/
            if(saveState.isRegistered(mActivityTabs)){
                listUserSettingsMenu.add(getResources().getString(R.string.txtUserProfileSettings));
            }

            listUserSettingsMenu.add(getResources().getString(R.string.txtUserPrivacySettings));
            listUserSettingsMenu.add(getResources().getString(R.string.txtUserPersonalSettings));
            listUserSettingsMenu.add(getResources().getString(R.string.txtChatButtonCategory));
            listUserSettingsMenu.add(getResources().getString(R.string.txtUserGroups));
            listUserSettingsMenu.add(getResources().getString(R.string.txtElderlyMode));

        }else  if(screenType.equalsIgnoreCase(getResources().getString(R.string.txtUserPrivacySettings).toUpperCase())){
            // in case of it is User privacy Settings
            listUserSettingsMenu.add(getResources().getString(R.string.txtLocationSettings));
        }

        adapterSettingMenu = new UserSettingsAdapter(mActivityTabs, listUserSettingsMenu);
        lvUserSetting.setAdapter(adapterSettingMenu);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivityTabs instanceof MainBaseActivity) {
                    ((MainBaseActivity) mActivityTabs).fragmentManager.popBackStack();
                } else if (mActivityTabs instanceof HomeScreenActivity) {
                    ((HomeScreenActivity) mActivityTabs).fragmentManager.popBackStack();
                }
            }
        });

        // Handle click event of List item click
        lvUserSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                adapterSelected_position = position;
                adapterSettingMenu.setRowColor(adapterSelected_position, true);

                String mOptionSelected = listUserSettingsMenu.get(position);
                /*if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtUserInfoCamelCase))){

                    // Go to User Info Fragment Screen
                    ((HomeScreenActivity)mActivityTabs).setFragment(new UserInfoFragment());
                }else*/ if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtUserProfileSettings))){

                    // Go to User Info Fragment Screen
                    ((HomeScreenActivity)mActivityTabs).setFragment(new UserInfoFragment());

                }else if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtUserPrivacySettings))){

                    // Go to User Privacy Settings Screen
                    UserSettings mUserSettings = new UserSettings();
                    mUserSettings.newInstance(getActivity(), getResources().getString(R.string.txtUserPrivacySettings));

                    if(mActivityTabs instanceof MainBaseActivity){
                        ((MainBaseActivity)mActivityTabs).setFragment(mUserSettings);
                    }else if(mActivityTabs instanceof HomeScreenActivity){
                        ((HomeScreenActivity)mActivityTabs).setFragment(mUserSettings);
                    }

                }else if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtChatButtonCategory))){

                    if (mActivityTabs instanceof MainBaseActivity) {
                        ((MainBaseActivity) mActivityTabs).setFragment(new ChatButtonCategoryListFragment());
                    } else if (mActivityTabs instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) mActivityTabs).setFragment(new ChatButtonCategoryListFragment());
                    }

                }else if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtUserGroups))){

                }else if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtElderlyMode))){

                    if (mActivityTabs instanceof MainBaseActivity) {
                        ((MainBaseActivity) mActivityTabs).setFragment(new ElderlyModeFragment());
                    } else if (mActivityTabs instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) mActivityTabs).setFragment(new ElderlyModeFragment());
                    }
                }else if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtLocationSettings))){

                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else if(mOptionSelected.trim().equalsIgnoreCase(getResources().getString(R.string.txtUserPersonalSettings))){

                    if(!saveState.isRegistered(mActivityTabs)){
                        ImageRequestDialog dialog=new ImageRequestDialog();
                        dialog.setCancelable(false);
                        dialog.newInstance("", mActivityTabs,Html.fromHtml("Please create profile<br>"+
                                "to use this feature").toString(),"",null,iNotifyGalleryDialog);
                        dialog.show(getChildFragmentManager(),"test");
                    }else{
                        if (mActivityTabs instanceof MainBaseActivity) {
                            ((MainBaseActivity) mActivityTabs).setFragment(new PersonalSettingsFragment());
                        } else if (mActivityTabs instanceof HomeScreenActivity) {
                            ((HomeScreenActivity) mActivityTabs).setFragment(new PersonalSettingsFragment());
                        }
                    }
                }
                //Toast.makeText(getActivity(), mOptionSelected, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

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

}
