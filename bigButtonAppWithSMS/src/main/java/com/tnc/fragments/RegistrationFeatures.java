package com.tnc.fragments;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.activities.YouTubeVideoViewerActivity;
import com.tnc.base.BaseFragment;
import com.tnc.bean.ChatUserDetailBean;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.dialog.BackupConfirmationDialog;
import com.tnc.dialog.ContactShareAcceptConfirmationDialog;
import com.tnc.dialog.ImageGalleryDialog;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * class to display registration fetaures/benefits of the app 
 *  @author a3logics
 */

public class RegistrationFeatures extends BaseFragment implements OnClickListener
{
    private TextView tvTitle,tvRegistrationMessage,tvRegBenefitFirst,tvRegBenefitSecond,tvRegBenefitThird,
            tvRegBenefitFourth,tvRegBenefitFifth,tvRegisterConfirmation;
    private Button btnYes,btnNo;
    private String title;
    private int VIDEO_REQUEST_ACTION = 1800;


    public void setINotificationDialog(ImageGalleryDialog imageGalleryDialog){
        this.imageGalleryDialog = imageGalleryDialog;
    }

    public RegistrationFeatures newInstance(String title)
    {
        this.title=title;
        RegistrationFeatures frag = new RegistrationFeatures();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.registrationfeatures, container, false);
        idInitialization(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(HomeScreenActivity.btnBack!=null)
        {
            HomeScreenActivity.btnBack.setEnabled(false);
            HomeScreenActivity.btnBack.setClickable(false);
        }
        if(HomeScreenActivity.btnDisable!=null)
            HomeScreenActivity.btnDisable.setEnabled(false);
        MainBaseActivity._bitmap=null;
        MainBaseActivity._bitmapContact=null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(HomeScreenActivity.btnBack!=null)
        {
            HomeScreenActivity.btnBack.setEnabled(true);
            HomeScreenActivity.btnBack.setClickable(true);
        }
        if(mActivity instanceof MainBaseActivity)
        {
            if(((MainBaseActivity)mActivity)!=null)
            {
                if (NetworkConnection.isNetworkAvailable(mActivity))
                {
                    if(saveState.getGCMRegistrationId(getActivity()).equals(""))
                    {
                        ((MainBaseActivity)mActivity).setGCMRegID();
                    }
                }
            }
        }
    }

    // Method to initialize views/widgets
    private void idInitialization(View view)
    {
        saveState=new SharedPreference();
        tvTitle=(TextView) view.findViewById(R.id.tvTitle);
        tvRegistrationMessage=(TextView) view.findViewById(R.id.tvRegistrationMessage);
        tvRegBenefitFirst=(TextView)view.findViewById(R.id.tvRegBenefitFirst);
        tvRegBenefitSecond=(TextView)view.findViewById(R.id.tvRegBenefitSecond);
        tvRegBenefitThird=(TextView)view.findViewById(R.id.tvRegBenefitThird);
        tvRegBenefitFourth=(TextView)view.findViewById(R.id.tvRegBenefitFourth);
        tvRegBenefitFifth=(TextView)view.findViewById(R.id.tvRegBenefitFifth);
        tvRegisterConfirmation=(TextView) view.findViewById(R.id.tvRegisterConfirmation);
        btnYes=(Button)view.findViewById(R.id.btnYes);
        btnNo=(Button)view.findViewById(R.id.btnNo);
        CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegistrationMessage, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitFirst, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitSecond, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitThird, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitFourth, "fonts/Roboto-Regular_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegBenefitFifth, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfTextView(getActivity(),tvRegisterConfirmation, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
        tvRegistrationMessage.setText(getActivity().getResources().getString(R.string.txtRegistrationFeatureTitle));
        tvRegisterConfirmation.setText(getActivity().getResources().getString(R.string.txtWouldYouLikeToRegister));
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        
//        tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.btnYes)
        {
            ContactShareAcceptConfirmationDialog mDialog = new ContactShareAcceptConfirmationDialog();
            mDialog.newInstance(getActivity().getResources().getString(R.string.app_name),getActivity(),
            		getActivity().getResources().getString(R.string.txtRegistrationVideoPopup),
                    "",iNotifyVideoViewAction);
            mDialog.setCancelable(false);
            mDialog.show(getChildFragmentManager(),"Test");
        }
        else if(v.getId()==R.id.btnNo)
        {
            if(mActivity instanceof MainBaseActivity)
            {
                ((MainBaseActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
                getActivity().finish();
            }
            else if(mActivity instanceof HomeScreenActivity)
            {
                ((HomeScreenActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
                getActivity().finish();
            }
        }
    }

    /**
     * interface to handle user actions to watch the intro video's accept/decline
     */
    INotifyGalleryDialog iNotifyVideoViewAction = new INotifyGalleryDialog() {
        @Override
        public void yes() {
            // in case user selects yes to watch the registration video
            Intent intent = new Intent(getActivity(), YouTubeVideoViewerActivity.class);
            intent.putExtra("video_type","registration_video");
            startActivityForResult(intent,VIDEO_REQUEST_ACTION);

        }

        @Override
        public void no() {
            // in case user selects no he don't want to watch the registration video then
            // navigate user to the parental consent screen
            if(mActivity instanceof MainBaseActivity){
                ((MainBaseActivity)mActivity).setFragment(new ParentalConsentFragment());
            }
            else if(mActivity instanceof HomeScreenActivity){
                ((HomeScreenActivity)mActivity).setFragment(new ParentalConsentFragment());
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //		if(resultCode==Activity.RESULT_OK)
        if(requestCode == VIDEO_REQUEST_ACTION){
            if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("status")){
                Bundle bundle = data.getExtras();
                String status  = bundle.getString("status");
                if(status.equalsIgnoreCase("done")){
                    // navigate user to the parental consent screen
                    if(mActivity instanceof MainBaseActivity){
                        ((MainBaseActivity)mActivity).setFragment(new ParentalConsentFragment());
                    }
                    else if(mActivity instanceof HomeScreenActivity){
                        ((HomeScreenActivity)mActivity).setFragment(new ParentalConsentFragment());
                    }
                }
            }
        }
    }

}
