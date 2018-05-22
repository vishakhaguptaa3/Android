package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class NotifyFriendResponse 
{
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;
	
	@SerializedName("public_key")
	public String public_key;

	@SerializedName("data")
	public String data;
	//public RegistrationReponseDataBean getData;

	public String getReponseCode() {
		return response_code;
	}

	public String getMessage() {
		return response_message;
	}

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
	 * @return the data
	 */
	public String getData() {
		return data;
	}
}
