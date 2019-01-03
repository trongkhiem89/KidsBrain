package com.kid.brain.activies.settings;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.activies.tutorial.AppInfoActivity_;
import com.kid.brain.activies.tutorial.TutorialActivity_;
import com.kid.brain.managers.application.BaseFragment;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IActivityCommunicatorListener;
import com.kid.brain.managers.listeners.IDialogOkListener;
import com.kid.brain.managers.listeners.IShare;
import com.kid.brain.provider.database.DatabaseManager;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.AccountResponse;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.PasswordParams;
import com.kid.brain.util.Constants;
import com.kid.brain.util.LocaleManager;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.ChangePasswordDialog;
import com.kid.brain.view.dialog.ShareSocialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends BaseFragment {

    private static final String TAG = SettingsFragment.class.getName();

    @ViewById
    TextView tvTutorial;

    @ViewById
    TextView tvShare;

    @ViewById
    TextView tvChangeLanguage;

    @ViewById
    TextView tvAppInfo;

    @ViewById
    TextView tvChangePassword;

    @ViewById
    TextView tvExportDatabase;

    private IActivityCommunicatorListener mCommunicatorListener;
    private CallbackManager mFbCallbackManager;
    private ShareDialog mFbShareDialog;

    @AfterViews
    void afterViews() {
        mFbCallbackManager = CallbackManager.Factory.create();
        mFbShareDialog = new ShareDialog(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCommunicatorListener = (IActivityCommunicatorListener) context;
    }

    @Click(R.id.relTutorial)
    void doTutorial() {
        //KidPreference.saveValue(KidPreference.KEY_SHOW_TUTORIAL, false);
        startActivity(new Intent(getActivity(), TutorialActivity_.class));
    }

    @Click(R.id.relShare)
    void doShare() {
        ShareSocialDialog dialog = new ShareSocialDialog(getActivity(), new IShare() {

            @Override
            public void shareEmail() {
                try {
                    onShareEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void shareFacebook() {
                try {
                    onShareFacebook();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void shareOther() {
                try {
                    onShareOther();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void onShareFacebook() throws Exception {
        mFbShareDialog.registerCallback(mFbCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                ALog.d(TAG, "Success");
            }

            @Override
            public void onCancel() {
                ALog.d(TAG, "Cancel");

            }

            @Override
            public void onError(FacebookException e) {
                ALog.d(TAG, "Error");
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            String appPackageName = getActivity() != null ? getActivity().getPackageName() : KidApplication.getInstance().getPackageName();
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(getString(R.string.str_app_share_link, appPackageName)))
                    .setQuote(getString(R.string.str_app_info_text))
                    .build();
            mFbShareDialog.show(linkContent);
        }
    }

    private void onShareEmail() throws Exception {
        onShareOther();
    }

    private void onShareOther() throws Exception {
        String appPackageName = getActivity() != null ? getActivity().getPackageName() : KidApplication.getInstance().getPackageName();
        String text = getString(R.string.str_app_info_text);
        String link = getString(R.string.str_app_share_link, appPackageName);
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        i.putExtra(android.content.Intent.EXTRA_TEXT, text + "\n" + Uri.parse(link));
        startActivity(Intent.createChooser(i, getString(R.string.str_share)));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Click(R.id.relChangeLanguage)
    void doChangeLanguage() {
        changeLanguage();
    }

    @Click(R.id.relAppInfo)
    void doAppInfo() {
        startActivity(new Intent(getActivity(), AppInfoActivity_.class));
    }

    @Click(R.id.relExportDatabase)
    void doExportDatabase() {
        DatabaseManager.exportDatabasesBackUp();
        Toast.makeText(getActivity(), "Exported success", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.relChangePassword)
    void doChangePassword() {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(getActivity(), new IDialogOkListener() {
            @Override
            public <T> void onOk(T object) {
                String[] data = (String[]) object;
                if (data != null && data.length == 2) {
                    updatePassword(data[0], data[1]);
                }
            }

            @Override
            public void onCancel() {

            }
        });
        changePasswordDialog.show();

    }

    private void updateUI() {
        tvTutorial.setText(getString(R.string.str_tutorial));
        tvShare.setText(getString(R.string.str_share));
        tvChangeLanguage.setText(getString(R.string.str_change_language));
        tvAppInfo.setText(getString(R.string.str_app_info));
        tvChangePassword.setText(getString(R.string.str_change_password));
    }

    private void changeLanguage() throws NullPointerException {
        String langCode = KidPreference.getStringValue(KidPreference.KEY_LANGUAGE_CODE);
        final int checkedItem = langCode.equalsIgnoreCase(Constants.Language.LANG_EN) ? 1 : 0;

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.str_language))
                .setSingleChoiceItems(getResources().getStringArray(R.array.languages), checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == checkedItem) return;
                        KidPreference.saveValue(KidPreference.KEY_LANGUAGE_CODE, which == 0 ? Constants.Language.LANG_VI : Constants.Language.LANG_EN);
                        LocaleManager.setNewLocale(getActivity(), which == 0 ? Constants.Language.LANG_VI : Constants.Language.LANG_EN);
                        dialog.dismiss();
                        updateUI();
                        mCommunicatorListener.passDataToActivity("");
                    }
                })
                .create().show();
    }

    private void updatePassword(String currentPassword, String newPassword) {
        if (!NetworkUtil.isConnected(getActivity())) {
            showErrorNetWork();
            return;
        }

        showProgressBar(getContext(), getString(R.string.dialog_message_updating));
        HeaderSession header = new HeaderSession();
        PasswordParams params = new PasswordParams(currentPassword, newPassword);
        String userId = KidPreference.getStringValue(KidPreference.KEY_USER_ID);

        APIService apiService = RetrofitConfig.getInstance(getActivity()).getRetrofit().create(APIService.class);
        Call<AccountResponse> callUser = apiService.updatePassword(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(userId),
                params);
        callUser.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                try {
                    dismissProgressBar();
                    if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                        AccountResponse accountResponse = response.body();
                        if (accountResponse != null) {
                            showErrorDialog(accountResponse.getError());
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
}
