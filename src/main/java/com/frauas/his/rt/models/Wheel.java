package com.frauas.his.rt.models;

public class Wheel {
    private double weight;
    private double radius;
    private boolean isAbsActive;
    private boolean isBreaking;
    private double velocity;

    public Wheel(double radius, double weight) {
        this.radius = radius;
        this.weight = weight;
    }

    public double getWeight() {
        return radius;
    }

//    public void setWeight(double weight) {
//        this.weight = weight;
//    }

    public double getRadius() {
        return radius;
    }

//    public void setRadius(double radius) {
//        this.radius = radius;
//    }

    public boolean isAbsActive() {
        return isAbsActive;
    }

    public void setAbsActive(boolean absActive) {
        isAbsActive = absActive;
    }

    public boolean isBreaking() {
        return isBreaking;
    }

    public void setBreaking(boolean breaking) {
        isBreaking = breaking;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
}
