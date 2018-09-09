package com.kid.brain.provider.request.model.history;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 4/5/17.
 */

public class HistoryResponse implements Serializable {

    @SerializedName("testHistoryId")
    private String historyId;

    @SerializedName("childrenId")
    private String kidId;

    @SerializedName("createAt")
    private String createAt;

    @SerializedName("testResultVOs")
    private List<CategoryVoResponse> categoryVoResponses;

    public String getHistoryId() {
        return historyId;
    }

    public String getKidId() {
        return kidId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public List<CategoryVoResponse> getCategoryVoResponses() {
        return categoryVoResponses;
    }
}
