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

    @Query("SELECT * FROM vehicle WHERE id=:id")
    Vehicle getVehicle(int id);

    @Query("SELECT * FROM vehicle WHERE name LIKE :vehicleName")
    Vehicle getVehicleByName(String vehicleName);

    @Query("DELETE from vehicle WHERE name LIKE :vehicleName")
    void deleteByName(String vehicleName);

    @Query("SELECT * FROM vehicle WHERE isMain = 1")
    List<Vehicle> getMains();

    @Query("SELECT * FROM vehicle WHERE isMain = 1")
    LiveData<Vehicle> getMain();

    @Query("SELECT * FROM vehicle WHERE isMain = 1 LIMIT 1")
    Vehicle getMainSync();

    @Query("SELECT devices FROM vehicle WHERE isMain = 1")
    LiveData<String[]> getDevices();

    @Query("SELECT devices FROM vehicle WHERE isMain = 1")
    String[] getDevicesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vehicle vehicle);

    @Delete
    void delete(Vehicle vehicle);

    @Delete
    void deleteAll(List<Vehicle> vehicles);

    @Update
    void update(Vehicle vehicle);

    @Update
    void updateAll(List<Vehicle> vehicles);
}
