package io.github.lamvv.yboxnews.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ViewPagerAdapter;
import io.github.lamvv.yboxnews.model.TabPagerItem;
import io.github.lamvv.yboxnews.view.activity.MainActivity;

@SuppressLint("NewApi")
public class ViewPagerFragment extends Fragment {
	private List<TabPagerItem> mTabs = new ArrayList<>();
	private Context mContext;
	private TabLayout mSlidingTabLayout;
	private ViewPagerAdapter pageAdapter;
	protected LayoutInflater inflate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = (MainActivity) getActivity();
		createTabPagerItem();
		inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((MainActivity) mContext).setTypeHomeMenu(0);
	}

	private void createTabPagerItem() {
		mTabs.add(new TabPagerItem(getString(R.string.home),
				MainFragment.newInstance(getString(R.string.home)),
				R.drawable.topchart));
		mTabs.add(new TabPagerItem(getString(R.string.recruitment),
				RecruitmentFragment.newInstance(getString(R.string.recruitment)),
				R.drawable.popular));
		mTabs.add(new TabPagerItem(getString(R.string.scholarship),
				ScholarshipFragment.newInstance(getString(R.string.scholarship)),
				R.drawable.playlist));
		mTabs.add(new TabPagerItem(getString(R.string.event),
				EventFragment.newInstance(getString(R.string.event)),
				R.drawable.star));
		mTabs.add(new TabPagerItem(getString(R.string.skill),
				SkillFragment.newInstance(getString(R.string.skill)),
				R.drawable.ic_more));
		mTabs.add(new TabPagerItem(getString(R.string.face),
				FaceFragment.newInstance(getString(R.string.face)),
				R.drawable.ic_more));
		mTabs.add(new TabPagerItem(getString(R.string.competition),
				CompetitionFragment.newInstance(getString(R.string.competition)),
				R.drawable.ic_more));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
		rootView.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

		mViewPager.setOffscreenPageLimit(mTabs.size());
		pageAdapter = new ViewPagerAdapter(getChildFragmentManager(), mTabs);
		mViewPager.setAdapter(pageAdapter);
		mSlidingTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mSlidingTabLayout.setElevation(15);
		}
		mSlidingTabLayout.setupWithViewPager(mViewPager);
		setupTabIcons();
	}

	private void setupTabIcons() {
		for (int i = 0; i < mSlidingTabLayout.getTabCount(); i++) {
			TabLayout.Tab tab = mSlidingTabLayout.getTabAt(i);
			if (tab != null)
				tab.setCustomView(pageAdapter.getTabView(inflate, mContext, i));
		}

		mSlidingTabLayout.getTabAt(0).getCustomView().setSelected(true);
	}
}