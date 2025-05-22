package com.example.demo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PhoneBookGUI extends JFrame {
    private JTextField nameField, phoneField, searchField;
    private JTable contactsTable;
    private DefaultTableModel tableModel;
    private HashMap<String, String> phoneBook;
    private JButton addButton, updateButton, deleteButton, clearButton, searchButton;
    
    public PhoneBookGUI() {
        phoneBook = new HashMap<>();
        
        // تنظیمات اصلی فریم
        setTitle("دفترچه تلفن پیشرفته");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // ایجاد پنل اصلی
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // پنل ورودی اطلاعات
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("افزودن/ویرایش مخاطب"));
        
        inputPanel.add(new JLabel("نام:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        
        inputPanel.add(new JLabel("شماره تلفن:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);
        
        // پنل دکمه‌ها
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        addButton = new JButton("افزودن");
        addButton.addActionListener(e -> addContact());
        
        updateButton = new JButton("بروزرسانی");
        updateButton.addActionListener(e -> updateContact());
        
        deleteButton = new JButton("حذف");
        deleteButton.addActionListener(e -> deleteContact());
        
        clearButton = new JButton("پاک کردن فرم");
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        // پنل جستجو
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createTitledBorder("جستجوی مخاطب"));
        
        searchField = new JTextField();
        searchButton = new JButton("جستجو");
        searchButton.addActionListener(e -> searchContact());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // ترکیب پنل‌های ورودی، دکمه‌ها و جستجو
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        northPanel.add(topPanel, BorderLayout.CENTER);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        
        // ایجاد جدول مخاطبین
        String[] columns = {"نام", "شماره تلفن"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // غیرقابل ویرایش کردن سلول‌های جدول
            }
        };
        
        contactsTable = new JTable(tableModel);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.getTableHeader().setReorderingAllowed(false);
        contactsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && contactsTable.getSelectedRow() != -1) {
                displayContact();
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(contactsTable);
        
        // پنل دکمه‌های فایل
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filePanel.setBorder(BorderFactory.createTitledBorder("عملیات فایل"));
        
        JButton saveButton = new JButton("ذخیره در فایل");
        saveButton.addActionListener(e -> saveToFile());
        
        JButton loadButton = new JButton("بارگذاری از فایل");
        loadButton.addActionListener(e -> loadFromFile());
        
        filePanel.add(saveButton);
        filePanel.add(loadButton);
        
        // اضافه کردن اجزا به پنل اصلی
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(filePanel, BorderLayout.SOUTH);
        
        // اضافه کردن پنل اصلی به فریم
        add(mainPanel);
    }
    
    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "لطفاً نام و شماره تلفن را وارد کنید", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (phoneBook.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "این نام قبلاً در دفترچه تلفن وجود دارد", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        phoneBook.put(name, phone);
        tableModel.addRow(new Object[]{name, phone});
        clearForm();
        JOptionPane.showMessageDialog(this, "مخاطب با موفقیت اضافه شد", "افزودن مخاطب", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "لطفاً یک مخاطب را انتخاب کنید", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String oldName = (String) tableModel.getValueAt(selectedRow, 0);
        String newName = nameField.getText().trim();
        String newPhone = phoneField.getText().trim();
        
        if (newName.isEmpty() || newPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "لطفاً نام و شماره تلفن را وارد کنید", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!oldName.equals(newName) && phoneBook.containsKey(newName)) {
            JOptionPane.showMessageDialog(this, "این نام قبلاً در دفترچه تلفن وجود دارد", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // حذف مخاطب قدیمی
        phoneBook.remove(oldName);
        
        // افزودن مخاطب جدید
        phoneBook.put(newName, newPhone);
        
        // بروزرسانی جدول
        tableModel.setValueAt(newName, selectedRow, 0);
        tableModel.setValueAt(newPhone, selectedRow, 1);
        
        clearForm();
        JOptionPane.showMessageDialog(this, "مخاطب با موفقیت بروزرسانی شد", "بروزرسانی مخاطب", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "لطفاً یک مخاطب را انتخاب کنید", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "آیا از حذف این مخاطب اطمینان دارید؟", 
            "تأیید حذف", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            phoneBook.remove(name);
            tableModel.removeRow(selectedRow);
            clearForm();
            JOptionPane.showMessageDialog(this, "مخاطب با موفقیت حذف شد", "حذف مخاطب", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void displayContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            String phone = (String) tableModel.getValueAt(selectedRow, 1);
            
            nameField.setText(name);
            phoneField.setText(phone);
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        contactsTable.clearSelection();
    }
    
    private void searchContact() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            refreshTable(); // نمایش همه مخاطبین
            return;
        }
        
        // پاک کردن جدول
        tableModel.setRowCount(0);
        
        // جستجو و نمایش نتایج
        for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
            String name = entry.getKey();
            String phone = entry.getValue();
            
            if (name.toLowerCase().contains(searchTerm) || phone.contains(searchTerm)) {
                tableModel.addRow(new Object[]{name, phone});
            }
        }
        
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "هیچ مخاطبی یافت نشد", "جستجو", JOptionPane.INFORMATION_MESSAGE);
            refreshTable(); // نمایش همه مخاطبین
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }
    
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("ذخیره دفترچه تلفن");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : phoneBook.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "دفترچه تلفن با موفقیت ذخیره شد", "ذخیره", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "خطا در ذخیره فایل: " + e.getMessage(), "خطا", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("بارگذاری دفترچه تلفن");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                phoneBook.clear();
                tableModel.setRowCount(0);
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        String phone = parts[1].trim();
                        phoneBook.put(name, phone);
                        tableModel.addRow(new Object[]{name, phone});
                    }
                }
                
                JOptionPane.showMessageDialog(this, "دفترچه تلفن با موفقیت بارگذاری شد", "بارگذاری", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "خطا در بارگذاری فایل: " + e.getMessage(), "خطا", JOptionPane.ERROR_MESSAGE);
            }
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
            PhoneBookGUI app = new PhoneBookGUI();
            app.setVisible(true);
        });
    }
}