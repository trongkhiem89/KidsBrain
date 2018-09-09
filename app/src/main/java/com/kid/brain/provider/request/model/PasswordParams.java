package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class PasswordParams implements Serializable {

    @SerializedName("currentPassword")
    private String currentPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public PasswordParams(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
