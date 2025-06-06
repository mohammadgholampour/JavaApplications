package com.example.demo;

import java.util.Scanner;

public class SimpleCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Welcome to the Simple Calculator!");
            System.out.print("Enter first number: ");
            double num1 = scanner.nextDouble();
            System.out.print("Enter second number: ");
            double num2 = scanner.nextDouble();
            System.out.print("Choose an operation (+, -, *, /): ");
            char operation = scanner.next().charAt(0);
            
            double result;
            switch (operation) {
                case '+':
                    result = num1 + num2;
                    break;
                case '-':
                    result = num1 - num2;
                    break;
                case '*':
                    result = num1 * num2;
                    break;
                case '/':
                    result = num1 / num2;
                    break;
                default:
                    System.out.println("Invalid operation!");
                    return;
            }
            System.out.println("Result: " + result);
        } finally {
            scanner.close(); // Ensure scanner is closed
        }
    }
}
