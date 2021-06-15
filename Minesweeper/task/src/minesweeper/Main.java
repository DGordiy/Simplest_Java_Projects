package minesweeper;

import java.util.*;

class Game {
    private final Scanner SCANNER = new Scanner(System.in);

    private final static int FIELD_SIZE = 9;
    private final static char EMPTY_CELL = '.';
    private final static char MINE_CELL = 'X';
    private final static char MARK = '*';
    private final static char OPENED_FREE_CELL = '/';

    private boolean[][] mines = null;
    private char[][] open = null;
    private int minesCount;
    private boolean firstTurn;

    Game() {}

    private void show(boolean showMines) {
        //HEADER
        System.out.print(" |");
        for (int i = 0; i < FIELD_SIZE; i++) {
            System.out.print(i + 1);
        }
        System.out.println("|");
        System.out.print("-|");
        for (int i = 0; i < FIELD_SIZE; i++) {
            System.out.print("-");
        }
        System.out.println("|");
        //

        for (int i = 0; i < FIELD_SIZE; i++) {
            System.out.print(i + 1);
            System.out.print("|");
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (showMines && mines[i][j]) {
                    System.out.print(MINE_CELL);
                } else if (open[i][j] != '\0') {
                    System.out.print(open[i][j]);
                } else {
                    System.out.print(EMPTY_CELL);
                }
            }

            System.out.println("|");
        }

        //FOOTER
        System.out.print("-|");
        for (int i = 0; i < FIELD_SIZE; i++) {
            System.out.print("-");
        }
        System.out.println("|");
    }

    private void generateMines(int minesCountToGenerate, int[] excludeCoord) {
        Random random = new Random();

        int count = minesCountToGenerate;
        while (count > 0) {
            for (int i = 0; i < FIELD_SIZE; i++) {
                int column = random.nextInt(FIELD_SIZE);
                if (i != excludeCoord[0] && column != excludeCoord[1] && !mines[i][column]) {
                    mines[i][column] = true;
                    count--;
                }

                if (count == 0) {
                    break;
                }
            }
        }
    }

    private int minesCountNearCell(int x, int y) {
        return getMine(x - 1, y - 1) + getMine(x - 1, y) + getMine(x - 1, y + 1)
                + getMine(x, y - 1) + getMine(x, y + 1)
                + getMine(x + 1, y - 1) + getMine(x + 1, y) + getMine(x + 1, y + 1);
    }

    public void start() {
        mines = new boolean[FIELD_SIZE][FIELD_SIZE];
        open = new char[FIELD_SIZE][FIELD_SIZE];
        firstTurn = true;

        System.out.print("How many mines do you want on the field? ");
        minesCount = Integer.parseInt(SCANNER.nextLine());
        minesCount = Math.min(minesCount, FIELD_SIZE * FIELD_SIZE - 1);
        generateMines(minesCount, new int[] {-1, -1});

        show(false);
        makeTurn();
    }

    private void feelFreeCells(int x, int y) {
        if (x < 0 || y < 0 || x >= FIELD_SIZE || y >= FIELD_SIZE) {
            return;
        }

        if (open[x][y] != '\0') {
            if (open[x][y] != MARK) {
                /*try (FileWriter fw = new FileWriter("log.txt", true)) {
                    fw.write(String.format("%d %d = %d\n", x, y, minesCountNearCell(x, y)));
                } catch (Exception e) {

                }
                 */

                return;
            }
        }

        int numOfMines = minesCountNearCell(x, y);
        if (numOfMines == 0) {
            open[x][y] = OPENED_FREE_CELL;

                feelFreeCells(x - 1, y);
                feelFreeCells(x, y - 1);
                feelFreeCells(x - 1, y - 1);
                feelFreeCells(x - 1, y + 1);
                feelFreeCells(x + 1, y);
                feelFreeCells(x, y + 1);
                feelFreeCells(x + 1, y + 1);
                feelFreeCells(x + 1, y - 1);
        } else {
            open[x][y] = Character.forDigit(numOfMines, 10);
        }
    }

    private int getMine(int x, int y) {
        if (x < 0 || y < 0 || x >= FIELD_SIZE || y >= FIELD_SIZE) {
            return 0;
        }

        return mines[x][y] ? 1 : 0;
    }

    private void makeTurn() {
        System.out.print("Set/unset mines marks or claim a cell as free: ");
        String[] command = SCANNER.nextLine().split("\\s+");

        int y = 0;
        int x = 0;
        String mode;
        if (command.length == 3) {
            y = Integer.parseInt(command[0]) - 1;
            x = Integer.parseInt(command[1]) - 1;
            mode = command[2].toUpperCase();
        } else {
            mode = "";
        }

        //Show result
        //-1 = LOSE
        //1 = WON
        int result = 0;

        switch (mode) {
            case "FREE":
                if (mines[x][y]) {
                    if (firstTurn) {
                        mines[x][y] = false;
                        generateMines(1, new int[] {x, y});
                        feelFreeCells(x, y);
                    } else {
                        result = -1;
                    }
                } else {
                    feelFreeCells(x, y);
                }
                break;
            case "MINE":
                open[x][y] = open[x][y] == MARK ? '\0' : MARK;
                break;
            default:
                System.out.println("Invalid command!");
                makeTurn();
                return;
        }

        System.out.println();

        firstTurn = false;
        //Analise if WON
        if (result == 0) {
            int marksCount = 0;
            int emptyCount = 0;
            for (int i = 0; i < FIELD_SIZE; i++) {
                for (int j = 0; j < FIELD_SIZE; j++) {
                    if (open[i][j] == MARK) {
                        marksCount++;
                    } else {
                        if (open[i][j] == '\0') {
                            emptyCount++;
                        }
                    }
                }
            }
            if (marksCount == minesCount
                    || marksCount == 0 && emptyCount == minesCount) {
                result = 1;
            }
        }
        //

        switch (result) {
            case 1:
                System.out.println("Congratulations! You found all mines!");
                break;
            case -1:
                show(true);
                System.out.println("You stepped on a mine and failed!");
                break;
            default:
                show(false);
                makeTurn();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
