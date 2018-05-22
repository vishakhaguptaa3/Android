package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class NotificationUpdateResponse 
{
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	@SerializedName("data")
	public NotificationUpdateResponseDatBean getData;

	public String getReponseCode() {
		return response_code;
	}

	public String getMessage() {
		return response_message;
	}
	
	public NotificationUpdateResponseDatBean getData()
	{
		return getData;
	}

}
