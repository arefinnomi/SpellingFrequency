package com.example.spellingfrequency.model;


import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.BanglaWordEntity;
import com.example.spellingfrequency.database.entity.CurrentWeightEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.round;

public class Word {
    private final static Random random = new Random();
    private BanglaWordEntity[] banglaWordEntities;
    private Map<EnglishWordEntity, BanglaWordEntity[]> synonymWordsWithBangla = new LinkedHashMap<>();
    private Map<EnglishWordEntity, BanglaWordEntity[]> antonymWordsWithBangla = new HashMap<>();
    private AppDatabase appDatabase;
    private EnglishWordEntity englishWordEntity;


    public Word(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public String getWord() {
        return englishWordEntity.getText();
    }

    public BanglaWordEntity[] getBanglaWords() {
        return banglaWordEntities;
    }

    public Map<EnglishWordEntity, BanglaWordEntity[]> getSynonymWordsWithBangla() {
        return synonymWordsWithBangla;
    }

    public Map<EnglishWordEntity, BanglaWordEntity[]> getAntonymWordsWithBangla() {
        return antonymWordsWithBangla;
    }

    public boolean isFavorite() {
        return englishWordEntity.isFavourite();
    }

    public void loadNextWord() {
        EnglishWordEntity[] englishWordEntities = this.appDatabase.englishWordDao().loadMinWeightWord();
        this.englishWordEntity = englishWordEntities[0];
        this.banglaWordEntities = this.appDatabase.banglaWordDao().loadBanglaWordsByEnglishWordId(this.englishWordEntity.getId());
        EnglishWordEntity[] synonymWords = this.appDatabase.synonymDao().loadSynonymByWord(this.englishWordEntity.getId());
        for (EnglishWordEntity synonymWord : synonymWords) {
            BanglaWordEntity[] banglaWordEntities = this.appDatabase.banglaWordDao().loadBanglaWordsByEnglishWordId(synonymWord.getId());
            synonymWordsWithBangla.put(synonymWord, banglaWordEntities);
        }
        EnglishWordEntity[] antonymWords = this.appDatabase.antonymDao().loadAntonymByWord(this.englishWordEntity.getId());
        for (EnglishWordEntity antonymWord : antonymWords) {
            BanglaWordEntity[] bangleWordEntities = this.appDatabase.banglaWordDao().loadBanglaWordsByEnglishWordId(antonymWord.getId());
            antonymWordsWithBangla.put(antonymWord, bangleWordEntities);
        }
    }

    public void loadWord(String word) {
        EnglishWordEntity englishWordEntities = appDatabase.englishWordDao().loadEnglishWordByText(word);
        this.englishWordEntity = englishWordEntities;
        this.banglaWordEntities = this.appDatabase.banglaWordDao().loadBanglaWordsByEnglishWordId(this.englishWordEntity.getId());
        EnglishWordEntity[] synonymWords = this.appDatabase.synonymDao().loadSynonymByWord(this.englishWordEntity.getId());
        for (EnglishWordEntity synonymWord : synonymWords) {
            BanglaWordEntity[] bangleWordEntities = this.appDatabase.banglaWordDao().loadBanglaWordsByEnglishWordId(synonymWord.getId());
            synonymWordsWithBangla.put(synonymWord, bangleWordEntities);
        }
        EnglishWordEntity[] antonymWords = this.appDatabase.antonymDao().loadAntonymByWord(this.englishWordEntity.getId());
        for (EnglishWordEntity antonymWord : antonymWords) {
            BanglaWordEntity[] bangleWordEntities = this.appDatabase.banglaWordDao().loadBanglaWordsByEnglishWordId(antonymWord.getId());
            antonymWordsWithBangla.put(antonymWord, bangleWordEntities);
        }
    }

    public void saveCurrentWordStatus(boolean mastered) {
        CurrentWeightEntity currentWeightEntity = appDatabase.currentWeightDao().loadCurrentMaxWeight();
        if (mastered && englishWordEntity.getRepeat() > 0) {
            englishWordEntity.setRepeat(englishWordEntity.getRepeat() - 1);
        } else {
            int previousRepeat = englishWordEntity.getRepeat();
            int newRepeat = (int) round(0.916999591 * previousRepeat
                    + 9.869104663 * exp(-0.658625544 * previousRepeat)
                    + 9.153025322 * pow(10, -2));
            englishWordEntity.setRepeat(newRepeat);
        }
        if (englishWordEntity.getRepeat() == 0) {
            englishWordEntity.setWeight(currentWeightEntity.getWeight() + 1);
        } else {
            int pos = (int) round(exp(3.6570 - 0.1943 * englishWordEntity.getRepeat()));
            englishWordEntity.setWeight(englishWordEntity.getWeight() + getRandomNumberInts(pos, pos + 5));
        }

        appDatabase.englishWordDao().update(englishWordEntity);
        currentWeightEntity.setWeight(max(currentWeightEntity.getWeight(), englishWordEntity.getWeight()));
        appDatabase.currentWeightDao().updateCurrentWeight(currentWeightEntity);


    }

    public void saveFavorite(boolean favourite) {
        englishWordEntity.setFavourite(favourite);
        appDatabase.englishWordDao().update(englishWordEntity);


    }

    public int getRandomNumberInts(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

}
