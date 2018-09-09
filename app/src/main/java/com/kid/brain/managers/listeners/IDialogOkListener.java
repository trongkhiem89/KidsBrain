package com.kid.brain.managers.listeners;

/**
 * Created by khiemnt on 4/17/17.
 */

public interface IDialogOkListener {
    <T> void onOk(T object);
    void onCancel();
}
