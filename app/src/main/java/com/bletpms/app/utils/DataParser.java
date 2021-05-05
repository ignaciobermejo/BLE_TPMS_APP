package com.bletpms.app.utils;

// TPMS BLE "manufacturer data" format
// "000180eaca108a78e36d0000e60a00005b00"
//  0001                                    Manufacturer (0001: TomTom)
//      80                                  Sensor Number (80:1, 81:2, 82:3, 83:4, ..)
//      80eaca108a78                        Sensor Address
//                  e36d0000                Pressure
//                          e60a0000        Temperature
//                                  5b      Battery percentage
//                                    00    Alarm Flag (00: OK, 01: No Pressure Alarm)
//
// How calculate Sensor Address:            (Sensor number):EA:CA:(Code binding reported in the leaflet) - i.e. 80:EA:CA:10:8A:78

public class DataParser {

    public static String retManData(byte[] bytes) {
        if (bytes != null){
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02X", b));
            }
            return result.toString().substring(18,54);
        } else {
            return null;
        }
    }

    private static String retByte(String data,int start) {
        // Return a single byte from string
        int sp=(start)*2;
        return data.substring(sp,sp+2);
    }

    private static double returnData(String data,int start) {
        // Return a long value with little endian conversion
        String byte0 = retByte(data, start);
        String byte1 = retByte(data, start+1);
        String byte2 = retByte(data, start+2);
        String byte3 = retByte(data, start+3);
        String s = byte3 + byte2 + byte1 + byte0;
        return Integer.parseInt(s, 16);
    }

    public static double getPressureKPA(String data){
        return returnData(data,8)/1000.0;
    }

    public static double getPressureBAR(String data){
        return returnData(data,8)/100000.0;
    }

    public static double getPressurePSI(String data){
        return getPressureKPA(data)/6.895;
    }

    public static double getTemperatureCelsius(String data){
        return returnData(data,12)/100.0;
    }

    public static double getTemperatureFahrenheit(String data){
        return ((getTemperatureCelsius(data))*(9.0/5.0))+32;
    }

    public static int getSensorNumber(String data){
        return Integer.parseInt(data.substring(5,6))+1;
    }

    public static String getSensorAddress(String data){
        return data.substring(4,16).replaceAll("(.{2})","$1"+':').substring(0,17).toUpperCase();
    }

    public static int getBatteryPercentage(String data) {
        // Return battery percentage
        return Integer.parseInt(retByte(data, 16),16);
    }

    public static int getAlarmFlag(String data) {
        // Return battery percentage
        return Integer.parseInt(retByte(data, 17),16);
    }
}
