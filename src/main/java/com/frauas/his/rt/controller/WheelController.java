package com.frauas.his.rt.controller;

import com.frauas.his.rt.gui.Chart;
import com.frauas.his.rt.gui.Main;
import com.frauas.his.rt.models.Wheel;
import com.frauas.his.rt.utils.Constants;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static java.lang.Thread.sleep;

public class WheelController implements Runnable {
    private boolean isRunning;
    private double coeff;
    private boolean pulse = true;
    public static long intervalPeriod = 2;

    private double initialVel;

    private List<Double> vOutput = new ArrayList<>();
    private List<Double> vVehicleOutput = new ArrayList<>();

    private Map<Double, Double> mOp = new HashMap();

    private Wheel wheel;
    private TimeSeries series;

    double vVehicle = 0;
    double vWheel = 0;
    double decreaseInSpeedPerPulse;

    JPanel jpHeader;
    JPanel jpContent;

    public long getIntervalPeriod() {
        return intervalPeriod;
    }

    public void setIntervalPeriod(long intervalPeriod) {
        this.intervalPeriod = intervalPeriod;
    }

    public WheelController(Wheel wheel, TimeSeries series, double coeff, JPanel p, JPanel h) {
        this.wheel = wheel;
        this.series = series;
        this.coeff = coeff;
        this.vVehicle = wheel.getVelocity();
        this.initialVel = wheel.getVelocity();

        this.jpHeader = h;
        this.jpContent = p;
    }

    @Override
    public void run() {

        double d = Calculation.calculateStoppingDistance(initialVel, coeff);
        double t = Calculation.calculateStoppingTime(initialVel, coeff);
        double dec = Calculation.calculateDeceleration(initialVel, d);
        decreaseInSpeedPerPulse = Math.abs(dec) / intervalPeriod;

        System.out.println("d: " + d);
        System.out.println("t: " + t);
        System.out.println("dec: " + dec);
        System.out.println("decInSpeed" + ": " + decreaseInSpeedPerPulse);

        this.isRunning = true;

        while (wheel.getVelocity() > 0) {
//            System.out.println("CP1: " + Calculation.convertMphToKmph(wheel.getVelocity()));
//        while (true) {
            if (!this.isRunning)
                break;

            //  RUN WHEEL HERE.
            if (this.wheel.isBreaking()) {
//                vVehicle = applyBrake();
                applyBrake();
//                this.wheel.setVelocity(vWheel);
            }
            this.series.addOrUpdate(new Millisecond(), Calculation.convertMphToKmph(vVehicle));

            //  PAUSE FOR SOME TIME.
            try {
                sleep((1 * 1000) / intervalPeriod);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //  SHOW OTHER STATIC CHARTS HERE.
        final Chart demo = new Chart("SPEED VS TIME", vVehicleOutput);
        ChartPanel cp = new ChartPanel(demo.createChart(vVehicleOutput,
                "Speed vs Time",
                "Time",
                "Speed"));
        cp.setPreferredSize(new Dimension(600, 300));

        //  REMOVE ALL EXCEPT THE FIRST COMPONENT. I.E. DYNAMIC GRAPH
        for (int i = 0; i < this.jpContent.getComponentCount(); i++) {
            if (i == 0) continue;

            this.jpContent.remove(i);
        }

        this.jpContent.add(cp);
        this.jpContent.revalidate();

        JLabel lblSd = (JLabel) this.jpHeader.getComponents()[1];
        lblSd.setText(String.valueOf(Main.df.format(d)));
        JLabel lblSt = (JLabel) this.jpHeader.getComponents()[4];
        lblSt.setText(String.valueOf(Main.df.format(t)));
        JLabel lblD = (JLabel) this.jpHeader.getComponents()[7];
        lblD.setText(String.valueOf(Main.df.format(dec)));
        jpHeader.revalidate();
    }

    public void killThread() {
        this.isRunning = false;
    }

    private synchronized void applyBrake() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                if (wheel.getVelocity() <= 0) {
                if (vVehicle <= 0) {
                    vVehicle = 0;
                    vWheel = 0;
                    wheel.setVelocity(0);
                    isRunning = false;
                    timer.cancel();
                    return;
                }

                vVehicle -= decreaseInSpeedPerPulse;
//if(vVehicle>20) {
                //calculate.
                if (pulse) {
                    vWheel = (1 - Constants.MINIMUM_THRESHOLD_SLIP_RATIO) * vVehicle;
                } else

                {
                    vWheel = (1 - Constants.MAXIMUM_THRESHOLD_SLIP_RATIO) * vVehicle;
                }
//}
//else{
////    ABSOFF
//}
                pulse = !pulse;

                //  UPDATE
//                series.addOrUpdate(new Millisecond(), Calculation.convertMphToKmph(vVehicle));
                wheel.setVelocity(vWheel);
                System.out.println(Calculation.convertMphToKmph(vWheel)
                        + "\t"
                        + Calculation.convertMphToKmph(vVehicle));

                //  SAVE OUTPUT.
                vOutput.add(vWheel);
                vVehicleOutput.add(vVehicle);
            }
        };

        long delay = 0;

        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay, (1 * 1000) / intervalPeriod);


        //DUMMY NOW
//        return vVehicle - 5;
    }

    public Wheel getWheel() {
        return wheel;
    }
}
