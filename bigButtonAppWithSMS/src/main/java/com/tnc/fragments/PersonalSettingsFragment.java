package com.tnc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.PersonalSettingsAdapter;
import com.tnc.adapter.UserSettingsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import java.util.ArrayList;

/**
 * Created by a3logics on 21/3/17.
 */

public class PersonalSettingsFragment extends BaseFragmentTabs {

    private FrameLayout flBackArrow;
    private TextView tvTitle,tvSettings;
    private Button btnBack;
    private ListView lvUserSetting;
    private PersonalSettingsAdapter adapterPersonalSettingMenu;
    private ArrayList<String> listPersonalSettingsMenu;
    private int adapterSelected_position = 0;
    private Context mContext;

    public PersonalSettingsFragment newInstance(Context mContext){
        PersonalSettingsFragment frag = new PersonalSettingsFragment();
        this.mContext = mContext;
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

        listPersonalSettingsMenu = new ArrayList<String>();

        tvSettings.setAllCaps(true);

        tvSettings.setText(getResources().getString(R.string.txtUserPersonalSettings));

        listPersonalSettingsMenu.add(getResources().getString(R.string.txtDialerInterface));
        listPersonalSettingsMenu.add(getResources().getString(R.string.txtChatButtonShape));
        listPersonalSettingsMenu.add(getResources().getString(R.string.txtVoiceMailSettings));

        adapterPersonalSettingMenu = new PersonalSettingsAdapter(mActivityTabs, listPersonalSettingsMenu);
        lvUserSetting.setAdapter(adapterPersonalSettingMenu);

        // handle item click event
        lvUserSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2){
                    adapterPersonalSettingMenu.setRowColor(position, true);

                    if (mActivityTabs instanceof MainBaseActivity) {
                        ((MainBaseActivity) mActivityTabs).setFragment(new VoiceMailSettingsFragment());
                    } else if (mActivityTabs instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) mActivityTabs).setFragment(new VoiceMailSettingsFragment());
                    }

                }
            }
        });

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
    }


    /**
     * interface to notify the list of deselecting the selected position
     */
    INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            adapterPersonalSettingMenu.setRowColor(adapterSelected_position, false);
        }
        @Override
        public void no() {
            adapterPersonalSettingMenu.setRowColor(adapterSelected_position, false);
        }
    };

}
