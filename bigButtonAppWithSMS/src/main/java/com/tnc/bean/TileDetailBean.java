package com.tnc.bean;

public class TileDetailBean 
{
	public String Name;
	public String Phone;
	public boolean Is_Mobile;
	public boolean International_Access_Code_Flag;
	public String International_Access_Code;
//	public boolean Is_TncUser=false;
	
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
	 * @return the is_Mobile
	 */
	public boolean isIs_Mobile() {
		return Is_Mobile;
	}
	/**
	 * @param is_Mobile the is_Mobile to set
	 */
	public void setIs_Mobile(boolean is_Mobile) {
		Is_Mobile = is_Mobile;
	}
	/**
	 * @return the international_Access_Code_Flag
	 */
	public boolean isInternational_Access_Code_Flag() {
		return International_Access_Code_Flag;
	}
	/**
	 * @param international_Access_Code_Flag the international_Access_Code_Flag to set
	 */
	public void setInternational_Access_Code_Flag(
			boolean international_Access_Code_Flag) {
		International_Access_Code_Flag = international_Access_Code_Flag;
	}
	/**
	 * @return the international_Access_Code
	 */
	public String getInternational_Access_Code() {
		return International_Access_Code;
	}
	/**
	 * @param international_Access_Code the international_Access_Code to set
	 */
	public void setInternational_Access_Code(String international_Access_Code) {
		International_Access_Code = international_Access_Code;
	}
}
