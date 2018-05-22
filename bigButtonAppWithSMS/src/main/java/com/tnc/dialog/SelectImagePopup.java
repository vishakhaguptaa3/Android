package com.tnc.dialog;

import com.baoyz.actionsheet.ActionSheet;
import com.facebook.CallbackManager;
import com.tnc.MainBaseActivity;
import com.tnc.SocialNetwork.Facebook.FaceBookApiResponse;
import com.tnc.SocialNetwork.Facebook.FaceBookClass;
import com.tnc.SocialNetwork.Facebook.FacebookOnTaskComplete;
import com.tnc.SocialNetwork.Google.GoogleClass;
import com.tnc.SocialNetwork.Google.GoogleUserDetailModel;
import com.tnc.SocialNetwork.Google.GoogleUserInfoInterface;
import com.tnc.fragments.UserInfoFragment;
import com.tnc.fragments.UserRegistrationFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

public class SelectImagePopup extends Activity implements ActionSheet.ActionSheetListener
{
	private TextView tvTitle,tvCamera,tvGallery;
	private Activity mActivity;
	private String title="";
	private Button btnCancel;
	private INotifyGalleryDialog iObject;
	private Fragment mFragment;
	private String[] optionList1 = {"Select Image", "Take New Photo", "From Gallery", "Google +","FaceBook"};
	private String[] optionList2 = {"Select Image", "Take New Photo", "From Gallery"};
	private CallbackManager callbackManager;

	public SelectImagePopup newInstance(String title, Activity mActivity, INotifyGalleryDialog iObject, Fragment fragment)
	{
		this.title=title;
		this.iObject=iObject;
		mFragment = fragment;
		this.mActivity = mActivity;
		if(mActivity instanceof MainBaseActivity){
			callbackManager = ((MainBaseActivity)mActivity).getCallbackManager();
		}else if(mActivity instanceof HomeScreenActivity){
			callbackManager = ((HomeScreenActivity)mActivity).getCallbackManager();
		}

		SelectImagePopup frag = new SelectImagePopup();
		Bundle args = new Bundle();
		if(fragment instanceof UserInfoFragment || fragment instanceof UserRegistrationFragment){
			createActionSheet(optionList1);
		}else{
			createActionSheet(optionList2);
		}
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//callbackManager = CallbackManager.Factory.create();
	}
//
//	@Override
//	public void onStart()
//	{
//		super.onStart();
//		Window window = getDialog().getWindow();
//		WindowManager.LayoutParams params = window.getAttributes();
//		params.dimAmount = 0.6f;
//		window.setAttributes(params);
//		window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
//		window.setBackgroundDrawableResource(android.R.color.transparent);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
//	{
//		View view = inflater.inflate(R.layout.selectimagepopup, container);
//		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//		tvCamera=(TextView)view.findViewById(R.id.tvCamera);
//		tvGallery=(TextView)view.findViewById(R.id.tvGallery);
//		btnCancel = (Button) view.findViewById(R.id.btnCancel);
////		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
////		CustomFonts.setFontOfTextView(getActivity(),tvCamera, "fonts/Roboto-Regular_1.ttf");
////		CustomFonts.setFontOfTextView(getActivity(),tvGallery, "fonts/Roboto-Regular_1.ttf");
//		CustomFonts.setFontOfButton(getActivity(),btnCancel, "fonts/Roboto-Bold_1.ttf");
//		tvTitle.setText("Select Image");
//		tvCamera.setText("Take New Photo");
//		tvGallery.setText("From Gallery");
//		tvCamera.setOnClickListener(this);
//		tvGallery.setOnClickListener(this);
//		btnCancel.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				dismiss();
//			}
//		});
//		return view;
//	}
//
//	@Override
//	public void onClick(View v) {
//		if(v.getId()==R.id.tvCamera)
//		{
//			dismiss();
//			iObject.yes();
//		}
//		else if(v.getId()==R.id.tvGallery)
//		{
//			dismiss();
//			iObject.no();
//		}
//	}

	void createActionSheet(String[] strings){
		ActionSheet.createBuilder(mActivity, mFragment.getFragmentManager())
				.setCancelButtonTitle("Cancel")
				.setOtherButtonTitles(strings)
				.setCancelableOnTouchOutside(true)
				.setListener(this).show();

	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {


	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch (index){
			case 1:
				iObject.yes();
				break;
			case 2:
				iObject.no();
				break;

			case 4:
				//((UserInfoFragment)mFragment).loginFacebook();
				FaceBookClass faceBookClass = new FaceBookClass(mActivity, callbackManager, new FacebookOnTaskComplete() {
					@Override
					public void onTaskComplete(FaceBookApiResponse faceBookApiResponse) {
						if(mFragment instanceof UserInfoFragment) {
							((UserInfoFragment) mFragment).loginFaceBook(faceBookApiResponse);
						}else if(mFragment instanceof UserRegistrationFragment){
							((UserRegistrationFragment) mFragment).loginFaceBook(faceBookApiResponse);
						}
					}
				});
				faceBookClass.loginFacebook();
				break;
			case 3:
				//((UserInfoFragment)mFragment).gmailLogin();
				GoogleClass googleClass = new GoogleClass(mActivity);
				googleClass.googleLogin(false, new GoogleUserInfoInterface() {
					@Override
					public void taskOnComplete(GoogleUserDetailModel googleUserDetailModel) {
						if(mFragment instanceof UserInfoFragment) {
							((UserInfoFragment) mFragment).setProfilePic(googleUserDetailModel.getPhotoUrl());
						}else if(mFragment instanceof UserRegistrationFragment){
							((UserRegistrationFragment) mFragment).setProfilePic(googleUserDetailModel.getPhotoUrl());
						}
					}
				});
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		callbackManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}


}
