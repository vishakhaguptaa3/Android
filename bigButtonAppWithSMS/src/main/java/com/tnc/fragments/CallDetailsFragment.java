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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.CallDetailsAdapter;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.CallDetailsBean;
import com.tnc.bean.UserCallLogDataBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * class : CallDetailsFragment
 * description : This class is used to display Dialled Call Numbers History from the App
 * Created by a3logics on 2/12/16.
 */

public class CallDetailsFragment extends BaseFragmentTabs {

    private LinearLayout llParentLayout;
    private FrameLayout flBackArrow;
    private TextView tvTitle,tvHeading,tvMissedCallCount;
    private Button btnBack, btnDialPad;
    private ListView lvCallDetails;
    private CallDetailsAdapter adapterCallDetails;
    //private ArrayList<UserCallLogDataBean> m1ListUserCallLogDataBean;
    private ArrayList<CallDetailsBean> mListUserCallLogDataBean;
    private List<CallDetailsBean> mListUserCallLogFiltered ;
    private int adapterSelected_position = 0;
    private Context mContext;
    private RadioGroup radioGroupCallType;
    private RadioButton rbIncomingCalls, rbOutgoingCalls, rbMissedCalls;
    private boolean isFirstTime = true;
    private int callType = 1;
    private INotifyGalleryDialog iNotifySelectTab;

    public CallDetailsFragment newInstance(Context mContext, INotifyGalleryDialog iNotifySelectTab){
        CallDetailsFragment frag = new CallDetailsFragment();
        this.mContext = mContext;
        this.iNotifySelectTab = iNotifySelectTab;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_details_fragment, container, false);
        idInitialization(view);
        return view;
    }

    // Method to initialize views/widgets
    private void idInitialization(View view) {
        saveState = new SharedPreference();
        llParentLayout=(LinearLayout) view.findViewById(R.id.llParentLayout);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvHeading = (TextView) view.findViewById(R.id.tvHeading);
        tvMissedCallCount = (TextView) view.findViewById(R.id.tvMissedCallCount);
        flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
        flBackArrow.setVisibility(View.VISIBLE);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnDialPad = (Button) view.findViewById(R.id.btnDialPad);

        radioGroupCallType  = (RadioGroup) view.findViewById(R.id.radioGroupCallType);

        rbIncomingCalls     = (RadioButton) view.findViewById(R.id.rbIncomingCalls);
        rbOutgoingCalls     = (RadioButton) view.findViewById(R.id.rbOutgoingCalls);
        rbMissedCalls       = (RadioButton) view.findViewById(R.id.rbMissedCalls);

        lvCallDetails = (ListView) view.findViewById(R.id.lvCallDetails);
        CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/comic_sans_ms_regular.ttf");
        CustomFonts.setFontOfTextView(getActivity(), tvHeading, "fonts/Roboto-Bold_1.ttf");

        tvHeading.setText(getResources().getString(R.string.txtCallHistory));

        mListUserCallLogDataBean = new ArrayList<CallDetailsBean>();

        llParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // get call list Details if first time
        if(isFirstTime){
            isFirstTime = false;
            rbIncomingCalls.setChecked(true);
            setCallLogs(callType);
        }

        radioGroupCallType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if(checkedId == R.id.rbIncomingCalls){
                    callType = 1;
                }else if(checkedId == R.id.rbOutgoingCalls){
                    callType = 2;
                }else if(checkedId == R.id.rbMissedCalls){
                    callType = 3;
                }
                // get call list Details
                setCallLogs(callType);
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

        btnDialPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialPad objDialPad=new DialPad();
                objDialPad.newInstance(getActivity(),null);
                ((HomeScreenActivity)getActivity()).setFragment(objDialPad);
            }
        });

        // Handle click event of List item click
        lvCallDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(mListUserCallLogDataBean != null && mListUserCallLogDataBean.size()>0){

                    if(!mListUserCallLogDataBean.get(0).getCallName().equalsIgnoreCase(
                            getResources().getString(R.string.txtEmptyCallLogmessage))){

                        adapterSelected_position = position;
                        adapterCallDetails.setRowColor(adapterSelected_position, true);

                        if(mListUserCallLogDataBean.get(position).getCallingNumber()!=null &&
                                !mListUserCallLogDataBean.get(position).getCallingNumber().trim().equalsIgnoreCase("")){

                            /*String mSelectedNumber = mListUserCallLogDataBean.get(position).getCallingNumber();

                            GlobalConfig_Methods.makePhoneCall(getActivity(), mSelectedNumber);*/

                            try{
                                // update call log status as read if it is unread
                                if(!(mListUserCallLogDataBean.get(position).getStatus() == 2))
                                    DBQuery.updateCallLogStatus(getActivity(),mListUserCallLogDataBean.get(position).getCallingNumber());
                            }catch(Exception e){
                                e.getMessage();
                            }

                            try{
                                if(position >= 0){
                                    CallLogDetailsScreenFragment mCallLogDetailsScreenFragment = new CallLogDetailsScreenFragment();
                                    mCallLogDetailsScreenFragment.newInstance(mContext,
                                            mListUserCallLogDataBean.get(position).getCallingNumber(),
                                            mListUserCallLogDataBean.get(position).isTncUser());
                                    if (mContext instanceof MainBaseActivity) {
                                        ((MainBaseActivity) mContext)
                                                .setFragment(mCallLogDetailsScreenFragment);
                                    } else if (mContext instanceof HomeScreenActivity) {
                                        ((HomeScreenActivity) mContext)
                                                .setFragment(mCallLogDetailsScreenFragment);
                                    }
                                }
                            }catch(Exception e){
                                e.getMessage();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        iNotifyGalleryDialog.yes();
        if(callType >= 1)
            setCallLogs(callType);
    }

    /**
     * fetch list of missed calls form the phone and display it in list adapter
     */
    /**
     * @author    a3logics
     * @version   1.5        (current version number of program)
     * @since     1.5        (the version of the package this class was first added to)
     */
    private void setCallLogs(int callType){

        // Get Call Logs
        mListUserCallLogDataBean = DBQuery.getcallLogsFromType(getActivity(), callType);

        if(mListUserCallLogDataBean == null || mListUserCallLogDataBean.size() == 0) {
            CallDetailsBean mCallDetailObject = new CallDetailsBean();
            mCallDetailObject.setCallName(getResources().getString(R.string.txtEmptyCallLogmessage));
            mListUserCallLogDataBean.add(mCallDetailObject);
        }

        adapterCallDetails = new CallDetailsAdapter(getActivity(),mListUserCallLogDataBean);
        lvCallDetails.setAdapter(adapterCallDetails);

        // Call Method to update missed calls count
        setMissedCallsCount();

        //1 - incoming, 2 - outgoing, 3 - missed
        /*mListUserCallLogFiltered = new ArrayList<CallDetailsBean>();

        if(mListUserCallLogDataBean!=null && mListUserCallLogDataBean.size() > 0){
            for(CallDetailsBean mCallDetailBean : mListUserCallLogDataBean){
                if(mCallDetailBean.getCallType()>0 && mCallDetailBean.getCallType() == callType){
                    mListUserCallLogFiltered.add(mCallDetailBean);
                }
            }
        }*/

        // in case of incoming calls
        /*mListUserCallLogFiltered = mListUserCallLogDataBean.stream()
                .filter(p -> p.getCallType() == callType).collect(Collectors.toList());*/

        /*mListUserCallLogDataBean =
                GlobalConfig_Methods.getMissedCallsUnread(getActivity(), getActivity().getContentResolver());
        */
    }

    /**
     * Method to update missed calls count
     */
    private void setMissedCallsCount(){

        //get missed calls count from database
        // Display Unread Call From a Log and to display the Indicator Logic
        int missedCallCount = 0;

        try{
            missedCallCount = DBQuery.getUnreadCallCount(mContext, String.valueOf(GlobalCommonValues.CallTypeMissed));
        }catch(Exception e){
            e.getMessage();
        }

        if(missedCallCount>0){
            tvMissedCallCount.setVisibility(View.VISIBLE);
            tvMissedCallCount.setText(String.valueOf(missedCallCount));
        }else{
            tvMissedCallCount.setVisibility(View.GONE);
        }
    }

    /**
     * interface to notify the list of deselecting the selected position
     */
    INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

        @Override
        public void yes() {
            if(adapterCallDetails!=null)
                adapterCallDetails.setRowColor(adapterSelected_position, false);
        }
        @Override
        public void no() {
            if(adapterCallDetails!=null)
                adapterCallDetails.setRowColor(adapterSelected_position, false);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(iNotifySelectTab!=null)
            iNotifySelectTab.yes();

        // Update and reset the missed call staus to 1
        /*if(mListUserCallLogDataBean != null && mListUserCallLogDataBean.size()>0){
            try{
                GlobalConfig_Methods.updateMissedCallsStatusToRead(getActivity());
            }catch(Exception e){
                e.getMessage();
            }
        }*/

    }
}
