package com.frauas.his.rt.controller;

import com.frauas.his.rt.models.Wheel;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import static java.lang.Thread.sleep;

public class WheelController implements Runnable {
    private boolean isRunning;

    private Wheel wheel;
    private TimeSeries series;

    double v;

    public WheelController(Wheel wheel, TimeSeries series) {
        this.wheel = wheel;
        this.series = series;
        this.v = wheel.getVelocity();
    }

    @Override
    public void run() {
        this.isRunning = true;
        while (wheel.getVelocity() != 0) {
            if (!this.isRunning)
                break;

            //  RUN WHEEL HERE.
            if (this.wheel.isBreaking())
                v = applyBrake();

            this.series.addOrUpdate(new Millisecond(), v);
            this.wheel.setVelocity(v);

            //  PAUSE FOR SOME TIME.
            try {
                sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void killThread() {
        this.isRunning = false;
    }

    private double applyBrake() {
        return v - 5;
    }

    public Wheel getWheel() {
        return wheel;
    }
}
