package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class NotificationImageStatusResponseDataBean 
{
	@SerializedName("notification_id")
	public String notification_id;

	@SerializedName("image")
	public String image;

	@SerializedName("status")
	public String status;

	@SerializedName("type")
	public String type;

	/**
	 * @return the notification_id
	 */
	public String getNotification_id() {
		return notification_id;
	}

	/**
	 * @param notification_id the notification_id to set
	 */
	public void setNotification_id(String notification_id) {
		this.notification_id = notification_id;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
