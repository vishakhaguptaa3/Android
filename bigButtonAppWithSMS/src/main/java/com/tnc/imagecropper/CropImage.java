/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tnc.imagecropper;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;

import com.tnc.MainBaseActivity;
import com.tnc.R;
import com.tnc.common.GlobalCommonValues;
import com.tnc.utility.Logs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.ExifInterface;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity {

	final int IMAGE_MAX_SIZE = 1024;

	private static final String TAG                    = "CropImage";
	public static final  String IMAGE_PATH             = "image-path";
	public static final  String SCALE                  = "scale";
	public static final  String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
	public static final  String ASPECT_X               = "aspectX";
	public static final  String ASPECT_Y               = "aspectY";
	public static final  String OUTPUT_X               = "outputX";
	public static final  String OUTPUT_Y               = "outputY";
	public static final  String SCALE_UP_IF_NEEDED     = "scaleUpIfNeeded";
	public static final  String CIRCLE_CROP            = "circleCrop";
	public static final  String RETURN_DATA            = "return-data";
	public static final  String RETURN_DATA_AS_BITMAP  = "data";
	public static final  String ACTION_INLINE_DATA     = "inline-data";

	// These are various options can be specified in the intent.
	//	private       Bitmap.CompressFormat mOutputFormat    = Bitmap.CompressFormat.JPEG;
	private       Uri                   mSaveUri         = null;
	private       boolean               mDoFaceDetection = true;
	private       boolean               mCircleCrop      = false;
	private final Handler               mHandler         = new Handler();

	private int             mAspectX;
	private int             mAspectY;
	private int             mOutputX;
	private int             mOutputY;
	private boolean         mScale;
	private CropImageView   mImageView;
	private ContentResolver mContentResolver;
	private Bitmap          mBitmap;
	private Bitmap 			bitmapCopy;
	private String          mImagePath;
	private String 			mFilePath="";
	private File			fileSource;
	private File            fileTarget;
	private String 			strSourceFileName="";
	private String 			path="";
	boolean       mWaitingToPick; // Whether we are wait the user to pick a face.
	boolean       mSaving;  // Whether the "save" button is already clicked.
	HighlightView mCrop;

	// These options specifiy the output image size and whether we should
	// scale the output to fit it (or just crop it).
	private boolean mScaleUp = true;

	private final BitmapManager.ThreadSet mDecodingThreads =
			new BitmapManager.ThreadSet();

	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		mContentResolver = getContentResolver();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cropimage);
		mImageView = (CropImageView) findViewById(R.id.image);
		MainBaseActivity._bitmap=null;
		MainBaseActivity.isImageSelected=false;
		GlobalCommonValues._bitmap=null;
		GlobalCommonValues.isImageSelected=false;
		//		showStorageToast(this);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) 
		{
			if (extras.getString(CIRCLE_CROP) != null) 
			{
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) 
				{
					mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				}
				mCircleCrop = true;
				mAspectX = 1;
				mAspectY = 1;
			}
			mFilePath=extras.getString(IMAGE_PATH);
			strSourceFileName=mFilePath.substring(mFilePath.lastIndexOf("/")+1,mFilePath.length());
			fileSource=new File(mFilePath.substring(0,mFilePath.lastIndexOf("/")),strSourceFileName);
			if(mFilePath.contains("TNC_IMAGES") || mFilePath.contains("TNC_IMAGES_RESPONSE"))
			{
				mImagePath=mFilePath;
				//				MediaStore.Images.Media.insertImage(
				//						getContentResolver(),
				//						BitmapFactory.decodeFile(mFilePath), "Big_Buton",
				//						"Observation Image");
				//				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				//				File f = new File(mFilePath);
				//				Uri contentUri = Uri.fromFile(f);
				//				mediaScanIntent.setData(contentUri);
				//				CropImage.this.sendBroadcast(mediaScanIntent);
				new Thread(new Runnable()
				{
					@Override
					public void run() {
						//						MediaStore.Images.Media.insertImage(
						//								getContentResolver(),
						//								BitmapFactory.decodeFile(mFilePath), "Big_Buton",
						//								"Observation Image");

						/**
						 * Save Clicked image in gallery
						 */
						File fileGalleryCopy=new File(Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+
								File.separator+"tnc_secondary_path",strSourceFileName);
						try {
							FileUtils.copyFile(fileSource, fileGalleryCopy);
						} catch (Exception e) {
							Logs.writeLog("CropImage","OnCreate",e.getMessage());
						}

						/**
						 * refresh Gallery
						 */
						Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						File f = new File(fileGalleryCopy.getAbsolutePath());
						Uri contentUri = Uri.fromFile(f);
						mediaScanIntent.setData(contentUri);
						CropImage.this.sendBroadcast(mediaScanIntent);
					}
				}).start();
			}
			else
			{
				path = android.os.Environment.getExternalStorageDirectory()
						+ File.separator
						+ "TNCImages";
				File fileBackupDir = new File(path);
				//			if(fileBackupDir.exists())
				//				fileBackupDir.delete();

				if (!fileBackupDir.exists()) {
					fileBackupDir.mkdirs();
				}
				if (fileSource.exists()) {
					fileTarget = new File(fileBackupDir, "test.jpg");
					try {
						fileTarget.createNewFile();
						FileUtils.copyFile(fileSource, fileTarget);
					}
					catch (Exception exception) 
					{
						exception.getMessage();
					}
				}
				try{
					if(fileTarget!=null){
						mImagePath=fileTarget.getAbsolutePath();	
					}
				}catch(Exception e){
					e.getMessage();
				}

			}

			try {
				mSaveUri = getImageUri(mImagePath);
				mBitmap = adjustImageOrientation(getBitmap(mImagePath),mImagePath);//getBitmap(mImagePath);
			} catch (Exception e) {
				Logs.writeLog("CropImage","adjustImageOrientation", e.getMessage());
			}
			if (extras.containsKey(ASPECT_X) && extras.get(ASPECT_X) instanceof Integer) {

				mAspectX = extras.getInt(ASPECT_X);
			} else {

				throw new IllegalArgumentException("aspect_x must be integer");
			}
			if (extras.containsKey(ASPECT_Y) && extras.get(ASPECT_Y) instanceof Integer) {

				mAspectY = extras.getInt(ASPECT_Y);
			} else {

				throw new IllegalArgumentException("aspect_y must be integer");
			}
			mOutputX = extras.getInt(OUTPUT_X);
			mOutputY = extras.getInt(OUTPUT_Y);
			mScale = extras.getBoolean(SCALE, true);
			mScaleUp = extras.getBoolean(SCALE_UP_IF_NEEDED, true);
		}


		if (mBitmap == null) {

			Log.d(TAG, "finish!!!");
			finish();
			return;
		}

		// Make UI fullscreen.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		findViewById(R.id.discard).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {

						//						setResult(RESULT_CANCELED);
						MainBaseActivity.isImageSelected=false;
						MainBaseActivity._bitmap=null;
						GlobalCommonValues.isImageSelected=false;
						GlobalCommonValues._bitmap=null;
						if(mFilePath.contains("TNC_IMAGES") || mFilePath.contains("TNC_IMAGES_RESPONSE"))
						{
							//							fileSource.delete();
							MainBaseActivity.isCameraCanceled=true;
						}
						finish();
					}
				});

		findViewById(R.id.save).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						try 
						{
							onSaveClicked();
						}
						catch (Exception e) 
						{
							finish();
						}
					}
				});
		findViewById(R.id.rotateLeft).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {

						mBitmap = Util.rotateImage(mBitmap, -90);
						RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
						mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
						mRunFaceDetection.run();
					}
				});

		findViewById(R.id.rotateRight).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {

						mBitmap = Util.rotateImage(mBitmap, 90);
						RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
						mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
						mRunFaceDetection.run();
					}
				});
		startFaceDetection();
	}

	private Uri getImageUri(String path) {

		return Uri.fromFile(new File(path));
	}

	private Bitmap adjustImageOrientation(Bitmap image, String picturePath) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(picturePath);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int rotate = 0;
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			if (rotate != 0) {
				int w = image.getWidth();
				int h = image.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap & convert to ARGB_8888, required by tess
				image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

			}
		} catch (IOException e) {
			return null;
		}
		return image.copy(Bitmap.Config.ARGB_8888, true);
	}

	private Bitmap getBitmap(String path) {

		Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			in = mContentResolver.openInputStream(uri);

			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			in = mContentResolver.openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(in, null, o2);
			in.close();

			return b;
		} catch (FileNotFoundException e) {
			Log.e(TAG, "file " + path + " not found");
		} catch (IOException e) {
			Log.e(TAG, "file " + path + " not found");
		}
		return null;
	}


	private void startFaceDetection() {

		if (isFinishing()) {
			return;
		}

		mImageView.setImageBitmapResetBase(mBitmap, true);

		Util.startBackgroundJob(this, null,
				"Please wait\u2026",
				new Runnable() {
			public void run() {

				final CountDownLatch latch = new CountDownLatch(1);
				final Bitmap b = mBitmap;
				mHandler.post(new Runnable() {
					public void run() {

						if (b != mBitmap && b != null) {
							mImageView.setImageBitmapResetBase(b, true);
							mBitmap.recycle();
							mBitmap = b;
						}
						if (mImageView.getScale() == 1F) {
							mImageView.center(true, true);
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				mRunFaceDetection.run();
			}
		}, mHandler);
	}


	private void onSaveClicked() throws Exception {
		// TODO this code needs to change to use the decode/crop/encode single
		// step api so that we don't require that the whole (possibly large)
		// bitmap doesn't have to be read into memory
		if (mSaving) return;

		if (mCrop == null) {

			return;
		}

		mSaving = true;

		Rect r = mCrop.getCropRect();

		int width = r.width();
		int height = r.height();

		// If we are circle cropping, we want alpha channel, which is the
		// third param here.
		Bitmap croppedImage;
		try {

			croppedImage = Bitmap.createBitmap(width, height,
					mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		} catch (Exception e) {
			throw e;
		}
		if (croppedImage == null) {

			return;
		}

		{
			bitmapCopy=mBitmap;
			Canvas canvas = new Canvas(croppedImage);
			Rect dstRect = new Rect(0, 0, width, height);
			canvas.drawBitmap(bitmapCopy, r, dstRect, null); //mBitmap
		}

		if (mCircleCrop) {

			// OK, so what's all this about?
			// Bitmaps are inherently rectangular but we want to return
			// something that's basically a circle.  So we fill in the
			// area around the circle with alpha.  Note the all important
			// PortDuff.Mode.CLEAR.
			Canvas c = new Canvas(croppedImage);
			Path p = new Path();
			p.addCircle(width / 2F, height / 2F, width / 2F,
					Path.Direction.CW);
			c.clipPath(p, Region.Op.DIFFERENCE);
			c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
		}

		/* If the output is required to a specific size then scale or fill */
		if (mOutputX != 0 && mOutputY != 0) {

			if (mScale) {

				/* Scale the image to the required dimensions */
				Bitmap old = croppedImage;
				croppedImage = Util.transform(new Matrix(),
						croppedImage, mOutputX, mOutputY, mScaleUp);
				if (old != croppedImage) {

					old.recycle();
				}
			} else {

				/* Don't scale the image crop it to the size requested.
				 * Create an new image with the cropped image in the center and
				 * the extra space filled.
				 */

				// Don't scale the image but instead fill it so it's the
				// required dimension
				Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY,
						Bitmap.Config.RGB_565);
				Canvas canvas = new Canvas(b);

				Rect srcRect = mCrop.getCropRect();
				Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

				int dx = (srcRect.width() - dstRect.width()) / 2;
				int dy = (srcRect.height() - dstRect.height()) / 2;

				/* If the srcRect is too big, use the center part of it. */
				srcRect.inset(Math.max(0, dx), Math.max(0, dy));

				/* If the dstRect is too big, use the center part of it. */
				dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

				/* Draw the cropped bitmap in the center */
				canvas.drawBitmap(bitmapCopy, srcRect, dstRect, null);  //mBitmap

				/* Set the cropped bitmap as the new bitmap */
				croppedImage.recycle();
				croppedImage = b;
			}
		}

		// Return the cropped image directly or save it to the specified URI.
		Bundle myExtras = getIntent().getExtras();
		if (myExtras != null && (myExtras.getParcelable("data") != null
				|| myExtras.getBoolean(RETURN_DATA))) 
		{
			Bundle extras = new Bundle();
			extras.putParcelable(RETURN_DATA_AS_BITMAP, croppedImage);
			setResult(RESULT_OK,
					(new Intent()).setAction(ACTION_INLINE_DATA).putExtras(extras));
			finish();
		} 
		else 
		{
			final Bitmap b = croppedImage;
			MainBaseActivity.selectedImagepath=mImagePath;
			MainBaseActivity._bitmap=b;
			MainBaseActivity.isImageSelected=true;
			GlobalCommonValues._bitmap=b;
			GlobalCommonValues.isImageSelected=true;
			GlobalCommonValues.selectedImagepath=mImagePath;
			Util.startBackgroundJob(this, null, getString(R.string.saving_image),
					new Runnable() {
				public void run() {

					saveOutput(b);
				}
			}, mHandler);
		}
	}

	private void saveOutput(Bitmap croppedImage) {

		if (mSaveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = mContentResolver.openOutputStream(mSaveUri);
				//				if (outputStream != null) {
				//					croppedImage.compress(mOutputFormat, 90, outputStream);
				//				}
				/*MainBaseActivity.selectedImagepath=mImagePath;
				MainBaseActivity._bitmap=croppedImage;
				MainBaseActivity.isImageSelected=true;*/
			} catch (IOException ex) {

				Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
				setResult(RESULT_CANCELED);
				//				if(mFilePath.contains("BIG_BUTTON_IMAGES"))
				//					fileSource.delete();
				finish();
				return;
			} finally {

				Util.closeSilently(outputStream);
			}
			Bundle extras = new Bundle();
			Intent intent = new Intent(mSaveUri.toString());
			intent.putExtras(extras);
			intent.putExtra(IMAGE_PATH, mImagePath);
			intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this));
			setResult(RESULT_OK, intent);
			//			GlobalCommonValues.imageResponsePathSaved=mImagePath;
		} else {

			Log.e(TAG, "not defined image url");
		}
		if(croppedImage!=null && croppedImage.isRecycled())
		{
			//			croppedImage.recycle();
			croppedImage=null;
		}
		//		if(mFilePath.contains("BIG_BUTTON_IMAGES"))
		//			fileSource.delete();
		finish();
	}

	@Override
	protected void onPause() {

		super.onPause();
		BitmapManager.instance().cancelThreadDecoding(mDecodingThreads);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null && !mBitmap.isRecycled()) 
		{
			//			mBitmap.recycle();
			mBitmap=null;
		}
		if (bitmapCopy != null && !bitmapCopy.isRecycled()) 
		{
			//			bitmapCopy.recycle();
			bitmapCopy=null;
		}
	}


	Runnable mRunFaceDetection = new Runnable() {
		float mScale = 1F;
		Matrix mImageMatrix;
		FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
		int mNumFaces;

		// For each face, we create a HightlightView for it.
		private void handleFace(FaceDetector.Face f) {

			PointF midPoint = new PointF();

			int r = ((int) (f.eyesDistance() * mScale)) * 2;
			f.getMidPoint(midPoint);
			midPoint.x *= mScale;
			midPoint.y *= mScale;

			int midX = (int) midPoint.x;
			int midY = (int) midPoint.y;

			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			RectF faceRect = new RectF(midX, midY, midX, midY);
			faceRect.inset(-r, -r);
			if (faceRect.left < 0) {
				faceRect.inset(-faceRect.left, -faceRect.left);
			}

			if (faceRect.top < 0) {
				faceRect.inset(-faceRect.top, -faceRect.top);
			}

			if (faceRect.right > imageRect.right) {
				faceRect.inset(faceRect.right - imageRect.right,
						faceRect.right - imageRect.right);
			}

			if (faceRect.bottom > imageRect.bottom) {
				faceRect.inset(faceRect.bottom - imageRect.bottom,
						faceRect.bottom - imageRect.bottom);
			}

			hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop,
					mAspectX != 0 && mAspectY != 0);

			mImageView.add(hv);
		}

		// Create a default HightlightView if we found no face in the picture.
		private void makeDefault() {

			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			int cropHeight = cropWidth;

			if (mAspectX != 0 && mAspectY != 0) {

				if (mAspectX > mAspectY) {

					cropHeight = cropWidth * mAspectY / mAspectX;
				} else {

					cropWidth = cropHeight * mAspectX / mAspectY;
				}
			}

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
					mAspectX != 0 && mAspectY != 0);

			mImageView.mHighlightViews.clear(); // Thong added for rotate

			mImageView.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() {

			if (mBitmap == null) {

				return null;
			}

			// 256 pixels wide is enough.
			if (mBitmap.getWidth() > 256) {

				mScale = 256.0F / mBitmap.getWidth();
			}
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		}

		public void run() {

			mImageMatrix = mImageView.getImageMatrix();
			Bitmap faceBitmap = prepareBitmap();

			mScale = 1.0F / mScale;
			if (faceBitmap != null && mDoFaceDetection) {
				FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
						faceBitmap.getHeight(), mFaces.length);
				mNumFaces = detector.findFaces(faceBitmap, mFaces);
			}

			if (faceBitmap != null && faceBitmap != mBitmap) {
				faceBitmap.recycle();
			}

			mHandler.post(new Runnable() {
				public void run() {

					mWaitingToPick = mNumFaces > 1;
					if (mNumFaces > 0) {
						for (int i = 0; i < mNumFaces; i++) {
							handleFace(mFaces[i]);
						}
					} else {
						makeDefault();
					}
					mImageView.invalidate();
					if (mImageView.mHighlightViews.size() == 1) {
						mCrop = mImageView.mHighlightViews.get(0);
						mCrop.setFocus(true);
					}

					if (mNumFaces > 1) {
						/*Toast.makeText(CropImage.this,
								"Multi face crop help",
								Toast.LENGTH_SHORT).show();*/
					}
				}
			});
		}
	};

	public static final int NO_STORAGE_ERROR  = -1;
	public static final int CANNOT_STAT_ERROR = -2;

	public static void showStorageToast(Activity activity) {

		showStorageToast(activity, calculatePicturesRemaining(activity));
	}

	@SuppressLint("ShowToast")
	public static void showStorageToast(Activity activity, int remaining) {

		String noStorageText = null;

		if (remaining == NO_STORAGE_ERROR) {

			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_CHECKING)) {

				noStorageText = activity.getString(R.string.preparing_card);
			} else {

				noStorageText = activity.getString(R.string.no_storage_card);
			}
		} else if (remaining < 1) {

			noStorageText = activity.getString(R.string.not_enough_space);
		}

		if (noStorageText != null) {

			//			Toast.makeText(activity, noStorageText, 5000).show();
		}
	}

	public static int calculatePicturesRemaining(Activity activity) {

		try {
			/*if (!ImageManager.hasStorage()) {
                return NO_STORAGE_ERROR;
            } else {*/
			String storageDirectory = "";
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				storageDirectory = Environment.getExternalStorageDirectory().toString();
			}
			else {
				storageDirectory = activity.getFilesDir().toString();
			}
			StatFs stat = new StatFs(storageDirectory);
			@SuppressWarnings("deprecation")
			float remaining = ((float) stat.getAvailableBlocks()
					* (float) stat.getBlockSize()) / 400000F;
			return (int) remaining;
			//}
		} catch (Exception ex) {
			// if we can't stat the filesystem then we don't know how many
			// pictures are remaining.  it might be zero but just leave it
			// blank since we really don't know.
			return CANNOT_STAT_ERROR;
		}
	}


}


