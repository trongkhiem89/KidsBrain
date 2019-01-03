package com.kid.brain.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;

import com.kid.brain.R;
import com.kid.brain.managers.application.BaseAppCompatActivity;
import com.kid.brain.view.dialog.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class EmailUtil {

	public static void sendFeedback(BaseAppCompatActivity parent, String mailBody) {
		String[] recipients = new String[] { "sucmanhbentrong@gmail.com" };
		EmailUtil.sendEmail(parent,
							recipients,
							"KidsBrains feedback",
							mailBody);
	}

	public static void sendEmail(Activity parent, String[] recipients, String subject, String htmlMessage) {
		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("message/rfc822");
		if (recipients != null && recipients.length > 0) {
			mailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
		}
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		mailIntent.putExtra(Intent.EXTRA_TEXT, htmlMessage);
		List<ResolveInfo> resInfo = parent.getPackageManager().queryIntentActivities(mailIntent, 0);
		if (resInfo == null || resInfo.size() <= 0) {
			DialogUtil.showErrorDialog(parent, parent.getString(R.string.str_no_email_app));
			return;
		}
		Intent intent = Intent.createChooser(mailIntent, parent.getString(R.string.str_send_via));
		parent.startActivityForResult(intent, 0);
	}
	public static void sendEmail2(Activity parent, String recipients, String subject, String htmlMessage) {
		// NOTE that recipients list is separated by comma
		if (recipients == null) {
			recipients = "";
		}
		if (subject == null) {
			subject = "";
		}
		if (htmlMessage == null) {
			htmlMessage = "";
		}
		Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
		String uriText = "mailto:%s" + 
				          "?subject=%s" + 
				          "&body=%s";
		uriText = String.format(uriText, recipients, subject, Html.fromHtml(htmlMessage));
		Uri uri = Uri.parse(uriText);
		mailIntent.setData(uri);
		parent.startActivityForResult(Intent.createChooser(mailIntent, parent.getString(R.string.str_send_via)), 0);
	}

	public static void sendBackupItems(Activity parent, Uri backupFileUri, String msgSubject, String msgBody) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		//emailIntent.setType(backupFileMimeType);
		emailIntent.setType("vnd.android.cursor.dir/email");
		//emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, msgSubject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, msgBody);
		emailIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri);
		emailIntent.putExtra(Constants.EXTRA_OPEN_IN_APP, true);
		parent.startActivityForResult(Intent.createChooser(emailIntent, parent.getString(R.string.str_sending)), Constants.REQUEST_CODE_ATTACH_DATABASE);
	}

	public static void sendMail(Context context, Uri attachmentUri, String subject, String body, boolean isBodyInHtml) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, isBodyInHtml ? Html.fromHtml(body) : body);
		emailIntent.putExtra(Constants.EXTRA_OPEN_IN_APP, true);
		if (attachmentUri != null) {
			emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
		}
		context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.str_send_via)));
	}

    public static void sendMail(Activity activity, Uri attachmentUri, String subject, String body, boolean isBodyInHtml) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, isBodyInHtml ? Html.fromHtml(body) : body);
        emailIntent.putExtra(Constants.EXTRA_OPEN_IN_APP, true);
        if (attachmentUri != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
        }
        activity.startActivityForResult(Intent.createChooser(emailIntent, activity.getString(R.string.str_send_via)), Constants.REQUEST_CODE_ATTACH_IMAGE);
    }

	public static void sendItemData(Context context, ArrayList<Uri> attachmentUris, String subject, String body, boolean isBodyInHtml) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, isBodyInHtml ? Html.fromHtml(body) : body);
		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUris);
		emailIntent.putExtra(Constants.EXTRA_OPEN_IN_APP, true);
		context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.str_send_via)));
	}

}
