package com.bletpms.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VehicleDAO {
    @Query("SELECT * FROM vehicle")
    LiveData<List<Vehicle>> getAll();

    @Query("SELECT * FROM vehicle WHERE isMain = 1")
    List<Vehicle> getMains();

    @Query("SELECT * FROM vehicle WHERE isMain = 1")
    LiveData<Vehicle> getMain();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vehicle vehicle);

    @Delete
    void delete(Vehicle vehicle);

    @Update
    void update(Vehicle vehicle);

    @Update
    void updateAll(List<Vehicle> vehicles);
}
