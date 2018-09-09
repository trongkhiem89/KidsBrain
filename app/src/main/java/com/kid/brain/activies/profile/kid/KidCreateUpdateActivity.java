package com.kid.brain.activies.profile.kid;

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
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.managers.listeners.IOnDatePickerChangeListener;
import com.kid.brain.models.Account;
import com.kid.brain.models.Kid;
import com.kid.brain.provider.database.KidRepository;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.HeaderSession;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.provider.request.WebserviceConfig;
import com.kid.brain.provider.request.model.AddKidParams;
import com.kid.brain.provider.request.model.Error;
import com.kid.brain.provider.request.model.KidResponse;
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

@EActivity(R.layout.activity_kid_create_edit)
@OptionsMenu(R.menu.home)
public class KidCreateUpdateActivity extends BaseAppCompatActivity {

    private static String TAG = KidCreateUpdateActivity.class.getName();

    @ViewById
    Toolbar toolbar;

    @ViewById
    ImageView imgUserAvatar;

    @ViewById
    EditText edtFullName;

    @ViewById
    EditText edtDateOfBirth;

    @ViewById
    RadioButton rbMale;

    @ViewById
    RadioButton rbFemale;

    @Extra(Constants.KEY_KID)
    Kid mKid;

    private boolean mIsEdit;
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

    @Click(R.id.btnSave)
    void doSaveProfile() {
        if (invalid()) return;
        if (NetworkUtil.isConnected(this)) {
            showProgressBar(getString(R.string.dialog_message_updating));

            if (mKid == null) {
                mKid = new Kid();
                mKid.setParentId(KidPreference.getStringValue(KidPreference.KEY_USER_ID));
            }

            int gender = rbMale.isChecked() ? Account.MALE : Account.FEMALE;
            mKid.setUsername(edtFullName.getText().toString().trim());
            mKid.setBirthDay(DateTimeUtils.convertLocalToUTC(edtDateOfBirth.getText().toString().trim()));
            mKid.setGender(gender);

            if (mIsEdit) {
                doUpdateKid(mKid);
            } else {
                doAddKid(mKid);
            }

        } else {
            showErrorNetWork();
        }

    }

    @Click(R.id.btnChangeAvatar)
    void doChangeAvatar() {
        showBottomSheetDialog();
        ALog.i(TAG, "Change Avatar");
    }

    @Click(R.id.btnDateOfBirth)
    void doChangeDateOfBirth() {
        String birthDay = mKid == null ? null : edtDateOfBirth.getText().toString().trim();
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
            outState.putString("photo", mKid.getPhoto());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String photo = savedInstanceState.getString("photo");
            if (!TextUtils.isEmpty(photo)) {
                mKid.setPhoto(photo);
                onDisplayAvatar(mKid.getPhoto());
            } else if (!TextUtils.isEmpty(mKid.getPhoto())) {
                onDisplayAvatar(mKid.getPhoto());
            }
        }
    }

    private void displayAccountInfo() {
        if (mKid != null) {
            mIsEdit = true;
            edtFullName.setText(mKid.getUsername());
            edtDateOfBirth.setText(DateTimeUtils.convertUTCToLocal(mKid.getBirthDay()));
            if (mKid.getGender() == Account.MALE) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }

            onDisplayAvatar(mKid.getPhoto());
        } else {
            mIsEdit = false;
            mKid = new Kid();
            mKid.setParentId(KidPreference.getStringValue(KidPreference.KEY_USER_ID));
            onDisplayAvatar(mKid.getPhoto());
        }
    }

    /**
     * Todo: Update Kid
     *
     * @param kid
     */
    private void doUpdateKid(Kid kid) {
        HeaderSession header = new HeaderSession();
        AddKidParams kidParams = new AddKidParams(kid);

        APIService apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);
        Call<KidResponse> callUser = apiService.updateKid(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(kid.getChildrenId()),
                kidParams);
        callUser.enqueue(new Callback<KidResponse>() {
            @Override
            public void onResponse(Call<KidResponse> call, Response<KidResponse> response) {
                if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                    KidResponse kidResponse = response.body();
                    if (kidResponse != null) {
                        Kid kid = kidResponse.getKid();
                        if (kid != null) {
                            ALog.i(TAG, kid.toString());
                            try {
                                long result = KidRepository.getInstance(KidCreateUpdateActivity.this).saveKid(kid);
                                ALog.i(TAG, ">>> Update Kid >>> " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(KidCreateUpdateActivity.this, kidResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                            dismissProgressBar();

                            Intent result = new Intent();
                            result.putExtra(Constants.KEY_KID, kid);
                            setResult(RESULT_OK, result);

                            KidCreateUpdateActivity.this.finish();

                        } else {
                            dismissProgressBar();
                            showErrorDialog(kidResponse.getError());
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

    private void doAddKid(Kid kid) {
        HeaderSession header = new HeaderSession();
        AddKidParams kidParams = new AddKidParams(kid);

        APIService apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);
        Call<KidResponse> callUser = apiService.addKid(
                header.getContentType(),
                header.getLanguageCode(),
                Long.parseLong(kid.getParentId()),
                kidParams);
        callUser.enqueue(new Callback<KidResponse>() {
            @Override
            public void onResponse(Call<KidResponse> call, Response<KidResponse> response) {
                if (WebserviceConfig.HTTP_CODE.OK == response.code()) {
                    KidResponse kidResponse = response.body();
                    if (kidResponse != null) {
                        Kid kid = kidResponse.getKid();
                        if (kid != null) {
                            ALog.i(TAG, kid.toString());
                            try {
                                long result = KidRepository.getInstance(KidCreateUpdateActivity.this).saveKid(kid);
                                ALog.i(TAG, ">>> Add Kid >>> " + result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(KidCreateUpdateActivity.this, kidResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                            dismissProgressBar();

                            Intent result = new Intent();
                            result.putExtra(Constants.KEY_KID, kid);
                            setResult(RESULT_OK, result);

                            KidCreateUpdateActivity.this.finish();

                        } else {
                            dismissProgressBar();
                            showErrorDialog(kidResponse.getError());
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

    private void onDisplayAvatar(String base64) {
        ImageLoader.getInstance().displayImage(base64, imgUserAvatar, KidApplication.getInstance().getOptions());

//        if (!TextUtils.isEmpty(base64)) {
//            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
//            Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            imgUserAvatar.setImageBitmap(bm);
//        }
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
                        mKid.setPhoto(strAvatarBase64);

                        Bundle outState = new Bundle();
                        outState.putString("photo", mKid.getPhoto());
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
                    mKid.setPhoto(strAvatarBase64);
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
                    strAvatarBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                    mKid.setPhoto(strAvatarBase64);
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
        mBottomSheetDialog = new BottomSheetDialog(KidCreateUpdateActivity.this);
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
                if (PermissionUtils.checkCameraPermission(KidCreateUpdateActivity.this)) {
                    if (ContextCompat.checkSelfPermission(KidCreateUpdateActivity.this,
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
                            ALog.e("Crop Image Error:", exception.getMessage());
                        }
                    }
                }


            }
        });
        tvChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissBottomSheet();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

        if (TextUtils.isEmpty(edtDateOfBirth.getText().toString().trim())) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_birth_day));
            edtDateOfBirth.requestFocus();
            return true;
        }

        if (!DateTimeUtils.doCheckValidDate(edtDateOfBirth.getText().toString().trim())) {
            DialogUtil.showErrorDialog(this, getString(R.string.error_invalid_birth_day));
            return true;
        }

//        if (!AppCommon.validateEmail(edtPhoneNumber.getText().toString().trim())) {
//            DialogUtil.showErrorDialog(this, getString(R.string.error_empty_phone_number));
//            return true;
//        }

        return false;
    }
}
