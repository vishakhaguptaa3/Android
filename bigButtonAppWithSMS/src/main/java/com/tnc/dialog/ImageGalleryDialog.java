package com.tnc.dialog;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment.AlertCallAction;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ImageGalleryDialog extends DialogFragment implements OnClickListener
{
	private TextView tvTitle,tvMessage,tvMessageSub;
	public Button btnYes,btnNo;
	private Context mAct;
	private String title="",message="",messageSub="";
	private AlertCallAction alertBack;
	private INotifyGalleryDialog iNotifyGalleryDialog;
	//	public boolean isOkClicked=false;

	public ImageGalleryDialog newInstance(String title, Context mAct,String message,
										  String messageSub,AlertCallAction alertBack, INotifyGalleryDialog iNotifyGalleryDialog)
	{
		this.mAct = mAct;
		this.title=title;
		this.message=message;
		this.messageSub=messageSub;
		this.alertBack=alertBack;
		ImageGalleryDialog frag = new ImageGalleryDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.iNotifyGalleryDialog = iNotifyGalleryDialog;
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
		View view = inflater.inflate(R.layout.imagegallerydialog, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvMessage=(TextView) view.findViewById(R.id.tvMessage);
		tvMessageSub=(TextView) view.findViewById(R.id.tvMessageSub);
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo= (Button) view.findViewById(R.id.btnNo);
		//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Roboto-Bold_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessage, "fonts/Roboto-Regular_1.ttf");
		//		CustomFonts.setFontOfTextView(getActivity(),tvMessageSub, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnYes, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNo, "fonts/Roboto-Bold_1.ttf");
		btnYes.setOnClickListener(this);
		btnNo.setOnClickListener(this);
		updateView();
		return view;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(iNotifyGalleryDialog!=null)
			iNotifyGalleryDialog.no();
	}

	private void updateView()
	{
		tvTitle.setText(title);
		tvMessage.setText(message);
		if(tvTitle.getText().toString().equals(""))
			tvTitle.setVisibility(View.GONE);
		if(message!=null && !message.equals(""))
		{
			tvMessage.setText(message);
		}
		if(messageSub!=null && !messageSub.equals(""))
		{
			tvMessageSub.setText(messageSub);
		}
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
		if(v.getId()==R.id.btnYes)
		{
			if(message.contains("messaging service"))
			{
				this.dismiss();

				try {
					if(isAirplaneModeOn(mAct)){
						showSimErrorDialog("The device is in Airplane mode or has no sim card or outside of cellular service range");
					}else{

						TelephonyManager telMgr = (TelephonyManager)mAct.getSystemService(Context.TELEPHONY_SERVICE);
						int simState = telMgr.getSimState();
						switch (simState) {
							case TelephonyManager.SIM_STATE_ABSENT:
								showSimErrorDialog("No sim present");

								break;
							case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
								// do something
								showSimErrorDialog("Sim network is lock");
								break;
							case TelephonyManager.SIM_STATE_PIN_REQUIRED:
								// do something
								showSimErrorDialog("The device is in Airplane mode or has no sim card or outside of cellular service range");
								break;
							case TelephonyManager.SIM_STATE_PUK_REQUIRED:
								// do something
								showSimErrorDialog("No sim present");
								break;
							case TelephonyManager.SIM_STATE_READY:
								//MainBaseActivity.selectedContactNumber.trim().length()==7 ||
								//phase-4
								SendMessagePopupDialog dialogSendSMS=new SendMessagePopupDialog();

								if(mAct instanceof MainBaseActivity)
								{
									dialogSendSMS.newInstance("", ((MainBaseActivity)mAct),"","", alertBack);
									dialogSendSMS.show(((MainBaseActivity)mAct).getSupportFragmentManager(), "Test");
								}
								else if(mAct instanceof HomeScreenActivity)
								{
									dialogSendSMS.newInstance("", ((HomeScreenActivity)mAct),"","", alertBack);
									dialogSendSMS.show(((HomeScreenActivity)mAct).getSupportFragmentManager(), "Test");
								}

								break;
							case TelephonyManager.SIM_STATE_UNKNOWN:
								showSimErrorDialog("No sim present");
								break;
						}
					}
					if(iNotifyGalleryDialog!=null)
						iNotifyGalleryDialog.yes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else if(v.getId()==R.id.btnNo)
		{
			this.dismiss();
			if(iNotifyGalleryDialog!=null)
				iNotifyGalleryDialog.no();
		}
	}

	public void showSimErrorDialog(String msg){
		ImageRequestDialog dialog2=new ImageRequestDialog();
		if(mAct instanceof MainBaseActivity)
		{
			dialog2.newInstance("", (MainBaseActivity)mAct,msg ,"",
					null, iNotifyGalleryDialog);
			dialog2.show(((MainBaseActivity)mAct).getSupportFragmentManager(), "test");
		}
		else if(mAct instanceof HomeScreenActivity)
		{
			dialog2.newInstance("", (HomeScreenActivity)mAct,msg ,"",
					null, iNotifyGalleryDialog);
			dialog2.show(((HomeScreenActivity)mAct).getSupportFragmentManager(), "test");
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean isAirplaneModeOn(Context context) throws Exception {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return Settings.System.getInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		} else {
			return Settings.Global.getInt(context.getContentResolver(),
					Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
		}
	}
}