package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NumberGuessingGameGUI extends JFrame {
    private JTextField guessField;
    private JLabel messageLabel, attemptsLabel, rangeLabel;
    private JButton guessButton, newGameButton, settingsButton;
    private JProgressBar hintBar;
    
    private int numberToGuess;
    private int minRange = 1;
    private int maxRange = 100;
    private int attempts = 0;
    private int maxAttempts = 10;
    private boolean gameOver = false;
    
    public NumberGuessingGameGUI() {
        // تنظیمات اصلی فریم
        setTitle("بازی حدس عدد");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // ایجاد پنل اصلی
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // پنل بالایی - عنوان و اطلاعات
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("بازی حدس عدد");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        rangeLabel = new JLabel(String.format("محدوده اعداد: %d تا %d", minRange, maxRange));
        rangeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(rangeLabel, BorderLayout.SOUTH);
        
        // پنل میانی - ورودی و پیام
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(new JLabel("حدس شما: "));
        guessField = new JTextField(10);
        inputPanel.add(guessField);
        
        messageLabel = new JLabel("یک عدد را حدس بزنید!");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        
        attemptsLabel = new JLabel(String.format("تعداد حدس‌ها: %d / %d", attempts, maxAttempts));
        attemptsLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // نوار راهنما
        hintBar = new JProgressBar(0, maxRange - minRange);
        hintBar.setStringPainted(true);
        hintBar.setString("راهنمای بصری");
        
        centerPanel.add(inputPanel);
        centerPanel.add(messageLabel);
        centerPanel.add(attemptsLabel);
        centerPanel.add(hintBar);
        
        // پنل پایینی - دکمه‌ها
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        guessButton = new JButton("حدس بزن");
        guessButton.addActionListener(e -> checkGuess());
        
        newGameButton = new JButton("بازی جدید");
        newGameButton.addActionListener(e -> startNewGame());
        
        settingsButton = new JButton("تنظیمات");
        settingsButton.addActionListener(e -> showSettings());
        
        buttonPanel.add(guessButton);
        buttonPanel.add(newGameButton);
        buttonPanel.add(settingsButton);
        
        // اضافه کردن پنل‌ها به پنل اصلی
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // اضافه کردن پنل اصلی به فریم
        add(mainPanel);
        
        // شروع بازی جدید
        startNewGame();
        
        // اضافه کردن رویداد کلید Enter برای فیلد حدس
        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkGuess();
                }
            }
        });
    }
    
    private void startNewGame() {
        // تولید عدد تصادفی جدید
        Random random = new Random();
        numberToGuess = random.nextInt(maxRange - minRange + 1) + minRange;
        
        // بازنشانی متغیرها
        attempts = 0;
        gameOver = false;
        
        // بروزرسانی رابط کاربری
        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        messageLabel.setText("یک عدد را حدس بزنید!");
        messageLabel.setForeground(Color.BLACK);
        attemptsLabel.setText(String.format("تعداد حدس‌ها: %d / %d", attempts, maxAttempts));
        rangeLabel.setText(String.format("محدوده اعداد: %d تا %d", minRange, maxRange));
        
        // بازنشانی نوار راهنما
        hintBar.setMinimum(minRange);
        hintBar.setMaximum(maxRange);
        hintBar.setValue(minRange);
        
        // تمرکز روی فیلد حدس
        guessField.requestFocus();
    }
    
    private void checkGuess() {
        if (gameOver) {
            return;
        }
        
        try {
            int guess = Integer.parseInt(guessField.getText());
            attempts++;
            
            // بررسی محدوده عدد
            if (guess < minRange || guess > maxRange) {
                messageLabel.setText(String.format("لطفاً عددی بین %d تا %d وارد کنید!", minRange, maxRange));
                messageLabel.setForeground(Color.RED);
                attempts--; // این حدس را حساب نمی‌کنیم
                return;
            }
            
            // بروزرسانی تعداد حدس‌ها
            attemptsLabel.setText(String.format("تعداد حدس‌ها: %d / %d", attempts, maxAttempts));
            
            // بررسی حدس
            if (guess < numberToGuess) {
                messageLabel.setText("بالاتر!");
                messageLabel.setForeground(new Color(255, 140, 0)); // نارنجی
                hintBar.setValue(Math.max(guess, hintBar.getValue()));
            } else if (guess > numberToGuess) {
                messageLabel.setText("پایین‌تر!");
                messageLabel.setForeground(new Color(255, 140, 0)); // نارنجی
                hintBar.setValue(Math.min(guess, hintBar.getValue()));
            } else {
                // حدس درست
                messageLabel.setText("تبریک! شما برنده شدید!");
                messageLabel.setForeground(new Color(0, 128, 0)); // سبز
                gameOver = true;
                guessButton.setEnabled(false);
                hintBar.setValue(numberToGuess);
                showGameOverDialog(true);
                return;
            }
            
            // بررسی اتمام تعداد حدس‌ها
            if (attempts >= maxAttempts) {
                messageLabel.setText(String.format("متأسفانه باختید! عدد صحیح %d بود.", numberToGuess));
                messageLabel.setForeground(Color.RED);
                gameOver = true;
                guessButton.setEnabled(false);
                hintBar.setValue(numberToGuess);
                showGameOverDialog(false);
            }
            
            // پاک کردن فیلد حدس و تمرکز مجدد
            guessField.setText("");
            guessField.requestFocus();
            
        } catch (NumberFormatException e) {
            messageLabel.setText("لطفاً یک عدد معتبر وارد کنید!");
            messageLabel.setForeground(Color.RED);
        }
    }
    
    private void showSettings() {
        // ایجاد دیالوگ تنظیمات
        JDialog settingsDialog = new JDialog(this, "تنظیمات بازی", true);
        settingsDialog.setSize(300, 200);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new GridLayout(4, 2, 10, 10));
        settingsDialog.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // فیلدهای تنظیمات
        JTextField minField = new JTextField(String.valueOf(minRange));
        JTextField maxField = new JTextField(String.valueOf(maxRange));
        JTextField attemptsField = new JTextField(String.valueOf(maxAttempts));
        
        // افزودن اجزا به دیالوگ
        settingsDialog.add(new JLabel("حداقل محدوده:"));
        settingsDialog.add(minField);
        settingsDialog.add(new JLabel("حداکثر محدوده:"));
        settingsDialog.add(maxField);
        settingsDialog.add(new JLabel("حداکثر تعداد حدس‌ها:"));
        settingsDialog.add(attemptsField);
        
        // دکمه‌های تأیید و لغو
        JButton saveButton = new JButton("ذخیره");
        JButton cancelButton = new JButton("لغو");
        
        saveButton.addActionListener(e -> {
            try {
                int newMin = Integer.parseInt(minField.getText());
                int newMax = Integer.parseInt(maxField.getText());
                int newAttempts = Integer.parseInt(attemptsField.getText());
                
                if (newMin >= newMax) {
                    JOptionPane.showMessageDialog(settingsDialog, 
                        "حداقل محدوده باید کمتر از حداکثر محدوده باشد", 
                        "خطا", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (newAttempts <= 0) {
                    JOptionPane.showMessageDialog(settingsDialog, 
                        "تعداد حدس‌ها باید بیشتر از صفر باشد", 
                        "خطا", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // اعمال تنظیمات جدید
                minRange = newMin;
                maxRange = newMax;
                maxAttempts = newAttempts;
                
                // شروع بازی جدید با تنظیمات جدید
                startNewGame();
                
                // بستن دیالوگ
                settingsDialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsDialog, 
                    "لطفاً مقادیر عددی معتبر وارد کنید", 
                    "خطا", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> settingsDialog.dispose());
        
        settingsDialog.add(saveButton);
        settingsDialog.add(cancelButton);
        
        // نمایش دیالوگ
        settingsDialog.setVisible(true);
    }
    
    private void showGameOverDialog(boolean won) {
        String message;
        String title;
        int messageType;
        
        if (won) {
            title = "تبریک!";
            message = String.format("شما برنده شدید! عدد صحیح %d بود.\nتعداد حدس‌ها: %d", numberToGuess, attempts);
            messageType = JOptionPane.INFORMATION_MESSAGE;
        } else {
            title = "باختید!";
            message = String.format("متأسفانه باختید! عدد صحیح %d بود.", numberToGuess);
            messageType = JOptionPane.ERROR_MESSAGE;
        }
        
        // نمایش دیالوگ با گزینه‌های بازی جدید یا خروج
        Object[] options = {"بازی جدید", "خروج"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            messageType,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // شروع بازی جدید
            startNewGame();
        } else {
            // خروج از برنامه
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        try {
            // تنظیم ظاهر برنامه مطابق با سیستم عامل
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            NumberGuessingGameGUI app = new NumberGuessingGameGUI();
            app.setVisible(true);
        });
    }
}