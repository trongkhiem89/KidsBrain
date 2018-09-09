package com.kid.brain.activies.authorization;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.OnCheckDbListener;
import com.kid.brain.models.Account;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.AccountResponse;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.LoginParams;
import com.kid.brain.util.AppCommon;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.PwdUtil;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_sigup)
public class SignUpActivity extends BaseAppCompatActivity {

    private final String TAG = SignUpActivity.class.getName();
    private final int PWD_MIN_LEN = 8;
    private final int PWD_MAX_LEN = 64;

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText edtEmail;

    @ViewById
    EditText edtPassword;

    @ViewById
    EditText edtRetypePassword;

    private Account mAccount;

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.app_name);
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    /**
     * Todo: click go back to login account
     */
    @Click(R.id.tvBackToLogin)
    void doGoBack() {
        SignUpActivity.this.finish();
    }

    /**
     * Todo: click button Sign Up
     */
    @Click(R.id.btnSignUp)
    void doSignUp() {
        if (NetworkUtil.isConnected(this)) {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String passwordConfirm = edtRetypePassword.getText().toString().trim();
            if (!invalid(email, password, passwordConfirm)) {
                createNewAccount(email, password);
            }
        } else {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_network));
        }
    }

    private void createNewAccount(String email, String password) {
            if (!NetworkUtil.isConnected(this)) {
                showErrorNetWork();
                return;
            }

            showProgressBar(getString(R.string.dialog_message_loading));
            HeaderSession header = new HeaderSession();
            LoginParams loginParams = new LoginParams(email, password);

            APIService apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);
            Call<AccountResponse> callUser = apiService.signUp(
                    header.getContentType(),
                    header.getLanguageCode(),
                    loginParams);
            callUser.enqueue(new Callback<AccountResponse>() {
                @Override
                public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                    try {
                        if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                            AccountResponse accountResponse = response.body();
                            if (accountResponse != null) {
                                mAccount = accountResponse.getAccount();
                                if (mAccount != null) {
                                    ALog.i(TAG, mAccount.toString());
                                    KidBean.getInstance().setAccount(mAccount);
                                    saveAccountAndGoHome();
                                } else {
                                    dismissProgressBar();
                                    showErrorDialog(accountResponse.getError());
                                }
                            } else {
                                dismissProgressBar();
                                showAlertDialog(getString(R.string.app_name), getString(R.string.error_sign_up));
                            }
                        } else {
                            dismissProgressBar();
                            String strError = readIn(response.errorBody().byteStream());
                            Error error = new Gson().fromJson(strError, Error.class);
                            showErrorDialog(error);
                        }
                    } catch (Exception e) {
                        showAlertDialog(getString(R.string.app_name), getString(R.string.error_sign_up));
                    }
                }

                @Override
                public void onFailure(Call<AccountResponse> call, Throwable t) {
                    showAlertDialog(getString(R.string.app_name), getString(R.string.error_internal));
                    dismissProgressBar();
                    t.printStackTrace();
                }
            });
    }

    private void saveAccountAndGoHome(){
        String randomPassword = KidPreference.getStringValue(KidPreference.KEY_PIN);
        if (TextUtils.isEmpty(randomPassword)) {
            randomPassword = PwdUtil.genSimplePassword();
            KidPreference.saveValue(KidPreference.KEY_PIN, randomPassword);
        }

        final String finalRandomPassword = randomPassword;
        KidApplication.getInstance().checkDatabase(randomPassword, new OnCheckDbListener() {
            @Override
            public void onOpenDbSuccess() {
                KidPreference.saveValue(KidPreference.KEY_PIN, finalRandomPassword);
                try {
                    KidPreference.saveValue(KidPreference.KEY_FULL_NAME, mAccount.getFullNameOrEmail());
                    KidPreference.saveValue(KidPreference.KEY_PHONE_NUMBER, mAccount.getMobile());
                    KidPreference.saveValue(KidPreference.KEY_EMAIL, mAccount.getEmail());
                    KidPreference.saveValue(KidPreference.KEY_PHOTO, mAccount.getPhoto());
                    KidPreference.saveValue(KidPreference.KEY_USER_ID, mAccount.getUserId());
                    KidPreference.saveValue(KidPreference.KEY_LOGGED, true);

                    KidRepository.newInstance(SignUpActivity.this).saveAccount(mAccount);
                    KidRepository.newInstance(SignUpActivity.this).saveKids(mAccount.getKids());

                    goToHome(SignUpActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dismissProgressBar();
                }
            }

            @Override
            public void onOpenDbFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.error_create_or_open_db_failed));
                        dismissProgressBar();
                    }
                });
            }
        });
    }

    private boolean invalid(String email, String password, String passwordConfirm) {
        if (TextUtils.isEmpty(email)) {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_empty_email));
            edtEmail.requestFocus();
            return true;
        }
        if (!AppCommon.validateEmail(email)) {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_valid_email));
            edtEmail.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_empty_password));
            edtPassword.requestFocus();
            return true;
        }
        if (password.length() < PWD_MIN_LEN || password.length() > PWD_MAX_LEN/*
                || !AppCommon.validatePassword(password) || password.contains(" ")*/) {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_valid_password));
            edtPassword.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_empty_password_confirm));
            edtRetypePassword.requestFocus();
            return true;
        }
        if (!password.equals(passwordConfirm)) {
            DialogUtil.showErrorDialog(SignUpActivity.this, getString(R.string.error_password_not_match));
            edtRetypePassword.requestFocus();
            return true;
        }
        return false;
    }
}

