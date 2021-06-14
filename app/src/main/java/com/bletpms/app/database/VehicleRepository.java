package com.bletpms.app.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class VehicleRepository {
    private final VehicleDAO mVehicleDAO;
    private final LiveData<List<Vehicle>> mAllVehicles;
    private final LiveData<Vehicle> mMainVehicle;

    public VehicleRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mVehicleDAO = db.vehicleDAO();
        mAllVehicles = mVehicleDAO.getAll();
        mMainVehicle = mVehicleDAO.getMain();
    }

    public LiveData<List<Vehicle>> getAllVehicles() {
        return mAllVehicles;
    }

    public LiveData<Vehicle> getMainVehicle() { return mMainVehicle; }

    public void insert(Vehicle vehicle){
        AppDatabase.databaseWriterExecutor.execute(() -> {
            List<Vehicle>mains = mVehicleDAO.getMains();
            for (Vehicle v:mains) {
                v.setMain(false);
            }
            mVehicleDAO.updateAll(mains);
            mVehicleDAO.insert(vehicle);
        });
    }

    public void delete(Vehicle vehicle){
        AppDatabase.databaseWriterExecutor.execute(() -> mVehicleDAO.delete(vehicle));
    }

    public void update(Vehicle vehicle){
        AppDatabase.databaseWriterExecutor.execute(() -> mVehicleDAO.update(vehicle));
    }

    public void setMain(Vehicle newMainVehicle){
        Vehicle old = mMainVehicle.getValue();
        if (old != null) old.setMain(false);
        update(old);
        newMainVehicle.setMain(true);
        update(newMainVehicle);
    }
}
