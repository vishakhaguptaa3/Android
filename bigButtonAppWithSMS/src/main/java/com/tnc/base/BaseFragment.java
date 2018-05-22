package com.tnc.base;

import com.tnc.MainBaseActivity;
import com.tnc.dialog.ImageGalleryDialog;
import com.tnc.dialog.RegistrationDetailDialog;
import com.tnc.dialog.SMSChargeDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * first Base class for the fragments while the other is basefragmenttabs
 * @author a3logics
 *
 */

public class BaseFragment extends Fragment 
{
	public Activity mActivity;
	public SharedPreference saveState;
	public static RegistrationDetailDialog dialogRegistration;
	public SMSChargeDialog dialogSMSCharge=null;
	public ImageGalleryDialog imageGalleryDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try {
			mActivity = (MainBaseActivity) this.getActivity();
		} catch (Exception e) {
			mActivity = (HomeScreenActivity) this.getActivity();
		}
	}

	public interface AlertCallAction
	{
		public void isAlert(boolean isOkClikced);
	}

	protected AlertCallAction alertBack=new AlertCallAction() {
		@Override
		public void isAlert(boolean isOkClicked) 
		{
			if(BaseFragment.dialogRegistration!=null)
			{
				BaseFragment.dialogRegistration.dismiss();
				BaseFragment.dialogRegistration=null;
			}
			else if(dialogSMSCharge!=null)
			{
				dialogSMSCharge.dismiss();
				dialogSMSCharge=null;
			}
		}
	}; 
	public void callDialog(DialogFragment fragment) 
	{
		MainBaseActivity screen = (MainBaseActivity) getActivity();
		screen.callDialog(fragment);
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		BaseFragment.dialogRegistration=null;
	}
}
