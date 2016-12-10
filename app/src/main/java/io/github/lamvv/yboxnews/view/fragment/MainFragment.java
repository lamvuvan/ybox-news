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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ArticlesAdapter;
import io.github.lamvv.yboxnews.constant.Constant;
import io.github.lamvv.yboxnews.iml.YboxAPI;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.CheckConfig;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.VerticalLineDecorator;
import io.github.lamvv.yboxnews.view.activity.MainActivity;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.github.lamvv.yboxnews.constant.Constant.ITEMS_PER_AD;

public class MainFragment extends Fragment {

	private List<Object> articles;
	private RecyclerView mRecyclerView;
	private ArticlesAdapter adapter;
	private YboxAPI api;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	MainActivity mainActivity;
	private LinearLayout rootLayout;
	private LinearLayout headerProgress;
	private FloatingActionButton fab;

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
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
		mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
		rootLayout = (LinearLayout)view.findViewById(R.id.rootLayout);
		headerProgress = (LinearLayout) view.findViewById(R.id.headerProgress);
		fab = (FloatingActionButton)view.findViewById(R.id.fab);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (!CheckConfig.isConnectedInternet(getActivity())) {
			Snackbar.make(rootLayout, getActivity().getResources().getString(R.string.error_no_internet),
					Snackbar.LENGTH_LONG).show();
		}

		headerProgress.setVisibility(View.VISIBLE);

		//onRefresh
		mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"),
				Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
		mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.addItemDecoration(new VerticalLineDecorator(2));

		if (!CheckConfig.isTablet(getActivity())) {
			if(CheckConfig.isPortrait(getActivity()))
				mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			else
				mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		} else {
			double diagonalInches = CheckConfig.getDiagonal(getActivity());
			if (diagonalInches >= 9) {
				mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
			} else {
				mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
			}
		}

		// Update the RecyclerView item's list with menu items and Native Express ads.
//		addNativeExpressAds();
//		setUpAndLoadNativeExpressAds();

		api = ServiceGenerator.createService(YboxAPI.class);
		load(1);

		try {
			adapter = new ArticlesAdapter(rootLayout, getActivity(), articles);
			AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
			mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//onLoadMore
		adapter.setLoadMoreListener(new ArticlesAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				mRecyclerView.post(new Runnable() {
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
				LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
				layoutManager.scrollToPositionWithOffset(0, 0);
			}
		});

	}

	private void load(int page){
		Call<ArticleList> call;
		if(fragmentName.equals(getResources().getString(R.string.home)))
			call = api.getArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.newest)))
			call = api.getNewestArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.top)))
			call = api.getTopVoteArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.recruitment)))
			call = api.getRecruitmentArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.scholarship)))
			call = api.getScholarshipArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.event)))
			call = api.getEventArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.skill)))
			call = api.getSkillArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.face)))
			call = api.getFaceArticle(page);
		else
			call = api.getCompetitionArticle(page);

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
		if(fragmentName.equals(getResources().getString(R.string.home)))
			call = api.getArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.newest)))
			call = api.getNewestArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.top)))
			call = api.getTopVoteArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.recruitment)))
			call = api.getRecruitmentArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.scholarship)))
			call = api.getScholarshipArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.event)))
			call = api.getEventArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.skill)))
			call = api.getSkillArticle(page);
		else if(fragmentName.equals(getResources().getString(R.string.face)))
			call = api.getFaceArticle(page);
		else
			call = api.getCompetitionArticle(page);

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

	/**
	 * Adds Native Express ads to the items list.
	 */
	private void addNativeExpressAds() {

		// Loop through the items array and place a new Native Express ad in every ith position in
		// the items List.
		for (int i = 0; i <= articles.size(); i += Constant.ITEMS_PER_AD) {
			final NativeExpressAdView adView = new NativeExpressAdView(getActivity());
			articles.add(i, adView);
		}
	}

	/**
	 * Sets up and loads the Native Express ads.
	 */
	private void setUpAndLoadNativeExpressAds() {
		// Use a Runnable to ensure that the RecyclerView has been laid out before setting the
		// ad size for the Native Express ad. This allows us to set the Native Express ad's
		// width to match the full width of the RecyclerView.
		mRecyclerView.post(new Runnable() {
			@Override
			public void run() {
				final float density = getActivity().getResources().getDisplayMetrics().density;
				// Set the ad size and ad unit ID for each Native Express ad in the items list.
				for (int i = 0; i <= articles.size(); i += ITEMS_PER_AD) {
					final NativeExpressAdView adView =
							(NativeExpressAdView) articles.get(i);
					AdSize adSize = new AdSize(
							(int) (mRecyclerView.getWidth() / density), Constant.NATIVE_EXPRESS_AD_HEIGHT);
					adView.setAdSize(adSize);
					adView.setAdUnitId(getResources().getString(R.string.native2_ad_unit_id));
				}

				// Load the first Native Express ad in the items list.
				loadNativeExpressAd(0);
			}
		});
	}

	/**
	 * Loads the Native Express ads in the items list.
	 */
	private void loadNativeExpressAd(final int index) {

		if (index >= articles.size()) {
			return;
		}

		Object item = articles.get(index);
		if (!(item instanceof NativeExpressAdView)) {
			throw new ClassCastException("Expected item at index " + index + " to be a Native"
					+ " Express ad.");
		}

		final NativeExpressAdView adView = (NativeExpressAdView) item;

		// Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
		// to finish loading before loading the next ad in the items list.
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				// The previous Native Express ad loaded successfully, call this method again to
				// load the next ad in the items list.
				loadNativeExpressAd(index + ITEMS_PER_AD);
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// The previous Native Express ad failed to load. Call this method again to load
				// the next ad in the items list.
				Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
						+ " load the next Native Express ad in the items list.");
				loadNativeExpressAd(index + ITEMS_PER_AD);
			}
		});

		// Load the Native Express ad.
		adView.loadAd(new AdRequest.Builder().build());
	}

}
