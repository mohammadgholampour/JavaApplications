package com.example.demo;

// PhoneBook.java
import java.util.HashMap;
import java.util.Scanner;

public class PhoneBook {
    public static void main(String[] args) {
        HashMap<String, String> phoneBook = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the Phone Book!");
        do {
            System.out.print("Enter command (add/view/exit): ");
            command = scanner.nextLine();

            if (command.equals("add")) {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                System.out.print("Enter phone number: ");
                String phoneNumber = scanner.nextLine();
                phoneBook.put(name, phoneNumber);
                System.out.println("Contact added!");
            } else if (command.equals("view")) {
                System.out.println("Phone Book:");
                for (String name : phoneBook.keySet()) {
                    System.out.println(name + ": " + phoneBook.get(name));
                }
            }
        } while (!command.equals("exit"));

        System.out.println("Exiting Phone Book. Goodbye!");
        scanner.close();
    }
}