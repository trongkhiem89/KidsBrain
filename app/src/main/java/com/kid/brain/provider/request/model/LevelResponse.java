package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.models.Level;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 4/5/17.
 */

public class LevelResponse implements Serializable {

    @SerializedName("responseVO")
    private Error error;

    @SerializedName("ageVOs")
    private List<Level> levels;

    public Error getError() {
        return error;
    }

    public List<Level> getLevels() {
        return levels;
    }
}
