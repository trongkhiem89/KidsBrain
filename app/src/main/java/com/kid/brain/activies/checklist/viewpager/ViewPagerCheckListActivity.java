package com.kid.brain.activies.checklist.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.results.ResultSurveyActivity;
import com.kid.brain.activies.results.ResultSurveyActivity_;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.models.Account;
import com.kid.brain.models.Category;
import com.kid.brain.models.Kid;
import com.kid.brain.models.QuestionScore;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.ResultSavingParams;
import com.kid.brain.provider.request.model.ScoreRate;
import com.kid.brain.provider.request.model.history.TestResponse;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_view_pager_check_list)
@OptionsMenu(R.menu.home)
public class ViewPagerCheckListActivity extends BaseAppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button btnBack;

    @ViewById
    Button btnContinue;

    @ViewById
    ViewPager viewpager;

    @Extra(Constants.KEY_KID)
    Kid mKid;

    @Extra(Constants.KEY_CATE)
    Category mCategory;

    private List<Category> mCategories;
    private Account mAccount;

    private ViewPagerAdapter adapter;
    private List<Fragment> fragments;
    private int positionCateDHNN;

    @AfterExtras
    void afterExtras() {

    }

    @AfterInject
    void afterInject() {
        try {
            mAccount = KidRepository.getInstance(ViewPagerCheckListActivity.this).getAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mKid != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCategories = KidBean.getInstance().getCategories(mKid.getAgeId());
                        //mCategories = KidRepository.getInstance(ViewPagerCheckListActivity.this).getCategories(mKid.getAgeId());
                        for (Category c : mCategories) {
                            if (c.getId() == mCategory.getId()) {
                                mCategories.remove(c);
                                break;
                            }
                        }
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
        btnContinue.setVisibility(View.VISIBLE);

        initialFragmentList();
        setupViewPager();
    }

    @OptionsItem(android.R.id.home)
    void home() {
        showDialogConfirmExitTest();
    }

    @OptionsItem(R.id.action_logo)
    void onClickLogo() {
        super.goToHome(ViewPagerCheckListActivity.this);
    }

    @Click(R.id.btnBack)
    void doBackPrevious() {
        if (viewpager.getCurrentItem() == 0) {
            showDialogConfirmExitTest();
        } else {
            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }
    }

    @Click(R.id.btnContinue)
    void doContinue() {
        if (btnContinue.getText().equals(getString(R.string.btn_done))) {
            DialogUtil.createCustomOkDialog(ViewPagerCheckListActivity.this,
                    getString(R.string.app_name),
                    getString(R.string.str_testing_completed),
                    getString(R.string.btn_ok),
                    new DialogUtil.DialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {
                            // Check Category Dau_Hieu_Nghi_Ngai have to choice
                            /*((ViewPagerCheckListFragment_) adapter.getItem(positionCateDHNN)).selectDefaultDauHieuNghiNgai();
                            startActivity(ResultSurveyActivity_.class);
                            ViewPagerCheckListActivity.this.finish();*/
                            calculateScore();
                        }
                    }).show();

        } else {
            if (((ViewPagerCheckListFragment_) adapter.getItem(viewpager.getCurrentItem())).isNeedChooseAnswer()) {
                showAlertDialog(getString(R.string.app_name), getString(R.string.str_choose_a_option));
            } else {
                viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (viewpager.getCurrentItem() == 0) {
            showDialogConfirmExitTest();
        } else {
            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }
    }

    private void goToResult(String historyId) {
        // Check Category Dau_Hieu_Nghi_Ngai have to choice
        ((ViewPagerCheckListFragment_) adapter.getItem(positionCateDHNN)).selectDefaultDauHieuNghiNgai();
        startActivity(ResultSurveyActivity_.class, historyId, Constants.KEY_HISTORY_ID);
        ViewPagerCheckListActivity.this.finish();
    }

    /**
     * Setup ViewPager
     */
    private void initialFragmentList() {
        fragments = new ArrayList<>();
        fragments.add(ViewPagerCheckListFragment_.newInstance(mKid, mCategory));
        for (Category category : mCategories) {
            if (category.getId() != mCategory.getId())
                fragments.add(ViewPagerCheckListFragment_.newInstance(mKid, category));
            if (category.getId() == ViewPagerCheckListFragment.CATE_DAU_HIEU_NGHI_NGAI)
                positionCateDHNN = mCategories.indexOf(category) + 1;
        }

        if (mCategory.getId() == ViewPagerCheckListFragment.CATE_DAU_HIEU_NGHI_NGAI) {
            positionCateDHNN = 0;
        }

        ALog.e("positionCateDHNN", positionCateDHNN + "");
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.setFragments(fragments);

        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        //viewpager.setPageTransformer(true, new DepthPageTransformer());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Require choose an answer enable to next question
                // Ignore Dau Hieu Nghi Ngai
                if (position > 0 && ((ViewPagerCheckListFragment_) adapter.getItem(position - 1)).isNeedChooseAnswer()) {
                    showAlertDialog(getString(R.string.app_name), getString(R.string.str_choose_a_option));
                    viewpager.setCurrentItem(position - 1);
                    return;
                }

                if (position == fragments.size() - 1) {
                    btnContinue.setText(getString(R.string.btn_done));
                    btnContinue.setBackgroundResource(R.drawable.selector_done_no_border_button);
                } else {
                    viewpager.setCurrentItem(position);
                    btnContinue.setText(getString(R.string.btn_continue));
                    btnContinue.setBackgroundResource(R.drawable.selector_next_no_border_button);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showDialogConfirmExitTest() {
        DialogUtil.createCustomConfirmDialog(ViewPagerCheckListActivity.this,
                getString(R.string.app_name),
                getString(R.string.str_testing_exit_confirm),
                new DialogUtil.ConfirmDialogOnClickListener() {
                    @Override
                    public void onOKButtonOnClick() {
                        ViewPagerCheckListActivity.this.finish();
                    }

                    @Override
                    public void onCancelButtonOnClick() {

                    }
                }).show();
    }

    private void calculateScore() {
        if (NetworkUtil.isConnected(this)) {
            showProgressBar(getString(R.string.dialog_message_saving));
            List<Integer> mQuestionIdParams = new ArrayList<>();
            List<ScoreRate> mCoreRateIdParams = new ArrayList<>();

            // Todo: From check list
            List<QuestionScore> mQuestionScore = KidBean.getInstance().getQuestionScores();
            if (mQuestionScore != null && mQuestionScore.size() > 0) {
                for (final QuestionScore qc : mQuestionScore) {
                    try {
                        if (!TextUtils.isEmpty(qc.getQuestionIds())) {
                            if (qc.getQuestionIds().contains(",")) {
                                String[] dataIds = qc.getQuestionIds().split(",");
                                for (String id : dataIds) {
                                    mQuestionIdParams.add(Integer.parseInt(id));
                                }
                            } else {
                                mQuestionIdParams.add(Integer.parseInt(qc.getQuestionIds()));
                            }
                        }

                        mCoreRateIdParams.add(new ScoreRate(qc.getScore(), Integer.parseInt(qc.getRateId())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    ResultSavingParams params = new ResultSavingParams();
                    params.setQuestionIds(mQuestionIdParams);
                    params.setRatingIds(mCoreRateIdParams);

                    String json = new Gson().toJson(params);
                    ALog.e("JSON PARAMS", json);
                    doSaveResult(params);
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissProgressBar();
                }
            } else {
                dismissProgressBar();
            }
        } else {
            showToast(getString(R.string.error_network));
        }
    }

    void doSaveResult(ResultSavingParams params) throws Exception {
        HeaderSession header = new HeaderSession();
        APIService apiService = RetrofitConfig.getInstance(ViewPagerCheckListActivity.this).getRetrofit().create(APIService.class);
        Call<TestResponse> callUser = apiService.saveResult(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(KidApplication.mKidTested.getChildrenId()),
                params);
        callUser.enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                try {
                    dismissProgressBar();
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        TestResponse responseResult = response.body();
                        if (responseResult != null && responseResult.getHistory() != null) {
                            String historyId = responseResult.getHistory().getHistoryId();
                            goToResult(historyId);
                        }
                    } else {
                        String strError = readIn(response.errorBody().byteStream());
                        Error error = new Gson().fromJson(strError, Error.class);
                        showErrorDialog(error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
                dismissProgressBar();
                t.printStackTrace();
            }
        });
    }
}
