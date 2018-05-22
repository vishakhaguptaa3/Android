package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class CheckTncUserResponseBeanData {

	@SerializedName("bbid")
	public String bbid;

	/**
	 * @return the bbid
	 */
	public String getBbid() {
		return bbid;
	}

	/**
	 * @param bbid the bbid to set
	 */
	public void setBbid(String bbid) {
		this.bbid = bbid;
	}
}
