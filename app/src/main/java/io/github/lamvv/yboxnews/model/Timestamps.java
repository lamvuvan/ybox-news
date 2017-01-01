package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/9/2016.
 */

public class Timestamps implements Serializable {

    @SerializedName("createdAt")
    private String createdAt;

    public Timestamps() {
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
