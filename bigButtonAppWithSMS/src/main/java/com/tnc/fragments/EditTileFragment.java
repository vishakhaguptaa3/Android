/*package com.tnc.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.base.BaseFragmentTabs;
import com.tnc.bean.AddTileBean;
import com.tnc.bean.ContactTilesBean;
import com.tnc.common.CustomFonts;
import com.tnc.common.GlobalConfig_Methods;
import com.tnc.database.DBQuery;
import com.tnc.dialog.ImageRequestDialog;
import com.tnc.dialog.SelectImagePopup;
import com.tnc.dialog.TileUpdateSuccessDialog;
import com.tnc.homescreen.HomeScreenActivity;
import com.tnc.imagecropper.CropImage;
import com.tnc.interfaces.INotifyGalleryDialog;
import com.tnc.preferences.SharedPreference;
import com.tnc.utility.Logs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class EditTileFragment extends BaseFragmentTabs{

	TextView tvTitle,tvStep,tvChooseContact,tvContact,tvContactPhone,tvIsEmergency,tvIsdCode,tvAddPhoto;
	Button btnBack,btnHome,btnSave;
	FrameLayout flBackArrow,flInformationButton;
	EditText etContactName,etContactNumber,etISDCode;
	CheckBox chkBoxIsEmergency;
	ImageView imViewUser;
	AddTileBean addTileBean=null;
	String TAG="CreateContactFragment";
	ImageRequestDialog dialogAlert=null;
	Context mActivity;
	private Uri fileUri;
	public static final int REQUEST_CODE_GALLERY = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
	String picturePath = "";
	ContactTilesBean objTileEdit;

	public EditTileFragment newInstance(Context mActivity,ContactTilesBean objTileEdit)
	{
		EditTileFragment frag = new EditTileFragment();
		this.mActivity=mActivity;
		this.objTileEdit=objTileEdit;
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.edittilefragment, container, false);
		idInitialization(view);
		return view;
	}

	@Override
	public void onPause() {
		GlobalConfig_Methods.hideKeyBoard(mActivity, etContactName);
		GlobalConfig_Methods.hideKeyBoard(mActivity, etContactNumber);
		GlobalConfig_Methods.hideKeyBoard(mActivity, etISDCode);
		super.onPause();
	}


	
	 * Initialization of widgets/views
	 * 
	private void idInitialization(View view)
	{
		saveState=new SharedPreference();
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvChooseContact=(TextView) view.findViewById(R.id.tvChooseContact);
		tvStep=(TextView) view.findViewById(R.id.tvStep);
		tvContact=(TextView) view.findViewById(R.id.tvContact);
		tvContactPhone=(TextView) view.findViewById(R.id.tvContactPhone);
		tvIsEmergency=(TextView) view.findViewById(R.id.tvIsEmergency);
		tvIsdCode=(TextView) view.findViewById(R.id.tvIsdCode);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		etContactName=(EditText) view.findViewById(R.id.etContactName);
		etContactNumber=(EditText) view.findViewById(R.id.etContactNumber);
		etISDCode=(EditText) view.findViewById(R.id.etISDCode);
		chkBoxIsEmergency=(CheckBox) view.findViewById(R.id.chkBoxIsEmergency);
		btnSave=(Button) view.findViewById(R.id.btnSave);	
		flInformationButton=(FrameLayout)view.findViewById(R.id.flInformationButton);
		btnHome=(Button) view.findViewById(R.id.btnHome);
		tvAddPhoto=(TextView) view.findViewById(R.id.tvAddPhoto);
		tvAddPhoto.setText(Html.fromHtml("Add<br>Photo").toString());
		imViewUser = (ImageView) view.findViewById(R.id.imViewUser);
		tvContact.setText("Button Name");
		tvContactPhone.setText("Button Number");
		tvIsEmergency.setText("Is Emergency");
		flBackArrow.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		setValues();
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvStep, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvChooseContact, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvContact, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvContactPhone, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etContactName, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etContactNumber, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfEditText(getActivity(),etISDCode, "fonts/Roboto-Light_1.ttf");
		CustomFonts.setFontOfButton(getActivity(),btnSave, "fonts/Roboto-Regular_1.ttf");

		btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mActivity instanceof MainBaseActivity)
				{
					((MainBaseActivity)mActivity).fragmentManager.popBackStack();
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
				}
			}
		});
		imViewUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectImagePopup dialogSelectimage = new SelectImagePopup();
				dialogSelectimage.setCancelable(false);
				if (mActivity instanceof MainBaseActivity) {
					dialogSelectimage.newInstance("Choose Image",
							((MainBaseActivity) mActivity), iObject);
					dialogSelectimage.show(((MainBaseActivity) mActivity)
							.getSupportFragmentManager(), "test");
				} else if (mActivity instanceof HomeScreenActivity) {
					dialogSelectimage.newInstance("Choose Image",
							((HomeScreenActivity) mActivity), iObject);
					dialogSelectimage.show(((HomeScreenActivity) mActivity)
							.getSupportFragmentManager(), "test");
				}
			}
		});

		btnSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ContactTilesBean objContactTiles=new ContactTilesBean();
				Bitmap _bitmap=null;
				byte[] arrayImage=null;
				
				try {
					_bitmap=((BitmapDrawable)((ImageView)imViewUser).getDrawable()).getBitmap();
					if(_bitmap!=null)
					{
						ByteArrayOutputStream blob = new ByteArrayOutputStream();
						_bitmap.compress(CompressFormat.PNG, 100 ignored for PNG, blob);
						arrayImage = blob.toByteArray();
					}
					else{
						_bitmap=((BitmapDrawable)(mActivityTabs.getResources().getDrawable(R.drawable.no_image))).getBitmap();
						ByteArrayOutputStream blob = new ByteArrayOutputStream();
						_bitmap.compress(CompressFormat.PNG, 100 ignored for PNG, blob);
						arrayImage = blob.toByteArray();
					}
				} catch (Exception e) {
					e.getMessage();
				}

				objContactTiles.setName(etContactName.getText().toString());
				objContactTiles.setPhoneNumber(etContactNumber.getText().toString());
				objContactTiles.setPrefix(etISDCode.getText().toString());
				objTileEdit.setIsEmergency(chkBoxIsEmergency.isChecked());
				objContactTiles.setImage(arrayImage);	
				objContactTiles.setTilePosition(objTileEdit.getTilePosition());
				DBQuery.updateTile(mActivity,objContactTiles);	

				TileUpdateSuccessDialog dialogTileUpdate=new TileUpdateSuccessDialog();
				dialogTileUpdate.setCancelable(false);
				if(mActivity instanceof MainBaseActivity)
				{
					dialogTileUpdate.newInstance("",((MainBaseActivity)mActivity),"Tile Updated Successfully");
					dialogTileUpdate.show(getChildFragmentManager(), "Test");
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					dialogTileUpdate.newInstance("",((HomeScreenActivity)mActivity),"Tile Updated Successfully");
					dialogTileUpdate.show(getChildFragmentManager(), "Test");
				}
			}
		});
	}

	private void setValues()
	{
		if(objTileEdit.getImage()!=null && objTileEdit.getImage().length>0)
		{
			byte arrayImage[]=objTileEdit.getImage();
			imViewUser.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
		}

		etContactName.setText(objTileEdit.getName());
		etContactNumber.setText(objTileEdit.getPhoneNumber());
		etISDCode.setText(objTileEdit.getPrefix());
		if(objTileEdit.isIsEmergency())
			chkBoxIsEmergency.setChecked(true);
		else 
			chkBoxIsEmergency.setChecked(false);
	}

	INotifyGalleryDialog iObject = new INotifyGalleryDialog() {
		@Override
		public void yes() {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputImageUri();
			if (fileUri != null) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			}
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		}

		@Override
		public void no() {
			if (mActivity instanceof MainBaseActivity) {
				((MainBaseActivity) mActivity)
				.setFragment(new GalleryFragment());
			} else if (mActivity instanceof HomeScreenActivity) {
				((HomeScreenActivity) mActivity)
				.setFragment(new GalleryFragment());
			}
		}
	};

	private void cropImage(boolean isCamera) {
		if (!MainBaseActivity.selectedImagepath.equals("")) {
			MainBaseActivity._bitmap = null;
			Intent intent = new Intent(getActivity(), CropImage.class);
			if (isCamera)
				intent.putExtra(CropImage.IMAGE_PATH, picturePath);
			else
				intent.putExtra(CropImage.IMAGE_PATH,
						MainBaseActivity.selectedImagepath);
			intent.putExtra(CropImage.SCALE, true);
			intent.putExtra(CropImage.ASPECT_X, 1);
			intent.putExtra(CropImage.ASPECT_Y, 1);
			getActivity().startActivityForResult(intent,
					REQUEST_CODE_CROP_IMAGE);
		}
	}

	private static Uri getOutputImageUri() {
		if (Environment.getExternalStorageState() == null) {
			return null;
		}

		File mediaStorage = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
				"BIG_BUTTON_IMAGES");
		if (!mediaStorage.exists() && !mediaStorage.mkdirs()) {
			Logs.writeLog("ChooseImageFragment", "getOutPutVideoUri",
					"failed to create directory:");
			return null;
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
		.format(new Date());
		File mediaFile = new File(mediaStorage, "IMG_" + timeStamp + ".jpg");
		return Uri.fromFile(mediaFile);
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (MainBaseActivity.isCameraCanceled) {
				MainBaseActivity._bitmap = null;
				MainBaseActivity.isImageSelected = false;
				MainBaseActivity.isImageRequested = false;
				if (mActivity instanceof MainBaseActivity)
					((MainBaseActivity) mActivity).objFragment = null;
				else if (mActivity instanceof HomeScreenActivity)
					((HomeScreenActivity) mActivity).objFragment = null;
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				fileUri = getOutputImageUri();
				if (fileUri != null) {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				}
				startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
				MainBaseActivity.isCameraCanceled = false;
			}
			
			 * if(MainBaseActivity._bitmap!=null &&
			 * MainBaseActivity.isImageSelected) { MainBaseActivity._bitmap =
			 * getResizedBitmap(MainBaseActivity._bitmap, 250, 250);
			 * imViewUser.setImageBitmap(MainBaseActivity._bitmap); }
			 
			if (MainBaseActivity._bitmap != null) {
				MainBaseActivity._bitmap = getResizedBitmap(
						MainBaseActivity._bitmap, 250, 250);
				imViewUser.setImageBitmap(MainBaseActivity._bitmap);
			}
			//			setCheckBoxClickability();
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
				try {
					Uri selectedImage = fileUri;
					picturePath = selectedImage.getPath();
					MainBaseActivity.selectedImagepath = picturePath;
					cropImage(true);	
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MainBaseActivity._bitmap = null;
		HomeTabFragment.isLongClicked=false;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!isFirstTime)
		{
			//			UsPhoneNumberFormatter addLineNumberFormatter = new UsPhoneNumberFormatter(
			//					new WeakReference<EditText>(etContactNumber));
			//			etContactNumber.addTextChangedListener(addLineNumberFormatter);

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

}
*/