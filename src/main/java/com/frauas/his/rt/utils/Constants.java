package com.frauas.his.rt.utils;

public class Constants {
    public static double STATIC_FRICTION_DRY_ROAD = 0.8d;
    public static double STATIC_FRICTION_WET_ROAD = 0.55d;
    public static double STATIC_FRICTION_ICY_ROAD = 0.3d;
    public static double KINETIC_FRICTION_DRY_ROAD = 0.65d;
    public static double KINETIC_FRICTION_WET_ROAD = 0.4d;
    public static double KINETIC_FRICTION_ICY_ROAD = 0.2d;

    public static double ACCELERATION_DUE_TO_GRAVITY = 9.8;
    public static double THRESHOLD_SLIP = 0.2d;


    public enum ROAD_CONDITIONS {
        DRY(0.8),
        WET(0.55),
        ICY(0.3);

        private double coeff;
        ROAD_CONDITIONS(double coeff){
            this.coeff = coeff;
        }

        public double getCoeff(){
            return this.coeff;
        }
    }

    public enum ROAD_TYPES {
        DIRT,
        GRAVELLED,
        PAVED
    }

}
