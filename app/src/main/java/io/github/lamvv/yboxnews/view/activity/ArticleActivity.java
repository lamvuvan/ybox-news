package io.github.lamvv.yboxnews.view.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ArticlesAdapter;
import io.github.lamvv.yboxnews.iml.GetArticleDetailTaskCompleteListener;
import io.github.lamvv.yboxnews.iml.YboxAPI;
import io.github.lamvv.yboxnews.listener.RecyclerTouchListener;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.CheckConfig;
import io.github.lamvv.yboxnews.util.GetArticleDetailTask;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.VerticalLineDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lamvu on 10/12/2016.
 */

public class ArticleActivity extends AppCompatActivity implements GetArticleDetailTaskCompleteListener<String> {

    private Toolbar mToolbar;
    protected int typeHomeMenu;
    private WebView mWebView;
    private FloatingActionButton fab;
    private List<Object> articles;
    private RecyclerView mRecyclerView;
    private ArticlesAdapter adapter;
    YboxAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        articles = new ArrayList<>();
        adapter = new ArticlesAdapter(this, articles);


        //banner ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        setTypeHomeMenu(1);

        //get data send from fragments
        Bundle bundle = getIntent().getExtras();
        final Article article = (Article) bundle.getSerializable("article");

        //webView
        mWebView = (WebView) findViewById(R.id.wvArticle);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        mWebView.setBackgroundColor(Color.TRANSPARENT);


        //launch task get detail article
        String detail = article.getLinks().getDetail();
        if(detail != null)
            launchGetDetailTask(detail);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_TEXT, article.getLinks().getDetail());
                emailIntent.setType("text/plain");

                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("message/rfc822");

                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_content));
                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<>();
                for (int i = 0; i < resInfo.size(); i++) {
                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if(packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                    } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if(packageName.contains("twitter")) {
                            intent.putExtra(Intent.EXTRA_TEXT, article.getLinks().getDetail());
                        } else if(packageName.contains("facebook")) {
                            intent.putExtra(Intent.EXTRA_TEXT, article.getLinks().getDetail());
                        }
                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }
                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
            }
        });


        mRecyclerView.setHasFixedSize(true);
        if(!CheckConfig.isTablet(this)) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            float widthDpi = displayMetrics.xdpi;
            float heightDpi = displayMetrics.ydpi;
            float widthInches = widthPixels / widthDpi;
            float heightInches = heightPixels / heightDpi;
            double diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
            if (diagonalInches >= 10) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        }


        mRecyclerView.addItemDecoration(new VerticalLineDecorator(2));
        mRecyclerView.setAdapter(adapter);
        api = ServiceGenerator.createService(YboxAPI.class);
//        load(1);

        /*
		 * onItemClickListener
		 */
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Article article = (Article) articles.get(position);
                Intent intent = new Intent(ArticleActivity.this, ArticleActivity.class);
                intent.putExtra("article", article);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

		/*
		 * onLoadMore
		 */
        adapter.setLoadMoreListener(new ArticlesAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int page = articles.size()/10;
                        page += 1;
                        loadMore(page);
                    }
                });
            }
        });

    }

    public void setTypeHomeMenu(int typeHomeMenu) {
        this.typeHomeMenu = typeHomeMenu;
        ActionBar actionBar = getSupportActionBar();
        if (typeHomeMenu == 0) {
            if (actionBar != null) {

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

                if (mToolbar != null){
                    mToolbar.setNavigationIcon(R.color.transparent);
                }
            }
        } else {
            if (actionBar != null) {
                if (mToolbar != null)
                    mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
    }

    @Override
    public void onGetDetailTaskComplete(String result) {
        mWebView.loadDataWithBaseURL(
                "",
                "<style>img{display: inline;height: auto;max-width: 100%;}"
                        + " p {font-family:\"Tangerine\", \"Sans-serif\",  \"Serif\" font-size: 48px} </style>"
                        + result, "text/html", "UTF-8", "");
    }

    private void launchGetDetailTask(String url){
        GetArticleDetailTask getArticleDetailTask = new GetArticleDetailTask(this, this);
        getArticleDetailTask.execute(url);
    }

    private void load(int page){
        Call<ArticleList> call = api.getArticle(page);
        call.enqueue(new Callback<ArticleList>() {
            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
                if(response.isSuccessful()){
                    articles.addAll(response.body().articles);
                    adapter.notifyDataChanged();
                }else{
//					Log.e("lamvv"," Response Error "+String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
//				Log.e("lamvv"," Response Error "+t.getMessage());
            }
        });
    }

    private void loadMore(int page){

        //add loading progress view
        articles.add(new Article("load"));
        adapter.notifyItemInserted(articles.size()-1);

        Call<ArticleList> call = api.getArticle(page);
        call.enqueue(new Callback<ArticleList>() {
            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
                if(response.isSuccessful()){
                    //remove loading view
                    articles.remove(articles.size()-1);

                    List<Article> result = response.body().articles;
                    if(result.size()>0){
                        //add loaded data
                        articles.addAll(result);
                    }else{//result size 0 means there is no more data available at server
                        adapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
//						Toast.makeText(mContext,"No More Data Available",Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                }else{
//					Log.e("lamvv"," Load More Response Error "+String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
//				Log.e("lamvv"," Load More Response Error "+t.getMessage());
            }
        });
    }
}
