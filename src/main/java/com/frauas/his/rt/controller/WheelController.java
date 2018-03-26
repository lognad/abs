package com.frauas.his.rt.controller;

import com.frauas.his.rt.gui.XYChart;
import com.frauas.his.rt.listeners.ChartHelper;
import com.frauas.his.rt.listeners.UIUpdater;
import com.frauas.his.rt.models.Output;
import com.frauas.his.rt.models.Wheel;
import com.frauas.his.rt.utils.Calculation;
import com.frauas.his.rt.utils.Constants;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class WheelController implements Runnable {

    UIUpdater listener;

    public void setListener(UIUpdater listener) {
        this.listener = listener;
    }

    private Wheel wheel;
    private int[] roadCondition = new int[3];
    private double[] roadDistance = new double[3];
    private JPanel jpHeader;
    private JPanel jpContent;
    private double totalStoppingDistNoABS;
    private double totalTimeNoABS;

    private double decreaseInSpeedPerPulse;
    private int PULSE_RATE = 15;
    private boolean pulse = false;

    private double vVehicle;
    private List<Output> vWheelOutput = new ArrayList<>();
//    private List<Output> vVehicleOutput = new ArrayList<>();

    private List<Double> velocity = new ArrayList<>();
    private List<Double> decrement = new ArrayList<>();

    private double totalStoppingDist = 0d;
    private long totalTime = 0;

    float time = 0;
//    double distanceTravelled = 0;
//    int index = 0;

    XYSeries velVsTimeSeries;
    XYSeries slipVsTimeSeries;
    XYChart velVsTimeChart;
    XYChart slipVsTimeChart;
    XYSeries series;

    //    private HashMap<Long, Double> vVehicleOutput = new HashMap<>();
//    private HashMap<Long, Double> vWheelOutput = new HashMap<>();


    public WheelController(Wheel wheel, int[] roadCondition, double[] roadDistance, JPanel jpHeader, JPanel jpContent) {
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
//        double stoppingDistanceABS = Calculation.calculateStoppingDistance(this.wheel.getVelocity(), Constants.ROAD_CONDITIONS.values()[this.roadCondition[0]].getCoeff());
//        double stoppingTimeABS = Calculation.calculateStoppingTime(this.wheel.getVelocity(), Constants.ROAD_CONDITIONS.values()[this.roadCondition[0]].getCoeff());
//        double decelerationABS = Calculation.calculateDeceleration(this.wheel.getVelocity(), stoppingDistanceABS);
//        decreaseInSpeedPerPulse = Math.abs(decelerationABS) / PULSE_RATE;

//        double distanceCovered = 0;

        //  ADD INITIAL VELOCITY TO OUTPUT ARRAY.
        vWheelOutput.add(new Output(this.wheel.getVelocity(), time, 0));

//        vVehicleOutput.add(new Output(vVehicle, time, 0));

        velVsTimeSeries = new XYSeries("Velocity vs Time");
        slipVsTimeSeries = new XYSeries("Slip vs Time");

        createGraphs();
        velVsTimeChart.revalidate();
        slipVsTimeChart.revalidate();

        applyBrakeWithoutABS();
        applyBrakeABS();
    }


    private void applyBrakeABS() {
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
        double v2 = Calculation.calculateVeloctiy(v1, sd1, d1);
        sd = Calculation.calculateStoppingDistance(v2, Constants.ROAD_CONDITIONS.values()[roadCondition[1]].getCoeff());
        double sd2 = sd;
        if (sd2 > roadDistance[1])
            sd2 = roadDistance[1];
        double st2 = Calculation.calculateStoppingTime(v2, Constants.ROAD_CONDITIONS.values()[roadCondition[1]].getCoeff());
        double d2 = Calculation.calculateDeceleration(v2, sd);
        double decrement2 = Math.abs(d2) / PULSE_RATE;

        //  ROAD_CONDITION_3
        double v3 = Calculation.calculateVeloctiy(v2, sd2, d2);
        double sd3 = Calculation.calculateStoppingDistance(v3, Constants.ROAD_CONDITIONS.values()[roadCondition[2]].getCoeff());
        double st3 = Calculation.calculateStoppingTime(v3, Constants.ROAD_CONDITIONS.values()[roadCondition[2]].getCoeff());
        double d3 = Calculation.calculateDeceleration(v3, sd3);
        double decrement3 = Math.abs(d3) / PULSE_RATE;


        velocity.add(v1);
        velocity.add(v2);
        velocity.add(v3);

        decrement.add(decrement1);
        decrement.add(decrement2);
        decrement.add(decrement3);

        sd1 = Double.isNaN(sd1) ? 0 : sd1;
        sd2 = Double.isNaN(sd2) ? 0 : sd2;
        sd3 = Double.isNaN(sd3) ? 0 : sd3;

        sd = sd1 + sd2 + sd3;
        double st = st1 + st2 + st3;
        System.out.println("\n\n\n");
        System.out.println("velocity: \tv1 -> " + v1 + "\t+\tv2 -> " + v2 + "\t+\tv3 -> " + v3);
        System.out.println("sd:\t" + sd1 + "\t+\t" + sd2 + "\t+\t" + sd3 + "\t=\t" + sd);
        System.out.println("decrement:\t" + decrement1 + "\t+\t" + decrement2 + "\t+\t" + decrement3);
        System.out.println("st:\t" + st1 + "\t+\t" + st2 + "\t+\t" + st3 + "\t=\t" + st);


        totalStoppingDist = sd;

        //  START TIME WHEN APPLYING BRAKE.
        totalTime = System.nanoTime();

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            int pulseCount = 0;
            int index = 0;
            double nextV = velocity.get(index + 1);
            float slip = 0;

            @Override
            public void run() {
                time = time + (1f / PULSE_RATE);
                decreaseInSpeedPerPulse = decrement.get(index);
                vVehicle -= decreaseInSpeedPerPulse;

                //  SET END CONDITION
                if (vVehicle <= 0) {
                    vVehicle = 0;
                    wheel.setVelocity(0);

                    vWheelOutput.add(new Output(wheel.getVelocity(), time, slip));
//                    vVehicleOutput.add(new Output(vVehicle, time, slip));

                    velVsTimeSeries.add(time, wheel.getVelocity());
                    slipVsTimeSeries.add(time, vVehicle);
                    velVsTimeChart.revalidate();
                    slipVsTimeChart.revalidate();
                    jpContent.revalidate();

                    System.out.println("Stopped\t" + wheel.getVelocity() + "\t" + vVehicle);
                    cancel();

                    //  STOP TIME
                    totalTime = System.nanoTime() - totalTime;

                    //  GENERATE GRAPHS.
//                    createGraphs();
                    updateUI();
                    return;
                }

                if (pulse) {
                    wheel.setVelocity((1 - Constants.MINIMUM_THRESHOLD_SLIP_RATIO) * vVehicle);
                    slip = (float) Constants.MINIMUM_THRESHOLD_SLIP_RATIO;
                } else {
                    wheel.setVelocity((1 - Constants.MAXIMUM_THRESHOLD_SLIP_RATIO) * vVehicle);
                    slip = (float) Constants.MAXIMUM_THRESHOLD_SLIP_RATIO;
                }
                if (pulseCount++ == 1) {
                    pulse = !pulse;
                    pulseCount = 0;
                }

                //  SAVE THE OUTPUTS.
                vWheelOutput.add(new Output(wheel.getVelocity(), time, slip));
//                vVehicleOutput.add(new Output(vVehicle, time, slip));

                velVsTimeSeries.add(time, Calculation.convertMpsToKmph(wheel.getVelocity()));
                slipVsTimeSeries.add(time, Calculation.convertMpsToKmph(vVehicle));
                series.add(time, slip);
                velVsTimeChart.revalidate();
                slipVsTimeChart.revalidate();
//                jpContent.revalidate();
//                System.out.println(time
//                        + "\t"
//                        + Calculation.convertMpsToKmph(wheel.getVelocity())
//                        + "\t"
//                        + Calculation.convertMpsToKmph(vVehicle)
//                        + "\t"
//                        + slip);

                //  CHANGE TO NEXT ROAD CONDITION IF
                if (vVehicle < nextV) {
                    if (index + 1 >= velocity.size()) {
                        nextV = 0;
                    } else {
                        try {
                            nextV = velocity.get(++index + 1);
                        } catch (Exception ex) {
                            nextV = 0;
                        }
                    }
                    decreaseInSpeedPerPulse = decrement.get(index);
                }
            }
        };

        int delay = 0;
        //  SET TASK SCHEDULER FOR TIMER TO RUN FOR DEFINED PURLE_RATE PER SECOND AND START TIMER.
        t.scheduleAtFixedRate(task, delay, ((1 * 1000) / PULSE_RATE));
    }

    private void applyBrakeWithoutABS() {
        double sd = 0;

        //  ROAD_CONDITION_1
        double v1 = this.wheel.getVelocity();
        sd = Calculation.calculateStoppingDistance(v1, Constants.ROAD_CONDITIONS_KINETIC.values()[roadCondition[0]].getCoeff());
        double sd1 = sd;
        if (sd1 > roadDistance[0]) {
            sd1 = roadDistance[0];
        }
        double d1 = Calculation.calculateDeceleration(v1, sd);

        //  ROAD_CONDITION_2
        double v2 = Calculation.calculateVeloctiy(v1, sd1, d1);
        sd = Calculation.calculateStoppingDistance(v2, Constants.ROAD_CONDITIONS_KINETIC.values()[roadCondition[1]].getCoeff());
        double sd2 = sd;
        if (sd2 > roadDistance[1])
            sd2 = roadDistance[1];
        double d2 = Calculation.calculateDeceleration(v2, sd);

        //  ROAD_CONDITION_3
        double v3 = Calculation.calculateVeloctiy(v2, sd2, d2);
        double sd3 = Calculation.calculateStoppingDistance(v3, Constants.ROAD_CONDITIONS_KINETIC.values()[roadCondition[2]].getCoeff());

        //  CALCULATE STOPPING TIME.
        double t1 = Calculation.calculateStoppingTime(v1 - v2, Constants.ROAD_CONDITIONS_KINETIC.values()[roadCondition[0]].getCoeff());
        double t2 = Calculation.calculateStoppingTime(v2 - v3, Constants.ROAD_CONDITIONS_KINETIC.values()[roadCondition[1]].getCoeff());
        double t3 = Calculation.calculateStoppingTime(v3, Constants.ROAD_CONDITIONS_KINETIC.values()[roadCondition[2]].getCoeff());
        //  CHECK FOR NaN.
        t1 = Double.isNaN(t1) ? 0 : t1;
        t2 = Double.isNaN(t2) ? 0 : t2;
        t3 = Double.isNaN(t3) ? 0 : t3;
        totalTimeNoABS = t1 + t2 + t3;
        System.out.println(t1 + "+" + t2 + "+" + t3 + ":\tTOTAL STOPPING TIME NO ABS: " + totalTimeNoABS);

        sd1 = Double.isNaN(sd1) ? 0 : sd1;
        sd2 = Double.isNaN(sd2) ? 0 : sd2;
        sd3 = Double.isNaN(sd3) ? 0 : sd3;

        sd = sd1 + sd2 + sd3;
        System.out.println("OUTPUTS WITHOUT ABS: ");
        System.out.println("velocity: \tv1 -> " + v1 + "\t+\tv2 -> " + v2 + "\t+\tv3 -> " + v3);
        System.out.println("sd:\t" + sd1 + "\t+\t" + sd2 + "\t+\t" + sd3 + "\t=\t" + sd);

        totalStoppingDistNoABS = sd;
    }


    private void createGraphs() {
        //  VELOCITY VS TIME GRAPH.
        velVsTimeChart = new XYChart();
        velVsTimeChart.setHelper(new ChartHelper() {
            @Override
            public XYDataset createDataset(List<Output> data, String title) {
//                velVsTimeSeries = new XYSeries(title);
//                slipVsTimeSeries = new XYSeries(title);
                data.forEach(x -> {
                    velVsTimeSeries.add(x.getTime(), Calculation.convertMpsToKmph(x.getVelocity()));
//                    slipVsTimeSeries.add(x.getTime(), Calculation.convertMpsToKmph(vVehicleOutput.get(data.indexOf(x)).getVelocity()));
                    slipVsTimeSeries.add(x.getTime(), Calculation.convertMpsToKmph(vVehicle));
                });
                XYSeriesCollection coll = new XYSeriesCollection();
                coll.addSeries(velVsTimeSeries);
                coll.addSeries(slipVsTimeSeries);
                return coll;
            }
        });
        ChartPanel cpVelVsTime = new ChartPanel(velVsTimeChart.createChart(vWheelOutput, "Velocity vs Time", "Time", "Velocity"));
        cpVelVsTime.setPreferredSize(new Dimension(500, 300));
//        cp2.getChart().getXYPlot().getRangeAxis().setRange(0, totalStoppingDist);

        //  SLIP VS TIME GRAPH
        slipVsTimeChart = new XYChart();
        slipVsTimeChart.setHelper((data, title) -> {
//                XYSeries series = new XYSeries(title);
            series = new XYSeries(title);
            data.forEach(x -> {
                series.add(x.getTime(), x.getSlip());
            });
            XYSeriesCollection coll = new XYSeriesCollection();
            coll.addSeries(series);
            return coll;
        });
        ChartPanel cpSlipVsTime = new ChartPanel(slipVsTimeChart.createChart(vWheelOutput, "Slip Vs Time", "Time", "Slip"));
        cpSlipVsTime.setPreferredSize(new Dimension(500, 300));
        cpSlipVsTime.getChart().getXYPlot().getRangeAxis().setRange(0, 1);

        //  SET GRAPHS TO THE UI.
        this.jpContent.removeAll();
//        this.jpContent.add(cp);
        this.jpContent.add(cpVelVsTime);
        this.jpContent.add(cpSlipVsTime);
        this.jpContent.revalidate();
    }

    private void updateUI() {
        this.listener.update(totalStoppingDist, totalTime, totalStoppingDistNoABS, totalTimeNoABS, 0);
    }
}
