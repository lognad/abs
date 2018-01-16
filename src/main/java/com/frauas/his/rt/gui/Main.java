package com.frauas.his.rt.gui;

import com.frauas.his.rt.controller.Calculation;
import com.frauas.his.rt.controller.WheelController;
import com.frauas.his.rt.models.Wheel;
import com.frauas.his.rt.utils.Constants;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main implements ActionListener {

    public static DecimalFormat df = new DecimalFormat(".##");

    private JPanel jpParent;
    private JScrollPane jspMain;
    private JPanel jpContainer;
    private JPanel jpConfigurations;
    private JPanel jpHeader;
    private JTextField txtWeight;
    private JTextField txtRadiusOfWheel;
    private JPanel jpContents;
    private JButton btnBrake;
    private JLabel lblRoadType;
    private JComboBox cbRoadType;
    private JLabel lblRoadCondition;
    private JComboBox cbRoadCondition;
    private JLabel lblWeight;
    private JLabel lblRadiusOfWheel;
    private JButton btnStart;
    private JLabel lblInitialVelocity;
    private JTextField txtInitialVelocity;
    private JLabel lblStoppingDistance;
    private JLabel lblStoppingTime;
    private JLabel lblDeceleration;

    private Wheel wheel;
    private WheelController controller;
    private TimeSeries series;
    private double velocity = 150;

    Thread td;

    public Main() {
        initialize();

        jpContents.setPreferredSize(new Dimension(800, 600));
//        createGraph();
        txtWeight.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char input = e.getKeyChar();
                if (!(Character.isDigit(input)
                        || input == KeyEvent.VK_PERIOD
                        || input == KeyEvent.VK_BACK_SPACE
                        || input == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
    }

    public void initialize() {
        //  POPULATE COMBOBOX.
//        cbRoadType.setModel(new DefaultComboBoxModel(Constants.ROAD_TYPES.values()));
        cbRoadType.setVisible(false);
        lblRoadType.setVisible(false);
        cbRoadCondition.setModel(new DefaultComboBoxModel(Constants.ROAD_CONDITIONS.values()));

        //  SET LISTENERS TO BUTTONS.
        btnStart.addActionListener(this);
        btnBrake.addActionListener(this);

        JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL, 30, 40, 0, 300);
        vbar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                vbar.repaint();
            }
        });
//        hbar.setUnitIncrement(2);
//        hbar.setBlockIncrement(1);
        jpParent.add(vbar, BorderLayout.EAST);
    }

    public static void main(String[] args) {

        Main mainPanel = new Main();

        JFrame frame = new JFrame("Main");
        frame.setContentPane(mainPanel.jpParent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(btnStart.getText())) {
            System.out.println("Start pressed");
            jpContents.removeAll();

            double initialVelocity = Calculation.convertKmphToMph(Double.parseDouble(txtInitialVelocity.getText()));
            this.wheel = new Wheel(Double.parseDouble(txtRadiusOfWheel.getText()), Double.parseDouble(txtWeight.getText()));
            this.wheel.setVelocity(initialVelocity);

            this.series = new TimeSeries("Random Data", Millisecond.class);

            double coeff = 0d;

            if (cbRoadCondition.getSelectedIndex() == Constants.ROAD_CONDITIONS.DRY.ordinal()) {
                coeff = Constants.ROAD_CONDITIONS.DRY.getCoeff();
            } else if (cbRoadCondition.getSelectedIndex() == Constants.ROAD_CONDITIONS.WET.ordinal()) {
                coeff = Constants.ROAD_CONDITIONS.WET.getCoeff();
            } else if (cbRoadCondition.getSelectedIndex() == Constants.ROAD_CONDITIONS.ICY.ordinal()) {
                coeff = Constants.ROAD_CONDITIONS.ICY.getCoeff();
            }

            System.out.println("COEFF: " + coeff);

            //  STOP EXISTING THREAD IF ANY.
            if (this.controller != null) this.controller.killThread();
            this.controller = new WheelController(this.wheel, this.series, coeff, jpContents, jpHeader);

            td = new Thread(this.controller);
            td.start();

            //  GENERATING TEST SERIES RANDOMLY.
            final TimeSeriesCollection dataset = new TimeSeriesCollection(series);

            DynamicChart chart = new DynamicChart("");

            JFreeChart jChart = chart.createChart(dataset, "Time", "Speed");
            jChart.removeLegend();
            ChartPanel cp = new ChartPanel(jChart);
            cp.setPreferredSize(new Dimension(600, 300));
            //  ADD TO THE PANEL
            jpContents.add(cp);

            jpContents.revalidate();
//            //  resize the main jframe.
//            SwingUtilities.getWindowAncestor(jpParent).pack();


        } else if (e.getActionCommand().equals(this.btnBrake.getText())) {
            System.out.println(e.getActionCommand() + "Brake Pressed.");
            this.controller.getWheel().setBreaking(!this.controller.getWheel().isBreaking());
        } else {
            System.out.println("Unhandled Event: " + e.getActionCommand());
        }
    }
}