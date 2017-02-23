package io.github.lamvv.yboxnews.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lamvu on 10/7/2016.
 */

public class Article implements Serializable {

    @SerializedName("id")
    private double id;
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
    @SerializedName("type")
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

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public Content getContent() {
        return content;
    }

    public Stats getStats() {
        return stats;
    }

    public Timestamps getTimestamps() {
        return timestamps;
    }

    public Links getLinks() {
        return links;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Article other = (Article) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
