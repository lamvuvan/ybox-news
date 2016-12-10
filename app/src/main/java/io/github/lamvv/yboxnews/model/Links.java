package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/9/2016.
 */

public class Links implements Serializable {

    @SerializedName("detail")
    private String detail;

    public Links() {
    }

    public Links(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

}
