package com.kid.brain.activies.tutorial;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_kids)
@OptionsMenu(R.menu.home)
public class SearchActivity extends BaseAppCompatActivity {

    private static String TAG = SearchActivity.class.getName();

    @ViewById
    Toolbar toolbar;

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.title_activity_search);
        showFragment(SearchFragment_.newInstance());
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.kidContentView, fragment, fragment.getClass().getName());
        transaction.commit();
    }
}
