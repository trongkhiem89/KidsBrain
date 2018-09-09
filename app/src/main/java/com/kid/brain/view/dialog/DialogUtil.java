package com.kid.brain.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.kid.brain.R;


/**
 * Created by khiemnt on 11/4/16.
 */
public class DialogUtil {

    private Context context;
    private static DialogUtil dialog;

    public DialogUtil(Context context) {
        this.context = context;
    }

    private static Window.Callback getCallback(final Activity activity, final AppCompatDialog dialog) {
        return new Window.Callback() {
            @Override
            public boolean onSearchRequested(SearchEvent searchEvent) {
                return false;
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
                return null;
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                activity.onUserInteraction();
                dialog.dispatchKeyEvent(keyEvent);
                return false;
            }

            @Override
            public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
                activity.onUserInteraction();
                dialog.dispatchKeyShortcutEvent(keyEvent);
                return false;
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                activity.onUserInteraction();
                dialog.dispatchTouchEvent(motionEvent);
                return false;
            }

            @Override
            public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
                activity.onUserInteraction();
                dialog.dispatchTrackballEvent(motionEvent);
                return false;
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
                activity.onUserInteraction();
                dialog.dispatchGenericMotionEvent(motionEvent);
                return false;
            }

            @Override
            public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
                activity.onUserInteraction();
                dialog.dispatchPopulateAccessibilityEvent(accessibilityEvent);
                return false;
            }

            @Nullable
            @Override
            public View onCreatePanelView(int i) {
                return null;
            }

            @Override
            public boolean onCreatePanelMenu(int i, Menu menu) {
                return false;
            }

            @Override
            public boolean onPreparePanel(int i, View view, Menu menu) {
                return false;
            }

            @Override
            public boolean onMenuOpened(int i, Menu menu) {
                return false;
            }

            @Override
            public boolean onMenuItemSelected(int i, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {

            }

            @Override
            public void onContentChanged() {
            }

            @Override
            public void onWindowFocusChanged(boolean b) {

            }

            @Override
            public void onAttachedToWindow() {

            }

            @Override
            public void onDetachedFromWindow() {

            }

            @Override
            public void onPanelClosed(int i, Menu menu) {

            }

            @Override
            public boolean onSearchRequested() {
                return false;
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                return null;
            }

            @Override
            public void onActionModeStarted(ActionMode actionMode) {

            }

            @Override
            public void onActionModeFinished(ActionMode actionMode) {

            }
        };
    }

    public static void showErrorDialog(Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(R.string.dialog_title));
        dialog.setMessage(message);
        dialog.setPositiveButton(context.getText(R.string.dialog_confirm_ok), null);
        dialog.show();
    }

    public static AppCompatDialog showConfirmDialog(Activity context, int titleResId, int messageResId,
                                                    final ConfirmDialogOnClickListener listener) {
        String title = context.getResources().getString(titleResId);
        String message = context.getResources().getString(messageResId);

        return showConfirmDialog(context, title, message, listener);
    }

    public static AppCompatDialog showConfirmDialog(Activity context, int titleResId, int messageResId, int okResId,
                                                    int cancelResId, final ConfirmDialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResId).setMessage(messageResId).setCancelable(false)
                .setPositiveButton(okResId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null)
                            listener.onOKButtonOnClick();
                    }
                }).setNegativeButton(cancelResId, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (listener != null)
                    listener.onCancelButtonOnClick();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().setCallback(getCallback(context, alert));
        alert.show();

        return alert;
    }
    /**
     * Show confirm dialog (Yes/No dialog)
     *
     * @param context  context that dialog will be shown
     * @param title    title of dialog
     * @param message  message of dialog
     * @param listener handle event when click button Yes/No
     * @return
     */
    public static AppCompatDialog showConfirmDialog(Activity context, String title, String message,
                                                    final ConfirmDialogOnClickListener listener) {
        // check context. If not check here, sometimes it can be crashed
        if (context == null)
            return null;
        Activity activity = (Activity) context;
        if (activity.isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null)
                            listener.onOKButtonOnClick();
                    }
                }).setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (listener != null)
                    listener.onCancelButtonOnClick();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().setCallback(getCallback(context, alert));
        alert.show();

        return alert;
    }


    /**
     * Khiemnt : Apply material for dialog v7
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     * @return
     */
    public static AlertDialog.Builder createCustomOkDialog(final Activity context, final String title, final String content, final DialogOnClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onOKButtonOnClick();
                        }
                    }
                });
        return alertDialog;
    }

    public static AlertDialog.Builder createCustomOkDialog(final Activity context, final String title, final String content, final String buttonName, final DialogOnClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onOKButtonOnClick();
                        }
                    }
                });
        return alertDialog;
    }

    public static AlertDialog.Builder createCustomOkDialog(final Activity context, final String title, final String content) {
        return createCustomOkDialog(context, title, content, new DialogOnClickListener() {
            @Override
            public void onOKButtonOnClick() {

            }
        });
    }

    public static AlertDialog.Builder createCustomConfirmDialog(final Activity context, final String title, final String content, final ConfirmDialogOnClickListener listener) {
        return createCustomConfirmDialog(context, title, content, context.getString(R.string.btn_ok), context.getString(R.string.btn_cancel), listener);
    }

    /**
     * Khiemnt : Apply material for dialog v7
     *
     * @param context
     * @param title
     * @param content
     * @param confirmText
     * @param cancelText
     * @param listener
     * @return
     */
    public static AlertDialog.Builder createCustomConfirmDialog(final Activity context, final String title, final String content,
                                                                String confirmText, String cancelText, final ConfirmDialogOnClickListener listener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onCancelButtonOnClick();
                        }
                    }
                })
                .setPositiveButton(confirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onOKButtonOnClick();
                        }
                    }
                });
        return alertDialog;
    }

    /**
     * using in confirm dialog
     */
    public interface InputDialogOnClickListener {
         void onOKButtonOnClick(String text);

         void onCancelButtonOnClick();
    }

    /**
     * using in confirm dialog
     */
    public interface ConfirmDialogOnClickListener {
         void onOKButtonOnClick();

         void onCancelButtonOnClick();
    }

    /**
     * using in normal dialog
     */
    public interface DialogOnClickListener {
         void onOKButtonOnClick();
    }

    public static AlertDialog.Builder showDialogSelectList(Context context, String title, final String[] data, final IDialogOnSelectItem callBack ){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setSingleChoiceItems(data, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.onSelectedItem(data[which]);
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                callBack.onCancel();
                dialog.dismiss();
            }
        });
        return builder;
    }

    public static AlertDialog.Builder showDialogFilterDocument(Context context, String title, final String[] data, int positionItemChecked, final IDialogOnSelectItem callBack ){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setSingleChoiceItems(data, positionItemChecked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.onSelectedItem(String.valueOf(which));
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                callBack.onCancel();
                dialog.dismiss();
            }
        });
        return builder;
    }

    public interface IDialogOnSelectItem{
        void onSelectedItem(String data);
        void onCancel();
    }
}
