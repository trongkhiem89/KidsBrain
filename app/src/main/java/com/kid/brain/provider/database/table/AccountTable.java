package com.kid.brain.provider.database.table;

import android.content.ContentValues;
import android.content.Context;

import com.kid.brain.models.Account;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;


public class AccountTable extends BaseTable {
    private final String TAG = AccountTable.class.getSimpleName();

    public interface IAccountTable{
        String TABLE_ACCOUNT = "accounts";
        String COLUMN_USER_ID = "userId";
        String COLUMN_BIRTH_DAY = "birthDay";
        String COLUMN_EMAIL = "email";
        String COLUMN_USER_NAME = "fullName";
        String COLUMN_MOBILE = "mobile";
        String COLUMN_ADDRESS = "address";
        String COLUMN_GENDER = "gender";
        String COLUMN_PHOTO = "photo";

    }

    public AccountTable(Context context) {
        super(context);
    }

    public AccountTable(SQLiteDatabase db) {
        super(db);
    }

    public long saveAccount(Account account, SQLiteDatabase sqliteDb) throws Exception{
        if (null == account) return -1;

        if (!checkExistAccount(account.getUserId(), sqliteDb)) {
            ContentValues values = new ContentValues();
            values.put(IAccountTable.COLUMN_USER_ID, account.getUserId());
            values.put(IAccountTable.COLUMN_EMAIL, account.getEmail());
            values.put(IAccountTable.COLUMN_BIRTH_DAY, account.getBirthDay());
            values.put(IAccountTable.COLUMN_USER_NAME, account.getUsername());
            values.put(IAccountTable.COLUMN_MOBILE, account.getMobile());
            values.put(IAccountTable.COLUMN_ADDRESS, account.getAddress());
            values.put(IAccountTable.COLUMN_GENDER, account.getGender());
            values.put(IAccountTable.COLUMN_PHOTO, account.getPhoto());

            return sqliteDb.insert(IAccountTable.TABLE_ACCOUNT, null, values);
        } else {
            return updateAccount(account, sqliteDb);
        }
	}

    public int updateAccount(Account account, SQLiteDatabase sqliteDb) throws Exception {
        if (null == account) return 0;

        ContentValues values = new ContentValues();
        values.put(IAccountTable.COLUMN_USER_ID, account.getUserId());
        values.put(IAccountTable.COLUMN_EMAIL, account.getEmail());
        values.put(IAccountTable.COLUMN_BIRTH_DAY, account.getBirthDay());
        values.put(IAccountTable.COLUMN_USER_NAME, account.getUsername());
        values.put(IAccountTable.COLUMN_MOBILE, account.getMobile());
        values.put(IAccountTable.COLUMN_ADDRESS, account.getAddress());
        values.put(IAccountTable.COLUMN_GENDER, account.getGender());
        values.put(IAccountTable.COLUMN_PHOTO, account.getPhoto());

        String whereClause = IAccountTable.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {account.getUserId()};

        return sqliteDb.update(IAccountTable.TABLE_ACCOUNT, values, whereClause, whereArgs);

    }

    public boolean checkExistAccount(String accountId, SQLiteDatabase db) throws Exception{
        String[] selectionColumns = null;
        String selection = IAccountTable.COLUMN_USER_ID + " = ?";

        String[] selectionArgs = {accountId};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor cursor = db.query(IAccountTable.TABLE_ACCOUNT, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
        boolean result = cursor.getCount() > 0 ;
        if ( null != cursor) cursor.close();
        return result;
    }

    public Account getAccount(SQLiteDatabase db) throws Exception {
        String[] selectionColumns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Account account = null;
        try {
            Cursor cursor = db.query(IAccountTable.TABLE_ACCOUNT, selectionColumns, selection, selectionArgs, groupBy, having, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String userId = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_USER_ID));
                        String email = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_EMAIL));
                        String birthDay = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_BIRTH_DAY));
                        String username = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_USER_NAME));
                        String mobile = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_MOBILE));
                        String address = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_ADDRESS));
                        int gender = cursor.getInt(cursor.getColumnIndex(IAccountTable.COLUMN_GENDER));
                        String photo = cursor.getString(cursor.getColumnIndex(IAccountTable.COLUMN_PHOTO));

                        account = new Account();
                        account.setUserId(userId);
                        account.setEmail(email);
                        account.setBirthDay(birthDay);
                        account.setUsername(username);
                        account.setMobile(mobile);
                        account.setAddress(address);
                        account.setGender(gender);
                        account.setPhoto(photo);

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
        return account;
    }

    public int deleteAccounts(SQLiteDatabase db) throws Exception {
        String whereClause = null;
        String whereArgs[] = null;
        return db.delete(IAccountTable.TABLE_ACCOUNT, whereClause, whereArgs);
    }


}
