package processor;

import java.util.Arrays;
import java.util.Scanner;

class Matrix {
    enum KindsOfTranspose {
        Main_Diagonal(1),
        Side_Diagonal(2),
        Vertical_Line(3),
        Horizontal_Line(4);

        private final int kind;
        KindsOfTranspose(int kind) {
            this.kind = kind;
        }

        public static KindsOfTranspose getValueByIndex(int index) {
            for (KindsOfTranspose val : KindsOfTranspose.values()) {
                if (val.kind == index) {
                    return val;
                }
            }

            return null;
        }
    }

    static double[][] sumOfMatrix(double[][] matrix1, double[][] matrix2) throws Exception {
        if (matrix1.length != matrix2.length) {
            throw new Exception("Matrices must have same size");
        }

        double[][] result = new double[matrix1.length][matrix1[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }

        return result;
    }

    static double[][] multiplyMatrixToConstant(double[][] matrix, double k) {
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = matrix[i][j] * k;
            }
        }

        return result;
    }

    static double[][] multiplyMatrixToMatrix(double[][] matrix1, double[][] matrix2) throws Exception {
        if (matrix1[0].length != matrix2.length) {
            throw new Exception("Number of rows of matrix 1 must be equal numbers of columns of matrix 2");
        }

        double[][] result = new double[matrix1.length][matrix2[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {

                double r = 0;
                for (int k = 0; k < matrix2.length; k++) {
                    r += matrix1[i][k] * matrix2[k][j];
                }

                result[i][j] = r;
            }
        }

        return result;
    }

    static double[][] transposeMatrix(double[][] matrix, KindsOfTranspose kindOfTranspose) {
        double[][] result;
        if (kindOfTranspose.equals(KindsOfTranspose.Side_Diagonal) || kindOfTranspose.equals(KindsOfTranspose.Main_Diagonal)) {
            result = new double[matrix[0].length][matrix.length];
        } else {
            result = new double[matrix.length][matrix[0].length];
        }

        switch (kindOfTranspose) {
            case Main_Diagonal:
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        result[j][i] = matrix[i][j];
                    }
                }
                break;
            case Side_Diagonal:
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        result[matrix[0].length - j - 1][matrix.length - i - 1] = matrix[i][j];
                    }
                }
                break;
            case Vertical_Line:
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[0].length; j++) {
                        result[i][matrix[0].length - j - 1] = matrix[i][j];
                    }
                }
                break;
            case Horizontal_Line:
                for (int i = 0; i < matrix.length; i++) {
                    System.arraycopy(matrix[i], 0, result[matrix.length - i - 1], 0, matrix[0].length);
                }
                break;
            default:
                break;
        }

        return result;
    }

    static double calculateDeterminant(double[][] matrix) {
        if (matrix.length == 1) {
            return matrix[0][0];
        } else if (matrix.length == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }

        double result = 0;
        for (int i = 0; i < matrix.length; i++) {
            result += matrix[i][0] * calculateDeterminant(minorMatrix(matrix, i, 0)) * (i % 2 == 0 ? 1 : -1);
        }

        return result;
    }

    static double[][] inverseMatrix(double[][] matrix) throws Exception {
        double det = calculateDeterminant(matrix);
        if (det == 0) {
            throw new Exception("Determinant = 0");
        }

        double[][] result = transposeMatrix(cofactorMatrix(matrix), KindsOfTranspose.Main_Diagonal);
        return multiplyMatrixToConstant(result, 1 / det);
    }

    private static double[][] cofactorMatrix(double[][] matrix) {
        double[][] result = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int k = (i + j) % 2 == 0 ? 1 : -1;
                result[i][j] = calculateDeterminant(minorMatrix(matrix, i, j)) * k;
            }
        }

        return result;
    }

    private static double[][] minorMatrix(double[][] matrix, int row, int col) {
        double[][] result = new double[matrix.length - 1][matrix.length - 1];

        for (int i = 0; i < matrix.length; i++) {
            if (i == row) {
                continue;
            }
            for (int j = 0; j < matrix.length; j++) {
                if (j == col) {
                    continue;
                }

                result[i < row ? i : i - 1][j < col ? j : j - 1] = matrix[i][j];
            }
        }

        return result;
    }

    static void printMatrix(double[][] matrix) {
        for (double[] doubles : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%.2f ", doubles[j]);
            }
            System.out.println();
        }
    }
}

public class Main {
    final static Scanner scanner = new Scanner(System.in);

    static void enterMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = Arrays.stream(scanner.nextLine().split("\\s+"))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        }
    }

    static void addMatrices() {
        System.out.print("Enter size of first matrix: ");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix1 = new double[n][m];
        System.out.println("Enter first matrix:");
        enterMatrix(matrix1);

        System.out.print("Enter size of second matrix: ");
        n = scanner.nextInt();
        m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix2 = new double[n][m];
        System.out.println("Enter second matrix:");
        enterMatrix(matrix2);

        System.out.println("Addition result:");
        try {
            Matrix.printMatrix(Matrix.sumOfMatrix(matrix1, matrix2));
        } catch (Exception e) {
            System.out.println("ERROR. " + e.getMessage());
        }
    }

    static void multiplyMatrixByAConstant() {
        System.out.print("Enter size of matrix:");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix = new double[n][m];
        System.out.println("Enter matrix:");
        enterMatrix(matrix);

        System.out.print("Enter coefficient:");
        double k = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("The multiplication result is:");
        Matrix.printMatrix(Matrix.multiplyMatrixToConstant(matrix, k));
    }

    static void multiplyMatrices() {
        System.out.print("Enter size of first matrix: ");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix1 = new double[n][m];
        System.out.println("Enter first matrix:");
        enterMatrix(matrix1);

        System.out.print("Enter size of second matrix: ");
        n = scanner.nextInt();
        m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix2 = new double[n][m];
        System.out.println("Enter second matrix:");
        enterMatrix(matrix2);

        System.out.println("The multiplication result is:");
        try {
            Matrix.printMatrix(Matrix.multiplyMatrixToMatrix(matrix1, matrix2));
        } catch (Exception e) {
            System.out.println("ERROR. " + e.getMessage());
        }
    }

    static void transposeMatrix() {
        System.out.println();

        int kindOfTranspose = 0;
        while (kindOfTranspose < 1 || kindOfTranspose > 4) {
            System.out.println("1. Main diagonal");
            System.out.println("2. Side diagonal");
            System.out.println("3. Vertical line");
            System.out.println("4. Horizontal line");

            kindOfTranspose = Integer.parseInt(scanner.nextLine());
            if (kindOfTranspose < 1 || kindOfTranspose > 4) {
                System.out.println("Incorrect choice");
            }
        }

        System.out.print("Enter size of matrix:");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix = new double[n][m];
        System.out.println("Enter matrix:");
        enterMatrix(matrix);

        System.out.println("The result is:");
        Matrix.printMatrix(Matrix.transposeMatrix(matrix, Matrix.KindsOfTranspose.getValueByIndex(kindOfTranspose)));
    }

    static void calculateDeterminant() {
        System.out.print("Enter size of matrix:");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix = new double[n][m];
        System.out.println("Enter matrix:");
        enterMatrix(matrix);

        System.out.println("The result is:");
        System.out.println(Matrix.calculateDeterminant(matrix));
    }

    static void inverseMatrix() {
        System.out.print("Enter matrix size:");
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        scanner.nextLine();

        double[][] matrix = new double[n][m];
        System.out.println("Enter matrix:");
        enterMatrix(matrix);

        System.out.println("The result is:");
        try {
            Matrix.printMatrix(Matrix.inverseMatrix(matrix));
        } catch (Exception e) {
            System.out.println("ERROR. " + e.getMessage());
        }
    }

    static void showMenu() {
        System.out.println("1. Add matrices");
        System.out.println("2. Multiply matrix by a constant");
        System.out.println("3. Multiply matrices");
        System.out.println("4. Transpose matrix");
        System.out.println("5. Calculate a determinant");
        System.out.println("0. Exit");

        System.out.print("Your choice: ");

        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1:
                addMatrices();
                break;
            case 2:
                multiplyMatrixByAConstant();
                break;
            case 3:
                multiplyMatrices();
                break;
            case 4:
                transposeMatrix();
                break;
            case 5:
                calculateDeterminant();
                break;
            case 6:
                inverseMatrix();
                break;
            case 0:
                break;
            default:
                System.out.println("Incorrect command");
        }

        if (choice != 0) {
            System.out.println();
            showMenu();
        }
    }

    public static void main(String[] args) {
        showMenu();
    }
}
