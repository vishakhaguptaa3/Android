package com.tnc.bean;

public class ConfigurationBean
{
	boolean isIntroWatched=false;
	boolean isRegisteredUser=false;
	boolean isRegisterForBackup=false;
	boolean isDBChanged=false;
	String text="";

	public ConfigurationBean()
	{
	}

	public ConfigurationBean(boolean isIntroWatched,boolean isRegisteredUser,
			boolean isRegisterForBackup,boolean isDBChanged,String text)
	{
		this.isIntroWatched=isIntroWatched;
		this.isRegisteredUser=isRegisteredUser;
		this.isRegisterForBackup=isRegisterForBackup;
		this.isDBChanged=isDBChanged;
		this.text=text;
	}

	/**
	 * @return the isIntroWatched
	 */
	public boolean isIntroWatched() {
		return isIntroWatched;
	}

	/**
	 * @param isIntroWatched the isIntroWatched to set
	 */
	public void setIntroWatched(boolean isIntroWatched) {
		this.isIntroWatched = isIntroWatched;
	}

	/**
	 * @return the isRegisteredUser
	 */
	public boolean isRegisteredUser() {
		return isRegisteredUser;
	}

	/**
	 * @param isRegisteredUser the isRegisteredUser to set
	 */
	public void setRegisteredUser(boolean isRegisteredUser) {
		this.isRegisteredUser = isRegisteredUser;
	}

	/**
	 * @return the isRegisterForBackup
	 */
	public boolean isRegisterForBackup() {
		return isRegisterForBackup;
	}

	/**
	 * @param isRegisterForBackup the isRegisterForBackup to set
	 */
	public void setRegisterForBackup(boolean isRegisterForBackup) {
		this.isRegisterForBackup = isRegisterForBackup;
	}

	/**
	 * @return the isDBChanged
	 */
	public boolean isDBChanged() {
		return isDBChanged;
	}

	/**
	 * @param isDBChanged the isDBChanged to set
	 */
	public void setDBChanged(boolean isDBChanged) {
		this.isDBChanged = isDBChanged;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
