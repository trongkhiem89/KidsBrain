package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.models.Kid;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class AddKidParams implements Serializable {

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("gender")
    private int gender;

    @SerializedName("imgBase64")
    private String imgBase64 = "";

    @SerializedName("dob")
    private String dob;

    public AddKidParams(Kid account) throws NullPointerException {
        this.fullName = account.getFullName();
        this.gender = account.getGender();
        this.dob = account.getBirthDay();
        this.imgBase64 = account.getPhoto();
    }
}
