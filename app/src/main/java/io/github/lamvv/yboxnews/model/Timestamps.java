package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/9/2016.
 */

public class Timestamps implements Serializable {

    @SerializedName("updatedAt")
    private String updatedAt;

    public Timestamps() {
    }

    public Timestamps(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

}
