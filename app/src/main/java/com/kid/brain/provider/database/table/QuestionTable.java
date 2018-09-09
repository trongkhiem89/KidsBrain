package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.models.Question;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class QuestionTable extends BaseTable {
    private final String TAG = QuestionTable.class.getSimpleName();

    public interface IQuestionTable {
        String TABLE_QUESTION = "questions";

        String COLUMN_QUESTION_ID = "questionId";
        String COLUMN_CATE_ID = "cateId";
        String COLUMN_LEVEL_ID = "levelId";
        String COLUMN_NAME = "name";
        String COLUMN_ICON = "icon";
        String COLUMN_DESCRIPTION = "description";
        String COLUMN_STATUS = "status";
        String COLUMN_ORDER = "sort";
        String COLUMN_CREATED_AT = "createdAt";
        String COLUMN_UPDATED_AT = "updatedAt";
        String COLUMN_IS_CHECKED = "isChecked";
    }

    public QuestionTable(Context context) {
        super(context);
    }

    public QuestionTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveQuestions(String levelId, String cateId, List<Question> questions, SQLiteDatabase sqliteDb) throws Exception {
        if (null == questions || questions.size() == 0) return -1;

        for (Question question : questions) {
            this.saveQuestion(levelId, cateId, question, sqliteDb);
        }

        return 1;
    }

    public long saveQuestion(String levelId, String cateId, Question question, SQLiteDatabase sqliteDb) throws Exception {
        if (null == question) return -1;

        if (!checkExistQuestion(levelId, cateId, String.valueOf(question.getId()), sqliteDb)) {
            ContentValues values = new ContentValues();
            values.put(IQuestionTable.COLUMN_QUESTION_ID, question.getId());
            values.put(IQuestionTable.COLUMN_CATE_ID, cateId);
            values.put(IQuestionTable.COLUMN_LEVEL_ID, levelId);
            values.put(IQuestionTable.COLUMN_NAME, question.getName());
            values.put(IQuestionTable.COLUMN_DESCRIPTION, question.getDescription());
            values.put(IQuestionTable.COLUMN_STATUS, question.getStatus());
            values.put(IQuestionTable.COLUMN_CREATED_AT, question.getCreatedAt());
            values.put(IQuestionTable.COLUMN_UPDATED_AT, question.getUpdatedAt());

            return sqliteDb.insert(IQuestionTable.TABLE_QUESTION, null, values);
        } else {
            return updateQuestion(levelId, cateId, question, sqliteDb);
        }
    }

    public int updateQuestion(String levelId, String cateId, Question question, SQLiteDatabase sqliteDb) throws Exception {
        if (null == question) return 0;

        ContentValues values = new ContentValues();
        values.put(IQuestionTable.COLUMN_QUESTION_ID, question.getId());
        values.put(IQuestionTable.COLUMN_CATE_ID, cateId);
        values.put(IQuestionTable.COLUMN_LEVEL_ID, levelId);
        values.put(IQuestionTable.COLUMN_NAME, question.getName());
        values.put(IQuestionTable.COLUMN_DESCRIPTION, question.getDescription());
        values.put(IQuestionTable.COLUMN_STATUS, question.getStatus());
        values.put(IQuestionTable.COLUMN_CREATED_AT, question.getCreatedAt());
        values.put(IQuestionTable.COLUMN_UPDATED_AT, question.getUpdatedAt());

        String whereClause = IQuestionTable.COLUMN_QUESTION_ID + " = ?";
        String[] whereArgs = {String.valueOf(question.getId())};

        return sqliteDb.update(IQuestionTable.TABLE_QUESTION, values, whereClause, whereArgs);

    }

    public boolean checkExistQuestion(String levelId, String cateId, String questionId, SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = IQuestionTable.COLUMN_QUESTION_ID + " = ? AND "
                + IQuestionTable.COLUMN_LEVEL_ID + " = ? AND "
                + IQuestionTable.COLUMN_CATE_ID + " = ?";

        String[] selectionArgs = {questionId, levelId, cateId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = db.query(IQuestionTable.TABLE_QUESTION, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
        boolean result = cursor.getCount() > 0;
        if (null != cursor) cursor.close();
        return result;
    }

    public Question getQuestion(String questionId, SQLiteDatabase db) throws Exception {
        if (TextUtils.isEmpty(questionId)) return null;
        String[] selectionColumns = null;
        String selection = IQuestionTable.COLUMN_QUESTION_ID + " = ?";
        String[] selectionArgs = {questionId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Question question = null;
        try {
            Cursor cursor = db.query(IQuestionTable.TABLE_QUESTION, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
//                        String questionId = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_QUESTION_ID));
                        String cateId = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_CATE_ID));
                        String levelId = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_LEVEL_ID));
                        String name = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_DESCRIPTION));
                        String status = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_STATUS));
                        String createdAt = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_CREATED_AT));
                        String updatedAt = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_UPDATED_AT));

                        question = new Question();
                        question.setId(Integer.parseInt(questionId));
                        question.setCateId(Integer.parseInt(cateId));
                        question.setLevelId(Integer.parseInt(levelId));
                        question.setName(name);
                        question.setDescription(description);
                        question.setStatus(status);
                        question.setCreatedAt(createdAt);
                        question.setUpdatedAt(updatedAt);

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
        return question;
    }

    public List<Question> getQuestions(String levelId, String cateId, SQLiteDatabase db) throws Exception {
        if (TextUtils.isEmpty(levelId) || TextUtils.isEmpty(cateId)) return new ArrayList<>();
        String[] selectionColumns = null;
        String selection = IQuestionTable.COLUMN_LEVEL_ID + " = ? AND " + IQuestionTable.COLUMN_CATE_ID + " = ?";
        String[] selectionArgs = {levelId, cateId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        List<Question> outQuestions = new ArrayList<>();
        Question question = null;
        try {
            Cursor cursor = db.query(IQuestionTable.TABLE_QUESTION, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String questionId = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_QUESTION_ID));
//                        String cateId = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_CATE_ID));
//                        String levelId = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_ID));
                        String name = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_DESCRIPTION));
                        String status = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_STATUS));
                        String createdAt = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_CREATED_AT));
                        String updatedAt = cursor.getString(cursor.getColumnIndex(IQuestionTable.COLUMN_UPDATED_AT));

                        question = new Question();
                        question.setId(Integer.parseInt(questionId));
                        question.setCateId(Integer.parseInt(cateId));
                        question.setLevelId(Integer.parseInt(levelId));
                        question.setName(name);
                        question.setDescription(description);
                        question.setStatus(status);
                        question.setCreatedAt(createdAt);
                        question.setUpdatedAt(updatedAt);

                        outQuestions.add(question);
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

        return outQuestions;
    }


}
