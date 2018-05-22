package com.tnc.bean;

public class NotificationUpdateBean 
{
	int notification_id;
	int status;
	String is_delete;

	public NotificationUpdateBean(int notification_id, int status,
			String is_delete) {
		super();
		this.notification_id = notification_id;
		this.status = status;
		this.is_delete = is_delete;
	}

	/**
	 * @return the notification_id
	 */
	public int getNotification_id() {
		return notification_id;
	}

	/**
	 * @param notification_id the notification_id to set
	 */
	public void setNotification_id(int notification_id) {
		this.notification_id = notification_id;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the is_delete
	 */
	public String isIs_delete() {
		return is_delete;
	}

	/**
	 * @param is_delete the is_delete to set
	 */
	public void setIs_delete(String is_delete) {
		this.is_delete = is_delete;
	}
		
}
