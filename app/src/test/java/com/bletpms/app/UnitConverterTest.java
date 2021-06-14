package com.bletpms.app;

import com.bletpms.app.utils.UnitConverter;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UnitConverterTest {

    @Test
    public void fahrenheitToCelsius(){
        int input = 140;
        int result = UnitConverter.fahrenheitToCelsius(input);
        assertThat(result).isEqualTo(60);
    }

    @Test
    public void celsiusToFahrenheit(){
        int input = 60;
        int result = UnitConverter.celsiusToFahrenheit(input);
        assertThat(result).isEqualTo(140);
    }

    @Test
    public void pressureConversions_floatValueInput(){
        float inputInBar = 2.5F;
        float outputInKPA = UnitConverter.doPressureConversion("BAR", "KPA", inputInBar);
        float outputInPSI = UnitConverter.doPressureConversion("BAR", "PSI", inputInBar);

        assertThat(outputInKPA).isEqualTo(250);
        assertThat(outputInPSI).isEqualTo(36.3F);

        assertThat(UnitConverter.doPressureConversion("PSI", "BAR", 36.3F)).isEqualTo(inputInBar);
    }

    @Test
    public void pressureConversions_integerValueInput(){
        int inputInBar = 2;
        float outputInKPA = UnitConverter.doPressureConversion("BAR", "KPA", inputInBar);
        float outputInPSI = UnitConverter.doPressureConversion("BAR", "PSI", inputInBar);

        assertThat(outputInKPA).isEqualTo(200);
        assertThat(outputInPSI).isEqualTo(29.0F);

        assertThat(UnitConverter.doPressureConversion("PSI", "BAR", 29.0F)).isEqualTo(inputInBar);
    }

    @Test
    public void pressureConversions_wrongUnitInput_returnZero(){
        String wrongUnit = "var";
        int inputInBar = 2;
        float outputInKPA = UnitConverter.doPressureConversion(wrongUnit, "KPA", inputInBar);

        assertThat(outputInKPA).isEqualTo(0);
    }
}