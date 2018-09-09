package com.kid.brain.provider.request;

import android.text.TextUtils;

import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.util.Constants;

/**
 * Created by khiemnt on 4/5/17.
 **/

public class HeaderSession {

    public HeaderSession() {
    }

    public String getAppId() {
        return WebserviceConfig.CID_APP_ID;
    }

    public String getContentType() {
        return WebserviceConfig.HEADER_CONTENT_TYPE_VALUE;
    }

    public String getLanguageCode() {
        String languageCode = KidPreference.getStringValue(KidPreference.KEY_LANGUAGE_CODE);
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = Constants.Language.LANG_VI;;
        }
        return languageCode;
    }

}
