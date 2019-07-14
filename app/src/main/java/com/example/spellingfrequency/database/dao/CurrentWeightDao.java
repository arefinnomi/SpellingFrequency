package com.example.spellingfrequency.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
