package com.bletpms.app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bletpms.app.database.Vehicle;
import com.bletpms.app.database.VehicleRepository;


public class HomeViewModel extends AndroidViewModel{

    private final LiveData<Vehicle> mMainVehicle;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        VehicleRepository mRepository = new VehicleRepository(application);
        mMainVehicle = mRepository.getMainVehicle();
    }

    LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

}
