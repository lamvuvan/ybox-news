package io.github.lamvv.yboxnews.model;

/**
 * Created by lamvu on 10/7/2016.
 */

public class Article {

    private String image;
    private String category;
    private String title;
    private String content;
    private String view;
    private String detail;
    private String pubDate;
    private String nextPageUrl;

    public Article() {
    }

    public Article(String image, String category, String title, String content, String view, String detail,
                   String pubDate, String nextPageUrl) {
        this.image = image;
        this.category = category;
        this.title = title;
        this.content = content;
        this.view = view;
        this.detail = detail;
        this.pubDate = pubDate;
        this.nextPageUrl = nextPageUrl;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
