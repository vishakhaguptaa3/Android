package com.tnc.fragments;

import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.MessageDeleteConfirmation;
import com.tnc.dialog.MessageSaveConfirmationDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * class to compose configured messages 
 *  @author a3logics
 */

public class ComposeMessageFragment extends BaseFragmentTabs implements OnClickListener
{
	private Activity mActivity;
	private FrameLayout flBackArrow,flInformationButton;
	private TextView tvTitle,tvHeading,tvTypeMessage;
	private Button btnBack,btnSaveMessage,btnCancel,btnDisableOverlay;
	//btnDisableSaveButton
	private EditText etComposeMessage;
	private int type,id;
	private String message="";
	private INotifyGalleryDialog iNotifyGalleryDialog;
	private RadioGroup rgMessageType;
	private RadioButton rbInitiation,rbResponse,rbBoth;
	//	boolean isUpdateMode=false;
	private String checkedValue="";
	private boolean isLocked;
	private Button btnDelete;

	public ComposeMessageFragment newInstance(Activity mActivity,int type,int id,String message,boolean isLocked,
											  INotifyGalleryDialog iNotifyGalleryDialog)
	{
		ComposeMessageFragment frag = new ComposeMessageFragment();
		this.mActivity=mActivity;
		Bundle args = new Bundle();
		frag.setArguments(args);
		this.id=id;
		this.type=type;
		this.message=message;
		this.iNotifyGalleryDialog=iNotifyGalleryDialog;
		this.isLocked=isLocked;
		//		this.isUpdateMode=isUpdateMode;
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.composemessagefragment, container, false);
		idInitialization(view);
		return view;
	}

	// Method to initialize views/widgets
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		flInformationButton=(FrameLayout) view.findViewById(R.id.flInformationButton);
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvHeading=(TextView) view.findViewById(R.id.tvHeading);
		tvTypeMessage=(TextView) view.findViewById(R.id.tvTypeMessage);
		etComposeMessage=(EditText) view.findViewById(R.id.etComposeMessage);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		btnSaveMessage=(Button) view.findViewById(R.id.btnSaveMessage);
		btnCancel=(Button) view.findViewById(R.id.btnCancel);
		btnDisableOverlay=(Button) view.findViewById(R.id.btnDisableOverlay);
		rgMessageType=(RadioGroup) view.findViewById(R.id.rgMessageType);
		rbInitiation=(RadioButton) view.findViewById(R.id.rbInitiation);
		rbResponse=(RadioButton) view.findViewById(R.id.rbResponse);
		rbBoth=(RadioButton) view.findViewById(R.id.rbBoth);
		btnDelete=(Button) view.findViewById(R.id.btnDelete);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvHeading, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvTypeMessage, "fonts/Roboto-Bold_1.ttf");
		flBackArrow.setVisibility(View.VISIBLE);
		flInformationButton.setVisibility(View.GONE);

//		tvTitle.setText(tvTitle.getText().toString() + Html.fromHtml("&trade;"));

		if(message!=null && !message.trim().equals(""))
		{
			etComposeMessage.setText(message);
		}
		if(type==0) // type 0- Initiation, 1 - Response
		{
			if(id>0)
			{
				tvHeading.setText("EDIT MESSAGE");
			}
			else{
				tvHeading.setText("COMPOSE NEW INITIATION MESSAGE");
			}
			rbInitiation.setChecked(true);
			checkedValue="init";
		}
		else if(type==1)
		{
			if(id>0)
			{
				tvHeading.setText("EDIT MESSAGE");
			}
			else{
				tvHeading.setText("COMPOSE NEW RESPONSE MESSAGE");
			}
			rbResponse.setChecked(true);
			checkedValue="resp";
		}
		btnBack.setOnClickListener(this);
		btnSaveMessage.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		if(isLocked)
		{
			btnDelete.setVisibility(View.GONE);
			btnCancel.setVisibility(View.GONE);
		}
		else {
			if(id>-1)
			{
				btnDelete.setVisibility(View.VISIBLE);
			}
			else
			{
				btnDelete.setVisibility(View.GONE);
			}
			btnCancel.setVisibility(View.GONE);
		}
		rgMessageType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.rbInitiation) {
					checkedValue="init";
				} else if(checkedId == R.id.rbResponse) {
					checkedValue="resp";
				} else if(checkedId == R.id.rbBoth){
					checkedValue="both";
				}
				if(!etComposeMessage.getText().toString().trim().equals(""))
				{
					btnSaveMessage.setVisibility(View.VISIBLE);
				}
				else if(etComposeMessage.getText().toString().trim().equals(""))
					btnSaveMessage.setVisibility(View.GONE);

			}
		});
		btnDelete.setOnClickListener(this);
		btnSaveMessage.setVisibility(View.GONE);
		etComposeMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!s.toString().trim().equals(""))
				{
					btnSaveMessage.setVisibility(View.VISIBLE);
				}
				else if(s.toString().trim().equals(""))
					btnSaveMessage.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * Method to save init response methods
	 * @param typeMethod
	 * @param isLockedMethod
	 */
	private void saveInitResponseMessage(int typeMethod,int isLockedMethod)
	{
		//		isLockedMethod=0;
		int saveId=-1;
		if(id==-1)  //In case Of New Message Is Saved
		{
			int maxId=-1;
			if(checkedValue.equalsIgnoreCase("init"))
			{
				maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
				saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),0,isLockedMethod,false,false);
			}
			else if(checkedValue.equalsIgnoreCase("resp"))
			{
				maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
				saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),1,isLockedMethod,false,false);
			}
			else if(checkedValue.equalsIgnoreCase("both"))
			{
				maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
				saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),0,isLockedMethod,false,false);
				maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
				saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),1,isLockedMethod,false,false);
			}

		}
		else if(id>0)  //In case Of Old Message Is Updated
		{
			//type -0 For Init  type -1 For resp
			if(typeMethod==0 && checkedValue.equalsIgnoreCase("init"))
			{

				if(isLocked)
				{
					int max_Id=-1;
					max_Id=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
					saveId=DBQuery.insertConfigMessage(mActivityTabs,max_Id+1,etComposeMessage.getText().toString(),typeMethod,0,false,false);
				}
				else
				{
					saveId=DBQuery.insertConfigMessage(mActivityTabs,id,etComposeMessage.getText().toString(),typeMethod,0,true,false);
				}
			}
			else if(typeMethod==1 && checkedValue.equalsIgnoreCase("resp"))
			{
				if(isLocked)
				{
					int max_Id=-1;
					max_Id=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
					saveId=DBQuery.insertConfigMessage(mActivityTabs,max_Id+1,etComposeMessage.getText().toString(),typeMethod,0,false,false);
				}
				else{
					saveId=DBQuery.insertConfigMessage(mActivityTabs,id,etComposeMessage.getText().toString(),typeMethod,0,true,false);
				}
			}
			else
			{
				int maxId=-1;   //typeMethod - 0 For Init  typeMethod - 1 For resp
				if(typeMethod==0 && checkedValue.equalsIgnoreCase("resp"))
				{
					if(isLocked)
					{
						maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
						saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),1,0,false,false);
					}
					else{
						maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
						saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),1,0,false,false);
					}
				}
				else if(typeMethod==1 && checkedValue.equalsIgnoreCase("init"))
				{
					if(isLocked)
					{
						maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
						saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),0,0,false,false);
					}
					else
					{
						maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
						saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),0,0,false,false);
					}
				}
				else if(checkedValue.equalsIgnoreCase("both"))
				{
					if(typeMethod==0)
					{
						if(isLocked)
						{
							maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
							saveId=DBQuery.insertConfigMessage(mActivityTabs,id,etComposeMessage.getText().toString(),typeMethod,isLockedMethod,true,false);
							maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
							saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),1,0,false,false);
						}

						else
						{
							saveId=DBQuery.insertConfigMessage(mActivityTabs,id,etComposeMessage.getText().toString(),typeMethod,isLockedMethod,true,false);
							maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
							saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),1,isLockedMethod,false,false);
						}
					}
					else if(typeMethod==1)
					{
						if(isLocked)
						{
							maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
							saveId=DBQuery.insertConfigMessage(mActivityTabs,id,etComposeMessage.getText().toString(),typeMethod,isLockedMethod,true,false);
							maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
							saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),0,0,false,false);
						}
						else
						{
							saveId=DBQuery.insertConfigMessage(mActivityTabs,id,etComposeMessage.getText().toString(),typeMethod,isLockedMethod,true,false);
							maxId=DBQuery.getConfigMessagesMaxCount(mActivityTabs);
							saveId=DBQuery.insertConfigMessage(mActivityTabs,maxId+1,etComposeMessage.getText().toString(),0,isLockedMethod,false,false);
						}
					}
				}
			}
		}
		if(saveId>0)
		{
			MessageSaveConfirmationDialog objDialog=new MessageSaveConfirmationDialog();
			if(id==-1)  //Case Of New Message Insertion
			{
				objDialog.newInstance("",((HomeScreenActivity)mActivityTabs),"Message Saved Successfully",
						iNotifyGalleryDialog,typeMethod);
			}
			else if(id>0)  //Case Of Update
			{
				objDialog.newInstance("",((HomeScreenActivity)mActivityTabs),"Message Updated Successfully",
						iNotifyGalleryDialog,typeMethod);
			}
			objDialog.show(getChildFragmentManager(),"test");
		}
	}

	/**
	 *  interface to delete configured messages
	 */
	INotifyGalleryDialog iNotifyDeleteMessage=new INotifyGalleryDialog() {

		@Override
		public void yes() {
			int i=DBQuery.deleteConfigMessage(mActivity,String.valueOf(id));
			if(i>=1)
			{
				GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etComposeMessage);
				((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
				if(type==0)
				{
					if(iNotifyGalleryDialog!=null)
						iNotifyGalleryDialog.yes();
				}
				else if(type==1)
				{
					if(iNotifyGalleryDialog!=null)
						iNotifyGalleryDialog.no();
				}
			}
			else{
				//				Toast.makeText(mActivityTabs, i+"", 1000).show();
			}
		}
		@Override
		public void no() {
		}
	};

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnBack)
		{
			GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etComposeMessage);
			((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
		}
		else if(v.getId()==R.id.btnSaveMessage)
		{
			if(etComposeMessage.getText().toString().trim().equals(""))
			{
				ImageRequestDialog dialog=new ImageRequestDialog();
				dialog.newInstance("",mActivityTabs,"Please Enter Message","",null);
				dialog.show(getChildFragmentManager(), "test");
			}
			else if(DBQuery.checkConfigMessageExistence(mActivityTabs, etComposeMessage.getText().toString().trim())>0)
			{
				ImageRequestDialog dialog=new ImageRequestDialog();
				dialog.newInstance("", mActivityTabs,"Message already exists","",null,null);
				dialog.show(getChildFragmentManager(),"test");
			}
			else{
				//check type     type -0 For Init     type -1 For resp
				if(isLocked)
					saveInitResponseMessage(type,1);
				else if(!isLocked)
					saveInitResponseMessage(type,0);
				saveState.setChanged(mActivity, true);
			}
		}
		else if(v.getId()==R.id.btnCancel)
		{
			GlobalConfig_Methods.hideKeyBoard(mActivityTabs, etComposeMessage);
			((HomeScreenActivity)mActivityTabs).fragmentManager.popBackStack();
		}
		else if(v.getId()==R.id.btnDelete)
		{
			MessageDeleteConfirmation dialogConfirmation=new MessageDeleteConfirmation();
			dialogConfirmation.newInstance("",mActivityTabs,"Do you want to delete this message?","", iNotifyDeleteMessage);
			dialogConfirmation.show(getChildFragmentManager(), "test");
			//			DBQuery.deleteConfigMessage(mActivity,id);
		}
	}
}
