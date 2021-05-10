package com.bletpms.app.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Vehicle implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int id;

    @NonNull
    private String name;

    private final int wheels;

    private String[] devices;

    private Boolean isMain;

    private String type;

    public Vehicle(@NonNull String name, String type, int wheels) {
        this.name = name;
        this.type = type;
        this.wheels = wheels;
        this.devices = new String[wheels];
        this.isMain = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getWheels() {
        return wheels;
    }

    public String[] getDevices() {
        return devices;
    }

    public void setDevices(String[] devices) {
        this.devices = devices;
    }

    public void setDevice(int wheelNumber, String deviceID) {
        this.devices[wheelNumber-1] =deviceID;
    }

    public String getDevice(int wheelNumber){
        return this.devices[wheelNumber-1];
    }

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Vehicle defaultVehicle() {
        return new Vehicle("Vehicle 1", "4",4);
    }
}
