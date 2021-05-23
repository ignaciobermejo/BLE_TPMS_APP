package com.bletpms.app.ui.pair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.DialogFragment;

import com.bletpms.app.R;
import com.bletpms.app.bluetooth.BluetoothService;
import com.bletpms.app.bluetooth.DeviceBeacon;
import com.bletpms.app.database.Vehicle;
import com.bletpms.app.ui.home.VehicleCard;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PairDeviceAutoDialog extends DialogFragment {

    private static final String TAG = "AutoPairDialogFragment";

    private final PairViewModel mPairViewModel;
    private final Vehicle mainVehicle;
    private final int selectedWheel;
    private final PairDeviceDialog pairDeviceDialog;
    private final MaterialCardView card;
    private final BluetoothService bluetoothService;

    private Handler myHandler;

    private SearchingDeviceDialog searchingDeviceDialog;
    private List<String> bindedDevices;

    private static final int SEARCHING_TIME_MS = 30000;

    private final ObservableMap.OnMapChangedCallback<ObservableMap<String, DeviceBeacon>, String, DeviceBeacon>
            autoPairBeaconObserver = new ObservableMap.OnMapChangedCallback<ObservableMap<String, DeviceBeacon>, String, DeviceBeacon>() {
        @Override
        public void onMapChanged(ObservableMap<String, DeviceBeacon> sender, String key) {
            if (!bindedDevices.contains(key)) {

                Log.i(TAG, "Ending search... NEW Device found: " + key);
                if (bluetoothService.isScanning()) bluetoothService.stopBleScan();
                mainVehicle.setDevice(selectedWheel, key);
                mPairViewModel.update(mainVehicle);
                myHandler.removeCallbacksAndMessages(null);
                searchingDeviceDialog.cancelDialog();
                card.findViewById(R.id.pairTextView).setVisibility(View.VISIBLE);
                card.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                bluetoothService.getBeacons().removeOnMapChangedCallback(this);
            }
        }
    };

    public PairDeviceAutoDialog(Vehicle vehicle, PairViewModel model, int selectedWheel, PairDeviceDialog pairDeviceDialog, MaterialCardView card, BluetoothService bluetoothService) {
        this.mainVehicle = vehicle;
        this.mPairViewModel = model;
        this.selectedWheel = selectedWheel;
        this.pairDeviceDialog = pairDeviceDialog;
        this.card = card;
        this.bluetoothService = bluetoothService;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_pair_device_auto, null);

        TextView textView = root.findViewById(R.id.autoTextView);
        Button searchButton = root.findViewById(R.id.pairDeviceSearchButton);
        Button cancelButton = root.findViewById(R.id.pairDeviceCancelButton);

        searchButton.setOnClickListener(v -> {
            if (pairDeviceDialog.getDialog() != null) pairDeviceDialog.getDialog().cancel();

            card.findViewById(R.id.pairTextView).setVisibility(View.GONE);
            card.findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);

            PairDeviceAutoDialog.this.requireDialog().hide();

            Log.i(TAG, "Searching NEW devices...");
            searchingDeviceDialog = new SearchingDeviceDialog(getActivity(), SEARCHING_TIME_MS);
            Dialog d = searchingDeviceDialog.showDialog();
            d.setOnCancelListener(dialog1 -> {
                Log.i(TAG, "Ending search... NEW devices NOT found");
                if (bluetoothService.isScanning()) bluetoothService.stopBleScan();
                myHandler.removeCallbacksAndMessages(null);
                bluetoothService.getBeacons().removeOnMapChangedCallback(autoPairBeaconObserver);
                card.findViewById(R.id.pairTextView).setVisibility(View.VISIBLE);
                card.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                PairDeviceAutoDialog.this.requireDialog().dismiss();
            });

            myHandler = new Handler();
            myHandler.postDelayed(() -> {
                Log.i(TAG, "Ending search... NEW devices NOT found");
                if (bluetoothService.isScanning()) bluetoothService.stopBleScan();
                bluetoothService.getBeacons().removeOnMapChangedCallback(autoPairBeaconObserver);
                searchingDeviceDialog.cancelDialog();
                card.findViewById(R.id.pairTextView).setVisibility(View.VISIBLE);
                card.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                textView.setText(R.string.pair_auto_search_not_found);
                PairDeviceAutoDialog.this.requireDialog().show();
            }, SEARCHING_TIME_MS);

            findNewDevices(searchingDeviceDialog, myHandler);
        });

        cancelButton.setOnClickListener(v -> PairDeviceAutoDialog.this.requireDialog().cancel());

        builder.setView(root);

        return builder.create();
    }

    private void findNewDevices(SearchingDeviceDialog dialog, Handler myHandler){
        if (bluetoothService.getBluetoothAdapter() == null || !bluetoothService.getBluetoothAdapter().isEnabled()) {
            bluetoothService.promptEnableBluetooth();
        } else {
            if (!bluetoothService.isScanning()) bluetoothService.startBleScan();
        }
        Log.i(TAG, "Starting search for devices...");

        bindedDevices = getBindedDevices();

        bluetoothService.getBeacons().addOnMapChangedCallback(autoPairBeaconObserver);
    }

    @NotNull
    private List<String> getBindedDevices() {
        bindedDevices = new ArrayList<>();
        String[] bindedDevicesArray = mainVehicle.getDevices();
        for (String s : bindedDevicesArray) {
            if (s != null) bindedDevices.add(s);
        }
        Log.i(TAG, "Devices binded: " + Arrays.toString(bindedDevices.toArray()));
        return bindedDevices;
    }

    private void deviceFound (){
        bluetoothService.getBeacons().removeOnMapChangedCallback(null);
    }
}
