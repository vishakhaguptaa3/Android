package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class VersionBean {

	@SerializedName("faq_version")
	public String faq_version;

	@SerializedName("clipart_version")
	public String clipart_version;
	
	@SerializedName("default_message_version")
	public String default_message_version;

	@SerializedName("emergency_number_version")
	public String emergency_number_version;

	/**
	 * @return the faq_version
	 */
	public String getFaq_version() {
		return faq_version;
	}

	/**
	 * @param faq_version the faq_version to set
	 */
	public void setFaq_version(String faq_version) {
		this.faq_version = faq_version;
	}

	/**
	 * @return the default_message_version
	 */
	public String getDefault_message_version() {
		return default_message_version;
	}

	/**
	 * @param default_message_version the default_message_version to set
	 */
	public void setDefault_message_version(String default_message_version) {
		this.default_message_version = default_message_version;
	}

	/**
	 * @return the emergency_number_version
	 */
	public String getEmergency_number_version() {
		return emergency_number_version;
	}

	/**
	 * @param emergency_number_version the emergency_number_version to set
	 */
	public void setEmergency_number_version(String emergency_number_version) {
		this.emergency_number_version = emergency_number_version;
	}

	/**
	 * @return the clipart_version
	 */
	public String getClipart_version() {
		return clipart_version;
	}

	/**
	 * @param clipart_version the clipart_version to set
	 */
	public void setClipart_version(String clipart_version) {
		this.clipart_version = clipart_version;
	}

}
