package com.tnc.httpconnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore;

import org.apache.http.entity.StringEntity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import android.content.Context;

public class MyHttpConnection {
	static int timeoutTime = 600000;
	private static AsyncHttpClient client = new AsyncHttpClient();
	//Private-Key
	public static void get(String url, RequestParams params,
						   AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeoutTime);
		setSSL(client);
		client.get((url), params, responseHandler);
	}

	public static void postWithJsonEntityHeader(Context context, String url,
												cz.msebera.android.httpclient.entity.StringEntity stringEntity,
												AsyncHttpResponseHandler responseHandler, String private_key,
												String public_key) {
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, (url), stringEntity, "application/json",
				responseHandler);
	}

	public static void postHeaderWithoutJsonEntity(Context context, String url,
												   AsyncHttpResponseHandler responseHandler, String private_key,
												   String public_key) {
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, url, null, responseHandler);
	}

	public static void getWithoutHeaderWithoutJsonEntity(Context context, String url,
														 AsyncHttpResponseHandler responseHandler, String private_key,
														 String public_key) {
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.get(context, url, null, responseHandler);
	}

	public static void postWithoutHeaderWithoutJsonEntity(Context context, String url,
														  AsyncHttpResponseHandler responseHandler, String private_key,
														  String public_key) {
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, url, null, responseHandler);
	}

	public static void postFileWithJsonEntityHeaderSetDeafultImage(
			Context context, String url,
			AsyncHttpResponseHandler responseHandler, String private_key,
			String public_key) {
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, (url), null, "application/json", responseHandler);
	}

	public static void postFileWithJsonEntityHeader(Context context,
													String url, File file, cz.msebera.android.httpclient.entity.StringEntity stringEntity,
													AsyncHttpResponseHandler responseHandler, String private_key,
													String public_key) {
		RequestParams requestParams = new RequestParams();
		try {
			requestParams.put("file", file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, url, requestParams, responseHandler);
	}

	public static void postFileWithJsonEntityHeaderRequestImage(
			Context context, String url, File file, String link,
			String notifications_id, AsyncHttpResponseHandler responseHandler,
			String private_key, String public_key) {
		RequestParams requestParams = new RequestParams();
		requestParams.put("notifications_id", notifications_id);
		try {
			if (file != null) {
				requestParams.put("image", file); // In case Gallery or Camera
				// Image
			} else if (file == null) {
				if (!link.equals("")) {
					requestParams.put("link", link); // In case of Clip Art
					// Images
				} else {
					requestParams.put("image", ""); // In case of Default Image
					// to b send in response
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, url, requestParams, responseHandler); // Original
	}

	public static void postFileWithJsonEntityHeaderShareContact(
			Context context, String url, File file,
			String id, AsyncHttpResponseHandler responseHandler,
			String private_key, String public_key) {
		RequestParams requestParams = new RequestParams();
		requestParams.put("to_id", id);
		try {
			if (file != null) {
				requestParams.put("file", file);
				// Image
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", private_key);
		client.addHeader("Public-Key", public_key);
		setSSL(client);
		client.post(context, url, requestParams, responseHandler); // Original
	}

	public static void post(String url, RequestParams params,
							AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeoutTime);
		setSSL(client);
		client.post((url), params, responseHandler);
	}

	public static void postWithJsonEntity(Context context, String url,
										  cz.msebera.android.httpclient.entity.StringEntity stringEntitiy, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeoutTime);
		setSSL(client);
		client.post(context, (url), stringEntitiy, "application/json",
				responseHandler);
	}

	// using GET method for web request
	public static void getWithoutPara(Context context, String url,
									  String header, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(timeoutTime);
		client.addHeader("Private-Key", header);
		setSSL(client);
		client.get(context, url, responseHandler);
	}

	public static void setSSL(AsyncHttpClient client){
		try{

			/// We initialize a default Keystore
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			// We load the KeyStore
			trustStore.load(null, null);
			// We initialize a new SSLSocketFacrory
			MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
			// We set that all host names are allowed in the socket factory
			socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			client.setSSLSocketFactory(socketFactory);
		}catch(Exception e){
			e.getMessage();
		}
	}
}