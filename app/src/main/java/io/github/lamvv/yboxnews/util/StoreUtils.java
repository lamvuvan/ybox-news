package io.github.lamvv.yboxnews.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by lamvu on 4/15/2017.
 */

public class StoreUtils {

    private static final String PLAY_STORE_APP_URL = "https://play.google.com/store/apps/details?id=";
    private static final String PLAY_STORE_DEV_URL = "https://play.google.com/store/apps/developer?id=";
    private static final String APP_PACKAGE_NAME = "io.github.lamvv.yboxnews";
    private static final String DEV_STORE_ID = "3AM+Studio";

    public static void gotoAppOnMarket(Context context){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE_NAME)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_APP_URL + APP_PACKAGE_NAME)));
        }
    }

    public static void gotoDevOnMarket(Context context){
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + DEV_STORE_ID)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_DEV_URL + DEV_STORE_ID)));
        }
    }
}
