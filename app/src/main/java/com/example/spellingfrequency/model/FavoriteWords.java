package com.example.spellingfrequency.model;

import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteWords {
    private List<EnglishWordEntity> englishWordEntities;
    private AppDatabase appDatabase;

    public FavoriteWords(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        this.englishWordEntities = new ArrayList<>();
        englishWordEntities.addAll(Arrays.asList(appDatabase.englishWordDao().loadAllFavoriteAndErrorEnglishWord()));
    }

    public void update() {
        this.englishWordEntities.clear();
        this.englishWordEntities.addAll(Arrays.asList(appDatabase.englishWordDao().loadAllFavoriteAndErrorEnglishWord()));
    }

    public List<EnglishWordEntity> getEnglishWordEntities() {
        return this.englishWordEntities;
    }

    public List<String> getEnglishWords() {
        List<String> words = new ArrayList<>();
        for (EnglishWordEntity englishWordEntity : englishWordEntities) {
            words.add(englishWordEntity.getText());
        }
        return words;
    }

}
