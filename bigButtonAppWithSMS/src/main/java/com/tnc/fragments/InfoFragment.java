package com.tnc.fragments;
/*package com.bigbutton.fragments;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.bigbutton.R;
import com.bigbutton.adapter.InfoMenuAdapter;
import com.bigbutton.base.BaseFragmentTabs;
import com.bigbutton.common.CustomFonts;
import com.bigbutton.common.TransparentProgressDialog;
import com.bigbutton.homescreen.HomeScreenActivity;
import com.bigbutton.interfaces.INotifyGalleryDialog;
import com.bigbutton.preferences.SharedPreference;
import com.google.gson.Gson;

public class InfoFragment extends BaseFragmentTabs{

	SharedPreference saveState;
	TransparentProgressDialog progress;
	Gson gson;
	TextView tvTitle,tvInfo;
	ListView lvInfoMenu;
	FrameLayout flBackArrow;
	Button btnBack;
	Context mActivity;
	INotifyGalleryDialog iNotifyRefreshSelectedTab;
	InfoMenuAdapter adapterInfoMenu;
	int adapterSelected_position = 0;
	ArrayList<String> listInfoMenu=new ArrayList<String>();

	public InfoFragment newInstance(Context mActivity,INotifyGalleryDialog iNotifyRefreshSelectedTab)
	{
		InfoFragment frag = new InfoFragment();
		this.mActivity=mActivity;
		this.iNotifyRefreshSelectedTab=iNotifyRefreshSelectedTab;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.infoscreen,container, false);
		idInitialization(view);
		return view;
	}

	@SuppressLint("ClickableViewAccessibility")
	private void idInitialization(View view) {
		saveState = new SharedPreference();
		progress = new TransparentProgressDialog(mActivityTabs,R.drawable.customspinner);
		gson = new Gson();
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvInfo=(TextView) view.findViewById(R.id.tvInfo);
		lvInfoMenu=(ListView) view.findViewById(R.id.lvInfoMenu);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		flBackArrow.setVisibility(View.VISIBLE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvInfo, "fonts/Roboto-Bold_1.ttf");
		if(listInfoMenu.isEmpty())
		{
			listInfoMenu.add("How to...");
			listInfoMenu.add("Rate the App");
			listInfoMenu.add("International Emergency Numbers");
			listInfoMenu.add("Share on Social Media");
			listInfoMenu.add("Privacy Policy");
			listInfoMenu.add("Contact Us");
		}

		adapterInfoMenu=new InfoMenuAdapter(mActivity,listInfoMenu);
		lvInfoMenu.setAdapter(adapterInfoMenu);

		lvInfoMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				adapterSelected_position = position;
				adapterInfoMenu.setRowColor(adapterSelected_position, true);
				if(position==2)
				{
					InternationalEmergencyNumbersFragment objIntlNumberFragment=new InternationalEmergencyNumbersFragment();
					objIntlNumberFragment.newInstance(mActivity);
					if(mActivity instanceof HomeScreenActivity)
					{
						((HomeScreenActivity)mActivity).setFragment(objIntlNumberFragment);
					}
				}
				else if(position==3)
				{
					//Share on Social Media
					
					
				}
				else if(position==5)
				{
					ContactUsFragment objContactusFragment=new ContactUsFragment();
					objContactusFragment.newInstance(mActivity);
					((HomeScreenActivity)mActivity).setFragment(objContactusFragment);
				}
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
			}
		});
	}

	*//**
	 * interface to notify the list of deselecting the selected position
	 *//*
	INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			adapterInfoMenu.setRowColor(adapterSelected_position, false); 
		}

		@Override
		public void no() {
			adapterInfoMenu.setRowColor(adapterSelected_position, false); 
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mActivityTabs instanceof HomeScreenActivity)
		{
			try {
				((HomeScreenActivity)mActivityTabs).getUnreadNotificationCount();
			} catch (Exception e) {
				e.getMessage();
			}
			if(iNotifyRefreshSelectedTab!=null)
				iNotifyRefreshSelectedTab.yes();
		}
	}
}
*/