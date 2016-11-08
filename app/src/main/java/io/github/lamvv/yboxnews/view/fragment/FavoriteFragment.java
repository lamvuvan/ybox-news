package io.github.lamvv.yboxnews.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.FavoriteAdapter;
import io.github.lamvv.yboxnews.model.Article;
import io.github.lamvv.yboxnews.util.CheckConfig;
import io.github.lamvv.yboxnews.util.SharedPreference;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by lamvu on 11/1/2016.
 */

public class FavoriteFragment extends Fragment {

    private List<Article> favoriteArticles;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private FavoriteAdapter favoriteAdapter;
    private SharedPreference sharedPreference;
    private LinearLayout rootLayout;

    public FavoriteFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteArticles = new ArrayList<>();
        sharedPreference = new SharedPreference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        rootLayout = (LinearLayout) view.findViewById(R.id.rootLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!CheckConfig.isConnectedInternet(getActivity())){
            Snackbar.make(rootLayout, getActivity().getResources().getString(R.string.error_no_internet),
                    Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            favoriteArticles = sharedPreference.getFavorites(getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            if (!favoriteArticles.isEmpty()) {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                favoriteAdapter = new FavoriteAdapter(rootLayout, getActivity(), favoriteArticles);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(favoriteAdapter);
                recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

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
