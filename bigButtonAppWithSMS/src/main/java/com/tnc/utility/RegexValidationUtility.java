package com.tnc.utility;

import java.util.regex.Pattern;

public class RegexValidationUtility 
{
	public final static Pattern ALPHA_NUMERIC_PATTERN=Pattern.compile("^[a-zA-Z0-9 .']{1,25}$");

	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	
	public static boolean isValidEmail(String email)
	{
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	public static boolean alphanumeric_validate(final String name) 
	{
		return ALPHA_NUMERIC_PATTERN.matcher(name).matches();
	}
}
