package com.frauas.his.rt.controller;

import com.frauas.his.rt.utils.Constants;

public class Calculation {
    public static double brakingDistance = 0.0d;
    public static double stoppingTime = 0.0d;
    public static double deceleration = 0.0d;

    public static double calculateStoppingDistance(double velocity, double coefficientOfFriction) {
        return (velocity * velocity) / (2 * coefficientOfFriction * Constants.ACCELERATION_DUE_TO_GRAVITY);
    }

    public static double calculateStoppingTime(double initialVelocity, double deceleration) {
        return initialVelocity / deceleration;
    }

    //  SOMETHING WRONG??
    public static double calculateDeceleration(double initialVelocity, double stoppingDistance) {
        return (initialVelocity * initialVelocity) / (2 * stoppingDistance);
    }

    public static double convertMphToKmph(double velocityMps) {
        return velocityMps * 18 / 5;
    }
}
