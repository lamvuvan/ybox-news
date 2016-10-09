package io.github.lamvv.yboxnews.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.view.fragment.ViewPagerFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    protected int typeHomeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        setTypeHomeMenu(0);
        initMainHome();


        /**
         * init facebook sdk
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    public void initMainHome(){
        Fragment mFragment;
        FragmentManager mFragmentManager = getSupportFragmentManager();
        mFragment = new ViewPagerFragment();
        if (mFragment != null){
            mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
        }
    }

    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    public void setTypeHomeMenu(int typeHomeMenu) {
        this.typeHomeMenu = typeHomeMenu;
        ActionBar actionBar = getSupportActionBar();
        if (typeHomeMenu == 0) {
            if (actionBar != null) {

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

                if (mToolbar != null){
                    mToolbar.setNavigationIcon(R.color.transparent);
                }
            }
        } else {
            if (actionBar != null) {
                if (mToolbar != null)
                    mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            }
        }
    }

    /**
     * init imageloader lib
     */
    /*public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoader.getInstance().init(config.build());
    }*/
}
