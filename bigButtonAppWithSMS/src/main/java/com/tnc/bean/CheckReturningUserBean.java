package com.tnc.bean;

public class CheckReturningUserBean {
	
	String backup_key="";
	String country_code="";
	String phone="";

	public CheckReturningUserBean(String backup_key,String country_code, String phone) {
		super();
		this.backup_key = backup_key;
		this.country_code = country_code;
		this.phone = phone;
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

	/**
	 * @return the country_code
	 */
	public String getCountry_code() {
		return country_code;
	}

	/**
	 * @param country_code the country_code to set
	 */
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return phone;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String phone) {
		this.phone = phone;
	}

}
