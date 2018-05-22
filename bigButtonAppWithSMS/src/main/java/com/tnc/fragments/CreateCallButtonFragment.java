package com.tnc.fragments;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

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
 * class to ask user either to Create call button or Skip and do registration  
 *  @author a3logics
 */

public class CreateCallButtonFragment extends BaseFragment implements OnClickListener
{
	private TextView tvTitle,tvAppInformation,tvEasySteps;
	private Button btnCreateCall,btnSkip;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.createcallfragment, container, false);
		idInitialization(view);
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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

	/*
	 * Initialization of widgets/views
	 */
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvAppInformation=(TextView) view.findViewById(R.id.tvAppInformation);
		tvEasySteps=(TextView) view.findViewById(R.id.tvEasySteps);
		tvAppInformation.setText(Html.fromHtml("Creating a Call Button <br> is really very easy.It<br> can be done in"));
		btnCreateCall=(Button) view.findViewById(R.id.btnCreateCall);
		btnSkip=(Button) view.findViewById(R.id.btnSkip);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(), tvTitle, "fonts/StencilStd.otf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvAppInformation, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvEasySteps, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(), btnCreateCall, "fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfButton(getActivity(), btnSkip, "fonts/Roboto-Regular_1.ttf");
		btnCreateCall.setOnClickListener(this);		
		btnSkip.setOnClickListener(this);
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnCreateCall)
		{
			ChooseContactFragment objChooseContact=null;
			if(mActivity instanceof MainBaseActivity)
			{
				objChooseContact=new ChooseContactFragment();
				objChooseContact.newInstance(((MainBaseActivity)mActivity));
				((MainBaseActivity)mActivity).setFragment(objChooseContact);
			}
			else if(mActivity instanceof HomeScreenActivity)
			{
				objChooseContact=new ChooseContactFragment();
				objChooseContact.newInstance(((HomeScreenActivity)mActivity));
				((HomeScreenActivity)mActivity).setFragment(objChooseContact);
			}
		}
		else if(v.getId()==R.id.btnSkip)
		{
			if(saveState.isRegistered(getActivity()))
			{
				getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
				getActivity().finish();
			}
			else
			{
				((MainBaseActivity)mActivity).setFragment(new UserRegistrationFragment());
			}
		}
	}
}
