package bullscows;

import java.util.Random;
import java.util.Scanner;

public class Main {
    static char[] secret;
    static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        String input = "";
        int secretLength = 0;
        int possibleSymbols = 0;
        System.out.println("Input the length of the secret code:");

        try {
            input = scanner.nextLine();
            secretLength = Integer.parseInt(input);
            if (secretLength > 36 || secretLength <= 0) {
                System.out.printf("Error: can't generate a secret number with a length of %d because there aren't enough unique digits.\n", secretLength);
                System.exit(0);
            }

            System.out.println("Input the number of possible symbols in the code:");
            input = scanner.nextLine();
            possibleSymbols = Integer.parseInt(input);

            if (possibleSymbols > 36) {
                System.out.println("Error: number of possible symbols must be less or eqgual than 36.");
                System.exit(0);
            }
            if (possibleSymbols < secretLength) {
                System.out.printf("Error: it's not possible to generate a code with a length of %d with %d unique symbols.", secretLength, possibleSymbols);
                System.exit(0);
            }
        } catch (NumberFormatException e) {
            System.out.printf("Error: %s isn't a valid number.\n", input);
            System.exit(0);
        }

        secret = generateSecret(secretLength, possibleSymbols);

        System.out.print("The secret is prepared: ");
        for (int i = 0; i < secretLength; i++) {
            System.out.print("*");
        }

        System.out.print(" ");
        if (possibleSymbols <= 10) {
            System.out.printf("(0-%d)", possibleSymbols - 1);
        } else {
            System.out.printf("(0-9, a-%c)", (char) ('a' + (possibleSymbols - 11)));
        }
        System.out.println(".");

        System.out.println("Okay, let's start a game!");
        startGame(1);

        scanner.close();
    }

    static void startGame(int turn) {
        System.out.printf("Turn %d:\n", turn);
        String input = scanner.nextLine();
        if (!showGrade(input.toCharArray())) {
            startGame(turn + 1);
        }
    }

    static boolean showGrade(char[] input) {
        int bulls = 0;
        int cows = 0;

        for (int i = 0; i < secret.length; i++) {
            if (input[i] == secret[i]) {
                bulls++;
                input[i] = '\0';
            }
        }
        for (int i = 0; i < secret.length; i++) {
            for (int j = 0; j < secret.length; j++) {
                if (j != i && input[j] != '\0' && input[j] == secret[i]) {
                    cows++;
                    input[j] = '\0';
                }
            }
        }

        if (bulls != 0 && cows != 0) {
            System.out.printf("Grade: %d bull(s) and %d cow(s).", bulls, cows);
        } else if (bulls != 0) {
            System.out.printf("Grade: %d bull(s).", bulls);
            if (bulls == secret.length) {
                System.out.println("\nCongratulations! You guessed the secret code.");
                return true;
            }
        } else if (cows != 0) {
            System.out.printf("Grade: %d cow(s).", cows);
        } else {
            System.out.print("Grade: None.");
        }

        System.out.println();

        return false;
    }

    static char[] generateSecret(int length, int possibleSymbols) {

        char[] result = new char[length];

        Random random = new Random();
        int i = 0;
        while (i < length) {
            int d = random.nextInt(possibleSymbols);
            char c = (char) (d < 10 ? ('0' + d) : ('a' + (d - 10)));

            if (i != 0 || c != '0') {
                boolean noSymbol = true;
                for (int j = 0; j < i; j++) {
                    if (result[j] == c) {
                        noSymbol = false;
                        break;
                    }
                }

                if (noSymbol) {
                    result[i] = c;
                    i++;
                }
            }
        }

        return result;
    }

}
