package com.tnc.dialog;

import com.tnc.R;
import com.tnc.common.CustomFonts;
import com.tnc.interfaces.INotifyAction;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


/*public class ShareSocialSiteDialog extends Dialog implements android.view.View.OnClickListener{

    private TextView tvTitle,tvCamera,tvGallery,tvLinkedIn;
    private 	Context mActivity;
    private String title="";
    private Button btnCancel;
    private INotifyAction iObjectSocialSitePost;
    private INotifyGalleryDialog iNotifyGalleryDialog;

    public ShareSocialSiteDialog(String title, Context mActivity,INotifyAction iObjectSocialSitePost,
                                 INotifyGalleryDialog iNotifyGalleryDialog){
        super(mActivity);
        this.mActivity = mActivity;
        this.title=title;
        this.iObjectSocialSitePost=iObjectSocialSitePost;
        this.iNotifyGalleryDialog=iNotifyGalleryDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.share_social_site_dialog);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCamera=(TextView)findViewById(R.id.tvCamera);
        tvGallery=(TextView)findViewById(R.id.tvGallery);
        tvLinkedIn=(TextView)findViewById(R.id.tvLinkedIn);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        CustomFonts.setFontOfButton(mActivity,btnCancel, "fonts/Roboto-Bold_1.ttf");
        tvTitle.setText(mActivity.getResources().getString(R.string.txtShareSocialMediaMessage));
        tvCamera.setText(mActivity.getResources().getString(R.string.txtFacebook));
        tvGallery.setText(mActivity.getResources().getString(R.string.txtTwitter));
        tvLinkedIn.setText(mActivity.getResources().getString(R.string.txtLinkedin));
        tvLinkedIn.setVisibility(View.GONE);
        tvCamera.setOnClickListener(this);
        tvGallery.setOnClickListener(this);
        tvLinkedIn.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tvCamera)
        {
            dismiss();
            // In case of content to be posted on Facebook
            if(iObjectSocialSitePost!=null)
                iObjectSocialSitePost.setAction("facebook");
        }
        else if(v.getId()==R.id.tvGallery)
        {
            dismiss();
            // In case of content to be posted on Twitter
            if(iObjectSocialSitePost!=null)
                iObjectSocialSitePost.setAction("twitter");
        }
        else if(v.getId()==R.id.tvLinkedIn)
        {
            dismiss();
            // In case of content to be posted on Linkedin
            if(iObjectSocialSitePost!=null)
                iObjectSocialSitePost.setAction("linkedin");
        }else if(v==btnCancel){
            dismiss();
        }
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
        if(iNotifyGalleryDialog!=null);
        iNotifyGalleryDialog.yes();
    }
}*/



public class ShareSocialSiteDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvCamera,tvGallery,tvLinkedIn;
	private 	Context mActivity;
	private String title="";
	private Button btnCancel;
	private INotifyAction iObjectSocialSitePost;
	private INotifyGalleryDialog iNotifyGalleryDialog;

	public ShareSocialSiteDialog(){
	}

	public ShareSocialSiteDialog newInstance(String title, Context mActivity,INotifyAction iObjectSocialSitePost,
											 INotifyGalleryDialog iNotifyGalleryDialog)
	{
		this.mActivity = mActivity;
		this.title=title;
		this.iObjectSocialSitePost=iObjectSocialSitePost;
		this.iNotifyGalleryDialog=iNotifyGalleryDialog;
		ShareSocialSiteDialog frag = new ShareSocialSiteDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(iNotifyGalleryDialog!=null);
		iNotifyGalleryDialog.yes();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.dimAmount = 0.6f;
		window.setAttributes(params);
		window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.share_social_site_dialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvCamera=(TextView)view.findViewById(R.id.tvCamera);
		tvGallery=(TextView)view.findViewById(R.id.tvGallery);
		tvLinkedIn=(TextView)view.findViewById(R.id.tvLinkedIn);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		CustomFonts.setFontOfButton(getActivity(),btnCancel, "fonts/Roboto-Bold_1.ttf");
		tvTitle.setText("Share on Social Media");
		tvCamera.setText("Facebook");
		tvGallery.setText("Twitter");
		tvLinkedIn.setText("Linkedin");
		tvLinkedIn.setVisibility(View.GONE);
		tvCamera.setOnClickListener(this);
		tvGallery.setOnClickListener(this);
		tvLinkedIn.setOnClickListener(this);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tvCamera)
		{
			dismiss();
			// In case of content to be posted on Facebook
			if(iObjectSocialSitePost!=null)
				iObjectSocialSitePost.setAction("facebook");
		}
		else if(v.getId()==R.id.tvGallery)
		{
			dismiss();
			// In case of content to be posted on Twitter
			if(iObjectSocialSitePost!=null)
				iObjectSocialSitePost.setAction("twitter");
		}
		else if(v.getId()==R.id.tvLinkedIn)
		{
			dismiss();
			// In case of content to be posted on Linkedin
			if(iObjectSocialSitePost!=null)
				iObjectSocialSitePost.setAction("linkedin");
		}
	}
}
