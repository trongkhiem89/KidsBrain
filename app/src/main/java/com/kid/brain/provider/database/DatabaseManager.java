package com.kid.brain.provider.database;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kid.brain.managers.application.KidApplication;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
	private static final String TAG = DatabaseManager.class.getSimpleName();

	private static final int iconMaxPixes = 40 * 40;
	protected static final int iconMaxBytes = iconMaxPixes * 4;
	private static final int photoMaxPixes = 240 * 400;
	protected static final int photoMaxBytes = photoMaxPixes * 4;
	protected static final int maxReadPerQuery = 500000;
	public static final float DB_SCHEMA_VERSION = 4.0f;
	private static final String SQL_COMMENT = "--";
	private static final Map<String, String[]> DB_MIGRATE_SCRIPT_NAMES_MAP = initializeDbMigrateScriptNamesMap();

	public static String DB_NAME = "KidsBrain.sqlite";

	public static Context ApplicationContext = null;

	private static DatabaseManager instance;

	private Context mCtx;
	private SQLiteDatabase mDb;
	private String mFromDbSchemaVersion;
	private String mSqlCipherDbPassword;

	private File mDbFile;

	public SQLiteDatabase getDb() {
		return mDb;
	}

	public String getSqlCipherDbPassword() {
		return mSqlCipherDbPassword;
	}

	public static String getDbsDirPath(Context ctx) {
		String dbFilePath = ctx.getDatabasePath(DB_NAME).getAbsolutePath();
		return dbFilePath.substring(0, dbFilePath.length() - DB_NAME.length());
	}

	public static String getDbFileFullPath(Context ctx) {
		return ctx.getDatabasePath(DB_NAME).getAbsolutePath();
	}

	public static String getDbFileFullPath() {
		if (ApplicationContext != null) {
			return getDbFileFullPath(ApplicationContext);
		} else {
			throw new RuntimeException("Didnt' set context for DbHelper!");
		}
	}

	private static Map<String, String[]> initializeDbMigrateScriptNamesMap() {
		// NOTEs:
		// WIDA's First DbSchemaVersion is "1.0"
		// Assuming that data issues Only exist in DB versions below 1.0
		Map<String, String[]> dbMigrateScriptNamesMap = new HashMap<String, String[]>();
		String fromDbSchemaVersion = "1.0";
		String[] dbMigrateScriptNamesInOrder = new String[] {
				"migrate_db_from_2.5_to_3.1.sql"};
		dbMigrateScriptNamesMap.put(fromDbSchemaVersion, dbMigrateScriptNamesInOrder);

		return dbMigrateScriptNamesMap;
	}


	public static DatabaseManager getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseManager(context);
		}
		return instance;
	}

	public static DatabaseManager getInstanceAfterKilledByOS(Context context) {
		instance = new DatabaseManager(context);
		return instance;
	}

	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager(KidApplication.getInstance().getAppContext());
		}
		return instance;
	}

	public static void setInstance(DatabaseManager dbHelper) {
		instance = dbHelper;
	}

	private DatabaseManager(Context context) {
		if (context == null) {
			throw new RuntimeException("PASSED Context is NULL!");
		}

		mCtx = context.getApplicationContext();
		mDbFile = context.getDatabasePath(DB_NAME);
		// Load SQL Cipher libraries first
//		SQLiteDatabase.loadLibs(mCtx);
	}

	public synchronized void createDb(String pass) {

		// Create a new DB
		SQLCipherHelper sqlCipherHelper = new SQLCipherHelper(mCtx);
		// Then Open/Create database using a password

		mDb = sqlCipherHelper.getWritableDatabase(pass);
		SqliteCipherUtil.checkMigrateDatabaseVersion(mDb);
		// Disable the HMAC functionality on DB to make it compatible with SQLCipher 1.x
//		mDb.execSQL("PRAGMA cipher_use_hmac = OFF;");
//
//		//WHY must Re-Key Here while DB is Just Keyed by 'app_db_default_password' during its creation???
		String rekeySql = String.format("PRAGMA rekey = '%s'", pass);
		mDb.execSQL(rekeySql);

		mSqlCipherDbPassword = pass;
	}

	public static SQLiteDatabaseHook createSQLiteDatabaseHook() {
		SQLiteDatabaseHook dbHook = new SQLiteDatabaseHook() {
			@Override
			public void preKey(SQLiteDatabase database) {
				// Prepare to open 1.x databases or 2.x databases with the HMAC
				// functionality disabled
//                database.execSQL("PRAGMA cipher_default_use_hmac = OFF;");
			}

			@Override
			public void postKey(SQLiteDatabase database) {
				SqliteCipherUtil.checkMigrateDatabaseVersion(database);
			}
		};

		return dbHook;
	}

	public synchronized boolean openDb(String sqlCipherDbPassword) {
		if (mDb != null && mDb.isOpen()) {
//			return true;
			return checkDbKey(sqlCipherDbPassword);
		}

		try {
			mDb = SQLiteDatabase.openDatabase(mDbFile.getAbsolutePath(), sqlCipherDbPassword, null, SQLiteDatabase.OPEN_READWRITE, DatabaseManager.createSQLiteDatabaseHook());
			mSqlCipherDbPassword = sqlCipherDbPassword;
			return mDb.isOpen();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean checkDbKey(String sqlCipherDbPassword) {
		try {
			SQLiteDatabase sqliteDatabase = SQLiteDatabase.openDatabase(mDbFile.getAbsolutePath(), sqlCipherDbPassword, null, SQLiteDatabase.OPEN_READWRITE,
					DatabaseManager.createSQLiteDatabaseHook());
			boolean checkDbKeyResult = sqliteDatabase.isOpen();
//			sqliteDatabase.close();
			return checkDbKeyResult;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized void closeDb() {
		if (mDb != null && mDb.isOpen()) {
			mDb.close();
			mDb = null;
		}
	}

	public synchronized void deleteDbFile() {
		closeDb();
		if (mDbFile != null && isDbFileExisted()) {
			mDbFile.delete();
		}
	}

	public void migrateDb() throws IOException {
		if (mFromDbSchemaVersion == null) {
			mFromDbSchemaVersion = getDbSchemaVersion();
		}
		migrateDb(mFromDbSchemaVersion);
	}

	private void migrateDb(String fromDbSchemaVersion) throws IOException {
		String[] migrateSqlScriptNames = DB_MIGRATE_SCRIPT_NAMES_MAP.get(fromDbSchemaVersion);
		if (migrateSqlScriptNames != null && migrateSqlScriptNames.length > 0) {
			mDb.beginTransaction();
			for (String sqlScriptName : migrateSqlScriptNames) {
				runSqlScript(sqlScriptName);
			}
			mDb.setTransactionSuccessful();
			mDb.endTransaction();
		}
	}

	public void upgradeBackupDb(SQLiteDatabase backupDb) throws IOException {
		String backupDbSchemaVersion = getDbSchemaVersion(backupDb);
		final float backupDbVersion = Float.parseFloat(backupDbSchemaVersion);
		if (backupDbVersion < DB_SCHEMA_VERSION) {
			String[] migrateSqlScriptNames = DB_MIGRATE_SCRIPT_NAMES_MAP.get(backupDbSchemaVersion);
			if (migrateSqlScriptNames != null && migrateSqlScriptNames.length > 0) {
				backupDb.beginTransaction();
				for (String sqlScriptName : migrateSqlScriptNames) {
					runSqlScript(backupDb, sqlScriptName);
				}
				backupDb.setTransactionSuccessful();
				backupDb.endTransaction();
			}
		}
	}

	/*public void mergeBackupDbIntoLocalDb(SQLiteDatabase backupDb) {
		mergeFromDbIntoDb(backupDb, mDb);
	}

	public void mergeLocalDbIntoBackupDb(SQLiteDatabase backupDb) {
		mergeFromDbIntoDb(mDb, backupDb);
	}

	private void mergeFromDbIntoDb(SQLiteDatabase fromDb, SQLiteDatabase intoDb) {
		intoDb.beginTransaction();

		// Used to track oldCateId and newCateId so that later items with
		// oldCateId need to change category_id to newCateId
		HashMap<String, String> map_oldCateId_newCateId = new HashMap<String, String>();

		// Categories (both template and custom), Classes (both template and
		// custom) & their Template Fields
		mergeCates(fromDb, intoDb, map_oldCateId_newCateId);

		// Items (both template and custom plus uncategorized as well) & their
		// Fields
		mergeItems(fromDb, intoDb);

		if (!map_oldCateId_newCateId.isEmpty()) {
			// Change items' category_id from oldCateId to newCateId
			Set<String> oldCateIds = map_oldCateId_newCateId.keySet();
			for (String oldCateId : oldCateIds) {
				String newCateId = map_oldCateId_newCateId.get(oldCateId);
				changeItemsCategoryId(intoDb, oldCateId, newCateId);
			}
		}

		intoDb.setTransactionSuccessful();
		intoDb.endTransaction();
	}*/

	/*private void changeItemsCategoryId(SQLiteDatabase db, String oldCateId, String newCateId) {
		ContentValues values = new ContentValues();
		values.put(ItemTable.COLUMN_CATEGORY_ID, newCateId);
		String whereClause = ItemTable.COLUMN_CATEGORY_ID + " = ? ";
		String[] whereArgs = { oldCateId };
		db.update(ItemTable.TABLE_NAME, values, whereClause, whereArgs);
	}

	private void mergeItems(SQLiteDatabase fromDb, SQLiteDatabase intoDb) {
		List<Item> fromItems = getAllItems(fromDb);
		for (Item fromItem : fromItems) {
			mergeItem(fromItem, intoDb);
		}
	}

	private void mergeCates(SQLiteDatabase fromDb, SQLiteDatabase intoDb, Map<String, String> map_oldCateId_newCateId) {
		// Merge both system-predefined (cause predefined categories can be
		// deleted by user) & user-created categories
		List<Category> fromCategories = getAllCates(fromDb);
		for (Category fromCate : fromCategories) {
			mergeCate(fromCate, intoDb, map_oldCateId_newCateId);
		}
	}

	private List<Category> getAllCates(SQLiteDatabase db) {
		CategoryTable categoryTable = new CategoryTable(db);
		return categoryTable.getAllRows();
	}

	private List<Item> getAllItems(SQLiteDatabase db) {
		ItemTable itemTable = new ItemTable(db);
		return itemTable.getAllRows();
	}

	private void mergeCate(Category fromCate, SQLiteDatabase intoDb, Map<String, String> map_oldCateId_newCateId) {
		Category intoCate = findCateByIdInDb(fromCate.getId(), intoDb);
		if (intoCate == null) {
			String fromCateLastModified = fromCate.getLastModified();
			if (!DateTimeUtils.isUTCString(fromCateLastModified)) {
				String lastModifiedInStandardizedUTC = DateTimeUtils.toStandardizedUTCFromGMT(fromCateLastModified);
				if (lastModifiedInStandardizedUTC != null) {
					fromCate.setLastModified(lastModifiedInStandardizedUTC);
				} else {
					fromCate.setLastModified(DateTimeUtils.getCurrentDateTime());
				}
			}

			// Check if the intoDb contains a category that has the same name as
			// fromCate
			intoCate = findCateByNameInDb(fromCate.getName(), intoDb);
			if (intoCate == null) {
				insertCate(fromCate, intoDb);
			} else {
				String intoCateLastModified = intoCate.getLastModified();
				try {
					String oldCateId = null;
					String newCateId = null;
					if (DateTimeUtils.compare(fromCateLastModified, intoCateLastModified) > 0) {
						replaceCate(intoCate, fromCate, intoDb);

						oldCateId = intoCate.getId();
						newCateId = fromCate.getId();
					} else {
						oldCateId = fromCate.getId();
						newCateId = intoCate.getId();
					}
					map_oldCateId_newCateId.put(oldCateId, newCateId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			String fromCateLastModified = fromCate.getLastModified();
			boolean isLastModifiedDateFormatValid = false;
			if (DateTimeUtils.isUTCString(fromCateLastModified)) {
				isLastModifiedDateFormatValid = true;
			} else if (DateTimeUtils.isGMTString(fromCateLastModified)) {
				isLastModifiedDateFormatValid = true;
				fromCateLastModified = DateTimeUtils.toStandardizedUTCFromGMT(fromCateLastModified);
				fromCate.setLastModified(fromCateLastModified);
			}
			String intoCateLastModified = intoCate.getLastModified();
			try {
				if (isLastModifiedDateFormatValid
						&& DateTimeUtils.compare(fromCateLastModified, intoCateLastModified) > 0) {
					replaceCate(intoCate, fromCate, intoDb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void insertClassAndItsTemplateFields(Class fromClass, SQLiteDatabase intoDb) {
		insertClass(fromClass, intoDb);
		List<ClassTemplateField> classTemplateFields = fromClass.getClassTemplateFields();
		for (ClassTemplateField classTemplateField : classTemplateFields) {
			insertClassTemplateField(fromClass.getId(), classTemplateField, intoDb);
		}
	}

	private void insertClass(Class fromClass, SQLiteDatabase intoDb) {
		ContentValues values = new ContentValues();
		values.put(ClassTable.COLUMN_ID, fromClass.getId());
		values.put(ClassTable.COLUMN_NAME, fromClass.getName());
		values.put(ClassTable.COLUMN_PARENT_ID, fromClass.getParentId());
		intoDb.insert(ClassTable.TABLE_NAME, null, values);
	}

	private long insertClassTemplateField(String classId, ClassTemplateField classTemplateField,
			SQLiteDatabase intoDb) {
		ContentValues values = new ContentValues();
		values.put(ClassTemplateFieldTable.COLUMN_CLASS_ID, classId);
		values.put(ClassTemplateFieldTable.COLUMN_ID, classTemplateField.getId());
		values.put(ClassTemplateFieldTable.COLUMN_INDEX, classTemplateField.getIndex());
		values.put(ClassTemplateFieldTable.COLUMN_IS_MANDATORY,
				classTemplateField.isMandatory() ? BaseTable.BOOL_Y : BaseTable.BOOL_N);
		values.put(ClassTemplateFieldTable.COLUMN_IS_SEARCHABLE,
				classTemplateField.isSearchable() ? BaseTable.BOOL_Y : BaseTable.BOOL_N);
		values.put(ClassTemplateFieldTable.COLUMN_IS_SUBTITLE,
				classTemplateField.isSubtitle() ? BaseTable.BOOL_Y : BaseTable.BOOL_N);
		values.put(ClassTemplateFieldTable.COLUMN_TITLE, classTemplateField.getTitle());
		values.put(ClassTemplateFieldTable.COLUMN_TYPE_ID, classTemplateField.getFieldTypeId());
		values.put(ClassTemplateFieldTable.COLUMN_VALUE, classTemplateField.getValue());
		long insertResult = intoDb.insert(ClassTemplateFieldTable.TABLE_NAME, null, values);
		return insertResult;
	}*/



	private void runSqlScript(String scriptFileName) throws IOException {
		runSqlScript(mDb, scriptFileName);
	}

	public void runSqlScript(SQLiteDatabase db, String scriptFileName) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(mCtx.getAssets().open(scriptFileName)));

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

			/* Execute script if it ends with ; */
			if (tmp.endsWith(";")) {
				try {
					db.execSQL(sql);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* For next statement in the script */
				sql = "";
			}
		}

		br.close();
		br = null;
		System.gc();
	}

	public boolean needDbMigration() {
		// Get DB Schema Version from table 'setting/settings'
		mFromDbSchemaVersion = getDbSchemaVersion();
		// return !DB_SCHEMA_VERSION.equalsIgnoreCase(mFromDbSchemaVersion);
		final float dbVersion = Float.parseFloat(mFromDbSchemaVersion);
		return (dbVersion < DB_SCHEMA_VERSION);
	}

	public String getDbSchemaVersion() {
		return getDbSchemaVersion(mDb);
	}

	public String getDbSchemaVersion(SQLiteDatabase db) {
		// NOTEs:
		// WIDA 1.0 has an EMPTY table named 'setting'
		// While WIDA 2.0 or above instead has a table named 'settings' whose
		// contents includes at least a predefined 'db_schema_version' setting
		// WIDA's First DbSchemaVersion is "2.5"
		// WIDA's Current DbSchemaVersion is "3.6"

		String dbSchemaVersion = "1.0";
		String sql = "SELECT value FROM settings WHERE name = ?";
		String[] selectionArgs = { "db_schema_version" };
		Cursor dbSchemaVersionCursor = null;
		try {
			dbSchemaVersionCursor = db.rawQuery(sql, selectionArgs);
			if (dbSchemaVersionCursor != null && dbSchemaVersionCursor.moveToFirst()) {
				dbSchemaVersion = dbSchemaVersionCursor.getString(dbSchemaVersionCursor.getColumnIndex("value"));
				if (dbSchemaVersion != null) {
					dbSchemaVersion = dbSchemaVersion.trim();
				}
			}
			if (dbSchemaVersionCursor != null) {
				dbSchemaVersionCursor.close();
			}
		} catch (Throwable t) {
			Log.e(TAG, "getDbSchemaVersion() => EXCEPTION:", t);

			// No such table: 'settings'
			sql = "SELECT value FROM setting WHERE name = ?";
			try {
				dbSchemaVersionCursor = db.rawQuery(sql, selectionArgs);
				if (dbSchemaVersionCursor != null) {
					dbSchemaVersionCursor.close();
				}
			} catch (Throwable t2) {
				Log.e(TAG, "getDbSchemaVersion() => EXCEPTION:", t2);
			}
		}

		return dbSchemaVersion;
	}

	public boolean isDbFileExisted() {
		File databasesFilePath = new File(getDbFileFullPath(KidApplication.getInstance().getAppContext()));
		return databasesFilePath.exists();
	}

	public boolean reKey(String sqlCipherDbPassword) {
		if (mDb == null || !mDb.isOpen()) {
			openDb(sqlCipherDbPassword);
		}
		try {
			String rekeySql = String.format("PRAGMA rekey = '%s'", sqlCipherDbPassword);
			mDb.execSQL(rekeySql);

			mSqlCipherDbPassword = sqlCipherDbPassword;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Khiemnt: Only export database to sdcard.
	public static void exportDatabasesBackUp(){
		File dbFile = new File(DatabaseManager.getDbFileFullPath());
		if (dbFile.exists()){
			final String outFolder = Environment.getExternalStorageDirectory().toString().concat("/KidsBrain");
			final File outFile = new File(outFolder);
			outFile.mkdirs();

			File tmp = new File(outFolder, dbFile.getName());
			try {
				FileUtils.copyFile(dbFile, tmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Khiemnt: Remove export db folder.
	public static void removeExportDatabases(){
		final String outFolder = Environment.getExternalStorageDirectory().toString().concat("/KidsBrain");
		final File outFile = new File(outFolder);
		if (outFile.isDirectory()) {
			File[] files = outFile.listFiles();
			if (files != null) {
				for (File f : files) {
					f.delete();
				}
			}
			outFile.delete();
		}
	}


}
