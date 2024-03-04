package com.topostechnology.utils;


public final class StringUtils {
    public static final String EMPTY = "";

    private StringUtils() {

    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }
    
    public static String getSubstring(String strng, int size) {
    	String substring = null;
    	if(isNotBlank(strng)) {
    		if(strng.length() > size) {
    			substring = strng.substring(0, size-1);
    		} else {
    			substring = strng;
    		}
    	}
    	return substring;
    }

}