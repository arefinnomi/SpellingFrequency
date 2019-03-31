package com.example.spellingfrequency.database.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Comparator;

@Entity(tableName = "EnglishWord", indices = {@Index(value = {"text"}, unique = true)})
public class EnglishWordEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @NonNull
    String text;

    @NonNull
    long frequency;

    @NonNull
    int repeat;

    @NonNull
    int weight;

    @NonNull
    boolean favourite;


    public EnglishWordEntity(String text, int repeat, long frequency) {
        this.text = text;
        this.repeat = repeat;
        this.frequency = frequency;
        this.favourite = false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public static class Compare implements Comparator<EnglishWordEntity> {

        @Override
        public int compare(EnglishWordEntity o1, EnglishWordEntity o2) {
            if (o1.getFrequency() == o2.getFrequency()) return o1.getText().compareTo(o2.getText());
            if(o1.getFrequency() > o2.getFrequency()) return -1;
            else return 1;
        }
    }

}
