package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SimpleCalculatorGUI extends JFrame {
    private JTextField displayField;
    private JTextArea historyArea;
    private double result = 0;
    private String lastOperator = "=";
    private boolean startNewInput = true;
    private ArrayList<String> history;
    private DecimalFormat df;
    
    public SimpleCalculatorGUI() {
        history = new ArrayList<>();
        df = new DecimalFormat("#.##########");
        
        // تنظیمات اصلی فریم
        setTitle("Advanced Calculator");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // ایجاد پنل اصلی
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // پنل نمایش
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayField = new JTextField("0");
        displayField.setFont(new Font("Arial", Font.BOLD, 24));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(false);
        displayPanel.add(displayField, BorderLayout.CENTER);
        
        // پنل دکمه‌ها
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        
        // دکمه‌های ماشین حساب
        String[] buttonLabels = {
            "C", "CE", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ".", "="
        };
        
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(new ButtonClickListener());
            
            // تنظیم رنگ دکمه‌ها
            if (label.matches("[0-9.]")) {
                button.setBackground(new Color(240, 240, 240));
            } else if (label.equals("=")) {
                button.setBackground(new Color(150, 200, 250));
            } else if (label.matches("[+\\-*/%]")) {
                button.setBackground(new Color(220, 220, 220));
            } else {
                button.setBackground(new Color(220, 200, 200));
            }
            
            buttonPanel.add(button);
        }
        
        // پنل تاریخچه
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Calculation History"));
        
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        JButton clearHistoryButton = new JButton("Clear History");
        clearHistoryButton.addActionListener(e -> clearHistory());
        
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        historyPanel.add(clearHistoryButton, BorderLayout.SOUTH);
        
        // پنل عملیات پیشرفته
        JPanel advancedPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        
        JButton sqrtButton = new JButton("√");
        sqrtButton.addActionListener(e -> calculateSqrt());
        
        JButton powerButton = new JButton("x²");
        powerButton.addActionListener(e -> calculatePower());
        
        JButton reciprocalButton = new JButton("1/x");
        reciprocalButton.addActionListener(e -> calculateReciprocal());
        
        JButton sinButton = new JButton("sin");
        sinButton.addActionListener(e -> calculateSin());
        
        advancedPanel.add(sqrtButton);
        advancedPanel.add(powerButton);
        advancedPanel.add(reciprocalButton);
        advancedPanel.add(sinButton);
        
        // اضافه کردن اجزا به پنل اصلی
        mainPanel.add(displayPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(advancedPanel, BorderLayout.SOUTH);
        mainPanel.add(historyPanel, BorderLayout.EAST);
        
        // اضافه کردن پنل اصلی به فریم
        add(mainPanel);
        
        // اضافه کردن رویداد کلید
        addKeyListener();
    }
    
    private void addKeyListener() {
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                if (Character.isDigit(key)) {
                    processDigit(String.valueOf(key));
                } else if (key == '.') {
                    processDecimalPoint();
                } else if ("+-*/".indexOf(key) != -1) {
                    processOperator(String.valueOf(key));
                } else if (key == '=' || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processEquals();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    processCE();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    processC();
                }
            }
        };
        
        // اضافه کردن کلیدهای میانبر به همه اجزا
        addKeyListenerToComponents(this, keyAdapter);
    }
    
    private void addKeyListenerToComponents(Container container, KeyAdapter keyAdapter) {
        container.addKeyListener(keyAdapter);
        for (Component component : container.getComponents()) {
            component.addKeyListener(keyAdapter);
            if (component instanceof Container) {
                addKeyListenerToComponents((Container) component, keyAdapter);
            }
        }
    }
    
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            if (command.matches("[0-9]")) {
                processDigit(command);
            } else if (command.equals(".")) {
                processDecimalPoint();
            } else if (command.matches("[+\\-*/%]")) {
                processOperator(command);
            } else if (command.equals("=")) {
                processEquals();
            } else if (command.equals("C")) {
                processC();
            } else if (command.equals("CE")) {
                processCE();
            } else if (command.equals("±")) {
                processNegate();
            }
        }
    }
    
    private void processDigit(String digit) {
        if (startNewInput) {
            displayField.setText(digit);
            startNewInput = false;
        } else {
            String currentText = displayField.getText();
            if (currentText.equals("0")) {
                displayField.setText(digit);
            } else {
                displayField.setText(currentText + digit);
            }
        }
    }
    
    private void processDecimalPoint() {
        if (startNewInput) {
            displayField.setText("0.");
            startNewInput = false;
        } else {
            String currentText = displayField.getText();
            if (!currentText.contains(".")) {
                displayField.setText(currentText + ".");
            }
        }
    }
    
    private void processOperator(String operator) {
        if (!startNewInput) {
            calculateResult();
        }
        lastOperator = operator;
        startNewInput = true;
    }
    
    private void processEquals() {
        calculateResult();
        lastOperator = "=";
        startNewInput = true;
    }
    
    private void processC() {
        result = 0;
        lastOperator = "=";
        startNewInput = true;
        displayField.setText("0");
    }
    
    private void processCE() {
        displayField.setText("0");
        startNewInput = true;
    }
    
    private void processNegate() {
        String currentText = displayField.getText();
        if (!currentText.equals("0")) {
            if (currentText.startsWith("-")) {
                displayField.setText(currentText.substring(1));
            } else {
                displayField.setText("-" + currentText);
            }
        }
    }
    
    private void calculateResult() {
        double currentValue = Double.parseDouble(displayField.getText());
        String historyEntry = "";
        
        switch (lastOperator) {
            case "+":
                historyEntry = df.format(result) + " + " + df.format(currentValue);
                result += currentValue;
                break;
            case "-":
                historyEntry = df.format(result) + " - " + df.format(currentValue);
                result -= currentValue;
                break;
            case "*":
                historyEntry = df.format(result) + " × " + df.format(currentValue);
                result *= currentValue;
                break;
            case "/":
                if (currentValue != 0) {
                    historyEntry = df.format(result) + " ÷ " + df.format(currentValue);
                    result /= currentValue;
                } else {
                    displayField.setText("Error: Division by zero");
                    startNewInput = true;
                    return;
                }
                break;
            case "%":
                historyEntry = df.format(result) + " % " + df.format(currentValue);
                result %= currentValue;
                break;
            case "=":
                result = currentValue;
                return;
        }
        
        historyEntry += " = " + df.format(result);
        addToHistory(historyEntry);
        displayField.setText(df.format(result));
    }
    
    private void calculateSqrt() {
        double currentValue = Double.parseDouble(displayField.getText());
        if (currentValue >= 0) {
            String historyEntry = "√(" + df.format(currentValue) + ")";
            result = Math.sqrt(currentValue);
            historyEntry += " = " + df.format(result);
            addToHistory(historyEntry);
            displayField.setText(df.format(result));
            startNewInput = true;
        } else {
            displayField.setText("Error: Square root of negative number");
            startNewInput = true;
        }
    }
    
    private void calculatePower() {
        double currentValue = Double.parseDouble(displayField.getText());
        String historyEntry = "(" + df.format(currentValue) + ")²";
        result = Math.pow(currentValue, 2);
        historyEntry += " = " + df.format(result);
        addToHistory(historyEntry);
        displayField.setText(df.format(result));
        startNewInput = true;
    }
    
    private void calculateReciprocal() {
        double currentValue = Double.parseDouble(displayField.getText());
        if (currentValue != 0) {
            String historyEntry = "1/(" + df.format(currentValue) + ")";
            result = 1 / currentValue;
            historyEntry += " = " + df.format(result);
            addToHistory(historyEntry);
            displayField.setText(df.format(result));
            startNewInput = true;
        } else {
            displayField.setText("Error: Division by zero");
            startNewInput = true;
        }
    }
    
    private void calculateSin() {
        double currentValue = Double.parseDouble(displayField.getText());
        String historyEntry = "sin(" + df.format(currentValue) + ")";
        result = Math.sin(Math.toRadians(currentValue));
        historyEntry += " = " + df.format(result);
        addToHistory(historyEntry);
        displayField.setText(df.format(result));
        startNewInput = true;
    }
    
    private void addToHistory(String entry) {
        history.add(entry);
        updateHistoryArea();
    }
    
    private void updateHistoryArea() {
        StringBuilder sb = new StringBuilder();
        for (String entry : history) {
            sb.append(entry).append("\n");
        }
        historyArea.setText(sb.toString());
        // اسکرول به پایین
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }
    
    private void clearHistory() {
        history.clear();
        historyArea.setText("");
    }
    
    public static void main(String[] args) {
        try {
            // تنظیم ظاهر برنامه مطابق با سیستم عامل
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            SimpleCalculatorGUI app = new SimpleCalculatorGUI();
            app.setVisible(true);
        });
    }
}