/*package com.tnc.fragments;

import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.adapter.GalleryAdapter;
import com.tnc.adapter.ImageItem;
import com.tnc.base.BaseFragment;
import com.tnc.common.CustomFonts;
import com.tnc.common.TransparentProgressDialog;
import com.tnc.homescreen.HomeScreenActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

public class GalleryFragment extends BaseFragment
{
	TextView tvTitle,tvContent,tvGallery;
	GridView gvGallery;
	Button btnBack;
	FrameLayout flBackArrow;
	GalleryAdapter adapterGallery;
	Display display;
	int height=0,width=0;
	int view_item_width=0;
	Cursor _cursor;
	private Uri[] mUrls = null;
	private String[] strUrls = null;
	private String[] mNames = null;
	long lastId;
	public ArrayList<ImageItem> images = new ArrayList<ImageItem>();
	Cursor cursorContents;
	TransparentProgressDialog progress;
	public static boolean ifisfirstTime=true;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.galleryfragment, container, false);
		idInitialization(view);
		return view;
	} 
	private void idInitialization(View view)
	{
		tvTitle=(TextView) view.findViewById(R.id.tvTitle);
		tvContent=(TextView) view.findViewById(R.id.tvContent);
		tvGallery=(TextView) view.findViewById(R.id.tvGallery);
		gvGallery=(GridView) view.findViewById(R.id.gvGallery);
		flBackArrow=(FrameLayout) view.findViewById(R.id.flBackArrow);
		btnBack=(Button) view.findViewById(R.id.btnBack);
		flBackArrow.setVisibility(View.VISIBLE);
//		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/StencilStd.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvTitle, "fonts/Helvetica-Bold.otf");
		CustomFonts.setFontOfTextView(getActivity(),tvContent, "fonts/Roboto-Bold_1.ttf");
		CustomFonts.setFontOfTextView(getActivity(),tvGallery, "fonts/Roboto-Bold_1.ttf");
		tvContent.setText("Choose Button Image from Gallery");
		setDimensions();
		MainBaseActivity.selectedImagepath="";
		MainBaseActivity.isImageSelected=false;
		MainBaseActivity._bitmap=null;
		if(GalleryFragment.ifisfirstTime)
			new GetPhotographs().execute();
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				MainBaseActivity.isImageSelected=false;
				if(mActivity instanceof MainBaseActivity)
					((MainBaseActivity)mActivity).fragmentManager.popBackStack();
				else if(mActivity instanceof HomeScreenActivity)
					((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
			}
		});
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		if(GalleryFragment.ifisfirstTime)
		{
			if(MainBaseActivity._bitmap!=null && MainBaseActivity.isImageSelected)
			{
				TilePreviewFragment objTilePreview=null;
				if(mActivity instanceof MainBaseActivity)
				{
					if(((MainBaseActivity)mActivity).currentFragment!=null && !UserRegistration.isOnUserRegistration)
					{
						objTilePreview=new TilePreviewFragment();
						objTilePreview.newInstance("",((MainBaseActivity)mActivity));
						((MainBaseActivity)mActivity).setFragment(objTilePreview);
						GalleryFragment.ifisfirstTime=false;
					}
					else if(UserRegistration.isOnUserRegistration)
					{
						((MainBaseActivity)mActivity).fragmentManager.popBackStack();
					}
				}
				else if(mActivity instanceof HomeScreenActivity)
				{
					if(((HomeScreenActivity)mActivity).currentFragment!=null && !UserRegistration.isOnUserRegistration)
					{			
						objTilePreview=new TilePreviewFragment();
						objTilePreview.newInstance("",((HomeScreenActivity)mActivity));
						((HomeScreenActivity)mActivity).setFragment(objTilePreview);
					}
					else if(UserRegistration.isOnUserRegistration)
					{
						((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
					}
				}
			}
		}
		else if(!GalleryFragment.ifisfirstTime)
		{
			if(mActivity instanceof MainBaseActivity)	
			{
				((MainBaseActivity)mActivity).fragmentManager.popBackStack();
			}
			else if(mActivity instanceof HomeScreenActivity)	
			{
				((HomeScreenActivity)mActivity).fragmentManager.popBackStack();
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ifisfirstTime=true;
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		adapterGallery.notifyDataSetChanged();
	}
	private Cursor fetchImages()
	{
		//		_contentUri = MEDIA_EXTERNAL_CONTENT_URI;
		_cursor = mActivity.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);
		if (_cursor != null) 
		{
			new Thread() 
			{
				public void run() 
				{
					try 
					{
						int image_column_index = _cursor
								.getColumnIndex(MediaStore.Images.Media._ID);
						_cursor.moveToFirst();
						mUrls = new Uri[_cursor.getCount()];
						strUrls = new String[_cursor.getCount()];
						mNames = new String[_cursor.getCount()];
						for (int i = 0; i < _cursor.getCount(); i++) 
						{
							_cursor.moveToPosition(i);
							int id = _cursor.getInt(image_column_index);
							ImageItem imageItem = new ImageItem();
							imageItem.id = id;
							mUrls[i] = Uri.parse(_cursor.getString(1));
							strUrls[i] = _cursor.getString(1);
							mNames[i] = _cursor.getString(3);
							lastId = id;
							imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
									mActivity.getContentResolver(), id,
									MediaStore.Images.Thumbnails.MICRO_KIND, null);
							images.add(imageItem);
						}
					}
					catch (Exception e) 
					{
						e.getMessage();
					}
					//					myProgressDialog.dismiss();
				}
			}.start();

			gvGallery.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View v,int position, long id) 
				{
					Log.e("intent : ", ""+position);
					Log.e("imgUrls", strUrls+"");
					Log.e("position", position+"");
				}
			});
		}
		return _cursor;
	}

	*//**
	 * fetch gallery images
	 *
	 *//*
	class GetPhotographs extends AsyncTask<String, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			progress=new TransparentProgressDialog(mActivity, R.drawable.customspinner);
			if ((!progress.isShowing()))
				progress.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			//		_contentUri = MEDIA_EXTERNAL_CONTENT_URI;
			_cursor = mActivity.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
					null);//MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC"
			if (_cursor != null) 
			{
				try 
				{
					int image_column_index = _cursor
							.getColumnIndex(MediaStore.Images.Media._ID);
					_cursor.moveToFirst();
					mUrls = new Uri[_cursor.getCount()];
					strUrls = new String[_cursor.getCount()];
					mNames = new String[_cursor.getCount()];
					for (int i = 0; i < _cursor.getCount(); i++) 
					{
						_cursor.moveToPosition(i);
						int id = _cursor.getInt(image_column_index);
						ImageItem imageItem = new ImageItem();
						imageItem.id = id;
						mUrls[i] = Uri.parse(_cursor.getString(1));
						strUrls[i] = _cursor.getString(1);
						mNames[i] = _cursor.getString(3);
						lastId = id;
						imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
								mActivity.getContentResolver(), id,
								MediaStore.Images.Thumbnails.MICRO_KIND, null);
						images.add(imageItem);
					}
				}
				catch (Exception e) 
				{
					e.getMessage();
				}

				gvGallery.setOnItemClickListener(new OnItemClickListener()
				{
					public void onItemClick(AdapterView<?> parent, View v,int position, long id) 
					{
						Log.e("intent : ", ""+position);
						Log.e("imgUrls", strUrls+"");
						Log.e("position", position+"");
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			adapterGallery=new GalleryAdapter(mActivity,images,_cursor,mUrls,
					strUrls,mNames,view_item_width);
			gvGallery.setAdapter(adapterGallery);
			if (progress.isShowing())
				progress.dismiss();
		}
	}

	@SuppressWarnings("deprecation")
	private void setDimensions()
	{
		display=mActivity.getWindow().getWindowManager().getDefaultDisplay(); 
		width = display.getWidth();
		height = display.getHeight();
		int display_width = width- 24;
		if (display_width > 0) 
		{
			int item_width = display_width / 3;
			if (item_width > 0)
			{
				view_item_width = item_width;
			}
		} 
		else 
		{
			view_item_width = (int) mActivity.getResources().getDimension(
					R.dimen.photo_side);
		}
		gvGallery.setColumnWidth(view_item_width);
	}
}
*/