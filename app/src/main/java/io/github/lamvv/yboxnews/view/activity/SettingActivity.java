package io.github.lamvv.yboxnews.view.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.constant.BuildConfig;

/**
 * Created by lamvu on 10/13/2016.
 */

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    protected int typeHomeMenu;
    private Switch switchTheme;
    private TextView tvLikeApp, tvInvite, tvNeedHelp, tvMoreApp;

    SharedPreferences prefs; // declare the sharedPreference
    boolean value = false; // default value if no value was found
    String key = "key"; // use this key to retrieve the value
    String sharedPrefName = "isMySwitchChecked"; // name of your sharedPreference

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getComponent();

        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.setting));
        }
        setTypeHomeMenu(1);

        prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        value = prefs.getBoolean(key, value); // retrieve the value of your key
        switchTheme.setChecked(value);

        switchTheme.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(switchTheme.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(key, true).commit();
                    onResume();
                }else{
                    prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(key, false).commit();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    onPause();
                }
            }
        });

        tvLikeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APP_PACKAGE_NAME)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME)));
                }
            }
        });

        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setAction(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.get_app) + " " +
                        BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
//                emailIntent.setType("message/rfc822");
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
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " + resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("facebook")) {
                            // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                            // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                            // will show the <meta content ="..."> text from that page with our link in Facebook.
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " +
                                    resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
                        } else if(packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " +
                                    resources.getString(R.string.get_app) + " " +
                                    BuildConfig.PLAY_STORE_APP_URL + BuildConfig.APP_PACKAGE_NAME);
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
            }
        });

        tvNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSendMail = new Intent(Intent.ACTION_SEND);
                intentSendMail.setType("*/*");
                intentSendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{"lamvv9x@gmail.com"});
                intentSendMail.putExtra(Intent.EXTRA_SUBJECT, "Feedback to " + getResources().getString(R.string.app_name));
                intentSendMail.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(intentSendMail);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingActivity.this, getString(R.string.not_email), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvMoreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + BuildConfig.DEV_STORE_ID)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_DEV_URL + BuildConfig.DEV_STORE_ID)));
                }
            }
        });
    }

    private void getComponent(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        switchTheme = (Switch)findViewById(R.id.switchTheme);
        tvLikeApp = (TextView)findViewById(R.id.likeApp);
        tvInvite = (TextView)findViewById(R.id.invite);
        tvNeedHelp = (TextView)findViewById(R.id.needHelp);
        tvMoreApp = (TextView)findViewById(R.id.moreApp);
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
}
