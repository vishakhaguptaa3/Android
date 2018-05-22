package com.tnc.imageloader;

import java.io.File;
import java.io.IOException;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.tnc.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoadTask extends AsyncTask<String, Void, Void> {

	Context context;
	String Image_Url;
	ImageView image_view;
	int image_width = 320;
	int broken_image_resource = R.drawable.no_image;
	File fileImage = null;
	int view_item_width;

	public ImageLoadTask(Context context, String Image_Url,
			ImageView image_view, int view_item_width) {
		this.context = context;
		this.Image_Url = Image_Url;
		this.image_view = image_view;
		this.view_item_width = view_item_width;
	}

	public void setImagWidth(int image_width) {
		this.image_width = image_width;
	}

	public void setBrokeImageResource(int broken_image_resource) {
		this.broken_image_resource = broken_image_resource;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(String... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			AQuery aq = new AQuery(context);
			if (Image_Url.startsWith("http")) {
				aq.id(image_view).image(Image_Url, true, false, view_item_width,
						broken_image_resource);
			} else {
				fileImage = new File(Image_Url);
				aq.id(image_view).image(fileImage, false, view_item_width,
						new BitmapAjaxCallback() {

							@Override
							public void callback(String url, ImageView iv,
									Bitmap bm, AjaxStatus status) {
								try {
									iv.setImageBitmap(adjustImageOrientation(
											bm, url));
								} catch (Exception e) {
									e.getMessage();
								}
							}
						});
			}
			// .progress(progress_bar)
			// .image(fileImage.getAbsolutePath(), true, true,
			// image_width,broken_image_resource);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCancelled() {

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
}
