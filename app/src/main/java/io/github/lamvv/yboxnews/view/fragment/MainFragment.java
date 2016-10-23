package io.github.lamvv.yboxnews.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import io.github.lamvv.yboxnews.listener.RecyclerTouchListener;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.model.ArticleList;
import io.github.lamvv.yboxnews.util.CheckConfig;
import io.github.lamvv.yboxnews.util.ServiceGenerator;
import io.github.lamvv.yboxnews.util.VerticalLineDecorator;
import io.github.lamvv.yboxnews.view.activity.ArticleActivity;
import io.github.lamvv.yboxnews.view.activity.MainActivity;
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

	private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";

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
		adapter = new ArticlesAdapter(getActivity(), articles);
	}

	@Override
	public void onResume() {
		super.onResume();
		Context mContext = (MainActivity) getActivity();
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
		mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
		mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		//onRefreshLayout
		mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"),
				Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
		mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.addItemDecoration(new VerticalLineDecorator(2));

		if(!CheckConfig.isTablet(getActivity())) {
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		} else {
			DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
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

		mRecyclerView.setAdapter(adapter);

//		addNativeExpressAds();
//		setUpAndLoadNativeExpressAds();

		api = ServiceGenerator.createService(YboxAPI.class);
		load(1);

		//onItemClickListener
		mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
				mRecyclerView, new RecyclerTouchListener.ClickListener() {
			@Override
			public void onClick(View view, int position) {
				Article article = (Article) articles.get(position);
				Intent intent = new Intent(getActivity(), ArticleActivity.class);
				intent.putExtra("article", article);
				startActivity(intent);
			}

			@Override
			public void onLongClick(View view, int position) {

			}
		}));

		//onLoadMore
		adapter.setLoadMoreListener(new ArticlesAdapter.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				mRecyclerView.post(new Runnable() {
					@Override
					public void run() {
						int page = articles.size()/10;
						page += 1;
						loadMore(page);
					}
				});
			}
		});

	}

	private void load(int page){
		Call<ArticleList> call = api.getArticle(page);
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

	private void loadMore(int page){

		//add loading progress view
		articles.add(new Article("load"));
		adapter.notifyItemInserted(articles.size()-1);

		Call<ArticleList> call = api.getArticle(page);
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
		for (int i = 0; i <= articles.size(); i += ITEMS_PER_AD) {
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
				for (int i = 0; i <= articles.size(); i += Constant.ITEMS_PER_AD) {
					final NativeExpressAdView adView =
							(NativeExpressAdView) articles.get(i);
					AdSize adSize = new AdSize(
							(int) (mRecyclerView.getWidth() / density), Constant.NATIVE_EXPRESS_AD_HEIGHT);
					adView.setAdSize(adSize);
					adView.setAdUnitId(getResources().getString(R.string.native_ad_unit_id));
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
				loadNativeExpressAd(index + Constant.ITEMS_PER_AD);
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// The previous Native Express ad failed to load. Call this method again to load
				// the next ad in the items list.
				Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
						+ " load the next Native Express ad in the items list.");
				loadNativeExpressAd(index + Constant.ITEMS_PER_AD);
			}
		});

		// Load the Native Express ad.
		adView.loadAd(new AdRequest.Builder().build());
	}

}
