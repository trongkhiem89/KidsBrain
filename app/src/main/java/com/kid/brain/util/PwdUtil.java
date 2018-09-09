package com.kid.brain.util;

import java.math.BigInteger;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PwdUtil {
	public static final int MAX_STRENGTH_VALUE = 44;

	// range 0 is { | } ~
	// range 1 is 0..9
	// range 2 is 33! " # $ % & ' ( ) * + , - . /
	// range 3 is a..z
	// range 4 is : ; < = > ? @
	// range 5 is A..Z
	// range 6 is [ \ ] ^ _ `
	// 0 2 4 group split into 2 sub-groups
	// 1 6 group split into 2 sub-groups
	// 3 split into 6 groups
	// 5 split into 2 groups
	public static String genPassword(int n) {
		char[] pw = new char[n];
		int c = 'A';
		int randomInt = 0;
		Random random = new Random();
		for (int i = 0; i < n; i++) {
			randomInt = random.nextInt(12);
			switch (randomInt) {
			case 0:
			case 1:
				randomInt = random.nextInt(26);
				if (randomInt >= 0 && randomInt < 4) {
					c = '{' + random.nextInt(4);
				} else if (randomInt >= 4 && randomInt < 19) {
					c = '!' + random.nextInt(15);
				} else {
					c = ':' + random.nextInt(7);
				}
				break;
			case 2:
			case 3:
				randomInt = random.nextInt(4);
				if (randomInt == 0) {
					c = '[' + random.nextInt(6);
				} else {
					c = '0' + random.nextInt(10);
				}
				break;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				c = 'a' + random.nextInt(26);
				break;
			case 10:
			case 11:
				c = 'A' + random.nextInt(26);
				break;
			}
			pw[i] = (char) c;
		}
		return new String(pw);
	}

	public static String genSimplePassword() {
		// SecureRandom random = new SecureRandom();
		Random random = new Random();
		return new BigInteger(40, random).toString(35);
	}

	public static int measurePasswordStrength(String pwd) {
		if (pwd == null || pwd.length() < 1) {
			return 0;
		}

		int upper = 0, lower = 0, numbers = 0, special = 0;
		int intScore = 0;
		Pattern p;
		Matcher m;
		int pwdLen = pwd.length();
		if (pwdLen < 5) // length less than 5
		{
			intScore += 3;
		} else if (pwdLen > 4 && pwd.length() < 8) // length between 5 and 7
		{
			intScore += 6;
		} else if (pwdLen > 7 && pwd.length() < 16) // length between 8 and 15
		{
			intScore += 12;
		} else if (pwdLen > 15) // length more than 15
		{
			// intScore += 18;
			intScore += 8;
		}
		// LETTERS
		p = Pattern.compile(".??[a-z]");
		m = p.matcher(pwd);
		while (m.find()) // test if at least one lower case letter
		{
			lower += 1;
		}
		if (lower > 0) {
			intScore += 1;
		}
		p = Pattern.compile(".??[A-Z]");
		m = p.matcher(pwd);
		while (m.find()) // test if at least one upper case letter
		{
			upper += 1;
		}
		if (upper > 0) {
			intScore += 5;
		}
		// NUMBERS
		p = Pattern.compile(".??[0-9]");
		m = p.matcher(pwd);
		while (m.find()) // test if at least one number
		{
			numbers += 1;
		}
		if (numbers > 0) {
			intScore += 5;
			if (numbers > 1) {
				// intScore += 2;
				intScore += 3;
				// if (numbers > 2) {
				// intScore += 3;
				// }
			}
		}
		// SPECIAL CHAR
		// range 0 is { | } ~
		// range 2 is ! " # $ % & ' ( ) * + , - . / (ignore comma ,)
		// range 4 is : ; < = > ? @
		// range 6 is [ \ ] ^ _ `
		// p = Pattern.compile(".??[:,!,@,#,$,%,^,&,*,?,_,~]");
		p = Pattern.compile(
				".??[{, |, }, ~, !, \", #, $, %, &, ', (, ), *, +, -, ., /, :, ;, <, =, >, ?, @, [, \\, ], ^, _, `]");
		m = p.matcher(pwd);
		while (m.find()) // test if at least one special character
		{
			special += 1;
		}
		if (special > 0) {
			intScore += 5;
			if (special > 1) {
				intScore += 5;
			}
		}
		// COMBOS
		if (upper > 0 && lower > 0) // test if both upper and lower case
		{
			intScore += 2;
		}
		if ((upper > 0 || lower > 0) && numbers > 0) // test if both letters and
														// numbers
		{
			intScore += 2;
		}
		if ((upper > 0 || lower > 0) && numbers > 0 && special > 0) // test if
																	// letters,
																	// numbers,
																	// and
																	// special
																	// characters
		{
			intScore += 2;
		}
		if (upper > 0 && lower > 0 && numbers > 0 && special > 0) // test if
																	// upper,
																	// lower,
																	// numbers,
																	// and
																	// special
																	// characters
		{
			intScore += 2;
		}
		return intScore;
	}

	public static int measurePasswordStrengthPercent(String pwd) {
		if (pwd == null || pwd.length() < 1) {
			return 0;
		}

		int strength = measurePasswordStrength(pwd);
		int percent = (int) (((float) strength / MAX_STRENGTH_VALUE) * 100);
		return percent;
	}

}
