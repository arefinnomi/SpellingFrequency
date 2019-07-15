package com.example.spellingfrequency.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Synonym", foreignKeys = {
        @ForeignKey(entity = EnglishWordEntity.class, parentColumns = "id", childColumns = "english_id"),
        @ForeignKey(entity = EnglishWordEntity.class, parentColumns = "id", childColumns = "synonym_id")})
public class SynonymEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public int english_id;
    @NonNull
    public int synonym_id;
    @NonNull
    public int wordnet_serial;

    public SynonymEntity(int english_id, int synonym_id, int wordnet_serial) {
        this.english_id = english_id;
        this.synonym_id = synonym_id;
        this.wordnet_serial = wordnet_serial;
    }

}
