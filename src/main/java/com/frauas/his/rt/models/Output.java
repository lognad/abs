package com.frauas.his.rt.models;

public class Output {
    double velocity;
    float time;
    float slip;

    public float getSlip() {
        return slip;
    }

    public void setSlip(float slip) {
        this.slip = slip;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public Output(double velocity, float time, float slip) {
        this.velocity = velocity;
        this.time = time;
        this.slip = slip;
    }
}
