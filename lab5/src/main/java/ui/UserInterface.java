package ui;

import service.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class UserInterface {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int CLK = 1000;
    private static final Font textFont = new Font("Text font", Font.ITALIC, 15);
    private static final Font headerFont = new Font("Header font", Font.BOLD, 20);
    private static final Font lowHeaderFont = new Font("Low header font", Font.PLAIN, 20);

    private static JFrame frame0;
    private static JFrame frame1;
    private static JFrame frame2;

    private static List<JPanel> rootPanel = new ArrayList<>(3);
    private static List<JPanel> inputPanel = new ArrayList<>(3);
    private static List<JPanel> outputPanel = new ArrayList<>(3);
    private static List<JPanel> debugPanel = new ArrayList<>(3);

    private static List<JTextArea> inputArea = new ArrayList<>(3);
    private static List<JTextArea> outputArea = new ArrayList<>(3);
    private static List<JTextArea> debugArea = new ArrayList<>(3);

    private static List<JComboBox> SAComboBox = new ArrayList<>(3);
    private static List<JComboBox> DAComboBox = new ArrayList<>(3);

    private static List<JTextArea> tokenArea = new ArrayList<>(3);
    private static List<JCheckBox> earlyTokenRelease = new ArrayList<>(3);
    private static JButton tokenLaunch;

    private static List<Integer> sa = new ArrayList<>(3);
    private static List<Integer> da = new ArrayList<>(3);

    private static List<Queue<String>> inputData = new ArrayList<>(3);
    private static List<String> currentInputText = new ArrayList<>(3);

    private static List<Channel> channels = new ArrayList<>(3);
    private static List<Boolean> token = new ArrayList<>(3);
    private static int senderIndex = 0;

    public void init() {
        initInputPanels();
        initOutputPanels();
        initMonitorDebugPanel();
        initDebugPanels();
        initRootPanels();
        initFrames();
        addAddressListeners();
        initQueues();
        initInputListeners();
        initLaunchTokenButton();
        initChannels();
    }

    private void initInputPanels() {
        for (int i = 0; i < 3; i++) {
            JPanel inputP = new JPanel();
            inputP.setLayout(new BorderLayout());

            JTextArea inputA = new JTextArea(4, 60);
            JScrollPane sp = new JScrollPane(inputA);
            inputA.setLineWrap(true);
            inputA.setFont(textFont);
            inputArea.add(i, inputA);

            JLabel inputHeader = new JLabel("Input (do not hold down the key, the last character is transmitted)");
            inputHeader.setFont(headerFont);

            inputP.add(inputHeader, BorderLayout.NORTH);
            inputP.add(sp);

            inputPanel.add(i, inputP);
        }
    }

    private void initOutputPanels() {
        for (int i = 0; i < 3; i++) {
            JPanel outputP = new JPanel();
            outputP.setLayout(new BorderLayout());

            JTextArea outputA = new JTextArea(4, 60);
            outputA.setEditable(false);
            JScrollPane sp = new JScrollPane(outputA);
            outputA.setLineWrap(true);
            outputA.setFont(textFont);
            outputArea.add(i, outputA);

            JLabel outputHeader = new JLabel("Output");
            outputHeader.setFont(headerFont);

            outputP.add(outputHeader, BorderLayout.NORTH);
            outputP.add(sp);

            outputPanel.add(i, outputP);
        }
    }

    private void initMonitorDebugPanel() {
        JPanel debugP = new JPanel();
        debugP.setLayout(new BorderLayout());

        JTextArea debugA = new JTextArea(4, 60);
        debugA.setEditable(false);
        JScrollPane sp = new JScrollPane(debugA);
        debugA.setLineWrap(true);
        debugA.setFont(textFont);
        debugArea.add(0, debugA);

        JLabel debugHeader = new JLabel("Control & debug");
        debugHeader.setFont(headerFont);

        debugP.add(debugHeader, BorderLayout.NORTH);
        debugP.add(sp);

        JPanel debugActionsPanel = new JPanel();
        debugActionsPanel.setLayout(new GridBagLayout());

        sa.add(0, 0);
        da.add(0, 1);

        JComboBox<Integer> source = new JComboBox<>(generateAddresses());
        JLabel sourceL = new JLabel("Source: ");
        sourceL.setFont(textFont);
        SAComboBox.add(0, source);
        debugActionsPanel.add(sourceL);
        debugActionsPanel.add(source);
        debugActionsPanel.add(new JLabel("   "));


        JComboBox<Integer> destination = new JComboBox<>(generateAddresses());
        destination.setSelectedItem(1);
        JLabel destinationL = new JLabel(" Destination: ");
        destinationL.setFont(textFont);
        DAComboBox.add(0, destination);
        debugActionsPanel.add(destinationL);
        debugActionsPanel.add(destination);
        debugActionsPanel.add(new JLabel("   "));


        JCheckBox earlyMarker = new JCheckBox("Early token release");
        earlyMarker.setFont(textFont);
        earlyTokenRelease.add(0, earlyMarker);
        debugActionsPanel.add(earlyMarker);
        debugActionsPanel.add(new JLabel("   "));

        tokenLaunch = new JButton("Launch token");
        tokenLaunch.setFont(textFont);
        debugActionsPanel.add(tokenLaunch);
        debugActionsPanel.add(new JLabel("                                                   "));

        JLabel markerL = new JLabel("Token: ");
        markerL.setFont(lowHeaderFont);
        debugActionsPanel.add(markerL);
        JTextArea tokenA = new JTextArea();
        tokenA.setEditable(false);
        tokenA.setColumns(2);
        tokenA.setFont(lowHeaderFont);
        tokenArea.add(0, tokenA);
        debugActionsPanel.add(tokenA);

        debugP.add(debugActionsPanel, BorderLayout.SOUTH);

        debugPanel.add(0, debugP);
    }

    private void initDebugPanels() {
        for (int i = 1; i < 3; i++) {
            JPanel debugP = new JPanel();
            debugP.setLayout(new BorderLayout());

            JTextArea debugA = new JTextArea(4, 60);
            debugA.setEditable(false);
            JScrollPane sp = new JScrollPane(debugA);
            debugA.setLineWrap(true);
            debugA.setFont(textFont);
            debugArea.add(i, debugA);

            JLabel debugHeader = new JLabel("Control & debug");
            debugHeader.setFont(headerFont);

            debugP.add(debugHeader, BorderLayout.NORTH);
            debugP.add(sp);

            JPanel debugActionsPanel = new JPanel();
            debugActionsPanel.setLayout(new GridBagLayout());

            JComboBox<Integer> source = new JComboBox<>(generateAddresses());
            JLabel sourceL = new JLabel("Source: ");
            sourceL.setFont(textFont);
            SAComboBox.add(i, source);
            debugActionsPanel.add(sourceL);
            debugActionsPanel.add(source);
            debugActionsPanel.add(new JLabel("   "));
            sa.add(i, 0);

            JComboBox<Integer> destination = new JComboBox<>(generateAddresses());
            destination.setSelectedIndex(1);
            JLabel destinationL = new JLabel(" Destination: ");
            destinationL.setFont(textFont);
            DAComboBox.add(i, destination);
            debugActionsPanel.add(destinationL);
            debugActionsPanel.add(destination);
            debugActionsPanel.add(new JLabel("   "));
            da.add(i, 1);

            JCheckBox earlyMarker = new JCheckBox("Early token release");
            earlyMarker.setFont(textFont);
            earlyTokenRelease.add(i, earlyMarker);
            debugActionsPanel.add(earlyMarker);
            debugActionsPanel.add(new JLabel("          " +
                    "                                                                                      "));


            JLabel markerL = new JLabel("Token: ");
            markerL.setFont(lowHeaderFont);
            debugActionsPanel.add(markerL);
            JTextArea tokenA = new JTextArea();
            tokenA.setEditable(false);
            tokenA.setColumns(2);
            tokenA.setFont(lowHeaderFont);
            tokenArea.add(i, tokenA);
            debugActionsPanel.add(tokenA);

            debugP.add(debugActionsPanel, BorderLayout.SOUTH);

            debugPanel.add(i, debugP);
        }
    }

    private void initRootPanels() {
        for (int i = 0; i < 3; i++) {
            JPanel buf = new JPanel();
            buf.setLayout(new BorderLayout());
            buf.add(inputPanel.get(i), BorderLayout.NORTH);
            buf.add(outputPanel.get(i), BorderLayout.CENTER);
            buf.add(debugPanel.get(i), BorderLayout.SOUTH);
            rootPanel.add(i, buf);
        }
    }

    private void initFrames() {
        frame0 = new JFrame("Station 1 (emulating monitor)");
        frame1 = new JFrame("Station 2");
        frame2 = new JFrame("Station 3");

        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame0.setSize(WIDTH, HEIGHT);
        frame0.setLocation(100, 50);
        frame0.setResizable(false);

        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(WIDTH, HEIGHT);
        frame1.setLocation(950, 350);
        frame1.setResizable(false);

        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(WIDTH, HEIGHT);
        frame2.setLocation(100, 600);
        frame2.setResizable(false);

        frame0.add(rootPanel.get(0));
        frame1.add(rootPanel.get(1));
        frame2.add(rootPanel.get(2));

        frame0.setVisible(true);
        frame1.setVisible(true);
        frame2.setVisible(true);
    }

    private void initQueues() {
        for (int i = 0; i < 3; i++) {
            inputData.add(i, new ArrayDeque<String>());
            currentInputText.add(i, "");
            token.add(i, false);
        }
    }

    private void initInputListeners() {
        inputArea.get(0).addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newText = inputArea.get(0).getText();
                if (!currentInputText.get(0).equals(newText)) {
                    String symbol = newText.substring(newText.length() - 1);
                    inputData.get(0).add(symbol);
                    currentInputText.set(0, currentInputText.get(0) + symbol);
                    inputArea.get(0).setText(currentInputText.get(0));
                }
            }
        });

        inputArea.get(1).addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newText = inputArea.get(1).getText();
                if (!currentInputText.get(1).equals(newText)) {
                    String symbol = newText.substring(newText.length() - 1);
                    inputData.get(1).add(symbol);
                    currentInputText.set(1, currentInputText.get(1) + symbol);
                    inputArea.get(1).setText(currentInputText.get(1));
                }
            }
        });

        inputArea.get(2).addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newText = inputArea.get(2).getText();
                if (!currentInputText.get(2).equals(newText)) {
                    String symbol = newText.substring(newText.length() - 1);
                    inputData.get(2).add(symbol);
                    currentInputText.set(2, currentInputText.get(2) + symbol);
                    inputArea.get(2).setText(currentInputText.get(2));
                }
            }
        });
    }

    private void initLaunchTokenButton() {
        tokenLaunch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendToken(0);
                tokenLaunch.setEnabled(false);
            }
        });
    }

    private Integer[] generateAddresses() {
        Integer[] array = new Integer[256];
        for (int i = 0; i < 256; i++) {
            array[i] = i;
        }
        return array;
    }

    private void addAddressListeners() {
        SAComboBox.get(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SAComboBox.get(0).getSelectedItem() == da.get(0)) {
                    debugMessage("source can't be equals destination, select another one.", 0);
                    SAComboBox.get(0).setSelectedItem(sa.get(0));
                } else {
                    sa.set(0, (Integer) SAComboBox.get(0).getSelectedItem());
                }
            }
        });

        DAComboBox.get(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DAComboBox.get(0).getSelectedItem() == sa.get(0)) {
                    debugMessage("source can't be equals destination, select another one.", 0);
                    DAComboBox.get(0).setSelectedItem(da.get(0));
                } else {
                    da.set(0, (Integer) DAComboBox.get(0).getSelectedItem());
                }
            }
        });

        SAComboBox.get(1).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SAComboBox.get(1).getSelectedItem() == da.get(1)) {
                    debugMessage("source can't be equals destination, select another one.", 1);
                    SAComboBox.get(1).setSelectedItem(sa.get(1));
                } else {
                    sa.set(1, (Integer) SAComboBox.get(1).getSelectedItem());
                }
            }
        });

        DAComboBox.get(1).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DAComboBox.get(1).getSelectedItem() == sa.get(1)) {
                    debugMessage("source can't be equals destination, select another one.", 1);
                    DAComboBox.get(1).setSelectedItem(da.get(1));
                } else {
                    da.set(1, (Integer) DAComboBox.get(1).getSelectedItem());
                }
            }
        });

        SAComboBox.get(2).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SAComboBox.get(2).getSelectedItem() == da.get(2)) {
                    debugMessage("source can't be equals destination, select another one.", 2);
                    SAComboBox.get(2).setSelectedItem(sa.get(2));
                } else {
                    sa.set(2, (Integer) SAComboBox.get(2).getSelectedItem());
                }
            }
        });

        DAComboBox.get(2).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DAComboBox.get(2).getSelectedItem() == sa.get(2)) {
                    debugMessage("source can't be equals destination, select another one.", 2);
                    DAComboBox.get(2).setSelectedItem(da.get(2));
                } else {
                    da.set(2, (Integer) DAComboBox.get(2).getSelectedItem());
                }
            }
        });
    }

    private void debugMessage(String message, int index) {
        debugArea.get(index).append("Debug: " + message + "\n");
    }

    class ChannelListener implements PropertyChangeListener {
        private int index;

        public ChannelListener(int index) {
            this.index = index;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("ChannelChanged")) {
                switch (index) {
                    case 0: {
                        new MyThread0().start();
                        break;
                    }
                    case 1: {
                        new MyThread1().start();
                        break;
                    }
                    case 2: {
                        new MyThread2().start();
                        break;
                    }
                }
            }
        }
    }

    private void initChannels() {
        for (int i = 0; i < 3; i++) {
            channels.add(i, new Channel());
            channels.get(i).addPropertyChangeListener(new ChannelListener(i));
        }
    }


    class MyThread0 extends Thread {
        public void run() {
            List<String> frame = channels.get(0).getData();
            channels.get(0).clearData();
            debugMessage("get frame " + frame.toString(), 0);

            if (frame.get(0).equals("T")) {
                token.set(0, true);
                showTokenInDebug(0);
            } else {
                if (sa.get(0).toString().equals(frame.get(2))) {
                    outputArea.get(0).append(frame.get(4));
                    frame.set(3, "C");
                }
            }


            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!inputData.get(0).isEmpty() && token.get(0)) {
                senderIndex = 0;
                sendDataFrame(1, inputData.get(0).remove());
            } else if (inputData.get(0).isEmpty() && token.get(0)) {
                sendToken(1);
            } else {
                if(!(senderIndex == 0)) {
                    channels.get(1).setData(frame);
                }
            }

            if (earlyTokenRelease.get(2).isSelected() && token.get(2)) {
               /* try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                sendToken(0);
            }
        }
    }

    class MyThread1 extends Thread {
        public void run() {
            List<String> frame = channels.get(1).getData();
            channels.get(1).clearData();
            debugMessage("get frame " + frame.toString(), 1);

            if (frame.get(0).equals("T")) {
                token.set(1, true);
                showTokenInDebug(1);
            } else {
                if (sa.get(1).toString().equals(frame.get(2))) {
                    outputArea.get(1).append(frame.get(4));
                    frame.set(3, "C");
                }
            }


            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!inputData.get(1).isEmpty() && token.get(1)) {
                senderIndex = 1;
                sendDataFrame(2, inputData.get(1).remove());
            } else if (inputData.get(1).isEmpty() && token.get(1)) {
                sendToken(2);
            } else {
                if(!(senderIndex == 1)) {
                    channels.get(2).setData(frame);
                }
            }

            if (earlyTokenRelease.get(0).isSelected() && token.get(0)) {
                /*try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                sendToken(1);
            }
        }
    }

    class MyThread2 extends Thread {
        public void run() {
            List<String> frame = channels.get(2).getData();
            channels.get(2).clearData();
            debugMessage("get frame " + frame.toString(), 2);

            if (frame.get(0).equals("T")) {
                token.set(2, true);
                showTokenInDebug(2);
            } else {
                if (sa.get(2).toString().equals(frame.get(2))) {
                    outputArea.get(2).append(frame.get(4));
                    frame.set(3, "C");
                }
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!inputData.get(2).isEmpty() && token.get(2)) {
                senderIndex = 2;
                sendDataFrame(0, inputData.get(2).remove());
            } else if (inputData.get(2).isEmpty() && token.get(2)) {
                sendToken(0);
            } else {
                if(!(senderIndex == 2)) {
                    channels.get(0).setData(frame);
                }
            }

            if (earlyTokenRelease.get(1).isSelected() && token.get(1)) {
                /*try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                sendToken(2);
            }
        }
    }

    private synchronized void sendDataFrame(int index, String data) {
        channels.get(index).setData(formDataFrame(data, index));
    }

    private synchronized void sendToken(int index) {
        if (index == 0) {
            token.set(2, false);
            removeTokenInDebug(2);
        } else {
            token.set(index - 1, false);
            removeTokenInDebug(index - 1);
        }
        channels.get(index).setData(formTokenFrame());
    }

    private List<String> formTokenFrame() {
        List<String> frame = new ArrayList<>(5);
        frame.add("T");
        frame.add(" ");
        frame.add(" ");
        frame.add(" ");
        frame.add(" ");
        return frame;
    }

    private List<String> formDataFrame(String data, int index) {
        List<String> frame = new ArrayList<>(5);
        frame.add("F");
        if (index == 0) {
            frame.add(sa.get(2).toString());
            frame.add(da.get(2).toString());
        } else {
            frame.add(sa.get(index - 1).toString());
            frame.add(da.get(index - 1).toString());
        }
        frame.add(" ");
        frame.add(data);

        return frame;
    }

    private void showTokenInDebug(int index) {
        tokenArea.get(index).setText("T");
    }

    private void removeTokenInDebug(int index) {
        tokenArea.get(index).setText(" ");
    }
}
