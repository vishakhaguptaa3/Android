package com.tnc.base;

import com.tnc.MainBaseActivity;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * second Base class for the fragments while the other is basefragment
 * @author a3logics
 *
 */


public class BaseFragmentTabs extends Fragment 
{
	public Activity mActivityTabs;
	public SharedPreference saveState;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		try{
			mActivityTabs = (HomeScreenActivity)this.getActivity();
		}
		catch(Exception e)
		{
			mActivityTabs = (MainBaseActivity)this.getActivity();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
