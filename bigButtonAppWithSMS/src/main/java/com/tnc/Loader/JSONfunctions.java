package com.tnc.Loader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url){
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;

		// Download JSON data from URL
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error in http connection "+e.toString());
		}

		// Convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),80);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error converting result "+e.toString());
		}

		try
		{
			jArray = new JSONObject(result);            
		}
		catch(JSONException e)
		{
			Log.e("log_tag", "Error parsing data "+e.toString());
		} 
		return jArray;
	}


	/*public static JSONObject getJSONfromURLS(String url){
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		HttpEntity entity2=null;

		// Download JSON data from URL
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			entity2 = response.getEntity();
			//			final JSONObject jObject = new JSONObject(EntityUtils.toString(entity));

		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error in http connection "+e.toString());
		}

		try
		{
			jArray = new JSONObject(EntityUtils.toString(entity2));            
		}
		catch(JSONException e)
		{
			Log.e("log_tag", "Error parsing data "+e.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return jArray;
	}*/


}


/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url){
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;

		// Download JSON data from URL
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) 
			{
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null)
				{
					sb.append(line); 
				}
				is.close();
				result=sb.toString();
			}
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error in http connection "+e.toString());
		}

		// Convert response to string
		try
		{
			//utf-8
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error converting result "+e.toString());
		}

		try
		{
			jArray = new JSONObject(result);            
		}
		catch(JSONException e)
		{
			Log.e("log_tag", "Error parsing data "+e.toString());
		} 
		return jArray;
	}
}*/

/*package itech.event.Loader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.util.Log;

public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url){
		//		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		String sourceString = "";

		// Download JSON data from URL
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			//	            is = entity.getContent();

			if (entity != null) {                 
				try 
				{
					sourceString= new String(EntityUtils.toString(entity));
					Log.i("GET RESPONSE", sourceString);
				}
				catch (ParseException exc)
				{
					exc.printStackTrace();
				} 
				catch (IllegalStateException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		catch(Exception exception)
		{
			Log.e("log_tag", "Error in http connection "+exception.toString());
		}

		// Convert response to string
		try{
			//	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			//			is.close();
			result=sb.toString();
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}

		try{

			jArray = new JSONObject(sourceString);            
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
		}

		return jArray;
	}
}*/
