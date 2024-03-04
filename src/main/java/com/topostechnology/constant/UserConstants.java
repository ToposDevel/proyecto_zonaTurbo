package com.topostechnology.constant;

public class UserConstants {
	
	public static final String REGEX_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\-[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String REGEX_CELLPHONE_NUMBER = "^[0-9]{10}$";
	public static final String REGEX_IMEI_NUMBER = "^[0-9]{15}$";
	
	private static final int USER_NAME_SIZE = 30;
	private static final int USER_LAST_NAME_SIZE = 36;
	private static final int USER_SECOND_NAME_SIZE = 36;
	private static final int USER_CELLPHONE_SIZE = 10;
	private static final int USER_EMAIL_SIZE = 50;
	private static final int USER_PASSWORD_SIZE = 20;
	private static final int USER_PASSWORD2_SIZE = 20;
	
}
