package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/9/2016.
 */

public class Content implements Serializable {

    @SerializedName("raw")
    private String raw;

    public Content() {
    }

    public Content(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

}
