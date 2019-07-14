package com.example.spellingfrequency.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.spellingfrequency.database.entity.EnglishWordEntity;

@Dao
public interface EnglishWordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEnglishWord(EnglishWordEntity englishWordEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(EnglishWordEntity... englishWordEntities);

    @Query("SELECT * FROM EnglishWord")
    EnglishWordEntity[] loadAllEnglishWord();

    @Query("SELECT * FROM EnglishWord WHERE text = :text")
    EnglishWordEntity loadEnglishWordByText(String text);


    @Query("DELETE FROM EnglishWord")
    void nukeTable();

    @Query("SELECT MAX(weight) as weight FROM EnglishWord")
    WeightPojo maxWeight();

    @Query("SELECT MIN(weight) as weight FROM EnglishWord")
    WeightPojo minWeight();

    @Query("UPDATE EnglishWord SET weight = :weight Where id = :id")
    void updateWeight(int id, int weight);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(EnglishWordEntity englishWordEntity);

    @Update
    int updateMulti(EnglishWordEntity... englishWordEntities);

    @Query("select *, min(weight)from EnglishWord")
    EnglishWordEntity[] loadMinWeightWord();

    @Query("Select count(*) from EnglishWord where repeat = 0")
    int countMasteredWord();

    @Query("Select count(*) from EnglishWord")
    int totalEnglishWord();

    @Query("Select count(*) from EnglishWord where repeat != 0 and weight!= id")
    int countCurrentErrorEnglishWord();

    @Query("Select * from EnglishWord where repeat != 0 and weight!= id")
    EnglishWordEntity[] loadAllCurrentErrorEnglishWord();

    @Query("Select * from EnglishWord where favourite = 1")
    EnglishWordEntity[] loadAllFavorites();


    @Query("Select * from EnglishWord where (repeat != 0 and weight!= id) or favourite = 1")
    EnglishWordEntity[] loadAllFavoriteAndErrorEnglishWord();

    @Query("Select * from EnglishWord where favourite = 1 or id != weight")
    EnglishWordEntity[] loadAllChanged();

    class WeightPojo {
        public int weight;
    }
}
