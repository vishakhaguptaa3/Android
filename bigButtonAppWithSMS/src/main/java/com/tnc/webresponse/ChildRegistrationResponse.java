package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class ChildRegistrationResponse {

	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	@SerializedName("public_key")
	public String public_key;

	@SerializedName("data")
	public String data;

}
