package com.kid.brain.managers.application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.kid.brain.provider.request.model.Error;
import com.kid.brain.R;
import com.kid.brain.activies.authorization.LoginActivity;
import com.kid.brain.view.dialog.DialogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created by khiemnt on 9/7/2015.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;

    /**
     * Fragment Transaction not send data
     * @param context   Context
     * @param frmName   Fragment Name Source target
     * @param container Container Fragment being replace
     * @param isSaveToBackStack Boolean is save or not save back stack
     * @param keySaveToBackStack Key fragment store in back stack
     */
    public void onTransactionFragment(AppCompatActivity context, Fragment frmName, int container, boolean isSaveToBackStack, String keySaveToBackStack){
        FragmentManager frmManager = context.getSupportFragmentManager();
        FragmentTransaction frmTransaction = frmManager.beginTransaction();
        if( isSaveToBackStack ) frmTransaction.addToBackStack(keySaveToBackStack);
        frmTransaction.setCustomAnimations(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left,
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
        frmTransaction.replace(container, frmName);
        frmTransaction.commit();
    }

    /**
     * Fragment Transaction and send data
     * @param context   Context
     * @param frmName   Fragment Name Source target
     * @param container Container Fragment being replace
     * @param isSaveToBackStack Boolean is save or not save back stack
     * @param keySaveToBackStack Key fragment store in back stack
     * @param data Object Serializable data send
     */
    public void onTransactionFragment(AppCompatActivity context, Fragment frmName, int container, boolean isSaveToBackStack, String keySaveToBackStack, Object data){
        FragmentManager frmManager = context.getSupportFragmentManager();
        FragmentTransaction frmTransaction = frmManager.beginTransaction();
        if( isSaveToBackStack ) frmTransaction.addToBackStack(keySaveToBackStack);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) data);
        frmName.setArguments(bundle);
        frmTransaction.setCustomAnimations(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left,
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
        frmTransaction.replace(container, frmName);
        frmTransaction.commit();
    }

    public void onTransactionFragment(AppCompatActivity context, int container, Fragment frmName){
        context.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left,
                    R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right)
                .replace(container, frmName)
                .commit();
    }

    public Fragment findFragmentBackStack(AppCompatActivity context, String TAG){
        FragmentManager frmManager = context.getSupportFragmentManager();
        Fragment currentFragment = frmManager.findFragmentByTag(TAG);
        return currentFragment;
    }

    public void popBackStack(AppCompatActivity context){
        FragmentManager frmManager = context.getSupportFragmentManager();
        if(frmManager.getBackStackEntryCount() > 0){
            frmManager.popBackStack();
            return;
        }
    }

    public void clearBackStack(AppCompatActivity context){
        FragmentManager frmManager = context.getSupportFragmentManager();
        int count = frmManager.getBackStackEntryCount();
        if( count > 0){
            for (int i = 0; i < count; i++) {
                frmManager.popBackStack();
            }
        }
    }

    public Fragment currentFragmentShowing(AppCompatActivity context, int container){
        FragmentManager frmManager = context.getSupportFragmentManager();
        Fragment frm = frmManager.findFragmentById(container);
        return frm;
    }

    public void showProgressBar(Context context){
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getString(R.string.dialog_message_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showProgressBar(Context context, String message){
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setMessage(String message){
        if (null != progressDialog && progressDialog.isShowing()){
            progressDialog.setMessage(message);
        }
    }

    public void dismissProgressBar(){
        if (null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void showErrorNetWork(){
        Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
//                    .setAction("Undo", mOnClickListener)
                .setActionTextColor(Color.RED)
                .show();
    }

    protected void showAlertDialog(String title, String message) {
        final AlertDialog.Builder dialog = DialogUtil.createCustomOkDialog(getActivity(), title, message);
        dialog.show();
    }

    protected void showAlertDialog(String title, String message, DialogUtil.DialogOnClickListener onClickYesListener) {
        final AlertDialog.Builder dialog = DialogUtil.createCustomOkDialog(getActivity(), title, message, onClickYesListener);
        dialog.show();
    }

    public void goBackToLoginActivity(Activity activity) {
        Intent i = new Intent(activity, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(i);
        activity.finish();
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

    public void showErrorDialog(Error error){
        if (null != error){
//            if (!TextUtils.isEmpty(error.getDetail())){
//                showAlertDialog(getString(R.string.dialog_title), error.getDetail());
//            } else {
                showAlertDialog(getString(R.string.dialog_title), error.getMessage());
//            }
        }
    }
}
