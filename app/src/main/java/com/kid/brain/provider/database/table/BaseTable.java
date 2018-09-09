package com.kid.brain.provider.database.table;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.provider.database.DatabaseManager;
import com.kid.brain.util.PwdUtil;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.UUID;

public abstract class BaseTable {
    public static final String BOOL_Y = "Y";
    public static final String BOOL_N = "N";
    public static final String STATUS_DELETED = "deleted";
    public static final String STATUS_ACTIVE = "active";

    public static String convertBooleanToText(boolean b) {
        if (b) {
            return BOOL_Y;
        } else {
            return BOOL_N;
        }
    }

    public static boolean convertTextToBoolean(String text) {
        if (BOOL_Y.equals(text)) {
            return true;
        } else {
            return false;
        }
    }

    // Phathv add for FE
    @SuppressLint("DefaultLocale")
    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

    public static String genRandomIdUsingCurrentDb() {
        return genRandomId(DatabaseManager.getInstance().getDb());
    }

    public static String genRandomId(SQLiteDatabase db) {
        String randomIdSql = "SELECT hex(randomblob(8))";
        Cursor randomIdCursor = null;
        try {
            randomIdCursor = db.rawQuery(randomIdSql, null);
            if (randomIdCursor.moveToFirst()) {
                String randomId = randomIdCursor.getString(0);
                randomIdCursor.close();
                return randomId;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot generate a random id string from database.");
        } finally {
            if (randomIdCursor != null) {
                randomIdCursor.close();
            }
        }
    }

    private Context mContext;

    private SQLiteDatabase mDatabase;

    public BaseTable(Context context) {
        if (context == null) {
            context = KidApplication.getInstance().getAppContext();
        }

        this.mDatabase = DatabaseManager.getInstance(context).getDb();
        this.mContext = context;
    }

    /**
     * Only use when access cloud database.
     *
     * @param db
     */
    public BaseTable(SQLiteDatabase db) {
        mDatabase = db;
        mContext = null;
    }

    public void setDb(SQLiteDatabase db) {
        mDatabase = db;
    }

    public Context getContext() {
        return mContext;
    }

    public SQLiteDatabase getDatabase() {
        if (mContext == null) {
            mContext = KidApplication.getInstance().getAppContext();
        }

        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = DatabaseManager.getInstance(mContext).getDb();
            if (mDatabase == null || !mDatabase.isOpen()) {
                String keycode = PwdUtil.genSimplePassword();
                KidPreference.saveValue(KidPreference.KEY_PIN, keycode);
                DatabaseManager.getInstance().openDb(keycode);
                mDatabase = DatabaseManager.getInstance(mContext).getDb();
            }
        }

        return mDatabase;
    }

    public String genRandom16BytesHexString() {
        String randomIdSql = "SELECT hex(randomblob(16))";
        Cursor randomIdCursor = null;
        try {
            randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
            if (randomIdCursor.moveToFirst()) {
                String randomId = randomIdCursor.getString(0);
                randomIdCursor.close();
                return randomId;
            } else
                return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (randomIdCursor != null) {
                randomIdCursor.close();
            }
        }
    }

    public String genRandom24BytesHexString() {
        String randomIdSql = "SELECT hex(randomblob(24))";
        Cursor randomIdCursor = null;
        try {
            randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
            if (randomIdCursor.moveToFirst()) {
                String randomId = randomIdCursor.getString(0);
                randomIdCursor.close();
                return randomId;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot generate a random id string from database.");
        } finally {
            if (randomIdCursor != null) {
                randomIdCursor.close();
            }
        }
    }

    public String genRandom32BytesHexString() {
        String randomIdSql = "SELECT hex(randomblob(32))";
        Cursor randomIdCursor = null;
        try {
            randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
            if (randomIdCursor.moveToFirst()) {
                String randomId = randomIdCursor.getString(0);
                return randomId;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot generate a random id string from database.");
        } finally {
            if (randomIdCursor != null) {
                randomIdCursor.close();
            }
        }
    }

    public String genRandom8BytesHexString() {
        String randomIdSql = "SELECT hex(randomblob(8))";
        Cursor randomIdCursor = null;
        try {
            randomIdCursor = getDatabase().rawQuery(randomIdSql, null);
            if (randomIdCursor.moveToFirst()) {
                String randomId = randomIdCursor.getString(0);
                randomIdCursor.close();
                return randomId;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (randomIdCursor != null) {
                randomIdCursor.close();
            }
        }
    }

    public String genRandomId() {
        return genRandomId(getDatabase());
    }

    public String getCurrentDateTime() {
        String currentDateTimeSql = "SELECT datetime('now')";
        Cursor currentDateTimeCursor = null;
        try {
            currentDateTimeCursor = getDatabase().rawQuery(currentDateTimeSql, null);
            if (currentDateTimeCursor.moveToFirst()) {
                String currentDateTimeStr = currentDateTimeCursor.getString(0);
                currentDateTimeCursor.close();
                return currentDateTimeStr;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot get current date and time from database.");
        } finally {
            if (currentDateTimeCursor != null) {
                currentDateTimeCursor.close();
            }
        }
    }

    /**
     * Only use when table has COLUMN_SYNC_STATUS and COLUMN_OBJECT_ID
     *
     * @param tableName
     * @return
     */
    /*protected List<Entity> getItemSimpleInfos(String tableName) {
        List<Entity> itemList = new ArrayList<Entity>();
        Cursor itemsCursor = null;
        try {
            String sqlQuery = "SELECT %s, %s, %s, %s, %s FROM %s";
            sqlQuery = String.format(sqlQuery, COLUMN_ID, COLUMN_STATUS, COLUMN_LAST_MODIFIED, COLUMN_OBJECT_ID,
                    COLUMN_SYNC_STATUS, tableName);
            if (FileTable.TABLE_NAME.equals(tableName)) {
                sqlQuery = "SELECT %s, %s, %s, %s, %s, %s, %s FROM %s";
                sqlQuery = String.format(sqlQuery, COLUMN_ID, COLUMN_STATUS, COLUMN_LAST_MODIFIED, COLUMN_OBJECT_ID,
                        COLUMN_SYNC_STATUS, FileTable.COLUMN_TYPE, FileTable.COLUMN_PATH, tableName);
            }

            itemsCursor = getDatabase().rawQuery(sqlQuery, null);
            if (itemsCursor.moveToFirst()) {
                do {
                    Entity item = new Entity() {
                    };

                    String itemId = itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_ID));
                    item.setId(itemId);

                    String lastModified = itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_LAST_MODIFIED));
                    item.setLastModified(lastModified);

                    String status = itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_STATUS));
                    item.setStatus(status);

                    String objectId = itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_OBJECT_ID));
                    item.setObjectId(objectId);

                    String syncStatus = itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_SYNC_STATUS));
                    item.setSyncStatus(syncStatus);
                    if (FileTable.TABLE_NAME.equals(tableName)) {
                        String type = itemsCursor.getString(itemsCursor.getColumnIndex(FileTable.COLUMN_TYPE));
                        item.setType(type);
                        String path = itemsCursor.getString(itemsCursor.getColumnIndex(FileTable.COLUMN_PATH));
                        item.setPath(path);
                        item.setPhysicalRootPath(Constants.ROOT_DOCUMENTS_DIR_PATH);
                    }

                    itemList.add(item);

                    itemsCursor.moveToNext();
                } while (!itemsCursor.isAfterLast());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (itemsCursor != null) {
                itemsCursor.close();
            }
        }
        return itemList;
    }

    protected void updateStatus(final String tableName, String id, String status, String updatedTime) {
        String sqlQuery = "UPDATE %s SET %s='%s', %s='%s' WHERE %s='%s'";
        sqlQuery = String.format(sqlQuery, tableName, COLUMN_STATUS, status, COLUMN_LAST_MODIFIED, updatedTime,
                COLUMN_ID, id);
        getDatabase().execSQL(sqlQuery);
    }*/



}
