package io.github.lamvv.yboxnews.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.RelatedArticleAdapter;
import io.github.lamvv.yboxnews.iml.GetArticleDetailTaskCompleteListener;
import io.github.lamvv.yboxnews.iml.YboxService;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.DeviceUtils;
import io.github.lamvv.yboxnews.util.GetArticleDetailTask;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.VerticalLineDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lamvu on 10/12/2016.
 */

public class DetailArticleActivity extends AppCompatActivity implements GetArticleDetailTaskCompleteListener<String> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.image)
    ImageView ivImage;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.view)
    TextView tvView;
    @BindView(R.id.updatedAt)
    TextView tvUpdatedAt;
    @BindView(R.id.content)
    WebView webView;
    @BindView(R.id.headerProgress)
    LinearLayout headerProgress;
    @BindView(R.id.article)
    LinearLayout linearLayoutArticle;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    private List<Object> articles;
    private RelatedArticleAdapter adapter;
    private YboxService api;

    private Article article;
    private String category;

    boolean loadSuccess = false;

    private static final String TAG = "lamvv";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);
        ButterKnife.bind(this);

        articles = new ArrayList<>();

        //get data send from fragments
        Bundle bundle = getIntent().getExtras();
        article = (Article) bundle.getSerializable("article");
        category = article.getCategory();

        //launch task get detail article
        String detail = article.getLinks().getDetail();
        if(detail != null) {
            launchGetDetailTask(detail);
            loadSuccess = true;
        }

        collapsingToolbarLayout.setTitle(article.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

        Picasso.with(this).load(article.getImage()).into(ivImage);

        //toolbar
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //webView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new MyWebViewClient());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, article.getLinks().getDetail());
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_content)));
            }
        });

        if(loadSuccess) {
            api = ServiceGenerator.createService(YboxService.class);
            load(1);

            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new VerticalLineDecorator(2));

            if (!DeviceUtils.isTablet(this)) {
                if (DeviceUtils.isPortrait(this))
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                else
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            } else {
                double diagonalInches = DeviceUtils.getDiagonal(this);
                if (diagonalInches >= 9) {
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                }
            }

            adapter = new RelatedArticleAdapter(rootLayout, this, articles);
            recyclerView.setAdapter(adapter);

            //banner ads
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("9E1B9BD30BDD0D71713E0611982A7D6C")
                    .addTestDevice("5911C7ACA6D91588481831737229F467")
                    .build();
            mAdView.loadAd(adRequest);

            //native ads
            NativeExpressAdView nativeAdView = (NativeExpressAdView) findViewById(R.id.nativeAdView);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("9E1B9BD30BDD0D71713E0611982A7D6C")
                    .addTestDevice("5911C7ACA6D91588481831737229F467")
                    .build();
            nativeAdView.loadAd(request);

            NativeExpressAdView nativeAdView2 = (NativeExpressAdView) findViewById(R.id.nativeAdView2);
            AdRequest request2 = new AdRequest.Builder()
                    .addTestDevice("9E1B9BD30BDD0D71713E0611982A7D6C")
                    .addTestDevice("5911C7ACA6D91588481831737229F467")
                    .build();
            nativeAdView2.loadAd(request2);
        }

    }

    @Override
    public void showProgress() {
        headerProgress.setVisibility(View.VISIBLE);
        linearLayoutArticle.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        headerProgress.setVisibility(View.GONE);
        linearLayoutArticle.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetDetailTaskComplete(String result) {
        tvTitle.setText(article.getTitle());
        tvView.setText(article.getStats().getView() + " " + getResources().getString(R.string.views) + " | ");
        tvUpdatedAt.setText(article.getTimestamps().getUpdatedAt());

        webView.loadDataWithBaseURL(
                "",
                "<style>img {display: block; margin: auto; height: auto;max-width: 100%; }"
                        + " p {font-family:\"Tangerine\", \"Sans-serif\",  \"Serif\" font-size: 48px; text-align:justify}"
                        + " iframe {display: block; margin: auto} </style>"
                        + result, "text/html", "UTF-8", "");
    }

    private void launchGetDetailTask(String url){
        GetArticleDetailTask getArticleDetailTask = new GetArticleDetailTask(this, this);
        getArticleDetailTask.execute(url);
    }

    private void load(int page){
        Call<ArticleList> call;

        if(category.equalsIgnoreCase("#Tuyển Dụng"))
            call = api.getRecruitmentArticle(page);
        else if(category.equalsIgnoreCase("#Kỹ Năng"))
            call = api.getSkillArticle(page);
        else if(category.equalsIgnoreCase("#Sự Kiện"))
            call = api.getEventArticle(page);
        else if(category.equalsIgnoreCase("#Học Bổng"))
            call = api.getScholarshipArticle(page);
        else if(category.equalsIgnoreCase("#Cuộc Thi"))
            call = api.getCompetitionArticle(page);
        else if(category.equalsIgnoreCase("#Gương Mặt"))
            call = api.getFaceArticle(page);
        else
            call = api.getArticle(page);

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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(String.valueOf(request));
            return true;
        }
    }

}
