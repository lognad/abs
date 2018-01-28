package com.frauas.his.rt.utils;

import com.frauas.his.rt.utils.Constants;

public class Calculation {
    public static double brakingDistance = 0.0d;
    public static double stoppingTime = 0.0d;
    public static double deceleration = 0.0d;

    public static double calculateStoppingDistance(double velocity, double coefficientOfFriction) {
        return (velocity * velocity) / (2 * coefficientOfFriction * Constants.ACCELERATION_DUE_TO_GRAVITY);
    }

    public static double calculateStoppingTime(double initialVelocity, double coefficientOfFriction) {
        return initialVelocity / (coefficientOfFriction * Constants.ACCELERATION_DUE_TO_GRAVITY);
    }

    //  SOMETHING WRONG??
    public static double calculateDeceleration(double initialVelocity, double distance) {
        return -((initialVelocity * initialVelocity) / (2 * distance));
    }

    public static double calculateVeloctiy(double velocity, double stoppingDistance, double deceleration) {
//        return Math.sqrt(Math.abs(velocity * velocity + 2 * deceleration * stoppingDistance));
        double v = velocity * velocity + 2 * deceleration * stoppingDistance;
        if (v <= 0) return 0;
        else return Math.sqrt(v);
    }

    public static double convertMpsToKmph(double velocityMps) {
        return velocityMps * 18 / 5;
    }

    public static double convertKmphToMps(double velocityMps) {
        return velocityMps * 5 / 18;
    }


    public static double calculateDistanceTravelled(double vVehicle, int time) {
        return vVehicle * time;
    }
}
