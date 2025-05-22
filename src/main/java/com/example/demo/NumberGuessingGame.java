package com.example.demo;
// NumberGuessingGame.java
import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Random random = new Random();
        int numberToGuess = random.nextInt(100) + 1;
        Scanner scanner = new Scanner(System.in);
        int guess;
        System.out.println("Welcome to the Number Guessing Game!");

        do {
            System.out.print("Enter your guess (1-100): ");
            guess = scanner.nextInt();
            if (guess < numberToGuess) {
                System.out.println("Higher!");
            } else if (guess > numberToGuess) {
                System.out.println("Lower!");
            } else {
                System.out.println("Congratulations! You've guessed the number!");
            }
        } while (guess != numberToGuess);

        scanner.close();
    }
}