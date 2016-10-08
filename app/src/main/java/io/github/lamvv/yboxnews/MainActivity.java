package io.github.lamvv.yboxnews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.adapter.ArticleAdapter;
import io.github.lamvv.yboxnews.constant.Constant;
import io.github.lamvv.yboxnews.iml.GetArticlesTaskCompleteListener;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.GetArticlesTask;

public class MainActivity extends AppCompatActivity implements GetArticlesTaskCompleteListener<List<Article>> {

    private List<Article> mList;
    private ListView mListView;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageLoader(this);
        getComponent();
        mList = new ArrayList<>();

        launchTask(Constant.URL);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = mList.get(position);
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("link", article.getLink());
                startActivity(intent);
            }
        });

    }

    private void getComponent(){
        mListView = (ListView)findViewById(R.id.lvArticle);
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

    @Override
    public void onGetTaskComplete(List<Article> result) {
        for(int i = 0; i < result.size(); i++){
            mList.add(result.get(i));
        }
        mAdapter = new ArticleAdapter(this, mList);
        mListView.setAdapter(mAdapter);
    }

    private void launchTask(String url){
        GetArticlesTask getArticlesTask = new GetArticlesTask(this, this);
        getArticlesTask.execute(url);
    }
}
