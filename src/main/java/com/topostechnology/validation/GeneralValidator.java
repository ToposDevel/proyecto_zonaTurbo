package com.topostechnology.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralValidator {

	public static boolean validatePattern(String pattern, String string) {
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher(string);
		return mat.matches();
	}

}
