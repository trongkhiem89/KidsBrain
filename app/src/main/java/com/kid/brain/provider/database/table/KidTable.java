package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.models.Kid;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class KidTable extends BaseTable {
    private final String TAG = KidTable.class.getSimpleName();

    public interface IKidTable {
        String TABLE_KID = "kids";
        String COLUMN_KID_ID = "id";
        String COLUMN_AGE_ID = "ageId";
        String COLUMN_PARENT_ID = "parentId";
        String COLUMN_BIRTH_DAY = "birthDay";
        String COLUMN_FULL_NAME = "fullName";
        String COLUMN_GENDER = "gender";
        String COLUMN_PHOTO = "photo";

    }

    public KidTable(Context context) {
        super(context);
    }

    public KidTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveKids(List<Kid> kids, SQLiteDatabase sqliteDb) throws Exception{
        if (null == kids || kids.size() == 0) return -1;
        for (Kid kid : kids) {
            this.saveKid(kid, sqliteDb);
        }
        return kids.size();
	}

    public long saveKid(Kid kid, SQLiteDatabase sqliteDb) throws Exception{
        if (null == kid) return -1;

        if (!checkExistKid(kid.getParentId(), kid.getChildrenId(), sqliteDb)) {
            ContentValues values = new ContentValues();
            values.put(IKidTable.COLUMN_KID_ID, kid.getChildrenId());
            values.put(IKidTable.COLUMN_AGE_ID, kid.getAgeId());
            values.put(IKidTable.COLUMN_PARENT_ID, kid.getParentId());
            values.put(IKidTable.COLUMN_BIRTH_DAY, kid.getBirthDay());
            values.put(IKidTable.COLUMN_FULL_NAME, kid.getUsername());
            values.put(IKidTable.COLUMN_GENDER, kid.getGender());
            values.put(IKidTable.COLUMN_PHOTO, kid.getPhoto());

            return sqliteDb.insert(IKidTable.TABLE_KID, null, values);
        } else {
            return updateKid(kid, sqliteDb);
        }
	}

    public int updateKid(Kid kid, SQLiteDatabase sqliteDb) throws Exception {
        if (null == kid) return 0;

        ContentValues values = new ContentValues();
        values.put(IKidTable.COLUMN_KID_ID, kid.getChildrenId());
        values.put(IKidTable.COLUMN_AGE_ID, kid.getAgeId());
        values.put(IKidTable.COLUMN_PARENT_ID, kid.getParentId());
        values.put(IKidTable.COLUMN_BIRTH_DAY, kid.getBirthDay());
        values.put(IKidTable.COLUMN_FULL_NAME, kid.getUsername());
        values.put(IKidTable.COLUMN_GENDER, kid.getGender());
        values.put(IKidTable.COLUMN_PHOTO, kid.getPhoto());

        String whereClause = IKidTable.COLUMN_KID_ID + " = ?";
        String[] whereArgs = {kid.getChildrenId()};

        return sqliteDb.update(IKidTable.TABLE_KID, values, whereClause, whereArgs);

    }

    public int deleteKid(String kidId, SQLiteDatabase sqliteDb) throws Exception {
        if (TextUtils.isEmpty(kidId)) return -1;

        String whereClause = IKidTable.COLUMN_KID_ID + " = ?";
        String[] whereArgs = {kidId};

        return sqliteDb.delete(IKidTable.TABLE_KID, whereClause, whereArgs);
    }

    public int deleteKids(SQLiteDatabase sqliteDb) throws Exception {
        String whereClause = null;
        String whereArgs[] = null;
        return sqliteDb.delete(IKidTable.TABLE_KID, whereClause, whereArgs);
    }


    public boolean checkExistKid(String parentId, String kidId, SQLiteDatabase db) throws Exception{
        String[] selectionColumns = null;
        String selection = IKidTable.COLUMN_KID_ID + " = ? ";
//        String selection = IKidTable.COLUMN_PARENT_ID + " = ? AND " + IKidTable.COLUMN_KID_ID + " = ? ";

        String[] selectionArgs = {kidId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = db.query(IKidTable.TABLE_KID, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
        boolean result = cursor.getCount() > 0 ;
        if ( null != cursor) cursor.close();
        return result;
    }

    public Kid getKid(String kidId, SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = IKidTable.COLUMN_KID_ID + " = ? ";
        String[] selectionArgs = {kidId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Kid kid = null;
        try {
            Cursor cursor = db.query(IKidTable.TABLE_KID, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String parentId = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_PARENT_ID));
                        String ageId = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_AGE_ID));
                        String birthDay = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_BIRTH_DAY));
                        String fullName = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_FULL_NAME));
                        int gender = cursor.getInt(cursor.getColumnIndex(IKidTable.COLUMN_GENDER));
                        String photo = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_PHOTO));

                        kid = new Kid();
                        kid.setChildrenId(kidId);
                        kid.setAgeId(ageId);
                        kid.setParentId(parentId);
                        kid.setBirthDay(birthDay);
                        kid.setUsername(fullName);
                        kid.setGender(gender);
                        kid.setPhoto(photo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return kid;
    }

    public List<Kid> getKids(String parentId, SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = null; //IKidTable.COLUMN_PARENT_ID + " = ? ";
        String[] selectionArgs = null; // {parentId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        List<Kid> kids = new ArrayList<>();
        Kid kid = null;
        try {
            Cursor cursor = db.query(IKidTable.TABLE_KID, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String kidId = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_KID_ID));
                        String ageId = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_AGE_ID));
                        String birthDay = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_BIRTH_DAY));
                        String fullName = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_FULL_NAME));
                        int gender = cursor.getInt(cursor.getColumnIndex(IKidTable.COLUMN_GENDER));
                        String photo = cursor.getString(cursor.getColumnIndex(IKidTable.COLUMN_PHOTO));

                        kid = new Kid();
                        kid.setChildrenId(kidId);
                        kid.setAgeId(ageId);
                        kid.setParentId(parentId);
                        kid.setBirthDay(birthDay);
                        kid.setUsername(fullName);
                        kid.setGender(gender);
                        kid.setPhoto(photo);

                        kids.add(kid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return kids;
    }


}
