package io.github.lamvv.yboxnews.util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by lamvu on 1/9/2017.
 */

public class MyUtils {

    private static final String SHARES_YBOX_NIGHT_MODE = "ybox_night_mode";
    private static final String NIGHT_MODE = "night_mode";

    public static boolean isNightMode() {
        SharedPreferences preferences = App.getAppContext().getSharedPreferences(
                SHARES_YBOX_NIGHT_MODE, Activity.MODE_PRIVATE);
        return preferences.getBoolean(NIGHT_MODE, false);
    }

    public static void saveTheme(boolean isNight) {
        SharedPreferences preferences = App.getAppContext().getSharedPreferences(
                SHARES_YBOX_NIGHT_MODE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NIGHT_MODE, isNight);
        editor.apply();
    }
}
