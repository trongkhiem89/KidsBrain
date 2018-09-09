package com.kid.brain.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.kid.brain.managers.help.KidPreference;

import java.util.Locale;

public class LocaleManager {

    public static Context setLocale(Context c) {
        return updateResources(c, getLanguage(c));
    }

    public static Context setNewLocale(Context c, String language) {
        return updateResources(c, language);
    }

    public static String getLanguage(Context c) {
        String languageCode = KidPreference.getStringValue(KidPreference.KEY_LANGUAGE_CODE);
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = Constants.Language.LANG_VI;
        }
        return languageCode;
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        return context;
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
    }
}