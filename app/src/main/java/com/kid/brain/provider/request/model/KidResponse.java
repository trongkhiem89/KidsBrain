package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;
import com.kid.brain.models.Kid;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class KidResponse implements Serializable {

    @SerializedName("responseVO")
    private Error error;

    @SerializedName("childrenVO")
    private Kid kid;

    public Error getError() {
        return error;
    }

    public Kid getKid() {
        return kid;
    }
}
