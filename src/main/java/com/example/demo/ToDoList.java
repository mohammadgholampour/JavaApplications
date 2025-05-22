package com.example.demo;
// ToDoList.java
import java.util.ArrayList;
import java.util.Scanner;

public class ToDoList {
    public static void main(String[] args) {
        ArrayList<String> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the To-Do List!");
        do {
            System.out.print("Enter command (add/view/exit): ");
            command = scanner.nextLine();

            if (command.equals("add")) {
                System.out.print("Enter task: ");
                String task = scanner.nextLine();
                tasks.add(task);
                System.out.println("Task added!");
            } else if (command.equals("view")) {
                System.out.println("Tasks:");
                for (String task : tasks) {
                    System.out.println(task);
                }
            }
        } while (!command.equals("exit"));

        System.out.println("Exiting To-Do List. Goodbye!");
        scanner.close();
    }
}