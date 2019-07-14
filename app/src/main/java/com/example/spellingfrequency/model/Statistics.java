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

    public static int getTotalWord() {
        return totalWord;
    }

    public static void setTotalWord(int totalWord) {
        Statistics.totalWord = totalWord;
    }

    public static int getMastered() {
        return mastered;
    }

    public static void setMastered(int mastered) {
        Statistics.mastered = mastered;
    }

    public static int getMistaken() {
        return mistaken;
    }

    public static void setMistaken(int mistaken) {
        Statistics.mistaken = mistaken;
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
