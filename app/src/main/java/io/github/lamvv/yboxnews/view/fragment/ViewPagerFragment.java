package io.github.lamvv.yboxnews.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.lamvv.yboxnews.R;
import io.github.lamvv.yboxnews.adapter.ViewPagerAdapter;
import io.github.lamvv.yboxnews.model.TabPagerItem;
import io.github.lamvv.yboxnews.view.activity.MainActivity;

import static io.github.lamvv.yboxnews.R.id.viewPager;

@SuppressLint("NewApi")
public class ViewPagerFragment extends Fragment {

	private List<TabPagerItem> mTabs = new ArrayList<>();
	private Context mContext;
	private TabLayout mSlidingTabLayout;
	private ViewPagerAdapter pageAdapter;
	private ViewPager mViewPager;
	private LayoutInflater inflate;
	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = (MainActivity) getActivity();
		createTabPagerItem();
		inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBar = ((MainActivity) getActivity()).getSupportActionBar();
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
				R.drawable.ic_home_white_24dp));
		mTabs.add(new TabPagerItem(getString(R.string.recruitment),
				RecruitmentFragment.newInstance(getString(R.string.recruitment)),
				R.drawable.ic_supervisor_account_white_24dp));
		mTabs.add(new TabPagerItem(getString(R.string.scholarship),
				ScholarshipFragment.newInstance(getString(R.string.scholarship)),
				R.drawable.ic_school_white_24dp));
		mTabs.add(new TabPagerItem(getString(R.string.event),
				EventFragment.newInstance(getString(R.string.event)),
				R.drawable.ic_event_white_24dp));
		mTabs.add(new TabPagerItem(getString(R.string.skill),
				SkillFragment.newInstance(getString(R.string.skill)),
				R.drawable.ic_build_white_24dp));
		mTabs.add(new TabPagerItem(getString(R.string.face),
				FaceFragment.newInstance(getString(R.string.face)),
				R.drawable.ic_face_white_24dp));
		mTabs.add(new TabPagerItem(getString(R.string.competition),
				CompetitionFragment.newInstance(getString(R.string.competition)),
				R.drawable.ic_equalizer_white_24dp));
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
		mViewPager = (ViewPager) view.findViewById(viewPager);
		mViewPager.setOffscreenPageLimit(mTabs.size());
		pageAdapter = new ViewPagerAdapter(getChildFragmentManager(), mTabs);
		mViewPager.setAdapter(pageAdapter);
		mSlidingTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mSlidingTabLayout.setElevation(15);
		}
		mSlidingTabLayout.setupWithViewPager(mViewPager);
		setupTabIcons();
		mSlidingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				switch(tab.getPosition()) {
					case 0:
						mViewPager.setCurrentItem(0);
						actionBar.setTitle(getResources().getString(R.string.home));
						break;
					case 1:
						mViewPager.setCurrentItem(1);
						actionBar.setTitle(getResources().getString(R.string.recruitment));
						break;
					case 2:
						mViewPager.setCurrentItem(2);
						actionBar.setTitle(getResources().getString(R.string.scholarship));
						break;
					case 3:
						mViewPager.setCurrentItem(3);
						actionBar.setTitle(getResources().getString(R.string.event));
						break;
					case 4:
						mViewPager.setCurrentItem(4);
						actionBar.setTitle(getResources().getString(R.string.skill));
						break;
					case 5:
						mViewPager.setCurrentItem(5);
						actionBar.setTitle(getResources().getString(R.string.face));
						break;
					case 6:
						mViewPager.setCurrentItem(6);
						actionBar.setTitle(getResources().getString(R.string.competition));
						break;
					default:
						mViewPager.setCurrentItem(tab.getPosition());
						actionBar.setTitle(getResources().getString(R.string.home));
						break;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
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