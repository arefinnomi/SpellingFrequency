package com.example.spellingfrequency.internet;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.CurrentWeightEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.google.gson.Gson;

public class Synchronization {
    public static void synchronize(Context context) {
        final AppDatabase db = AppDatabase.getDatabase(context);
        generateJson(db);
    }

    private static String generateJson(@NonNull final AppDatabase db) {
        MergedData mergedData = new MergedData(db.currentWeightDao().loadAllCurrentWeight(), db.englishWordDao().loadAllChanged());
        Gson gson = new Gson();
        return gson.toJson(mergedData);
    }

    private static class MergedData {
        CurrentWeightEntity[] currentWeightEntities;
        EnglishWordEntity[] englishWordEntities;

        public MergedData(CurrentWeightEntity[] currentWeightEntities, EnglishWordEntity[] englishWordEntities) {
            this.currentWeightEntities = currentWeightEntities;
            this.englishWordEntities = englishWordEntities;
        }

        public CurrentWeightEntity[] getCurrentWeightEntities() {
            return currentWeightEntities;
        }

        public void setCurrentWeightEntities(CurrentWeightEntity[] currentWeightEntities) {
            this.currentWeightEntities = currentWeightEntities;
        }

        public EnglishWordEntity[] getEnglishWordEntities() {
            return englishWordEntities;
        }

        public void setEnglishWordEntities(EnglishWordEntity[] englishWordEntities) {
            this.englishWordEntities = englishWordEntities;
        }
    }
}
