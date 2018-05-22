package com.tnc.webresponse;

import com.google.gson.annotations.SerializedName;

public class HowtoReponseDataBean {

	@SerializedName("question")
	public String question; 

	@SerializedName("answer")
	public String answer; 

	@SerializedName("version")
	public String version; 

	@SerializedName("add_date")
	public String add_date;

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the add_date
	 */
	public String getAdd_date() {
		return add_date;
	}

	/**
	 * @param add_date the add_date to set
	 */
	public void setAdd_date(String add_date) {
		this.add_date = add_date;
	}
}
