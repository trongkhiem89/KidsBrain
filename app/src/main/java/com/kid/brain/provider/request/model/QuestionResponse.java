package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.models.SubCategory;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class QuestionResponse implements Serializable {

    @SerializedName("responseVO")
    private Error error;

    @SerializedName("categoryMappingVO")
    private SubCategory data;

    public Error getError() {
        return error;
    }

    public SubCategory getData() {
        return data;
    }
}
