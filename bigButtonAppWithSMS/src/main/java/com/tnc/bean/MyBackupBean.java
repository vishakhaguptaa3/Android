package com.tnc.bean;

import java.util.Date;

public class MyBackupBean 
{
	Date date;
	int backupID;

	public MyBackupBean()
	{
	}
	
	public MyBackupBean(Date date,int backupID)
	{
		this.date=date;
		this.backupID=backupID;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the backupID
	 */
	public int getBackupID() {
		return backupID;
	}

	/**
	 * @param backupID the backupID to set
	 */
	public void setBackupID(int backupID) {
		this.backupID = backupID;
	}
}
