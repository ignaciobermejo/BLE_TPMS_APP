package com.bletpms.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bletpms.app.bluetooth.BluetoothService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothService = new BluetoothService(this, this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String[] darkModeValues = getResources().getStringArray(R.array.theme_values);
        String pref = preferences.getString("theme", getString(R.string.theme_def_value));
        if (pref.equals(darkModeValues[0]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (pref.equals(darkModeValues[1]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (pref.equals(darkModeValues[2]))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        final String[] pressureInitialValues = getResources().getStringArray(R.array.initial_pressure_values_bar);
        preferences.edit().putFloat("pressure_lower_value", preferences.getFloat("pressure_lower_value", Float.parseFloat(pressureInitialValues[0]))).apply();
        preferences.edit().putFloat("pressure_upper_value", preferences.getFloat("pressure_upper_value", Float.parseFloat(pressureInitialValues[1]))).apply();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_settings) {
            if (bluetoothService.isScanning())
                bluetoothService.stopBleScan();
            Intent intent = new Intent();
            intent.setClassName(this, "com.bletpms.app.ui.settings.SettingsActivity");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.nav_view);
        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_home)
        {
            super.onBackPressed();
        }
        else
        {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothService.ENABLE_BLUETOOTH_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) bluetoothService.startBleScan();
            else bluetoothService.promptEnableBluetooth();
            /*if (resultCode != Activity.RESULT_OK) bluetoothService.promptEnableBluetooth();
            if (resultCode == Activity.RESULT_OK) bluetoothService.startBleScan();*/
        }
    }

    public void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[] {permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BluetoothService.LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                bluetoothService.requestLocationPermission();
            } else {
                bluetoothService.startBleScan();
            }
        }
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }
}