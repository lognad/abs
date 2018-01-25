package com.frauas.his.rt.gui;

import com.frauas.his.rt.listeners.ChartHelper;
import com.frauas.his.rt.listeners.UIUpdater;
import com.frauas.his.rt.models.Output;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.List;

public class XYChart extends JPanel {
    UIUpdater listener;

    ChartHelper helper;

    public XYChart() {
        super();
    }

    public void setHelper(ChartHelper helper) {
        this.helper = helper;
    }

    public JFreeChart createChart(List<Output> data, String title, String xLabel, String yLabel) {
        XYDataset dataset = this.helper.createDataset(data, title);  //createDataSeries(data, title);
        return ChartFactory.createXYLineChart(title,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
    }

    private XYDataset createDataSeries(List<Output> data, String title) {
        XYSeries series = new XYSeries(title);
        data.forEach(x -> {
            series.add(x.getTime(), x.getSlip());
        });
        XYSeriesCollection coll = new XYSeriesCollection();
        coll.addSeries(series);
        return coll;
    }


    public void saveChart() {
//        JFreeChart xylineChart = ChartFactory.createXYLineChart(
//                "Browser usage statastics",
//                "Category",
//                "Score",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true, true, false);
//
//        int width = 640;   /* Width of the image */
//        int height = 480;  /* Height of the image */
//        File XYChart = new File("XYLineChart.jpeg");
//        ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);
    }

}
