package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class PrivacyTermsReponseDataBean {

	@SerializedName("name")
	public String name;
	
	@SerializedName("version")
	public String version;
	
	@SerializedName("add_date")
	public String add_date;
	
	@SerializedName("update_date")
	public String update_date;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the add_date
	 */
	public String getAdd_date() {
		return add_date;
	}

	/**
	 * @param add_date the add_date to set
	 */
	public void setAdd_date(String add_date) {
		this.add_date = add_date;
	}

	/**
	 * @return the update_date
	 */
	public String getUpdate_date() {
		return update_date;
	}

	/**
	 * @param update_date the update_date to set
	 */
	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}
}
