package com.kid.brain.activies.checklist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.models.Account;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.util.Constants;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_check_list)
@OptionsMenu(R.menu.home)
public class CheckHistoryActivity extends BaseAppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button btnContinue;

    @Extra(Constants.KEY_HISTORY_ID)
    String mHistoryId;

    @Extra(Constants.KEY_CATE_ID)
    String mCategoryId;

    private Account mAccount;

    @AfterExtras
    void afterExtras() {
    }

    @AfterInject
    void afterInject() {

        try {
            mAccount = KidRepository.getInstance(CheckHistoryActivity.this).getAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, mAccount.getFullNameOrEmail());
        replaceFragment(CheckHistoryFragment_.newInstance(mHistoryId, mCategoryId));
        btnContinue.setVisibility(View.GONE);
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @OptionsItem(R.id.action_logo)
    void actionLogo() {
        super.goToHome(CheckHistoryActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CheckHistoryActivity.this.finish();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(fragment.getTag());
        transaction.replace(R.id.containerCheckList, fragment, fragment.getClass().getName());
        transaction.commit();
    }

}
