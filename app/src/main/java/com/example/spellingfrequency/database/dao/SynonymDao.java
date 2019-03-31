package com.example.spellingfrequency.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.example.spellingfrequency.database.entity.SynonymEntity;

@Dao
public interface SynonymDao {
    @Insert()
    void insertAll(SynonymEntity... synonymEntities);

    @Query("SELECT * FROM Synonym")
    SynonymEntity[] loadAllSynonym();

    @Query("select EnglishWord.* from Synonym " +
            "inner join EnglishWord on EnglishWord.id = Synonym.synonym_id " +
            "Where Synonym.english_id = :wordId " +
            "order by Synonym.wordnet_serial, EnglishWord.frequency desc")
    EnglishWordEntity[] loadSynonymByWord(int wordId);

    @Query("DELETE FROM Synonym")
    void nukeTable();
}
