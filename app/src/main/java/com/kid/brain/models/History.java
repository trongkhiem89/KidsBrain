package com.kid.brain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class History implements Serializable {

    @SerializedName("testHistoryId")
    private String id;

    @SerializedName("createAt")
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
