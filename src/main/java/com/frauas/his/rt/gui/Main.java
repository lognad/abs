package com.frauas.his.rt.gui;

import com.frauas.his.rt.controller.WheelController1;
import com.frauas.his.rt.listeners.UIUpdater;
import com.frauas.his.rt.utils.Calculation;
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
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Main implements ActionListener {

    public static DecimalFormat df = new DecimalFormat("#.##");

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
    private JLabel lblStoppingDistNoABS;
    private JLabel lblStoppingTimeNoABS;
    private JTextField txtRoadDistance0;
    private JTextField txtRoadDistance1;
    private JTextField txtRoadDistance2;
    private JComboBox cbRoadCondition1;
    private JComboBox cbRoadCondition0;
    private JComboBox cbRoadCondition2;
    private JButton btnSimulate;

    private Wheel wheel;
    private WheelController controller;
    private WheelController1 controller1;

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
        cbRoadCondition0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = cbRoadCondition0.getSelectedIndex();
                cbRoadCondition1.setSelectedIndex(i);
                cbRoadCondition2.setSelectedIndex(i);
            }
        });
        cbRoadCondition1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = cbRoadCondition1.getSelectedIndex();
                cbRoadCondition2.setSelectedIndex(i);
            }
        });
    }

    public void initialize() {
        //  POPULATE COMBOBOX.

        hideUnRequired();
        cbRoadCondition.setModel(new DefaultComboBoxModel(Constants.ROAD_CONDITIONS.values()));

        cbRoadCondition0.setModel(new DefaultComboBoxModel(Constants.ROAD_CONDITIONS.values()));
        cbRoadCondition1.setModel(new DefaultComboBoxModel(Constants.ROAD_CONDITIONS.values()));
        cbRoadCondition2.setModel(new DefaultComboBoxModel(Constants.ROAD_CONDITIONS.values()));

//        cbRoadCondition0.addActionListener(this);
//        cbRoadCondition1.addActionListener(this);
//        cbRoadCondition2.addActionListener(this);

        //  SET LISTENERS TO BUTTONS.
        btnStart.addActionListener(this);
        btnBrake.addActionListener(this);
        btnSimulate.addActionListener(this);

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

    private void hideUnRequired() {
        btnStart.setVisible(false);
        btnBrake.setVisible(false);
        cbRoadCondition.setVisible(false);

    }

    public static void main(String[] args) {

        Main mainPanel = new Main();

        JFrame frame = new JFrame("Main");
        frame.setContentPane(mainPanel.jpParent);
//        frame.setPreferredSize(new Dimension(800, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(btnStart.getText())) {
            System.out.println("Start pressed");
            jpContents.removeAll();

            double initialVelocity = Calculation.convertKmphToMps(Double.parseDouble(txtInitialVelocity.getText()));
            this.wheel = new Wheel(Double.parseDouble(txtRadiusOfWheel.getText()), Double.parseDouble(txtWeight.getText()));
            this.wheel.setVelocity(initialVelocity);

            this.series = new TimeSeries("Random Data", Millisecond.class);

            double coeff = 0d;
            double coeffK = 0d;

            if (cbRoadCondition.getSelectedIndex() == Constants.ROAD_CONDITIONS.DRY.ordinal()) {
                coeff = Constants.ROAD_CONDITIONS.DRY.getCoeff();
                coeffK = Constants.KINETIC_FRICTION_DRY_ROAD;
            } else if (cbRoadCondition.getSelectedIndex() == Constants.ROAD_CONDITIONS.WET.ordinal()) {
                coeff = Constants.ROAD_CONDITIONS.WET.getCoeff();
                coeffK = Constants.KINETIC_FRICTION_WET_ROAD;
            } else if (cbRoadCondition.getSelectedIndex() == Constants.ROAD_CONDITIONS.ICY.ordinal()) {
                coeff = Constants.ROAD_CONDITIONS.ICY.getCoeff();
                coeffK = Constants.KINETIC_FRICTION_ICY_ROAD;
            }

            System.out.println("COEFF: " + coeff);

            //  STOP EXISTING THREAD IF ANY.
            if (this.controller != null) this.controller.killThread();
            this.controller = new WheelController(this.wheel, this.series, coeff, coeffK, jpContents, jpHeader);


            jpContents.removeAll();
            jpContents.revalidate();

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
        } else if (e.getActionCommand().equals(this.btnSimulate.getText())) {
            int roadConditions[] = new int[3];
            double roadDistances[] = new double[3];

            roadConditions[0] = cbRoadCondition0.getSelectedIndex();
            roadDistances[0] = Double.parseDouble(txtRoadDistance0.getText());
            roadConditions[1] = cbRoadCondition1.getSelectedIndex();
            roadDistances[1] = Double.parseDouble(txtRoadDistance1.getText());
            roadConditions[2] = cbRoadCondition2.getSelectedIndex();
            roadDistances[2] = Double.parseDouble(txtRoadDistance2.getText());

            double initialVelocity = Calculation.convertKmphToMps(Double.parseDouble(txtInitialVelocity.getText()));
            this.wheel = new Wheel(Double.parseDouble(txtRadiusOfWheel.getText()), Double.parseDouble(txtWeight.getText()));
            this.wheel.setVelocity(initialVelocity);


            this.controller1 = new WheelController1(this.wheel, roadConditions, roadDistances, jpHeader, jpContents);

            this.controller1.setListener((stoppingDist, stoppingTime, stoppingDistNoABS, stoppingTimeNoABS, deceleration) -> {
                lblStoppingDistance.setText(df.format(stoppingDist));
                lblStoppingTime.setText(String.valueOf(df.format(stoppingTime / 1000000000.0d)));
                lblStoppingDistNoABS.setText(df.format(stoppingDistNoABS));
                lblStoppingTimeNoABS.setText(String.valueOf(df.format((stoppingTimeNoABS / 1000000000.0d))));
                lblDeceleration.setText(String.valueOf(deceleration));
            });

            Thread t = new Thread(controller1);
            t.start();


        } else {
            System.out.println("Unhandled Event: " + e.getActionCommand());
        }
    }
}
