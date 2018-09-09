package com.kid.brain.activies.checklist;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.enums.EQuestion;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Question;
import com.kid.brain.models.SubCategory;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.QuestionResponse;
import com.kid.brain.util.Constants;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.adapters.QuestionAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_check_list)
public class CheckHistoryFragment extends BaseFragment {

    private static String TAG = CheckHistoryFragment.class.getName();

    @ViewById
    ImageView imgKidLevel;

    @ViewById
    TextView tvCategory;

    @ViewById
    TextView tvParentCategory;

    @ViewById
    RecyclerView rvQuestion;

    private String mHistoryId;
    private String mCategoryId;

    private List<Question> mQuestions;
    private QuestionAdapter mAdapter;
    private RecyclerViewSkeletonScreen skeletonScreen;


    public CheckHistoryFragment() {
    }

    public static CheckHistoryFragment_ newInstance(String historyId, String categoryId) {
        CheckHistoryFragment_ fragment = new CheckHistoryFragment_();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_HISTORY_ID, historyId);
        args.putString(Constants.KEY_CATE_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterInject
    void afterInject() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mHistoryId = bundle.getString(Constants.KEY_HISTORY_ID);
            mCategoryId = bundle.getString(Constants.KEY_CATE_ID);
            ALog.e("mHistoryId", mHistoryId);
            ALog.e("mCategoryId", mCategoryId);
        }
    }

    @AfterViews
    void afterViews() {
        initialView();
        try {
            doFetchHistoryQuestions();
        } catch (Exception e) {
            ALog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void initialView() {
        mAdapter = new QuestionAdapter(getActivity());
        mAdapter.setOnItemClickListener(onItemClickListener);
        mAdapter.setEQuestion(EQuestion.HISTORY);

        rvQuestion.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvQuestion.setAdapter(mAdapter);

        skeletonScreen = Skeleton.bind(rvQuestion)
                .adapter(mAdapter)
                .load(R.layout.item_skeleton_news)
                .show();
    }

    private void hideSkeletonScreenLoading() {
        skeletonScreen.hide();
    }

    private void doFetchHistoryQuestions() {
        HeaderSession header = new HeaderSession();

        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<QuestionResponse> callUser = apiService.fetchQuestionHistoryDetail(
                header.getContentType(),
                header.getLanguageCode(),
                mHistoryId,
                Long.parseLong(mCategoryId));
        callUser.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                try {
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        QuestionResponse accountResponse = response.body();
                        if (accountResponse != null) {
                            SubCategory subCategory = accountResponse.getData();
                            if (subCategory != null) {
                                mQuestions = subCategory.getQuestions();
                                if (mQuestions != null) {
                                    displayCategory(Integer.parseInt(mCategoryId),
                                            subCategory.getCateName(),
                                            subCategory.getLevelName());
                                    displayQuestions();
                                } else if (accountResponse.getError().getCode() == WebserviceConfig.HTTP_CODE.FAILED) {
                                    showErrorDialog(accountResponse.getError());
                                } else {
                                    showAlertDialog(getString(R.string.app_name), getString(R.string.error_data_not_found));
                                }
                            } else {
                                showAlertDialog(getString(R.string.app_name), getString(R.string.error_data_not_found));
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
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void displayCategory(int cateId, String cateName, String levelName) {
        setCategoryIcon(imgKidLevel, cateId);
        tvCategory.setText(cateName);

        if (KidApplication.mKidTested != null )
            tvParentCategory.setText(KidApplication.mKidTested.getNameAndAge(getActivity()));
        else
            tvParentCategory.setText(levelName);
    }

    private void displayQuestions() {
        hideSkeletonScreenLoading();
        if (mQuestions != null && mQuestions.size() > 0) {
            mAdapter.setData(mQuestions);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                rvQuestion.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            else
                rvQuestion.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        }
    }

    //TODO: Click on Item listener
    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            ALog.d("Question:", object.toString());
        }

        @Override
        public <T> void onItemLongClickListener(T object) {

        }
    };

    private void setCategoryIcon(ImageView cateIcon, int cateId) {
        switch (cateId) {
            case 1: // Vận động
                cateIcon.setImageResource(R.drawable.icon_van_dong);
                break;
            case 4: // Nhận thức
                cateIcon.setImageResource(R.drawable.icon_nhan_thuc);
                break;
            case 5: // Ngôn ngữ
                cateIcon.setImageResource(R.drawable.icon_ngon_ngu);
                break;
            case 7: // Cảm xúc / Tình cảm xã hội
                cateIcon.setImageResource(R.drawable.icon_cam_xuc);
                break;
            case 8: // Dấu hiệu nghi ngại
                cateIcon.setImageResource(R.drawable.icon_dau_hieu_nghi_ngai);
                break;
            default:
                cateIcon.setImageResource(R.drawable.icon_van_dong);
                break;


        }
    }
}
