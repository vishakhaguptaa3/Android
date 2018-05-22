package com.tnc.bean;

public class UserRegistrationBean
{
	private String name="";
	private String number="";
	private String email="";
	private String device_id="";
	private String push_notification_id="";
	private String device="";
	private String is_default_image;
	private String image;
	private String group_code;

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	private String country_code;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	private String age;

	private String passcode = "";

	public UserRegistrationBean() 
	{
	}
	//,String number--2nd arguement
	public UserRegistrationBean(String name,String email,String device_id,
			String push_notification_id,String device,String is_default_image,String image,
			String group_code)
	{
		this.name=name;
		//		this.number=number;
		this.email=email;
		this.device_id=device_id;
		this.push_notification_id=push_notification_id;
		this.device=device;
		this.is_default_image=is_default_image;
		this.image=image;
		this.group_code=group_code;
	}
	public UserRegistrationBean(String name,String phoneNumber , String email,String device_id,String age,String country_code,
			String push_notification_id,String device,String is_default_image,String image,
			String group_code)
	{
		this.name=name;
		this.number=phoneNumber;
		this.email=email;
		this.device_id=device_id;
		this.push_notification_id=push_notification_id;
		this.device=device;
		this.is_default_image=is_default_image;
		this.image=image;
		this.group_code=group_code;
		this.age = age;
		this.country_code = country_code;
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
	 * @return the device_id
	 */
	public String getDevice_id() {
		return device_id;
	}
	/**
	 * @param device_id the device_id to set
	 */
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	/**
	 * @return the push_notification_id
	 */
	public String getPush_notification_id() {
		return push_notification_id;
	}
	/**
	 * @param push_notification_id the push_notification_id to set
	 */
	public void setPush_notification_id(String push_notification_id) {
		this.push_notification_id = push_notification_id;
	}
	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}
	/**
	 * @param device the device to set
	 */
	public void setDevice(String device) {
		this.device = device;
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
	 * @return the group_code
	 */
	public String getGroup_code() {
		return group_code;
	}
	/**
	 * @param group_code the group_code to set
	 */
	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	@Override
	public String toString() {
		return name+" \n"+
		number+" \n"+
		email+" \n"+
		device_id+" \n"+
		push_notification_id+" \n"+
		device+" \n"+
		is_default_image+" \n"+
		image+" \n"+
		group_code;
	}
}
