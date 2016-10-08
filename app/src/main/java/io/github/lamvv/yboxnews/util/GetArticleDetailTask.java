package io.github.lamvv.yboxnews.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.iml.GetArticleDetailTaskCompleteListener;

/**
 * Created by lamvu on 10/8/2016.
 */

public class GetArticleDetailTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private GetArticleDetailTaskCompleteListener<String> mCallback;
    private ProgressDialog dialog;

    public GetArticleDetailTask(Context context, GetArticleDetailTaskCompleteListener<String> callback){
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.title_dialog));
        dialog.setMessage(mContext.getResources().getString(R.string.message_dialog));
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String detail = "";
//        StringBuilder detail = null;
        try {
            Document doc = Jsoup.connect(params[0]).timeout(10000).get();
            Elements article = doc.select("div.article-detail div");
            Elements title = article.select("strong.text-title");
            Elements create = doc.select("div.col-md-11 span.create-at");
            String pubDate = create.attr("data-create");
            Elements thumb = doc.select("img.article-cover");
            String imgThumb = thumb.attr("src");
            Elements content = doc.select("div.text-content.ybox-article");
//            doc.select("table").remove();
            String cont = content.html();
            Elements views = doc.select("div.col-md-12 div.col-md-8 span.article-vote");
            String viewCount = null;
            if(views.select("i") != null){
                viewCount = views.text();
            }

//            detail.append("<h2 style = \" color: red \">" + title.text() + "</h2>");
//            detail.append("<font size=\" 1.2em \" style = \" color: #005500 \"><em>" + pubDate + "</em></font>");
//            detail.append("<p style = \" color: #111111 \"><b>" + "<font size=\" 4em \" >" + content.text() + "</font></b></p>");
//            detail.append("<font size=\" 4em \" >"+  main.toString() + "</font>");

            detail += "<h2 style = \" color: red \">" + title.text() + "</h2>";
            detail += "<font size=\" 1.2em \" style = \" color: #005500 \"><em>" + pubDate + ", " + viewCount +  "</em></font>";
            detail += "<img src = \"" + imgThumb + "\"" + "/>";
            detail += "<p style = \" color: #111111 \"><b>" + "<font size=\" 4em \" >" + cont.toString() + "</font></b></p>";
//            detail += "<font size=\" 4em \" >"+  main.toString() + "</font>";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        mCallback.onGetDetailTaskComplete(s);
    }
}
