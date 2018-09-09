package com.kid.brain.provider.request.model.history;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.provider.request.model.Error;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class TestResponse implements Serializable {

    @SerializedName("responseVO")
    private Error error;

    @SerializedName("testHistoryVO")
    private HistoryResponse history;

    public Error getError() {
        return error;
    }

    public HistoryResponse getHistory() {
        return history;
    }
}
