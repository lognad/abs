package com.frauas.his.rt.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;

public class DynamicChart extends ApplicationFrame {
    private TimeSeries series;

    private JFreeChart chart;
    public DynamicChart(String title) {
        super(title);
    }


    public JFreeChart createChart(final XYDataset dataset, String xAxisLabel, String yAxisLabel) {
        chart = ChartFactory.createTimeSeriesChart(
                this.getTitle(),
                xAxisLabel,
                yAxisLabel,
                dataset,
                true,
                true,
                false
        );
        final XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 250.0);

        return chart;
    }
}
