package com.kid.brain.provider.database;

import android.content.Context;

import com.kid.brain.models.Account;
import com.kid.brain.models.Category;
import com.kid.brain.models.Item;
import com.kid.brain.models.Kid;
import com.kid.brain.models.Level;
import com.kid.brain.models.Question;
import com.kid.brain.models.Rate;
import com.kid.brain.provider.database.table.AccountTable;
import com.kid.brain.provider.database.table.CategoryTable;
import com.kid.brain.provider.database.table.ItemTable;
import com.kid.brain.provider.database.table.KidTable;
import com.kid.brain.provider.database.table.LevelCateRateTable;
import com.kid.brain.provider.database.table.LevelTable;
import com.kid.brain.provider.database.table.QuestionTable;
import com.kid.brain.provider.database.table.RateTable;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.List;

/**
 * Created by khiemnt on 2/24/17.
 */

public class KidRepository {

    private SQLiteDatabase mDb;

    private AccountTable mAccountTable;
    private KidTable mKidTable;
    private ItemTable mItemTable;
    private LevelTable mLevelTable;
    private CategoryTable mCategoryTable;
    private QuestionTable mQuestionTable;
    private LevelCateRateTable mLevelCateRateTable;
    private RateTable mRateTable;

    private static KidRepository kidRepository;

    public static KidRepository newInstance(Context context) {
        kidRepository = new KidRepository(context);
        return kidRepository;
    }

    public static KidRepository getInstance(Context context) {
        if (kidRepository == null) {
            kidRepository = new KidRepository(context);
        }
        return kidRepository;
    }

    public KidRepository(Context context) {
        mDb = DatabaseManager.getInstance(context).getDb();

        mAccountTable = new AccountTable(context);
        mKidTable = new KidTable(context);
        mItemTable = new ItemTable(context);
        mLevelTable = new LevelTable(context);
        mCategoryTable = new CategoryTable(context);
        mQuestionTable = new QuestionTable(context);
        mLevelCateRateTable = new LevelCateRateTable(context);
        mRateTable = new RateTable(context);

    }

    public void resetDatabase(){
        try {
            mAccountTable.deleteAccounts(mDb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mKidTable.deleteKids(mDb);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**************************************
     * TODO: ACCOUNT TABLE
     * *************************************/
    public long saveAccount(Account account) throws Exception {
        return mAccountTable.saveAccount(account, mDb);
    }

    public int updateAccount(Account account) throws Exception {
        return mAccountTable.updateAccount(account, mDb);
    }

    public boolean checkExistAccount(String accountId) throws Exception {
        return mAccountTable.checkExistAccount(accountId, mDb);
    }

    public Account getAccount() throws Exception {
        return mAccountTable.getAccount(mDb);
    }


    /**************************************
     * TODO: KID TABLE
     * *************************************/
    public long saveKid(Kid kid) throws Exception {
        return mKidTable.saveKid(kid, mDb);
    }

    public long saveKids(List<Kid> kids) throws Exception {
        return mKidTable.saveKids(kids, mDb);
    }

    public long deleteKid(String kidId) throws Exception {
        return mKidTable.deleteKid(kidId, mDb);
    }

    public Kid getKid(String kidId) throws Exception {
        return mKidTable.getKid(kidId, mDb);
    }

    public List<Kid> getKids(String parentId) throws Exception {
        return mKidTable.getKids(parentId, mDb);
    }

    /**************************************
     * TODO: LEVEL TABLE
     * ************************************/
    public List<Item> getItems() throws Exception {
        return mItemTable.getItems(mDb);
    }

    /**************************************
     * TODO: LEVEL TABLE
     * ************************************/
    public long saveLevel(Level level) throws Exception {
        return mLevelTable.saveLevel(level, mDb);
    }

    public long saveLevels(List<Level> levels) throws Exception {
        return mLevelTable.saveLevels(levels, mDb);
    }

    public int updateLevel(Level level) throws Exception {
        return mLevelTable.updateLevel(level, mDb);
    }

    public boolean checkExistLevel(String levelId) throws Exception {
        return mLevelTable.checkExistLevel(levelId, mDb);
    }

    public Level getLevel(String levelId) throws Exception {
        return mLevelTable.getLevel(levelId, mDb);
    }

    public List<Level> getLevels() throws Exception {
        return mLevelTable.getLevels(mDb);
    }

    /**************************************
     * TODO: CATEGORY TABLE
     * ************************************/
    public long saveCategory(String levelId, Category category) throws Exception {
        return mCategoryTable.saveCategory(levelId, category, mDb);
    }

    public long saveCategories(String levelId, List<Category> categories) throws Exception {
        return mCategoryTable.saveCategories(levelId, categories, mDb);
    }

    public int updateCategory(String levelId, Category category) throws Exception {
        return mCategoryTable.updateCategory(levelId, category, mDb);
    }

    public boolean checkExistCategory(String levelId, String cateId) throws Exception {
        return mCategoryTable.checkExistCategory(levelId, cateId, mDb);
    }

    public Category getCategory(String levelId, String cateId) throws Exception {
        return mCategoryTable.getCategory(levelId, cateId, mDb);
    }

    public List<Category> getCategories(String levelId) throws Exception {
        return mCategoryTable.getCategories(levelId, mDb);
    }

    /**************************************
     * TODO: QUESTION TABLE
     * ************************************/
    public long saveQuestion(String levelId, String cateId, Question question) throws Exception {
        return mQuestionTable.saveQuestion(levelId, cateId, question, mDb);
    }

    public long saveQuestions(String levelId, String cateId, List<Question> questions) throws Exception {
        return mQuestionTable.saveQuestions(levelId, cateId, questions, mDb);
    }

    public int updateQuestion(String levelId, String cateId, Question question) throws Exception {
        return mQuestionTable.updateQuestion(levelId, cateId, question, mDb);
    }

    public Question getQuestion(String questionId) throws Exception {
        return mQuestionTable.getQuestion(questionId, mDb);
    }

    public List<Question> getQuestions(String levelId, String cateId) throws Exception {
        return mQuestionTable.getQuestions(levelId, cateId, mDb);
    }

    /**************************************
     * TODO: LEVE CATE RATE TABLE
     * ************************************/
    public long saveLevelCateRate(String levelId, String cateId, String rateId, int score) throws Exception {
        return mLevelCateRateTable.saveData(levelId, cateId, rateId, score, mDb);
    }

    public List<Rate> getLeveCateRates(String levelId, String cateId) throws Exception {
        return mLevelCateRateTable.getRates(levelId, cateId, mDb);
    }

    /**************************************
     * TODO: RATE TABLE
     * ************************************/
    public long saveRate(Rate rate, String levelId, String cateId) throws Exception {
        return mRateTable.saveRate(rate, levelId, cateId, mDb);
    }

    public long saveRates(List<Rate> rates, String levelId, String cateId) throws Exception {
        return mRateTable.saveRates(rates, levelId, cateId, mDb);
    }

    public int updateRate(Rate rate, String levelId, String cateId) throws Exception {
        return mRateTable.updateRate(rate, levelId, cateId, mDb);
    }

    public Rate getRate(String rateId) throws Exception {
        return mRateTable.getRate(rateId, mDb);
    }

    public List<Rate> getRates(String levelId, String cateId) throws Exception {
        return mRateTable.getRates(levelId, cateId, mDb);
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

}
