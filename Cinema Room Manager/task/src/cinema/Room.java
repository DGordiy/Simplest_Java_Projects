package cinema;

import java.util.Arrays;
import java.util.Scanner;

public class Room {

    private final int rows_count;
    private final int seats_count;
    private final char[][] seats;

    private int income = 0;
    private int purchasedTickets = 0;

    Room(int rows_count, int seats_count) {
        this.rows_count = rows_count;
        this.seats_count = seats_count;

        this.seats = new char[rows_count][seats_count];
        for (int i = 0; i < rows_count; i++) {
            Arrays.fill(seats[i], 'S');
        }
    }

    public void show() {
        System.out.println("Cinema:");

        System.out.print(" ");

        for (int i = 0; i < seats_count; i++) {
            System.out.printf(" %d", i + 1);
        }

        for (int i = 0; i < rows_count; i++) {
            System.out.println();
            System.out.print(i + 1);

            for (int j = 0; j < seats_count; j++) {
                System.out.printf(" %c", seats[i][j]);
            }
        }

        System.out.println();
    }

    public int getTotalIncome() {
        return seats_count *
                (getTicketPrice(1, 1) * (rows_count / 2) +
                        (rows_count > 1 ? getTicketPrice(rows_count, 1) * (rows_count - rows_count / 2) : 0));
    }

    public int getTicketPrice(int row, int seat) {
        if (rows_count * seats_count > 60) {
            return row <= rows_count / 2 ? 10 : 8;
        } else {
            return 10;
        }
    }

    public int showMenu(Scanner scanner) {
        System.out.println("1. Show the seats");
        System.out.println("2. Buy a ticket");
        System.out.println("3. Statistics");
        System.out.println("0. Exit");

        int choice = Integer.parseInt(scanner.nextLine());
        System.out.println();
        switch (choice) {
            case 1:
                show();
                break;
            case 2:
                buyTicket(scanner);
                break;
            case 3:
                showStatistics();
                break;
            case 0:
                return 0;
            default:
                System.out.println();
                return showMenu(scanner);
        }

        System.out.println();
        return choice;
    }

    public void buyTicket(Scanner scanner) {
        try {
            System.out.println("Enter a row number:");
            int rowNumber = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter a seat number in that row:");
            int seatNumber = Integer.parseInt(scanner.nextLine());

            if (seats[rowNumber - 1][seatNumber - 1] == 'S') {
                seats[rowNumber - 1][seatNumber - 1] = 'B';
                int price = getTicketPrice(rowNumber, seatNumber);
                income += price;
                purchasedTickets++;

                System.out.printf("\nTicket price: $%d\n", price);
            } else {
                System.out.println("That ticket has already been purchased!\n");
                buyTicket(scanner);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Wrong input!\n");
            buyTicket(scanner);
        }
    }

    public void showStatistics() {
        System.out.printf("Number of purchased tickets: %d\n", purchasedTickets);
        System.out.printf("Percentage: %.2f%%\n", (double) purchasedTickets / (rows_count * seats_count) * 100);
        System.out.printf("Current income: $%d\n", income);
        System.out.printf("Total income: $%d\n", getTotalIncome());
    }
}
