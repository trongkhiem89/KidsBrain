package com.kid.brain.managers.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.kid.brain.R;
import com.kid.brain.activies.authorization.LoginActivity_;
import com.kid.brain.activies.home.HomeActivity;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.util.LocaleManager;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.DialogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import javax.crypto.Cipher;


/**
 * Created by khiemnt on 9/3/2015.
 * Abstract base appCompatActivity
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    public static String TAG;
    private ProgressDialog progressDialog;
    private int onStartCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getBaseContext().getClass().getName();

        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
            onStartCount = 2;
        }
        if (getIntent() != null) {

        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
        ALog.d(TAG, "attachBaseContext");
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

    /**
     * Abstract start new activity not param
     *
     * @param className Target source class
     * @param <T>
     */
    public <T> void startActivity(Class<T> className) {
        startActivity(new Intent(this, className));
    }

    public <T> void startActivityForResult(Class<T> className, int code) {
        startActivityForResult(new Intent(this, className), code);
    }

    public <T> void startActivityForResult(Class<T> className, int code, Object param, String keyParam) {
        Intent intent = new Intent(this, className);
        if (param instanceof Integer) {
            intent.putExtra(keyParam, (int) param);
        } else if (param instanceof Long) {
            intent.putExtra(keyParam, (long) param);
        } else if (param instanceof Float) {
            intent.putExtra(keyParam, (float) param);
        } else if (param instanceof String) {
            intent.putExtra(keyParam, String.valueOf(param));
        } else if (param instanceof Boolean) {
            intent.putExtra(keyParam, (boolean) param);
        } else {
            intent.putExtra(keyParam, (Serializable) param);
        }
        startActivityForResult(intent, code);
    }

    /**
     * Abstract start new activity have param
     *
     * @param className Target source class
     * @param param     Param
     * @param keyParam  Key param
     * @param <T>
     */
    public <T> void startActivity(Class<T> className, Object param, String keyParam) {
        Intent intent = new Intent(this, className);
        if (param instanceof Integer) {
            intent.putExtra(keyParam, (int) param);
        } else if (param instanceof Long) {
            intent.putExtra(keyParam, (long) param);
        } else if (param instanceof Float) {
            intent.putExtra(keyParam, (float) param);
        } else if (param instanceof String) {
            intent.putExtra(keyParam, String.valueOf(param));
        } else if (param instanceof Boolean) {
            intent.putExtra(keyParam, (boolean) param);
        } else {
            intent.putExtra(keyParam, (Serializable) param);
        }
        startActivity(intent);
    }

    /**
     * Go to Home or Logout.
     * @param activity
     */
    public void goToHome(Activity activity) {
        Intent i = new Intent(activity, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(i);
        activity.finish();
    }

    public void goToLogin(Activity activity) {
        Intent i = new Intent(activity, LoginActivity_.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(i);
        activity.finish();
    }

    public boolean isHomeActivityRunning(Class<?> className) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo info : task) {
                if (info.baseActivity.getClassName().equals(className.getName())) return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public void setUpToolbar(Toolbar toolbar, int title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setTitle(getString(title));
        //toolbar.inflateMenu(R.menu.menu_main);
    }
    public void setUpToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setTitle(title);
        //toolbar.inflateMenu(R.menu.menu_main);
    }

    public void setUpToolbarWithBackButton(Toolbar toolbar, int title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(title));
    }
    public void setUpToolbarWithBackButton(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(title);
    }

    public void showBackButton(int title) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(title));
    }

    public void setHomeIcon(int homeIcon) {
        getSupportActionBar().setHomeAsUpIndicator(homeIcon);
    }

    public void disableScrollToolbar(Toolbar toolbar) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags
        toolbar.setLayoutParams(params);
    }

    public void enableScrollToolbar(Toolbar toolbar) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);  // clear all scroll flags
        toolbar.setLayoutParams(params);
    }


    public void showProgressBar() {
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.dialog_message_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressBar(String message) {
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressBar() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setMessage(String message) {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        }
    }

    protected void showAlertDialog(String title, String message) {
        try {
            final AlertDialog.Builder dialog = DialogUtil.createCustomOkDialog(this, title, message);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showAlertDialog(String title, String message, DialogUtil.DialogOnClickListener onClickYesListener) {
        final AlertDialog.Builder dialog = DialogUtil.createCustomOkDialog(this, title, message, onClickYesListener);
        dialog.show();
    }

    public void showToast(String msg) {
        Toast.makeText(KidApplication.getInstance().getAppContext(), msg, Toast.LENGTH_LONG).show();
    }

    public final void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public final void showKeyBoard() {
        try {
            hideKeyBoard();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
        }
    }

    public void showErrorNetWork() {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
//                    .setAction("Undo", mOnClickListener)
                .setActionTextColor(Color.RED)
                .show();
    }

    public void showErrorSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }

    public String readIn(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void showErrorDialog(Error error) {
        if (null != error) {
//            if (!TextUtils.isEmpty(error.getDetail())) {
//                showAlertDialog(getString(R.string.dialog_title), error.getDetail());
//            } else {
                showAlertDialog(getString(R.string.dialog_title), error.getMessage());
//            }
        }
    }


    /////// SETUP FINGER TOUCH ID ////////
/*    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher cipherNotInvalidated;

    public void setupFingerTouchId() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }

        try {
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        KeyguardManager keyguardManager = null;
        FingerprintManager fingerprintManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyguardManager = getSystemService(KeyguardManager.class);
            fingerprintManager = getSystemService(FingerprintManager.class);
        }


        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            showAlertDialog(getString(R.string.dialog_title), getString(R.string.fingerprint_setting_has_not_set_finger), new DialogUtil.DialogOnClickListener() {
                @Override
                public void onOKButtonOnClick() {
                    startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), Constants.REQUEST_SET_FINGERPRINT);
                }
            });
            return;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // noinspection ResourceType
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // This happens when no fingerprints are registered.
                showAlertDialog(getString(R.string.dialog_title), getString(R.string.fingerprint_setting_register_finger), new DialogUtil.DialogOnClickListener() {
                    @Override
                    public void onOKButtonOnClick() {
                        startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), Constants.REQUEST_SET_FINGERPRINT);
                    }
                });
                return;
            }
        }
//        createKey(DEFAULT_KEY_NAME, true);
        try {
            createKey(KEY_NAME_NOT_INVALIDATED, false);
        } catch (RuntimeException e) {
            showAlertDialog(getString(R.string.dialog_title), getString(R.string.fingerprint_setting_register_finger), new DialogUtil.DialogOnClickListener() {
                @Override
                public void onOKButtonOnClick() {
                    startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), Constants.REQUEST_SET_FINGERPRINT);
                }
            });
            return;
        }
        onShowFingerprintAuthenticationDialog(cipherNotInvalidated);
    }*/


    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String, boolean)} method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    /*private boolean initCipher(Cipher cipher) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey("key_not_invalidated", null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName                          the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     */
    /*public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder = new KeyGenParameterSpec.Builder(keyName,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        // Require the user to authenticate with a fingerprint to authorize every use
                        // of the key
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

                mKeyGenerator.init(builder.build());
                mKeyGenerator.generateKey();
            }

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
//            }

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*public void onShowFingerprintAuthenticationDialog(Cipher mCipher) {
        // Set up the crypto object for later. The object will be authenticated by use
        // of the fingerprint.
        if (initCipher(mCipher)) {
            FingerprintAuthenticationDialogFragment mFragment = new FingerprintAuthenticationDialogFragment();
            mFragment.setCryptoObject(new FingerprintManagerCompat.CryptoObject(mCipher));
            mFragment.show(getFragmentManager(), FingerprintAuthenticationDialogFragment.class.getName());

        } else {
            DialogUtil.showErrorDialog(this, getString(R.string.new_fingerprint_enrolled));
        }
    }*/
}
