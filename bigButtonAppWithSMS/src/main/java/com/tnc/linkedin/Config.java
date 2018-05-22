package com.tnc.linkedin;

public class Config {

	public static String LINKEDIN_CONSUMER_KEY = "75hc1yi8ikcj3k";
	public static String LINKEDIN_CONSUMER_SECRET = "9OgUFDPMtKi10jSz";
	public static String scopeParams = "rw_nus+r_basicprofile";
	
	public static String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	public static String OAUTH_CALLBACK_HOST = "callback";
	public static String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
}
