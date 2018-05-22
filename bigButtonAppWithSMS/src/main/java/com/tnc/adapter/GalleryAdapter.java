package com.tnc.adapter;

import java.util.ArrayList;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.imagecropper.CropImage;
import com.tnc.imageloader.ImageLoadTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter 
{
	private Context mContext;
	private Cursor imageCursor;
	private Uri[] mUrls;
	private String[] strUrls;
	private String[] mNames;
	private int view_item_width=0;
	private ImageLoadTask imageLoader=null;
	public ArrayList<ImageItem> images = new ArrayList<ImageItem>();

	public GalleryAdapter(Context mContext,ArrayList<ImageItem> images,Cursor imageCursor,Uri[] mUrls,String[] strUrls,
			String[] mNames,int view_item_width) 
	{
		this.mContext=mContext;
		this.images.clear();
		this.images=images;
		this.imageCursor=imageCursor;
		this.mUrls=mUrls;
		this.strUrls=strUrls;
		this.mNames=mNames;
		this.view_item_width=view_item_width;
	}

	@Override
	public int getCount() 
	{
		return images.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	static class ViewHolder
	{
		ImageView imViewGallery;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder=null;
		//reuse views
		if ( convertView== null) 
		{
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.galleryadapter, null);
			// configure view holder
			holder = new ViewHolder();
			holder.imViewGallery=(ImageView) convertView.findViewById(R.id.imViewGallery);
			convertView.setTag(holder);
		}
		else
		{
			// fill data
			holder = (ViewHolder) convertView.getTag();
		}
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.height=view_item_width;
		params.width=view_item_width;
		holder.imViewGallery.setLayoutParams(params);
		//		ImageItem item = images.get(position);
		holder.imViewGallery.setId(position);
		try {
			imageLoader=new ImageLoadTask(mContext,mUrls[position].getPath(),
					holder.imViewGallery,view_item_width); 
			imageLoader.execute();
		} catch (Exception e) {
			e.getMessage();
		}

		holder.imViewGallery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds=true;
				options.inSampleSize = 8;
				MainBaseActivity._bitmap=null;
				Log.e("intent : ", ""+position);
				Log.e("imgUrls", mUrls[position].getPath()+"");
				Log.e("position", position+"");
				MainBaseActivity.selectedImagepath= mUrls[position].getPath();
				//				GlobalCommonValues.selectedImagepath= mUrls[position].getPath();
				MainBaseActivity.isImageSelected=true;
				//				GlobalCommonValues.isImageSelected=true;
				try {
					MainBaseActivity._bitmap=BitmapFactory.decodeFile(mUrls[position].getPath(),options);
					//					GlobalCommonValues._bitmap=BitmapFactory.decodeFile(mUrls[position].getPath(),options);
				} catch (Exception e) {
					e.getMessage();
				}
				Intent cropIntent=new Intent(mContext,CropImage.class);
				cropIntent.putExtra(CropImage.IMAGE_PATH,MainBaseActivity.selectedImagepath);
				cropIntent.putExtra(CropImage.SCALE, true);
				cropIntent.putExtra(CropImage.ASPECT_X, 1);
				cropIntent.putExtra(CropImage.ASPECT_Y, 1);
				(((Activity)mContext)).startActivity(cropIntent);
				//				((MainBaseActivity)mContext).fragmentManager.popBackStack();
				//				((MainBaseActivity)mContext).setFragment(new TilePreviewFragment());;
			}
		});
		return convertView;
	}
}
