package com.kid.brain.activies.authorization;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.home.HomeActivity;
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
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.PwdUtil;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseAppCompatActivity {


    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText edtEmail;

    @ViewById
    EditText edtPassword;

    private Account mAccount;
    private CallbackManager mCallbackManager;

    @AfterViews
    void afterViews() {
        setUpToolbar(toolbar, R.string.app_name);
    }

    @Click({R.id.btnLogin})
    void doLogin() {
        login();
    }

    @Click({R.id.btnSignUp})
    void doSignUp() {
        startActivity(SignUpActivity_.class);
    }

    @Click({R.id.tvForgotPassword})
    void onForgotPassword() {
        startActivity(ForgotPasswordActivity_.class);
    }

    @Click({R.id.btnFacebook})
    void onLoginFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        ALog.e("LoginResult", loginResult.toString());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        ALog.e("onCancel","");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        ALog.e("FacebookException",exception.getMessage());
                        exception.printStackTrace();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void login() {
        if (!NetworkUtil.isConnected(this)) {
            showErrorNetWork();
            return;
        }

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (invalid(email, password)) return;

        showProgressBar(getString(R.string.dialog_message_sigin));
        HeaderSession header = new HeaderSession();
        LoginParams loginParams = new LoginParams(email, password);

        APIService apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);
        Call<AccountResponse> callUser = apiService.login(
                header.getContentType(),
                header.getLanguageCode(),
                loginParams);
        callUser.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

                if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                    AccountResponse accountResponse = response.body();
                    if (accountResponse != null) {
                        mAccount = accountResponse.getAccount();
                        if (mAccount != null) {
                            ALog.i(TAG, mAccount.toString());
                            saveAccountAndGoHome();
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
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                dismissProgressBar();
                t.printStackTrace();
            }
        });
    }

    private void saveAccountAndGoHome() {
        String randomPassword = KidPreference.getStringValue(KidPreference.KEY_PIN);
        if (TextUtils.isEmpty(randomPassword)) {
            randomPassword = PwdUtil.genSimplePassword();
            KidPreference.saveValue(KidPreference.KEY_PIN, randomPassword);
        }

        KidApplication.getInstance().checkDatabase(randomPassword, new OnCheckDbListener() {
            @Override
            public void onOpenDbSuccess() {
                try {
                    KidBean.getInstance().setAccount(mAccount);
                    KidPreference.saveValue(KidPreference.KEY_FULL_NAME, mAccount.getFullNameOrEmail());
                    KidPreference.saveValue(KidPreference.KEY_PHONE_NUMBER, mAccount.getMobile());
                    KidPreference.saveValue(KidPreference.KEY_EMAIL, mAccount.getEmail());
                    KidPreference.saveValue(KidPreference.KEY_PHOTO, mAccount.getPhoto());
                    KidPreference.saveValue(KidPreference.KEY_USER_ID, mAccount.getUserId());
                    KidPreference.saveValue(KidPreference.KEY_LOGGED, true);

                    KidRepository.newInstance(LoginActivity.this).saveAccount(mAccount);
                    KidRepository.getInstance(LoginActivity.this).saveKids(mAccount.getKids());

                    startActivity(HomeActivity.class);
                    LoginActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlertDialog(getString(R.string.app_name), e.getMessage());
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

    private boolean invalid(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_email));
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_password));
            return true;
        }
        return false;
    }
}

