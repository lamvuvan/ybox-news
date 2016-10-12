package io.github.lamvv.yboxnews.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {
	protected View containerView;
	protected LayoutInflater inflate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return containerView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(containerView != null){
			try {
				ViewGroup parrent = (ViewGroup) containerView.getParent();
				if(parrent!=null){
					parrent.removeAllViews();
				}
			} catch (Exception e) {
			}
		}
	}
}
