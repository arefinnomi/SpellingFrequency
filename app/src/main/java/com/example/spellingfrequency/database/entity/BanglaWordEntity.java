package com.example.spellingfrequency.database.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "BanglaWord",
        indices = {@Index(value = {"text"})},
        foreignKeys = @ForeignKey(entity = EnglishWordEntity.class, parentColumns = "id", childColumns = "english_id"))
public class BanglaWordEntity {
    @NonNull
    public int english_id;

    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @NonNull
    String text;

    public BanglaWordEntity(@NonNull String text, int english_id) {
        this.text = text;
        this.english_id = english_id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }
}