package com.kid.brain.provider.database;

import com.kid.brain.util.log.ALog;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class SqliteCipherUtil {

    public static String singleValueFromQuery(SQLiteDatabase database, String query) {
        Cursor cursor = database.rawQuery(query, new String[]{});
        String value = "";
        if (cursor != null) {
            cursor.moveToFirst();
            value = cursor.getString(0);
            cursor.close();
        }
        return value;
    }

    public static int singleIntegerValueFromQuery(SQLiteDatabase database, String query) {
        Cursor cursor = database.rawQuery(query, new String[]{});
        int value = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            value = cursor.getInt(0);
            cursor.close();
        }
        return value;
    }

    public static void checkMigrateDatabaseVersion(SQLiteDatabase database) {
        String migrateResult = SqliteCipherUtil.singleValueFromQuery(database, "PRAGMA cipher_migrate;");
        ALog.e("checkMigrateDatabaseVersion", migrateResult + "");
    }
}