package com.kid.brain.activies.profile.kid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.home.CategoriesActivity_;
import com.kid.brain.activies.results.BookingActivity;
import com.kid.brain.activies.results.ResultSurveyActivity_;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.enums.EKidCate;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.managers.help.KidBusiness;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.History;
import com.kid.brain.models.Kid;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.BookingParams;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.KidResponse;
import com.kid.brain.provider.request.model.history.SearchHistoryParams;
import com.kid.brain.provider.request.model.history.TestResponse;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.adapters.HistoryAdapter;
import com.kid.brain.view.custom.MyItemDecoration;
import com.kid.brain.view.dialog.DateTimeUtils;
import com.kid.brain.view.dialog.DialogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.kid.brain.util.Constants.REQUEST_CODE_UPDATE_KID;

@EFragment(R.layout.fragment_kid_detail)
public class KidDetailFragment extends BaseFragment {

    private static final String TAG = KidDetailFragment.class.getName();

    @ViewById
    ImageView imgUserAvatar;

    @ViewById(R.id.tvUserName)
    TextView tvOld;

    @ViewById(R.id.tvFullName)
    TextView tvUserName;

    @ViewById
    TextView tvDateOfBirth;

    @ViewById
    TextView tvGender;

    @ViewById
    ImageButton imbEditProfile;

    @ViewById
    Button btnStartTest;

    @ViewById
    RecyclerView rvHistories;

    private List<History> mHistories;
    private HistoryAdapter adapter;
    private RecyclerViewSkeletonScreen skeletonScreen;

    private String mKidId;
    private boolean mIsView;
    private Kid mKid;


    public KidDetailFragment() {
    }

    public static KidDetailFragment_ newInstance(String kidId) {
        KidDetailFragment_ fragment = new KidDetailFragment_();
        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_KID_ID, kidId);
        fragment.setArguments(args);
        return fragment;
    }

    public static KidDetailFragment_ newInstance(Kid kid) {
        KidDetailFragment_ fragment = new KidDetailFragment_();
        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_KID, kid);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterInject
    void afterInject() {
        if (getArguments() != null) {
//            mKidId = getArguments().getString(Constants.KEY_KID_ID);
            Kid kid = (Kid) getArguments().getSerializable(Constants.KEY_KID);
            if (null != kid) {
                mKidId = kid.getChildrenId();
                mIsView = kid.isEnableView();
            }
        }
    }

    @AfterViews
    void afterViews() {
        initialView();
        isOnlyView(mIsView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mKidId != null) {
            ALog.i("Kid ID", mKidId);
            doFetchKidDetail(mKidId);
        }
    }

    void doFetchKidDetail(String kidId) {
        HeaderSession header = new HeaderSession();
        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<KidResponse> callUser = apiService.fetchKid(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(kidId));
        callUser.enqueue(new Callback<KidResponse>() {
            @Override
            public void onResponse(Call<KidResponse> call, Response<KidResponse> response) {
                if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                    KidResponse accountResponse = response.body();
                    if (accountResponse != null) {
                        mKid = accountResponse.getKid();
                        if (mKid != null) {
                            ALog.i(TAG, mKid.toString());
                            try {
                                long result = KidRepository.getInstance(getActivity()).saveKid(mKid);
                                ALog.i(TAG, ">>> Save or update kid >>> " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            displayAccountInfo();

                            mHistories = mKid.getHistories();
                            displayHistories();

                            dismissProgressBar();

//                            isOnlyView(mKid.isEnableView());

                        } else {
                            dismissProgressBar();
                            showErrorDialog(accountResponse.getError());
                        }
                    } else {
                        dismissProgressBar();
                    }
                } else {
                    dismissProgressBar();
                    String strError = readIn(response.errorBody().byteStream());
                    Error error = new Gson().fromJson(strError, Error.class);
                    showErrorDialog(error);
                }
            }

            @Override
            public void onFailure(Call<KidResponse> call, Throwable t) {
                dismissProgressBar();
                t.printStackTrace();
            }
        });
    }

    void doFetchHistories() {
        mHistories = KidBean.getInstance().getHistories();
        displayHistories();
    }

    @UiThread
    void displayAccountInfo() {
        if (mKid != null) {
            String strOld = mKid.getAge(getActivity());
            if (!TextUtils.isEmpty(strOld)) {
                tvOld.setText(strOld);
            }
            tvUserName.setText(mKid.getFullNameOrEmail());
            tvDateOfBirth.setText(DateTimeUtils.convertUTCToLocal(mKid.getBirthDay()));
            tvGender.setText(KidBusiness.getInstance().getGender(mKid.getGender()));
            ImageLoader.getInstance().displayImage(mKid.getPhoto(), imgUserAvatar, KidApplication.getInstance().getOptions());
        }
    }

    @Click(R.id.btnStartTest)
    void onStartTest() {
//        chooseTestOptions();
        int type = EKidCate.Normal.getName();
        goToTest(type);
    }

    @Click(R.id.imbEditProfile)
    void onEditProfile() {
        if (mKid != null) {
            Intent intentEditProfile = new Intent(getActivity(), KidCreateUpdateActivity_.class);
            intentEditProfile.putExtra(Constants.KEY_KID, mKid);
            startActivityForResult(intentEditProfile, REQUEST_CODE_UPDATE_KID);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_KID && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                mKid = (Kid) data.getExtras().getSerializable(Constants.KEY_KID);
                displayAccountInfo();
            }
        }
    }

    private void initialView() {
        imbEditProfile.setVisibility(View.GONE);
        adapter = new HistoryAdapter(getActivity());
        adapter.setOnItemClickListener(onItemClickListener);

        rvHistories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvHistories.addItemDecoration(new MyItemDecoration(getActivity()));
        rvHistories.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(rvHistories)
                .adapter(adapter)
                .load(R.layout.item_skeleton_person)
                .show();
    }

    private void hideSkeletonScreenLoading() {
        skeletonScreen.hide();
    }

    private void displayHistories() {
        hideSkeletonScreenLoading();
        if (mHistories != null && mHistories.size() > 0) {
            if (mHistories.size() > 1) {
                Collections.sort(mHistories, new Comparator<History>() {
                    @Override
                    public int compare(History o1, History o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
            }
            adapter.setData(mHistories);
        }
    }

    private void isOnlyView(boolean isOnlyView) {
        btnStartTest.setVisibility(isOnlyView ? View.GONE : View.VISIBLE);
        imbEditProfile.setVisibility(isOnlyView ? View.GONE : View.VISIBLE);
    }

    private void goToTest(int type) {
        ALog.i("", ">>> Starting to test");
        KidApplication.mKidTested = mKid;
        Intent intentStartTesting = new Intent(getActivity(), CategoriesActivity_.class);
        intentStartTesting.putExtra(Constants.KEY_KID, mKid);
        intentStartTesting.putExtra(Constants.KEY_KID_CATE, type);
        startActivity(intentStartTesting);
    }

    //TODO: Click on Item listener
    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            KidApplication.mKidTested = mKid;
            History history = (History) object;
            Intent intentResultHistory = new Intent(getActivity(), ResultSurveyActivity_.class);
            intentResultHistory.putExtra(Constants.KEY_HISTORY, history);
            startActivity(intentResultHistory);
        }

        @Override
        public <T> void onItemLongClickListener(final T object) {
            if (object instanceof History) {
                DialogUtil.createCustomConfirmDialog(getActivity(),
                        getString(R.string.app_name),
                        getString(R.string.str_history_delete_confirm_message),
                        getString(R.string.btn_delete),
                        getString(R.string.btn_cancel),
                        new DialogUtil.ConfirmDialogOnClickListener() {
                            @Override
                            public void onOKButtonOnClick() {
                                doDeleteHistory((History) object);
                            }

                            @Override
                            public void onCancelButtonOnClick() {

                            }
                        }).show();
            }

        }
    };

    void doDeleteHistory(final History history) {
        if (NetworkUtil.isConnected(getActivity())) {
            showProgressBar(getActivity(), getString(R.string.dialog_message_sending));
            HeaderSession header = new HeaderSession();

            APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
            Call<TestResponse> callUser = apiService.deleteHistory(
                    header.getContentType(),
                    header.getLanguageCode(),
                    history.getId());
            callUser.enqueue(new Callback<TestResponse>() {
                @Override
                public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                    try {
                        if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                            TestResponse accountResponse = response.body();
                            if (accountResponse.getError().getCode() == WebserviceConfig.HTTP_CODE.SUCCESS) {
                                dismissProgressBar();
                                Toast.makeText(getActivity(), accountResponse.getError().getMessage() , Toast.LENGTH_SHORT).show();
                                mHistories.remove(history);
                                adapter.notifyDataSetChanged();
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
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseTestOptions() throws NullPointerException {
        final int checkedItem = -1;
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.str_choose_option_test))
                .setSingleChoiceItems(getResources().getStringArray(R.array.testOptions), checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int type = EKidCate.Normal.getName();
                        if (which == 1) {
                            type = EKidCate.Autism.getName();
                        } else if (which == 2) {
                            type = EKidCate.CerebralPalsy.getName();
                        }

//                        if (which == 0) {
                        goToTest(type);
//                        } else if (which == 1){
//                            Toast.makeText(getActivity(), getString(R.string.str_test_option_coming_soon), Toast.LENGTH_SHORT).show();
//                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
