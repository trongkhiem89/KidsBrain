package com.kid.brain.activies.results;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.checklist.CheckHistoryActivity_;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.managers.help.KidBusiness;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IDialogOkListener;
import com.kid.brain.models.History;
import com.kid.brain.models.QuestionScore;
import com.kid.brain.models.Rate;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.KidResponse;
import com.kid.brain.provider.request.model.ResultSavingParams;
import com.kid.brain.provider.request.model.ScoreRate;
import com.kid.brain.provider.request.model.history.CategoryVoResponse;
import com.kid.brain.provider.request.model.history.HistoryResponse;
import com.kid.brain.provider.request.model.history.RatingVoResponse;
import com.kid.brain.provider.request.model.history.TestResponse;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.DialogUtil;
import com.kid.brain.view.dialog.InputDialog;

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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_result_survey)
@OptionsMenu(R.menu.home)
public class ResultSurveyActivity extends BaseAppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    LinearLayout llRoot;

    @ViewById
    Button btnConsultant;

    LayoutInflater inflater;

    List<QuestionScore> mQuestionScore;
    List<Rate> mRates;

    @Extra(Constants.KEY_HISTORY)
    History mHistory;

    @Extra(Constants.KEY_SEARCH)
    boolean isFromSearchScreen;

    private String mKidId;
    private String mHistoryId;
    private List<Integer> mQuestionIdParams;
    private List<ScoreRate> mCoreRateIdParams;

    @AfterExtras
    void afterExtras() {

    }

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.app_name);
        inflater = LayoutInflater.from(this);

        if (mHistory == null) {

            mQuestionIdParams = new ArrayList<>();
            mCoreRateIdParams = new ArrayList<>();

            // Todo: From check list
            mQuestionScore = KidBean.getInstance().getQuestionScores();
            if (mQuestionScore != null && mQuestionScore.size() > 0) {
                for (final QuestionScore qc : mQuestionScore) {
                    try {
                        View view = inflater.inflate(R.layout.row_result, null, false);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(mHistoryId) && qc.getCategory() != null) {
                                    Intent intentProduct = new Intent(ResultSurveyActivity.this, CheckHistoryActivity_.class);
                                    intentProduct.putExtra(Constants.KEY_HISTORY_ID, mHistoryId);
                                    intentProduct.putExtra(Constants.KEY_CATE_ID, String.valueOf(qc.getCategory().getId()));
                                    startActivity(intentProduct);
                                }
                            }
                        });

                        TextView cateName = view.findViewById(R.id.cateName);
                        cateName.setText(qc.getCategory().getName());

                        mRates = KidRepository.getInstance(ResultSurveyActivity.this).getRates(
                                String.valueOf(qc.getLevel().getAgeId()),
                                String.valueOf(qc.getCategory().getId())
                        );

                        if (mRates == null || mRates.size() == 0) {
                            mRates = KidRepository.getInstance(ResultSurveyActivity.this).getRates(
                                    String.valueOf(qc.getLevel().getAgeId()),
                                    String.valueOf(qc.getCategory().getId())
                            );
                        }

                        Rate rate = KidBusiness.getInstance().findRateByScore(mRates, qc.getScore());
                        if (rate != null && !TextUtils.isEmpty(rate.getName())) {
                            TextView recommend = view.findViewById(R.id.recommend);
                            recommend.setText(rate.getName());

                            TextView score = view.findViewById(R.id.score);
                            score.setText(getString(R.string.str_score, String.valueOf(qc.getScore())));
                        }

                        llRoot.addView(view);

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
                }
            }
        } else {
            // Todo: From history
            mHistoryId = mHistory.getId();
            this.doFetchHistory(mHistory.getId());
        }

        if (isFromSearchScreen) {
            btnConsultant.setText(getString(R.string.btn_save_result));
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @OptionsItem(R.id.action_logo)
    void onClickLogo() {
        super.goToHome(ResultSurveyActivity.this);
    }

    @Click(R.id.btnConsultant)
    void doConsultant() {
        if (isFromSearchScreen) {
            try {
                doSaveSearchHistory();
            } catch (Exception e) {
                e.printStackTrace();
                showToast(e.getMessage());
            }
        } else {
            InputDialog inputDialog = new InputDialog(ResultSurveyActivity.this, new IDialogOkListener() {
                @Override
                public <T> void onOk(T object) {
                    doSendEmailResult((String) object);
                }

                @Override
                public void onCancel() {

                }
            });
            inputDialog.show();
//        EmailUtil.sendEmail2(ResultSurveyActivity.this, Constants.CONTACT_SUPPORT, "Subject", "");
        }
    }

    @Click(R.id.btnBooking)
    void doBooking() {
        startActivity(BookingActivity_.class, mHistoryId, Constants.KEY_HISTORY_ID);
    }

    void doSaveResult(ResultSavingParams params) throws Exception {
        HeaderSession header = new HeaderSession();
        APIService apiService = RetrofitConfig.getInstance(ResultSurveyActivity.this).getRetrofit().create(APIService.class);
        Call<TestResponse> callUser = apiService.saveResult(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(KidApplication.mKidTested.getChildrenId()),
                params);
        callUser.enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                try {
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        TestResponse responseResult = response.body();
                        if (responseResult != null && responseResult.getHistory() != null) {
                            mHistoryId = responseResult.getHistory().getHistoryId();
                            dismissProgressBar();
                        } else {
                            dismissProgressBar();
                        }
                    } else {
                        dismissProgressBar();
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

    void doSaveSearchHistory() throws Exception {
        if (NetworkUtil.isConnected(this)) {
            showProgressBar(getString(R.string.dialog_message_saving));

            String userId = KidPreference.getStringValue(KidPreference.KEY_USER_ID);
            String kidId = mKidId;

            if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(mKidId)) {
                dismissProgressBar();
                throw new Exception(getString(R.string.error_internal));
            }

            HeaderSession header = new HeaderSession();
            APIService apiService = RetrofitConfig.getInstance(ResultSurveyActivity.this).getRetrofit().create(APIService.class);
            Call<KidResponse> callUser = apiService.saveSearchHistory(
                    header.getContentType(),
                    header.getLanguageCode(),
                    Long.parseLong(userId),
                    Long.parseLong(kidId));
            callUser.enqueue(new Callback<KidResponse>() {
                @Override
                public void onResponse(Call<KidResponse> call, Response<KidResponse> response) {
                    try {
                        dismissProgressBar();
                        if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                            KidResponse responseResult = response.body();
                            if (responseResult != null && responseResult.getKid() != null) {
                                showAlertDialog(getString(R.string.app_name), responseResult.getError().getMessage());
                            } else if (responseResult.getError() != null) {
                                showErrorDialog(responseResult.getError());
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
                public void onFailure(Call<KidResponse> call, Throwable t) {
                    dismissProgressBar();
                    t.printStackTrace();
                }
            });
        } else {
            showToast(getString(R.string.error_network));
        }
    }

    void doFetchHistory(String historyId) {
        HeaderSession header = new HeaderSession();
        APIService apiService = RetrofitConfig.getInstance(ResultSurveyActivity.this).getRetrofit().create(APIService.class);
        Call<TestResponse> callUser = apiService.fetchHistoryDetail(
                header.getContentType(),
                header.getLanguageCode(),
                historyId);
        callUser.enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(final Call<TestResponse> call, Response<TestResponse> response) {
                try {
                    dismissProgressBar();

                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        TestResponse responseResult = response.body();
                        if (responseResult != null) {
                            HistoryResponse historyResponse = responseResult.getHistory();
                            if (historyResponse != null) {
                                mKidId = historyResponse.getKidId();
                                List<CategoryVoResponse> categoryResponses = historyResponse.getCategoryVoResponses();
                                if (categoryResponses != null) {
                                    for (final CategoryVoResponse vo : categoryResponses) {
                                        View view = inflater.inflate(R.layout.row_result, null, false);

                                        TextView cateName = view.findViewById(R.id.cateName);
                                        cateName.setText(vo.getCateName());


                                        RatingVoResponse rate = vo.getRate();
                                        if (rate != null && !TextUtils.isEmpty(rate.getName())) {
                                            TextView recommend = view.findViewById(R.id.recommend);
                                            recommend.setText(rate.getName());

                                            TextView score = view.findViewById(R.id.score);
                                            score.setText(getString(R.string.str_score, rate.getScore()));
                                        }

                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intentProduct = new Intent(ResultSurveyActivity.this, CheckHistoryActivity_.class);
                                                intentProduct.putExtra(Constants.KEY_HISTORY_ID, mHistory.getId());
                                                intentProduct.putExtra(Constants.KEY_CATE_ID, vo.getCateId());
                                                startActivity(intentProduct);
                                            }
                                        });

                                        llRoot.addView(view);

                                    }
                                } else {
                                    showErrorDialog(responseResult.getError());
                                }
                            } else if (responseResult.getError() != null) {
                                String message = responseResult.getError().getMessage();
                                if (TextUtils.isEmpty(message)) {
                                    message = getString(R.string.error_data_not_found);
                                }
                                DialogUtil.createCustomOkDialog(ResultSurveyActivity.this,
                                        getString(R.string.app_name),
                                        message,
                                        getString(R.string.btn_close),
                                        new DialogUtil.DialogOnClickListener() {
                                            @Override
                                            public void onOKButtonOnClick() {
                                                ResultSurveyActivity.this.finish();
                                            }
                                        }).show();
                            }
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

    void doSendEmailResult(String email) {
        if (NetworkUtil.isConnected(this)) {

            showProgressBar(getString(R.string.dialog_message_sending));
            HeaderSession header = new HeaderSession();
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), email);

            APIService apiService = RetrofitConfig.getInstance(ResultSurveyActivity.this).getRetrofit().create(APIService.class);
            Call<TestResponse> callUser = apiService.sendResults(
                    header.getContentType(),
                    header.getLanguageCode(),
                    mHistoryId,
                    body);
            callUser.enqueue(new Callback<TestResponse>() {
                @Override
                public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                    try {
                        if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                            TestResponse accountResponse = response.body();
                            if (accountResponse != null) {
                                dismissProgressBar();
                                showAlertDialog(getString(R.string.app_name), accountResponse.getError().getMessage());
                            } else {
                                dismissProgressBar();
                            }
                        } else {
                            dismissProgressBar();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KidBean.getInstance().resetQuestionScore();
    }
}
