package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class ChangeNumberResponse 
{
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;
	
	@SerializedName("public_key")
	public String public_key;

	@SerializedName("data")
	public ChangeNumberResponseDataBean getData;

	/**
	 * @return the response_code
	 */
	public String getResponse_code() {
		return response_code;
	}

	/**
	 * @return the response_message
	 */
	public String getResponse_message() {
		return response_message;
	}

	/**
	 * @return the public_key
	 */
	public String getPublic_key() {
		return public_key;
	}

	/**
	 * @return the getData
	 */
	public ChangeNumberResponseDataBean getGetData() {
		return getData;
	}
	
}
