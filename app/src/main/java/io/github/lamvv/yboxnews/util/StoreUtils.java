package io.github.lamvv.yboxnews.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import static io.github.lamvv.yboxnews.common.Constants.APP_PACKAGE_NAME;
import static io.github.lamvv.yboxnews.common.Constants.DEV_STORE_ID;
import static io.github.lamvv.yboxnews.common.Constants.PLAY_STORE_APP_URL;
import static io.github.lamvv.yboxnews.common.Constants.PLAY_STORE_DEV_URL;

/**
 * Created by lamvu on 4/15/2017.
 */

public class StoreUtils {

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
