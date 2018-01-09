package com.frauas.his.rt.gui;

import com.frauas.his.rt.controller.Calculation;
import com.frauas.his.rt.controller.WheelController;
import com.frauas.his.rt.models.Wheel;
import com.frauas.his.rt.utils.Constants;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main implements ActionListener {
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
        cbRoadType.setModel(new DefaultComboBoxModel(Constants.ROAD_TYPES.values()));
        cbRoadCondition.setModel(new DefaultComboBoxModel(Constants.ROAD_CONDITIONS.values()));

        //  SET LISTENERS TO BUTTONS.
        btnStart.addActionListener(this);
        btnBrake.addActionListener(this);

//        Calculation calc = new Calculation();
//        wheel = new Wheel(0, 0);
//        wheel.setAbsActive(true);
//        double coeff = Constants.STATIC_FRICTION_DRY_ROAD;
//        double stoppingDistance = 0;
//        double deceleration = 0;
//        double stoppingTime = 0;
//
//        DecimalFormat df = new DecimalFormat("#.##");
//
//        for (int i = 0; i <= 50; i++) {
//            wheel.setVelocity(i);
//            stoppingDistance = Calculation.calculateStoppingDistance(wheel.getVelocity(), coeff);
//            deceleration = Calculation.calculateDeceleration(wheel.getVelocity(), stoppingDistance);
////            stoppingTime = Calculation.calculateStoppingTime(wheel.getVelocity(), deceleration);
//            System.out.println(df.format(stoppingDistance) + ",\t" + df.format(stoppingTime));
//        }
    }

    public void brake() {
        // ACTIVATE ABS ONLY IF THE SPEED OF THE WHEEL IS GREATER THAN 20KMPH
        if (Calculation.convertMphToKmph(wheel.getVelocity()) > 20.0d) {
            wheel.setAbsActive(true);
        } else {
            wheel.setAbsActive(false);
        }

        //  APPLY BREAK HERE.
        do {

        } while (wheel.getVelocity() != 0);
    }

    public static void main(String[] args) {

        Main mainPanel = new Main();

        JFrame frame = new JFrame("Main");
        frame.setContentPane(mainPanel.jpParent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

//    private JPanel createGraph() {
//        List<Double> scores = new ArrayList<Double>();
//        Random random = new Random();
//        int maxDataPoints = 40;
//        int maxScore = 10;
//        for (int i = 0; i < maxDataPoints; i++) {
//            scores.add((double) random.nextDouble() * maxScore);
//        }
//        GraphPanel graphPanel = new GraphPanel(scores);
//        graphPanel.setPreferredSize(new Dimension(400, 250));
//
//        return graphPanel;
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(btnStart.getText())) {
            System.out.println("Start pressed");
            jpContents.removeAll();

            double initialVelocity = Double.parseDouble(txtInitialVelocity.getText());
            this.wheel = new Wheel(Double.parseDouble(txtRadiusOfWheel.getText()), Double.parseDouble(txtWeight.getText()));
            this.wheel.setVelocity(initialVelocity);
            this.series = new TimeSeries("Random Data", Millisecond.class);

            //  STOP EXISTING THREAD IF ANY.
            if (this.controller != null) this.controller.killThread();
            this.controller = new WheelController(this.wheel, this.series);

            td = new Thread(this.controller);
            td.start();

            //  GENERATING TEST SERIES RANDOMLY.
            final TimeSeriesCollection dataset = new TimeSeriesCollection(series);

            DynamicChart chart = new DynamicChart("TESTING");

            JFreeChart jChart = chart.createChart(dataset, "Time", "Speed");
            jChart.removeLegend();
            ChartPanel cp = new ChartPanel(jChart);

            //  ADD TO THE PANEL
            jpContents.add(cp);
//            jpContents.add(cp);

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
