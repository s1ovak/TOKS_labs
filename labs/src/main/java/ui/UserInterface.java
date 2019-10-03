package ui;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;
import service.SerialPortService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UserInterface {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 620;
    private static final Font textFont = new Font("Text font", Font.ITALIC, 15);
    private static final Font headerFont = new Font("Header font", Font.BOLD, 20);

    private static JTextArea outputArea;
    private static JLabel debugLabel;
    private static JPanel rootPanel;
    private static JPanel inputPanel;
    private static JPanel outputPanel;
    private static JPanel debugPanel;
    private static JTextArea inputArea;
    private static JComboBox<String> speeds;
    private static JComboBox<String> ports;
    private static JComboBox<String> parities;
    private static JComboBox<String> dataBits;
    private static JComboBox<String> stopBits;
    private static SerialPortService serialPortService;
    private static String currentInputText = "";
    private static JButton connectButton;
    private static JButton disconnectButton;
    private static JButton clearOutput;

    public void init() {
        JFrame frame = new JFrame("Lab 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());

        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        debugPanel = new JPanel();
        debugPanel.setLayout(new GridLayout(12, 1));

        outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());

        /**
         * Input panel
         */
        inputArea = new JTextArea(5, 60);
        JScrollPane sp1 = new JScrollPane(inputArea);
        inputArea.setLineWrap(true);
        inputArea.setFont(textFont);

        JLabel inputHeader = new JLabel("Input");
        inputHeader.setFont(headerFont);
        inputPanel.add(inputHeader, BorderLayout.NORTH);
        inputPanel.add(sp1);

        /**
         * Output panel
         */
        outputArea = new JTextArea(5, 60);
        JScrollPane sp = new JScrollPane(outputArea);
        outputArea.setFont(textFont);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        JLabel outputHeader = new JLabel("Output");
        outputHeader.setFont(headerFont);
        outputPanel.add(outputHeader, BorderLayout.NORTH);
        outputPanel.add(sp);


        /**
         * Debug panel
         */
        debugLabel = new JLabel("Debug: ");
        debugLabel.setFont(textFont);
        JLabel debugHeader = new JLabel("Debug & control");
        JPanel debugHeaderPanel = new JPanel();
        debugHeaderPanel.setLayout(new BorderLayout());
        clearOutput = new JButton("clear");
        debugHeader.add(clearOutput, BorderLayout.EAST);
        debugHeader.setFont(headerFont);
        debugHeaderPanel.add(debugHeader, BorderLayout.WEST);
        debugHeaderPanel.add(clearOutput, BorderLayout.EAST);
        debugPanel.add(debugHeaderPanel);
        debugPanel.add(debugLabel);

        JPanel portsPanel = new JPanel();
        portsPanel.setLayout(new GridLayout(1, 4));
        JLabel portsHeader = new JLabel("Ports:");
        portsHeader.setFont(textFont);
        debugPanel.add(portsHeader);
        ports = new JComboBox<String>(SerialPortList.getPortNames());
        portsPanel.add(ports);
        connectButton = new JButton("connect");
        portsPanel.add(connectButton);
        disconnectButton = new JButton("disconnect");
        portsPanel.add(disconnectButton);
        disconnectButton.setEnabled(false);
        JButton refreshButton = new JButton("refresh");
        portsPanel.add(refreshButton);
        debugPanel.add(portsPanel);

        JLabel dataBitsHeader = new JLabel("Data bits:");
        dataBitsHeader.setFont(textFont);
        debugPanel.add(dataBitsHeader);
        String[] dataBitsNames = {"5", "6", "7", "8"};
        dataBits = new JComboBox<String>(dataBitsNames);
        debugPanel.add(dataBits);

        JLabel speedHeader = new JLabel("Speed: ");
        speedHeader.setFont(textFont);
        debugPanel.add(speedHeader);
        String[] bounds = {"110", "300", "600", "1200", "4800", "9600", "14400", "19200", "38400", "57600", "115200"};
        speeds = new JComboBox<String>(bounds);
        debugPanel.add(speeds);

        JLabel parityHeader = new JLabel("Parity:");
        parityHeader.setFont(textFont);
        debugPanel.add(parityHeader);
        String[] parityNames = {"EVEN", "MARK", "NONE", "ODD", "SPACE"};
        parities = new JComboBox<String>(parityNames);
        debugPanel.add(parities);

        JLabel stopBitsHeader = new JLabel("Stop bits:");
        stopBitsHeader.setFont(textFont);
        debugPanel.add(stopBitsHeader);
        String[] stopBitsNames = {"1", "1.5", "2"};
        stopBits = new JComboBox<String>(stopBitsNames);
        debugPanel.add(stopBits);

        rootPanel.add(inputPanel, BorderLayout.NORTH);
        rootPanel.add(outputPanel, BorderLayout.CENTER);
        rootPanel.add(debugPanel, BorderLayout.SOUTH);

        frame.add(rootPanel);
        frame.setResizable(false);
        frame.setVisible(true);


        /**
         * Listeners
         */
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectAction();
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disconnectAction();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshAction();
            }
        });

        clearOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputArea.setText("");
            }
        });

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newText = inputArea.getText();
                if (!currentInputText.equals(newText) && serialPortService != null) {
                    currentInputText = newText;
                    char value = newText.charAt(newText.length() - 1);
                    if(value < 'А' || value > 'я') {
                        serialPortService.write((byte) value);
                        debugLabel.setText("Debug: send '" + value + "'");
                    }
                }
            }
        });
    }

    private static class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.isERR()) {
                debugLabel.setText("Debug: incompatible port settings, to fix reconnect to ports with the same params.");
                return;
            }

            if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
                String value = new String(serialPortService.read());
                outputArea.setText(outputArea.getText() + value);
                debugLabel.setText("Debug: get symbol '" + value + "'");
            }
        }
    }

    private void connectAction() {
        if (serialPortService != null) {
            serialPortService.close();
        }

        try {
            serialPortService = new SerialPortService(ports.getSelectedItem().toString());
        } catch (NullPointerException e) {
            return;
        }

        boolean isOpened = serialPortService.open();

        int dataBits = getDataBits();
        int speed = getSpeed();
        int parity = getParity();
        int stopBits = getStopBits();

        if ((dataBits == 5 && stopBits == 2) || (dataBits != 5 && stopBits == SerialPort.STOPBITS_1_5)) {
            debugMessage("incorrect params, can't connect.");
            serialPortService.close();
            return;
        }

        serialPortService.setParams(dataBits, speed, parity, stopBits);

        serialPortService.setFlowControl(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
        serialPortService.addListener(new PortReader());

        if (isOpened) {
            debugMessage("successfully connected.");
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
        } else {
            debugMessage("can't connect.");
        }
    }

    private void disconnectAction() {
        if (serialPortService != null) {
            debugMessage("disconnected from " + serialPortService.getPort().getPortName());
            serialPortService.close();
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
        }

    }

    private void refreshAction() {
        String[] portsNames = SerialPortList.getPortNames();
        ports.removeAllItems();
        for (String port : portsNames) {
            ports.addItem(port);
        }
        debugMessage("refreshed ports list.");
    }


    private void debugMessage(String message) {
        if (debugLabel != null) {
            debugLabel.setText("Debug: " + message);
        }
    }

    private int getDataBits() {
        try {
            return Integer.parseInt(dataBits.getSelectedItem().toString());
        } catch (NullPointerException e) {
            return -1;
        }
    }

    private int getSpeed() {
        try {
            return Integer.parseInt(speeds.getSelectedItem().toString());
        } catch (NullPointerException e) {
            return -1;
        }
    }

    private int getParity() {
        try {
            int value;
            String s = parities.getSelectedItem().toString();
            if (s.equals("EVEN")) {
                value = SerialPort.PARITY_EVEN;
            }
            if (s.equals("MARK")) {
                value = SerialPort.PARITY_MARK;
            }
            if (s.equals("NONE")) {
                value = SerialPort.PARITY_NONE;
            }
            if (s.equals("ODD")) {
                value = SerialPort.PARITY_ODD;
            } else {
                value = SerialPort.PARITY_SPACE;
            }
            return value;
        } catch (NullPointerException e) {
            return -1;
        }
    }

    private int getStopBits() {
        try {
            int value;
            String s = stopBits.getSelectedItem().toString();
            if (s.equals("1")) {
                value = SerialPort.STOPBITS_1;
            } else if (s.equals("1.5")) {
                value = SerialPort.STOPBITS_1_5;
            } else {
                value = SerialPort.STOPBITS_2;
            }
            return value;
        } catch (NullPointerException e) {
            return -1;
        }
    }
}
