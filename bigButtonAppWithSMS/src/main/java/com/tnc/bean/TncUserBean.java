package com.tnc.bean;

public class TncUserBean {

	public String Name;
	public String Phone;
	public String Country;
	public String Is_Mobile;
	public String ISDCodeFlag;
	public String ISDCode;

	public TncUserBean(String name, String phone, String country,
			String is_Mobile, String iSDCodeFlag, String iSDCode) {
		super();
		Name = name;
		Phone = phone;
		Country = country;
		Is_Mobile = is_Mobile;
		ISDCodeFlag = iSDCodeFlag;
		ISDCode = iSDCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return Phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		Phone = phone;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return Country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		Country = country;
	}

	/**
	 * @return the is_Mobile
	 */
	public String getIs_Mobile() {
		return Is_Mobile;
	}

	/**
	 * @param is_Mobile the is_Mobile to set
	 */
	public void setIs_Mobile(String is_Mobile) {
		Is_Mobile = is_Mobile;
	}

	/**
	 * @return the iSDCodeFlag
	 */
	public String getISDCodeFlag() {
		return ISDCodeFlag;
	}

	/**
	 * @param iSDCodeFlag the iSDCodeFlag to set
	 */
	public void setISDCodeFlag(String iSDCodeFlag) {
		ISDCodeFlag = iSDCodeFlag;
	}

	/**
	 * @return the iSDCode
	 */
	public String getISDCode() {
		return ISDCode;
	}

	/**
	 * @param iSDCode the iSDCode to set
	 */
	public void setISDCode(String iSDCode) {
		ISDCode = iSDCode;
	}
}
