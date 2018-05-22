package com.tnc.fragments;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.checkinternet.NetworkConnection;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * class to Select option to choose/create contact for creating the button
 *  @author a3logics
 */


public class ChooseContactFragment extends BaseFragment implements
OnClickListener {
	LinearLayout llFavorites, llContacts, llCreate,llTopHeader,llButtonHolder;
	TextView tvTitle, tvStep, tvChooseContact, tvFromFavorites,
	tvFavoritesList, tvFromContacts, tvContactList, tvCreate,
	tvNewContact;
	Button btnBack;
	FrameLayout flBackArrow;
	Context mActivity;
//	Handler handler = new Handler();

	public ChooseContactFragment newInstance(Context mActivity) {
		ChooseContactFragment frag = new ChooseContactFragment();
		this.mActivity = mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.choosecontactfragment, container,
				false);
		idInitialization(view);
		MainBaseActivity.selectedConatctId=-1;
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mActivity instanceof MainBaseActivity)
			if (((MainBaseActivity) mActivity != null)) {
				if (((MainBaseActivity) mActivity) != null) {
					if (NetworkConnection.isNetworkAvailable(mActivity)) {
						if (saveState.getGCMRegistrationId(getActivity())
								.equals("")) {
							((MainBaseActivity) mActivity).setGCMRegID();
						}
					}
				}

			}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (HomeScreenActivity.btnNotification != null
				&& HomeScreenActivity.btnAddTile != null
				&& HomeScreenActivity.btnCallEmergency != null) {
			HomeScreenActivity.btnNotification.setClickable(false);
			HomeScreenActivity.btnNotification.setEnabled(false);
			HomeScreenActivity.btnAddTile.setClickable(false);
			HomeScreenActivity.btnAddTile.setEnabled(false);
			HomeScreenActivity.btnCallEmergency.setClickable(false);
			HomeScreenActivity.btnCallEmergency.setEnabled(false);
		}
		if(HomeScreenActivity.btnDisable!=null)
			HomeScreenActivity.btnDisable.setEnabled(false);
		MainBaseActivity.isMobileChecked=false;
		MainBaseActivity.selectedCountryCodeForTileDetails=null;
		MainBaseActivity.selectedPrefixCodeForTileDetails=null;
		MainBaseActivity.isIsdCodeFlagChecked=false;
		MainBaseActivity.contactNumberForTile=null;
		MainBaseActivity.contactNameForTile=null;
		MainBaseActivity.objTileDetailBeanStatic=null;
//		ContactDetailsFragment.isdCodeNumber=null;
		CreateContactFragment.isdCodeNumberCreateContact=null;
		MainBaseActivity.isContactCreated=false;
		MainBaseActivity.objTileEdit=null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mActivity instanceof HomeScreenActivity) {
			if (HomeScreenActivity.btnNotification != null
					&& HomeScreenActivity.btnAddTile != null
					&& HomeScreenActivity.btnCallEmergency != null) {
				HomeScreenActivity.btnNotification.setClickable(true);
				HomeScreenActivity.btnNotification.setEnabled(true);
				HomeScreenActivity.btnAddTile.setClickable(true);
				HomeScreenActivity.btnAddTile.setEnabled(true);
				HomeScreenActivity.btnCallEmergency.setClickable(true);
				HomeScreenActivity.btnCallEmergency.setEnabled(true);
			}
		}
	}

	/*
	 * Initialization of widgets/views
	 */
	private void idInitialization(View view) {
		saveState = new SharedPreference();

		llFavorites = (LinearLayout) view.findViewById(R.id.llFavorites);
		llContacts = (LinearLayout) view.findViewById(R.id.llContacts);
		llCreate = (LinearLayout) view.findViewById(R.id.llCreate);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvStep = (TextView) view.findViewById(R.id.tvStep);
		tvChooseContact = (TextView) view.findViewById(R.id.tvChooseContact);
		tvFromFavorites = (TextView) view.findViewById(R.id.tvFromFavorites);
		tvFavoritesList = (TextView) view.findViewById(R.id.tvFavoritesList);
		tvFromContacts = (TextView) view.findViewById(R.id.tvFromContacts);
		tvContactList = (TextView) view.findViewById(R.id.tvContactList);
		tvCreate = (TextView) view.findViewById(R.id.tvCreate);
		tvNewContact = (TextView) view.findViewById(R.id.tvNewContact);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		flBackArrow.setVisibility(View.VISIBLE);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		llTopHeader=(LinearLayout) view.findViewById(R.id.llTopHeader);
		llButtonHolder=(LinearLayout) view.findViewById(R.id.llButtonHolder);
		
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		//		CustomFonts.setFontOfTextView(getActivity(), tvTitle,
		//				"fonts/StencilStd.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvStep,
				"fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvChooseContact,
				"fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvFromFavorites,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvFavoritesList,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvContactList,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvFromContacts,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvCreate,
				"fonts/Roboto-Regular_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvNewContact,
				"fonts/Roboto-Regular_1.ttf");
		llFavorites.setOnClickListener(this);
		llContacts.setOnClickListener(this);
		llCreate.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		llTopHeader.setOnClickListener(this);
		llButtonHolder.setOnClickListener(this);
		tvStep.setVisibility(View.GONE);

		tvChooseContact.setAllCaps(true);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
	}

	//Method to reset static values
	private void initializeValues() {
		//phase-4 Comment
		MainBaseActivity.selectedCountryCodeForTileDetails="";
		MainBaseActivity.selectedPrefixCodeForTileDetails="";
		MainBaseActivity.selectedImagepath = "";
		MainBaseActivity._bitmap = null;
		GlobalCommonValues._Contacimage = null;
		MainBaseActivity.isImageRequested = false;
		MainBaseActivity.isImageSelected = false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnBack) {
			if (mActivity instanceof MainBaseActivity)
				((MainBaseActivity) mActivity).fragmentManager.popBackStack();
			else if (mActivity instanceof HomeScreenActivity)
				((HomeScreenActivity) mActivity).fragmentManager.popBackStack();
		} else if (v.getId() == R.id.llFavorites) {
			MainBaseActivity.isContactCreated=false;
			ContactListFragment contactFragment = null;
			MainBaseActivity.selectedCountryCodeForTileDetails="";
			MainBaseActivity.selectedPrefixCodeForTileDetails = "";  //phase-4
			if (mActivity instanceof MainBaseActivity) {
				contactFragment = new ContactListFragment();
				contactFragment.newInstance("Favorites List",
						((MainBaseActivity) mActivity));
				((MainBaseActivity) mActivity).setFragment(contactFragment);
			} else if (mActivity instanceof HomeScreenActivity) {
				contactFragment = new ContactListFragment();
				contactFragment.newInstance("Favorites List",
						((HomeScreenActivity) mActivity));
				((HomeScreenActivity) mActivity).setFragment(contactFragment);
			}
			//Call Method to reset static values
			initializeValues();

		} else if (v.getId() == R.id.llContacts) {
			MainBaseActivity.isContactCreated=false;
			ContactListFragment contactFragment = null;
			MainBaseActivity.selectedCountryCodeForTileDetails="";
			MainBaseActivity.selectedPrefixCodeForTileDetails = "";  //phase-4
			if (mActivity instanceof MainBaseActivity) {
				contactFragment = new ContactListFragment();
				contactFragment.newInstance("Contact List",
						((MainBaseActivity) mActivity));
				((MainBaseActivity) mActivity).setFragment(contactFragment);
			} else if (mActivity instanceof HomeScreenActivity) {
				contactFragment = new ContactListFragment();
				contactFragment.newInstance("Contact List",
						((HomeScreenActivity) mActivity));
				((HomeScreenActivity) mActivity).setFragment(contactFragment);
			}
			//Call Method to reset static values
			initializeValues();
		} else if (v.getId() == R.id.llCreate) {
			MainBaseActivity.isContactCreated=true;
			TileContactDetailsFragment objTileContactDetailsFragment=null;
			if(mActivity instanceof MainBaseActivity)
			{
				objTileContactDetailsFragment=new TileContactDetailsFragment();
				objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,null,null,null,"",null,false);
				((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
			}
			else if(mActivity instanceof HomeScreenActivity)
			{
				objTileContactDetailsFragment=new TileContactDetailsFragment();
				objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,null,null,null,"",null,false);
				((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
			}
			//Call Method to reset static values
			initializeValues();
		}
		else if(v.getId()==R.id.llTopHeader)
		{

		}
		else if(v.getId()==R.id.llButtonHolder)
		{

		}
		else if(v.getId()==R.id.tvStep)
		{

		}
		else if(v.getId()==R.id.tvChooseContact)
		{

		}
	}
}
