package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 11/7/16.
 */
public class Error implements Serializable {

    @SerializedName("error")
    private int code;

    @SerializedName("message")
    private String message;

    public Error() {
    }

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
