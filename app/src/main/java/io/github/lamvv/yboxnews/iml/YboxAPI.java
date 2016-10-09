package io.github.lamvv.yboxnews.iml;

import io.github.lamvv.yboxnews.model.ArticleList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lamvu on 10/8/2016.
 */

public interface YboxAPI {

    @GET("search?page=")
    Call<ArticleList> getArticle(@Query("index") int index);
}
