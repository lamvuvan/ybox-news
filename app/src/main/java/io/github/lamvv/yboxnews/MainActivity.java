package io.github.lamvv.yboxnews;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.iml.GetArticlesTaskCompleteListener;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.GetArticlesTask;

public class MainActivity extends AppCompatActivity implements GetArticlesTaskCompleteListener<List<Article>> {

    private List<Article> mList;
//    String link = "http://www.wn.com.vn/brands/Anh-%C4%91ep.html";
    String link = "http://ybox.vn";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageLoader(this);

        mList = new ArrayList<>();

        new GetImage().execute();

    }

    @Override
    public void onGetTaskComplete(List<Article> result) {
        for(int i = 0; i < result.size(); i++){
            mList.add(result.get(i));
        }
    }

    private void launchTask(String url){
        GetArticlesTask getArticlesTask = new GetArticlesTask(this, this);
        getArticlesTask.execute(url);
    }

    public class GetImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.e("lamvv", "on get data");
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle(getResources().getString(R.string.title_dialog));
            dialog.setMessage(getResources().getString(R.string.message_dialog));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(link).timeout(10000).get();
                Elements eles = doc.select("div.col-md-11 a.article-wrapper-link");
                for(int i = 0; i < eles.size(); i++){
                    Log.e("lamvv", "elements: " + eles.toString());
                    Element e = eles.get(i);
                    Log.e("lamvv", "element: " + e.toString());
                    String link = e.attr("href");
                    Log.e("lamvv", "link: " + link);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            Log.e("lamvv", "finish");
        }
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoader.getInstance().init(config.build());
    }
}
