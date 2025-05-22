package com.example.demo;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateCalculatorGUI extends JFrame {
    private JFormattedTextField startDateField, endDateField;
    private JLabel resultLabel;
    private JComboBox<String> calculationTypeCombo;
    private JPanel resultPanel;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public DateCalculatorGUI() {
        // تنظیمات اصلی فریم
        setTitle("محاسبه‌گر تاریخ پیشرفته");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // ایجاد پنل اصلی
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // پنل ورودی تاریخ‌ها
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder("ورود تاریخ‌ها"));
        
        // فرمت‌دهنده تاریخ
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        
        // ایجاد فیلدهای تاریخ
        try {
            DateFormatter formatter = new DateFormatter(sdf);
            
            startDateField = new JFormattedTextField(formatter);
            startDateField.setValue(new Date());
            startDateField.setColumns(10);
            
            endDateField = new JFormattedTextField(formatter);
            endDateField.setValue(new Date());
            endDateField.setColumns(10);
            
        } catch (Exception e) {
            startDateField = new JFormattedTextField();
            startDateField.setText(LocalDate.now().format(dateFormatter));
            startDateField.setColumns(10);
            
            endDateField = new JFormattedTextField();
            endDateField.setText(LocalDate.now().format(dateFormatter));
            endDateField.setColumns(10);
        }
        
        // افزودن راهنمای متنی
        startDateField.setToolTipText("تاریخ را به فرمت YYYY-MM-DD وارد کنید");
        endDateField.setToolTipText("تاریخ را به فرمت YYYY-MM-DD وارد کنید");
        
        // نوع محاسبه
        String[] calculationTypes = {
            "تعداد روزها", 
            "تعداد ماه‌ها", 
            "تعداد سال‌ها", 
            "محاسبه کامل (سال، ماه، روز)"
        };
        calculationTypeCombo = new JComboBox<>(calculationTypes);
        
        // افزودن اجزا به پنل ورودی
        inputPanel.add(new JLabel("تاریخ شروع:"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("تاریخ پایان:"));
        inputPanel.add(endDateField);
        inputPanel.add(new JLabel("نوع محاسبه:"));
        inputPanel.add(calculationTypeCombo);
        
        // پنل دکمه‌ها
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton calculateButton = new JButton("محاسبه");
        calculateButton.addActionListener(e -> calculateDateDifference());
        
        JButton swapButton = new JButton("جابجایی تاریخ‌ها");
        swapButton.addActionListener(e -> swapDates());
        
        JButton clearButton = new JButton("پاک کردن");
        clearButton.addActionListener(e -> clearFields());
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(clearButton);
        
        // پنل نتیجه
        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("نتیجه محاسبه"));
        
        resultLabel = new JLabel("لطفاً تاریخ‌ها را وارد کرده و دکمه محاسبه را بزنید");
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        resultLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        
        // پنل اطلاعات اضافی
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("اطلاعات تاریخ امروز"));
        
        JLabel todayInfoLabel = new JLabel(getTodayInfo());
        todayInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        infoPanel.add(todayInfoLabel, BorderLayout.CENTER);
        
        // اضافه کردن اجزا به پنل اصلی
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        southPanel.add(resultPanel);
        southPanel.add(infoPanel);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        // اضافه کردن پنل اصلی به فریم
        add(mainPanel);
    }
    
    private void calculateDateDifference() {
        try {
            // دریافت تاریخ‌ها از فیلدها
            LocalDate startDate = parseDate(startDateField.getText());
            LocalDate endDate = parseDate(endDateField.getText());
            
            // بررسی معتبر بودن تاریخ‌ها
            if (startDate == null || endDate == null) {
                showError("لطفاً تاریخ‌ها را به فرمت صحیح (YYYY-MM-DD) وارد کنید");
                return;
            }
            
            // انتخاب نوع محاسبه
            int selectedType = calculationTypeCombo.getSelectedIndex();
            String result = "";
            
            switch (selectedType) {
                case 0: // تعداد روزها
                    long days = ChronoUnit.DAYS.between(startDate, endDate);
                    result = String.format("تعداد روزها: %d روز", days);
                    break;
                    
                case 1: // تعداد ماه‌ها
                    long months = ChronoUnit.MONTHS.between(startDate, endDate);
                    result = String.format("تعداد ماه‌ها: %d ماه", months);
                    break;
                    
                case 2: // تعداد سال‌ها
                    long years = ChronoUnit.YEARS.between(startDate, endDate);
                    result = String.format("تعداد سال‌ها: %d سال", years);
                    break;
                    
                case 3: // محاسبه کامل
                    Period period = Period.between(startDate, endDate);
                    result = String.format("فاصله زمانی: %d سال، %d ماه و %d روز", 
                                          period.getYears(), period.getMonths(), period.getDays());
                    break;
            }
            
            // نمایش نتیجه
            resultLabel.setText(result);
            resultLabel.setForeground(Color.BLUE);
            
        } catch (Exception e) {
            showError("خطا در محاسبه: " + e.getMessage());
        }
    }
    
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    private void swapDates() {
        String temp = startDateField.getText();
        startDateField.setText(endDateField.getText());
        endDateField.setText(temp);
    }
    
    private void clearFields() {
        startDateField.setText(LocalDate.now().format(dateFormatter));
        endDateField.setText(LocalDate.now().format(dateFormatter));
        resultLabel.setText("لطفاً تاریخ‌ها را وارد کرده و دکمه محاسبه را بزنید");
        resultLabel.setForeground(Color.BLACK);
    }
    
    private void showError(String message) {
        resultLabel.setText(message);
        resultLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, message, "خطا", JOptionPane.ERROR_MESSAGE);
    }
    
    private String getTodayInfo() {
        LocalDate today = LocalDate.now();
        return String.format("امروز: %s", today.format(dateFormatter));
    }
    
    public static void main(String[] args) {
        try {
            // تنظیم ظاهر برنامه مطابق با سیستم عامل
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            DateCalculatorGUI app = new DateCalculatorGUI();
            app.setVisible(true);
        });
    }
}