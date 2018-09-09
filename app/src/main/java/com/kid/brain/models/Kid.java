package com.kid.brain.models;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.R;
import com.kid.brain.util.AppCommon;
import com.kid.brain.view.dialog.DateTimeUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Kid extends Account implements Serializable {

    private String childrenId;
    private String parentId;
    private String ageId;
    private String isView;

    @SerializedName("testHistoryVOs")
    private List<History> histories;

    public Kid() {

    }

    public String getNameAndAge (Context context) {
        String age = getAge(context);
        if (TextUtils.isEmpty(age)) {
            return getFullName();
        }
        return getFullName() + " ( " + age + " )";
    }

    public Level convertKid2Level() {
        Level level = new Level();

        if (TextUtils.isEmpty(getAgeId())) {
            level.setId(0);
        } else {
            level.setId(Integer.parseInt(getAgeId()));
        }

        level.setName(this.getFullName());
        level.setIcon(this.getPhoto());
        return level;
    }

    public String getAge(Context context) {
        try {
            if (TextUtils.isEmpty(this.getBirthDay())) return "";

            String dateOfBirth = DateTimeUtils.convertUTCToLocal(this.getBirthDay());
            Age age = AppCommon.calculateAge(DateTimeUtils.strToDate(dateOfBirth));
            if (age.getYears() > 0) {
                String yearOld = String.valueOf(age.getYears());
                if (age.getMonths() > 0) {
                    yearOld = yearOld + "." + age.getMonths();
                }
                return context.getString(R.string.str_old, yearOld);
            }
            return context.getString(R.string.str_month, String.valueOf(age.getMonths()));
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isEnableView () {
        if (!TextUtils.isEmpty(isView) || "1".equals(isView)) {
            return true;
        }
        return false;
    }

    public String getIsView() {
        return isView;
    }

    public void setIsView(String isView) {
        this.isView = isView;
    }

    public String getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(String childrenId) {
        this.childrenId = childrenId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAgeId() {
        return ageId;
    }

    public void setAgeId(String ageId) {
        this.ageId = ageId;
    }

    public List<History> getHistories() {
        return histories;
    }

    @Override
    public String toString() {
        return "Kid{" +
                "childrenId='" + childrenId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", ageId='" + ageId + '\'' +
                '}';
    }
}
