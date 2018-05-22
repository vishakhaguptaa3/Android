package com.tnc.bean;

public class BBContactsBean
{
	String countryCode="";
	int mobID;
	int BBID;
	String name="";
	String phoneNumber="";
	String image="";
	boolean IsMobile=false;

	String category = "";

	byte[] mImageArray;

	boolean isContactChecked = false;

	public BBContactsBean()
	{
	}

	public BBContactsBean(String countryCode,int mobID,int BBID,String name,String phoneNumber)
	{
		this.mobID=mobID;
		this.BBID=BBID;
		this.name=name;
		this.phoneNumber=phoneNumber;
	}
	/**
	 * @return the mobID
	 */
	public int getMobID() {
		return mobID;
	}

	/**
	 * @param mobID the mobID to set
	 */
	public void setMobID(int mobID) {
		this.mobID = mobID;
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
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

	public boolean isMobile() {
		return IsMobile;
	}

	public void setMobile(boolean mobile) {
		IsMobile = mobile;
	}

	public byte[] getmImageArray() {
		return mImageArray;
	}

	public void setmImageArray(byte[] mImageArray) {
		this.mImageArray = mImageArray;
	}

    public boolean isContactChecked() {
        return isContactChecked;
    }

    public void setContactChecked(boolean contactChecked) {
        isContactChecked = contactChecked;
    }
}
