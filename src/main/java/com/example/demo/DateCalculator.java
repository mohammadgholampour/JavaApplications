package com.example.demo;

// DateCalculator.java
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;

public class DateCalculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Date Calculator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setVisible(true);
        });
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Date Calculator!");

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateInput = scanner.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateInput = scanner.nextLine();

        LocalDate startDate = LocalDate.parse(startDateInput);
        LocalDate endDate = LocalDate.parse(endDateInput);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        System.out.println("Days between: " + daysBetween);
        scanner.close();
    }
}