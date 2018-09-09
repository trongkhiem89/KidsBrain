package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.models.Level;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class CategoryResponse implements Serializable {

    @SerializedName("responseVO")
    private Error error;

    @SerializedName("ageVO")
    private Level level;

    public Error getError() {
        return error;
    }

    public Level getLevel() {
        return level;
    }
}
