package com.tnc.fragments;

import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * class to display selected contact details /create new contact 
 *  @author a3logics
 */


public class ContactDetailsFragment extends BaseFragment implements
OnClickListener {
	private TextView tvTitle, tvContact, tvClickName, tvContactPhone,tvNumberTypeTag;
	private EditText etContactName;
	private 	LinearLayout llContainer,llContactNumbers,llRadioButtonHolder;
	private 	FrameLayout flInformationButton;
	private Button btnBack,btnHome,btnNext;
	private 	String savedName = "";
	private FrameLayout flBackArrow;
	private ContactDetailsBean selectedContactBean = null;
	// RadioButton rdoButtonNumber;
	// ImageView imViewTag;
	private View contactNumberViews;
	private String strSelectedNumber = "";
	public boolean isFirstTime = true;
	private ArrayList<String> listContactCheck=new ArrayList<String>();
	private int selectedContact=-1;
	private TextView tvContactNumber;
	private RadioButton rdoButtonNumber;
	private ImageView imViewTag;
	private 	Button btnDisableOverlay;
	private LinearLayout llViewBorder;
	private Button btnDisableNextButton;
	private boolean isClickEvent=false;
	private String number="";
	private String numberFilter="";
	private String prefix="";
	private SharedPreference saveState;
	// TextView tvContactNumber;
	//	public static String isdCodeNumber="";
	public ContactDetailsFragment() {
	}

	public ContactDetailsFragment(ContactDetailsBean selectedContactBean) {
		this.selectedContactBean = selectedContactBean;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contactdetailfragment, container,
				false);
		idInitialization(view);
		return view;
	}

	// Method to initialize views/widgets

	@SuppressLint("InflateParams")
	private void idInitialization(View view) {
		saveState=new SharedPreference();
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvContact = (TextView) view.findViewById(R.id.tvContact);
		tvClickName = (TextView) view.findViewById(R.id.tvClickName);
		tvContactPhone = (TextView) view.findViewById(R.id.tvContactPhone);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		flBackArrow = (FrameLayout) view.findViewById(R.id.flBackArrow);
		etContactName = (EditText) view.findViewById(R.id.etContactName);
		llContactNumbers = (LinearLayout) view.findViewById(R.id.llContactNumbers);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		llContainer=(LinearLayout) view.findViewById(R.id.llContainer);
		btnDisableNextButton=(Button) view.findViewById(R.id.btnDisableNextButton);
		btnDisableNextButton.setVisibility(View.GONE);
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
		//		etContactName.setHintTextColor(getResources().getColor(android.R.color.black));
		if(mActivity instanceof MainBaseActivity)
		{
			String fragment=((MainBaseActivity)mActivity).fragmentManager.getFragments().get(0).toString();
			if(fragment.contains("VideoFragment"))
			{
				flInformationButton.setVisibility(View.GONE);
				btnHome.setVisibility(View.GONE);
			}
			else{
				flInformationButton.setVisibility(View.VISIBLE);
				btnHome.setVisibility(View.VISIBLE);
			}
		}
		else if(mActivity instanceof HomeScreenActivity){
			flInformationButton.setVisibility(View.VISIBLE);
			btnHome.setVisibility(View.VISIBLE);
		}
		btnHome.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		flBackArrow.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(this);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(), tvContact,
				"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvClickName,
				"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(), tvContactPhone,
				"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(), etContactName,
				"fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfButton(getActivity(), btnNext,
				"fonts/Roboto-Regular_1.ttf");
		etContactName.setText(selectedContactBean.get_name());
		savedName = etContactName.getText().toString();
		inflateView(isClickEvent);
		if(listContactCheck.size()==1)
		{
			if(listContactCheck.get(0).replaceAll(" ","").trim().length()>1) // if(listContactCheck.get(0).replaceAll(" ","").trim().length()>7)
			{
				boolean isExist = GlobalConfig_Methods.CheckTileDuplicacy(getActivity(),listContactCheck.get(0).trim());

				rdoButtonNumber.setChecked(true);
				if(isExist){
					btnDisableOverlay.setVisibility(View.VISIBLE);
					btnDisableNextButton.setVisibility(View.VISIBLE);
					selectedContact=-1;	
				}
				else if(!isExist){
					btnDisableOverlay.setVisibility(View.GONE);
					btnDisableNextButton.setVisibility(View.GONE);
					selectedContact=0;
				}
			}
		}
	}

	boolean isInflateVieClicked=false;
	int count=0;

	// add views as per the count of phone numbers 
	@SuppressLint("InflateParams")
	private void inflateView(boolean isClickEvent)
	{
		count=0;
		listContactCheck.clear();
		numberLayoutLoop:		for ( int i = 0; i < selectedContactBean.get_phone().size(); i++) {
			if(!listContactCheck.isEmpty() && listContactCheck.contains(selectedContactBean.get_phone().get(i).replace(" ","")))
			{
			}else{
				contactNumberViews = getActivity().getLayoutInflater().inflate(R.layout.contactnumber_count, null);
				imViewTag = (ImageView) contactNumberViews.findViewById(R.id.imViewTag);
				listContactCheck.add(selectedContactBean.get_phone().get(i).replace(" ",""));
				btnDisableOverlay=(Button) contactNumberViews.findViewById(R.id.btnDisableOverlay);
				rdoButtonNumber = (RadioButton) contactNumberViews.findViewById(R.id.rdoButtonNumber);
				tvContactNumber = (TextView) contactNumberViews.findViewById(R.id.tvContactNumber);
				tvNumberTypeTag = (TextView) contactNumberViews.findViewById(R.id.tvNumberTypeTag);
				llRadioButtonHolder=(LinearLayout) contactNumberViews.findViewById(R.id.llRadioButtonHolder);
				llViewBorder=(LinearLayout) contactNumberViews.findViewById(R.id.llViewBorder);
				tvContactNumber.setText(GlobalConfig_Methods.trimSpecialPhoneNumberToDisplay(selectedContactBean.get_phone().get(i).trim())); //listContactCheck.get(i).trim()  //(GlobalConfig_Methods.trimSpecialCharactersFromString(selectedContactBean.get_phone().get(i)));
				tvNumberTypeTag.setText(selectedContactBean.getsType().get(i));
				contactNumberViews.setId(listContactCheck.size());
				rdoButtonNumber.setId(listContactCheck.size());
				imViewTag.setVisibility(View.GONE);
				btnDisableOverlay.setVisibility(View.GONE);
				llViewBorder.setVisibility(View.GONE);
				contactNumberViews.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				llRadioButtonHolder.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				tvContactNumber.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				llContainer.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
				if(selectedContactBean.get_phone().size()==1)
				{
					addView(i);
					break numberLayoutLoop;
				}
				else
				{
					if(!isClickEvent)
					{
						number = selectedContactBean.get_phone().get(i).trim();
						if(selectedContactBean.get_phone().get(i).replaceAll(" ","").trim().length()>7){
							boolean isExist = GlobalConfig_Methods.CheckTileDuplicacy(getActivity(),selectedContactBean.get_phone().get(i));

							if(isExist)
							{
								rdoButtonNumber.setChecked(true);
								rdoButtonNumber.setClickable(false);
								btnDisableOverlay.setVisibility(View.VISIBLE);
								count++;	
							}else if(!isExist){
								rdoButtonNumber.setChecked(false);
								rdoButtonNumber.setClickable(true);
								btnDisableOverlay.setVisibility(View.GONE);	
								
							}
							
						}
						if(selectedContact!=-1 && selectedContact == contactNumberViews.getId())
						{
							rdoButtonNumber.setChecked(true);
							rdoButtonNumber.setClickable(true);

						}
						else if(MainBaseActivity.selectedConatctId>-1 && MainBaseActivity.selectedConatctId == contactNumberViews.getId())
						{
							rdoButtonNumber.setChecked(true);
							rdoButtonNumber.setClickable(true);
							selectedContact=MainBaseActivity.selectedConatctId;
						}
						
						if(listContactCheck.size()==count)
						{
							btnDisableNextButton.setVisibility(View.VISIBLE);
						}
						else{
							btnDisableNextButton.setVisibility(View.GONE);
						}
					}
					else if(isClickEvent)
					{
						number = selectedContactBean.get_phone().get(i).trim();
						if(selectedContactBean.get_phone().get(i).replaceAll(" ","").trim().length()>7){
							boolean isExist = GlobalConfig_Methods.CheckTileDuplicacy(getActivity(),selectedContactBean.get_phone().get(i));

							if(isExist)
							{
								rdoButtonNumber.setChecked(true);
								rdoButtonNumber.setClickable(false);
								btnDisableOverlay.setVisibility(View.VISIBLE);
							}else if(!isExist){
								rdoButtonNumber.setChecked(false);
								rdoButtonNumber.setClickable(true);
								btnDisableOverlay.setVisibility(View.GONE);
								
							}
						}
						if(selectedContact!=-1 && selectedContact == contactNumberViews.getId())
						{
							rdoButtonNumber.setChecked(true);
							rdoButtonNumber.setClickable(true);
						}
						else if(MainBaseActivity.selectedConatctId>-1 && MainBaseActivity.selectedConatctId == contactNumberViews.getId())
						{
							rdoButtonNumber.setChecked(true);
							rdoButtonNumber.setClickable(true);
							selectedContact=MainBaseActivity.selectedConatctId;
						}
						
					}
					addView(i);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void addView(int i)
	{
		if(saveState.isRegistered(mActivity))
		{
			String number=GlobalConfig_Methods.getBBNumberToCheck(mActivity,selectedContactBean.get_phone().get(i));
			String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
			boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
			try {
				String []arrayUserDetails=number.split(",");
				countryCodeRegisteredUser=arrayUserDetails[0];
				numberRegisteredUser=arrayUserDetails[1];
				isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
				isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
				isdCodeRegisteredUser=arrayUserDetails[4];
				isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);
			} catch (Exception e) {
				e.getMessage();
			}

			if(isTncUserRegisteredUser)
			{
				llViewBorder.setVisibility(View.VISIBLE);
			}
			else{
				llViewBorder.setVisibility(View.GONE);	
			}
		}

		rdoButtonNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listContactCheck.size()>1 && selectedContact != v.getId())
				{
					MainBaseActivity.selectedConatctId=-1;
					selectedContact=v.getId();
					((RadioButton)v).setEnabled(true);
					((RadioButton)v).setChecked(true);
					isInflateVieClicked=true;
					llContactNumbers.removeAllViews();
					isClickEvent=true;
					inflateView(isClickEvent);
				}
			}
		});
		llContactNumbers.addView(contactNumberViews);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isFirstTime = true;
		//		isFound=false;
		llContactNumbers.removeAllViews();
		
	}

	@Override
	public void onClick(View v) {
		CreateContactFragment.isdCodeNumberCreateContact=null;
		if (v.getId() == R.id.btnNext) 
		{
			MainBaseActivity.isContactCreated=false;
			if(!etContactName.getText().toString().trim().equals(""))
			{
				if(selectedContact!=-1)
				{
					if (!etContactName.getText().toString().trim().equals("")) 
					{
						String strPhone="";
						if(listContactCheck.size()>1)
						
						{
							strPhone=listContactCheck.get(selectedContact-1);//selectedContactBean.get_phone().get(selectedContact-1);
						}
						else 
						{
							strPhone=listContactCheck.get(selectedContact);
						}
						String contact_Number="";

						contact_Number=strPhone;	
						if(contact_Number.length()<=2)
						{
							ImageRequestDialog dialog = new ImageRequestDialog();
							dialog.setCancelable(false);
							dialog.newInstance("", mActivity,"Invalid Phone Number","",null);
							dialog.show(getChildFragmentManager(), "test");
						}

						else if(contact_Number.length()>=3 && contact_Number.length()<=9)
						{
							//In this case we will execute a case of non-registerered user on tileContactDetailsFragment
							strSelectedNumber=contact_Number;
							isFirstTime = false;
							if (!strSelectedNumber.trim().equals("")) 
							{
								llContactNumbers.removeAllViews();
								TileContactDetailsFragment objTileContactDetailsFragment=null;

								if(mActivity instanceof MainBaseActivity)
								{
									objTileContactDetailsFragment=new TileContactDetailsFragment();
									objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,null,strSelectedNumber,etContactName.getText().toString().trim(),
											strSelectedNumber,null,true);

									((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
								}
								else if(mActivity instanceof HomeScreenActivity)
								{
									objTileContactDetailsFragment=new TileContactDetailsFragment();
									objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,null,strSelectedNumber,etContactName.getText().toString().trim(),
											strSelectedNumber,null,true);
									((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
								}
								MainBaseActivity.selectedConatctId=selectedContact;
								isClickEvent=false;
							}
							else 
							{
								ImageRequestDialog dialog = new ImageRequestDialog();
								dialog.setCancelable(false);
								dialog.newInstance("", mActivity,"Please select Contact Phone","",null);
								dialog.show(getChildFragmentManager(), "test");
							}
						}
						else
						{  // in case of number >=10
							strSelectedNumber=contact_Number;
							isFirstTime = false;

							if (!strSelectedNumber.trim().equals("")) 
							{
								llContactNumbers.removeAllViews();
                                CreateChatButton objTileContactDetailsFragment;
                                objTileContactDetailsFragment=new CreateChatButton();
								if(mActivity instanceof MainBaseActivity)
								{
//									objTileContactDetailsFragment=new TileContactDetailsFragment();
									if(saveState.isRegistered(mActivity))
									{
										objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,GlobalConfig_Methods.getBBNumberToCheck(mActivity,strSelectedNumber),null,etContactName.getText().toString().trim(),strSelectedNumber,null,true);
									}
									else {
										objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,null,strSelectedNumber,etContactName.getText().toString().trim(),strSelectedNumber,null,true);
									}
									((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
								}
								else if(mActivity instanceof HomeScreenActivity)
								{
//									objTileContactDetailsFragment=new TileContactDetailsFragment();
									if(saveState.isRegistered(mActivity))
									{
										objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,GlobalConfig_Methods.getBBNumberToCheck(mActivity,strSelectedNumber), null,etContactName.getText().toString().trim(),
												strSelectedNumber,null,true);
									}
									else {
										objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,null,strSelectedNumber,etContactName.getText().toString().trim(),strSelectedNumber,null,true);
									}
									((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
								}
								MainBaseActivity.selectedConatctId=selectedContact;
								isClickEvent=false;
							}
							else 
							{
								ImageRequestDialog dialog = new ImageRequestDialog();
								dialog.setCancelable(false);
								dialog.newInstance("", mActivity,"Please select Contact Phone","",null);
								dialog.show(getChildFragmentManager(), "test");
							}
						}
					}
					else
					{
						ImageRequestDialog dialog = new ImageRequestDialog();
						dialog.setCancelable(false);
						dialog.newInstance("", mActivity,"Please enter following field:\nContact Name","",null);
						dialog.show(getChildFragmentManager(), "test");
					}
				}
				else
				{
					ImageRequestDialog dialog = new ImageRequestDialog();
					dialog.setCancelable(false);
					dialog.newInstance("", mActivity,"Please select Contact Phone","",null);
					dialog.show(getChildFragmentManager(), "test");
				}
			}
			else{
				ImageRequestDialog dialog = new ImageRequestDialog();
				dialog.setCancelable(false);
				dialog.newInstance("", mActivity,"Please enter following field:\nContact Name","",null);
				dialog.show(getChildFragmentManager(), "test");
			}
		} 
		else if (v.getId() == R.id.btnBack) 
		{
			if(mActivity instanceof MainBaseActivity)
				((MainBaseActivity) mActivity).fragmentManager.popBackStack();
			if(mActivity instanceof HomeScreenActivity)
				((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
		}
		else if(v.getId()==R.id.btnHome)
		{
			getActivity().startActivity(new Intent(getActivity(),HomeScreenActivity.class));
			getActivity().finish();
		}
	}
}