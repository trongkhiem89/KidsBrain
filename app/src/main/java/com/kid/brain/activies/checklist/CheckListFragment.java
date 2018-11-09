package com.kid.brain.activies.checklist;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidBusiness;
import com.kid.brain.managers.listeners.IActivityCommunicatorListener;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Category;
import com.kid.brain.models.Kid;
import com.kid.brain.models.Question;
import com.kid.brain.models.Rate;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.QuestionResponse;
import com.kid.brain.util.Constants;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.adapters.QuestionAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_check_list)
public class CheckListFragment extends BaseFragment {

    private static String TAG = CheckListFragment.class.getName();

    @ViewById
    ImageView imgKidLevel;

    @ViewById
    TextView tvCategory;

    @ViewById
    TextView tvParentCategory;

    @ViewById
    RecyclerView rvQuestion;

    @ViewById
    TextView empty;

    private Kid mKid;
    private Category mCategory;

    private Question mQuestionSelected;
    private List<Question> mQuestions;
    private List<Rate> mRates;
    private QuestionAdapter mAdapter;
    private RecyclerViewSkeletonScreen skeletonScreen;

    private int mScore = 0;
    private List<Integer> mQuestionIdSelected;

    private IActivityCommunicatorListener mCommunicatorListener;

    public CheckListFragment() {
        mQuestionIdSelected = new ArrayList<>();
    }

    public static CheckListFragment_ newInstance(Kid level, Category category) {
        CheckListFragment_ fragment = new CheckListFragment_();
        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_KID, level);
        args.putSerializable(Constants.KEY_CATE, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCommunicatorListener = (IActivityCommunicatorListener) context;
    }

    @AfterInject
    void afterInject() {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            mLevel = (Level) bundle.getSerializable(Constants.KEY_LEVEL);
            mKid = (Kid) bundle.getSerializable(Constants.KEY_KID);
            mCategory = (Category) bundle.getSerializable(Constants.KEY_CATE);
        }
    }

    @AfterViews
    void afterViews() {
        initialView();
        try {
            displayCategory();
//            mQuestions = KidRepository.getInstance(getActivity()).getQuestions(String.valueOf(mKid.getAgeId()), String.valueOf(mCategory.getId()));
            if (mQuestions == null || mQuestions.size() == 0) {
                doFetchQuestions();
            } else {
                displayQuestions();

                mRates = KidRepository.getInstance(getActivity()).getRates(
                        String.valueOf(mKid.getAgeId()),
                        String.valueOf(mCategory.getId())
                );
            }
        } catch (Exception e) {
            ALog.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void initialView() {
        mAdapter = new QuestionAdapter(getActivity());
        mAdapter.setOnItemClickListener(onItemClickListener);

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

    private void doFetchQuestions() {
        HeaderSession header = new HeaderSession();

        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<QuestionResponse> callUser = apiService.fetchQuestionsByLevelIdCateId(
                header.getContentType(),
                header.getLanguageCode(),
                Integer.parseInt(mKid.getAgeId()),
                mCategory.getId());
        callUser.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                try {
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        QuestionResponse accountResponse = response.body();
                        if (accountResponse != null) {
                            mQuestions = accountResponse.getData().getQuestions();
                            if (mQuestions != null) {
                                try {
                                    displayQuestions();

                                    // Save questions
                                    KidRepository.getInstance(getActivity()).saveQuestions(
                                            String.valueOf(mKid.getAgeId()),
                                            String.valueOf(mCategory.getId()),
                                            mQuestions
                                    );

                                    // Save rates
                                    mRates = accountResponse.getData().getRates();
                                    KidRepository.getInstance(getActivity()).saveRates(mRates,
                                            String.valueOf(mKid.getAgeId()),
                                            String.valueOf(mCategory.getId())
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (accountResponse.getError().getCode() == WebserviceConfig.HTTP_CODE.FAILED) {
                                showErrorDialog(accountResponse.getError());
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

    private void displayCategory() {
        if (mCategory == null || mKid == null) return;
        ImageLoader.getInstance().displayImage(mCategory.getAvatar(), imgKidLevel, KidApplication.getInstance().getOptions());
//        setCategoryIcon(imgKidLevel, mCategory.getId());
        tvCategory.setText(mCategory.getName());
        tvParentCategory.setText(mKid.getNameAndAge(getActivity()));
    }

    private void displayQuestions() throws NullPointerException{
        hideSkeletonScreenLoading();
        if (mQuestions != null && mQuestions.size() > 0) {
            mAdapter.setData(mQuestions);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                rvQuestion.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            else
                rvQuestion.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        }
    }

    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            mQuestionSelected = (Question) object;
            updateAnswerCheckList(mQuestionSelected);
        }

        @Override
        public <T> void onItemLongClickListener(T object) {

        }
    };

    private void updateAnswerCheckList(final Question questionSelected) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Question q : mQuestions) {
                    if (q.getId() == questionSelected.getId()) {
                        q.setChecked(questionSelected.isChecked());
                        if (questionSelected.isChecked()) {
                            mScore++;
                            mQuestionIdSelected.add(q.getId());
                        } else {
                            mScore--;
                            for(Integer i : mQuestionIdSelected) {
                                if (q.getId() == i) {
                                    mQuestionIdSelected.remove(i);
                                }
                            }
                        }
                        break;
                    }
                }

                Rate rate = KidBusiness.getInstance().findRateByScore(mRates, mScore);
                String data[] = {
                        String.valueOf(rate.getId()),
                        String.valueOf(mScore),
                        TextUtils.join(",", mQuestionIdSelected)
                };
                mCommunicatorListener.passDataToActivity(data);
            }
        }).run();
    }

    private void setCategoryIcon(ImageView cateIcon, int cateId){
        switch (cateId){
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
