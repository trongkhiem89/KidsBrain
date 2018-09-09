package com.kid.brain.view.dialog;

import android.text.TextUtils;

import org.androidannotations.annotations.EBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by khiemnt on 4/20/17.
 */

@EBean(scope = EBean.Scope.Singleton)
public class DateTimeUtils {

    public static final String DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String DATE_LOCAL_FORMAT = "dd-MM-yyyy";
    private static final String DATE_LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";

    /**
     * Lấy ngày tháng hiện tại
     * @return dd/MM/yyy hh:mm
     */
    public String getCurrentDateTime() {
        String separator = "/";

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH),
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE);

        StringBuilder resultDate = new StringBuilder();
        resultDate.append(day).append(separator);
        resultDate.append(month + 1).append(separator);
        resultDate.append(year).append(" ");
        resultDate.append(hour).append(":").append(minute);

        return resultDate.toString();
    }

    /**
     * Lấy ngày tháng hiện tại
     * @return dd/MM/yyyy
     */
    public String getCurrentDate() {
        String separator = "-";

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder resultDate = new StringBuilder();
        resultDate.append(day).append(separator);
        resultDate.append(month + 1).append(separator);
        resultDate.append(year);

        return resultDate.toString();
    }


    /**
     * Lấy ngày đầu tiên của tháng hiện tại
     * @return dd/MM/yyyy
     */
    public String getFirstDayOfMonth() {
        String separator = "-";

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder resultDate = new StringBuilder();
        resultDate.append(day).append(separator);
        resultDate.append(month + 1).append(separator);
        resultDate.append(year);

        return resultDate.toString();
    }

    /**
     * Lấy ngày tháng trước
     * @return dd/MM/yyyy
     */
    public String getPreviousMonthDate() {
        String separator = "-";

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder resultDate = new StringBuilder();
        resultDate.append(day).append(separator);
        resultDate.append(month).append(separator);
        resultDate.append(year);

        return resultDate.toString();
    }

    /**
     * Convert String date to date
     * @param dateString String date
     * @return dd/MM/yyyy
     */
    public static Date strToDate(String dateString) {
        if (TextUtils.isEmpty(dateString))
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_LOCAL_FORMAT);
        try {
            Date date = sdf.parse(dateString);
            return date;
        } catch (ParseException pe) {
            return new Date();
        }
    }

    /**
     * dd-MM-yyyy
     * @param dateString
     * @return yyyy-MM-dd
     */
    public String convertStringDateToUTC(String dateString) {
        String separator = "-";
        if (TextUtils.isEmpty(dateString)) return dateString;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_LOCAL_FORMAT);
        try {
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH);

            StringBuilder resultDate = new StringBuilder();
            resultDate.append(year).append(separator);
            resultDate.append(month+1).append(separator);
            resultDate.append(day);

            return resultDate.toString();
        } catch (ParseException pe) {
            return dateString;
        }
    }


    public boolean doCheckValidDate(String startDate, String endDate) {

        try {
            Date dateStart = strToDate(startDate);
            Date dateEnd = strToDate(endDate);

            if (dateStart == null || dateEnd == null) return false;

            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTimeZone(TimeZone.getDefault());
            calendarStart.setTime(dateStart);

            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTimeZone(TimeZone.getDefault());
            calendarEnd.setTime(dateEnd);

            long diff = calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis();
            float dayCount = (float) diff / (24 * 60 * 60 * 1000);

            return dayCount > 0;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean doCheckValidDate(String inputDate) {

        try {
            Date dateStart = strToDate(inputDate);
            Date dateNow = new Date();

            if (dateStart == null) return false;

            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTimeZone(TimeZone.getDefault());
            calendarStart.setTime(dateStart);

            Calendar calendarNow = Calendar.getInstance();
            calendarNow.setTimeZone(TimeZone.getDefault());
            calendarNow.setTime(dateNow);

            long diff = calendarNow.getTimeInMillis() - calendarStart.getTimeInMillis();
            float dayCount = (float) diff / (24 * 60 * 60 * 1000);

            return dayCount > 0;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static String convertUTCToLocal(String timeUTC){
        if (TextUtils.isEmpty(timeUTC)) return "";

        SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_UTC_FORMAT);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat localFormat = new SimpleDateFormat(DATE_LOCAL_FORMAT);
        try {
            Date d = utcFormat.parse(timeUTC);
            return localFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeUTC;
    }

    public static String convertLocalToUTC(String timeLocal){
        if (TextUtils.isEmpty(timeLocal)) return "";

        SimpleDateFormat localFormat = new SimpleDateFormat(DATE_LOCAL_FORMAT);
        SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_UTC_FORMAT);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date d = localFormat.parse(timeLocal);
            return utcFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeLocal;
    }

    public static String convertUTCToLocalDateTime(String timeUTC){
        if (TextUtils.isEmpty(timeUTC)) return "";

        SimpleDateFormat utcFormat = new SimpleDateFormat(DATE_UTC_FORMAT);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat localFormat = new SimpleDateFormat(DATE_LOCAL_DATE_TIME_FORMAT);
        try {
            Date d = utcFormat.parse(timeUTC);
            return localFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeUTC;
    }
}
