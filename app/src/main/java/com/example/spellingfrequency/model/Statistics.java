package com.example.spellingfrequency.model;

import com.example.spellingfrequency.database.AppDatabase;


public class Statistics {
    private static int totalWord;
    private static int mastered;
    private static int mistaken;
    private static int due;
    private static AppDatabase appDatabase;

    public Statistics(AppDatabase appDatabase) {
        Statistics.appDatabase = appDatabase;
    }

    public String update() {
        totalWord = appDatabase.englishWordDao().totalEnglishWord();
        mastered = appDatabase.englishWordDao().countMasteredWord();
        mistaken = appDatabase.englishWordDao().countCurrentErrorEnglishWord();
        due = totalWord - mastered - mistaken;

        return toString();

    }

    public String toString() {
        return "total: " + totalWord + "\n"
                + "mastered: " + mastered + "\n"
                + "mistaken: " + mistaken + "\n"
                + "New: " + due;
    }
}
