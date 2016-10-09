package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lamvu on 10/9/2016.
 */

public class ArticleList {

    @SerializedName("data")
    public List<Article> articles;
}
