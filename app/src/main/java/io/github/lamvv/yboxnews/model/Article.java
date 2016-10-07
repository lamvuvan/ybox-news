package io.github.lamvv.yboxnews.model;

/**
 * Created by lamvu on 10/7/2016.
 */

public class Article {

    private String title;
    private String content;
    private String link;
    private String img;
    private String category;
    private String pubDate;

    public Article() {
    }

    public Article(String title, String content, String link, String img, String category, String pubDate) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.img = img;
        this.category = category;
        this.pubDate = pubDate;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
}
