package com.bletpms.app.ui.pair;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bletpms.app.database.Vehicle;
import com.bletpms.app.database.VehicleRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PairViewModel extends AndroidViewModel {

    private VehicleRepository mRepository;
    private LiveData<Vehicle> mMainVehicle;
    private LiveData<String[]> mDevices;

    public PairViewModel(Application application) {
        super(application);
        mRepository = new VehicleRepository(application);
        mMainVehicle = mRepository.getMainVehicle();
        mDevices = mRepository.getDevices();
    }

    LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

    LiveData<String[]> getDevices(){return mDevices;}

    public void update(Vehicle vehicle){
        mRepository.update(vehicle);
    }

}