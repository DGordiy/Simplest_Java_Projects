package maze;

import java.io.*;
import java.util.*;

class Maze {

    public boolean isGenerated() {
        return generated;
    }

    private static class Edge {
        int xStart, yStart;
        int xEnd, yEnd;

        public Edge(int xStart, int yStart, int xEnd, int yEnd) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
        }
    }
    private static final int MAX_STEPS = 1000;

    private int[][] data;
    private int width;
    private int height;

    private boolean generated = false;

    public void generate(int width, int height) {
        //
        this.width = width;
        this.height = height;

        Random random = new Random();
        data = new int[height][width];

        ArrayList<Edge> frontiers = new ArrayList<>();
        frontiers.add(new Edge(1, 1, 1, 1));

        while (!frontiers.isEmpty()) {
            int idx = random.nextInt(frontiers.size());
            Edge e = frontiers.remove(idx);

            if (data[e.yEnd][e.xEnd] == 0) {
                data[e.yStart][e.xStart] = 1;
                data[e.yEnd][e.xEnd] = 1;
                //add LEFT edge
                if (e.xEnd > 2 && data[e.yEnd][e.xEnd - 2] == 0)
                    frontiers.add(new Edge(e.xEnd - 1, e.yEnd, e.xEnd - 2, e.yEnd));
                //add RIGHT edge
                if (e.xEnd < width - 3 && data[e.yEnd][e.xEnd + 2] == 0)
                    frontiers.add(new Edge(e.xEnd + 1, e.yEnd, e.xEnd + 2, e.yEnd));
                //add TOP edge
                if (e.yEnd > 2 && data[e.yEnd - 2][e.xEnd] == 0)
                    frontiers.add(new Edge(e.xEnd, e.yEnd - 1, e.xEnd, e.yEnd - 2));
                //add BOTTOM edge
                if (e.yEnd < height - 3 && data[e.yEnd + 2][e.xEnd] == 0)
                    frontiers.add(new Edge(e.xEnd, e.yEnd + 1, e.xEnd, e.yEnd + 2));
            }
        }
        //cut Entrance and Exit openings
        data[1][0] = 1;
        int y = height - 3 + height % 2;
        data[y][width - 2 + width % 2] = 1;
        data[y][width - 1] = 1;

        //make escape
        makeEscape();

        generated = true;
    }

    private void makeEscape() {
        int startX = 0, startY = 1;
        int endX = width - 1, endY = height - 3 + height % 2;

        traverseEscape(startX, startY, endX, endY, 1);
        clearDataForEscape(endX, endY, startX, startY);
    }

    private void traverseEscape(int fromX, int fromY, int toX, int toY, int steps) {
        data[fromY][fromX] = MAX_STEPS + steps;
        if (fromX == toX && fromY == toY) {
            return;
        }

        if (fromX > 1 && (data[fromY][fromX - 1] == 1 || data[fromY][fromX - 1] > 1 && data[fromY][fromX - 1] > steps + MAX_STEPS)) {
            data[fromY][fromX - 1] = steps + MAX_STEPS;
            traverseEscape(fromX - 1, fromY, toX, toY, steps + 1);
        }
        if (fromX < width - 1 && (data[fromY][fromX + 1] == 1 || data[fromY][fromX + 1] > 1 && data[fromY][fromX + 1] > steps + MAX_STEPS)) {
            data[fromY][fromX + 1] = steps + MAX_STEPS;
            traverseEscape(fromX + 1, fromY, toX, toY, steps + 1);
        }
        if (fromY > 1 && (data[fromY - 1][fromX] == 1 || data[fromY - 1][fromX] > 1 && data[fromY - 1][fromX] > steps + MAX_STEPS)) {
            data[fromY - 1][fromX] = steps + MAX_STEPS;
            traverseEscape(fromX, fromY - 1, toX, toY, steps + 1);
        }
        if (fromY < height - 1 && (data[fromY + 1][fromX] == 1 || data[fromY + 1][fromX] > 1 && data[fromY + 1][fromX] > steps + MAX_STEPS)) {
            data[fromY + 1][fromX] = steps + MAX_STEPS;
            traverseEscape(fromX, fromY + 1, toX, toY, steps + 1);
        }
    }

    private void clearDataForEscape(int fromX, int fromY, int toX, int toY) {
        data[fromY][fromX] -= MAX_STEPS - 1;
        if (fromX == toX && fromY == toY) {
            return;
        }

        int minX = width;
        int minY = height;
        int minWeight = MAX_STEPS * 10;
        if (fromX > 0 && (data[fromY][fromX - 1] > MAX_STEPS && data[fromY][fromX - 1] < minWeight)) {
            minX = fromX - 1;
            minY = fromY;
            minWeight = data[fromY][fromX - 1];
        }
        if (fromX < width - 2 && (data[fromY][fromX + 1] > MAX_STEPS && data[fromY][fromX + 1] < minWeight)) {
            minX = fromX + 1;
            minY = fromY;
            minWeight = data[fromY][fromX + 1];
        }
        if (fromY > 0 && (data[fromY - 1][fromX] > MAX_STEPS && data[fromY - 1][fromX] < minWeight)) {
            minX = fromX;
            minY = fromY - 1;
            minWeight = data[fromY - 1][fromX];
        }
        if (fromY < height - 2 && (data[fromY + 1][fromX] > MAX_STEPS && data[fromY + 1][fromX] < minWeight)) {
            minX = fromX;
            minY = fromY + 1;
            minWeight = data[fromY + 1][fromX];
        }
        clearDataForEscape(minX, minY, toX, toY);
    }

    public void show(boolean withEscape) {
        for (int[] line : data) {
            for (int cell : line) {
                switch (cell) {
                    case 0:
                        System.out.print("\u2588\u2588");
                        break;
                    case 1:
                        System.out.print("  ");
                        break;
                    default:
                        if (withEscape && cell < MAX_STEPS) {
                            System.out.print("//");
                        } else {
                            System.out.print("  ");
                        }
                }

            }

            System.out.println();
        }
    }

    public void saveToFile(String fileName) {
        try (ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(fileName))) {
            ous.writeObject(data);
        } catch (IOException e) {
            System.out.println("Cannot save the maze. Reason: " + e.getMessage());
        }
    }

    public void loadFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            data = (int[][]) ois.readObject();

            width = data[0].length;
            height = data.length;

            generated = true;
        } catch (FileNotFoundException e) {
            System.out.println("The file '" + fileName + "' does not exist");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cannot load the maze. It has an invalid format");
        }
    }
}

public class Main {
    private static Maze maze;

    private static int selectFromMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Generate a new maze");
        System.out.println("2. Load a maze");

        if (maze.isGenerated()) {
            System.out.println("3. Save the maze");
            System.out.println("4. Display the maze");
            System.out.println("5. Find the escape");
        }

        System.out.println("0. Exit");

        Scanner scanner = new Scanner(System.in);
        int result = Integer.parseInt(scanner.nextLine());

        switch (result) {
            case 1:
                System.out.println("Enter the size of a new maze");
                int size = scanner.nextInt();

                maze.generate(size, size);
                maze.show(false);

                break;
            case 2:
                maze.loadFromFile(scanner.nextLine());
                break;
            case 3:
                if (maze.isGenerated()) {
                    maze.saveToFile(scanner.nextLine());
                } else {
                    System.out.println("Incorrect option");
                }

                break;
            case 4:
                if (maze.isGenerated()) {
                    maze.show(false);
                } else {
                    System.out.println("Incorrect option");
                }

                break;
            case 5:
                if (maze.isGenerated()) {
                    maze.show(true);
                } else {
                    System.out.println("Incorrect option");
                }

                break;
            case 0:
                return 0;
            default:
                System.out.println("Incorrect option");
        }

        System.out.println();

        return result;
    }

    public static void main(String[] args) {
        maze = new Maze();

        int selection = -1;
        while (selection != 0) {
            selection = selectFromMenu();
        }
        System.out.println("Bye!");
    }

}
