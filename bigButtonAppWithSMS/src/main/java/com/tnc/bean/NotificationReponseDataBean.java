package com.tnc.bean;


import com.google.gson.annotations.SerializedName;

public class NotificationReponseDataBean
{

	@SerializedName("id")
	public String id;

	@SerializedName("from_user_id")
	public String from_user_id;

	@SerializedName("to_user_id")
	public String to_user_id;

	@SerializedName("type")
	public String type;

	@SerializedName("message")
	public String message;

	@SerializedName("image")
	public String image;

	@SerializedName("status")
	public int status;

	@SerializedName("datetime")
	public String datetime;
	
	@SerializedName("name")
	public String name;

	@SerializedName("from_user_phone")
	public String from_user_phone;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the from_user_id
	 */
	public String getFrom_user_id() {
		return from_user_id;
	}

	/**
	 * @param from_user_id the from_user_id to set
	 */
	public void setFrom_user_id(String from_user_id) {
		this.from_user_id = from_user_id;
	}

	/**
	 * @return the to_user_id
	 */
	public String getTo_user_id() {
		return to_user_id;
	}

	/**
	 * @param to_user_id the to_user_id to set
	 */
	public void setTo_user_id(String to_user_id) {
		this.to_user_id = to_user_id;
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

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(String datetime) {
		this.datetime = datetime;
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

	public String getFrom_user_phone() {
		return from_user_phone;
	}

	public void setFrom_user_phone(String from_user_phone) {
		this.from_user_phone = from_user_phone;
	}
}
