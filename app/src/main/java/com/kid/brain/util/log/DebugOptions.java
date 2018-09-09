package com.kid.brain.util.log;


import com.kid.brain.BuildConfig;

/**
 * Config all debug options in application. In release version, all value must
 * set to FALSE
 */
public class DebugOptions {
	/**
	 * Set enable/disable log in logcat
	 */
	public static final boolean ENABLE_LOG = BuildConfig.DEBUG;

	public static final boolean ENABLE_DEBUG = BuildConfig.DEBUG;
	
	public static final boolean ENABLE_FOR_DEV = true;
}
