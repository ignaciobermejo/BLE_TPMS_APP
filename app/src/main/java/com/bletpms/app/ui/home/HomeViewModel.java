package com.bletpms.app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bletpms.app.bluetooth.BluetoothService;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.database.VehicleRepository;

import java.util.ArrayList;


public class HomeViewModel extends AndroidViewModel{

    private VehicleRepository mRepository;
    private LiveData<Vehicle> mMainVehicle;
    private LiveData<String[]> mDevices;

    private ArrayList<VehicleCard> lastCards;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new VehicleRepository(application);
        mMainVehicle = mRepository.getMainVehicle();
        mDevices = mRepository.getDevices();
        lastCards = new ArrayList<>();
    }

    LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

    LiveData<String[]> getDevices(){return mDevices;}

    public ArrayList<VehicleCard> getLastCards() {
        return lastCards;
    }

    public void setLastCards(ArrayList<VehicleCard> lastCards) {
        this.lastCards = lastCards;
    }
}
