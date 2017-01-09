package io.github.lamvv.yboxnews.util;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * Created by lamvu on 10/12/2016.
 */

public class App extends Application {

    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppContext = this;

        SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
