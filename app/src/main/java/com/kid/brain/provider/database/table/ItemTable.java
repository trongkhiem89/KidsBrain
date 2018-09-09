package com.kid.brain.provider.database.table;

import android.content.Context;
import android.text.TextUtils;

import com.kid.brain.managers.help.KidPreference;
import com.kid.brain.models.Item;
import com.kid.brain.util.Constants;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class ItemTable extends BaseTable {
    private final String TAG = ItemTable.class.getSimpleName();

    public interface IItemTable {
        String TABLE_ITEM = "items";
        String TABLE_ITEM_LANGUAGE = "itemLanguages";

        String COLUMN_ID = "id";
        String COLUMN_NAME = "name";
        String COLUMN_ICON = "icon";
        String COLUMN_STATUS = "status";
        String COLUMN_SORT = "sort";

        String COLUMN_ITEM_ID = "itemId";
        String COLUMN_LANGUAGE_CODE = "langCode";
    }

    public ItemTable(Context context) {
        super(context);
    }

    public ItemTable(SQLiteDatabase db) {
        super(db);
    }

    public List<Item> getItems(SQLiteDatabase db) throws Exception {
        String languageCode = KidPreference.getStringValue(KidPreference.KEY_LANGUAGE_CODE);
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = Constants.Language.LANG_VI;
        }

        String rawQuery = "SELECT "
                + "it." + IItemTable.COLUMN_ID + ", "
                + "il." + IItemTable.COLUMN_NAME + ", "
                + "it." + IItemTable.COLUMN_ICON + ", "
                + "it." + IItemTable.COLUMN_SORT + ", "
                + "it." + IItemTable.COLUMN_STATUS + " "
                + "FROM " + IItemTable.TABLE_ITEM + " AS it "
                + "INNER JOIN " + IItemTable.TABLE_ITEM_LANGUAGE + " AS il "
                + "ON it." + IItemTable.COLUMN_ID + " = " + "il." + IItemTable.COLUMN_ITEM_ID + " "
                + "WHERE il." + IItemTable.COLUMN_LANGUAGE_CODE + " = '" + languageCode + "' "
                + "ORDER BY " + IItemTable.COLUMN_SORT + " ASC";

        List<Item> outItems = new ArrayList<>();
        Item item = null;
        try {
            Cursor cursor = db.rawQuery(rawQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    try {
                        String id = cursor.getString(cursor.getColumnIndex(IItemTable.COLUMN_ID));
                        String name = cursor.getString(cursor.getColumnIndex(IItemTable.COLUMN_NAME));
                        String icon = cursor.getString(cursor.getColumnIndex(IItemTable.COLUMN_ICON));
                        String status = cursor.getString(cursor.getColumnIndex(IItemTable.COLUMN_STATUS));
                        int order = cursor.getInt(cursor.getColumnIndex(IItemTable.COLUMN_SORT));

                        item = new Item();
                        item.setId(id);
                        item.setName(name);
                        item.setIcon(icon);
                        item.setStatus(status);
                        item.setSort(order);

                        outItems.add(item);
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

        return outItems;
    }


}
