package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class NotificationImageStatusResponse
{

	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	@SerializedName("public_key")
	public String public_key;

	/*@SerializedName("total_count")
	public String total_count;

	@SerializedName("start_index")
	public String start_index;

	@SerializedName("page_size")
	public String page_size;
*/
	@SerializedName("data")
	public NotificationImageStatusResponseDataBean getData;

	public NotificationImageStatusResponseDataBean getData()
	{
		return getData;
	}
}
