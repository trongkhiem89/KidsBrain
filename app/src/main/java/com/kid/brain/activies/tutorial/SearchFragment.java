package com.kid.brain.activies.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.kid.brain.R;
import com.kid.brain.activies.results.ResultSurveyActivity_;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.models.History;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_search)
public class SearchFragment extends BaseFragment {

    private static final String TAG = SearchFragment.class.getName();

    @ViewById
    EditText edtHistoryId;

    public SearchFragment() {
    }

    public static SearchFragment_ newInstance() {
        SearchFragment_ fragment = new SearchFragment_();
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

    @Click
    void btnSearch() {
        if (NetworkUtil.isConnected(getActivity())) {

            String code = edtHistoryId.getText().toString();
            if (invalid(code)) return;

            History history = new History();
            history.setId(code);
            Intent intentResultHistory = new Intent(getActivity(), ResultSurveyActivity_.class);
            intentResultHistory.putExtra(Constants.KEY_HISTORY, history);
            intentResultHistory.putExtra(Constants.KEY_SEARCH, true);
            startActivity(intentResultHistory);

            /*showProgressBar(getActivity(), getString(R.string.dialog_message_searching));
            HeaderSession header = new HeaderSession();

            APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
            Call<TestResponse> callUser = apiService.searchHistoryCode(
                    header.getContentType(),
                    header.getLanguageCode(),
                    code);
            callUser.enqueue(new Callback<TestResponse>() {
                @Override
                public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                    try {
                        if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                            TestResponse accountResponse = response.body();
                            if (accountResponse != null && accountResponse.getError().getCode() == WebserviceConfig.HTTP_CODE.SUCCESS) {
                                dismissProgressBar();

                            } else {
                                dismissProgressBar();
                                showAlertDialog(getString(R.string.app_name), accountResponse.getError().getMessage());
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
            });*/
        }
    }

    private boolean invalid(String code) {
        if (TextUtils.isEmpty(code)) {
            DialogUtil.showErrorDialog(getActivity(), getString(R.string.error_empty_history_code));
            return true;
        }
        return false;
    }

}
