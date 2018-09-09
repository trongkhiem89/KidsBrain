package com.kid.brain.provider.database;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SQLCipherHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLCipherHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String SQL_COMMENT = "--";

    private Context mContext;
    private String mDbName;
    private boolean mOldDbAlreadyExisted;

    public SQLCipherHelper(Context context) {
        // super(context, DbHelper.DB_NAME, null, DATABASE_VERSION);
        // mContext = context;
        this(context, DatabaseManager.DB_NAME);
    }

    public SQLCipherHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);

        mContext = context;
        mDbName = dbName;
    }

    public SQLCipherHelper(Context context, boolean oldDbAlreadyExisted) {
        this(context, DatabaseManager.DB_NAME, oldDbAlreadyExisted);
    }

    public SQLCipherHelper(Context context, String dbName,
                           boolean oldDbAlreadyExisted) {
        super(context, dbName, null, DATABASE_VERSION);

        mContext = context;
        mDbName = dbName;
        mOldDbAlreadyExisted = oldDbAlreadyExisted;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "onCreate()-db.getVersion=" + db.getVersion());
        if (!DatabaseManager.DB_NAME.equals(mDbName) || mOldDbAlreadyExisted) {
            return;
        }

        try {
            String[] assetSqlScripts = {"create_default_data.sql"};
            for (int i = 0; i < assetSqlScripts.length; i++) {
                runAssetSqlScript(assetSqlScripts[i], db);
            }
            Log.i(TAG, "IN onCreate() runAssetSqlScript() CALLED!");
        } catch (IOException ioe) {
            Log.e(TAG,
                    "IN onCreate() IOException CALLING runAssetSqlScript():",
                    ioe);
        }
    }

    private void runAssetSqlScript(String assetSqlScriptName, SQLiteDatabase db)
            throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(mContext
                .getAssets().open(assetSqlScriptName)));

        String stmt_str = null;
        String sql = "";
        String tmp;
        while ((stmt_str = br.readLine()) != null) {
            tmp = stmt_str.trim();
            if (tmp.length() <= 0 || tmp.startsWith(SQL_COMMENT)) {
                continue;
            } else {
                sql += " " + stmt_str;
            }

            // Execute script if it ends with ;
            if (tmp.endsWith(";")) {
                try {
                    db.execSQL(sql);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // For next statement in the script
                sql = "";
            }
        }

        br.close();
        br = null;
        System.gc();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "IN onUpgrade() oldVersion=" + oldVersion);
        Log.i(TAG, "IN onUpgrade() newVersion=" + newVersion);
        try {
            runAssetSqlScript("upgrade_db.sql", db);
            Log.i(TAG, "IN onUpgrade() runAssetSqlScript() CALLED!");
        } catch (IOException ioe) {
            Log.e(TAG,
                    "IN onUpgrade() IOException CALLING runAssetSqlScript():",
                    ioe);
        }
    }

}
