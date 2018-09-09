package com.kid.brain.provider.request.model.history;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class SearchHistoryParams implements Serializable {

    @SerializedName("testHistoryId")
    private String code;


    public SearchHistoryParams(String code) {
        this.code = code;
    }
}
