package com.example.spellingfrequency.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.spellingfrequency.database.entity.CurrentWeightEntity;

@Dao
public interface CurrentWeightDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(CurrentWeightEntity... currentWeights);

    @Query("SElECT * FROM CurrentWeight WHERE current = 'max_weight'")
    CurrentWeightEntity loadCurrentMaxWeight();

    @Query("SElECT * FROM CurrentWeight WHERE current = 'min_weight'")
    CurrentWeightEntity loadCurrentMinWeight();

    @Query("SElECT * FROM CurrentWeight")
    CurrentWeightEntity[] loadAllCurrentWeight();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrentWeight(CurrentWeightEntity currentWeightEntity);

    @Query("DELETE FROM CurrentWeight")
    void nukeTable();
}
