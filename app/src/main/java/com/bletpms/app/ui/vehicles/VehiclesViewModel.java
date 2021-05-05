package com.bletpms.app.ui.vehicles;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    LiveData<List<Vehicle>> getAllVehicles() { return mAllVehicles; }
    LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

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