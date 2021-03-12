package com.bletpms.app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VehicleTypes{

    enum VehicleType {

        FOUR_WHEELS("4", 4, "vehicle_layout_four_wheels"),
        TWO_WHEELS_MOTO("2", 2, "vehicle_layout_two_wheels"),
        TWO_WHEELS_BIKE("2b", 2, "vehicle_layout_two_wheels"),
        FOUR_WHEELS_TWO_WHEELS_TRAILER("4+2rem", 6, "vehicle_layout_trailer_six_wheels"),
        FOUR_WHEELS_FOUR_WHEELS_TRAILER("4+4rem", 8, "vehicle_layout_trailer_eight_wheels"),
        TWO_FOUR_WHEELS("2-4", 6, "vehicle_layout_six_wheels"),
        SIX_WHEELS("6", 6, "vehicle_layout_six_wheels"),
        ONE_TWO_WHEELS("1-2", 3, "vehicle_layout_one_two_wheels"),
        TWO_ONE_WHEELS("2-1", 3, "vehicle_layout_two_one_wheels"),
        TWO_WHEELS_TRAILER("2rem", 2, "vehicle_layout_two_wheels"),
        FOUR_WHEELS_TRAILER("4rem", 4, "vehicle_layout_four_wheels");

        private final String name;
        private final int wheels;
        private final String layout;

        VehicleType(String name, int wheels, String layout) {
            this.name = name;
            this.wheels = wheels;
            this.layout = layout;
        }

        public String getName() {
            return name;
        }

        public int getWheels() {
            return wheels;
        }

        public String getLayout() {
            return layout;
        }
    }

    public static Map<String, Integer> getAllWheels(){
        Map<String, Integer> map = new HashMap<>();
        for (VehicleType vt: VehicleType.values()) {
            map.put(vt.getName(),vt.getWheels());
        }
        return map;
    }

    public static Map<String, String> getAllLayouts(){
        Map<String, String> map = new HashMap<>();
        for (VehicleType vt: VehicleType.values()) {
            map.put(vt.getName(),vt.getLayout());
        }
        return map;
    }

    public static ArrayList<String> getImagesName(){
        ArrayList<String>list = new ArrayList<>();
        for (VehicleType vt: VehicleType.values()) {
            list.add(vt.getName()+".png");
        }
        return list;
    }
}
