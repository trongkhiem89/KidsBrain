package com.kid.brain.activies.authorization;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.AccountResponse;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.util.AppCommon;
import com.kid.brain.util.Constants;
import com.kid.brain.util.EmailUtil;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends BaseAppCompatActivity {

    private final String TAG = ForgotPasswordActivity.class.getName();

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText edtEmail;


    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.btn_forgot_password);
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @Click(R.id.btnContactSupport)
    void doContactSupport() {
        EmailUtil.sendEmail2(ForgotPasswordActivity.this, Constants.CONTACT_SUPPORT, "Subject", "");
    }

    @Click(R.id.btnSend)
    void doSendEmail() {
        if (NetworkUtil.isConnected(this)) {
            String email = edtEmail.getText().toString().trim();
            if (!invalid(email)) {
                sendForgotPassword(email);
            }
        } else {
            DialogUtil.showErrorDialog(ForgotPasswordActivity.this, getString(R.string.error_network));
        }
    }

    private void sendForgotPassword(String email) {
        if (!NetworkUtil.isConnected(this)) {
            showErrorNetWork();
            return;
        }

        showProgressBar(getString(R.string.dialog_message_sending));
        HeaderSession header = new HeaderSession();

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), email);

        APIService apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);
        Call<AccountResponse> callUser = apiService.forgotPassword(
                header.getContentType(),
                header.getLanguageCode(),
                body);
        callUser.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                try {
                    dismissProgressBar();
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        AccountResponse accountResponse = response.body();
                        if (accountResponse != null) {
                            if (accountResponse.getError() != null && accountResponse.getError().getCode() == WebserviceConfig.HTTP_CODE.SUCCESS) {
                                String msg = accountResponse.getError().getMessage();
                                if (TextUtils.isEmpty(msg)) {
                                    msg = getString(R.string.str_send_forgot_password_success);
                                }
                                DialogUtil.createCustomOkDialog(ForgotPasswordActivity.this,
                                        getString(R.string.app_name),
                                        msg,
                                        getString(R.string.btn_ok),
                                        new DialogUtil.DialogOnClickListener() {
                                            @Override
                                            public void onOKButtonOnClick() {
                                                ForgotPasswordActivity.this.finish();
                                            }
                                        }).show();
                            } else {
                                showErrorDialog(accountResponse.getError());
                            }
                        } else {
                            showAlertDialog(getString(R.string.app_name), getString(R.string.error_internal));
                        }
                    } else {
                        String strError = readIn(response.errorBody().byteStream());
                        Error error = new Gson().fromJson(strError, Error.class);
                        showErrorDialog(error);
                    }
                } catch (Exception e) {
                    showAlertDialog(getString(R.string.app_name), getString(R.string.error_internal));
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

    private boolean invalid(String email) {
        if (TextUtils.isEmpty(email)) {
            DialogUtil.showErrorDialog(ForgotPasswordActivity.this, getString(R.string.error_empty_email));
            edtEmail.requestFocus();
            return true;
        }
        if (!AppCommon.validateEmail(email)) {
            DialogUtil.showErrorDialog(ForgotPasswordActivity.this, getString(R.string.error_valid_email));
            edtEmail.requestFocus();
            return true;
        }

        return false;
    }
}

