package com.example.demo;

// UnitConverter.java
import java.util.Scanner;

public class UnitConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Unit Converter!");

        System.out.print("Enter value in meters: ");
        double meters = scanner.nextDouble();
        double feet = meters * 3.28084;

        System.out.println(meters + " meters is equal to " + feet + " feet.");
        scanner.close();
    }
}