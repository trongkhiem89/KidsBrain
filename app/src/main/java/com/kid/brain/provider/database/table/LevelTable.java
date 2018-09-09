package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.models.Level;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class LevelTable extends BaseTable {
    private final String TAG = LevelTable.class.getSimpleName();

    public interface ILevelTable {
        String TABLE_LEVEL = "levels";

        String COLUMN_LEVEL_ID = "id";
        String COLUMN_NAME = "name";
        String COLUMN_ICON = "icon";
        String COLUMN_DESCRIPTION = "description";
        String COLUMN_STATUS = "status";
        String COLUMN_CREATED_AT = "createdAt";
        String COLUMN_UPDATED_AT = "updatedAt";

    }

    public LevelTable(Context context) {
        super(context);
    }

    public LevelTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveLevels(List<Level> levels, SQLiteDatabase sqliteDb) throws Exception {
        if (null == levels || levels.size() == 0) return -1;

        for (Level level : levels) {
            this.saveLevel(level, sqliteDb);
        }

        return 1;
    }

    public long saveLevel(Level level, SQLiteDatabase sqliteDb) throws Exception {
        if (null == level) return -1;

        if (!checkExistLevel(String.valueOf(level.getId()), sqliteDb)) {
            ContentValues values = new ContentValues();
            values.put(ILevelTable.COLUMN_LEVEL_ID, level.getId());
            values.put(ILevelTable.COLUMN_NAME, level.getName());
            values.put(ILevelTable.COLUMN_ICON, level.getIcon());
            values.put(ILevelTable.COLUMN_DESCRIPTION, level.getDescription());
            values.put(ILevelTable.COLUMN_STATUS, level.getStatus());
            values.put(ILevelTable.COLUMN_CREATED_AT, level.getCreatedAt());
            values.put(ILevelTable.COLUMN_UPDATED_AT, level.getUpdatedAt());

            return sqliteDb.insert(ILevelTable.TABLE_LEVEL, null, values);
        } else {
            return updateLevel(level, sqliteDb);
        }
    }

    public int updateLevel(Level level, SQLiteDatabase sqliteDb) throws Exception {
        if (null == level) return 0;

        ContentValues values = new ContentValues();
        values.put(ILevelTable.COLUMN_LEVEL_ID, level.getId());
        values.put(ILevelTable.COLUMN_NAME, level.getName());
        values.put(ILevelTable.COLUMN_ICON, level.getIcon());
        values.put(ILevelTable.COLUMN_DESCRIPTION, level.getDescription());
        values.put(ILevelTable.COLUMN_STATUS, level.getStatus());
        values.put(ILevelTable.COLUMN_CREATED_AT, level.getCreatedAt());
        values.put(ILevelTable.COLUMN_UPDATED_AT, level.getUpdatedAt());

        String whereClause = ILevelTable.COLUMN_LEVEL_ID + " = ?";
        String[] whereArgs = {String.valueOf(level.getId())};

        return sqliteDb.update(ILevelTable.TABLE_LEVEL, values, whereClause, whereArgs);

    }

    public boolean checkExistLevel(String levelId, SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = ILevelTable.COLUMN_LEVEL_ID + " = ?";

        String[] selectionArgs = {levelId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = db.query(ILevelTable.TABLE_LEVEL, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
        boolean result = cursor.getCount() > 0;
        if (null != cursor) cursor.close();
        return result;
    }

    public Level getLevel(String id, SQLiteDatabase db) throws Exception {
        if (TextUtils.isEmpty(id)) return null;
        String[] selectionColumns = null;
        String selection = ILevelTable.COLUMN_LEVEL_ID + " = ?";
        String[] selectionArgs = {id};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Level level = null;
        try {
            Cursor cursor = db.query(ILevelTable.TABLE_LEVEL, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String levelId = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_LEVEL_ID));
                        String name = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_NAME));
                        String icon = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_ICON));
                        String description = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_DESCRIPTION));
                        String status = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_STATUS));
                        String createdAt = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_CREATED_AT));
                        String updatedAt = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_UPDATED_AT));

                        level = new Level();
                        level.setId(Integer.parseInt(levelId));
                        level.setName(name);
                        level.setIcon(icon);
                        level.setDescription(description);
                        level.setStatus(status);
                        level.setCreatedAt(createdAt);
                        level.setUpdatedAt(updatedAt);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }

    public List<Level> getLevels(SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        List<Level> outLevels = new ArrayList<>();
        Level level = null;
        try {
            Cursor cursor = db.query(ILevelTable.TABLE_LEVEL, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String levelId = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_LEVEL_ID));
                        String name = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_NAME));
                        String icon = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_ICON));
                        String description = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_DESCRIPTION));
                        String status = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_STATUS));
                        String createdAt = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_CREATED_AT));
                        String updatedAt = cursor.getString(cursor.getColumnIndex(ILevelTable.COLUMN_UPDATED_AT));

                        level = new Level();
                        level.setId(Integer.parseInt(levelId));
                        level.setName(name);
                        level.setIcon(icon);
                        level.setDescription(description);
                        level.setStatus(status);
                        level.setCreatedAt(createdAt);
                        level.setUpdatedAt(updatedAt);

                        outLevels.add(level);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (outLevels != null && outLevels.size() > 1){
//            Collections.sort(outLevels, new Comparator<Level>() {
//                @Override
//                public int compare(Level lhs, Level rhs) {
//                    return String.valueOf(rhs.getId()).compareTo(String.valueOf(lhs.getId()));
//                }
//            });
//        }

        return outLevels;
    }


}
