package com.tnc.common;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Utitlity class to check network connection availability 
 *  @author a3logics
 */


public class CustomFonts {

	/**
	 * 
	 * @param context
	 * @param editText
	 * @param font
	 * set font of edittext with specified attributes
	 */
	public static void setFontOfEditText(Context context, EditText editText,
			String font) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
		editText.setTypeface(typeface);
	}

	/**
	 * 
	 * @param context
	 * @param editText
	 * @param font
	 * set font of Button with specified attributes
	 */
	public static void setFontOfButton(Context context, Button button,
			String font) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
		button.setTypeface(typeface);
	}

	/**
	 * 
	 * @param context
	 * @param editText
	 * @param font
	 * set font of TextView with specified attributes
	 */
	public static void setFontOfTextView(Context context, TextView textView,
			String font) {
		try 
		{
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
			textView.setTypeface(typeface);	
		}
		catch (Exception e) 
		{
			e.getMessage();
		}
	}
}
