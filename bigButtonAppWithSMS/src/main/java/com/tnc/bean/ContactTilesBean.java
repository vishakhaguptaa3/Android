package com.tnc.bean;

public class ContactTilesBean 
{
	String Name="";
	String PhoneNumber="";
	byte[] Image;
	boolean IsEmergency=false;
	String tilePosition="";
	int IsImagePending;
	String prefix="";
	String CountryCode="";
	boolean IsTncUser=false;
	boolean IsMobile=false;
	String ButtonType;
	int BBID;
	boolean isImageLocked = false;
	boolean isContactChecked = false;

	public ContactTilesBean()
	{
	}

	public ContactTilesBean(String Name,String PhoneNumber,byte[] Image,boolean IsEmergency,boolean IsMobile,
			String tilePosition,String CountryCode)
	{
		this.Name=Name;
		this.PhoneNumber=PhoneNumber;
		this.Image=Image;
		this.IsEmergency=IsEmergency;
		this.tilePosition=tilePosition;
		this.CountryCode=CountryCode;
		this.IsMobile=IsMobile;
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
	public void setImage(byte[] image) {
		Image = image;
	}

	/**
	 * @return the isEmergency
	 */
	public boolean isIsEmergency() {
		return IsEmergency;
	}

	/**
	 * @param isEmergency the isEmergency to set
	 */
	public void setIsEmergency(boolean isEmergency) {
		IsEmergency = isEmergency;
	}

	/**
	 * @return the tilePosition
	 */
	public String getTilePosition() {
		return tilePosition;
	}

	/**
	 * @param tilePosition the tilePosition to set
	 */
	public void setTilePosition(String tilePosition) {
		this.tilePosition = tilePosition;
	}

	/**
	 * @return the isImagePending
	 */
	public int getIsImagePending() {
		return IsImagePending;
	}

	/**
	 * @param isImagePending the isImagePending to set
	 */
	public void setIsImagePending(int isImagePending) {
		IsImagePending = isImagePending;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return CountryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}

	/**
	 * @return the isTncUser
	 */
	public boolean isIsTncUser() {
		return IsTncUser;
	}

	/**
	 * @param isTncUser the isTncUser to set
	 */
	public void setIsTncUser(boolean isTncUser) {
		IsTncUser = isTncUser;
	}

	/**
	 * @return the buttonType
	 */
	public String getButtonType() {
		return ButtonType;
	}

	/**
	 * @param buttonType the buttonType to set
	 */
	public void setButtonType(String buttonType) {
		ButtonType = buttonType;
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
	 * DEVANSHU NATH TRIPATHI
	 */
	
	/**
	 * @return the isMobile
	 */
	public boolean isIsMobile() {
		return IsMobile;
	}

	/**
	 * @param isMobile the isMobile to set
	 */
	public void setIsMobile(boolean isMobile) {
		IsMobile = isMobile;
	}

	public boolean isContactChecked() {
		return isContactChecked;
	}

	public void setContactChecked(boolean contactChecked) {
		isContactChecked = contactChecked;
	}


	public boolean isImageLocked() {
		return isImageLocked;
	}

	public void setImageLocked(boolean imageLocked) {
		isImageLocked = imageLocked;
	}
}
