package io.github.lamvv.yboxnews.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.constant.Constant;
import io.github.lamvv.yboxnews.iml.GetArticlesTaskCompleteListener;
import io.github.lamvv.yboxnews.model.Article;

/**
 * Created by lamvu on 10/7/2016.
 */

public class GetArticlesTask extends AsyncTask<String, Void, List<Article>> {

    private Context mContext;
    private GetArticlesTaskCompleteListener<List<Article>> mCallback;
    private ProgressDialog dialog;

    public GetArticlesTask(Context context, GetArticlesTaskCompleteListener<List<Article>> callback) {
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
        String jsonStr = ServiceHandler.serviceHandler(params[0]);
        List<Article> list = new ArrayList<>();
        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray dataArr = jsonObj.getJSONArray(Constant.KEY_DATA);
                for (int i = 0; i < dataArr.length(); i++) {
                    JSONObject item = dataArr.getJSONObject(i);
                    String image = item.getString(Constant.KEY_IMAGE);
                    String category = item.getString(Constant.KEY_CATEGORY);
                    String title = item.getString(Constant.KEY_TITLE);
                    JSONObject objContent = item.getJSONObject(Constant.KEY_CONTENT);
                    String rawContent = objContent.getString(Constant.KEY_RAW);
                    JSONObject objTime = item.getJSONObject(Constant.KEY_TIMESTAMPS);
                    String pubDate = objTime.getString(Constant.KEY_UPDATEDAT);
                    JSONObject objLink = item.getJSONObject(Constant.KEY_LINKS);
                    String detail = objLink.getString(Constant.KEY_DETAIL);
                    list.add(new Article(title, rawContent, detail, image, category, pubDate));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
