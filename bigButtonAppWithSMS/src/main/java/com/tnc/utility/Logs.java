package com.tnc.utility;

import android.util.Log;

public class Logs
{
	public static boolean isShow=true;

	/**
	 * 
	 * @param classname,function name,error message
	 * @return
	 */
	public static void writeLog(String className,String functioName,String error)
	{
		try {
			Log.i(className+"-->"+functioName, error);
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
