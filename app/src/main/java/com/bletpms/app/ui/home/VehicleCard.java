package com.bletpms.app.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.bletpms.app.R;
import com.bletpms.app.bluetooth.DeviceBeacon;
import com.bletpms.app.ui.settings.PressureLimitsActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class VehicleCard {

    private final MaterialCardView baseCard;
    private final Context context;

    private boolean isBinded = false;
    private String deviceID = null;

    private boolean isDataLoaded = false;

    private final TextView pressureTextView;
    private final TextView temperatureTextView;
    private final TextView pressureUnitTextView;
    private final TextView temperatureUnitTextView;
    private final ImageView signalImageView;
    private final ImageView pressureImageView;
    private final ImageView temperatureImageView;
    private final ImageView batteryImageView;

    private String temperatureUnitPref;
    private String pressureUnitPref;
    private float temperatureUpperLimitPref;
    private float pressureLowerLimitPref;
    private float pressureUpperLimitPref;

    private DeviceBeacon lastBeacon;

    SharedPreferences preferences;

    private CircularProgressIndicator progressIndicator;

    private Group dataViewsGroups;


    public VehicleCard(Context context, MaterialCardView baseCard) {
        this.baseCard = baseCard;
        this.context = context;

        pressureTextView = baseCard.findViewById(R.id.pressureTextView);
        temperatureTextView = baseCard.findViewById(R.id.tempTextView);
        pressureUnitTextView = baseCard.findViewById(R.id.pressureUnitTextView);
        temperatureUnitTextView = baseCard.findViewById(R.id.tempUnitTextView);

        signalImageView = baseCard.findViewById(R.id.signalIcon);
        pressureImageView = baseCard.findViewById(R.id.pressureIcon);
        temperatureImageView = baseCard.findViewById(R.id.temperatureIcon);
        batteryImageView = baseCard.findViewById(R.id.batteryIcon);

        progressIndicator = baseCard.findViewById(R.id.progressIndicator);
        dataViewsGroups = baseCard.findViewById(R.id.dataViewsGroup);
        //progressIndicator.show();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        getPreferences();

        updateUnits();

    }

    public void getPreferences(){
        this.temperatureUnitPref = preferences.getString("temperature_unit", context.getResources().getString(R.string.temperature_unit_def_value));
        this.pressureUnitPref = preferences.getString("pressure_unit", context.getResources().getString(R.string.pressure_unit_def_value));;
        this.temperatureUpperLimitPref = preferences.getInt("temperature_upper_limit", 65);
        this.pressureLowerLimitPref = preferences.getFloat("pressure_lower_value", (float) PressureLimitsActivity.defaultLowerBar);
        this.pressureUpperLimitPref = preferences.getFloat("pressure_upper_value", (float) PressureLimitsActivity.defaultUpperBar);
    }

    public void updateUnits(){
        pressureUnitTextView.setText(pressureUnitPref);

        final  String[] temperatureUnitValues = context.getResources().getStringArray(R.array.temperature_values);
        final  String[] temperatureUnitEntries = context.getResources().getStringArray(R.array.temperature_entries);
        String tempUnit = "";
        if (temperatureUnitPref.matches(temperatureUnitValues[0])) {
            tempUnit = temperatureUnitEntries[0];
        } else if (temperatureUnitPref.matches(temperatureUnitValues[1])){
            tempUnit = temperatureUnitEntries[1];
        }
        temperatureUnitTextView.setText(tempUnit);

    }

    public void isLoadingData(boolean isLoading){
        if (isLoading){
            dataViewsGroups.setVisibility(View.GONE);
            progressIndicator.show();
        } else {
            dataViewsGroups.setVisibility(View.VISIBLE);
            progressIndicator.hide();
            isDataLoaded = true;
        }
    }

    public void updateData(DeviceBeacon deviceBeacon) {

        if (!isDataLoaded) isLoadingData(false);
        lastBeacon = deviceBeacon;

        if (deviceBeacon.getAlarmFlag() == 1) signalImageView.setColorFilter(getRedColor());
        else signalImageView.setColorFilter(ContextCompat.getColor(context,R.color.green));

        if (deviceBeacon.getBatteryPercentage() <= 25) batteryImageView.setColorFilter(getRedColor());
        else batteryImageView.clearColorFilter();

        final String[] pressureUnitValues = context.getResources().getStringArray(R.array.pressure_values);
        float pressureValue = 0;
        if (pressureUnitPref.matches(pressureUnitValues[0]))
            pressureValue = (float) deviceBeacon.getPressureBAR();
        else if (pressureUnitPref.matches(pressureUnitValues[1]))
            pressureValue = (float) deviceBeacon.getPressureKPA();
        else if (pressureUnitPref.matches(pressureUnitValues[2]))
            pressureValue = (float) deviceBeacon.getPressurePSI();

        if (pressureValue < pressureLowerLimitPref || pressureValue > pressureUpperLimitPref){
            pressureImageView.setColorFilter(getRedColor());
            pressureTextView.setTextColor(getRedColor());
        } else {
            pressureImageView.clearColorFilter();
            pressureTextView.setTextColor(ContextCompat.getColor(context,R.color.white));
        }

        pressureTextView.setText(String.format(Locale.getDefault(),"%.1f",pressureValue));
        //pressureUnitTextView.setText(pressureUnitPref);

        final  String[] temperatureUnitValues = context.getResources().getStringArray(R.array.temperature_values);
        //final  String[] temperatureUnitEntries = context.getResources().getStringArray(R.array.temperature_entries);
        float tempValue = 0;
        //String tempUnit = "";
        if (temperatureUnitPref.matches(temperatureUnitValues[0])) {
            tempValue = (float) deviceBeacon.getTemperatureCelsius();
            //tempUnit = temperatureUnitEntries[0];
        } else if (temperatureUnitPref.matches(temperatureUnitValues[1])){
            tempValue = (float) deviceBeacon.getTemperatureFahrenheit();
            //tempUnit = temperatureUnitEntries[1];
        }

        if (tempValue > temperatureUpperLimitPref) {
            temperatureImageView.setColorFilter(getRedColor());
            temperatureTextView.setTextColor(getRedColor());
        } else {
            temperatureImageView.clearColorFilter();
            temperatureTextView.setTextColor(ContextCompat.getColor(context,R.color.white));
        }

        temperatureTextView.setText(String.format(Locale.getDefault(),"%.0f", tempValue));
        //temperatureUnitTextView.setText(tempUnit);

        /*pressureTextView.setText(String.format(Locale.getDefault(),"%.1f",deviceBeacon.getPressureBAR()));
        temperatureTextView.setText(String.format(Locale.getDefault(),"%.0f",deviceBeacon.getTemperatureCelsius()));
        pressureUnitTextView.setText(pressureUnitPref);*/
    }

    private int getRedColor(){
        return ContextCompat.getColor(context,R.color.red);
    }

    public void setDeviceID(String deviceID) {
        if (deviceID != null){
            isBinded = true;
            isLoadingData(true);
            this.deviceID = deviceID;
        }
    }

    public DeviceBeacon getLastBeacon() {
        return lastBeacon;
    }

    public MaterialCardView getBaseCard() {
        return baseCard;
    }

    public boolean isBinded() {
        return isBinded;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public TextView getPressureTextView() {
        return pressureTextView;
    }

    public TextView getTemperatureTextView() {
        return temperatureTextView;
    }

    public TextView getPressureUnitTextView() {
        return pressureUnitTextView;
    }

    public ImageView getSignalImageView() {
        return signalImageView;
    }

    public ImageView getPressureImageView() {
        return pressureImageView;
    }

    public ImageView getTemperatureImageView() {
        return temperatureImageView;
    }

    public ImageView getBatteryImageView() {
        return batteryImageView;
    }

    @NotNull
    @Override
    public String toString() {
        return "VehicleCard{" +
                "baseCardID=" + baseCard.getId() +
                ", isBinded=" + isBinded +
                ", deviceID='" + deviceID + '\'' +
                '}';
    }
}
