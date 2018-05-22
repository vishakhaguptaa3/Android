package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class ParentConsentStatusResponseData {
	@SerializedName("email")
	public String email;

	@SerializedName("is_consent")
	public String is_consent;
	
	@SerializedName("created_on")
	public String created_on;
}
