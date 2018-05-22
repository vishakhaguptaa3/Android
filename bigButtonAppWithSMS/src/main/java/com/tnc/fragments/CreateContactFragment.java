package com.tnc.fragments;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragment;
import com.tnc.bean.AddTileBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.preferences.SharedPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
/**
 * class to Create New Contact 
 *  @author a3logics
 */

public class CreateContactFragment extends BaseFragment implements OnClickListener
{
	private TextView tvTitle,tvStep,tvChooseContact,tvContact,tvContactPhone,tvInternationalCode;
	private Button btnBack,btnHome,btnNext;
	private FrameLayout flBackArrow,flInformationButton;
	private EditText etContactName,etContactNumber,etISDCode;
	private CheckBox chkBoxISD;
	private AddTileBean addTileBean=null;
	private String TAG="CreateContactFragment";
	private ImageRequestDialog dialogAlert=null;
	private Context mActivity;
	public boolean isFirstTime = true;
	private LinearLayout llTopHeader;
	public static String isdCodeNumberCreateContact="";

	public CreateContactFragment newInstance(Context mActivity)
	{
		CreateContactFragment frag = new CreateContactFragment();
		this.mActivity=mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.createcontactfragment, container, false);
		idInitialization(view);
		return view;
	}

	@Override
	public void onPause() {
		GlobalConfig_Methods.hideKeyBoard(mActivity, etContactName);
		GlobalConfig_Methods.hideKeyBoard(mActivity, etContactNumber);
		GlobalConfig_Methods.hideKeyBoard(mActivity, etISDCode);
		super.onPause();
		
		if(mActivity instanceof MainBaseActivity)
			if(((MainBaseActivity)mActivity)!=null)
				if(saveState.getGCMRegistrationId(getActivity()).equals(""))
					((MainBaseActivity)mActivity).setGCMRegID();
	}

	/*
	 * Initialization of widgets/views
	 * */
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvChooseContact=(TextView) view.findViewById(R.id.tvChooseContact);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		tvContact=(TextView) view.findViewById(R.id.tvContact);
		tvContactPhone=(TextView) view.findViewById(R.id.tvContactPhone);
		tvInternationalCode=(TextView) view.findViewById(R.id.tvInternationalCode);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		etContactName=(EditText) view.findViewById(R.id.etContactName);
		etContactNumber=(EditText) view.findViewById(R.id.etContactNumber);
		etISDCode=(EditText) view.findViewById(R.id.etISDCode);
		chkBoxISD=(CheckBox) view.findViewById(R.id.chkBoxISD);
		btnNext=(Button) view.findViewById(R.id.btnNext);	
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		llTopHeader=(LinearLayout) view.findViewById(R.id.llTopHeader);
		llTopHeader.setOnClickListener(this);
		
//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));
		
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
		tvInternationalCode.setText("Dialing Prefix");
		tvStep.setText("STEP 2");
		tvStep.setVisibility(View.GONE);
		etISDCode.setEnabled(false);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvChooseContact, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvContact, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvContactPhone, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etContactName, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etContactNumber, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etISDCode, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnNext, "fonts/Roboto-Regular_1.ttf");
		flBackArrow.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnHome.setOnClickListener(this);
		chkBoxISD.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)
				{
					etISDCode.setEnabled(true);
				}
				else
				{
					//phase-4 Comment
					//					MainBaseActivity.selectedISDCode="";
					isdCodeNumberCreateContact="";
					etISDCode.setEnabled(false);
					etISDCode.setText("");
				}
			}
		});
		//		 Attach TextWatcher to EditText
		TextWatcher watcher=new GlobalConfig_Methods().convertPhoneToUSFormat(etContactNumber);
		etContactNumber.addTextChangedListener(watcher);
	}
	@Override
	public void onResume() {
		super.onResume();
		if(!isFirstTime)
		{
			SpannableString sp = new SpannableString(MainBaseActivity.contactNumberForTile);
			etContactNumber.setText(sp);
			if(isdCodeNumberCreateContact!=null && !isdCodeNumberCreateContact.trim().equals(""))
			{
				chkBoxISD.setChecked(true);
				etISDCode.setText(isdCodeNumberCreateContact);
			}
			else{
				chkBoxISD.setChecked(false);
				//				etISDCode.setText("");
			}
		}
	}

	@Override
	public void onDestroy() {
		isdCodeNumberCreateContact=null;
		super.onDestroy();
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnBack)
		{
			if(mActivity instanceof MainBaseActivity)
				((MainBaseActivity)mActivity).fragmentManager.popBackStack();
			else if(mActivity instanceof HomeScreenActivity)
				((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
		}
		else if(v.getId()==R.id.btnNext)
		{
			if(chkBoxISD.isChecked())
			{
				if(!etContactName.getText().toString().trim().equals("") &&
						!etContactNumber.getText().toString().trim().equals("") &&
						! etISDCode.getText().toString().trim().equals(""))
				{
					createContactTile();
				}
				else if(etContactName.getText().toString().trim().equals("") &&
						etContactNumber.getText().toString().trim().equals("") &&
						etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following fields:\n"+
							"contact name,phone number,international access codes","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter contact name,phone number,international access codes",Toast.LENGTH_LONG).show();
				}
				else if(!etContactName.getText().toString().trim().equals("") &&
						etContactNumber.getText().toString().trim().equals("") &&
						etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following fields:\nphone number,international access codes","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter contact name",Toast.LENGTH_LONG).show();
				}
				else if(etContactName.getText().toString().trim().equals("") &&
						!etContactNumber.getText().toString().trim().equals("") &&
						!etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following field:\ncontact name","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter contact name",Toast.LENGTH_LONG).show();
				}
				else if(!etContactName.getText().toString().trim().equals("") &&
						etContactNumber.getText().toString().trim().equals("") &&
						!etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following field:\nphone number","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter phone number",Toast.LENGTH_LONG).show();
				}
				else if(!etContactName.getText().toString().trim().equals("") &&
						!etContactNumber.getText().toString().trim().equals("") &&
						etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following field:\ninternational access codes","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter international access codes",Toast.LENGTH_LONG).show();
				}
				else if(etContactName.getText().toString().trim().equals("") &&
						etContactNumber.getText().toString().trim().equals("") &&
						!etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following fields:\ncontact name,phone number","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter international access codes",Toast.LENGTH_LONG).show();
				}
				else if(etContactName.getText().toString().trim().equals("") &&
						!etContactNumber.getText().toString().trim().equals("") &&
						etISDCode.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following field:\nphone number","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter international access codes",Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				if(!etContactName.getText().toString().trim().equals("") &&
						!etContactNumber.getText().toString().trim().equals("") )
				{
					createContactTile();
				}
				else if(etContactName.getText().toString().trim().equals("") &&
						etContactNumber.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following fields:\ncontact name,phone number","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter contact name,phone number",Toast.LENGTH_LONG).show();				
				}
				else if(etContactName.getText().toString().trim().equals("") &&
						!etContactNumber.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following field:\ncontact name","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter contact name",Toast.LENGTH_LONG).show();				
				}
				else if(!etContactName.getText().toString().trim().equals("") &&
						etContactNumber.getText().toString().trim().equals(""))
				{
					dialogAlert=new ImageRequestDialog();
					dialogAlert.setCancelable(false);
					dialogAlert.newInstance("",mActivity,"Please enter following field:\nphone number","",null);
					dialogAlert.show(getChildFragmentManager(), "test");
					//					Toast.makeText(getActivity(), "Please enter phone number",Toast.LENGTH_LONG).show();
				}
			}
		}
		else if(v.getId()==R.id.btnHome)
		{
			if(mActivity instanceof MainBaseActivity)
			{
				((MainBaseActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
				((MainBaseActivity)mActivity).finish();
			}
			else if(mActivity instanceof HomeScreenActivity)
			{
				((HomeScreenActivity)mActivity).startActivity(new Intent(mActivity,HomeScreenActivity.class));
				((HomeScreenActivity)mActivity).finish();
			}
		}
		else if(v.getId()==R.id.llTopHeader)
		{

		}
	}

	/**
	 * Method to create contact Tile
	 */
	private void createContactTile()
	{   
		String number=GlobalConfig_Methods.trimSpecialCharactersFromString(etContactNumber.getText().toString().trim());
		String code=GlobalConfig_Methods.trimSpecialCharactersFromString(etISDCode.getText().toString());
		TileContactDetailsFragment objTileContactDetailsFragment = null;
		int size=-1;

		//lenght 0-2
		if(number.trim().length()<=2)
		{
			dialogAlert=new ImageRequestDialog();
			dialogAlert.newInstance("",mActivity,"Invalid Phone Number","",null);
			dialogAlert.show(getChildFragmentManager(), "test");
		}
		else if(number.trim().length()>=3 && number.trim().length()<=9)
		{

			if(chkBoxISD.isChecked())
			{
				if(code!=null && !code.trim().equals(""))

				{
					boolean isExist = false;
					isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), number);
					if(isExist){
						size=1;	
					}else if(!isExist){
						size=-1;	
					}
				}
			}
			else if(!chkBoxISD.isChecked())
			{
				boolean isExist = false;
				isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), number);
				if(isExist){
					size=1;	
				}else if(!isExist){
					size=-1;	
				}
			}

			if(size>0)
			{
				ImageRequestDialog dialog=new ImageRequestDialog();
				dialog.setCancelable(false);
				dialog.newInstance("", mActivity,"Tile for this contact number already exists","",null);
				dialog.show(getChildFragmentManager(), "test");
			}
			else{
				if(chkBoxISD.isChecked() && !etISDCode.getText().toString().trim().equals(""))
				{
					isdCodeNumberCreateContact=etISDCode.getText().toString().trim();
				}
				if(mActivity instanceof MainBaseActivity)
				{
					objTileContactDetailsFragment=new TileContactDetailsFragment();
					objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,null,String.valueOf(number),etContactName.getText().toString().trim(),
							String.valueOf(number),null,true);
					((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					objTileContactDetailsFragment=new TileContactDetailsFragment();
					objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,null,String.valueOf(number),etContactName.getText().toString().trim(),
							String.valueOf(number),null,true);
					((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
				}
				MainBaseActivity.isContactCreated=true;
				isFirstTime = false;	
				//				MainBaseActivity.objTileEdit=null;
			}
		}

		else if(number.trim().length()==10 && code.trim().equals(""))
		{
			boolean isExist = false;
			isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), number);
			if(isExist){
				size=1;	
			}else if(!isExist){
				size=-1;	
			}
			if(size>0)
			{
				ImageRequestDialog dialog=new ImageRequestDialog();
				dialog.setCancelable(false);
				dialog.newInstance("", mActivity,"Tile for this contact number already exists","",null);
				dialog.show(getChildFragmentManager(), "test");
			}
			else{
				//countrycode + "," + number +"," + isMobile + "," + isdCodeflag + "," + isdCode + "," + isTnCUser;
				if(mActivity instanceof MainBaseActivity)
				{
					objTileContactDetailsFragment = new TileContactDetailsFragment();
					if(saveState.isRegistered(mActivity))
					{
						objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,GlobalConfig_Methods.getBBNumberToCheck(mActivity,number),null,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
					else{
						objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,null,number,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
					((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					objTileContactDetailsFragment = new TileContactDetailsFragment();
					if(saveState.isRegistered(mActivity))
					{
						objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,GlobalConfig_Methods.getBBNumberToCheck(mActivity,number),null,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
					else{
						objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,null,number,etContactName.getText().toString().trim(),String.valueOf(number),null,true);
					}
					((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
				}
				MainBaseActivity.isContactCreated=true;
				isFirstTime = false;	
			}
		}
		else if(number.trim().length()>=10 && chkBoxISD.isChecked() && !code.trim().equals(""))
		{
			if(chkBoxISD.isChecked())
			{
				if(code!=null && !code.trim().equals(""))

				{
					boolean isExist = false;
					isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), number);
					if(isExist){
						size=1;	
					}else if(!isExist){
						size=-1;	
					}
				}
			}
			else if(!chkBoxISD.isChecked())
			{
				boolean isExist = false;
				isExist=GlobalConfig_Methods.CheckTileDuplicacy(getActivity(), number);
				if(isExist){
					size=1;	
				}else if(!isExist){
					size=-1;	
				}
			}

			if(size>0)
			{
				ImageRequestDialog dialog=new ImageRequestDialog();
				dialog.setCancelable(false);
				dialog.newInstance("", mActivity,"Tile for this contact number already exists","",null);
				dialog.show(getChildFragmentManager(), "test");
			}
			else {
				//countrycode + "," + number +"," + isMobile + "," + isdCodeflag + "," + isdCode + "," + isTnCUser;
				if(mActivity instanceof MainBaseActivity)
				{
					objTileContactDetailsFragment = new TileContactDetailsFragment();
					if(saveState.isRegistered(mActivity))
					{
						if(chkBoxISD.isChecked())
							isdCodeNumberCreateContact=etISDCode.getText().toString().trim();
						else{
							isdCodeNumberCreateContact="";
						}
						objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,GlobalConfig_Methods.getBBNumberToCheck(mActivity,number),null,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
					else{
						if(chkBoxISD.isChecked())
							isdCodeNumberCreateContact=etISDCode.getText().toString().trim();
						else{
							isdCodeNumberCreateContact="";
						}
						objTileContactDetailsFragment.newInstance(((MainBaseActivity)mActivity),false,null,isdCodeNumberCreateContact+number,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
					((MainBaseActivity) mActivity).setFragment(objTileContactDetailsFragment);
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					objTileContactDetailsFragment = new TileContactDetailsFragment();
					if(saveState.isRegistered(mActivity))
					{
						if(chkBoxISD.isChecked())
							isdCodeNumberCreateContact=etISDCode.getText().toString().trim();
						else{
							isdCodeNumberCreateContact="";
						}
						objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,GlobalConfig_Methods.getBBNumberToCheck(mActivity,code+number),null,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
					else{
						if(chkBoxISD.isChecked())
							isdCodeNumberCreateContact=etISDCode.getText().toString().trim();
						else
							isdCodeNumberCreateContact="";
						objTileContactDetailsFragment.newInstance(((HomeScreenActivity)mActivity),false,null,isdCodeNumberCreateContact+number,etContactName.getText().toString().trim(),
								String.valueOf(number),null,true);
					}
				}
				((HomeScreenActivity) mActivity).setFragment(objTileContactDetailsFragment);
				MainBaseActivity.isContactCreated=true;
				isFirstTime = false;	
			}
		}
	}
}