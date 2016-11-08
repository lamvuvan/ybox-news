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
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.view.fragment.FavoriteFragment;
import io.github.lamvv.yboxnews.view.fragment.MainFragment;

import static io.github.lamvv.yboxnews.constant.BuildConfig.APP_PACKAGE_NAME;
import static io.github.lamvv.yboxnews.constant.BuildConfig.DEV_STORE_ID;
import static io.github.lamvv.yboxnews.constant.BuildConfig.PLAY_STORE_APP_URL;
import static io.github.lamvv.yboxnews.constant.BuildConfig.PLAY_STORE_DEV_URL;

public class MainActivity extends AppCompatActivity {

    private StartAppAd startAppAd = new StartAppAd(this);

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View navHeader;

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler mHandler;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_NEWEST = "newest";
    private static final String TAG_TOP = "top";
    private static final String TAG_RECRUITMENT = "recruitment";
    private static final String TAG_SCHOLARSHIP = "scholarship";
    private static final String TAG_EVENT = "event";
    private static final String TAG_SKILL = "skill";
    private static final String TAG_FACE = "face";
    private static final String TAG_COMPETITION = "competition";
    private static final String TAG_FAVORITE = "favorite";
    public static String CURRENT_TAG = TAG_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //admob ads
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //init startapp ads
        StartAppSDK.init(this, getResources().getString(R.string.startapp_dev_id),
                getResources().getString(R.string.startapp_app_id), false);
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC);
        startAppAd.disableSplash();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        mHandler = new Handler();

        setSupportActionBar(toolbar);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startAppAd.onBackPressed(this);
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return MainFragment.newInstance(getResources().getString(R.string.home));
            case 1:
                return MainFragment.newInstance(getResources().getString(R.string.newest));
            case 2:
                return MainFragment.newInstance(getResources().getString(R.string.top));
            case 3:
                //Blog corporation-cats
                return MainFragment.newInstance(getResources().getString(R.string.recruitment));
            case 4:
                return MainFragment.newInstance(getResources().getString(R.string.scholarship));
            case 5:
                return MainFragment.newInstance(getResources().getString(R.string.event));
            case 6:
                return MainFragment.newInstance(getResources().getString(R.string.skill));
            case 7:
                return MainFragment.newInstance(getResources().getString(R.string.face));
            case 8:
                return MainFragment.newInstance(getResources().getString(R.string.competition));
            case 9:
                return new FavoriteFragment();
            default:
                return MainFragment.newInstance(getResources().getString(R.string.home));
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_newest:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_NEWEST;
                        break;
                    case R.id.nav_top:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_TOP;
                        break;
                    case R.id.nav_recruitment:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_RECRUITMENT;
                        break;
                    case R.id.nav_scholarship:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SCHOLARSHIP;
                        break;
                    case R.id.nav_event:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_EVENT;
                        break;
                    case R.id.nav_skill:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_SKILL;
                        break;
                    case R.id.nav_face:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_FACE;
                        break;
                    case R.id.nav_competition:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_COMPETITION;
                        break;
                    case R.id.nav_favorite:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_FAVORITE;
                        break;
//                    case R.id.nav_shareapp:
//                        shareApp();
//                        drawer.closeDrawers();
//                        return true;
                    case R.id.nav_rateus:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE_NAME)));
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_APP_URL + APP_PACKAGE_NAME)));
                        }
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_otherapp:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + DEV_STORE_ID)));
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_DEV_URL + DEV_STORE_ID)));
                        }
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void shareApp(){
        Resources resources = getResources();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
        emailIntent.setType("text/plain");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("message/rfc822");

        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.shareapp));
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
                    intent.putExtra(Intent.EXTRA_TEXT, PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
                } else if(packageName.contains("facebook")) {
                    intent.putExtra(Intent.EXTRA_TEXT, PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
                } else if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
                } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.get_app) + " " +
                            PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

}
