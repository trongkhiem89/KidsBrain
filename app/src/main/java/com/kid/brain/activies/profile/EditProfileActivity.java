package com.kid.brain.activies.profile;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidBean;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IOnDatePickerChangeListener;
import com.kid.brain.models.Account;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.AccountResponse;
import com.kid.brain.provider.request.model.EditProfileParams;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.util.BitmapUtils;
import com.kid.brain.util.Constants;
import com.kid.brain.util.NetworkUtil;
import com.kid.brain.util.PermissionUtils;
import com.kid.brain.util.log.ALog;
import com.kid.brain.view.dialog.DatePickerDialog;
import com.kid.brain.view.dialog.DateTimeUtils;
import com.kid.brain.view.dialog.DialogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_edit_profile)
@OptionsMenu(R.menu.menu_edit_profile)
public class EditProfileActivity extends BaseAppCompatActivity {

    private static String TAG = EditProfileActivity.class.getName();

    @ViewById
    Toolbar toolbar;

    @ViewById
    ImageView imgUserAvatar;

    @ViewById
    EditText edtEmail;

    @ViewById
    EditText edtFullName;

    @ViewById
    EditText edtPhoneNumber;

    @ViewById
    EditText edtDateOfBirth;

    @ViewById
    EditText edtAddress;

    @ViewById
    RadioButton rbMale;

    @ViewById
    RadioButton rbFemale;

    @Extra(Constants.KEY_ACCOUNT)
    Account mAccount;

    private BottomSheetDialog mBottomSheetDialog;
    private Uri picUri;
    private String strAvatarBase64 = "";

    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        setUpToolbarWithBackButton(toolbar, R.string.app_name);
        displayAccountInfo();
    }

    @OptionsItem(android.R.id.home)
    void home() {
        this.finish();
    }

    @OptionsItem(R.id.actionSave)
    void doSaveProfile() {
        if (mAccount != null) {
            if (invalid()) return;
            if (NetworkUtil.isConnected(this)) {
                showProgressBar(getString(R.string.dialog_message_updating));

                mAccount.setUsername(edtFullName.getText().toString().trim());
                mAccount.setAddress(edtAddress.getText().toString().trim());
                mAccount.setMobile(edtPhoneNumber.getText().toString().trim());
                mAccount.setAddress(edtAddress.getText().toString().trim());
                mAccount.setBirthDay(DateTimeUtils.convertLocalToUTC(edtDateOfBirth.getText().toString().trim()));
                int gender = rbMale.isChecked() ? Account.MALE : Account.FEMALE;
                mAccount.setGender(gender);

                doUpdateProfile(mAccount);

            } else {
                showErrorNetWork();
            }

        } else {
            showErrorSnackbar("Error");
        }
    }

    @Click(R.id.btnChangeAvatar)
    void doChangeAvatar() {
        showBottomSheetDialog();
    }

    @Click(R.id.btnDateOfBirth)
    void doChangeDateOfBirth() {
        String birthDay = mAccount == null ? null : edtDateOfBirth.getText().toString().trim();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, birthDay, new IOnDatePickerChangeListener() {
            @Override
            public void onSetDate(String dateString) {
                edtDateOfBirth.setText(dateString);
            }
        });
        datePickerDialog.show();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (outState != null)
            outState.putString("photo", mAccount.getPhoto());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String photo = savedInstanceState.getString("photo");
            if (!TextUtils.isEmpty(photo)) {
                mAccount.setPhoto(photo);
                onDisplayAvatar(mAccount.getPhoto());
            } else if (!TextUtils.isEmpty(mAccount.getPhoto())) {
                onDisplayAvatar(mAccount.getPhoto());
            }
        }
    }

    private void displayAccountInfo() {
        if (mAccount != null) {
            edtEmail.setText(mAccount.getEmail());
            edtFullName.setText(mAccount.getUsername());
            edtPhoneNumber.setText(mAccount.getMobile());
            edtAddress.setText(mAccount.getAddress());
            edtDateOfBirth.setText(DateTimeUtils.convertUTCToLocal(mAccount.getBirthDay()));
            if (mAccount.getGender() == Account.MALE) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }

            onDisplayAvatar(mAccount.getPhoto());
        }
    }

    private void doUpdateProfile(Account account) {
        HeaderSession header = new HeaderSession();
        EditProfileParams profileParams = new EditProfileParams(account);

        APIService apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);
        Call<AccountResponse> callUser = apiService.updateProfile(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(account.getUserId()),
                profileParams);
        callUser.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

                if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                    AccountResponse accountResponse = response.body();
                    if (accountResponse != null) {
                        Account account = accountResponse.getAccount();
                        if (account != null) {
                            ALog.i(TAG, account.toString());
                            try {
                                KidBean.getInstance().setAccount(account);
                                KidPreference.saveValue(KidPreference.KEY_FULL_NAME, account.getFullNameOrEmail());
                                KidPreference.saveValue(KidPreference.KEY_PHONE_NUMBER, mAccount.getMobile());
                                KidPreference.saveValue(KidPreference.KEY_EMAIL, account.getEmail());
                                KidPreference.saveValue(KidPreference.KEY_PHOTO, account.getPhoto());
                                KidRepository.getInstance(EditProfileActivity.this).updateAccount(account);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(EditProfileActivity.this, accountResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                            dismissProgressBar();
                            EditProfileActivity.this.finish();

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

    private void onDisplayAvatar(String base64) {
        ImageLoader.getInstance().displayImage(base64, imgUserAvatar, KidApplication.getInstance().getOptions());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent,
                            Constants.IMAGE_CAPTURE_REQUEST);
                }
            } else if (requestCode == Constants.IMAGE_CAPTURE_REQUEST) {
                if (data != null && data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {

                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, Constants.IMAGE_WIDTH_SIZE, Constants.IMAGE_HEIGHT_SIZE, true);
                        imgUserAvatar.setImageBitmap(resized);

                        byte[] bytes = BitmapUtils.getInstance().convertBitmapToByte(resized);
                        strAvatarBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                        mAccount.setPhoto(strAvatarBase64);

                        Bundle outState = new Bundle();
                        outState.putString("photo", mAccount.getPhoto());
                    }
                }

            } else if (requestCode == Constants.PICK_IMAGE_REQUEST) {
                picUri = data.getData();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picUri));
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, Constants.IMAGE_WIDTH_SIZE, Constants.IMAGE_HEIGHT_SIZE, true);

                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    imgUserAvatar.setImageBitmap(resized);

                    byte[] bytes = BitmapUtils.getInstance().convertBitmapToByte(resized);
                    strAvatarBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                    mAccount.setPhoto(strAvatarBase64);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //performCrop();
            }

            //user is returning from cropping the image
            else if (requestCode == Constants.PIC_CROP) {
                //get the returned data
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    imgUserAvatar.setImageBitmap(bitmap);

                    byte[] bytes = BitmapUtils.getInstance().convertBitmapToByte(bitmap);
                    strAvatarBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    mAccount.setPhoto(strAvatarBase64);
                }
            }
        }
    }

    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, Constants.PIC_CROP);
        } catch (ActivityNotFoundException exception) {
            ALog.e("Crop Image Error:", exception.getMessage());
        }
    }

    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_camera_gallery, null);
        mBottomSheetDialog = new BottomSheetDialog(EditProfileActivity.this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
        TextView tvChooseCamera = view.findViewById(R.id.tvChooseCamera);
        TextView tvChooseGallery = view.findViewById(R.id.tvChooseGallery);
        tvChooseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissBottomSheet();
                if (PermissionUtils.checkCameraPermission(EditProfileActivity.this)) {
                    if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        ALog.e("Camera Permission", "Camera is enabled !");
                        try {
                            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(pictureIntent,
                                        Constants.IMAGE_CAPTURE_REQUEST);
                            }
                        } catch (ActivityNotFoundException exception) {
                            exception.printStackTrace();
                        }
                    }
                }


            }
        });
        tvChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissBottomSheet();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST);
            }
        });
    }

    private void dismissBottomSheet() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
    }

    private boolean invalid() {
        if (TextUtils.isEmpty(edtFullName.getText().toString().trim())) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_username));
            edtFullName.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(edtPhoneNumber.getText().toString().trim())) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_phone_number));
            edtPhoneNumber.requestFocus();
            return true;
        }

//        if (!AppCommon.validateEmail(edtPhoneNumber.getText().toString().trim())) {
//            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_phone_number));
//            return true;
//        }

        if (TextUtils.isEmpty(edtAddress.getText().toString().trim())) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_address));
            edtAddress.requestFocus();
            return true;
        }

        return false;
    }
}
