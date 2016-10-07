package io.github.lamvv.yboxnews.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.iml.GetArticlesTaskCompleteListener;
import io.github.lamvv.yboxnews.model.Article;

/**
 * Created by lamvu on 10/7/2016.
 */

public class GetArticlesTask extends AsyncTask<String, Void, List<Article>> {

    private Context mContext;
    private GetArticlesTaskCompleteListener<List<Article>> mCallback;
    private ProgressDialog dialog;

    public GetArticlesTask(Context context, GetArticlesTaskCompleteListener<List<Article>> callback){
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(mContext.getResources().getString(R.string.title_dialog));
        dialog.setMessage(mContext.getResources().getString(R.string.message_dialog));
    }

    @Override
    protected List<Article> doInBackground(String... params) {
        List<Article> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(params[0]).get();
            Elements elements = doc.select("div.col-md-11 a");//chọn thẻ chứa link
            for(int i = 0; i < elements.size(); i++){
                Element e = elements.get(i);//lấy các thẻ a chứa article
//                Elements img = e.select("img");
//                String title = e.attr("title");
//                String image = img.attr("src");
//                Log.e("lamvv", "title: " + title);
//                Log.e("lamvv", "image: " + image);
                String link = e.attr("href");
                Log.e("lamvv", "link: " + link);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<Article> articles) {
        super.onPostExecute(articles);
        if (dialog.isShowing()){
            dialog.dismiss();
        }
        mCallback.onGetTaskComplete(articles);
    }
}
