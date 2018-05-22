package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponseDataBeanNewPhase {
	
	@SerializedName("public_key")
	String public_key;

	@SerializedName("passcode")
	String passcode;
	
	@SerializedName("image")
	String image;

	@SerializedName("name")
	String name;

	@SerializedName("user_type")
	String user_type;

	@SerializedName("is_default_image")
	String is_default_image;

	@SerializedName("match_type")
	String match_type;

	@SerializedName("group_code")
	String group_code;

	@SerializedName("is_activate")//
	String is_activate;

	@SerializedName("is_verified")
	String is_verified;

	@SerializedName("is_guestuser")
	String is_guestuser;

	@SerializedName("is_premium_user")
	String is_premium_user;

	@SerializedName("backup_key")
	String backup_key;

	@SerializedName("country_code")
	String country_code;

	public String getMatch_type() {
		return match_type;
	}

	public void setMatch_type(String match_type) {
		this.match_type = match_type;
	}

	public String getGroup_code() {
		return group_code;
	}

	public void setGroup_code(String group_code) {
		this.group_code = group_code;
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
	 * @return the passcode
	 */
	public String getPasscode() {
		return passcode;
	}
	/**
	 * @param passcode the passcode to set
	 */
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the is_default_image
	 */
	public String getIs_default_image() {
		return is_default_image;
	}
	/**
	 * @param is_default_image the is_default_image to set
	 */
	public void setIs_default_image(String is_default_image) {
		this.is_default_image = is_default_image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getIs_activate() {
		return is_activate;
	}

	public void setIs_activate(String is_activate) {
		this.is_activate = is_activate;
	}

	public String getIs_verified() {
		return is_verified;
	}

	public void setIs_verified(String is_verified) {
		this.is_verified = is_verified;
	}

	public String getIs_guestuser() {
		return is_guestuser;
	}

	public void setIs_guestuser(String is_guestuser) {
		this.is_guestuser = is_guestuser;
	}

	public String getIs_premium_user() {
		return is_premium_user;
	}

	public void setIs_premium_user(String is_premium_user) {
		this.is_premium_user = is_premium_user;
	}

	public String getBackup_key() {
		return backup_key;
	}

	public void setBackup_key(String backup_key) {
		this.backup_key = backup_key;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
}
