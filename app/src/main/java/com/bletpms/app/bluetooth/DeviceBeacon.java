package com.bletpms.app.bluetooth;

import android.bluetooth.le.ScanRecord;

import com.bletpms.app.utils.DataParser;

public class DeviceBeacon {

    private final String mName;
    private final String mAddress;
    private final int mSignal;
    private final int mSensorNumber;
    private final double mTemperatureCelsius;
    private final double mTemperatureFahrenheit;
    private final double mPressureKPA;
    private final double mPressureBAR;
    private final double mPressurePSI;
    private final int mBatteryPercentage;
    private final int mAlarmFlag;

    public DeviceBeacon(ScanRecord record, String deviceAddress, int rssi) {
        mSignal = rssi;
        mAddress = deviceAddress;
        //TPMS2_204589-->204589
        mName = record.getDeviceName().substring(6);

        String data = DataParser.retManData(record.getBytes());

        if (data != null){
            mSensorNumber = DataParser.getSensorNumber(data);
            mTemperatureCelsius = DataParser.getTemperatureCelsius(data);
            mTemperatureFahrenheit = DataParser.getTemperatureFahrenheit(data);
            mPressureKPA = DataParser.getPressureKPA(data);
            mPressureBAR = DataParser.getPressureBAR(data);
            mPressurePSI = DataParser.getPressurePSI(data);
            mBatteryPercentage = DataParser.getBatteryPercentage(data);
            mAlarmFlag = DataParser.getAlarmFlag(data);
        } else {
            mSensorNumber = mBatteryPercentage = mAlarmFlag = 0;
            mTemperatureCelsius = mTemperatureFahrenheit = mPressureBAR = mPressureKPA = mPressurePSI = 0;
        }
    }


    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getSignal() {
        return mSignal;
    }

    public int getSensorNumber() {
        return mSensorNumber;
    }

    public double getTemperatureCelsius() {
        return mTemperatureCelsius;
    }

    public double getTemperatureFahrenheit() {
        return mTemperatureFahrenheit;
    }

    public double getPressureKPA() {
        return mPressureKPA;
    }

    public double getPressureBAR() {
        return mPressureBAR;
    }

    public double getPressurePSI() {
        return mPressurePSI;
    }

    public int getBatteryPercentage() {
        return mBatteryPercentage;
    }

    public int getAlarmFlag() {
        return mAlarmFlag;
    }

    @Override
    public String toString() {
        return "DeviceBeacon{" +
                "mName='" + mName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mSignal=" + mSignal +
                ", mSensorNumber=" + mSensorNumber +
                ", mTemperatureCelsius=" + mTemperatureCelsius +
                ", mPressureKPA=" + mPressureKPA +
                ", mPressureBAR=" + mPressureBAR +
                ", mBatteryPercentage=" + mBatteryPercentage +
                ", mAlarmFlag=" + mAlarmFlag +
                '}';
    }
}
