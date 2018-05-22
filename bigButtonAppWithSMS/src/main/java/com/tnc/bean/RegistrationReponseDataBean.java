package com.tnc.bean;

import com.google.gson.annotations.SerializedName;

public class RegistrationReponseDataBean
{
	@SerializedName("name")
	String name;

	@SerializedName("number")
	String number;

	@SerializedName("email")
	String email;

	@SerializedName("image")
	String image;

	@SerializedName("is_default_image")
	String is_default_image;

	@SerializedName("public_key")
	String public_key;

	@SerializedName("backupkey")
	String backupkey;

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
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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
	 * @return the backupkey
	 */
	public String getBackupkey() {
		return backupkey;
	}

	/**
	 * @param backupkey the backupkey to set
	 */
	public void setBackupkey(String backupkey) {
		this.backupkey = backupkey;
	}
}
