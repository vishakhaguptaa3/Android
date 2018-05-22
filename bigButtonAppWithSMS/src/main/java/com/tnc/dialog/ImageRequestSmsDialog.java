package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.fragments.RegistrationFeatures;
import com.tnc.fragments.TilePreviewFragment;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ImageRequestSmsDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnOk;
	private 	Context mAct;
	private String title="",message="",messageSub="";
	private AlertCallAction alertBack;
	public boolean isFirstTime=false;
	private SharedPreference saveState;

	public ImageRequestSmsDialog newInstance(String title, Context mAct,String message,
			String messageSub,AlertCallAction alertBack,boolean isFirstTime,SharedPreference saveState)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		this.alertBack=alertBack;
		this.isFirstTime=isFirstTime;
		this.saveState=new SharedPreference();
		this.saveState=saveState;
		this.setCancelable(false);
		ImageRequestSmsDialog frag = new ImageRequestSmsDialog();
		Bundle args = new Bundle();
		//		args.putString("title", title);
		frag.setArguments(args);
		return frag;
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
		window.setBackgroundDrawableResource(android.R.color.transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.registrationdetaildialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnOk = (Button) view.findViewById(R.id.btnOk);
		CustomFonts.setFontOfButton(getActivity(),btnOk, "fonts/Roboto-Bold_1.ttf");
		btnOk.setOnClickListener(this);
		updateView();
		return view;
	}

	private void updateView() 
	{
		tvTitle.setText(title);
		tvMessage.setText(message);
		tvMessageSub.setText(messageSub);
		if(title.trim().equals(""))
		{
			tvTitle.setVisibility(View.GONE);
		}
		else{
			tvTitle.setVisibility(View.VISIBLE);
		}
		if(messageSub.trim().equals(""))
		{
			tvMessageSub.setVisibility(View.GONE);
		}
		else{
			tvMessageSub.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnOk)
		{
//			if(title.contains("THANK YOU"))
//			{
//				if(mAct instanceof MainBaseActivity)
//				{
//					((MainBaseActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
//					((MainBaseActivity)mAct).finish();
//				}
//				else if(mAct instanceof HomeScreenActivity)
//				{
//					((HomeScreenActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
//					((HomeScreenActivity)mAct).finish();
//				}
//
//				if(GlobalCommonValues.TelephoneNumberTobeDisplayed!=null && 
//						!GlobalCommonValues.TelephoneNumberTobeDisplayed.trim().equals(""))
//					GlobalCommonValues.TelephoneNumberTobeDisplayed="";
//			}
//			else
//			{
				saveState=new SharedPreference();
				if(MainBaseActivity.isImageRequested && !saveState.isRegistered(mAct))
				{
					if(mAct instanceof MainBaseActivity)
					{
						int num=((MainBaseActivity)mAct).fragmentManager.getBackStackEntryCount();
						BackStackEntry backStackEntry=((MainBaseActivity)mAct).fragmentManager.getBackStackEntryAt(num-1);
						if(backStackEntry.getName().contains("TilePreview"))
						{
							((MainBaseActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
							((MainBaseActivity)mAct).finish();
						}
						else
						{
							((MainBaseActivity)mAct).objFragment=new TilePreviewFragment();
							((MainBaseActivity)mAct).setFragment(((MainBaseActivity)mAct).objFragment);
						}
					}
					else if(mAct instanceof HomeScreenActivity)
					{
						int num=((HomeScreenActivity)mAct).fragmentManager.getBackStackEntryCount();
						BackStackEntry backStackEntry=((HomeScreenActivity)mAct).fragmentManager.getBackStackEntryAt(num-1);
						if(backStackEntry.getName().contains("TilePreview"))
						{
							((HomeScreenActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
							((HomeScreenActivity)mAct).finish();
						}
						else
						{
							((HomeScreenActivity)mAct).objFragment=new TilePreviewFragment();
							((HomeScreenActivity)mAct).setFragment(((HomeScreenActivity)mAct).objFragment);
						}
					}
				}
				else if(!MainBaseActivity.isImageRequested && saveState.isRegistered(mAct))
				{
					if(mAct instanceof MainBaseActivity)
					{
						int num=((MainBaseActivity)mAct).fragmentManager.getBackStackEntryCount();
						BackStackEntry backStackEntry=((MainBaseActivity)mAct).fragmentManager.getBackStackEntryAt(num-1);
						if(backStackEntry.getName().contains("TilePreview"))
						{
							((MainBaseActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
							((MainBaseActivity)mAct).finish();
						}
						else
						{
							((MainBaseActivity)mAct).objFragment=new TilePreviewFragment();
							((MainBaseActivity)mAct).setFragment(((MainBaseActivity)mAct).objFragment);
						}
					}
					else if(mAct instanceof HomeScreenActivity)
					{
						int num=((HomeScreenActivity)mAct).fragmentManager.getBackStackEntryCount();
						BackStackEntry backStackEntry=((HomeScreenActivity)mAct).fragmentManager.getBackStackEntryAt(num-1);
						if(backStackEntry.getName().contains("TilePreview"))
						{
							((HomeScreenActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
							((HomeScreenActivity)mAct).finish();
						}
						else
						{
							((HomeScreenActivity)mAct).objFragment=new TilePreviewFragment();
							((HomeScreenActivity)mAct).setFragment(((HomeScreenActivity)mAct).objFragment);
						}
					}
				}
				else if(!MainBaseActivity.isImageRequested && !saveState.isRegistered(mAct))
				{
					if(mAct instanceof MainBaseActivity)
					{
						int num=((MainBaseActivity)mAct).fragmentManager.getBackStackEntryCount();
						BackStackEntry backStackEntry=((MainBaseActivity)mAct).fragmentManager.getBackStackEntryAt(num-1);
						if(backStackEntry.getName().contains("TilePreview") && !saveState.isRegistered(mAct))
						{
							((MainBaseActivity)mAct).setFragment(new RegistrationFeatures());
						}
						else
						{
							((MainBaseActivity)mAct).objFragment=new TilePreviewFragment();
							((MainBaseActivity)mAct).setFragment(((MainBaseActivity)mAct).objFragment);
						}
					}
					else if(mAct instanceof HomeScreenActivity)
					{
						int num=((HomeScreenActivity)mAct).fragmentManager.getBackStackEntryCount();
						BackStackEntry backStackEntery=((HomeScreenActivity)mAct).fragmentManager.getBackStackEntryAt(num-1);
						if(backStackEntery.getName().contains("TilePreview") && !saveState.isRegistered(mAct))
						{
							((HomeScreenActivity)mAct).setFragment(new RegistrationFeatures());
						}
						else
						{
							((HomeScreenActivity)mAct).objFragment=new TilePreviewFragment();
							((HomeScreenActivity)mAct).setFragment(((HomeScreenActivity)mAct).objFragment);
						}
					}
				}
				else
				{
					if(saveState.isRegistered(mAct) && MainBaseActivity.isImageRequested)
					{
						if(mAct instanceof MainBaseActivity)
						{
							((MainBaseActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
							((MainBaseActivity)mAct).finish();
//							((MainBaseActivity)mAct).runOnUiThread(new Runnable() {
//						        @Override
//						        public void run() {
//						            Toast.makeText(getActivity(), "Main", Toast.LENGTH_SHORT).show();
//						        }
//						    });

//							((MainBaseActivity)mAct).objFragment=new TilePreviewFragment();
//							((MainBaseActivity)mAct).setFragment(((MainBaseActivity)mAct).objFragment);
						}
						else if(mAct instanceof HomeScreenActivity)
						{
							((HomeScreenActivity)mAct).startActivity(new Intent(mAct,HomeScreenActivity.class));
							((HomeScreenActivity)mAct).finish();
//							((HomeScreenActivity)mAct).runOnUiThread(new Runnable() {
//						        @Override
//						        public void run() {
//						            Toast.makeText(getActivity(), "Main", Toast.LENGTH_SHORT).show();
//						        }
//						    });

//							((HomeScreenActivity)mAct).objFragment=new TilePreviewFragment();
//							((HomeScreenActivity)mAct).setFragment(((HomeScreenActivity)mAct).objFragment);
						}
					}
					else
					{	
						if(!saveState.isRegistered(mAct))
						{
							if(mAct instanceof MainBaseActivity)
							{
								((MainBaseActivity)mAct).setFragment(new RegistrationFeatures());
							}
							else if(mAct instanceof HomeScreenActivity)
							{
								((HomeScreenActivity)mAct).setFragment(new RegistrationFeatures());
							}
						}
						else
						{
							getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
							getActivity().finish();
						}
					}
				}
//			}
				if(GlobalCommonValues.TelephoneNumberTobeDisplayed!=null && 
						!GlobalCommonValues.TelephoneNumberTobeDisplayed.trim().equals(""))
					GlobalCommonValues.TelephoneNumberTobeDisplayed="";
		}
	}
}