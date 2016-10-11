package io.github.lamvv.yboxnews.controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.github.lamvv.yboxnews.R;

public class FragmentController {
	//https://finfo-api.vndirect.com.vn/industries

	public static void replaceDontAddToBackStack(Context mContext, Fragment mFragment) {
		FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
		mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
	}

	public static void replaceWithAddToBackStack(Context mContext, Fragment mFragment, String nameClass) {
		FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
		mFragmentManager.beginTransaction().replace(R.id.container, mFragment, nameClass).addToBackStack(nameClass)
				.commit();
	}

	public static void replaceWithAddToBackStackAnimation(Context mContext, Fragment mFragment, String nameClass) {
		FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit,
				R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
		ft.replace(R.id.container, mFragment, nameClass);
		ft.addToBackStack(nameClass);
		ft.commit();

	}

	public static void replaceWithPopAllBackStack(Context mContext, Fragment mFragment) {
		((FragmentActivity) mContext).getSupportFragmentManager().popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment)
				.commit();
	}
}
