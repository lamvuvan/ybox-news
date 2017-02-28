package io.github.lamvv.yboxnews.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ArticleAdapter;
import io.github.lamvv.yboxnews.repository.network.YboxService;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.DeviceUtils;
import io.github.lamvv.yboxnews.util.DividerItemDecoration;
import io.github.lamvv.yboxnews.util.NetworkUtils;
import io.github.lamvv.yboxnews.repository.network.ServiceGenerator;
import io.github.lamvv.yboxnews.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

	@BindView(R.id.recyclerView)
	RecyclerView recyclerView;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout swipeRefreshLayout;
	@BindView(R.id.rootLayout)
	RelativeLayout rootLayout;
	@BindView(R.id.headerProgress)
	LinearLayout headerProgress;
	@BindView(R.id.fab)
	FloatingActionButton fab;

	@BindString(R.string.home)
	String home;
	@BindString(R.string.newest)
	String newest;
	@BindString(R.string.top)
	String top;
	@BindString(R.string.recruitment)
	String recruitment;
	@BindString(R.string.scholarship)
	String scholarship;
	@BindString(R.string.event)
	String event;
	@BindString(R.string.skill)
	String skill;
	@BindString(R.string.face)
	String face;
	@BindString(R.string.error_internet)
	String errorInternet;

	private List<Object> articles;
	private ArticleAdapter adapter;
	private YboxService service;
	MainActivity mainActivity;

	private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";
	private String fragmentName;

	public static MainFragment newInstance(String text) {
		MainFragment mFragment = new MainFragment();
		Bundle mBundle = new Bundle();
		mBundle.putString(TEXT_FRAGMENT, text);
		mFragment.setArguments(mBundle);
		return mFragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		articles = new ArrayList<>();
		fragmentName = getArguments().getString(TEXT_FRAGMENT);
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
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (!NetworkUtils.isConnectedInternet(getActivity())) {
			Snackbar.make(rootLayout, errorInternet, Snackbar.LENGTH_LONG).show();
		}

		headerProgress.setVisibility(View.VISIBLE);

		//onRefresh
		swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"),
				Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
		swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

		if (!DeviceUtils.isTablet(getActivity())) {
			if(DeviceUtils.isPortrait(getActivity()))
				recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			else
				recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		} else {
			double diagonalInches = DeviceUtils.getDiagonal(getActivity());
			if (diagonalInches > 9.5) {
				recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
			}
		}

		service = ServiceGenerator.createService(YboxService.class);
		load(1);

		try {
			adapter = new ArticleAdapter(rootLayout, getActivity(), articles);
			recyclerView.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//onLoadMore
		adapter.setLoadMoreListener(new ArticleAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				recyclerView.post(new Runnable() {
					@Override
					public void run() {
						int page = articles.size() / 10;
						page += 1;
						loadMore(page);
					}
				});
			}
		});

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				layoutManager.scrollToPositionWithOffset(0, 0);
			}
		});

	}

	private void load(int page){
		Call<ArticleList> call;
		if(fragmentName.equals(home))
			call = service.getArticle(page);
		else if(fragmentName.equals(newest))
			call = service.getNewestArticle(page);
		else if(fragmentName.equals(top))
			call = service.getTopVoteArticle(page);
		else if(fragmentName.equals(recruitment))
			call = service.getRecruitmentArticle(page);
		else if(fragmentName.equals(scholarship))
			call = service.getScholarshipArticle(page);
		else if(fragmentName.equals(event))
			call = service.getEventArticle(page);
		else if(fragmentName.equals(skill))
			call = service.getSkillArticle(page);
		else if(fragmentName.equals(face))
			call = service.getFaceArticle(page);
		else
			call = service.getCompetitionArticle(page);

		call.enqueue(new Callback<ArticleList>() {
			@Override
			public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
				if(response.isSuccessful()){
					headerProgress.setVisibility(View.GONE);
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

		Call<ArticleList> call;
		if(fragmentName.equals(home))
			call = service.getArticle(page);
		else if(fragmentName.equals(newest))
			call = service.getNewestArticle(page);
		else if(fragmentName.equals(top))
			call = service.getTopVoteArticle(page);
		else if(fragmentName.equals(recruitment))
			call = service.getRecruitmentArticle(page);
		else if(fragmentName.equals(scholarship))
			call = service.getScholarshipArticle(page);
		else if(fragmentName.equals(event))
			call = service.getEventArticle(page);
		else if(fragmentName.equals(skill))
			call = service.getSkillArticle(page);
		else if(fragmentName.equals(face))
			call = service.getFaceArticle(page);
		else
			call = service.getCompetitionArticle(page);

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
			swipeRefreshLayout.postDelayed(new Runnable() {
				@Override
				public void run() {
					swipeRefreshLayout.setRefreshing(false);
					load(1);
				}
			}, 1000);
		}
	};

}
