package io.github.lamvv.yboxnews.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ArticlesAdapter;
import io.github.lamvv.yboxnews.iml.YboxAPI;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.VerticalLineDecorator;
import io.github.lamvv.yboxnews.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lamvu on 10/10/2016.
 */

public class ScholarshipFragment extends Fragment {

    List<Article> articles;
    RecyclerView mRecyclerView;
    ArticlesAdapter adapter;
    Context mContext;
    YboxAPI api;

    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";

    public static ScholarshipFragment newInstance(String text) {
        ScholarshipFragment mFragment = new ScholarshipFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        articles = new ArrayList<>();
        adapter = new ArticlesAdapter(getActivity(), articles);
    }


    @Override
    public void onResume() {
        super.onResume();
        Context mContext = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        adapter.setLoadMoreListener(new ArticlesAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
//						int index = articles.size() - 1;
                        int page = articles.size()/10;
                        page += 1;
                        Log.e("lamvv", "page " + page);
                        loadMore(page);
                    }
                });
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new VerticalLineDecorator(2));
        mRecyclerView.setAdapter(adapter);

        api = ServiceGenerator.createService(YboxAPI.class);
        load(1);
    }

    private void load(int page){
        Call<ArticleList> call = api.getScholarshipArticle(page);
        call.enqueue(new Callback<ArticleList>() {
            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
                if(response.isSuccessful()){
                    articles.addAll(response.body().articles);
                    adapter.notifyDataChanged();
                }else{
                    Log.e("lamvv"," Response Error "+String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
                Log.e("lamvv"," Response Error "+t.getMessage());
            }
        });
    }

    private void loadMore(int page){

        //add loading progress view
        articles.add(new Article("load"));
        adapter.notifyItemInserted(articles.size()-1);

        Call<ArticleList> call = api.getScholarshipArticle(page);
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
                        Log.e("lamvv", "size " + articles.size());
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
