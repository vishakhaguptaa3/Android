package com.tnc.bean;

public class MessageSendBean
{
	String to_user_id;
	String message;

	public MessageSendBean(){

	}

	public MessageSendBean(String to_user_id, String message) {
		super();
		this.to_user_id = to_user_id;
		this.message = message;
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
}
