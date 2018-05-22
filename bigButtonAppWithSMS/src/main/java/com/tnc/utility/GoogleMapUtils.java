package com.tnc.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

/**
 * Created by a3logics on 5/1/17.
 */

public class GoogleMapUtils {

    /**
     * Name: getCompleteAddressString
     *
     * Description: Method use for get address from latitude, longitude.
     *
     * @param :mContext
     * @param :latitude
     * @param :longitude
     * @return
     */

    public static String getCompleteAddressString(String lat, String lng) {

        // Log.e("print the location lat long ", lat + "////" + lng);

        String roadName = "";
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=__LAT__,__LNG__&sensor=false";

        url = url.replaceAll("__LAT__", lat);
        url = url.replaceAll("__LNG__", lng);

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet httpget = new HttpGet(url);

        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            return roadName = "";
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception squish) {
                return roadName = "";
            }
        }

        JSONObject jObject;
        JSONArray jArray = null;
        try {
            jObject = new JSONObject(result);
            jArray = jObject.getJSONArray("results");
        } catch (JSONException e1) {
            return roadName = "";
        }

        if (jArray != null && jArray.length() > 0) {
            try {
                JSONObject oneObject = jArray.getJSONObject(0);
                // Log.e("Print the address", oneObject.toString());
                // Pulling items from the array
                roadName = oneObject.getString("formatted_address");
            } catch (JSONException e) {
                return roadName = "";
            }
        }
        return roadName;
    }

    public static String getAddressString(Context mCtx, double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        try {
            strAdd = getCompleteAddressString("" + LATITUDE, "" + LONGITUDE);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(mCtx, Locale.getDefault());

                addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

                strAdd = addresses.get(0).getAddressLine(0);
                strAdd += ", " + addresses.get(0).getAddressLine(1);
                strAdd += ", " + addresses.get(0).getSubAdminArea();
                strAdd += ", " + addresses.get(0).getAdminArea();
                strAdd += ", " + addresses.get(0).getCountryName();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return strAdd;
    }
}
