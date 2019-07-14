package com.example.spellingfrequency.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.spellingfrequency.database.entity.BanglaWordEntity;

@Dao
public interface BanglaWordDao {
    @Insert()
    void insertBanglaWord(BanglaWordEntity banglaWordEntity);

    @Insert()
    void insertAll(BanglaWordEntity... bnaglaWordEntities);

    @Query("SELECT * FROM BanglaWord")
    BanglaWordEntity[] loadAllBanglaWord();

    @Query("SELECT * FROM BanglaWord WHERE english_id = :englishWordId")
    BanglaWordEntity[] loadBanglaWordsByEnglishWordId(int englishWordId);

    @Query("DELETE FROM BanglaWord")
    void nukeTable();

}
