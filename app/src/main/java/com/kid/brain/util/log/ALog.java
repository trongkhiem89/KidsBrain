package com.kid.brain.util.log;

import android.util.Log;

/**
 * Using this ALog instead of class Log - To disable all log: set value of
 * Define.ENABLE_LOG to FALSE
 */
public class ALog {

	public static void d(String tag, String msg) {
		if (DebugOptions.ENABLE_LOG) {
			Log.d(">>>>> " + tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DebugOptions.ENABLE_LOG) {
			Log.e(">>>>> " + tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DebugOptions.ENABLE_LOG) {
			Log.i(">>>>> " + tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DebugOptions.ENABLE_LOG) {
			Log.v(">>>>> " + tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DebugOptions.ENABLE_LOG) {
			Log.w(">>>>> " + tag, msg);
		}
	}

	public static void println(int priority, String tag, String msg) {
		if (DebugOptions.ENABLE_LOG) {
			Log.println(priority, tag, msg);
		}
	}

}
