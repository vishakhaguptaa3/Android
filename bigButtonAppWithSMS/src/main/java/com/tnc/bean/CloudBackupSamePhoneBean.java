package com.tnc.bean;

public class CloudBackupSamePhoneBean 
{
	String restore_type="";
	String device_id="";
	String phone="";
	String backup_key="";
	public CloudBackupSamePhoneBean(String restore_type, String device_id,
			String phone, String backup_key) {
		super();
		this.restore_type = restore_type;
		this.device_id = device_id;
		this.phone = phone;
		this.backup_key = backup_key;
	}
	/**
	 * @return the restore_type
	 */
	public String getRestore_type() {
		return restore_type;
	}
	/**
	 * @param restore_type the restore_type to set
	 */
	public void setRestore_type(String restore_type) {
		this.restore_type = restore_type;
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
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the backup_key
	 */
	public String getBackup_key() {
		return backup_key;
	}
	/**
	 * @param backup_key the backup_key to set
	 */
	public void setBackup_key(String backup_key) {
		this.backup_key = backup_key;
	}
	
}
