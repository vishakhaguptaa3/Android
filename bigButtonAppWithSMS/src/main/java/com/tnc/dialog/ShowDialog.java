package com.tnc.dialog;

import java.util.Locale;

import com.tnc.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ShowDialog {

	public static void alert(Context mAct, String title, String message) 
	{
		AlertDialog alertDialog = new AlertDialog.Builder(mAct).create();
		// alertDialog.setTitle(title);
		String outputMessage = message.substring(0, 1).toUpperCase(Locale.ENGLISH) + message.substring(1).toLowerCase(Locale.ENGLISH);
		alertDialog.setMessage(outputMessage);
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mAct
				.getResources().getString(R.string.txtOk),
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}
		});
		alertDialog.show();
	}

	public static void alert(Context mAct, String title, String message, final AlertCallBack alerAction)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(mAct).create();
		// alertDialog.setTitle(title);
		String outputMessage = message.substring(0, 1).toUpperCase(Locale.ENGLISH) + message.substring(1).toLowerCase(Locale.ENGLISH);
		alertDialog.setMessage(outputMessage);
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mAct
						.getResources().getString(R.string.txtOk),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if(alerAction!=null){
							alerAction.alertAction(true);
						}
					}
				});
		alertDialog.show();
	}

	public static void alert_YES_NO(Activity mAct,String title, String message,
			final AlertCallBack alerAction) {
		final AlertDialog alertDialog = new AlertDialog.Builder(mAct)
		.create();
		alertDialog.setTitle(title);
		String outputMessage = message.substring(0, 1).toUpperCase(
				Locale.ENGLISH)
				+ message.substring(1).toLowerCase(Locale.ENGLISH);
		alertDialog.setMessage(outputMessage);
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);

		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				mAct.getResources().getString(R.string.txtOk),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				alerAction.alertAction(true);
			}
		});

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
				mAct.getResources().getString(R.string.txtCancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}
}
