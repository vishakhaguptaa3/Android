package com.tnc.webresponse;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class PrivacyTermsReponseBean {

	@SerializedName("response_code")
	public String response_code; 
	
	@SerializedName("response_message")
	public String response_message; 
	
	@SerializedName("public_key")
	public String public_key;

	@SerializedName("data")
	public ArrayList<PrivacyTermsReponseDataBean> getData;
	
}
