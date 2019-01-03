package com.kid.brain.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kid.brain.R;
import com.kid.brain.managers.listeners.IDialogOkListener;
import com.kid.brain.managers.listeners.IShare;

/**
 * Created by khiemnt on 4/20/17.
 */

public class ShareSocialDialog extends AlertDialog.Builder implements View.OnClickListener {

    private IShare iShare;
    private AlertDialog dialog;

    public ShareSocialDialog(@NonNull Context context, IShare iShare) {
        super(context);
        this.iShare = iShare;
        doSetupView();
    }

    public ShareSocialDialog(@NonNull Context context, @StyleRes int themeResId, IShare iShare) {
        super(context, themeResId);
        this.iShare = iShare;
        doSetupView();
    }

    @Override
    public AlertDialog create() {
        this.dialog = super.create();
        return this.dialog;
    }

    private void doSetupView() {
        View root = View.inflate(getContext(), R.layout.dialog_share, null);
        setView(root);
        setTitle(getContext().getString(R.string.str_share));
        final TextView tvEmail = root.findViewById(R.id.tvEmail);
        final TextView tvFacebook = root.findViewById(R.id.tvFacebook);
        final TextView tvOther = root.findViewById(R.id.tvOther);
        tvEmail.setOnClickListener(this);
        tvFacebook.setOnClickListener(this);
        tvOther.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvEmail) {
            iShare.shareEmail();
            closeDialog();
        } else if (id == R.id.tvFacebook) {
            iShare.shareFacebook();
            closeDialog();
        } else if (id == R.id.tvOther) {
            iShare.shareOther();
            closeDialog();
        }
    }

    private void closeDialog() {
        this.dialog.dismiss();
    }
}
