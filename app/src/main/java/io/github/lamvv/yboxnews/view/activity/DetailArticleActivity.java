package io.github.lamvv.yboxnews.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import io.github.lamvv.yboxnews.util.DividerItemDecoration;
import io.github.lamvv.yboxnews.util.GetArticleDetailTask;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.SharedPreferenceUtils;
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
    @BindView(R.id.category)
    TextView tvCategory;
    @BindView(R.id.view)
    TextView tvView;
    @BindView(R.id.createdAt)
    TextView tvCreatedAt;
    @BindView(R.id.content)
    WebView webView;
    @BindView(R.id.headerProgress)
    LinearLayout headerProgress;
    @BindView(R.id.article)
    LinearLayout linearLayoutArticle;
    @BindView(R.id.related)
    LinearLayout linearLayoutRelated;
    @BindView(R.id.fabFavorite)
    FloatingActionButton fabFavorite;
    @BindView(R.id.fabShare)
    FloatingActionButton fabShare;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    private List<Object> articles;
    private RelatedArticleAdapter adapter;
    private YboxService api;

    private Article article;
    private String category;

    private SharedPreferenceUtils sharedPreference;

    private static final String TAG = "lamvv";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);
        ButterKnife.bind(this);

        articles = new ArrayList<>();
        sharedPreference = new SharedPreferenceUtils();

        //get data send from fragments
        Bundle bundle = getIntent().getExtras();
        article = (Article) bundle.getSerializable("article");
        category = article.getCategory();

        //launch task get detail article
        String detail = article.getLinks().getDetail();
        if(detail != null) {
            launchGetDetailTask(detail);
        }

        collapsingToolbarLayout.setTitle(article.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));

        dynamicToolbarColor();

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

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, article.getLinks().getDetail());
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_content)));
            }
        });

        if(checkFavoriteItem(article)){
            fabFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            fabFavorite.setTag("active");
        }else{
            fabFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
            fabFavorite.setTag("deactive");
        }

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = fabFavorite.getTag().toString();
                if(tag.equalsIgnoreCase("deactive")){
                    sharedPreference.addFavorite(DetailArticleActivity.this, article);
                    fabFavorite.setTag("active");
                    fabFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Snackbar.make(rootLayout, getResources().getString(R.string.add_favorite_message),
                            Snackbar.LENGTH_SHORT).show();
                }else{
                    sharedPreference.removeFavorite(DetailArticleActivity.this, article);
                    fabFavorite.setTag("deactive");
                    fabFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Snackbar.make(rootLayout, getResources().getString(R.string.remove_favorite_message),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        api = ServiceGenerator.createService(YboxService.class);
        load(1);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
//                    .addTestDevice("9E1B9BD30BDD0D71713E0611982A7D6C")
//                    .addTestDevice("5911C7ACA6D91588481831737229F467")
                .build();
        mAdView.loadAd(adRequest);

        //native ads
        NativeExpressAdView nativeAdView = (NativeExpressAdView) findViewById(R.id.nativeAdView);
        AdRequest request = new AdRequest.Builder()
//                    .addTestDevice("9E1B9BD30BDD0D71713E0611982A7D6C")
//                    .addTestDevice("5911C7ACA6D91588481831737229F467")
                .build();
        nativeAdView.loadAd(request);

        tvTitle.setText(article.getTitle());
        tvCategory.setText(article.getCategory() + " | ");
        tvView.setText(article.getStats().getView() + " " + getResources().getString(R.string.views) + " | ");
        tvCreatedAt.setText(article.getTimestamps().getCreatedAt());

    }

    private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher_outer);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(
                        ContextCompat.getColor(DetailArticleActivity.this, R.color.colorPrimary)));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(
                        ContextCompat.getColor(DetailArticleActivity.this, R.color.colorPrimaryDark)));
            }
        });
    }

    @Override
    public void showProgress() {
        headerProgress.setVisibility(View.VISIBLE);
        linearLayoutArticle.setVisibility(View.GONE);
        linearLayoutRelated.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        headerProgress.setVisibility(View.GONE);
        linearLayoutArticle.setVisibility(View.VISIBLE);
        linearLayoutRelated.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetDetailTaskComplete(String result) {
        fabShare.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.RollIn).playOn(fabShare);
        fabFavorite.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.RollIn).playOn(fabFavorite);

        webView.loadDataWithBaseURL(
                "",
                "<style>img {display: block; margin: auto; height: auto;max-width: 100%; }"
                        + " p {font-family:\"Tangerine\", \"Sans-serif\",  \"Serif\" font-size: 48px; text-align:justify}"
                        + " iframe {display: block; margin: auto}"
                        + " a {word-wrap: break-word}"
                        + " </style>"
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

    private boolean checkFavoriteItem(Article checkArticle) {
        boolean check = false;
        List<Article> favorites = sharedPreference.getFavorites(DetailArticleActivity.this);
        if (favorites != null) {
            for (Article article : favorites) {
                if (article.equals(checkArticle)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

}
