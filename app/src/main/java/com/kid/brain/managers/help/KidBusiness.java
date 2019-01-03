package com.kid.brain.managers.help;

import android.text.TextUtils;

import com.kid.brain.managers.enums.EGender;
import com.kid.brain.models.Account;
import com.kid.brain.models.Kid;
import com.kid.brain.models.Rate;
import com.kid.brain.util.Constants;

import java.util.List;

public class KidBusiness {

    private static KidBusiness mKidBusiness;

    public static KidBusiness getInstance() {
        if (mKidBusiness == null) {
            mKidBusiness = new KidBusiness();
        }
        return mKidBusiness;
    }

    public String getGender(int gender) {
        String languageCode = KidPreference.getStringValue(KidPreference.KEY_LANGUAGE_CODE);
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = Constants.Language.LANG_VI;
        }

        if (languageCode.equalsIgnoreCase(Constants.Language.LANG_VI)) {
            if (gender == Account.MALE) {
                return EGender.MALE.getName();
            }
            return EGender.FEMALE.getName();
        } else {
            if (gender == Account.MALE) {
                return EGender.MALE.toString().toLowerCase();
            }
            return EGender.FEMALE.toString().toLowerCase();
        }
    }

    public Kid getDefault() {
        Kid account = new Kid();
        account.setChildrenId("-1");
        return account;
    }

    public Rate findRateByScore(List<Rate> rates, int inputScore) {
        Rate outRate = new Rate();
        if (rates == null || rates.size() == 0) {
            return outRate;
        }

        for (Rate rate : rates) {
            if (!TextUtils.isEmpty(rate.getScore())) {
                if (rate.getScore().contains(",")) {
                    String[] scores = rate.getScore().split(",");
                    for (String score : scores) {
                        if (Integer.parseInt(score) == inputScore) {
                            return rate;
                        }
                    }
                } else if (Integer.parseInt(rate.getScore()) == inputScore) {
                    return rate;
                }
            }
        }

        return outRate;
    }
}
