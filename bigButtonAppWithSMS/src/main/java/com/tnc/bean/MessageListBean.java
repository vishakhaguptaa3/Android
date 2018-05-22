package com.tnc.bean;

public class MessageListBean 
{
	String message_last_id;

	public MessageListBean(String message_last_id) {
		super();
		this.message_last_id = message_last_id;
	}

	/**
	 * @return the message_last_id
	 */
	public String getMessage_last_id() {
		return message_last_id;
	}

	/**
	 * @param message_last_id the message_last_id to set
	 */
	public void setMessage_last_id(String message_last_id) {
		this.message_last_id = message_last_id;
	}

}
