package com.kid.brain.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kid.brain.R;
import com.kid.brain.managers.listeners.IDialogOkListener;

/**
 * Created by khiemnt on 4/20/17.
 */

public class ChangePasswordDialog extends AlertDialog.Builder {

    private IDialogOkListener iDialogOkListener;

    public ChangePasswordDialog(@NonNull Context context, IDialogOkListener iDialogOkListener) {
        super(context);
        this.iDialogOkListener = iDialogOkListener;
        doSetupView();
    }

    public ChangePasswordDialog(@NonNull Context context, @StyleRes int themeResId, IDialogOkListener iDialogOkListener) {
        super(context, themeResId);
        this.iDialogOkListener = iDialogOkListener;
        doSetupView();
    }

    private void doSetupView() {
        View root = View.inflate(getContext(), R.layout.dialog_change_password, null);
        setView(root);
        final EditText edtCurrentPassword = root.findViewById(R.id.edtCurrentPassword);
        final EditText edtNewPassword = root.findViewById(R.id.edtNewPassword);
        final EditText edtNewPasswordConfirm = root.findViewById(R.id.edtNewPasswordConfirm);

        setMessage(getContext().getString(R.string.str_change_password));
        setNegativeButton(getContext().getString(R.string.dialog_confirm_cancel), null);
        setPositiveButton(getContext().getString(R.string.btn_change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String currentPassword = edtCurrentPassword.getText().toString();
                String newPassword = edtNewPassword.getText().toString();
                String newPasswordConfirm = edtNewPasswordConfirm.getText().toString();

                if (!invalid(currentPassword, newPassword, newPasswordConfirm)) {
                    String[] data = {currentPassword, newPassword};
                    iDialogOkListener.onOk(data);
                    dialogInterface.dismiss();
                }
            }
        });
    }

    private boolean invalid(String current, String password, String passwordConfirm) {

        if (TextUtils.isEmpty(current)) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_empty_password), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_empty_new_password), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_empty_password_confirm), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_password_not_match), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
