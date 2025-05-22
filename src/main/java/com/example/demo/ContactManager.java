package com.example.demo;

import javax.swing.*;
import java.awt.*;

class Contact {
    String name;
    String phoneNumber;

    Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}

public class ContactManager {
    private static DefaultListModel<String> contactListModel = new DefaultListModel<>();
    private static JList<String> contactList = new JList<>(contactListModel);
    private static JTextField nameField = new JTextField(15);
    private static JTextField phoneField = new JTextField(15);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Contact Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Phone:"));
            inputPanel.add(phoneField);

            JButton addButton = new JButton("Add");
            addButton.addActionListener(e -> addContact());
            inputPanel.add(addButton);

            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(new JScrollPane(contactList), BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    private static void addContact() {
        String name = nameField.getText();
        String phoneNumber = phoneField.getText();
        if (!name.isEmpty() && !phoneNumber.isEmpty()) {
            contactListModel.addElement(name + ": " + phoneNumber);
            nameField.setText("");
            phoneField.setText("");
        }
    }
}
