package cinema;

import java.util.Scanner;

public class Cinema {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Enter the number of rows:");
        int rows = Integer.parseInt(SCANNER.nextLine());

        System.out.println("Enter the number of seats in each row:");
        int seats = Integer.parseInt(SCANNER.nextLine());

        Room room = new Room(rows, seats);

        System.out.println();
        while (room.showMenu(SCANNER) != 0);

        SCANNER.close();
    }
}