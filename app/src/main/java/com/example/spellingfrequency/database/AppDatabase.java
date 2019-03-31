package com.example.spellingfrequency.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.example.spellingfrequency.database.dao.AntonymDao;
import com.example.spellingfrequency.database.dao.BanglaWordDao;
import com.example.spellingfrequency.database.dao.CurrentWeightDao;
import com.example.spellingfrequency.database.dao.EnglishWordDao;
import com.example.spellingfrequency.database.dao.SynonymDao;
import com.example.spellingfrequency.database.entity.AntonymEntity;
import com.example.spellingfrequency.database.entity.BanglaWordEntity;
import com.example.spellingfrequency.database.entity.CurrentWeightEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.example.spellingfrequency.database.entity.SynonymEntity;


@Database(entities = {EnglishWordEntity.class, BanglaWordEntity.class, SynonymEntity.class, CurrentWeightEntity.class, AntonymEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "dictionary.db").allowMainThreadQueries().build();
                    Log.d("mytag", "getDatabase: not exist");
                }
            }
        }

        Log.d("mytag", "getDatabase: ");
        return INSTANCE;
    }

    public abstract EnglishWordDao englishWordDao();

    public abstract BanglaWordDao banglaWordDao();

    public abstract SynonymDao synonymDao();

    public abstract AntonymDao antonymDao();

    public abstract CurrentWeightDao currentWeightDao();
}
