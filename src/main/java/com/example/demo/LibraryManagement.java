package com.example.demo;

// LibraryManagement.java
import java.util.ArrayList;
import java.util.Scanner;

class Book {
    String title;
    String author;

    Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
}

public class LibraryManagement {
    public static void main(String[] args) {
        ArrayList<Book> books = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the Library Management System!");
        do {
            System.out.print("Enter command (add/view/exit): ");
            command = scanner.nextLine();

            if (command.equals("add")) {
                System.out.print("Enter book title: ");
                String title = scanner.nextLine();
                System.out.print("Enter book author: ");
                String author = scanner.nextLine();
                books.add(new Book(title, author));
                System.out.println("Book added!");
            } else if (command.equals("view")) {
                System.out.println("Books in Library:");
                for (Book book : books) {
                    System.out.println(book.title + " by " + book.author);
                }
            }
        } while (!command.equals("exit"));

        System.out.println("Exiting Library Management. Goodbye!");
        scanner.close();
    }
}