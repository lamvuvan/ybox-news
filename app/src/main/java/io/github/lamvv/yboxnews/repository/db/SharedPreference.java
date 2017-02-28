package io.github.lamvv.yboxnews.repository.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.lamvv.yboxnews.model.Article;

/**
 * Created by lamvu on 11/1/2016.
 */

public class SharedPreference {

    private static final String PREFS_NAME = "ybox_favorite";
    private static final String FAVORITES = "favorite";

    public SharedPreference() {
        super();
    }

    public void saveFavorites(Context context, List<Article> favorites) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }

    public void addFavorite(Context context, Article article) {
        List<Article> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<>();
        favorites.add(article);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Article article) {
        List<Article> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(article);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<Article> getFavorites(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<Article> favorites;
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Article[] favoriteItems = gson.fromJson(jsonFavorites, Article[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
            return (ArrayList<Article>) favorites;
        } else {
            return null;
        }
    }
}
