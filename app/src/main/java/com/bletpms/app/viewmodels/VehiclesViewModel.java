package com.bletpms.app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bletpms.app.database.Vehicle;
import com.bletpms.app.database.VehicleRepository;

import java.util.List;

public class VehiclesViewModel extends AndroidViewModel {

    private final VehicleRepository mRepository;
    private final LiveData<List<Vehicle>> mAllVehicles;
    private final LiveData<Vehicle> mMainVehicle;

    public VehiclesViewModel(Application application) {
        super(application);
        mRepository = new VehicleRepository(application);
        mAllVehicles = mRepository.getAllVehicles();
        mMainVehicle = mRepository.getMainVehicle();
    }

    public LiveData<List<Vehicle>> getAllVehicles() { return mAllVehicles; }
    public LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

    public void insert(Vehicle vehicle) {
        mRepository.insert(vehicle);
    }

    public void delete(Vehicle vehicle){
        mRepository.delete(vehicle);
    }
    public void update(Vehicle vehicle){
        mRepository.update(vehicle);
    }
    public void setMain(Vehicle vehicle){
        mRepository.setMain(vehicle);
    }
}