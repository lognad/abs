package com.frauas.his.rt.controller;

import com.frauas.his.rt.gui.Chart;
import com.frauas.his.rt.models.Wheel;
import com.frauas.his.rt.utils.Calculation;
import com.frauas.his.rt.utils.Constants;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class WheelController1 implements Runnable {

    private Wheel wheel;
    private int[] roadCondition = new int[3];
    private double[] roadDistance = new double[3];
    private JPanel jpHeader;
    private JPanel jpContent;
    private double decreaseInSpeedPerPulse;
    private int PULSE_RATE = 15;
    private boolean pulse = false;

    private double vVehicle;
    private List<Double> vWheelOutput = new ArrayList<>();
    private List<Double> vVehicleOutput = new ArrayList<>();
//    private HashMap<Long, Double> vWheelOutput = new HashMap<>();
//    private HashMap<Long, Double> vVehicleOutput = new HashMap<>();

    public WheelController1(Wheel wheel, int[] roadCondition, double[] roadDistance, JPanel jpHeader, JPanel jpContent) {
        this.wheel = wheel;
        this.roadCondition = roadCondition;
        this.roadDistance = roadDistance;
        this.jpHeader = jpHeader;
        this.jpContent = jpContent;
        this.vVehicle = this.wheel.getVelocity();
    }

    @Override
    public void run() {
        //  WITH ABS
        double dABS = Calculation.calculateStoppingDistance(this.wheel.getVelocity(), Constants.ROAD_CONDITIONS.values()[this.roadCondition[0]].getCoeff());
        double tABS = Calculation.calculateStoppingTime(this.wheel.getVelocity(), Constants.ROAD_CONDITIONS.values()[this.roadCondition[0]].getCoeff());
        double decABS = Calculation.calculateDeceleration(this.wheel.getVelocity(), dABS);
        decreaseInSpeedPerPulse = Math.abs(decABS) / PULSE_RATE;

        double distanceCovered = 0;

        //  ADD INITIAL VELOCITY TO OUTPUT ARRAY.
        vWheelOutput.add(this.wheel.getVelocity());
        vVehicleOutput.add(vVehicle);

        applyBrakeNew();
//        applyBrakeCompound();
//        applyBrake();
//        }


    }


    private void applyBrakeNew() {
        double sd = 0;
        System.out.println("Initial Velocity(mps): " + this.wheel.getVelocity());

        //  ROAD_CONDITION_1
        double v1 = this.wheel.getVelocity();
        sd = Calculation.calculateStoppingDistance(v1, Constants.ROAD_CONDITIONS.values()[roadCondition[0]].getCoeff());
        double sd1 = sd;
        if (sd1 > roadDistance[0]) {
            sd1 = roadDistance[0];
        }
        double st1 = Calculation.calculateStoppingTime(v1, Constants.ROAD_CONDITIONS.values()[roadCondition[0]].getCoeff());
        double d1 = Calculation.calculateDeceleration(v1, sd);
        double decrement1 = Math.abs(d1) / PULSE_RATE;

        //  ROAD_CONDITION_2
        double v2 = Math.sqrt(Math.abs(v1 * v1 + 2 * d1 * sd1));
        sd = Calculation.calculateStoppingDistance(v2, Constants.ROAD_CONDITIONS.values()[roadCondition[1]].getCoeff());
        double sd2 = sd;
        if (sd2 > roadDistance[1])
            sd2 = roadDistance[1];
        double st2 = Calculation.calculateStoppingTime(v2, Constants.ROAD_CONDITIONS.values()[roadCondition[1]].getCoeff());
        double d2 = Calculation.calculateDeceleration(v2, sd);
        double decrement2 = Math.abs(d2) / PULSE_RATE;

        //  ROAD_CONDITION_3
        double v3 = Math.sqrt(Math.abs(v2 * v2 + 2 * d2 * sd2));
        double sd3 = Calculation.calculateStoppingDistance(v3, Constants.ROAD_CONDITIONS.values()[roadCondition[2]].getCoeff());
        double st3 = Calculation.calculateStoppingTime(v3, Constants.ROAD_CONDITIONS.values()[roadCondition[2]].getCoeff());
        double d3 = Calculation.calculateDeceleration(v3, sd3);
        double decrement3 = Math.abs(d3) / PULSE_RATE;


        sd = sd1 + sd2 + sd3;
        double st = st1 + st2 + st3;
        System.out.println("velocity: \tv1 -> " + v1 + "\t+\tv2 -> " + v2 + "\t+\tv3 -> " + v3);
        System.out.println("sd:\t" + sd1 + "\t+\t" + sd2 + "\t+\t" + sd3 + "\t=\t" + sd);
        System.out.println("decrement:\t" + decrement1 + "\t+\t" + decrement2 + "\t+\t" + decrement3);
        System.out.println("st:\t" + st1 + "\t+\t" + st2 + "\t+\t" + st3 + "\t=\t" + st);
    }


    int time = 0;
    double distanceTravelled = 0;
    int index = 0;

    private void applyBrakeCompound() {


        Timer timer = new Timer();
        TimerTask task = new TimerTask() {  //  TIMER TASK IS THREAD SAFE. NO NEED FOR EXTERNAL SYNCHRONIZATION.
            int pulseCount = 0;

            @Override
            public void run() {

                double rd = roadDistance[index];
                int rc = roadCondition[index];
                double sd = Calculation.calculateStoppingDistance(wheel.getVelocity(), Constants.ROAD_CONDITIONS.values()[rc].getCoeff());
//        double st = Calculation.calculateStoppingTime(this.wheel.getVelocity(), Constants.ROAD_CONDITIONS.values()[rd].getCoeff());
                double dec = Calculation.calculateDeceleration(wheel.getVelocity(), sd);
                decreaseInSpeedPerPulse = Math.abs(dec) / PULSE_RATE;

                time += 0.15;

                distanceTravelled = Calculation.calculateDistanceTravelled(vVehicle, time);

                System.out.println(time + "\t->\t" + distanceTravelled);

                //  END CONDITION IF THE ROAD CONDITION CHANGES DURING THE BREAKING.
                if (distanceTravelled > rd) {
                    //  CHANGE ROAD CONDITION.
                    index++;
                }

                vVehicle -= decreaseInSpeedPerPulse;

                //  SET END CONDITION
                if (vVehicle < 0) {
                    vVehicle = 0;
                    wheel.setVelocity(0);

                    vWheelOutput.add(wheel.getVelocity());
                    vVehicleOutput.add(vVehicle);
                    System.out.println("Stopped\t" + wheel.getVelocity() + "\t" + vVehicle);
                    cancel();

                    //  GENERATE GRAPHS.
                    createGraphs();
                    return;
                }


                if (pulse) {
                    wheel.setVelocity((1 - Constants.MINIMUM_THRESHOLD_SLIP_RATIO) * vVehicle);
                } else {
                    wheel.setVelocity((1 - Constants.MAXIMUM_THRESHOLD_SLIP_RATIO) * vVehicle);
                }
                if (pulseCount++ == 2) {
                    pulse = !pulse;
                    pulseCount = 0;
                }

                //  SAVE THE OUTPUTS.
                vWheelOutput.add(wheel.getVelocity());
                vVehicleOutput.add(vVehicle);

                System.out.println(Calculation.convertMpsToKmph(wheel.getVelocity())
                        + "\t"
                        + Calculation.convertMpsToKmph(vVehicle));
            }
        };

        int delay = 0;
        timer.scheduleAtFixedRate(task, delay, ((1 * 1000) / PULSE_RATE));

    }

    private void applyBrake() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {  //  TIMER TASK IS THREAD SAFE. NO NEED FOR EXTERNAL SYNCHRONIZATION.
            int pulseCount = 0;

            @Override
            public void run() {
                vVehicle -= decreaseInSpeedPerPulse;

                //  SET END CONDITION
                if (vVehicle < 0) {
                    vVehicle = 0;
                    wheel.setVelocity(0);

                    vWheelOutput.add(wheel.getVelocity());
                    vVehicleOutput.add(vVehicle);
                    System.out.println("Stopped\t" + wheel.getVelocity() + "\t" + vVehicle);
                    cancel();

                    //  GENERATE GRAPHS.
                    createGraphs();
                    return;
                }


                if (pulse) {
                    wheel.setVelocity((1 - Constants.MINIMUM_THRESHOLD_SLIP_RATIO) * vVehicle);
                } else {
                    wheel.setVelocity((1 - Constants.MAXIMUM_THRESHOLD_SLIP_RATIO) * vVehicle);
                }
                if (pulseCount++ == 2) {
                    pulse = !pulse;
                    pulseCount = 0;
                }

                //  SAVE THE OUTPUTS.
                vWheelOutput.add(wheel.getVelocity());
                vVehicleOutput.add(vVehicle);

                System.out.println(Calculation.convertMpsToKmph(wheel.getVelocity())
                        + "\t"
                        + Calculation.convertMpsToKmph(vVehicle));
            }
        };

        int delay = 0;
        timer.scheduleAtFixedRate(task, delay, ((1 * 1000) / PULSE_RATE));
    }

    private void createGraphs() {
        final Chart chart = new Chart("SPEED VS TIME", vWheelOutput);
        ChartPanel cp = new ChartPanel(chart.createChart(vWheelOutput, vVehicleOutput,
                "Wheel",
                "Time",
                "Speed"));
        cp.setPreferredSize(new Dimension(500, 300));

        ChartPanel cp1 = new ChartPanel(chart.createChart(vVehicleOutput, vWheelOutput,
                "Vehicle",
                "Time",
                "Speed"));
        cp1.setPreferredSize(new Dimension(500, 300));

        this.jpContent.removeAll();
        this.jpContent.add(cp);
        this.jpContent.add(cp1);
        this.jpContent.revalidate();
    }
}
