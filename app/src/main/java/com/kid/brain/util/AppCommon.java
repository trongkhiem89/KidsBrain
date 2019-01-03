package com.kid.brain.util;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.kid.brain.models.Age;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class AppCommon {

    public static boolean validateAlphaNumericWithUnderScore(String alphaNumbericString) {
        boolean valid = false;
        String regexString = "^[a-zA-Z0-9_]*$";
        valid = alphaNumbericString.matches(regexString);
        return valid;
    }

    public static boolean beginIsNumber(String name) {
        boolean valid = false;
        for (int k = 0; k < 10; k++) {
            if (name.charAt(0) == '0' + k) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    // Username can contain alphanumeric
    // characters, underscore (_), at( @ ), or dot(.) sign
    public static boolean validateUserName(String userName) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_\\.]*");
        return pattern.matcher(userName).matches();
    }

    public static boolean acceptName(String name) {
        return true;
    }

    // Password has atleast an uppercase
    // letter, an lowercase letter, and an number.
    public static boolean validatePassword(String password) {
        Pattern pattern1 = Pattern.compile(".*[A-Z].*");
        Pattern pattern2 = Pattern.compile(".*[a-z].*");
        Pattern pattern3 = Pattern.compile(".*[0-9].*");
        return pattern1.matcher(password).matches() && pattern2.matcher(password).matches()
                && pattern3.matcher(password).matches();
    }

    public static boolean acceptUrl(String url) {
        Pattern pattern = Pattern.compile("[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]*");
        return pattern.matcher(url).matches();
    }

    public static boolean acceptEmail(String email) {
        Pattern pattern = Pattern.compile("[-a-zA-Z0-9@.]*");
        return pattern.matcher(email).matches();
    }

    public static boolean validateEmail(String emailString) {
        String emailRegEx = "\\b([a-zA-Z0-9%_.+\\-]+)@([a-zA-Z0-9.\\-]+?\\.[a-zA-Z]{2,6})\\b";
        return emailString.matches(emailRegEx);
    }

    public static boolean validatePhone(String phoneString) {
        String phoneRegEx = "^[0-9]{10,11}$";
        return phoneString.matches(phoneRegEx);
    }

    public static boolean validateUrl(String urlString) {
        String urlRegEx = "(http|https)://((\\w)*|([0-9]*)|([-|_])*)+([-|\\.|/|?|=|&]((\\w)*|([0-9]*)|([-|_])*))+";
        return urlString.matches(urlRegEx);
    }

    public static boolean validateDate(String dateString) {
        String dateRegEx = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
        return dateString.matches(dateRegEx);
    }

    public static boolean validateNumber(String numberString) {
        String numberRegEx = "^[0-9]*$";
        return numberString.matches(numberRegEx);
    }

    public static String formatUTF8(String msgUTF8) {
        if (TextUtils.isEmpty(msgUTF8))
            return msgUTF8;
        try {
            return URLDecoder.decode(msgUTF8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return msgUTF8;
    }

    public static void changeLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }

    public static Age calculateAge(Date birthDate) {
        int years = 0;
        int months = 0;
        int days = 0;

        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }

        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }

        // < 36 thang
        if (months > 0) {
            if (years < 3) {
                months += years * 12;
                years = 0;
            }
        } else {
            if (years <= 3) {
                months += years * 12;
                years = 0;
            }
        }

        //Create new Age object
        return new Age(days, months, years);
    }
}
