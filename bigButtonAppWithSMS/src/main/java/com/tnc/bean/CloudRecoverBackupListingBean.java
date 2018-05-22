package com.tnc.bean;

import com.google.gson.annotations.SerializedName;

public class CloudRecoverBackupListingBean 
{
	@SerializedName("datetime")
	String datetime="";
	@SerializedName("url")
	public String url;
	@SerializedName("public_key")
	public String public_key;
	@SerializedName("backup_key")
	public String backup_key;
	public CloudRecoverBackupListingBean()
	{
	}

	/**
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the public_key
	 */
	public String getPublic_key() {
		return public_key;
	}

	/**
	 * @param public_key the public_key to set
	 */
	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	/**
	 * @return the backup_key
	 */
	public String getBackup_key() {
		return backup_key;
	}

	/**
	 * @param backup_key the backup_key to set
	 */
	public void setBackup_key(String backup_key) {
		this.backup_key = backup_key;
	}

	}
