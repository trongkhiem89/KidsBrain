package com.kid.brain.activies.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.checklist.CheckListActivity_;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Category;
import com.kid.brain.models.Kid;
import com.kid.brain.models.Level;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.CategoryResponse;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.util.Constants;
import com.kid.brain.view.adapters.CategoryAdapter;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_categories)
public class CategoriesFragment extends BaseFragment {

    @ViewById
    TextView tvLevelName;

    @ViewById
    RecyclerView rvCategories;

    private RecyclerViewSkeletonScreen skeletonScreen;
    private List<Category> mCategories;
    private CategoryAdapter adapter;
    private Kid mKid;
    private int mKidCate;
//    private Level mLevel;
    private Category mCategory;

    public CategoriesFragment() {
    }

//    public static CategoriesFragment_ newInstance(Level level) {
//        CategoriesFragment_ fragment = new CategoriesFragment_();
//        Bundle args = new Bundle();
//        args.putSerializable(Constants.KEY_LEVEL, level);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static CategoriesFragment_ newInstance(Kid kid, int kidCate) {
        CategoriesFragment_ fragment = new CategoriesFragment_();
        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_KID, kid);
        args.putSerializable(Constants.KEY_KID_CATE, kidCate);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterInject
    void afterInject() {
        if (getArguments() != null) {
//            mLevel = (Level) getArguments().getSerializable(Constants.KEY_LEVEL);
            mKid = (Kid) getArguments().getSerializable(Constants.KEY_KID);
            mKidCate = getArguments().getInt(Constants.KEY_KID_CATE);
//            ALog.i("Level", mLevel != null ? mLevel.toString() : "");
        }
    }

    @AfterViews
    void afterViews() {
        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.app_name));

        try {
            initialView();
            displayLevelInfo();
//            mCategories = KidRepository.getInstance(getActivity()).getCategories(mKid.getAgeId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCategories == null || mCategories.size() == 0)
                doFetchCategories();
            else
                displayCategories();
        }
    }

    private void initialView() {
        adapter = new CategoryAdapter(getActivity());
        adapter.setOnItemClickListener(onItemClickListener);

        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(rvCategories)
                .adapter(adapter)
                .load(R.layout.item_skeleton_person)
                .show();
    }

    private void hideSkeletonScreenLoading() {
        skeletonScreen.hide();
    }

    private void doFetchCategories() {
        if (TextUtils.isEmpty(mKid.getAgeId())) {
            DialogUtil.createCustomOkDialog(getActivity(),
                    getString(R.string.app_name),
                    getString(R.string.error_data_not_found),
                    getString(R.string.btn_close),
                    new DialogUtil.DialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {
                            getActivity().finish();
                        }
                    }).show();
            return;
        }

        showProgressBar(getActivity());
        HeaderSession header = new HeaderSession();

        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<CategoryResponse> callUser = apiService.fetchCategoriesByLevel(
                header.getContentType(),
                header.getLanguageCode(),
                mKidCate,
                Integer.parseInt(mKid.getAgeId()));
        callUser.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                try {
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        CategoryResponse accountResponse = response.body();
                        if (accountResponse != null) {
                            dismissProgressBar();
                            Level level = accountResponse.getLevel();
                            if (level != null) {
                                mCategories = level.getCategories();
                                if (mCategories != null) {
                                    if (mCategories.size() > 0) {
                                        displayCategories();
                                        try {
                                            KidBean.getInstance().saveCategoriesByLevel(mKid.getAgeId(), mCategories);
//                                            KidRepository.getInstance(getActivity()).saveCategories(mKid.getAgeId(), mCategories);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        DialogUtil.createCustomOkDialog(getActivity(),
                                                getString(R.string.app_name),
                                                getString(R.string.error_data_not_found),
                                                getString(R.string.btn_close),
                                                new DialogUtil.DialogOnClickListener() {
                                                    @Override
                                                    public void onOKButtonOnClick() {
                                                        getActivity().finish();
                                                    }
                                                }).show();
                                    }
                                } else {
                                    showErrorDialog(accountResponse.getError());
                                }
                            } else {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                dismissProgressBar();
                t.printStackTrace();
            }
        });

    }

    private void displayLevelInfo() {
        if (mKid != null) {
            tvLevelName.setText(mKid.getNameAndAge(getActivity()));
        }
    }

    private void displayCategories() {
        hideSkeletonScreenLoading();
        if (mCategories != null && mCategories.size() > 0) {
            adapter.setData(mCategories);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                rvCategories.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            else
                rvCategories.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
    }

    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            mCategory = (Category) object;
            Intent intentProduct = new Intent(getActivity(), CheckListActivity_.class);
            intentProduct.putExtra(Constants.KEY_KID, mKid);
            intentProduct.putExtra(Constants.KEY_CATE, mCategory);
            startActivity(intentProduct);
            if (getActivity() != null)
                getActivity().finish();
        }

        @Override
        public <T> void onItemLongClickListener(T object) {

        }
    };


}
