package com.example.demo;

// StudentRegistration.java
import java.util.ArrayList;
import java.util.Scanner;

class Student {
    String name;
    int id;

    Student(String name, int id) {
        this.name = name;
        this.id = id;
    }
}

public class StudentRegistration {
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the Student Registration System!");
        do {
            System.out.print("Enter command (register/view/exit): ");
            command = scanner.nextLine();

            if (command.equals("register")) {
                System.out.print("Enter student name: ");
                String name = scanner.nextLine();
                System.out.print("Enter student ID: ");
                int id = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                students.add(new Student(name, id));
                System.out.println("Student registered!");
            } else if (command.equals("view")) {
                System.out.println("Registered Students:");
                for (Student student : students) {
                    System.out.println(student.name + " (ID: " + student.id + ")");
                }
            }
        } while (!command.equals("exit"));

        System.out.println("Exiting Student Registration. Goodbye!");
        scanner.close();
    }
}