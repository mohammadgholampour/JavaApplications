package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ToDoListGUI extends JFrame {
    private JTextField taskField;
    private JComboBox<String> priorityCombo, categoryCombo;
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private JDateChooser dueDateChooser;
    private ArrayList<Task> tasks;
    
    public ToDoListGUI() {
        tasks = new ArrayList<>();
        
        // Main frame settings
        setTitle("To-Do List Manager - مدیریت کارها");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel - Data entry
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("افزودن کار جدید"));
        
        // Task title
        inputPanel.add(new JLabel("عنوان کار:"));
        taskField = new JTextField();
        inputPanel.add(taskField);
        
        // Priority
        inputPanel.add(new JLabel("اولویت:"));
        String[] priorities = {"High", "Medium", "Low"};
        priorityCombo = new JComboBox<>(priorities);
        inputPanel.add(priorityCombo);
        
        // Category
        inputPanel.add(new JLabel("دسته‌بندی:"));
        String[] categories = {"Work", "Personal", "Study", "Shopping", "Other"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setEditable(true);
        inputPanel.add(categoryCombo);
        
        // Due date
        inputPanel.add(new JLabel("تاریخ انجام:"));
        dueDateChooser = new JDateChooser();
        dueDateChooser.setDateFormatString("yyyy-MM-dd");
        inputPanel.add(dueDateChooser);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton addButton = new JButton("افزودن کار");
        addButton.addActionListener(e -> addTask());
        
        JButton updateButton = new JButton("بروزرسانی");
        updateButton.addActionListener(e -> updateTask());
        
        JButton deleteButton = new JButton("حذف کار");
        deleteButton.addActionListener(e -> deleteTask());
        
        JButton clearButton = new JButton("پاک کردن فرم");
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBorder(BorderFactory.createTitledBorder("جستجو و فیلتر"));
        
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("جستجو");
        JButton clearSearchButton = new JButton("پاک کردن جستجو");
        
        // Sort panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JComboBox<String> sortCombo = new JComboBox<>(new String[]{"تاریخ", "اولویت", "دسته‌بندی", "عنوان"});
        JButton sortButton = new JButton("مرتب‌سازی");
        
        searchButton.addActionListener(e -> searchTasks(searchField.getText()));
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });
        
        sortButton.addActionListener(e -> sortTasks((String) sortCombo.getSelectedItem()));
        
        searchPanel.add(new JLabel("جستجو:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        
        sortPanel.add(new JLabel("مرتب‌سازی بر اساس:"));
        sortPanel.add(sortCombo);
        sortPanel.add(sortButton);
        
        JPanel filterSortPanel = new JPanel(new BorderLayout());
        filterSortPanel.add(searchPanel, BorderLayout.WEST);
        filterSortPanel.add(sortPanel, BorderLayout.EAST);
        
        // Combine top panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(filterSortPanel, BorderLayout.SOUTH);
        
        // Create tasks table
        String[] columns = {"عنوان", "اولویت", "دسته‌بندی", "تاریخ انجام", "وضعیت"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only status column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) {
                    return Boolean.class; // Status column as checkbox
                }
                return String.class;
            }
        };
        
        tasksTable = new JTable(tableModel);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksTable.getTableHeader().setReorderingAllowed(false);
        tasksTable.setRowHeight(25);
        
        // Add listener for checkbox changes
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 4) { // Status column
                int row = e.getFirstRow();
                boolean completed = (Boolean) tableModel.getValueAt(row, 4);
                
                // Find the actual task index
                int taskIndex = findTaskIndex(row);
                if (taskIndex != -1) {
                    tasks.get(taskIndex).setCompleted(completed);
                    tasksTable.repaint(); // Refresh colors
                }
            }
        });
        
        tasksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tasksTable.getSelectedRow() != -1) {
                displayTask();
            }
        });
        
        // Change row colors based on priority
        tasksTable.setDefaultRenderer(Object.class, new TaskTableCellRenderer());
        
        JScrollPane tableScrollPane = new JScrollPane(tasksTable);
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("آمار کارها"));
        
        JLabel totalLabel = new JLabel("کل: 0");
        JLabel completedLabel = new JLabel("انجام شده: 0");
        JLabel pendingLabel = new JLabel("در انتظار: 0");
        
        statsPanel.add(totalLabel);
        statsPanel.add(completedLabel);
        statsPanel.add(pendingLabel);
        
        // File buttons panel
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filePanel.setBorder(BorderFactory.createTitledBorder("عملیات فایل"));
        
        JButton saveButton = new JButton("ذخیره در فایل");
        saveButton.addActionListener(e -> saveToFile());
        
        JButton loadButton = new JButton("بارگذاری از فایل");
        loadButton.addActionListener(e -> loadFromFile());
        
        JButton exportButton = new JButton("خروجی CSV");
        exportButton.addActionListener(e -> exportToCSV());
        
        filePanel.add(saveButton);
        filePanel.add(loadButton);
        filePanel.add(exportButton);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statsPanel, BorderLayout.NORTH);
        bottomPanel.add(filePanel, BorderLayout.SOUTH);
        
        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add Enter key event for task title field
        taskField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addTask();
                }
            }
        });
        
        // Update stats initially
        updateStatistics(totalLabel, completedLabel, pendingLabel);
        
        // Add a timer to update stats periodically
        javax.swing.Timer statsTimer = new javax.swing.Timer(1000, e -> updateStatistics(totalLabel, completedLabel, pendingLabel));
        statsTimer.start();
    }
    
    private int findTaskIndex(int tableRow) {
        if (tableModel.getRowCount() == 0 || tableRow >= tableModel.getRowCount()) {
            return -1;
        }
        
        String title = (String) tableModel.getValueAt(tableRow, 0);
        String priority = (String) tableModel.getValueAt(tableRow, 1);
        String category = (String) tableModel.getValueAt(tableRow, 2);
        
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getTitle().equals(title) && 
                task.getPriority().equals(priority) && 
                task.getCategory().equals(category)) {
                return i;
            }
        }
        return -1;
    }
    
    private void updateStatistics(JLabel totalLabel, JLabel completedLabel, JLabel pendingLabel) {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isCompleted).count();
        int pending = total - completed;
        
        totalLabel.setText("کل: " + total);
        completedLabel.setText("انجام شده: " + completed);
        pendingLabel.setText("در انتظار: " + pending);
    }
    
    private void sortTasks(String sortBy) {
        Comparator<Task> comparator;
        
        switch (sortBy) {
            case "تاریخ":
                comparator = (t1, t2) -> {
                    if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
                    if (t1.getDueDate() == null) return 1;
                    if (t2.getDueDate() == null) return -1;
                    return t1.getDueDate().compareTo(t2.getDueDate());
                };
                break;
            case "اولویت":
                comparator = (t1, t2) -> {
                    Map<String, Integer> priorityOrder = Map.of("High", 1, "Medium", 2, "Low", 3);
                    return priorityOrder.get(t1.getPriority()).compareTo(priorityOrder.get(t2.getPriority()));
                };
                break;
            case "دسته‌بندی":
                comparator = Comparator.comparing(Task::getCategory);
                break;
            case "عنوان":
            default:
                comparator = Comparator.comparing(Task::getTitle);
                break;
        }
        
        tasks.sort(comparator);
        refreshTable();
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("خروجی به فایل CSV");
        fileChooser.setSelectedFile(new File("tasks.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Title,Priority,Category,Due Date,Status");
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                for (Task task : tasks) {
                    String dueDateStr = task.getDueDate() != null ? sdf.format(task.getDueDate()) : "Not Set";
                    String status = task.isCompleted() ? "Completed" : "Pending";
                    
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        task.getTitle(),
                        task.getPriority(),
                        task.getCategory(),
                        dueDateStr,
                        status);
                }
                
                JOptionPane.showMessageDialog(this, "فایل CSV با موفقیت ذخیره شد.", "خروجی موفق", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "خطا در ذخیره فایل: " + e.getMessage(), "خطای خروجی", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addTask() {
        String title = taskField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "لطفاً عنوان کار را وارد کنید.", "خطا", JOptionPane.ERROR_MESSAGE);
            taskField.requestFocus();
            return;
        }
        
        // Check for duplicate tasks
        for (Task existingTask : tasks) {
            if (existingTask.getTitle().equalsIgnoreCase(title)) {
                JOptionPane.showMessageDialog(this, "کاری با این عنوان قبلاً وجود دارد.", "خطا", JOptionPane.ERROR_MESSAGE);
                taskField.requestFocus();
                return;
            }
        }
        
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        Date dueDate = dueDateChooser.getDate();
        
        Task task = new Task(title, priority, category, dueDate, false);
        tasks.add(task);
        
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "کار با موفقیت اضافه شد.", "افزودن کار", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void addTaskToTable(Task task) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = task.getDueDate() != null ? sdf.format(task.getDueDate()) : "تعیین نشده";
        
        tableModel.addRow(new Object[]{
            task.getTitle(),
            task.getPriority(),
            task.getCategory(),
            dueDateStr,
            task.isCompleted()
        });
    }
    
    private void updateTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "لطفاً کاری را برای بروزرسانی انتخاب کنید.", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String title = taskField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "لطفاً عنوان کار را وارد کنید.", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String priority = (String) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        Date dueDate = dueDateChooser.getDate();
        boolean completed = (boolean) tableModel.getValueAt(selectedRow, 4);
        
        // Find and update task in the list
        int taskIndex = findTaskIndex(selectedRow);
        if (taskIndex != -1) {
            Task task = tasks.get(taskIndex);
            task.setTitle(title);
            task.setPriority(priority);
            task.setCategory(category);
            task.setDueDate(dueDate);
            task.setCompleted(completed);
            
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "کار با موفقیت بروزرسانی شد.", "بروزرسانی کار", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "لطفاً کاری را برای حذف انتخاب کنید.", "خطا", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "آیا مطمئن هستید که می‌خواهید این کار را حذف کنید؟", 
            "تایید حذف", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int taskIndex = findTaskIndex(selectedRow);
            if (taskIndex != -1) {
                tasks.remove(taskIndex);
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "کار با موفقیت حذف شد.", "حذف کار", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void displayTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow != -1) {
            int taskIndex = findTaskIndex(selectedRow);
            if (taskIndex != -1) {
                Task task = tasks.get(taskIndex);
                
                taskField.setText(task.getTitle());
                priorityCombo.setSelectedItem(task.getPriority());
                categoryCombo.setSelectedItem(task.getCategory());
                dueDateChooser.setDate(task.getDueDate());
            }
        }
    }
    
    private void clearForm() {
        taskField.setText("");
        priorityCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        dueDateChooser.setDate(null);
        tasksTable.clearSelection();
        taskField.requestFocus();
    }
    
    private void searchTasks(String searchTerm) {
        if (searchTerm.isEmpty()) {
            refreshTable();
            return;
        }
        
        searchTerm = searchTerm.toLowerCase();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search and display results
        boolean found = false;
        for (Task task : tasks) {
            if (task.getTitle().toLowerCase().contains(searchTerm) ||
                task.getCategory().toLowerCase().contains(searchTerm) ||
                task.getPriority().toLowerCase().contains(searchTerm)) {
                
                addTaskToTable(task);
                found = true;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, "کاری با این مشخصات یافت نشد.", "نتایج جستجو", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Task task : tasks) {
            addTaskToTable(task);
        }
    }
    
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("ذخیره لیست کارها");
        fileChooser.setSelectedFile(new File("tasks.dat"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(tasks);
                JOptionPane.showMessageDialog(this, "لیست کارها با موفقیت ذخیره شد.", "ذخیره موفق", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "خطا در ذخیره فایل: " + e.getMessage(), "خطای ذخیره", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("بارگذاری لیست کارها از فایل");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (ArrayList<Task>) ois.readObject();
                refreshTable();
                JOptionPane.showMessageDialog(this, "لیست کارها با موفقیت بارگذاری شد.", "بارگذاری موفق", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "خطا در بارگذاری فایل: " + e.getMessage(), "خطای بارگذاری", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Inner class for colored table row rendering
    class TaskTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String priority = (String) table.getValueAt(row, 1);
                boolean completed = (boolean) table.getValueAt(row, 4);
                
                if (completed) {
                    c.setBackground(new Color(220, 255, 220)); // Light Green
                    c.setForeground(Color.DARK_GRAY);
                } else if (priority.equals("High")) {
                    c.setBackground(new Color(255, 220, 220)); // Light Red
                    c.setForeground(Color.BLACK);
                } else if (priority.equals("Medium")) {
                    c.setBackground(new Color(255, 255, 200)); // Light Yellow
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
            
            return c;
        }
    }
    
    // Task class
    static class Task implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String title;
        private String priority;
        private String category;
        private Date dueDate;
        private boolean completed;
        private Date createdDate;
        
        public Task(String title, String priority, String category, Date dueDate, boolean completed) {
            this.title = title;
            this.priority = priority;
            this.category = category;
            this.dueDate = dueDate;
            this.completed = completed;
            this.createdDate = new Date();
        }
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Date getDueDate() { return dueDate; }
        public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
        
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        
        public Date getCreatedDate() { return createdDate; }
        public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    }
    
    // Simple date chooser class
    static class JDateChooser extends JPanel {
        private JSpinner dateSpinner;
        
        public JDateChooser() {
            setLayout(new BorderLayout());
            
            SpinnerDateModel model = new SpinnerDateModel();
            dateSpinner = new JSpinner(model);
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(editor);
            
            add(dateSpinner, BorderLayout.CENTER);
            
            // Add clear button
            JButton clearButton = new JButton("×");
            clearButton.setPreferredSize(new Dimension(25, 25));
            clearButton.addActionListener(e -> setDate(null));
            add(clearButton, BorderLayout.EAST);
        }
        
        public void setDateFormatString(String format) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dateSpinner.getEditor();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            editor.getTextField().setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.DateFormatter(sdf)
            ));
        }
        
        public Date getDate() {
            try {
                return (Date) dateSpinner.getValue();
            } catch (Exception e) {
                return null;
            }
        }
        
        public void setDate(Date date) {
            if (date != null) {
                dateSpinner.setValue(date);
            } else {
                dateSpinner.setValue(new Date());
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            // Set look and feel to match the operating system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ToDoListGUI app = new ToDoListGUI();
            app.setVisible(true);
        });
    }
}