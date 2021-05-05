package com.bletpms.app.bluetooth;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.DialogFragment;

import com.bletpms.app.MainActivity;
import com.bletpms.app.R;
import com.bletpms.app.ui.home.HomeViewModel;
import com.bletpms.app.utils.DataParser;

import java.util.ArrayList;
import java.util.List;

public class BluetoothService {

    private static final String TAG = "BluetoothService";

    private final MainActivity mainActivity;
    private final Context context;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private ScanSettings scanSettings;
    private ScanCallback scanCallback;

    private HomeViewModel homeViewModel;

    private DataParser parser;

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private boolean isScanning = false;
    private boolean locationDialogAlreadyDisplayed = false;

    private ObservableMap<String,DeviceBeacon> mBeacons;

    public BluetoothService(MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;

        /*bluetoothAdapter = getBluetoothAdapter();
        bleScanner = getBleScanner();*/

        mBeacons = new ObservableArrayMap<>();
    }

    public void startBleScan() {
        if (!isLocationPermissionGranted()) {
            requestLocationPermission();
        } else {
            ScanFilter beaconFilter = new ScanFilter.Builder()
                    .setManufacturerData(0x0100, new byte[]{})
                    .build();
            ArrayList<ScanFilter> filters = new ArrayList<>();
            filters.add(beaconFilter);
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();

            getBleScanner().startScan(filters, settings, mScanCallback);
            setScanningState(true);
        }
    }

    public void stopBleScan(){
        getBleScanner().stopScan(mScanCallback);
        setScanningState(false);
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult");
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "onBatchScanResults: "+results.size()+" results");
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "LE Scan Failed: "+errorCode);
        }

        private void processResult(ScanResult result){
            Log.i(TAG, "New LE Device: " + result.getDevice().getName() + " @ " + result.getRssi());

            DeviceBeacon beacon = new DeviceBeacon(result.getScanRecord(), result.getDevice().getAddress(), result.getRssi());
            mBeacons.put(beacon.getName(),beacon);
        }
    };

    public ObservableMap<String, DeviceBeacon> getBeacons() {
        return mBeacons;
    }

    private BluetoothLeScanner getBleScanner(){
        return getBluetoothAdapter().getBluetoothLeScanner();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        return bluetoothManager.getAdapter();
    }

    public void promptEnableBluetooth() {
        if (!getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }

    public void requestLocationPermission() {
        if (isLocationPermissionGranted()||locationDialogAlreadyDisplayed) {
            return;
        }
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*DialogFragment newFragment = new LocationDialog(LOCATION_PERMISSION_REQUEST_CODE);
                newFragment.show(mainActivity.getSupportFragmentManager(),"location");*/

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage(R.string.location_message)
                        .setTitle(R.string.location_title)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mainActivity.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                        LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        });
                builder.show();
                locationDialogAlreadyDisplayed = true;
            }
        });
    }

    protected boolean isLocationPermissionGranted(){
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean hasPermission(String permissionType) {
        return ContextCompat.checkSelfPermission(context, permissionType) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void setScanningState(boolean scanning) {
        isScanning = scanning;
    }
}
