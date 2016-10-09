package io.github.lamvv.yboxnews.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.model.TabPagerItem;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<TabPagerItem> mTabs;
    public ViewPagerAdapter(FragmentManager fragmentManager, List<TabPagerItem> tabs) {
        super(fragmentManager);
        this.mTabs = tabs;
    }

    public View getTabView(LayoutInflater inflate, Context mContext, int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View view = inflate.inflate(R.layout.custom_tab, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mTabs.get(position).getTitle());
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mTabs.get(position).getIcon());
        return view;
    }
    
    public void setDatasource(List<TabPagerItem> datasource){
        mTabs = datasource;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mTabs.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }
}