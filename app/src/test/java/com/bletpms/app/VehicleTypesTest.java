package com.bletpms.app;

import com.bletpms.app.utils.VehicleTypes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;

public class VehicleTypesTest {

    @Test
    public void getAllWheels_returnsOK() {
        assertThat(VehicleTypes.getAllWheels()).isInstanceOf(Map.class);
        assertThat(VehicleTypes.getAllWheels()).isNotEmpty();
        assertThat(VehicleTypes.getAllWheels()).containsEntry("4", 4);
    }

    @Test
    public void getAllLayouts_returnsOK() {
        assertThat(VehicleTypes.getAllLayouts()).isInstanceOf(Map.class);
        assertThat(VehicleTypes.getAllLayouts()).isNotEmpty();
        assertThat(VehicleTypes.getAllLayouts()).containsEntry("4","vehicle_layout_four_wheels");
    }

    @Test
    public void getImagesName_returnsOK() {
        assertThat(VehicleTypes.getImagesName()).isInstanceOf(ArrayList.class);
        assertThat(VehicleTypes.getImagesName()).isNotEmpty();
        assertThat(VehicleTypes.getImagesName()).contains("4.png");
    }

    @Test
    public void getAllVehicleTypeInfo_existingName_returnsOK(){
        String name = "4";
        int wheels = Objects.requireNonNull(VehicleTypes.getAllWheels().get(name));
        String layout = VehicleTypes.getAllLayouts().get(name);
        String result = wheels + ", " + layout;
        assertThat(result).matches("4, vehicle_layout_four_wheels");
    }

    @Test
    public void getAllVehicleTypeInfo_noExistingName_returnsNull(){
        String name = "44"; 
        assertThat(VehicleTypes.getAllWheels().get(name)).isNull();
        assertThat(VehicleTypes.getAllLayouts().get(name)).isNull();
    }
}