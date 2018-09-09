package com.kid.brain.managers.help;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.kid.brain.managers.application.KidApplication;

public class KidPreference {

    private static final String PREF = "KidPreferences";
    public static final String KEY_PIN = "Pin";
    public static final String KEY_LANGUAGE_CODE = "LanguageCode";
    public static final String KEY_INTRODUCED = "Introduced";
    public static final String KEY_SHOW_TUTORIAL = "Tutorial";
    public static final String KEY_LOGGED = "Logged";

    public static final String KEY_FULL_NAME = "FullName";
    public static final String KEY_PHONE_NUMBER = "PhoneNumber";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PHOTO = "Photo";
    public static final String KEY_USER_ID = "UserId";

    public static void saveValue(String key, Object value) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            Gson gson = new Gson();
            String jsonData = gson.toJson(value);
            editor.putString(key, jsonData);
        }
        editor.apply();
    }

    public static String getStringValue(String key) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static int getIntValue(String key) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, -1);
    }
    public static boolean getBooleanValue(String key) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }
    public static float getFloatValue(String key) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getFloat(key, -1);
    }

    public static long getLongValue(String key) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sharedPref.getLong(key, -1);
    }

    public static <T> Object getObjectValue(String key, Class<T> objectClass) {
        SharedPreferences sharedPref = KidApplication.getInstance().getAppContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(sharedPref.getString(key, ""), objectClass);
    }

}
