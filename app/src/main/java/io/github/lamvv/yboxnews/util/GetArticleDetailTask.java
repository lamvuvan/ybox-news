package io.github.lamvv.yboxnews.util;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.lamvv.yboxnews.interfaces.GetArticleDetailTaskCompleteListener;

/**
 * Created by lamvu on 10/8/2016.
 */

public class GetArticleDetailTask extends AsyncTask<String, Void, String> {

    private Context context;
    private GetArticleDetailTaskCompleteListener<String> callback;

    private static final String TAG = "lamvv";

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
            Elements content = doc.select("div.text-content.ybox-article");
            String cont = content.html();
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
