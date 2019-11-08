package ui;

import service.CSMAService;
import service.CSMAServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 460;
    private static final Font textFont = new Font("Text font", Font.ITALIC, 15);
    private static final Font headerFont = new Font("Header font", Font.BOLD, 20);
    private static String currentInputText = "";
    CSMAService csmaService = new CSMAServiceImpl();
    private static List<String> dataBytes = new ArrayList<String>();
    private static String channel= "";

    private static JPanel rootPanel;
    private static JPanel inputPanel;
    private static JPanel outputPanel;
    private static JPanel debugPanel;
    private static JTextArea inputArea;
    private static JTextArea outputArea;
    private static JTextArea debugArea;
    private static JCheckBox burstMode;

    public void init() {
        JFrame frame = new JFrame("Lab 4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());

        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());

        debugPanel = new JPanel();
        debugPanel.setLayout(new BorderLayout());

        /**
         * Input panel
         */
        inputArea = new JTextArea(5, 60);
        JScrollPane sp1 = new JScrollPane(inputArea);
        inputArea.setLineWrap(true);
        inputArea.setFont(textFont);
        JLabel inputHeader = new JLabel("Input (do not hold down the key, the last character is transmitted)");
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
        debugArea = new JTextArea(5, 60);
        JScrollPane sp2 = new JScrollPane(debugArea);
        debugArea.setFont(textFont);
        debugArea.setLineWrap(true);
        debugArea.setEditable(false);
        JLabel debugHeader = new JLabel("Control & debug");
        debugHeader.setFont(headerFont);
        debugPanel.add(debugHeader, BorderLayout.NORTH);
        debugPanel.add(sp2);
        burstMode = new JCheckBox("Burst mode");
        debugPanel.add(burstMode, BorderLayout.SOUTH);


        rootPanel.add(inputPanel, BorderLayout.NORTH);
        rootPanel.add(outputPanel, BorderLayout.CENTER);
        rootPanel.add(debugPanel, BorderLayout.SOUTH);


        frame.add(rootPanel);
        frame.setResizable(false);
        frame.setVisible(true);


        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String newText = inputArea.getText();
                if (!currentInputText.equals(newText)) {
                    String symbol = newText.substring(newText.length() - 1);
                    currentInputText = currentInputText + symbol;
                    inputArea.setText(currentInputText);
                    if (!burstMode.isSelected()) {
                        String text = send(symbol);
                        if (!text.equals("mistake")) {
                            outputArea.append(text);
                        }
                    } else {
                        dataBytes.add(symbol);
                        if (dataBytes.size() == 4) {
                            String res = send(dataBytes.get(0));
                            if (!res.equals("mistake")) {
                                outputArea.append(res);
                                dataBytes.remove(0);
                                outputArea.append(concatCollection(dataBytes));
                            }
                            dataBytes.clear();
                        }
                    }
                }
            }
        });
    }

    private String concatCollection(List<String> collection) {
        String result = "";
        for (String elem : collection) {
            result += elem;
        }
        return result;
    }

    private String send(String text) {
        inputArea.setEditable(false);

        try {
            debugArea.append(text);
            for (int numberOfAttempts = 0; ; numberOfAttempts++) {
                csmaService.waitChannelFree();
                channel = text;
                csmaService.wait(CSMAService.COLLISION_WINDOW);
                if (csmaService.isCollision()) {
                    debugArea.append("X");
                    csmaService.wait(csmaService.calculateDelay(numberOfAttempts));
                } else {
                    break;
                }

                if (numberOfAttempts == 9) {
                    debugArea.append(" TOO MUCH ATTEMPTS");
                    text = "mistake";
                    break;
                }
            }
            debugArea.append("\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inputArea.setEditable(true);
        if(text.equals("mistake")) {
            channel = "";
            return text;
        }
        else {
            return channel;
        }
    }
}
