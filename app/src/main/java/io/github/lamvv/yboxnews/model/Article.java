package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/7/2016.
 */

public class Article implements Serializable {

    @SerializedName("image")
    private String image;
    @SerializedName("category")
    private String category;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private Content content;
    @SerializedName("stats")
    private Stats stats;
    @SerializedName("timestamps")
    private Timestamps timestamps;
    @SerializedName("links")
    private Links links;
    private String type;

    public Article() {
    }

    public Article(String type){
        this.type = type;
    }

    public Article(String image, String category, String title, Content content, Stats stats, Timestamps timestamps, Links links) {
        this.image = image;
        this.category = category;
        this.title = title;
        this.content = content;
        this.stats = stats;
        this.timestamps = timestamps;
        this.links = links;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Timestamps getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Timestamps timestamps) {
        this.timestamps = timestamps;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getType() {
        return type;
    }

}
