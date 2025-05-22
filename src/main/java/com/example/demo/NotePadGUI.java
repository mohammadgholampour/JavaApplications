package com.example.demo;

import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class NotePadGUI extends JFrame {
    private JTextArea textArea;
    private JList<String> notesList;
    private DefaultListModel<String> notesModel;
    private ArrayList<String> notes;
    private JTextField titleField;
    
    public NotePadGUI() {
        notes = new ArrayList<>();
        
        // Main frame settings
        setTitle("Advanced Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titleField = new JTextField();
        titleField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        titlePanel.add(new JLabel("Title:"), BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.CENTER);
        
        // Create text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // Create notes list
        notesModel = new DefaultListModel<>();
        notesList = new JList<>(notesModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && notesList.getSelectedIndex() != -1) {
                displayNote(notesList.getSelectedIndex());
            }
        });
        JScrollPane notesScrollPane = new JScrollPane(notesList);
        notesScrollPane.setPreferredSize(new Dimension(200, 0));
        
        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton newButton = new JButton("New Note");
        newButton.addActionListener(e -> newNote());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveNote());
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteNote());
        
        JButton exportButton = new JButton("Export to File");
        exportButton.addActionListener(e -> exportToFile());
        
        JButton importButton = new JButton("Import from File");
        importButton.addActionListener(e -> importFromFile());
        
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        
        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(notesScrollPane, BorderLayout.WEST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void newNote() {
        titleField.setText("");
        textArea.setText("");
    }
    
    private void saveNote() {
        String title = titleField.getText().trim();
        String content = textArea.getText().trim();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the note content.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedIndex = notesList.getSelectedIndex();
        if (selectedIndex != -1) {
            // ویرایش یادداشت موجود
            notes.set(selectedIndex, title + "\n" + content);
            notesModel.setElementAt(title, selectedIndex);
        } else {
            // افزودن یادداشت جدید
            notes.add(title + "\n" + content);
            notesModel.addElement(title);
        }
        
        JOptionPane.showMessageDialog(this, "Note saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void displayNote(int index) {
        if (index >= 0 && index < notes.size()) {
            String note = notes.get(index);
            int newlineIndex = note.indexOf('\n');
            if (newlineIndex != -1) {
                titleField.setText(note.substring(0, newlineIndex));
                textArea.setText(note.substring(newlineIndex + 1));
            } else {
                titleField.setText(note);
                textArea.setText("");
            }
        }
    }
    
    private void deleteNote() {
        int selectedIndex = notesList.getSelectedIndex();
        if (selectedIndex != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this note?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                notes.remove(selectedIndex);
                notesModel.remove(selectedIndex);
                titleField.setText("");
                textArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a note to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Notes As");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String note : notes) {
                    writer.println(note);
                    writer.println("---"); // Separator between notes
                }
                JOptionPane.showMessageDialog(this, "Notes saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Notes From File");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                notes.clear();
                notesModel.clear();
                
                StringBuilder noteBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("---")) {
                        // End of a note
                        String note = noteBuilder.toString().trim();
                        if (!note.isEmpty()) {
                            notes.add(note);
                            int newlineIndex = note.indexOf('\n');
                            if (newlineIndex != -1) {
                                notesModel.addElement(note.substring(0, newlineIndex));
                            } else {
                                notesModel.addElement(note);
                            }
                        }
                        noteBuilder = new StringBuilder();
                    } else {
                        noteBuilder.append(line).append("\n");
                    }
                }
                
                // Add the last note if it exists
                String lastNote = noteBuilder.toString().trim();
                if (!lastNote.isEmpty()) {
                    notes.add(lastNote);
                    int newlineIndex = lastNote.indexOf('\n');
                    if (newlineIndex != -1) {
                        notesModel.addElement(lastNote.substring(0, newlineIndex));
                    } else {
                        notesModel.addElement(lastNote);
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Notes loaded successfully.", "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
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
            NotePadGUI app = new NotePadGUI();
            app.setVisible(true);
        });
    }
}