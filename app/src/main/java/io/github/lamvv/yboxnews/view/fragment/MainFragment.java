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
import android.util.Log;
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
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.repository.network.ServiceGenerator;
import io.github.lamvv.yboxnews.repository.network.YboxService;
import io.github.lamvv.yboxnews.util.DeviceUtils;
import io.github.lamvv.yboxnews.util.DividerItemDecoration;
import io.github.lamvv.yboxnews.util.NetworkUtils;
import io.github.lamvv.yboxnews.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

	private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";

	private static final String TEXT_CATEGORY = "TEXT_CATEGORY";

	private static final String TAG = "MainFragment";

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

	private List<Object> mArticles;
	private ArticleAdapter mAdapter;
	private YboxService mService;
	MainActivity mMainActivity;

	private String mFragmentName;
	private String mCategory;

	public static MainFragment newInstance(String text, String category) {
		MainFragment mFragment = new MainFragment();
		Bundle mBundle = new Bundle();
		mBundle.putString(TEXT_FRAGMENT, text);
		mBundle.putString(TEXT_CATEGORY, category);
		mFragment.setArguments(mBundle);
		return mFragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mArticles = new ArrayList<>();
		mFragmentName = getArguments().getString(TEXT_FRAGMENT);
		mCategory = getArguments().getString(TEXT_CATEGORY);
		Log.i(TAG, "onCreate: " + mCategory);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof MainActivity) {
			this.mMainActivity = (MainActivity) context;
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

		mService = ServiceGenerator.createService(YboxService.class);
		mFragmentName = mFragmentName.toLowerCase();

		load(mFragmentName, mCategory, 1);

		try {
			mAdapter = new ArticleAdapter(rootLayout, getActivity(), mArticles);
			recyclerView.setAdapter(mAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//onLoadMore
		mAdapter.setLoadMoreListener(new ArticleAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				recyclerView.post(new Runnable() {
					@Override
					public void run() {
						int page = mArticles.size() / 10;
						page += 1;
						loadMore(mFragmentName, mCategory, page);
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

	private void load(String fragmentName, String category, int page){
		Call<ArticleList> call;
		if(fragmentName.equalsIgnoreCase(home))
			call = mService.getArticle("fil", page);
		else if(fragmentName.equalsIgnoreCase(newest) || fragmentName.equalsIgnoreCase(top))
			call = mService.getTypeArticle(category, page);
		else
			call = mService.getCategoryArticle("fil", category, page);
		call.enqueue(getArticle);
	}

	Callback<ArticleList> getArticle = new Callback<ArticleList>() {
		@Override
		public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
			if(response.isSuccessful()){
				headerProgress.setVisibility(View.GONE);
				mArticles.addAll(response.body().articles);
				mAdapter.notifyDataChanged();
			}else{
				Log.e(TAG, "Response Error " + String.valueOf(response.code()));
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			Log.e(TAG, "Response Error " + t.getMessage());
		}
	};

	private void loadMore(String fragmentName, String category, int page){
		//add loading progress view
		mArticles.add(new Article("load"));
		mAdapter.notifyItemInserted(mArticles.size()-1);

		Call<ArticleList> call;
		if(fragmentName.equalsIgnoreCase(home))
			call = mService.getArticle("fil", page);
		else if(fragmentName.equalsIgnoreCase(newest) || fragmentName.equalsIgnoreCase(top))
			call = mService.getTypeArticle(category, page);
		else
			call = mService.getCategoryArticle("fil", category, page);
		call.enqueue(new Callback<ArticleList>() {
			@Override
			public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {
				if(response.isSuccessful()){
					//remove loading view
					mArticles.remove(mArticles.size()-1);

					List<Article> result = response.body().articles;
					if(result.size()>0){
						//add loaded data
						mArticles.addAll(result);
					}else{//result size 0 means there is no more data available at server
						mAdapter.setMoreDataAvailable(false);
						//telling adapter to stop calling load more as no more server data available
//						Toast.makeText(mContext,"No More Data Available",Toast.LENGTH_LONG).show();
					}
					mAdapter.notifyDataChanged();
					//should call the custom method adapter.notifyDataChanged here to get the correct loading status
				}else{
					Log.e(TAG, "Load More Response Error " + String.valueOf(response.code()));
				}
			}

			@Override
			public void onFailure(Call<ArticleList> call, Throwable t) {
				Log.e(TAG, "Load More Response Error " + t.getMessage());
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
					load(mFragmentName, mCategory, 1);
				}
			}, 1000);
		}
	};

}
