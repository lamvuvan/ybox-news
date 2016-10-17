package io.github.lamvv.yboxnews.view.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.iml.GetArticleDetailTaskCompleteListener;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.GetArticleDetailTask;

/**
 * Created by lamvu on 10/12/2016.
 */

public class ArticleActivity extends AppCompatActivity implements GetArticleDetailTaskCompleteListener<String>,
        ObservableScrollViewCallbacks {

    private Toolbar mToolbar;
    protected int typeHomeMenu;
    private WebView mWebView;
    private ShareButton btnShareFacebook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        btnShareFacebook = (ShareButton)findViewById(R.id.btnShareFacebook);


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
        Article article = (Article) bundle.getSerializable("article");

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


        //hiding action bar when scroll
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setContentTitle(article.getTitle())
                .setImageUrl(Uri.parse(article.getImage()))
                .setContentUrl(Uri.parse(article.getLinks().getDetail()))
                .build();
        btnShareFacebook.setShareContent(shareLinkContent);

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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }
}
