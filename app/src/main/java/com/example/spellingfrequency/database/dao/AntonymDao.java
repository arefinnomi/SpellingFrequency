package com.example.spellingfrequency.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.spellingfrequency.database.entity.AntonymEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;

@Dao
public interface AntonymDao {
    @Insert()
    void insertAll(AntonymEntity... antonymEntities);

    @Query("SELECT * FROM Antonym")
    AntonymEntity[] loadAllAntonym();

    @Query("select EnglishWord.* from Antonym inner join EnglishWord on EnglishWord.id = Antonym.antonym_id Where Antonym.english_id = :wordId order by EnglishWord.frequency desc")
    EnglishWordEntity[] loadAntonymByWord(int wordId);

    @Query("DELETE FROM Antonym")
    void nukeTable();
}
