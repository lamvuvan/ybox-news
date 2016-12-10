package io.github.lamvv.yboxnews.view.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.RelatedArticleAdapter;
import io.github.lamvv.yboxnews.iml.GetArticleDetailTaskCompleteListener;
import io.github.lamvv.yboxnews.iml.YboxAPI;
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
 * Created by lamvu on 12/9/2016.
 */

public class ArticleFragment extends Fragment implements GetArticleDetailTaskCompleteListener<String> {

    private Toolbar mToolbar;
    private WebView mWebView;
    private FloatingActionButton fab;

    private List<Object> articles;
    private RecyclerView mRecyclerView;
    private RelatedArticleAdapter adapter;
    private YboxAPI api;
    private Article article;
    private String category;

    private RelativeLayout rootLayout;
    private LinearLayout headerProgress;

    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";
    private static final String ARTICLE = "article";

    public static ArticleFragment newInstance(String text, Article article){
        ArticleFragment mFragment = new ArticleFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mBundle.putSerializable(ARTICLE, article);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        articles = new ArrayList<>();
        article = (Article) getArguments().getSerializable(ARTICLE);
        category = getArguments().getString(TEXT_FRAGMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        rootLayout = (RelativeLayout)view.findViewById(R.id.rootLayout);
        headerProgress = (LinearLayout)view.findViewById(R.id.headerProgress);
        //webView
        mWebView = (WebView) view.findViewById(R.id.wvArticle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setWebViewClient(new ArticleFragment.MyWebViewClient());

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

                PackageManager pm = getActivity().getPackageManager();
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
        mRecyclerView.addItemDecoration(new VerticalLineDecorator(2));

        if(!CheckConfig.isTablet(getActivity())) {
            if(CheckConfig.isPortrait(getActivity()))
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            else
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            float widthDpi = displayMetrics.xdpi;
            float heightDpi = displayMetrics.ydpi;
            float widthInches = widthPixels / widthDpi;
            float heightInches = heightPixels / heightDpi;
            double diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
            if (diagonalInches >= 9) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            }
        }

        adapter = new RelatedArticleAdapter(rootLayout, getActivity(), articles);
        mRecyclerView.setAdapter(adapter);

        api = ServiceGenerator.createService(YboxAPI.class);
        load(1);
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
        GetArticleDetailTask getArticleDetailTask = new GetArticleDetailTask(getActivity(), this);
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
            headerProgress.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            headerProgress.setVisibility(View.VISIBLE);
            view.loadUrl(String.valueOf(request));
            return true;
        }
    }
}
