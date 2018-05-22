package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class DefaultMessagesResponseData {

	@SerializedName("type")
	public String type;

	@SerializedName("message")
	public String message;

	@SerializedName("version")
	public String version;

	@SerializedName("add_date")
	public String add_date;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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
}
