package com.kid.brain.activies.profile.kid;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.authorization.LoginActivity;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.help.KidBusiness;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Account;
import com.kid.brain.models.Kid;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.AccountResponse;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.KidResponse;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.adapters.KidAdapter;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_kids)
public class KidsFragment extends BaseFragment {

    private static final String TAG = KidsFragment.class.getName();
    private RecyclerViewSkeletonScreen skeletonScreen;

    @ViewById
    RecyclerView rvKids;

    private Kid mKid;
    private String mParentId;
    private List<Kid> mKids;
    private KidAdapter adapter;

    public KidsFragment() {
    }

    public static KidsFragment_ newInstance() {
        KidsFragment_ fragment = new KidsFragment_();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.app_name));
        try {
            initialView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mParentId = KidPreference.getStringValue(KidPreference.KEY_USER_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkUtil.isConnected(getActivity())) {
            doFetchKids(mParentId);
        } else {
            try {
                mKids = KidRepository.getInstance(getActivity()).getKids(mParentId);
                displayItems();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doDeleteKid(Kid kid) {
        HeaderSession header = new HeaderSession();
        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<KidResponse> callUser = apiService.deleteKid(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(mParentId),
                Long.parseLong(kid.getChildrenId()));
        callUser.enqueue(new Callback<KidResponse>() {
            @Override
            public void onResponse(Call<KidResponse> call, Response<KidResponse> response) {

                if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                    KidResponse kidResponse = response.body();
                    if (kidResponse != null) {
                        Error error = kidResponse.getError();
                        if (error.getCode() == WebserviceConfig.HTTP_CODE.SUCCESS) {
                            try {
                                long result = KidRepository.getInstance(getActivity()).deleteKid(mKid.getChildrenId());
                                ALog.i(TAG, ">>> Delete Kid >>> " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mKids.remove(mKid);
                            adapter.notifyDataSetChanged();

                            dismissProgressBar();

                        } else {
                            dismissProgressBar();
                            showErrorDialog(error);
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

    private void doFetchKids(String userId) {
        HeaderSession header = new HeaderSession();
        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<AccountResponse> callKids = apiService.fetchKids(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(userId));
        callKids.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                try {
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        AccountResponse accountResponse = response.body();
                        if (accountResponse != null) {
                            Account mAccount = accountResponse.getAccount();
                            if (mAccount != null) {
                                ALog.i(TAG, mAccount.toString());
                                try {
                                    mKids = mAccount.getKids();
                                    displayItems();
                                    KidRepository.getInstance(getActivity()).saveKids(mAccount.getKids());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showErrorDialog(accountResponse.getError());
                            }
                        }
                    } else {
                        String strError = readIn(response.errorBody().byteStream());
                        Error error = new Gson().fromJson(strError, Error.class);
                        showErrorDialog(error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dismissProgressBar();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                dismissProgressBar();
                t.printStackTrace();
            }
        });
    }

    private void initialView() {
        adapter = new KidAdapter(getActivity());
        adapter.setOnItemClickListener(onItemClickListener);

        rvKids.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvKids.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(rvKids)
                .adapter(adapter)
                .load(R.layout.item_skeleton_person)
                .show();
    }

    private void hideSkeletonScreenLoading() {
        skeletonScreen.hide();
    }

    private void displayItems() {
        hideSkeletonScreenLoading();
        if (mKids == null) {
            mKids = new ArrayList<>();
        }

        this.addDefaultItem();

        if (mKids != null && mKids.size() > 0) {
            adapter.setData(mKids);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                rvKids.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            else
                rvKids.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
    }

    private void addDefaultItem() {
        Iterator<Kid> iterator = mKids.iterator();
        while (iterator.hasNext()) {
            Kid kid = iterator.next();
            if (Account.DEFAULT_ID.equalsIgnoreCase(kid.getChildrenId())) {
                iterator.remove();
            }
        }
        mKids.add(KidBusiness.getInstance().getDefault());
    }

    //TODO: Click on Item listener
    private IOnItemClickListener onItemClickListener = new IOnItemClickListener() {
        @Override
        public <T> void onItemClickListener(T object) {
            Kid kid = (Kid) object;
            if (kid == null) return;
            if (Account.DEFAULT_ID.equalsIgnoreCase(kid.getChildrenId())) {
                // Click default add kid
                Intent intentEditProfile = new Intent(getActivity(), KidCreateUpdateActivity_.class);
                startActivity(intentEditProfile);
            } else {
                if (NetworkUtil.isConnected(getActivity())) {
                    Intent intentEditProfile = new Intent(getActivity(), KidDetailActivity_.class);
                    intentEditProfile.putExtra(Constants.KEY_KID, kid);
                    startActivity(intentEditProfile);
                } else {
                    showAlertDialog(getString(R.string.app_name), getString(R.string.error_network));
                }
            }
        }

        @Override
        public <T> void onItemLongClickListener(T object) {
            mKid = (Kid) object;
            if (mKid == null) return;
            if (Account.DEFAULT_ID.equalsIgnoreCase(mKid.getChildrenId())) return;

            DialogUtil.createCustomConfirmDialog(getActivity(),
                    getString(R.string.app_name),
                    getString(R.string.str_kid_delete_confirm_message),
                    getString(R.string.btn_delete),
                    getString(R.string.btn_cancel),
                    new DialogUtil.ConfirmDialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {
                            if (NetworkUtil.isConnected(getActivity())) {
                                doDeleteKid(mKid);
                            } else {
                                showAlertDialog(getString(R.string.app_name), getString(R.string.error_network));
                            }
                        }

                        @Override
                        public void onCancelButtonOnClick() {

                        }
                    }).show();
        }
    };


}
