package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.models.Rate;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RateTable extends BaseTable {
    private final String TAG = RateTable.class.getSimpleName();

    public interface IRateTable {
        String TABLE_RATE = "rates";

        String COLUMN_RATE_ID = "id";
        String COLUMN_CATE_ID = "cateId";
        String COLUMN_LEVEL_ID = "levelId";
        String COLUMN_SCORE_RANGE = "scoreRange";
        String COLUMN_RATING = "rating";
        String COLUMN_NAME = "name";
    }

    public RateTable(Context context) {
        super(context);
    }

    public RateTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveRates(List<Rate> rates, String levelId, String cateId, SQLiteDatabase sqliteDb) throws Exception {
        if (null == rates || rates.size() == 0
    || TextUtils.isEmpty(levelId) || TextUtils.isEmpty(cateId)) return -1;

        for (Rate rate : rates) {
            this.saveRate(rate, levelId, cateId, sqliteDb);
        }

        return 1;
    }

    public long saveRate(Rate rate, String levelId, String cateId, SQLiteDatabase sqliteDb) throws Exception {
        if (null == rate) return -1;

        if (!checkExistRate(String.valueOf(rate.getId()), levelId, cateId, sqliteDb)) {
            ContentValues values = new ContentValues();
            values.put(IRateTable.COLUMN_RATE_ID, rate.getId());
            values.put(IRateTable.COLUMN_LEVEL_ID, levelId);
            values.put(IRateTable.COLUMN_CATE_ID, cateId);
            values.put(IRateTable.COLUMN_SCORE_RANGE, rate.getScore());
            values.put(IRateTable.COLUMN_RATING, rate.getRating());
            values.put(IRateTable.COLUMN_NAME, rate.getName());

            return sqliteDb.insert(IRateTable.TABLE_RATE, null, values);
        } else {
            return updateRate(rate, levelId, cateId, sqliteDb);
        }
    }

    public int updateRate(Rate rate, String levelId, String cateId, SQLiteDatabase sqliteDb) throws Exception {
        if (null == rate) return 0;

        ContentValues values = new ContentValues();
        values.put(IRateTable.COLUMN_RATE_ID, rate.getId());
        values.put(IRateTable.COLUMN_LEVEL_ID, levelId);
        values.put(IRateTable.COLUMN_CATE_ID, cateId);
        values.put(IRateTable.COLUMN_SCORE_RANGE, rate.getScore());
        values.put(IRateTable.COLUMN_RATING, rate.getRating());
        values.put(IRateTable.COLUMN_NAME, rate.getName());

        String whereClause = IRateTable.COLUMN_RATE_ID + " = ?";
        String[] whereArgs = {String.valueOf(rate.getId())};

        return sqliteDb.update(IRateTable.TABLE_RATE, values, whereClause, whereArgs);

    }

    public boolean checkExistRate(String rateId, String levelId, String cateId,SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = IRateTable.COLUMN_RATE_ID + " = ? " +
                "AND " + IRateTable.COLUMN_LEVEL_ID + " = ? " +
                "AND " + IRateTable.COLUMN_CATE_ID + " = ?";
        String[] selectionArgs = {rateId, levelId, cateId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = db.query(IRateTable.TABLE_RATE, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
        if (cursor == null) return false;
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public Rate getRate(String id, SQLiteDatabase db) throws Exception {
        if (TextUtils.isEmpty(id)) return null;
        String[] selectionColumns = null;
        String selection = IRateTable.COLUMN_RATING + " = ?";
        String[] selectionArgs = {id};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Rate rate = null;
        try {
            Cursor cursor = db.query(IRateTable.TABLE_RATE, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String rateId = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_RATE_ID));
                        String levelId = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_LEVEL_ID));
                        String cateId = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_CATE_ID));
                        String score = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_SCORE_RANGE));
                        String rating = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_RATING));
                        String name = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_NAME));

                        rate = new Rate();
                        rate.setId(rateId);
                        rate.setLevelId(levelId);
                        rate.setCateId(cateId);
                        rate.setScore(score);
                        rate.setRating(rating);
                        rate.setName(name);

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
        return rate;
    }

    public List<Rate> getRates(String levelId, String cateId, SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = IRateTable.COLUMN_LEVEL_ID + " = ? AND " + IRateTable.COLUMN_CATE_ID + " = ? ";
        String[] selectionArgs = {levelId, cateId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        List<Rate> outRates = new ArrayList<>();
        Rate rate = null;
        try {
            Cursor cursor = db.query(IRateTable.TABLE_RATE, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String rateId = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_RATE_ID));
                        String score = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_SCORE_RANGE));
                        String rating = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_RATING));
                        String name = cursor.getString(cursor.getColumnIndex(IRateTable.COLUMN_NAME));

                        rate = new Rate();
                        rate.setId(rateId);
                        rate.setLevelId(levelId);
                        rate.setCateId(cateId);
                        rate.setScore(score);
                        rate.setRating(rating);
                        rate.setName(name);

                        outRates.add(rate);
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
        return outRates;
    }


}
