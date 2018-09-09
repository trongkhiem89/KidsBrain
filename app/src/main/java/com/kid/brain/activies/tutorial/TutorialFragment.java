package com.kid.brain.activies.tutorial;

import android.os.Bundle;

import com.kid.brain.R;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.util.log.ALog;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_tutorial)
public class TutorialFragment extends BaseFragment {

    private static final String TAG = TutorialFragment.class.getName();

    public TutorialFragment() {
    }

    public static TutorialFragment_ newInstance() {
        TutorialFragment_ fragment = new TutorialFragment_();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {

    }

    //    @Click (R.id.btnStartTest)
    void onStartTest() {
        ALog.i("", ">>> Starting to test");
    }

}
