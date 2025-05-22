package com.example.demo;

import java.util.ArrayList;
import java.util.Scanner;

public class NotePad {
    public static void main(String[] args) {
        ArrayList<String> notes = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the Note Pad!");
        do {
            System.out.print("Enter command (add/view/exit): ");
            command = scanner.nextLine();

            if (command.equals("add")) {
                System.out.print("Enter note: ");
                String note = scanner.nextLine();
                notes.add(note);
                System.out.println("Note added!");
            } else if (command.equals("view")) {
                System.out.println("Notes:");
                for (String note : notes) {
                    System.out.println(note);
                }
            }
        } while (!command.equals("exit"));

        System.out.println("Exiting Note Pad. Goodbye!");
        scanner.close();
    }
}
