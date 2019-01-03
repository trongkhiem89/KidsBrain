package com.kid.brain.managers.help;

import android.text.TextUtils;

import com.kid.brain.models.Account;
import com.kid.brain.models.Category;
import com.kid.brain.models.History;
import com.kid.brain.models.Kid;
import com.kid.brain.models.Level;
import com.kid.brain.models.Question;
import com.kid.brain.models.QuestionScore;
import com.kid.brain.models.Rate;
import com.kid.brain.util.log.ALog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KidBean {

    private static String TAG = KidBean.class.getName();
    private static KidBean mKidBean;

    public static KidBean getInstance() {
        if (mKidBean == null) {
            mKidBean = new KidBean();
        }
        return mKidBean;
    }

    private Account mAccount;

    private List<Kid> mKids = new ArrayList<>();

    private List<Level> mLevel = new ArrayList<>();

    private List<History> mHistories = new ArrayList<>();

    /**
     * {String} levelId Key
     * {List<Category>} values
     */
    private Map<String, List<Category>> mMapCategories = new HashMap<>();

    /**
     * {String} levelId_categoryId Key
     * {List<Question>} values
     */
    private Map<String, List<Question>> mMapQuestion = new HashMap<>();

    private List<Rate> mRates = new ArrayList<>();

    /**
     * Contain score of level and category
     */
    private List<QuestionScore> mQuestionScores = new ArrayList<>();

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account mAccount) {
        this.mAccount = mAccount;
    }

    public synchronized void saveLevels(final List<Level> levels) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (levels != null && levels.size() > 0) {
                    mLevel.clear();
                    mLevel = levels;

                    for (Level level : mLevel) {
                        saveCategoriesByLevel(String.valueOf(level.getId()), level.getCategories());
                    }
                    ALog.i(TAG, " >>>> Levels are saved with size = " + mLevel.size());
                }
            }
        }).run();
    }

    public List<Level> getLevels() {
        return this.mLevel;
    }

    public void saveQuestionByLevelIdCateId(final String levelId, final String cateId, final List<Question> questions) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(levelId) || TextUtils.isEmpty(cateId)) return;
                if (questions != null && questions.size() > 0) {
                    String levelIdCateId = levelId.concat("_").concat(cateId);
                    boolean isExists = mMapQuestion.containsKey(levelIdCateId);
                    if (isExists) {
                        mMapQuestion.remove(levelIdCateId);
                    }
                    mMapQuestion.put(levelIdCateId, questions);


                    ALog.i(TAG, " >>>> Question are saved with size = " + questions.size());
                }
            }
        }).run();
    }

    public List<Question> getQuestions(String levelId, String cateId) {
        List<Question> outQuestions = new ArrayList<>();
        if (TextUtils.isEmpty(levelId) || TextUtils.isEmpty(cateId)) return outQuestions;
        if (mMapQuestion == null) return outQuestions;

        String levelIdCateId = levelId.concat("_").concat(cateId);
        if (mMapQuestion.containsKey(levelIdCateId)) {
            outQuestions = mMapQuestion.get(levelIdCateId);
        }

        List<Question> outQuestionsClone = new ArrayList<>();
        outQuestionsClone.addAll(outQuestions);
        return outQuestionsClone;
    }

    public synchronized void saveCategoriesByLevel(final String levelId, final List<Category> categories) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(levelId)) return;
                if (categories != null && categories.size() > 0) {
                    boolean isExists = mMapCategories.containsKey(levelId);
                    if (isExists) {
                        mMapCategories.remove(levelId);
                    }
                    mMapCategories.put(levelId, categories);
                    ALog.i(TAG, " >>>> Categories are saved with size = " + categories.size());
                }
            }
        }).run();
    }

    public List<Category> getCategories(String levelId) {
        List<Category> outCategories = new ArrayList<>();
        if (mMapCategories == null || TextUtils.isEmpty(levelId)) return outCategories;
        if (mMapCategories.containsKey(levelId)) {
            outCategories = mMapCategories.get(levelId);
        }

        List<Category> outCategoriesClone = new ArrayList<>();
        outCategoriesClone.addAll(outCategories);
        return outCategoriesClone;
    }

    public synchronized void saveRate(final List<Rate> rates) {
        if (rates != null && rates.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Rate rateNew : rates) {
                        boolean isExists = false;
                        for (Rate rate : mRates) {
                            if (rateNew.getId() == rate.getId()) {
                                isExists = true;
                                break;
                            }
                        }
                        if (!isExists) {
                            mRates.add(rateNew);
                        }
                    }
                }
            }).run();
        }
    }

    public Rate findRateByScore(int inputScore) {
        Rate outRate = new Rate();
        if (mRates == null || mRates.size() == 0) {
            return outRate;
        }

        for (Rate rate : mRates) {
            if (!TextUtils.isEmpty(rate.getScore()) && rate.getScore().contains(",")) {
                String[] scores = rate.getScore().split(",");
                for (String score : scores) {
                    if (Integer.parseInt(score) == inputScore) {
                        return rate;
                    }
                }
            }
        }

        return outRate;
    }

    public void addScore(QuestionScore questionScore) {
        if (isExistQuestionScore(questionScore)) {
            resetScoreRateQuestionIds(questionScore);
        } else {
            if (this.mQuestionScores == null) {
                this.mQuestionScores = new ArrayList<>();
            }
            this.mQuestionScores.add(questionScore);
        }
    }

    private boolean isExistQuestionScore(QuestionScore questionScore) {
        if (this.mQuestionScores == null || this.mQuestionScores.size() == 0) return false;
        for (QuestionScore qc : this.mQuestionScores) {
            if (qc.getLevel().getAgeId().equals(questionScore.getLevel().getAgeId())
                    && qc.getCategory().getId() == questionScore.getCategory().getId()) {
                return true;
            }
        }
        return false;
    }

    private void resetScoreRateQuestionIds(QuestionScore questionScore) {
        for (QuestionScore qc : this.mQuestionScores) {
            if (qc.getLevel().getAgeId().equals(questionScore.getLevel().getAgeId())
                    && qc.getCategory().getId() == questionScore.getCategory().getId()) {
                qc.setRateId(questionScore.getRateId());
                qc.setScore(questionScore.getScore());
                qc.setQuestionIds(questionScore.getQuestionIds());
                break;
            }
        }
    }

    public List<QuestionScore> getQuestionScores() {
        return mQuestionScores;
    }

    public void resetQuestionScore() {
        if (mQuestionScores != null) {
            mQuestionScores.clear();
        }
    }

    public List<Kid> getKids() {
        return mKids;
    }

    public void saveKid(Kid inputKid) throws NullPointerException {
        if (mKids == null) {
            mKids = new ArrayList<>();
        }

        if (this.checkKidExists(inputKid, mKids)) {
            this.updateKid(inputKid, mKids);
        } else {
            mKids.add(inputKid);
        }
    }

    public boolean checkKidExists(Kid inputKid, List<Kid> kids) throws NullPointerException {
        for (Kid kid : kids) {
            if (kid.getChildrenId().equalsIgnoreCase(inputKid.getChildrenId())) {
                return true;
            }
        }
        return false;
    }

    public void updateKid(Kid inputKid, List<Kid> kids) throws NullPointerException {
        for (Kid kid : kids) {
            if (kid.getChildrenId().equalsIgnoreCase(inputKid.getChildrenId())) {
                kid.setPhoto(inputKid.getPhoto());
                kid.setUsername(inputKid.getUsername());
                kid.setBirthDay(inputKid.getBirthDay());
                kid.setGender(inputKid.getGender());
                break;
            }
        }
    }

    public List<History> getHistories() {
        if (mHistories != null) {
            mHistories.clear();
        }

        for (int i = 0; i < 50; i++) {
            History history = new History();
            history.setId(String.valueOf(i));
            history.setDate(new Date().toString());
            mHistories.add(history);
        }

        return mHistories;
    }
}
