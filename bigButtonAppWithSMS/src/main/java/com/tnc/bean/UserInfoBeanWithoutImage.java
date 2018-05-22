package com.tnc.bean;

public class UserInfoBeanWithoutImage {

	String name="";
	String email="";
	String is_default_image;

	public UserInfoBeanWithoutImage(String name, String email,
			String is_default_image) {
		super();
		this.name = name;
		this.email = email;
		this.is_default_image = is_default_image;
	}

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
}
