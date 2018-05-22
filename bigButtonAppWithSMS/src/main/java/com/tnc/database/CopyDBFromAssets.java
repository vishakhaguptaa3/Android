package com.tnc.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Utitlity class to copy the database from assets to project specific folder 
 *  @author a3logics
 */

public class CopyDBFromAssets 
{
	public static final String DATABASE_NAME = "tnc.sqlite";

	public static String DB_PATH;

	private SharedPreferences app_preferences;

	private Context mContext;

	@SuppressLint("SdCardPath")
	public CopyDBFromAssets(Context context) 
	{
		this.mContext = context;
		DB_PATH ="/data/data/" + this.mContext.getPackageName()
		+ "/databases/";/*android.os.Environment.getExternalStorageDirectory()
				+ File.separator
				+ "BigButtonDatabase/"*/ 

		app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this.mContext);
	}

	public void copyDatabase() 
	{
		boolean silent = app_preferences.getBoolean("install", false);
		if (silent)
		{
		}
		else 
		{
			boolean dbexist = checkdatabase();
			if (dbexist) {
				// //system.out.println("Database exists");
			}
			else {
				try{
					CopyAssets();
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	// Check for data base in memory
	private boolean checkdatabase() 
	{
		// SQLiteDatabase checkdb = null;
		boolean checkdb = false;
		try
		{
			File dbfile = new File(DB_PATH + CopyDBFromAssets.DATABASE_NAME);
			checkdb = dbfile.exists();
		}
		catch (SQLiteException e) 
		{
			// //system.out.println("Database doesn't exist...");
		}
		return checkdb;
	}

	// Copy Database from assets folder to phone memory
	private void CopyAssets() 
	{
		boolean isSuccess = false;
		AssetManager assetManager = this.mContext.getAssets();
		String[] files = null;
		try 
		{
			files = assetManager.list("");
		}
		catch (IOException e){
			Log.e("tag", e.getMessage());
		}
		for (String filename : files) 
		{
			// //system.out.println("File:" + filename);
			if (filename.equals(CopyDBFromAssets.DATABASE_NAME)) 
			{
				InputStream in = null;
				OutputStream out = null;
				try 
				{
					in = assetManager.open(filename);
					File dir = new File(DB_PATH);
					if (!dir.exists())
						dir.mkdir();
					out = new FileOutputStream(DB_PATH + filename);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
					isSuccess = true;
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					isSuccess = false;
				}
			}
		}
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putBoolean("install", isSuccess);
		editor.commit();
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException 
	{
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) 
		{
			out.write(buffer, 0, read);
		}
	}
}
