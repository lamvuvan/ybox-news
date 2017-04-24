package io.github.lamvv.yboxnews.repository.network;

import io.github.lamvv.yboxnews.model.ArticleList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by lamvu on 10/8/2016.
 */

public interface YboxService {

    @Headers("Cache-Control: public, max-age=60")
    @GET("search?")
    Call<ArticleList> getArticle(@Query("page") int page);

    @Headers("Cache-Control: public, max-age=60")
    @GET("search?")
    Call<ArticleList> getCategoryArticle(@Query("category") String category, @Query("page") int page);

    @Headers("Cache-Control: public, max-age=60")
    @GET("search?")
    Call<ArticleList> getTypeArticle(@Query("article_type") String articleType, @Query("page") int page);

    @Headers("Cache-Control: public, max-age=60")
    @GET("search?article_type=new&page")
    Call<ArticleList> getNewestArticle(@Query("page") int page);

}
