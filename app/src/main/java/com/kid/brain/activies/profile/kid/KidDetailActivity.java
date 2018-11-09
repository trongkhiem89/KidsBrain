package com.kid.brain.activies.profile.kid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.models.Kid;
import com.kid.brain.util.Constants;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_kids)
@OptionsMenu(R.menu.home)
public class KidDetailActivity extends BaseAppCompatActivity {

    private static String TAG = KidDetailActivity.class.getName();

    @ViewById
    Toolbar toolbar;

    @Extra(Constants.KEY_KID)
    Kid mKid;

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.app_name);
        showFragment(KidDetailFragment_.newInstance(mKid));
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @OptionsItem(R.id.action_logo)
    void onClickLogo() {
        super.goToHome(KidDetailActivity.this);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.kidContentView, fragment, fragment.getClass().getName());
        transaction.commit();
    }
}
