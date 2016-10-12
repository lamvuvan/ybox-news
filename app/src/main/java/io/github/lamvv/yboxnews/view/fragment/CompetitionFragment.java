package io.github.lamvv.yboxnews.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ArticlesAdapter;
import io.github.lamvv.yboxnews.iml.YboxAPI;
import io.github.lamvv.yboxnews.listener.RecyclerTouchListener;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.VerticalLineDecorator;
import io.github.lamvv.yboxnews.view.activity.ArticleActivity;
import io.github.lamvv.yboxnews.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by lamvu on 10/11/2016.
 */

public class CompetitionFragment extends Fragment implements ObservableScrollViewCallbacks {

    private List<Object> articles;
//    private RecyclerView mRecyclerView;
    private ObservableRecyclerView mRecyclerView;
    private ArticlesAdapter adapter;
    private YboxAPI api;
    Context mContext;
    MainActivity mainActivity;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";

    public static CompetitionFragment newInstance(String text) {
        CompetitionFragment mFragment = new CompetitionFragment();
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
//        ActionBar actionBar = ((MainActivity) mContext).getSupportActionBar();
//        actionBar.setSubtitle("Ybox " + mContext.getResources().getString(R.string.competition));
        ((MainActivity) mContext).setTypeHomeMenu(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
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
//        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mRecyclerView = (ObservableRecyclerView)view.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        /*
         * onRefreshLayout
         */
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"),
                Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new VerticalLineDecorator(2));
        mRecyclerView.setAdapter(adapter);
        api = ServiceGenerator.createService(YboxAPI.class);
        load(1);

        /*
         * onItemClickListener
         */
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Article article = (Article) articles.get(position);
//                Bundle args = new Bundle();
//                args.putString("detail", article.getLinks().getDetail());
//                Fragment two = ArticleFragment.newInstance("Article");
//                two.setArguments(args);
//                FragmentController.replaceWithAddToBackStackAnimation(getActivity(), two, ArticleFragment.class.toString());
                Intent intent = new Intent(getActivity(), ArticleActivity.class);
                intent.putExtra("detail", article.getLinks().getDetail());
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
//						int index = articles.size() - 1;
                        int page = articles.size()/10;
                        page += 1;
                        loadMore(page);
                    }
                });
            }
        });

    }

    private void load(int page){
        Call<ArticleList> call = api.getCompetitionArticle(page);
        call.enqueue(new Callback<ArticleList>() {
            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
                if(response.isSuccessful()){
                    articles.addAll(response.body().articles);
                    adapter.notifyDataChanged();
                }else{
//                    Log.e("lamvv"," Response Error "+String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
//                Log.e("lamvv"," Response Error "+t.getMessage());
            }
        });
    }

    private void loadMore(int page){

        //add loading progress view
        articles.add(new Article("load"));
        adapter.notifyItemInserted(articles.size()-1);

        Call<ArticleList> call = api.getCompetitionArticle(page);
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

    /**
     * handle refresh
     */
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    load(1);
                }
            }, 1000);
        }
    };

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = mainActivity.getSupportActionBar();
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
