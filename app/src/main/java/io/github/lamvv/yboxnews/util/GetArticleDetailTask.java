package io.github.lamvv.yboxnews.util;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.lamvv.yboxnews.iml.GetArticleDetailTaskCompleteListener;

/**
 * Created by lamvu on 10/8/2016.
 */

public class GetArticleDetailTask extends AsyncTask<String, Void, String> {

    private Context context;
    private GetArticleDetailTaskCompleteListener<String> callback;

    public GetArticleDetailTask(Context context, GetArticleDetailTaskCompleteListener<String> callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.showProgress();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder detail = new StringBuilder();
        try {
            Document doc = Jsoup.connect(params[0]).timeout(20000).get();
//            Elements article = doc.select("div.article-detail div");
//            Elements title = article.select("strong.text-title");
//            Elements create = doc.select("div.col-md-11 span.create-at");
//            String pubDate = create.attr("data-create");
//            Elements thumb = doc.select("img.article-cover");
//            String imgThumb = thumb.attr("src");
            Elements content = doc.select("div.text-content.ybox-article");
            String cont = content.html();
//            Elements views = doc.select("div.col-md-12 div.col-md-8 span.article-vote");
//            String viewCount = null;
//            if(views.select("i") != null){
//                viewCount = views.text();
//            }

//            detail.append("<h2 style = \" color: #111111 \">" + title.text() + "</h2>");
//            detail.append("<font size=\" 2em \" style = \" color: #005500 \"><em>" + pubDate + ", " + viewCount + "</em></font>");
//            detail.append("<img src = \"" + imgThumb + "\"" + "/>");
//            detail.append("<p style = \" color: #111111 \"><b>" + "<font size=\" 4em \" >" + cont.toString() + "</font></b></p>");
            detail.append("<p>" + "<font size=\" 4em \" >" + cont + "</font></p>");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return detail.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.hideProgress();
        callback.onGetDetailTaskComplete(s);
    }
}
