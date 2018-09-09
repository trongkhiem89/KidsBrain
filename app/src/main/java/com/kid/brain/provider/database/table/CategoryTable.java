package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.models.Category;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class CategoryTable extends BaseTable {
    private final String TAG = CategoryTable.class.getSimpleName();

    public interface ICategoryTable {
        String TABLE_CATEGORY = "categories";

        String COLUMN_CATE_ID = "cateId";
        String COLUMN_LEVEL_ID = "levelId";
        String COLUMN_NAME = "name";
        String COLUMN_ICON = "icon";
        String COLUMN_DESCRIPTION = "description";
        String COLUMN_STATUS = "status";
        String COLUMN_ORDER = "sort";
        String COLUMN_CREATED_AT = "createdAt";
        String COLUMN_UPDATED_AT = "updatedAt";

    }

    public CategoryTable(Context context) {
        super(context);
    }

    public CategoryTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveCategories(String levelId, List<Category> categories, SQLiteDatabase sqliteDb) throws Exception {
        if (TextUtils.isEmpty(levelId) || null == categories || categories.size() == 0) return -1;

        for (Category category : categories) {
            this.saveCategory(levelId, category, sqliteDb);
        }

        return 1;
    }

    public long saveCategory(String levelId, Category category, SQLiteDatabase sqliteDb) throws Exception {
        if (null == category) return -1;

        if (!checkExistCategory(levelId, String.valueOf(category.getId()), sqliteDb)) {
            ContentValues values = new ContentValues();
            values.put(ICategoryTable.COLUMN_CATE_ID, category.getId());
            values.put(ICategoryTable.COLUMN_LEVEL_ID, levelId);
            values.put(ICategoryTable.COLUMN_NAME, category.getName());
            values.put(ICategoryTable.COLUMN_ICON, category.getIcon());
            values.put(ICategoryTable.COLUMN_DESCRIPTION, category.getDescription());
            values.put(ICategoryTable.COLUMN_STATUS, category.getStatus());
            values.put(ICategoryTable.COLUMN_CREATED_AT, category.getCreatedAt());
            values.put(ICategoryTable.COLUMN_UPDATED_AT, category.getUpdatedAt());

            return sqliteDb.insert(ICategoryTable.TABLE_CATEGORY, null, values);
//            return sqliteDb.insertWithOnConflict(ICategoryTable.TABLE_CATEGORY, null, values, CONFLICT_REPLACE);
        } else {
            return updateCategory(levelId, category, sqliteDb);
        }
    }

    public int updateCategory(String levelId, Category category, SQLiteDatabase sqliteDb) throws Exception {
        if (null == category) return 0;

        ContentValues values = new ContentValues();
        values.put(ICategoryTable.COLUMN_CATE_ID, category.getId());
        values.put(ICategoryTable.COLUMN_LEVEL_ID, levelId);
        values.put(ICategoryTable.COLUMN_NAME, category.getName());
        values.put(ICategoryTable.COLUMN_ICON, category.getIcon());
        values.put(ICategoryTable.COLUMN_DESCRIPTION, category.getDescription());
        values.put(ICategoryTable.COLUMN_STATUS, category.getStatus());
        values.put(ICategoryTable.COLUMN_CREATED_AT, category.getCreatedAt());
        values.put(ICategoryTable.COLUMN_UPDATED_AT, category.getUpdatedAt());

        String whereClause = ICategoryTable.COLUMN_CATE_ID + " = ?";
        String[] whereArgs = {String.valueOf(category.getId())};

        return sqliteDb.update(ICategoryTable.TABLE_CATEGORY, values, whereClause, whereArgs);

    }

    public boolean checkExistCategory(String levelId, String cateId, SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = ICategoryTable.COLUMN_CATE_ID + " = ? AND " + ICategoryTable.COLUMN_LEVEL_ID + " = ? ";

        String[] selectionArgs = {cateId, levelId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = db.query(ICategoryTable.TABLE_CATEGORY, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
        boolean result = cursor.getCount() > 0;
        if (null != cursor) cursor.close();
        return result;
    }

    public Category getCategory(String levelId, String cateId, SQLiteDatabase db) throws Exception {
        if (TextUtils.isEmpty(levelId) || TextUtils.isEmpty(cateId)) return null;
        String[] selectionColumns = null;
        String selection = ICategoryTable.COLUMN_CATE_ID + " = ? AND " + ICategoryTable.COLUMN_LEVEL_ID + " = ? ";
        String[] selectionArgs = {cateId, levelId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Category category = null;
        try {
            Cursor cursor = db.query(ICategoryTable.TABLE_CATEGORY, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
//                        String cateId = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_CATE_ID));
//                        String levelId = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_ID));
                        String name = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_NAME));
                        String icon = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_ICON));
                        String description = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_DESCRIPTION));
                        String status = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_STATUS));
                        String createdAt = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_CREATED_AT));
                        String updatedAt = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_UPDATED_AT));

                        category = new Category();
                        category.setId(Integer.parseInt(cateId));
                        category.setLevelId(Integer.parseInt(levelId));
                        category.setName(name);
                        category.setIcon(icon);
                        category.setDescription(description);
                        category.setStatus(status);
                        category.setCreatedAt(createdAt);
                        category.setUpdatedAt(updatedAt);

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
        return category;
    }

    public List<Category> getCategories(String levelId, SQLiteDatabase db) throws Exception {
        if (TextUtils.isEmpty(levelId)) return new ArrayList<>();
        String[] selectionColumns = null;
        String selection = ICategoryTable.COLUMN_LEVEL_ID + " = ? ";
        String[] selectionArgs = {levelId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        List<Category> categories = new ArrayList<>();
        Category category = null;
        try {
            Cursor cursor = db.query(ICategoryTable.TABLE_CATEGORY, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String cateId = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_CATE_ID));
//                        String levelId = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_ID));
                        String name = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_NAME));
                        String icon = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_ICON));
                        String description = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_DESCRIPTION));
                        String status = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_STATUS));
                        String createdAt = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_CREATED_AT));
                        String updatedAt = cursor.getString(cursor.getColumnIndex(ICategoryTable.COLUMN_UPDATED_AT));

                        category = new Category();
                        category.setId(Integer.parseInt(cateId));
                        category.setLevelId(Integer.parseInt(levelId));
                        category.setName(name);
                        category.setIcon(icon);
                        category.setDescription(description);
                        category.setStatus(status);
                        category.setCreatedAt(createdAt);
                        category.setUpdatedAt(updatedAt);

                        categories.add(category);
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

        return categories;
    }


}
