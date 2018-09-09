package com.kid.brain.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;

import com.kid.brain.R;
import com.kid.brain.managers.listeners.IOnDatePickerChangeListener;
import com.kid.brain.util.log.ALog;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by khiemnt on 4/20/17.
 */

public class DatePickerDialog extends AlertDialog.Builder implements DatePicker.OnDateChangedListener {

    private String resultDate;
    private IOnDatePickerChangeListener onDatePickerChangeListener;
    private Date mDate;
    private String separator = "-";

    public DatePickerDialog(@NonNull Context context, String strDate, IOnDatePickerChangeListener onDatePickerChangeListener) {
        super(context);
        this.mDate = DateTimeUtils.strToDate(strDate);
        this.onDatePickerChangeListener = onDatePickerChangeListener;
        doSetupView();
    }

    public DatePickerDialog(@NonNull Context context, @StyleRes int themeResId, String strDate, IOnDatePickerChangeListener onDatePickerChangeListener) {
        super(context, themeResId);
        this.mDate = DateTimeUtils.strToDate(strDate);
        this.onDatePickerChangeListener = onDatePickerChangeListener;
        doSetupView();
    }

    private void doSetupView() {
        setView(createDatePicker());
        setNegativeButton(getContext().getString(R.string.dialog_confirm_cancel), null);
        setPositiveButton(getContext().getString(R.string.dialog_confirm_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onDatePickerChangeListener.onSetDate(resultDate);
                dialogInterface.dismiss();
            }
        });
    }

    private DatePicker createDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (mDate != null) {
            calendar.setTime(mDate);
        }

        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePicker datePicker = new DatePicker(getContext());
        datePicker.init(year, month, day, this);

        resultDate = day + separator + (month + 1) + separator + year;

        return datePicker;
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        resultDate = day + separator + (month + 1) + separator + year;
        ALog.e("onDateChanged:", resultDate);
    }
}
