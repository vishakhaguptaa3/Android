package com.tnc.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;
import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.bean.ClipArtBean;
import com.tnc.homescreen.HomeScreenActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class ClipArtsAdapter extends BaseAdapter 
{
	private Context mContext;
	private int view_item_width;
	//int[] imageId;
	private ArrayList<ClipArtBean> listClipArtsString = 
			new ArrayList<ClipArtBean>();
	private LruCache<String, Bitmap> mLruCache;
	
	public ClipArtsAdapter(Context mContext,
			ArrayList<ClipArtBean> listClipArtsString,int view_item_width)
	{
		this.mContext=mContext;
		this.view_item_width=view_item_width;
		this.listClipArtsString = listClipArtsString;
		//this.imageId=imageId;
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/4th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 4;


		// LruCache takes key-value pair in constructor
		// key is the string to refer bitmap
		// value is the stored bitmap
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listClipArtsString.size();//imageId.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Bitmap thumbnailImage = null;		
		LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.clipartsadapter, null);
		// configure view holder
		ImageView imViewGallery=(ImageView) convertView.findViewById(R.id.imViewGallery);
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.height=view_item_width;
		params.width=view_item_width;
		imViewGallery.setLayoutParams(params);

		String imageName = listClipArtsString.get(position).getClipArtName();
		if(imageName.startsWith("http")){
			Picasso.with(mContext).load(imageName).into(imViewGallery);
		}else{

			/*String imageNameArray[] = imageName.split("_");
			String imageNameDisplay = imageNameArray[imageNameArray.length-1];

			String uri = imageNameDisplay.toLowerCase();  // where myresource (without the extension) is the file

			int imageResource = mContext.getResources().getIdentifier(uri.substring(0,uri.indexOf(".")), "drawable", mContext.getPackageName());

			Drawable res = mContext.getResources().getDrawable(imageResource);
			imViewGallery.setImageDrawable(res);*/

			String imageNameArray[] = imageName.split("_");
			String imageNameDisplay = imageNameArray[imageNameArray.length-1];

			String uri = imageNameDisplay.toLowerCase();  // where myresource (without the extension) is the file
			thumbnailImage = getBitmapFromMemCache(uri.substring(0,uri.indexOf(".")));

			if (thumbnailImage == null){
				// if asked thumbnail is not present it will be put into cache
				BitmapWorkerTask task = new BitmapWorkerTask(imViewGallery);
				task.execute(uri.substring(0,uri.indexOf(".")));
			}
			imViewGallery.setImageBitmap(thumbnailImage);
		}

		//imViewGallery.setImageResource(imageId[position]);
		imViewGallery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				MainBaseActivity.isImageSelected=true;
				if (MainBaseActivity._bitmap != null && !MainBaseActivity._bitmap.isRecycled()) {
					MainBaseActivity._bitmap.recycle();
					MainBaseActivity._bitmap = null; 
				}
				MainBaseActivity._bitmap=((BitmapDrawable)((ImageView)v).getDrawable()).getBitmap();
				if(mContext instanceof MainBaseActivity)
				{
					((MainBaseActivity)mContext).fragmentManager.popBackStack();
				}
				else if(mContext instanceof HomeScreenActivity)
				{
					((HomeScreenActivity)mContext).fragmentManager.popBackStack();
				}
			}
		});
		return convertView;
	}

	/**
	 * This function will return resource id for provided resource name
	 * @param variableName - name of drawable, e.g R.drawable.<b>image</b>
	 * @param с - class of resource, e.g R.drawable.class or R.raw.class
	 * @return integer id of resource
	 */
	public static int getResId(String variableName, Class<?> с) {

		Field field = null;
		int resId = 0;
		try {
			field = с.getField(variableName);
			try {
				resId = field.getInt(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resId;

	}

	/**
	 *  This function will return the scaled version of original image.
	 *  Loading original images into thumbnail is wastage of computation
	 *  and hence we will take put scaled version.
	 */
	private Bitmap getScaledImage (String imagePath){
		Bitmap bitmap = null;
		//Uri imageUri = Uri.parse (imagePath);
		int imageResource=getResId(imagePath,R.drawable.class);
		Drawable drawable = mContext.getResources().getDrawable(imageResource);
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();

			/**
			 * inSampleSize flag if set to a value > 1,
			 * requests the decoder to sub-sample the original image,
			 * returning a smaller image to save memory.
			 * This is a much faster operation as decoder just reads
			 * every n-th pixel from given image, and thus
			 * providing a smaller scaled image.
			 * 'n' is the value set in inSampleSize
			 * which would be a power of 2 which is downside
			 * of this technique.
			 */
			options.inSampleSize = 4;

			options.inScaled = true;

			BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
			bitmap = bitmapDrawable.getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 50, stream); //use the compression format of your need
			InputStream inputStream = new ByteArrayInputStream(stream.toByteArray());

			bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mLruCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mLruCache.get(key);
	}
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> imageViewReference;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			final Bitmap bitmap = getScaledImage(params[0]);
			addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
			return bitmap;
		}

		//  onPostExecute() sets the bitmap fetched by doInBackground();
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = (ImageView)imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
}
