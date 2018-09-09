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
import com.kid.brain.util.AppCommon;

/**
 * Created by khiemnt on 4/20/17.
 */

public class InputDialog extends AlertDialog.Builder {

    private IDialogOkListener iDialogOkListener;

    public InputDialog(@NonNull Context context, IDialogOkListener iDialogOkListener) {
        super(context);
        this.iDialogOkListener = iDialogOkListener;
        doSetupView();
    }

    public InputDialog(@NonNull Context context, @StyleRes int themeResId, IDialogOkListener iDialogOkListener) {
        super(context, themeResId);
        this.iDialogOkListener = iDialogOkListener;
        doSetupView();
    }

    private void doSetupView() {
        View root = View.inflate(getContext(), R.layout.dialog_input, null);
        setView(root);
        final EditText edtEmail = root.findViewById(R.id.edtEmail);
        setMessage(getContext().getString(R.string.str_send_result));
        setNegativeButton(getContext().getString(R.string.dialog_confirm_cancel), null);
        setPositiveButton(getContext().getString(R.string.btn_send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = edtEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show();
                } else if (!AppCommon.validateEmail(email)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.error_valid_email), Toast.LENGTH_SHORT).show();
                } else {
                    iDialogOkListener.onOk(email);
                    dialogInterface.dismiss();
                }
            }
        });
    }
}
