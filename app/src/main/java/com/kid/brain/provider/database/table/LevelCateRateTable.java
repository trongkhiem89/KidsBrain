package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.models.Rate;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class LevelCateRateTable extends BaseTable {
    private final String TAG = LevelCateRateTable.class.getSimpleName();

    public interface ILevelCateRateTable {
        String TABLE_LEVEL_CATE_RATE = "levelCateRates";

        String COLUMN_ID = "id";
        String COLUMN_LEVEL_ID = "levelId";
        String COLUMN_CATE_ID = "cateId";
        String COLUMN_RATE_ID = "rateId";
        String COLUMN_SCORE = "score";
    }

    public LevelCateRateTable(Context context) {
        super(context);
    }

    public LevelCateRateTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveData(String levelId, String cateId, String rateId, int score, SQLiteDatabase sqliteDb) throws Exception {
        if (TextUtils.isEmpty(levelId) || TextUtils.isEmpty(cateId) || TextUtils.isEmpty(rateId))
            return -1;

        ContentValues values = new ContentValues();
        values.put(ILevelCateRateTable.COLUMN_LEVEL_ID, levelId);
        values.put(ILevelCateRateTable.COLUMN_CATE_ID, cateId);
        values.put(ILevelCateRateTable.COLUMN_RATE_ID, rateId);
        values.put(ILevelCateRateTable.COLUMN_SCORE, score);

        return sqliteDb.insertWithOnConflict(ILevelCateRateTable.TABLE_LEVEL_CATE_RATE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Rate> getRates(String levelId, String cateId, SQLiteDatabase db) {

        String sql = "SELECT r.id, r.scoreRange, r.rating, r.name\n" +
                "FROM levelCateRates as lcr\n" +
                "LEFT JOIN rates as r ON r.id = lcr.rateId  \n" +
                "WHERE lcr.levelId = ? AND lcr.cateId = ?\n " +
                "GROUP BY lcr.levelId";

        List<Rate> rates = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, new String[]{levelId, cateId});

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                try {

                    String rateId = cursor.getString(cursor.getColumnIndex(RateTable.IRateTable.COLUMN_RATE_ID));
                    String scoreRange = cursor.getString(cursor.getColumnIndex(RateTable.IRateTable.COLUMN_SCORE_RANGE));
                    String rating = cursor.getString(cursor.getColumnIndex(RateTable.IRateTable.COLUMN_RATING));
                    String name = cursor.getString(cursor.getColumnIndex(RateTable.IRateTable.COLUMN_NAME));

                    Rate rate = new Rate();
                    rate.setId(rateId);
                    rate.setScore(scoreRange);
                    rate.setRating(rating);
                    rate.setName(name);

                    rates.add(rate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }

        if (cursor != null) cursor.close();

        return rates;
    }

}
