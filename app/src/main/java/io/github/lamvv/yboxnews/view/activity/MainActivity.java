package io.github.lamvv.yboxnews.view.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppAd.AdMode;
import com.startapp.android.publish.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.constant.BuildConfig;
import io.github.lamvv.yboxnews.view.fragment.ViewPagerFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    protected int typeHomeMenu;
    private boolean doubleBackToExitPressedOnce = false;
    private StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init admob ads
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));

        //init startapp ads
        StartAppSDK.init(this, getResources().getString(R.string.startapp_dev_id),
                getResources().getString(R.string.startapp_app_id), false);
        startAppAd.loadAd(AdMode.AUTOMATIC);
        startAppAd.disableSplash();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(mToolbar);
//        actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
//        }
//        setTypeHomeMenu(0);
        initMainHome();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        handleItemMenuSelected(id);

        return super.onOptionsItemSelected(item);
    }

    private void handleItemMenuSelected(int id){
        switch (id){
            case R.id.action_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.action_share:
                Resources resources = getResources();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                emailIntent.setType("text/plain");

                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("message/rfc822");

                Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_app));
                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<>();
                for (int i = 0; i < resInfo.size(); i++) {
                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if(packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                    } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if(packageName.contains("twitter")) {
                            intent.putExtra(Intent.EXTRA_TEXT, BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("facebook")) {
                            intent.putExtra(Intent.EXTRA_TEXT, BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_TEXT, BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                            intent.setType("message/rfc822");
                        }
                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }
                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
                break;
            case R.id.action_rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APP_PACKAGE_NAME)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME)));
                }
                break;
            case R.id.action_otherapp:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + BuildConfig.DEV_STORE_ID)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_DEV_URL + BuildConfig.DEV_STORE_ID)));
                }
                break;
        }
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
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
            }
        }
    }

    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed(this);
        super.onBackPressed();

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.double_back), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);*/
    }

}
