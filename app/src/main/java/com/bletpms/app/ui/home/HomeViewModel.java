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


public class HomeViewModel extends AndroidViewModel{

    private VehicleRepository mRepository;
    private LiveData<Vehicle> mMainVehicle;
    private LiveData<String[]> mDevices;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new VehicleRepository(application);
        mMainVehicle = mRepository.getMainVehicle();
        mDevices = mRepository.getDevices();
    }

    LiveData<Vehicle> getMainVehicle(){return mMainVehicle;}

    LiveData<String[]> getDevices(){return mDevices;}
}
/*
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> buttonText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
        buttonText = new MutableLiveData<>();
    }

    public LiveData<String> getHomeText() {
        return mText;
    }
    public void setHomeText(String text) {
        mText.setValue(mText.getValue().concat( "\n" + text));
    }

    public LiveData<String> getHomeButtonText() {
        return buttonText;
    }
    public void setHomeButtonText(String text) {
        buttonText.setValue(text);
    }
}*/
