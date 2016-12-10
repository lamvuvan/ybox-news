package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/9/2016.
 */

public class Stats implements Serializable {

    @SerializedName("view")
    private String view;

    public Stats() {
    }

    public Stats(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

}
