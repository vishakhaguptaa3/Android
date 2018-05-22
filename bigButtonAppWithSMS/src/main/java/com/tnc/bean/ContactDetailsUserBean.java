package com.tnc.bean;


public class ContactDetailsUserBean 
{
	public void setImage(byte[] image) {
		Image = image;
	}
	int BBID;
	String Name="";
	String Email="";
	String PhoneNumber="";
	byte[] Image;
	boolean IsDefaultImage;

	public ContactDetailsUserBean()
	{
	}

	public ContactDetailsUserBean(int BBID,String Name,String Email,String PhoneNumber,
			byte[] Image,boolean IsDefaultImage)
	{
		this.BBID=BBID;
		this.Name=Name;
		this.Email=Email;
		this.PhoneNumber=PhoneNumber;
		this.Image=Image;
		this.IsDefaultImage=IsDefaultImage;
	}
	/**
	 * @return the bBID
	 */
	public int getBBID() {
		return BBID;
	}
	/**
	 * @param bBID the bBID to set
	 */
	public void setBBID(int bBID) {
		BBID = bBID;
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
	 * @return the email
	 */
	public String getEmail() {
		return Email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		Email = email;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return PhoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		PhoneNumber = phoneNumber;
	}
	/**
	 * @return the image
	 */
	public byte[] getImage() {
		return Image;
	}
	/**
	 * @param image the image to set
	 */

	/**
	 * @return the isDefaultImage
	 */
	public boolean isIsDefaultImage() {
		return IsDefaultImage;
	}

	/**
	 * @param isDefaultImage the isDefaultImage to set
	 */
	public void setIsDefaultImage(boolean isDefaultImage) {
		IsDefaultImage = isDefaultImage;
	}
}
