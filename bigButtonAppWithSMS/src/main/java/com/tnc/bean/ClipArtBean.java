package com.tnc.bean;

public class ClipArtBean {
	public String ClipArtName="";

	public ClipArtBean(){		
		super();
	}

	public ClipArtBean(String clipArtName) {
		super();
		ClipArtName = clipArtName;
	}

	/**
	 * @return the clipArtName
	 */
	public String getClipArtName() {
		return ClipArtName;
	}

	/**
	 * @param clipArtName the clipArtName to set
	 */
	public void setClipArtName(String clipArtName) {
		ClipArtName = clipArtName;
	}

}
