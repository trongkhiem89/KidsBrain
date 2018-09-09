package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class LoginParams implements Serializable {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public LoginParams(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
