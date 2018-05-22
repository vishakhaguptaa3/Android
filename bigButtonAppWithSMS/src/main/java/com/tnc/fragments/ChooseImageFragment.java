package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.ContactListAdapter;
import com.tnc.base.BaseFragment;
import com.tnc.bean.BBContactsBean;
import com.tnc.bean.CheckTncUserBean;
import com.tnc.bean.ContactDetailsBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalCommonValues;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageGalleryDialog;
import com.tnc.dialog.RegistrationCheckDialog;
import com.tnc.dialog.SendMessagePopupDialog;
import com.tnc.dialog.ShowDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.httpconnection.MyHttpConnection;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;
import com.tnc.webresponse.CheckTncUserResponseBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

/**
 * class to choose image to be display on the home screen on the user contact tile 
 *  @author a3logics
 */

public class ChooseImageFragment extends BaseFragment  implements OnClickListener
{
	private FrameLayout flBackArrow,flInformationButton;
	private TextView tvTitle,tvStep,tvChooseButton;
	private ListView lvImageMenu;
	private Button btnBack,btnSkip,btnHome;
	private String title;
	private ArrayList<String> listMenu;
	private ContactListAdapter adapterMenu=null;
	private String path="";
	private Bitmap image=null;
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	public static final int REQUEST_CODE_GALLERY      = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
	public static final int REQUEST_SMS = 0x4;
	private final int PICK_IMAGE_REQUEST = 210;
	private Uri fileUri;
	private String picturePath="";
	private ContactDetailsBean selectedContactBean=null;
	private Context mActivity;
	private int adapterSelected_position = 0;
	private Gson gson=null;
	private LinearLayout llTopHeader,llButtonHolder;

	public ChooseImageFragment()
	{
	}

	public ChooseImageFragment(ContactDetailsBean selectedContactBean)
	{
		this.selectedContactBean=new ContactDetailsBean();
		this.selectedContactBean=selectedContactBean;
	}

	public ChooseImageFragment newInstance(Context mActivity)
	{
		ChooseImageFragment frag = new ChooseImageFragment();
		this.mActivity=mActivity;
		//		this.objTileDetailBean=objTileDetailBean;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.chooseimagefragment, container, false);
		idInitialization(view);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void idInitialization(View view)
	{
		saveState=new SharedPreference();

		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		tvChooseButton=(TextView) view.findViewById(R.id.tvChooseButton);
		lvImageMenu=(ListView) view.findViewById(R.id.lvImageMenu);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		btnSkip=(Button) view.findViewById(R.id.btnSkip);
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		llTopHeader=(LinearLayout) view.findViewById(R.id.llTopHeader);
		llButtonHolder=(LinearLayout) view.findViewById(R.id.llButtonHolder);

		tvChooseButton.setAllCaps(true);

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
		tvStep.setText("STEP 3");
		tvStep.setVisibility(View.GONE);
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/comic_sans_ms_regular.ttf");
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvChooseButton, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnSkip, "fonts/Roboto-Regular_1.ttf");
		listMenu=new ArrayList<String>();
		listMenu.add(getActivity().getResources().getString(R.string.txtChooseFromGallery));
		listMenu.add(getActivity().getResources().getString(R.string.txtTakeNewPhoto));
		listMenu.add(getActivity().getResources().getString(R.string.txtRequestFromContact));
		listMenu.add(getActivity().getResources().getString(R.string.txtClipArt));
		flBackArrow.setVisibility(View.VISIBLE);
		adapterMenu=new ContactListAdapter(mActivity,null,listMenu,true);
		adapterMenu.notifyDataSetChanged();
		lvImageMenu.setAdapter(adapterMenu);
		if(!MainBaseActivity.isImageSelected)
			MainBaseActivity._bitmap=null;
		llTopHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		llButtonHolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		btnHome.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
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
		});

		lvImageMenu.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unused")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3)
			{
				if(position==0) //From Gallery
				{
					adapterSelected_position = position;
					adapterMenu.setRowColor(adapterSelected_position, true);

					MainBaseActivity.isImageRequested=false;
					MainBaseActivity.isSmsSent=false;
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(
							Intent.createChooser(intent, "Complete action using"),
							PICK_IMAGE_REQUEST);
				}
				else if(position==1) //Camera Click
				{
					adapterSelected_position = position;
					adapterMenu.setRowColor(adapterSelected_position, true);
					MainBaseActivity._bitmap=null;
					MainBaseActivity.isImageSelected=false;
					MainBaseActivity.isImageRequested=false;
					MainBaseActivity.isSmsSent=false;
					if(mActivity instanceof MainBaseActivity)
						((MainBaseActivity)mActivity).objFragment=null;
					else if(mActivity instanceof HomeScreenActivity)
						((HomeScreenActivity)mActivity).objFragment=null;
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri =GlobalConfig_Methods. getOutputImageUri(); // create a file to save the image in specific folder
					if (fileUri != null) {
						intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					}

					startActivityForResult(intent,REQUEST_CODE_TAKE_PICTURE);
				}
				else if(position==2) //Image Request
				{
					if(MainBaseActivity.objTileDetailBeanStatic!=null && !MainBaseActivity.objTileDetailBeanStatic.Is_Mobile ){
					}
					else{
						adapterSelected_position = position;
						adapterMenu.setRowColor(adapterSelected_position, true);
					}

					if(MainBaseActivity.objTileDetailBeanStatic.Is_Mobile)
					{
						MainBaseActivity.isImageSelected=false;
						MainBaseActivity._bitmap=null;

						String number=GlobalConfig_Methods.getBBNumberToCheck(mActivity,MainBaseActivity.selectedPrefixCodeForTileDetails+MainBaseActivity.selectedCountryCodeForTileDetails+MainBaseActivity.contactNumberForTile);
						String countryCodeRegisteredUser="",numberRegisteredUser="",isdCodeRegisteredUser="";
						boolean isMobileRegisteredUser=false,isdCodeFlagRegisteredUser=false,isTncUserRegisteredUser=false;
						String []arrayUserDetails=number.split(",");
						countryCodeRegisteredUser=arrayUserDetails[0];
						numberRegisteredUser=arrayUserDetails[1];
						isMobileRegisteredUser=Boolean.parseBoolean(arrayUserDetails[2]);
						isdCodeFlagRegisteredUser=Boolean.parseBoolean(arrayUserDetails[3]);
						isdCodeRegisteredUser=arrayUserDetails[4];
						isTncUserRegisteredUser=Boolean.parseBoolean(arrayUserDetails[5]);

						if(saveState.isRegistered(getActivity()) && isTncUserRegisteredUser)
						{
							int bbid=DBQuery.getBBIDFromPhoneNumberAndCountryCode(mActivity,numberRegisteredUser,countryCodeRegisteredUser);
							saveState.setBBID_User(mActivity,String.valueOf(bbid));
							MainBaseActivity.selectedBBID=saveState.getBBID(mActivity);
							MainBaseActivity.isImageRequested=true;
							MainBaseActivity.isNotificationPushSend=true;
							MainBaseActivity.isSmsSent=false;
							TilePreviewFragment objTilePreview=null;
							if(mActivity instanceof MainBaseActivity)
							{
								objTilePreview=new TilePreviewFragment();
								((MainBaseActivity)mActivity).objFragment=objTilePreview;
								((MainBaseActivity)mActivity).setFragment(((MainBaseActivity)mActivity).objFragment);
							}
							else if(mActivity instanceof HomeScreenActivity)
							{
								objTilePreview=new TilePreviewFragment();
								((HomeScreenActivity)mActivity).objFragment=objTilePreview;
								((HomeScreenActivity)mActivity).setFragment(((HomeScreenActivity)mActivity).objFragment);
							}
						}
						else
						{
							if(saveState.isRegistered(mActivity))
							{
								CheckTncUserBean checkTncuserBean=new CheckTncUserBean();
								String _IsdCode="";
								checkTncuserBean.setNumber(MainBaseActivity.selectedCountryCodeForTileDetails+"-"+MainBaseActivity.contactNumberForTile);//(GlobalConfig_Methods.trimSpecialCharactersFromString(_IsdCode)+"-"+GlobalConfig_Methods.trimSpecialCharactersFromString(MainBaseActivity.objTileDetailBeanStatic.Phone));
								checkTncUser(checkTncuserBean);
							}
							else if(!saveState.isRegistered(getActivity()))  //if(!saveState.isRegistered(getActivity()) && isContactMatched)
							{
								MainBaseActivity.isNotificationPushSend=false;
								MainBaseActivity.isImageRequested=false;  //Made False for phase-4
								MainBaseActivity.isSmsSent=false;
								//	Comments made for Phase-4
								////Made False for phase-4

								RegistrationCheckDialog dialog = new RegistrationCheckDialog();
								dialog.setCancelable(false);

								if(mActivity instanceof MainBaseActivity)
								{
									dialog.newInstance("",((MainBaseActivity)mActivity),Html.fromHtml(
											"Please create profile <br>to use this feature").toString(),"", null,iNotifyGalleryDialog);

								}
								else if(mActivity instanceof HomeScreenActivity)
								{
									dialog.newInstance("",((HomeScreenActivity)mActivity),Html.fromHtml(
											"Please create profile <br>to use this feature").toString(),"", null,iNotifyGalleryDialog);

								}
								dialog.show(getChildFragmentManager(), "test");
							}
						}
					}
				}
				else if(position==3) //Clip Arts
				{
					adapterSelected_position = position;
					adapterMenu.setRowColor(adapterSelected_position, true);
					MainBaseActivity.isImageRequested=false;
					MainBaseActivity.isSmsSent=false;
					ClipArtsFragment  objClipArts=null;
					if(mActivity instanceof MainBaseActivity)
					{
						objClipArts=new ClipArtsFragment();
						((MainBaseActivity)mActivity).objFragment=new ClipArtsFragment();
						((MainBaseActivity)mActivity).setFragment(((MainBaseActivity)mActivity).objFragment);
					}
					else if(mActivity instanceof HomeScreenActivity)
					{
						objClipArts=new ClipArtsFragment();
						((HomeScreenActivity)mActivity).objFragment=objClipArts;
						((HomeScreenActivity)mActivity).setFragment(objClipArts);
					}
				}
			}
		});
		btnBack.setOnClickListener(this);
		btnSkip.setOnClickListener(this);
	}

	/**
	 * request for registration
	 *
	 * @param : userRegistrationBean
	 */
	private void checkTncUser(CheckTncUserBean checkTncuserBean) {
		try {
			gson = new Gson();
			String stingGson = gson.toJson(checkTncuserBean);
			cz.msebera.android.httpclient.entity.StringEntity stringEntity;
			stringEntity = new cz.msebera.android.httpclient.entity.StringEntity(stingGson);
			MyHttpConnection.postWithJsonEntityHeader(mActivity,
					GlobalCommonValues.CHECKTNCUSER, stringEntity,
					checkTncUserResponseHandler,
					mActivity.getString(R.string.private_key), "");
		} catch (Exception e) {
			e.getMessage();
		}
	}

	TransparentProgressDialog progress;

	//async task to call the web service to check the user as TnC user
	AsyncHttpResponseHandler checkTncUserResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onStart() {
			// Initiated the request
			//			if ((!progress.isShowing()))
			//				progress.show();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			super.onSuccess(statusCode, headers, response);
			// Successfully got a response
			try {
				if (response != null) {
					Logs.writeLog("checkTncUserResponseHandler", "OnSuccess", response.toString());
					getResponseCheckTncUserResponseHandler(response.toString());
				}
			} catch (JsonSyntaxException jsone) {
				jsone.toString();
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
			// Response failed :(
			if (response != null)
				Logs.writeLog("checkTncUserResponseHandler", "OnFailure", response);
		}

		@Override
		public void onFinish() {
			// Completed the request (either success or failure)
		}
	};

	public void onStop() {
		super.onStop();
	}

	/**
	 *  Handle response from the server for the request being made to check the user as TnC user
	 * @param response
	 */
	private void getResponseCheckTncUserResponseHandler(String response)
	{
		if (!TextUtils.isEmpty(response) && GlobalConfig_Methods.isJsonString(response))
		{
			gson = new Gson();
			CheckTncUserResponseBean get_Response = gson.fromJson(response,CheckTncUserResponseBean.class);
			if (get_Response.getReponseCode().equals(GlobalCommonValues.SUCCESS_CODE))
			{
				if(!saveState.getBBID(mActivity).trim().equals(get_Response.getData.bbid.trim()))
				{
					MainBaseActivity.selectedBBID=get_Response.getData.bbid;
					saveState.setBBID_User(mActivity,String.valueOf(get_Response.getData.bbid));
					MainBaseActivity.isNotificationPushSend=true;
				}

				// add number in BBContacts Table if it doesn't exist already
				ArrayList<BBContactsBean> mListBBContactsFromDB = new ArrayList<BBContactsBean>();
				boolean isTnCUserExist = false;

				mListBBContactsFromDB = DBQuery.getAllBBContacts(mActivity);
				if(mListBBContactsFromDB!=null && mListBBContactsFromDB.size()>0){
					tagloop: for(int j=0;j<mListBBContactsFromDB.size();j++){
						String mTncUserPhoneNumber = mListBBContactsFromDB.get(j).getCountryCode() + mListBBContactsFromDB.get(j).getPhoneNumber();
						if(mTncUserPhoneNumber.equals(MainBaseActivity.selectedCountryCodeForTileDetails+MainBaseActivity.contactNumberForTile)){
							isTnCUserExist = true;
							break tagloop;
						}
					}
				}

				if(!isTnCUserExist){
					ArrayList<BBContactsBean> mListBBContact = new ArrayList<BBContactsBean>();
					BBContactsBean mObjBBContact = new BBContactsBean();
					mObjBBContact.setBBID(Integer.parseInt(get_Response.getData.bbid.trim()));
					mObjBBContact.setCountryCode(MainBaseActivity.selectedCountryCodeForTileDetails);
					mObjBBContact.setPhoneNumber(MainBaseActivity.contactNumberForTile);
					mObjBBContact.setName(MainBaseActivity.objTileDetailBeanStatic.Name);
					mObjBBContact.setImage("");
					mObjBBContact.setMobID(11000);
					mListBBContact.add(mObjBBContact);

					DBQuery.insertBBContact(getActivity(),mListBBContact);
				}

				MainBaseActivity.isImageRequested=true;
				MainBaseActivity.isSmsSent=false;
				TilePreviewFragment objTilePreview=null;
				if(mActivity instanceof MainBaseActivity)
				{
					objTilePreview=new TilePreviewFragment();
					((MainBaseActivity)mActivity).objFragment=objTilePreview;
					((MainBaseActivity)mActivity).setFragment(((MainBaseActivity)mActivity).objFragment);
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					objTilePreview=new TilePreviewFragment();
					((HomeScreenActivity)mActivity).objFragment=objTilePreview;
					((HomeScreenActivity)mActivity).setFragment(((HomeScreenActivity)mActivity).objFragment);
				}
			}
			else{
				MainBaseActivity.isNotificationPushSend=false;
				MainBaseActivity.isImageRequested=true;
				MainBaseActivity.isSmsSent=true;
				sendSMS();
			}
		}
		else {
			Log.d("improper_response",response);
			ShowDialog.alert(
					mActivity,
					"",
					getResources().getString(
							R.string.improper_response_network));
		}
	}

	private void sendSMS(){
		iNotifyGalleryDialog.yes();

		boolean isAirplaneModeON = false;

		try{
			isAirplaneModeON = GlobalConfig_Methods.isAirplaneModeOn(mActivity);
		}catch(Exception e){
			e.getMessage();
		}

		if(isAirplaneModeON){
			GlobalConfig_Methods.showSimErrorDialog(mActivity,
					"The device is in Airplane mode or has no sim card or outside of cellular service range");
		}else{
			String mSimState = GlobalConfig_Methods.checkSimState(mActivity);
			if(mSimState.equalsIgnoreCase(GlobalCommonValues.SUCCESS)){
				SendMessagePopupDialog dialogSendSMS=new SendMessagePopupDialog();

				if(mActivity instanceof MainBaseActivity)
				{
					dialogSendSMS.newInstance("", ((MainBaseActivity)mActivity),"","", alertBack);
					dialogSendSMS.show(((MainBaseActivity)mActivity).getSupportFragmentManager(), "Test");
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					dialogSendSMS.newInstance("", ((HomeScreenActivity)mActivity),"","", alertBack);
					dialogSendSMS.show(((HomeScreenActivity)mActivity).getSupportFragmentManager(), "Test");
				}
			}else{
				GlobalConfig_Methods.showSimErrorDialog(mActivity, mSimState);
			}
		}

//		String message = getResources().getString(R.string.app_name) +
//				"\nwould like to use your text messaging service.You may incur text messaging charges from your carrier. Also, please make sure this is not a landline.";
//		String messageSub="Would you like to Continue?";
//		imageGalleryDialog = new ImageGalleryDialog();
//		imageGalleryDialog.setCancelable(false);
//		imageGalleryDialog.newInstance("",mActivity,message,messageSub,alertBack,iNotifyGalleryDialog);
//		imageGalleryDialog.setCancelable(false);
//		imageGalleryDialog.show(getChildFragmentManager(), "test");
	}

	INotifyGalleryDialog iNotifyGalleryDialog = new INotifyGalleryDialog() {

		@Override
		public void yes() {
			adapterMenu.setRowColor(adapterSelected_position, false);
		}

		@Override
		public void no() {
			adapterMenu.setRowColor(adapterSelected_position, false);
		}
	};

	void selectedImageandPath(String path,Bitmap image)
	{
		this.path=path;
		this.image=image;
	}

	/**
	 *
	 * @param f-> image file which is to be decoded in bitmap
	 * @return bitmap
	 */
	Bitmap decodeFile(File f)
	{
		try
		{
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 100;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true)
			{
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		}
		catch (FileNotFoundException e)
		{
			Logs.writeLog("MainBaseActivity", "decodeFile",e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode ==REQUEST_CODE_TAKE_PICTURE){
				Uri selectedImage = fileUri;
				picturePath= selectedImage.getPath();
				MainBaseActivity.selectedImagepath=picturePath;
				cropImage(true);
			}
			else if(requestCode==PICK_IMAGE_REQUEST) {
				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				@SuppressWarnings("unused")
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				MainBaseActivity.selectedImagepath=picturePath;
				Intent cropIntent=new Intent(getActivity(),CropImage.class);
				cropIntent.putExtra(CropImage.IMAGE_PATH,MainBaseActivity.selectedImagepath);
				cropIntent.putExtra(CropImage.SCALE, true);
				cropIntent.putExtra(CropImage.ASPECT_X, 1);
				cropIntent.putExtra(CropImage.ASPECT_Y, 1);
				getActivity().startActivity(cropIntent);
			}
		}
	}

	/**
	 * get Uri of the image
	 * @param inContext
	 * @param inImage-> Bitmap of the image
	 * @return Uri
	 */
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	/**
	 *
	 * @param uri
	 * @return String-> path of Image Uri being passed as an argument
	 */
	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	/**
	 * navigate to the cropping tool screen and allow us to crop the image
	 * @param isCamera
	 */
	private void cropImage(boolean isCamera)
	{
		if(!MainBaseActivity.selectedImagepath.equals(""))
		{
			MainBaseActivity._bitmap=null;
			Intent intent = new Intent(getActivity(), CropImage.class);
			if(isCamera)
				intent.putExtra(CropImage.IMAGE_PATH,picturePath);
			else
				intent.putExtra(CropImage.IMAGE_PATH,MainBaseActivity.selectedImagepath);
			intent.putExtra(CropImage.SCALE, true);
			intent.putExtra(CropImage.ASPECT_X, 1);
			intent.putExtra(CropImage.ASPECT_Y, 1);
			getActivity().startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		lvImageMenu.setEnabled(false);
		lvImageMenu.setClickable(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		lvImageMenu.setEnabled(true);
		lvImageMenu.setClickable(true);
		adapterMenu.setRowColor(adapterSelected_position,false);
		if(MainBaseActivity.isCameraCanceled)
		{
			MainBaseActivity._bitmap=null;
			MainBaseActivity.isImageSelected=false;
			MainBaseActivity.isImageRequested=false;
			if(mActivity instanceof MainBaseActivity)
				((MainBaseActivity)mActivity).objFragment=null;
			else if(mActivity instanceof HomeScreenActivity)
				((HomeScreenActivity)mActivity).objFragment=null;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			fileUri = GlobalConfig_Methods.getOutputImageUri();
			if (fileUri != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			}
			startActivityForResult(intent,REQUEST_CODE_TAKE_PICTURE);
			MainBaseActivity.isCameraCanceled=false;
		}
		lvImageMenu.setAdapter(adapterMenu);
		if(MainBaseActivity._bitmap!=null && MainBaseActivity.isImageSelected)
		{
			MainBaseActivity._bitmap = GlobalConfig_Methods.getResizedBitmap(
					MainBaseActivity._bitmap, 250, 250);
			if(mActivity instanceof MainBaseActivity)
			{
				int num=((MainBaseActivity)mActivity).fragmentManager.getBackStackEntryCount();
				BackStackEntry backStackEntry=((MainBaseActivity)mActivity).fragmentManager.getBackStackEntryAt(num-1);
				if(!backStackEntry.getName().contains("TilePreview"))
				{
					((MainBaseActivity)mActivity).setFragment(new TilePreviewFragment());
				}
			}
			else if(mActivity instanceof HomeScreenActivity)
			{

				int num=((HomeScreenActivity)mActivity).fragmentManager.getBackStackEntryCount();
				BackStackEntry backStackEntry=((HomeScreenActivity)mActivity).fragmentManager.getBackStackEntryAt(num-1);
				if(!backStackEntry.getName().contains("TilePreview"))
				{
					((HomeScreenActivity)mActivity).setFragment(new TilePreviewFragment());
				}
			}
		}
	}

	/**
	 * save camera image to a path
	 */
	void saveCameraImage()
	{
		File camera_file = new File(Environment.getExternalStorageDirectory().toString());
		for (File temp : camera_file.listFiles())
		{
			if (temp.getName().equals("temp.jpg"))
			{
				camera_file = temp;
				break;
			}
		}
		try
		{
			Bitmap bitmap;
			bitmap = decodeFile(new File(camera_file.getAbsolutePath()));
			String path = android.os.Environment
					.getExternalStorageDirectory()
					+ File.separator
					+ "TNC";
			new File(path).mkdir();
			OutputStream outFile = null;
			File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
			try
			{
				file.createNewFile();
				outFile = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
				outFile.flush();
				outFile.close();
				selectedImageandPath(file.getAbsolutePath(), bitmap);
				((MainBaseActivity)mActivity).strImageBase64=GlobalConfig_Methods.getBase64EncodedImage(file.getAbsolutePath());
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * save gallery image
	 * @param data->Intent data
	 */
	void saveGalleryImage(Intent data)
	{
		Uri selectedImage = data.getData();
		String[] filePath = { MediaStore.Images.Media.DATA };
		Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,null, null, null);
		c.moveToFirst();
		int columnIndex = c.getColumnIndex(filePath[0]);
		String picturePath = c.getString(columnIndex);
		c.close();
		try
		{
			Bitmap bitmap;
			bitmap = decodeFile(new File(picturePath));
			String path = android.os.Environment.getExternalStorageDirectory()
					+ File.separator
					+ "TNCGallery";
			new File(path).mkdir();
			OutputStream outFile = null;
			File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
			try
			{
				file.createNewFile();
				outFile = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
				outFile.flush();
				outFile.close();
				selectedImageandPath(file.getAbsolutePath(), bitmap);
				((MainBaseActivity)mActivity).strImageBase64=GlobalConfig_Methods.getBase64EncodedImage(file.getAbsolutePath());
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnBack)
		{
			if(mActivity instanceof MainBaseActivity)
				((MainBaseActivity) mActivity).fragmentManager.popBackStack();
			if(mActivity instanceof HomeScreenActivity)
				((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
		}
		else if(v.getId()==R.id.btnSkip)
		{
			//Phase-4 Change
			if(MainBaseActivity.objTileEdit==null)
			{
				MainBaseActivity.isImageRequested=false;
			}
			else if(MainBaseActivity.objTileEdit!=null)
			{
				int status=MainBaseActivity.objTileEdit.getIsImagePending();
				if(status==0)
					MainBaseActivity.isImageRequested=false;
				else if(status==1)
					MainBaseActivity.isImageRequested=true;
			}

			MainBaseActivity._bitmap=null;
			MainBaseActivity.isImageSelected=false;
			MainBaseActivity.selectedImagepath=null;
			MainBaseActivity.isCameraCanceled=false;
			MainBaseActivity.isSmsSent=false;
			TilePreviewFragment objTilePreview=null;
			if(mActivity instanceof MainBaseActivity)
			{
				objTilePreview=new TilePreviewFragment();
				((MainBaseActivity)mActivity).objFragment=objTilePreview;
				((MainBaseActivity)mActivity).setFragment(((MainBaseActivity)mActivity).objFragment);
			}
			else if(mActivity instanceof HomeScreenActivity)
			{
				objTilePreview=new TilePreviewFragment();
				((HomeScreenActivity)mActivity).objFragment=objTilePreview;
				((HomeScreenActivity)mActivity).setFragment(((HomeScreenActivity)mActivity).objFragment);
			}
		}
	}
}
