package io.github.lamvv.yboxnews.iml;

import io.github.lamvv.yboxnews.model.ArticleList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lamvu on 10/8/2016.
 */

public interface YboxAPI {

    @GET("search?page")
    Call<ArticleList> getArticle(@Query("page") int page);

    @GET("search?category=recruitment&page")
    Call<ArticleList> getRecruitmentArticle(@Query("page") int page);

    @GET("search?category=scholarship&page")
    Call<ArticleList> getScholarshipArticle(@Query("page") int page);

    @GET("search?category=event&page")
    Call<ArticleList> getEventArticle(@Query("page") int page);

    @GET("search?category=skill&page")
    Call<ArticleList> getSkillArticle(@Query("page") int page);

    @GET("search?category=face&page")
    Call<ArticleList> getFaceArticle(@Query("page") int page);

    @GET("search?category=competition&page")
    Call<ArticleList> getCompetitionArticle(@Query("page") int page);
}
