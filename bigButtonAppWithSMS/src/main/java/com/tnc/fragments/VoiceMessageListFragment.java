package com.tnc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.VoiceMailListAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CallDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;
import java.util.ArrayList;

/**
 * Created by a3logics on 25/4/17.
 */

public class VoiceMessageListFragment extends BaseFragmentTabs implements View.OnClickListener,
        AdapterView.OnItemClickListener{

    private FrameLayout          flBackArrow, flInformationButton;
    private LinearLayout         llParentLayout;
    private TextView             tvTitle, tvVoiceMailHeading;
    private Button               btnBack, btnHome;
    private ListView             lvVoiceMailList;
    private VoiceMailListAdapter mAdapterVoiceMail;
    private ArrayList<CallDetailsBean> mListVoiceMessages = new ArrayList<CallDetailsBean>();
    private int adapterSelected_position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view                      = inflater.inflate(R.layout.voice_mail_list_fragment, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState = new SharedPreference();

        tvTitle                       = (TextView) view.findViewById(R.id.tvTitle);
        tvVoiceMailHeading            = (TextView) view.findViewById(R.id.tvVoiceMailHeading);

        flBackArrow                   = (FrameLayout)  view.findViewById(R.id.flBackArrow);
        flInformationButton           = (FrameLayout)  view.findViewById(R.id.flInformationButton);
        llParentLayout                = (LinearLayout) view.findViewById(R.id.llParentLayout);

        btnHome                       = (Button) view.findViewById(R.id.btnHome);
        btnBack                       = (Button) view.findViewById(R.id.btnBack);

        lvVoiceMailList               = (ListView) view.findViewById(R.id.lvVoiceMailList);

        flBackArrow.setVisibility(View.VISIBLE);
        flInformationButton.setVisibility(View.VISIBLE);
        btnHome.setVisibility(View.VISIBLE);

        // Set font style of text views
        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvVoiceMailHeading, "fonts/Roboto-Bold_1.ttf");

        //Apply click Listeners
        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        if(!GlobalConfig_Methods.isValidList(mListVoiceMessages) || mListVoiceMessages.get(0) == null){
            return;
        }

        mListVoiceMessages.get(0).setCallName(getResources().getString(R.string.txtEmptyVoiceLogMessage));

        // Set adapter for ListView
        mAdapterVoiceMail = new VoiceMailListAdapter(getActivity(), mListVoiceMessages);
        lvVoiceMailList.setAdapter(mAdapterVoiceMail);

        lvVoiceMailList.setOnItemClickListener(this);

        llParentLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            if (mActivityTabs instanceof MainBaseActivity) {
                ((MainBaseActivity) mActivityTabs).fragmentManager.popBackStack();
            } else if (mActivityTabs instanceof HomeScreenActivity) {
                ((HomeScreenActivity) mActivityTabs).fragmentManager.popBackStack();
            }
        }else if(v.getId() == R.id.btnHome){
            getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
            getActivity().finish();
        }else if(v.getId() == R.id.llParentLayout){
            //disable background click
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mListVoiceMessages != null && mListVoiceMessages.size()>0){

            /*if(!mListVoiceMessages.get(0).getCallName().equalsIgnoreCase(
                    getResources().getString(R.string.txtEmptyVoiceLogmessage))){

                adapterSelected_position = position;
                mAdapterVoiceMail.setRowColor(adapterSelected_position, true);
            }*/

            try{
                if(position >= 0){
                    VoiceMailDetailsFragment mVoiceMailDetailsFragment = new VoiceMailDetailsFragment();
                    mVoiceMailDetailsFragment.newInstance(getActivity(),
                            mListVoiceMessages.get(position).getCallingNumber());
                    if (getActivity() instanceof MainBaseActivity) {
                        ((MainBaseActivity) getActivity())
                                .setFragment(mVoiceMailDetailsFragment);
                    } else if (getActivity() instanceof HomeScreenActivity) {
                        ((HomeScreenActivity) getActivity())
                                .setFragment(mVoiceMailDetailsFragment);
                    }
                }
            }catch(Exception e){
                e.getMessage();
            }
        }
    }
}
