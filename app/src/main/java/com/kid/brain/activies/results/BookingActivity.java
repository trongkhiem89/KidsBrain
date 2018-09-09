package com.kid.brain.activies.results;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.BookingParams;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.history.TestResponse;
import com.kid.brain.util.AppCommon;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.view.dialog.DialogUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_booking)
@OptionsMenu(R.menu.home)
public class BookingActivity extends BaseAppCompatActivity {

    private static String TAG = BookingActivity.class.getName();

    @Extra(Constants.KEY_HISTORY_ID)
    String mHistoryId;

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText edtFullName;

    @ViewById
    EditText edtPhoneNumber;

    @ViewById
    EditText edtEmail;

    @ViewById
    EditText edtNote;


    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.str_booking);

        edtFullName.setText(KidPreference.getStringValue(KidPreference.KEY_FULL_NAME));
        edtPhoneNumber.setText(KidPreference.getStringValue(KidPreference.KEY_PHONE_NUMBER));
        edtEmail.setText(KidPreference.getStringValue(KidPreference.KEY_EMAIL));
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @OptionsItem(R.id.action_logo)
    void onClickLogo() {
        super.goToHome(BookingActivity.this);
    }

    @Click(R.id.btnBooking)
    void doBooking() {
        if (NetworkUtil.isConnected(this)) {

            String fullName = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhoneNumber.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (invalid(fullName, email, phone, note)) return;

            showProgressBar(getString(R.string.dialog_message_sending));
            HeaderSession header = new HeaderSession();
            BookingParams params = new BookingParams(phone, email, fullName, note);

            APIService apiService = RetrofitConfig.getInstance(BookingActivity.this).getRetrofit().create(APIService.class);
            Call<TestResponse> callUser = apiService.booking(
                    header.getContentType(),
                    header.getLanguageCode(),
                    mHistoryId,
                    params);
            callUser.enqueue(new Callback<TestResponse>() {
                @Override
                public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                    try {
                        if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                            TestResponse accountResponse = response.body();
                            if (accountResponse != null && accountResponse.getError().getCode() == WebserviceConfig.HTTP_CODE.SUCCESS) {
                                dismissProgressBar();
                                DialogUtil.createCustomOkDialog(BookingActivity.this,
                                        getString(R.string.app_name),
                                        getString(R.string.str_booking_success),
                                        getString(R.string.btn_close),
                                        new DialogUtil.DialogOnClickListener() {
                                            @Override
                                            public void onOKButtonOnClick() {
                                                BookingActivity.this.finish();
                                            }
                                        }).show();
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
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean invalid(String fullName, String email, String phone, String note) {
        if (TextUtils.isEmpty(fullName)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_username));
            edtFullName.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(email)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_email));
            edtEmail.requestFocus();
            return true;
        }

        if (!AppCommon.validateEmail(email)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_valid_email));
            edtEmail.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(phone)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_phone_number));
            edtPhoneNumber.requestFocus();
            return true;
        }

        if (!AppCommon.validatePhone(phone)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_format_phone_number));
            return true;
        }

        if (TextUtils.isEmpty(note)) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_note));
            edtNote.requestFocus();
            return true;
        }

        return false;
    }
}
