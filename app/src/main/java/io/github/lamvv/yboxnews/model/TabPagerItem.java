package io.github.lamvv.yboxnews.model;

import android.support.v4.app.Fragment;

public class TabPagerItem {

    private final CharSequence mTitle;
    private final Fragment mFragment;
    private final int mIcon;
    
    public TabPagerItem(CharSequence title, Fragment fragment, int mIcon) {
        this.mTitle = title;
        this.mFragment = fragment;
        this.mIcon = mIcon;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public CharSequence getTitle() {
        return mTitle;
    }
    
    public int getIcon(){
    	return mIcon;
    }
}
