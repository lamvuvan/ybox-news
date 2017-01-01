package io.github.lamvv.yboxnews.view.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import io.github.lamvv.yboxnews.R;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static io.github.lamvv.yboxnews.util.ShareUtils.APP_PACKAGE_NAME;
import static io.github.lamvv.yboxnews.util.ShareUtils.CC_EMAIL;
import static io.github.lamvv.yboxnews.util.ShareUtils.DEV_STORE_ID;
import static io.github.lamvv.yboxnews.util.ShareUtils.MAIN_EMAIL;
import static io.github.lamvv.yboxnews.util.ShareUtils.PLAY_STORE_APP_URL;
import static io.github.lamvv.yboxnews.util.ShareUtils.PLAY_STORE_DEV_URL;

/**
 * Created by lamvu on 10/13/2016.
 */

public class SettingActivity extends AppCompatActivity {

    private Toolbar toolbar;
//    private Switch switchTheme;
    private SwitchCompat switchTheme;
    private TextView tvLikeApp, tvRateApp, tvNeedHelp, tvSendEmail, tvMoreApp, tvCheckApps;

    SharedPreferences prefs; // declare the sharedPreference
    SharedPreferences.Editor editor;
    boolean value = false; // default value if no value was found
    String key = "key"; // use this key to retrieve the value
    String sharedPrefName = "isMySwitchChecked"; // name of your sharedPreference

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        OverScrollDecoratorHelper.setUpStaticOverScroll(scrollView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        getComponent();

        this.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.setting));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        value = prefs.getBoolean(key, value); // retrieve the value of your key
        switchTheme.setChecked(value);

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean checked) {
                if(switchTheme.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    editor = prefs.edit();
                    editor.clear();
                    editor.putBoolean(key, true);
                    editor.apply();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    editor = prefs.edit();
                    editor.clear();
                    editor.putBoolean(key, false);
                    editor.apply();
                }
            }
        });

        tvLikeApp.setOnClickListener(onClickRateApp);
        tvRateApp.setOnClickListener(onClickRateApp);

        tvNeedHelp.setOnClickListener(onClickNeedHelp);
        tvSendEmail.setOnClickListener(onClickNeedHelp);

        tvMoreApp.setOnClickListener(onClickMoreApp);
        tvCheckApps.setOnClickListener(onClickMoreApp);
    }

    private View.OnClickListener onClickRateApp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PACKAGE_NAME)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_APP_URL + APP_PACKAGE_NAME)));
            }
        }
    };

    private View.OnClickListener onClickNeedHelp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentSendMail = new Intent(Intent.ACTION_SEND);
            intentSendMail.setType("*/*");
            intentSendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIN_EMAIL});
            intentSendMail.putExtra(Intent.EXTRA_CC, new String[]{CC_EMAIL});
            intentSendMail.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intentSendMail.putExtra(Intent.EXTRA_TEXT, "");
            try {
                startActivity(intentSendMail);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(SettingActivity.this, getString(R.string.error_email), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener onClickMoreApp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + DEV_STORE_ID)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_DEV_URL + DEV_STORE_ID)));
            }
        }
    };

    private void getComponent(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switchTheme = (SwitchCompat) findViewById(R.id.switchTheme);
        tvLikeApp = (TextView)findViewById(R.id.likeApp);
        tvRateApp = (TextView)findViewById(R.id.rateApp);
        tvNeedHelp = (TextView)findViewById(R.id.needHelp);
        tvSendEmail = (TextView)findViewById(R.id.sendEmail);
        tvMoreApp = (TextView)findViewById(R.id.moreApp);
        tvCheckApps = (TextView)findViewById(R.id.checkApps);
    }

}
