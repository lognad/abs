package com.frauas.his.rt.gui;

import com.frauas.his.rt.utils.Calculation;
import com.frauas.his.rt.controller.WheelController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Chart extends JPanel {
    public Chart(final String title) {
//        super(title);
        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset, "TEST", "time", "value");
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
        chartPanel.setMouseZoomable(true, false);
//        setContentPane(chartPanel);
    }

    public Chart(final String title, List<Double> ds) {
//        super(title);
//        final XYDataset dataset = createDataset(ds);
//        final JFreeChart chart = createChart(dataset);
//        final ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
//        chartPanel.setMouseZoomable(true, false);
//        setContentPane(chartPanel);
    }

    Second current = new Second();

    private XYDataset createDataset() {
        final TimeSeries series = new TimeSeries("Random Data");
//        Second current = new Second();
        double value = 100.0;

//        for (int i = 0; i < 4000; i++) {
//
//            try {
//                value = value + Math.random() - 0.5;
//                series.add(current, new Double(value));
//                current = (Second) current.next();
//            } catch (SeriesException e) {
//                System.err.println("Error adding to series");
//            }
//        }

        List<Double> x = getData();
        x.forEach(y -> {
            series.add(current, y);
            current = (Second) current.next();
        });

        return new TimeSeriesCollection(series);
    }

    public XYDataset createDataset(List<Double> d) {
        final TimeSeries series = new TimeSeries("Velocity vs Time");
//        Second current = new Second();
        double value = 100.0;
        List<Double> x = d;
        double pulsePeriod = WheelController.intervalPeriod;

        x.forEach(y -> {
            series.add(current, y);
            current = (Second) current.next();
        });

        return new TimeSeriesCollection(series);
    }

    public XYDataset createDataset(List<Double> d, String title) {
        final TimeSeries series = new TimeSeries(title);

        List<Double> x = d;
        x.forEach(y -> {
            series.add(current, Calculation.convertMphToKmph(y));
            current = (Second) current.next();
        });

        return new TimeSeriesCollection(series);
    }

    public JFreeChart createChart(final XYDataset dataset, String title, String xLabel, String yLabel) {
        return ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                dataset,
                false,
                true,
                false);
    }

    public JFreeChart createChart(List<Double> dataset, String title, String xLabel, String yLabel) {
        XYDataset ds = createDataset(dataset, title);
        return ChartFactory.createTimeSeriesChart(
                title,
                xLabel,
                yLabel,
                ds,
                false,
                true,
                false);
    }

    public static void main(final String[] args) {
        final String title = "Time Series Management";
        final Chart demo = new Chart(title);
//        demo.pack();
//        RefineryUtilities.positionFrameRandomly(demo);
        demo.setVisible(true);
    }

    public static List<Double> getData() {
        List<Double> d = new ArrayList<>();
        d.add(150d);
        d.add(149d);
        d.add(145d);
        d.add(143d);
        d.add(141d);
//        d.add(139d);
//        d.add(137d);
//        d.add(133d);
//        d.add(130d);
//        d.add(129d);
//        d.add(127d);

        return d;
    }
}
