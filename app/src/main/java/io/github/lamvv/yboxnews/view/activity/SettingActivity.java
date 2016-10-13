package io.github.lamvv.yboxnews.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rey.material.widget.Switch;

import io.github.lamvv.yboxnews.R;

/**
 * Created by lamvu on 10/13/2016.
 */

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    protected int typeHomeMenu;
    private Switch switchTheme;

    SharedPreferences prefs; // declare the sharedPreference
    boolean value = false; // default value if no value was found
    String key = "key"; // use this key to retrieve the value
    String sharedPrefName = "isMySwitchChecked"; // name of your sharedPreference

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        switchTheme = (Switch)findViewById(R.id.switchTheme);
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
