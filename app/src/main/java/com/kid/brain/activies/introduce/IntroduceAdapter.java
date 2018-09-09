package com.kid.brain.activies.introduce;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class IntroduceAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	public IntroduceAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setIntroduceDataPage(List<Fragment> fragments){
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
