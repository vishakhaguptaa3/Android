package com.tnc.checkinternet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utitlity class to check network connection availability 
 *  @author a3logics
 */

/**
 * use following permisstion in AndroidManifest file
 * 
 *		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */
public class NetworkConnection {

	/**
	 * this function is used to check if WIFI network is available or not
	 * @param context
	 * 			pass application context
	 * @return
	 * 		true - if network is available
	 * 		false - if network is not available
	 */

	public static  boolean isWifiAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected())
			return true;
		return false;
	}

	/**
	 * this function is used to check if mobile network is available or not
	 * @param context
	 * 			pass application context
	 * @return
	 * 		true - if network is available
	 * 		false - if network is not available
	 */

	private static boolean isMobileNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (networkInfo != null && networkInfo.isAvailable()
				&& networkInfo.isConnected())
			return true;

		return false;
	}

	/**
	 * this function is used to check if any network is available or not
	 * @param context
	 * 			pass application context
	 * @return
	 * 		true - if network is available
	 * 		false - if network is not available
	 */

	public static boolean isNetworkAvailable(Context context) {
		if(context!=null){
			if (isWifiAvailable(context))
				return true;
			if (isMobileNetworkAvailable(context))
				return true;
		}
		return false;
	}
}
