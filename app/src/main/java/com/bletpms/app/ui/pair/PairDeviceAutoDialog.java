package com.bletpms.app.ui.pair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.DialogFragment;

import com.bletpms.app.R;
import com.bletpms.app.bluetooth.BluetoothService;
import com.bletpms.app.bluetooth.DeviceBeacon;
import com.bletpms.app.database.Vehicle;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PairDeviceAutoDialog extends DialogFragment {

    private static final String TAG = "AutoPairDialogFragment";

    private final PairViewModel mPairViewModel;
    private final Vehicle mainVehicle;
    private final int selectedWheel;
    private final PairDeviceDialog pairDeviceDialog;
    private View root;
    private final MaterialCardView card;
    private final BluetoothService bluetoothService;

    private Handler myHandler;

    private static final int SEARCHING_TIME_MS = 30000;

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
        root = inflater.inflate(R.layout.dialog_pair_device_auto, null);

        TextView textView = root.findViewById(R.id.autoTextView);
        Button searchButton = root.findViewById(R.id.pairDeviceSearchButton);
        Button cancelButton = root.findViewById(R.id.pairDeviceCancelButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pairDeviceDialog.getDialog() != null) pairDeviceDialog.getDialog().cancel();

                card.findViewById(R.id.pairTextView).setVisibility(View.GONE);
                card.findViewById(R.id.progressIndicator).setVisibility(View.VISIBLE);

                PairDeviceAutoDialog.this.getDialog().hide();

                Log.i(TAG, "Searching NEW devices...");
                SearchingDeviceDialog dialog = new SearchingDeviceDialog(getActivity(), SEARCHING_TIME_MS);
                Dialog d = dialog.showDialog();
                d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.i(TAG, "Ending search... NEW devices NOT found");
                        if (bluetoothService.isScanning()) bluetoothService.stopBleScan();
                        myHandler.removeCallbacksAndMessages(null);
                        card.findViewById(R.id.pairTextView).setVisibility(View.VISIBLE);
                        card.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                        PairDeviceAutoDialog.this.getDialog().dismiss();
                    }
                });

                myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Ending search... NEW devices NOT found");
                        if (bluetoothService.isScanning()) bluetoothService.stopBleScan();
                        dialog.cancelDialog();
                        card.findViewById(R.id.pairTextView).setVisibility(View.VISIBLE);
                        card.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                        textView.setText(R.string.pair_auto_search_not_found);
                        PairDeviceAutoDialog.this.getDialog().show();
                    }
                }, SEARCHING_TIME_MS);

                findNewDevices(dialog, myHandler);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PairDeviceAutoDialog.this.getDialog().cancel();
            }
        });

        builder.setView(root);

        return builder.create();
    }

    private void findNewDevices(SearchingDeviceDialog dialog, Handler myHandler){
        if (bluetoothService.getBluetoothAdapter() == null || !bluetoothService.getBluetoothAdapter().isEnabled()) {
            bluetoothService.promptEnableBluetooth();
        } else {
            if (!bluetoothService.isScanning()) bluetoothService.startBleScan();
        }

        List<String> bindedDevices = new ArrayList<>();
        String[] bindedDevicesArray = mainVehicle.getDevices();
        for (String s : bindedDevicesArray) {
            if (s != null) bindedDevices.add(s);
        }
        Log.i(TAG, "Devices binded: " + bindedDevices.toString());

        bluetoothService.getBeacons().addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<String, DeviceBeacon>, String, DeviceBeacon>() {
            @Override
            public void onMapChanged(ObservableMap<String, DeviceBeacon> sender, String key) {

                boolean found = false;
                if (!found && !bindedDevices.contains(key)){
                    found = true;
                    Log.i(TAG, "NEW Device found: " + key);
                    if (bluetoothService.isScanning()) bluetoothService.stopBleScan();
                    mainVehicle.setDevice(selectedWheel,key);
                    mPairViewModel.update(mainVehicle);
                    myHandler.removeCallbacksAndMessages(null);
                    dialog.cancelDialog();
                    card.findViewById(R.id.pairTextView).setVisibility(View.VISIBLE);
                    card.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
                    //PairDeviceAutoDialog.this.getDialog().cancel();
                }
            }
        });
    }
}
