package com.kid.brain.activies.checklist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.kid.brain.R;
import com.kid.brain.activies.results.ResultSurveyActivity_;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.managers.listeners.IActivityCommunicatorListener;
import com.kid.brain.models.Account;
import com.kid.brain.models.Category;
import com.kid.brain.models.Kid;
import com.kid.brain.models.QuestionScore;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.util.Constants;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Iterator;
import java.util.List;

@EActivity(R.layout.activity_check_list)
@OptionsMenu(R.menu.home)
public class CheckListActivity extends BaseAppCompatActivity implements IActivityCommunicatorListener {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button btnContinue;

    @Extra(Constants.KEY_KID)
    Kid mKid;

//    @Extra(Constants.KEY_LEVEL)
//    Level mLevel;

    @Extra(Constants.KEY_CATE)
    Category mCategory;

    private List<Category> mCategories;
    private Iterator mIterator;
    private Account mAccount;
    private int mCount;

    private QuestionScore mQuestionScore;
    private String mQuestionIds;
    private String mRateId = "0";
    private int mScore;
    private boolean mIsCompleted;

    @AfterExtras
    void afterExtras() {

    }

    @AfterInject
    void afterInject() {
        try {
            mAccount = KidRepository.getInstance(CheckListActivity.this).getAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mKid != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCategories = KidBean.getInstance().getCategories(mKid.getAgeId());
//                        mCategories = KidRepository.getInstance(CheckListActivity.this).getCategories(mKid.getAgeId());
                        for (Category c : mCategories) {
                            if (c.getId() == mCategory.getId()) {
                                mCategories.remove(c);
                                break;
                            }
                        }
                        mIterator = mCategories.iterator();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
    }

    @AfterViews
    void afterViews() {
        if (mAccount == null) {
            this.finish();
            return;
        }

        setUpToolbarWithBackButton(toolbar, mAccount.getFullNameOrEmail());
        replaceFragment(CheckListFragment_.newInstance(mKid, mCategory));
        btnContinue.setVisibility(View.VISIBLE);
    }

    @OptionsItem(android.R.id.home)
    void home() {
        if (!this.mIsCompleted) {
            showDialogConfirmExitTest();
        }
    }

    @OptionsItem(R.id.action_logo)
    void onClickLogo() {
       super.goToHome(CheckListActivity.this);
    }

    @Click(R.id.btnContinue)
    void doContinue() {
        mCount++;
        ALog.e("mCount", String.valueOf(mCount));
        if (mCount == mCategories.size()) {
            btnContinue.setText(getString(R.string.btn_done));
            btnContinue.setBackgroundResource(R.drawable.selector_done_no_border_button);
        }

        if (mIterator.hasNext()) {
            if (TextUtils.isEmpty(mQuestionIds) && mCategory.getId() != 8) {
                showAlertDialog(getString(R.string.app_name), "Bạn cần chọn một đáp án!");
                return;
            }

            saveScoreToBean();
            mCategory = (Category) mIterator.next();
            onTransactionFragment(CheckListFragment_.newInstance(mKid, mCategory));
        } else {
            if (TextUtils.isEmpty(mQuestionIds) && mCategory.getId() != 8) {
                showAlertDialog(getString(R.string.app_name), "Bạn cần chọn một đáp án!");
                return;
            }

            DialogUtil.createCustomOkDialog(CheckListActivity.this,
                    getString(R.string.app_name),
                    getString(R.string.str_testing_completed),
                    getString(R.string.btn_ok),
                    new DialogUtil.DialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {
                            mIsCompleted = true;
                            saveScoreToBean();
                            startActivity(ResultSurveyActivity_.class);
                            CheckListActivity.this.finish();
                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.mIsCompleted) {
            showDialogConfirmExitTest();
        }
//        FragmentManager frmManager = getSupportFragmentManager();
//        int count = frmManager.getBackStackEntryCount();
//        if (count > 1) {
//            frmManager.popBackStack();
//        } else {
//            CheckListActivity.this.finish();
//        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(fragment.getTag() + mCategory.getId() + mCount);
        transaction.replace(R.id.containerCheckList, fragment, fragment.getClass().getName() + mCategory.getId() + mCount);
        transaction.commit();
    }

    public void onTransactionFragment(Fragment frmName) {
        FragmentManager frmManager = getSupportFragmentManager();
        FragmentTransaction frmTransaction = frmManager.beginTransaction();
        frmTransaction.addToBackStack(frmName.getTag() + mCategory.getId() + mCount);
        frmTransaction.setCustomAnimations(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left,
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
        frmTransaction.replace(R.id.containerCheckList, frmName, frmName.getClass().getName() + mCategory.getId() + mCount);
        frmTransaction.commit();
    }

    @Override
    public <T> void passDataToActivity(T object) {
        if (object instanceof String[]) {
            String data[] = (String[]) object;
            mRateId = data[0];
            mScore = Integer.parseInt(data[1]);
            mQuestionIds = data[2];
        }
    }

    private void saveScoreToBean() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Save to bean
                mQuestionScore = new QuestionScore();
                mQuestionScore.setLevel(mKid);
                mQuestionScore.setCategory(mCategory);
                mQuestionScore.setScore(mScore);
                mQuestionScore.setRateId(mRateId);
                mQuestionScore.setQuestionIds(mQuestionIds);
                KidBean.getInstance().addScore(mQuestionScore);

                // Save to SQLite
                try {
                    KidRepository.getInstance(CheckListActivity.this).saveLevelCateRate(
                            String.valueOf(mKid.getAgeId()),
                            String.valueOf(mCategory.getId()),
                            mRateId,
                            mScore
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mRateId = "0";
                    mScore = 0;
                    mQuestionIds = "";
                }
            }
        }).run();
    }

    private void showDialogConfirmExitTest() {
        DialogUtil.createCustomConfirmDialog(CheckListActivity.this,
                getString(R.string.app_name),
                getString(R.string.str_testing_exit_confirm),
                new DialogUtil.ConfirmDialogOnClickListener() {
                    @Override
                    public void onOKButtonOnClick() {
                        CheckListActivity.this.finish();
                    }

                    @Override
                    public void onCancelButtonOnClick() {

                    }
                }).show();
    }
}
