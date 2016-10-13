package io.github.lamvv.yboxnews.view.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.constant.BuildConfig;
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
    String detail;
    Article article;

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
        article = (Article) bundle.getSerializable("article");

        //webView
        mWebView = (WebView) findViewById(R.id.wvArticle);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        mWebView.setBackgroundColor(Color.TRANSPARENT);


        //launch task get detail article
        detail = article.getLinks().getDetail();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        handleItemMenuSelected(id);

        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    private void handleItemMenuSelected(int id){
        switch (id){
            case R.id.action_setting:
                startActivity(new Intent(ArticleActivity.this, SettingActivity.class));
                break;
            case R.id.action_share:
                Resources resources = getResources();
                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.get_app) + " " +
                        BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                emailIntent.setType("text/plain");


                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("message/rfc822");

                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_app));
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
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " + resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("facebook")) {
                            // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                            // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                            // will show the <meta content ="..."> text from that page with our link in Facebook.
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " +
                                    resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " +
                                    resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                            intent.setType("message/rfc822");
                        }
                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }
                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
                break;
            case R.id.action_rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APP_PACKAGE_NAME)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME)));
                }
                break;
            case R.id.action_otherapp:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + BuildConfig.DEV_STORE_ID)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_DEV_URL + BuildConfig.DEV_STORE_ID)));
                }
                break;
        }
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
