package io.github.lamvv.yboxnews.view.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.valuepotion.sdk.ValuePotion;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.util.CropCircleTransformation;
import io.github.lamvv.yboxnews.util.FeedbackUtils;
import io.github.lamvv.yboxnews.util.MyUtils;
import io.github.lamvv.yboxnews.util.StoreUtils;
import io.github.lamvv.yboxnews.view.fragment.FavoriteFragment;
import io.github.lamvv.yboxnews.view.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    StartAppAd startAppAd = new StartAppAd(this);
    boolean requestAdFailed = false;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View navHeader;

    private ImageView imgNavHeaderBg, imgProfile;

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler handler;

    WindowManager mWindowManager = null;
    View mNightView = null;
    private boolean mIsAddedView;

    private boolean mIsChangeTheme;

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
        setNightOrDayMode();
        setContentView(R.layout.activity_main);

        ValuePotion.init(this, getString(R.string.client_id_vp), getString(R.string.secret_key_vp));

        //admob ads
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestNewInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                requestAdFailed = true;
            }
        });
        requestNewInterstitial();

        //init startapp ads
        StartAppSDK.init(this, getResources().getString(R.string.startapp_dev_id),
                getResources().getString(R.string.startapp_app_id), false);
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC);
        startAppAd.disableSplash();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        handler = new Handler();

        setSupportActionBar(toolbar);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initNightModeSwitch();

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    protected void displayInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(requestAdFailed)
                startAppAd.onBackPressed(this);
            else
                displayInterstitial();
            super.onBackPressed();
        }
    }

    private void initNightModeSwitch() {
        MenuItem menuNightMode = navigationView.getMenu().findItem(R.id.nav_night_mode);
        SwitchCompat dayNightSwitch = (SwitchCompat) MenuItemCompat.getActionView(menuNightMode);
        setCheckedState(dayNightSwitch);
        setCheckedEvent(dayNightSwitch);
    }

    private void setCheckedState(SwitchCompat dayNightSwitch) {
        if (MyUtils.isNightMode()) {
            dayNightSwitch.setChecked(true);
        } else {
            dayNightSwitch.setChecked(false);
        }
    }

    private void setCheckedEvent(SwitchCompat dayNightSwitch) {
        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeToNight();
                    MyUtils.saveTheme(true);
                } else {
                    changeToDay();
                    MyUtils.saveTheme(false);
                }

                mIsChangeTheme = true;
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void setNightOrDayMode() {
        if (MyUtils.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

//            initNightView();
//            mNightView.setBackgroundResource(R.color.night_mask);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void changeToDay() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        mNightView.setBackgroundResource(android.R.color.transparent);
    }

    public void changeToNight() {
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        initNightView();
//        mNightView.setBackgroundResource(R.color.night_mask);
    }

    private void initNightView() {
        if (mIsAddedView) {
            return;
        }
        WindowManager.LayoutParams nightViewParam = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mNightView = new View(this);
//        mWindowManager.addView(mNightView, nightViewParam);
        mIsAddedView = true;
    }

    private void removeNightModeMask() {
        if (mIsAddedView) {
//            mWindowManager.removeViewImmediate(mNightView);
            mWindowManager = null;
            mNightView = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        removeNightModeMask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void loadNavHeader() {
        Picasso.with(this)
                .load(R.drawable.header_bg)
                .into(imgNavHeaderBg);
        Picasso.with(this)
                .load(R.drawable.ic_launcher_inner)
                .transform(new CropCircleTransformation())
                .into(imgProfile);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

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
        handler.post(mPendingRunnable);

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return MainFragment.newInstance(getResources().getString(R.string.home), "home");
            case 1:
                return MainFragment.newInstance(getResources().getString(R.string.newest), "new");
            case 2:
                return MainFragment.newInstance(getResources().getString(R.string.top), "top");
            case 3:
                return MainFragment.newInstance(getResources().getString(R.string.recruitment), "recruitment");
            case 4:
                return MainFragment.newInstance(getResources().getString(R.string.scholarship), "scholarship");
            case 5:
                return MainFragment.newInstance(getResources().getString(R.string.event), "event");
            case 6:
                return MainFragment.newInstance(getResources().getString(R.string.skill), "skill");
            case 7:
                return MainFragment.newInstance(getResources().getString(R.string.face), "face");
            case 8:
                return MainFragment.newInstance(getResources().getString(R.string.competition), "competition");
            case 9:
                return new FavoriteFragment();
            default:
                return MainFragment.newInstance(getResources().getString(R.string.home), "home");
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
                    case R.id.nav_night_mode:
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_rateus:
                        StoreUtils.gotoAppOnMarket(MainActivity.this);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_otherapp:
                        StoreUtils.gotoDevOnMarket(MainActivity.this);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_setting:
                        FeedbackUtils.sendFeedback(MainActivity.this,
                                getResources().getString(R.string.app_name),
                                getString(R.string.error_email));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_version:
                        StoreUtils.gotoAppOnMarket(MainActivity.this);
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
                if (mIsChangeTheme) {
                    mIsChangeTheme = false;
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    recreate();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ValuePotion.getInstance().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ValuePotion.getInstance().onStop(this);
    }

}
