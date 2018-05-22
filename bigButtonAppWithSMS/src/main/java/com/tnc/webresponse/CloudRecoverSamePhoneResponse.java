package com.tnc.webresponse;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.tnc.bean.CloudRecoverBackupListingBean;

public class CloudRecoverSamePhoneResponse 
{
	@SerializedName("response_code")
	public String response_code;

	@SerializedName("response_message")
	public String response_message;

	@SerializedName("data")
	public ArrayList<CloudRecoverBackupListingBean> listCloudRecoverResponse;

	public String getResponse_code() 
	{
		return response_code;
	}

	public void setResponse_code(String response_code) 
	{
		this.response_code = response_code;
	}

	public String getMessage() 
	{
		return response_message;
	}

	public void setMessage(String response_message) 
	{
		this.response_message = response_message;
	}
	public ArrayList<CloudRecoverBackupListingBean> getData() 
	{
		return listCloudRecoverResponse;
	}
}
