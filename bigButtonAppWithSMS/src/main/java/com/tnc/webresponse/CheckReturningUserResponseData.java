package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class CheckReturningUserResponseData {
	
	@SerializedName("is_premium_user")
	public String is_premium_user;

	public String getIs_premium_user() {
		return is_premium_user;
	}

	public void setIs_premium_user(String is_premium_user) {
		this.is_premium_user = is_premium_user;
	}
}
