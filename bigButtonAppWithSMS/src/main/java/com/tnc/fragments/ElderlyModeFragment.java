package com.tnc.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tnc.BuildConfig;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.ElderlyModeAdapter;
import com.tnc.adapter.SettingMenuAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.dialog.AlertCallBack;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import java.util.ArrayList;

/**
 * Created by a3logics on 30/11/16.
 */

public class ElderlyModeFragment extends BaseFragmentTabs {

    private Context mContext;

    private TextView tvTitle, tvElderlyModeTitle;

    private FrameLayout flBackArrow;

    private Button btnBack;

    private ListView lvElderlyModeOptions;

    private ElderlyModeAdapter mAdapterElderlyMode;

    private ArrayList<String> mListElderlyModeMenu = new ArrayList<String>();

    public ElderlyModeFragment newInstance(Context mContext){
        this.mContext = mContext;
        ElderlyModeFragment frag = new ElderlyModeFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.elderly_mode_fragment, container, false);
        idInitialization(view);
        return view;
    }
    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState = new SharedPreference();
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvElderlyModeTitle=(TextView) view.findViewById(R.id.tvElderlyModeTitle);
        flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack=(Button) view.findViewById(R.id.btnBack);
        lvElderlyModeOptions=(ListView) view.findViewById(R.id.lvElderlyModeOptions);
        view.findViewById(R.id.llParentLayoutElderlyMode).setClickable(false);

        // Set Fonts of Views
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvElderlyModeTitle, "fonts/Roboto-Bold_1.ttf");

        mListElderlyModeMenu.add(getResources().getString(R.string.txtDisableLongTop));
        mListElderlyModeMenu.add(getResources().getString(R.string.txtAutoSpeakerMode));
        mListElderlyModeMenu.add(getResources().getString(R.string.txtEmergencyContactNotification));
        mListElderlyModeMenu.add(getResources().getString(R.string.txtHomeAppMode));

        // set elderly mode list options adapter
        mAdapterElderlyMode=new ElderlyModeAdapter(mActivityTabs, mListElderlyModeMenu);
        lvElderlyModeOptions.setAdapter(mAdapterElderlyMode);

        lvElderlyModeOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 3 || mListElderlyModeMenu.get(position).toString().equalsIgnoreCase(getResources().getString(R.string.txtHomeAppMode))){  // for clearing clear defaults & going to app settings
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener()
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
    }
}
