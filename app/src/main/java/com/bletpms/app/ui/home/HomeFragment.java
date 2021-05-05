package com.bletpms.app.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bletpms.app.MainActivity;
import com.bletpms.app.R;
import com.bletpms.app.bluetooth.BluetoothService;
import com.bletpms.app.bluetooth.DeviceBeacon;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.ui.settings.PressureLimitsActivity;
import com.bletpms.app.utils.BitmapFromAssetsProvider;
import com.bletpms.app.utils.VehicleTypes;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private BluetoothService bluetoothService;
    private View root;
    private ArrayList<MaterialCardView> cards;
    private boolean layoutLoaded = false;
    private Vehicle mainVehicle;

    private ArrayList<VehicleCard> vehicleCards;

    private SharedPreferences preferences;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                    bluetoothService.promptEnableBluetooth();
                }
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        bluetoothService = ((MainActivity)requireActivity()).getBluetoothService();

        homeViewModel.getMainVehicle().observe(getViewLifecycleOwner(), new Observer<Vehicle>() {
            @Override
            public void onChanged(Vehicle vehicle) {
                if (!layoutLoaded){
                    loadImageAndLayout(vehicle, inflater);
                    attachDevicesToCards(vehicle.getDevices(),vehicleCards);
                }
                mainVehicle = vehicle;
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Map<String, ?> allEntries = PreferenceManager.getDefaultSharedPreferences(getContext()).getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bluetoothService.isScanning()) bluetoothService.stopBleScan();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (bluetoothService.getBluetoothAdapter() == null || !bluetoothService.getBluetoothAdapter().isEnabled()) {
            bluetoothService.promptEnableBluetooth();
        } else {
            if (!bluetoothService.isScanning()) bluetoothService.startBleScan();
        }
        //if (!bluetoothService.isScanning()) bluetoothService.startBleScan();

        if (layoutLoaded) updateCardsData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        bluetoothService.getBeacons().addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<String, DeviceBeacon>, String, DeviceBeacon>() {
            @Override
            public void onMapChanged(ObservableMap<String, DeviceBeacon> sender, String key) {
                //Debug info
                for (Map.Entry<String, DeviceBeacon> entry : sender.entrySet()) {
                    String s = entry.getKey();
                    DeviceBeacon deviceBeacon = entry.getValue();
                    Log.i(TAG, "Device name: " + s + " [" + deviceBeacon.toString() + "]");
                }

                for (VehicleCard card: vehicleCards) {
                    if (card.isBinded()){
                        if (card.getDeviceID().matches(sender.get(key).getName()))
                            card.updateData(sender.get(card.getDeviceID()));
                    }
                }
            }
        });
    }


    private void updateCardsData(){
        Log.i(TAG, "Cards data updated");
        for (VehicleCard card: vehicleCards) {
            card.getPreferences();
            card.updateUnits();
            if (card.isBinded()){
                if (card.getLastBeacon() != null)
                    card.updateData(card.getLastBeacon());
            }
        }
    }
    private void attachDevicesToCards(String[] devices, ArrayList<VehicleCard> cards) {
        for (int i = 0; i < devices.length; i++ ){
            cards.get(i).setDeviceID(devices[i]);
            Log.i(TAG, cards.get(i).toString());
        }
    }

    private  void loadImageAndLayout(Vehicle vehicle, LayoutInflater inflater){
        String layoutName = VehicleTypes.getAllLayouts().get(vehicle.getType());
        int layoutId = getResources().getIdentifier(layoutName, "layout", "com.bletpms.app");

        LinearLayout baseLayout = root.findViewById(R.id.homeView);
        View newRoot = inflater.inflate(layoutId, (ViewGroup) root, false);
        baseLayout.addView(newRoot);

        final ImageView vehicleImage = newRoot.findViewById(R.id.vehicleImageView);
        vehicleImage.setImageBitmap(new BitmapFromAssetsProvider(getContext()).getBitmap(vehicle.getType()));

        cards = new ArrayList<>();
        for (int i = 0; i < vehicle.getDevices().length; i++ ){
            String cardIDString = "card"+(i+1);
            int cardID = getResources().getIdentifier(cardIDString, "id","com.bletpms.app");
            cards.add(root.findViewById(cardID));
        }

        vehicleCards = new ArrayList<>();
        for (MaterialCardView card: cards) {
            View innerCardLayout = inflater.inflate(R.layout.card_home, (ViewGroup) root, false);
            card.addView(innerCardLayout);

            vehicleCards.add(new VehicleCard(getContext(), card));
        }

        layoutLoaded = true;
    }

    private void createCardsContent(Vehicle vehicle, LayoutInflater inflater) {
        for (MaterialCardView card: cards) {
            View newRoot = inflater.inflate(R.layout.card_home, (ViewGroup) root, false);
            card.addView(newRoot);
        }
    }

    private boolean isCardsSizeLarge(String vehicleType){
        return vehicleType.matches("2|1-2|2-1");
    }

/*    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && sharedPreferences != null) {
            if (!key.matches("theme")) {
                Log.i(TAG, "Preferences changed!");
                String temperatureUnitPref = preferences.getString("temperature_unit", getString(R.string.temperature_unit_def_value));
                String pressureUnitPref = preferences.getString("pressure_unit", getString(R.string.pressure_unit_def_value));;
                float temperatureUpperLimitPref = preferences.getInt("temperature_upper_limit", 65);
                float pressureLowerLimitPref = preferences.getFloat("pressure_lower_value", (float) PressureLimitsActivity.defaultLowerBar);
                float pressureUpperLimitPref = preferences.getFloat("pressure_upper_value", (float) PressureLimitsActivity.defaultUpperBar);
                for (VehicleCard card: vehicleCards) {
                    card.updatePreferences(temperatureUnitPref, pressureUnitPref, temperatureUpperLimitPref, pressureLowerLimitPref, pressureUpperLimitPref);
                    if (key.matches("temperature_unit|pressure_unit"))
                        card.updateUnits();
                    if (card.isBinded()){
                        if (card.getLastBeacon() != null)
                            card.updateData(card.getLastBeacon());
                    }
                }
            }
        }
    }*/
}