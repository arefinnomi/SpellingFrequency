package com.example.spellingfrequency.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "CurrentWeight", indices = {@Index(value = {"current"}, unique = true)})
public class CurrentWeightEntity {

    @PrimaryKey(autoGenerate = true)
    int id;
    String current;
    int weight;

    public CurrentWeightEntity(String current, int weight) {
        this.current = current;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
