package com.bletpms.app.utils;

import java.util.Locale;

public class UnitConverter {

    public static int fahrenheitToCelsius(int value){
        return ( 5 *(value - 32)) / 9;
    }

    public static int celsiusToFahrenheit(int value){
        return ( 9 * value + (32 * 5)) / 5;
    }

    // Inputs: BAR, KPA or PSI
    public static float doPressureConversion(String oldUnit, String newUnit, float value) {
        float newValue = 0;
        if (oldUnit.matches("BAR")){
            if (newUnit.matches("KPA")){
                newValue = value * 100;
            } else {
                newValue = value * 14.504F;
            }
        }
        if (oldUnit.matches("KPA")){
            if (newUnit.matches("BAR")){
                newValue = value / 100;
            } else {
                newValue = value / 6.895F;
            }
        }
        if (oldUnit.matches("PSI")){
            if (newUnit.matches("BAR")){
                newValue = value / 14.504F;
            } else {
                newValue = value * 6.895F;
            }
        }
        return Float.parseFloat(String.format(Locale.getDefault(), "%.1f", newValue).replace(",", "."));
    }
}
