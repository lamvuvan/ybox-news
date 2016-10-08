package io.github.lamvv.yboxnews.model;

/**
 * Created by lamvu on 10/7/2016.
 */

public class Article {

    private String title;
    private String content;
    private String link;
    private String image;
    private String category;
    private String pubDate;
    private String nextPageUrl;

    public Article() {
    }

    public Article(String title, String content, String link, String image, String category, String pubDate, String nextPageUrl) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.image = image;
        this.category = category;
        this.pubDate = pubDate;
        this.nextPageUrl = nextPageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }
}
