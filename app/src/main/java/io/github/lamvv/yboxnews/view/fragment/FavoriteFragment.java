package io.github.lamvv.yboxnews.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.FavoriteAdapter;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.DeviceUtils;
import io.github.lamvv.yboxnews.util.DividerItemDecoration;
import io.github.lamvv.yboxnews.util.NetworkUtils;
import io.github.lamvv.yboxnews.repository.db.SharedPreference;

/**
 * Created by lamvu on 11/1/2016.
 */

public class FavoriteFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;

    @BindString(R.string.error_internet)
    String errorInternet;

    private List<Article> favoriteArticles;
    private FavoriteAdapter favoriteAdapter;
    private SharedPreference sharedPreference;

    public FavoriteFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteArticles = new ArrayList<>();
        sharedPreference = new SharedPreference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!NetworkUtils.isConnectedInternet(getActivity())){
            Snackbar.make(rootLayout, errorInternet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            favoriteArticles = sharedPreference.getFavorites(getActivity());
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
            if (!favoriteArticles.isEmpty()) {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                favoriteAdapter = new FavoriteAdapter(rootLayout, getActivity(), favoriteArticles);
                recyclerView.setAdapter(favoriteAdapter);

                favoriteAdapter.notifyDataSetChanged();
            } else {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
