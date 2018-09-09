package com.kid.brain.managers.application;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kid.brain.R;
import com.kid.brain.managers.listeners.OnCheckDbListener;
import com.kid.brain.models.Kid;
import com.kid.brain.provider.database.DatabaseManager;
import com.kid.brain.provider.request.APIService;
import com.kid.brain.provider.request.RetrofitConfig;
import com.kid.brain.util.LocaleManager;
import com.kid.brain.util.log.ALog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by khiemnt on 11/9/16.
 */

public class KidApplication extends MultiDexApplication {

    private final String TAG = KidApplication.class.getName();
    private static Context mContext;
    private static KidApplication mInstance;
    public static Kid mKidTested;
    private APIService apiService;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseManager.ApplicationContext = getApplicationContext();
        mContext = getApplicationContext();

        try {
            SQLiteDatabase.loadLibs(this);

            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

        } catch (RuntimeException e){
            e.printStackTrace();
        }
        apiService = RetrofitConfig.getInstance(this).getRetrofit().create(APIService.class);

        initImageLoader(getApplicationContext());
    }

    public void initImageLoader(Context context) {
        //  ImageLoaderConfiguration.createDefault(this);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        mContext = base;
        super.attachBaseContext(LocaleManager.setLocale(base));
        ALog.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
        ALog.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

    public Context getAppContext(){
        if (mContext == null) {
            mContext = getApplicationContext();
        }
        return mContext;
    }


    public static KidApplication getInstance() {
        if (mInstance == null) {
            mInstance = new KidApplication();
        }
        return mInstance;
    }

    public APIService getApiService() {
        return apiService;
    }

    public DisplayImageOptions getOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_user)
                .showImageForEmptyUri(R.drawable.icon_user)
                .showImageOnFail(R.drawable.icon_user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    /**
     * Create Kids Brain Database
     * @param keycode Keycode using to open database
     * @param listener Listener open db without keycode is success or fail
     */
    public void checkDatabase(final String keycode, final OnCheckDbListener listener) {
        final DatabaseManager databaseManager = DatabaseManager.getInstance(getAppContext());
        if (!databaseManager.isDbFileExisted()) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    databaseManager.createDb(keycode);
                    if (databaseManager.openDb(keycode)) {
                        listener.onOpenDbSuccess();
                    } else {
                        listener.onOpenDbFailed();
                    }
                }
            };
            new Thread(r).start();
        } else {
            if (databaseManager.openDb(keycode)) {
                listener.onOpenDbSuccess();
            } else {
                listener.onOpenDbFailed();
            }
        }
    }

    /**
     * Only to check db opening
     * @param keycode
     * @param listener
     */
    public void checkDbOpen(final String keycode, final OnCheckDbListener listener){
        final DatabaseManager databaseManager = DatabaseManager.getInstance(getAppContext());
        if (!databaseManager.isDbFileExisted()) {
            listener.onOpenDbFailed();
        } else {
            if (databaseManager.openDb(keycode)) {
                listener.onOpenDbSuccess();
            } else {
                listener.onOpenDbFailed();
            }
        }
    }
}
