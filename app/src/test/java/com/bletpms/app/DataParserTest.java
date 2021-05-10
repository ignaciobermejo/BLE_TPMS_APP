package com.bletpms.app;

import com.bletpms.app.utils.DataParser;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DataParserTest {
    final byte[]input = {0x02,0x01,0x05,0x03,0x03, (byte) 0xb0, (byte) 0xfb,0x13, (byte) 0xff,0x00,0x01,
            (byte) 0x80, (byte) 0xea, (byte) 0xca,0x10,0x53, (byte) 0x9c,0x3c,
            (byte) 0xe4,0x01,0x00, (byte) 0xe3,0x08,0x00,0x00,0x4b,0x00};
    @Test
    public void retManData_correctInput_returnOK() {
        String result = DataParser.retManData(input);
        assertThat(result).hasLength(36);
    }

    @Test
    public void retManData_nullInput_returnNull() {
        assertThat(DataParser.retManData(null)).isNull();
    }

    @Test
    public void getPressureKPA() {
        String data = DataParser.retManData(input);
        double result = DataParser.getPressureKPA(data);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isNotNaN();
    }

    @Test
    public void getPressureBAR() {
        String data = DataParser.retManData(input);
        double result = DataParser.getPressureBAR(data);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isNotNaN();
    }

    @Test
    public void getPressurePSI() {
        String data = DataParser.retManData(input);
        double result = DataParser.getPressurePSI(data);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isNotNaN();
    }

    @Test
    public void getTemperatureCelsius() {
        String data = DataParser.retManData(input);
        double result = DataParser.getTemperatureCelsius(data);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isNotNaN();
    }

    @Test
    public void getTemperatureFahrenheit() {
        String data = DataParser.retManData(input);
        double result = DataParser.getTemperatureFahrenheit(data);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isNotNaN();
    }

    @Test
    public void getSensorNumber() {
        String data = DataParser.retManData(input);
        int result = DataParser.getSensorNumber(data);
        assertThat(result).isInstanceOf(Integer.class);
        assertThat(result).isAnyOf(1,2,3,4);
    }

    @Test
    public void getSensorAddress() {
        String data = DataParser.retManData(input);
        String result = DataParser.getSensorAddress(data);
        assertThat(result).hasLength(17);
    }

    @Test
    public void getBatteryPercentage() {
        String data = DataParser.retManData(input);
        int result = DataParser.getBatteryPercentage(data);
        assertThat(result).isInstanceOf(Integer.class);
        assertThat(result).isAnyOf(0,25,75,100);
    }

    @Test
    public void getAlarmFlag() {
        String data = DataParser.retManData(input);
        int result = DataParser.getAlarmFlag(data);
        assertThat(result).isInstanceOf(Integer.class);
        assertThat(result).isAnyOf(0,1);
    }
}