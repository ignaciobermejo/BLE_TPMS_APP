package com.bletpms.app.ui.pair;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bletpms.app.database.Vehicle;
import com.bletpms.app.database.VehicleRepository;

public class PairViewModel extends AndroidViewModel {

    private final VehicleRepository mRepository;
    private final LiveData<Vehicle> mMainVehicle;

    public PairViewModel(Application application) {
        super(application);
        mRepository = new VehicleRepository(application);
        mMainVehicle = mRepository.getMainVehicle();
    }

    LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

    public void update(Vehicle vehicle){
        mRepository.update(vehicle);
    }

}