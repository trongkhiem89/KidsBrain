package com.kid.brain.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.kid.brain.R;
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.models.Permission;
import com.kid.brain.view.dialog.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static ArrayList<Permission> sPermissions;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final int REQUEST_CODE_FINGERPRINT_PERMISSION = 1;

    @TargetApi(Build.VERSION_CODES.M)
    public static void createPermissionsCheckListIfNeed() {
        if (Build.VERSION.SDK_INT < 23) return;
        Context context = KidApplication.getInstance().getAppContext();
        if (sPermissions == null || sPermissions.size() == 0) {
            sPermissions = new ArrayList<Permission>();
//            sPermissions.add(new Permission(Manifest.permission.RECORD_AUDIO, context.getString(R.string.permission_record_audio)));
//            sPermissions.add(new Permission(Manifest.permission.ACCESS_COARSE_LOCATION, context.getString(R.string.permission_access_coarse_location)));
//            sPermissions.add(new Permission(Manifest.permission.ACCESS_FINE_LOCATION, context.getString(R.string.permission_access_fine_location)));
            sPermissions.add(new Permission(Manifest.permission.READ_CONTACTS, context.getString(R.string.permission_read_contacts)));
//            sPermissions.add(new Permission(Manifest.permission.WRITE_CONTACTS, context.getString(R.string.permission_write_contacts)));
            sPermissions.add(new Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getString(R.string.permission_write_ex_storage)));
            sPermissions.add(new Permission(Manifest.permission.READ_EXTERNAL_STORAGE, context.getString(R.string.permission_read_ex_storage)));
            sPermissions.add(new Permission(Manifest.permission.CAMERA, context.getString(R.string.permission_camera)));
//            sPermissions.add(new Permission(Manifest.permission.GET_ACCOUNTS, context.getString(R.string.permission_get_account)));
        }
        for (Permission item : sPermissions) {
            item.isGranted = context.checkSelfPermission(item.packageName) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermissionsGranted(final Activity activity) {
        if (Build.VERSION.SDK_INT < 23) return true;
        createPermissionsCheckListIfNeed();
        String[] notGrantedList = getPackageNameNotGrantedArray(sPermissions);
        if (notGrantedList != null && notGrantedList.length > 0) {
            String message = activity.getString(R.string.permission_guide);
            for (Permission item : sPermissions) {
                if (!item.isGranted) {
                    message += "\n    " + item.displayName;
                }
            }
            DialogUtil.showConfirmDialog(activity, activity.getString(R.string.dialog_title),
                    message, new DialogUtil.ConfirmDialogOnClickListener() {
                        @Override
                        public void onOKButtonOnClick() {
                            activity.requestPermissions(getPackageNameNotGrantedArray(sPermissions),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }

                        @Override
                        public void onCancelButtonOnClick() {
                            Toast.makeText(activity, activity.getString(R.string.permission_denied_and_guide_to_setting),
                                    Toast.LENGTH_LONG).show();
                            goAppSetting(activity);
                        }
                    });
            return false;
        }
        return true;

    }

    private static String[] getPackageNameNotGrantedArray(ArrayList<Permission> permissions) {
        List<String> list = new ArrayList<String>();
        for (Permission item : permissions) {
            if (!item.isGranted) {
                list.add(item.packageName);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static boolean isGrantedAllPermissions(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, activity.getString(R.string.permission_denied_and_guide_to_setting), Toast.LENGTH_LONG).show();
                goAppSetting(activity);
                return false;
            }
        }
        return true;
    }

    private static void goAppSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
        activity.finish();
    }

    public static void clearCheckList() {
        if (sPermissions != null) {
            sPermissions.clear();
            sPermissions = null;
        }
    }


    /**
     * Check camera permission.
     * @param context
     * @return
     */
    public static boolean checkCameraPermission(final Activity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.error_camera_permission)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(context,
                                        new String[]{Manifest.permission.CAMERA},
                                        Constants.REQUEST_CODE_CAMERA);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.REQUEST_CODE_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }
}
