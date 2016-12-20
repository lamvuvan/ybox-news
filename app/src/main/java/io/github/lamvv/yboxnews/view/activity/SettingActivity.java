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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.List;

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
    private Switch switchTheme;
    private TextView tvLikeApp, tvRateApp, tvInvite, tvSendFriend, tvNeedHelp, tvSendEmail, tvMoreApp, tvCheckApps;

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

        switchTheme.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
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

        tvInvite.setOnClickListener(onClickInvite);
        tvSendFriend.setOnClickListener(onClickInvite);

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

    private View.OnClickListener onClickInvite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Resources resources = getResources();
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setAction(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name));
            emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.get_app) + " " +
                    PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
//                emailIntent.setType("message/rfc822");
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
                        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " + resources.getString(R.string.get_app) + " " +
                                PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
                    } else if(packageName.contains("facebook")) {
                        // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                        // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                        // will show the <meta content ="..."> text from that page with our link in Facebook.
                        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " +
                                resources.getString(R.string.get_app) + " " +
                                PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
                    } else if(packageName.contains("mms")) {
                        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_name) + " " +
                                resources.getString(R.string.get_app) + " " +
                                PLAY_STORE_APP_URL + APP_PACKAGE_NAME);
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
    };

    private View.OnClickListener onClickNeedHelp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentSendMail = new Intent(Intent.ACTION_SEND);
            intentSendMail.setType("*/*");
            intentSendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIN_EMAIL});
            intentSendMail.putExtra(Intent.EXTRA_CC, new String[]{CC_EMAIL});
            intentSendMail.putExtra(Intent.EXTRA_SUBJECT, "Feedback to " + getResources().getString(R.string.app_name));
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
        switchTheme = (Switch)findViewById(R.id.switchTheme);
        tvLikeApp = (TextView)findViewById(R.id.likeApp);
        tvRateApp = (TextView)findViewById(R.id.rateApp);
        tvInvite = (TextView)findViewById(R.id.invite);
        tvSendFriend = (TextView)findViewById(R.id.sendFriend);
        tvNeedHelp = (TextView)findViewById(R.id.needHelp);
        tvSendEmail = (TextView)findViewById(R.id.sendEmail);
        tvMoreApp = (TextView)findViewById(R.id.moreApp);
        tvCheckApps = (TextView)findViewById(R.id.checkApps);
    }

}
