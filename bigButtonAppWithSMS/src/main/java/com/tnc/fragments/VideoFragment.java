package com.tnc.fragments;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.activities.YouTubeVideoViewerActivity;
import com.tnc.base.BaseFragment;
import com.tnc.common.CustomFonts;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

public class VideoFragment extends BaseFragment implements OnClickListener
{
    private Button btnSkip;
    private ImageView btnClickplay;
    private MediaController mController;
    private TextView txtBigButton,txtAppInformation;
    //	boolean isFinished=false;
    private boolean isFirstTime=true;

    private int VIDEO_REQUEST_ACTION = 1800;

    public static VideoFragment newInstance()
    {
        VideoFragment fragmentFirst = new VideoFragment();
        Bundle args = new Bundle();
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.videofragment, container, false);

        saveState=new SharedPreference();

        idInitialization(view);

		/*//These four lines below were added as no intro video exists right now
		saveState=new SharedPreference();
		if(saveState.isFirst(mActivity))
		saveState.setFirstTime(mActivity, false);
		//((MainBaseActivity)mActivity).setFragment(new CreateCallButtonFragment());*/

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!isFirstTime)
            btnSkip.setText("CONTINUE");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        isFirstTime=false;
    }

    /*
     * Initialization of widgets/views
     */
    private void idInitialization(View view)
    {
        txtBigButton=(TextView) view.findViewById(R.id.txtBigButton);
        txtAppInformation=(TextView) view.findViewById(R.id.txtAppInformation);
        btnSkip=(Button)view.findViewById(R.id.btnSkip);
        btnClickplay=(ImageView) view.findViewById(R.id.btnClickplay);
        btnSkip.setOnClickListener(this);
        btnClickplay.setOnClickListener(this);
        CustomFonts.setFontOfTextView(mActivity.getApplicationContext(),txtBigButton, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(mActivity.getApplicationContext(),txtBigButton, "fonts/Helvetica-Bold.otf");
        //		CustomFonts.setFontOfTextView(mActivity.getApplicationContext(),txtBigButton,"fonts/StencilStd.otf");
        CustomFonts.setFontOfTextView(mActivity.getApplicationContext(),txtAppInformation,"fonts/Roboto-Bold_1.ttf");
        CustomFonts.setFontOfButton(getActivity(), btnSkip, "fonts/Roboto-Regular_1.ttf");

        
//        txtBigButton.setText(txtBigButton.getText().toString() + Html.fromHtml("&trade;"));
        
//        txtAppInformation.setText(txtAppInformation.getText().toString() + Html.fromHtml("&trade;"));
        
        saveState.setFirstTime(mActivity, false);

//		if(saveState.isFirst(mActivity))
//			saveState.setFirstTime(mActivity, false);
		/*if(!saveState.isFirst(mActivity))
		{
			btnClickplay.setEnabled(false);
			btnClickplay.setClickable(false);
		}
		else
		{
			btnClickplay.setEnabled(true);
			btnClickplay.setClickable(true);
		}*/
    }

	/*@Override
	public void onStop() 
	{
		super.onStop();
		saveState.setFirstTime(getActivity(), false);
	}*/

    /*@Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        saveState.setFirstTime(getActivity(), false);
    }*/
    //http://download.wavetlan.com/SVV/Media/HTTP/H264/Talkinghead_Media/H264_test1_Talkinghead_mp4_480x360.mp4
    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.btnClickplay)
        {
            isFirstTime = false;
            Intent intent = new Intent(getActivity(), YouTubeVideoViewerActivity.class);
            intent.putExtra("video_type","intro_video");
            startActivityForResult(intent,VIDEO_REQUEST_ACTION);
			/*Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri data=Uri.parse("http://pushnoti1.a3logics.in/bigbutton2/application/utility/video/introvideo.mp4");//("http://www.pocketjourney.com/downloads/pj/video/famous.3gp");
			intent.setDataAndType(data, "video/mp4");
			startActivityForResult(intent,1800);*/
        }
        else if(v.getId()==R.id.btnSkip)
        {
            if(btnSkip.getText().toString().equalsIgnoreCase("skip")){
                ImageRequestDialog mDialog = new ImageRequestDialog();
                mDialog.newInstance(getResources().getString(R.string.app_name),getActivity(),
                        getResources().getString(R.string.txtVideoPopupMessage),
                        "",mAlertCallBack);
                mDialog.setCancelable(false);
                mDialog.show(getChildFragmentManager(),"Test");
            }
            else if(btnSkip.getText().toString().equalsIgnoreCase("continue")){
                ((MainBaseActivity)mActivity).setFragment(new CreateCallButtonFragment());
            }
        }
    }

    /**
     * interface to handle popup event and navigate to the createcallfragment screen,
     * in case user skips the video
     */
    AlertCallAction mAlertCallBack = new AlertCallAction() {
        @Override
        public void isAlert(boolean isOkClikced) {
            if(isOkClikced){
                ((MainBaseActivity)mActivity).setFragment(new CreateCallButtonFragment());
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //		if(resultCode==Activity.RESULT_OK)
        if(requestCode == VIDEO_REQUEST_ACTION){
            isFirstTime=false;
            if(data!=null && data.getExtras()!=null && data.getExtras().containsKey("status")){
                Bundle bundle = data.getExtras();
                String status  = bundle.getString("status");
                if(status.equalsIgnoreCase("done")){
                    btnSkip.setText("CONTINUE");
                }
            }
        }
        //				isFirstTime=false;
    }
}
