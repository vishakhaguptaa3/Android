package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	public String getReponseCode() {
		return response_code;
	}

	public String getMessage() {
		return response_message;
	}

}
