package com.example.spellingfrequency.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "Antonym", foreignKeys = {
        @ForeignKey(entity = EnglishWordEntity.class, parentColumns = "id", childColumns = "english_id"),
        @ForeignKey(entity = EnglishWordEntity.class, parentColumns = "id", childColumns = "antonym_id")})
public class AntonymEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public int english_id;
    @NonNull
    public int antonym_id;

    public AntonymEntity(int english_id, int antonym_id) {
        this.english_id = english_id;
        this.antonym_id = antonym_id;
    }

}