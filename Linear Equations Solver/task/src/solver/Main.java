package solver;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    static Line[] system;
    static double[] result;
    static boolean[][] colSwap;

    static boolean infiniteSolutions = false;
    static boolean noSolutions = false;

    static class Line {
        private static int length;
        private boolean zeroLine;

        private int row;
        private double coefs[];

        private Line() {}

        public Line(double[] coefs, int row) {
            this.row = row;
            this.coefs = coefs;
        }

        public boolean isZeroLine() {
            return zeroLine;
        }

        public void setZeroLine(boolean zeroLine) {
            this.zeroLine = zeroLine;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public double[] getCoefs() {
            return coefs;
        }

        public double getCoef(int n) {
            return coefs[n];
        }

        public void setCoef(int index, double coef) {
            coefs[index] = coef;
        }

        public static void setLength(int length) {
            Line.length = length;
        }

        public double getResult() {
            return coefs[length - 1];
        }

        void mult(double k) {
            System.out.print(k);
            System.out.printf(" * R%d -> R%d\n", row + 1, row + 1);

            for (int i = 0; i < length; i++) {
                coefs[i] *= k;
            }
        }

        void addLine(Line l, double k) {
            System.out.print(k);
            System.out.printf(" * R%d + R%d -> R%d\n", l.getRow() + 1, row + 1, row + 1);

            double[] coef2 = l.getCoefs();
            for (int i = 0; i < length; i++) {
                coefs[i] += coef2[i] * k;
            }
        }

        void swapWithLine(Line l) {
            System.out.printf("R%d <-> R%d\n", l.getRow() + 1, row + 1);

            for (int i = 0; i < length; i++) {
                double tmp = getCoef(i);
                setCoef(i, l.getCoef(i));
                l.setCoef(i, tmp);
            }

            /*int r = l.getRow();
            l.setRow(row);
            row = r;*/
        }
    }

    private static void swapColumns(int c1, int c2) {
        System.out.printf("C%d <-> C%d\n", c1 + 1, c2 + 1);

        for (Line l : system) {
            double tmp = l.getCoef(c1);
            l.setCoef(c1, l.getCoef(c2));
            l.setCoef(c2, tmp);
        }
        colSwap[c1][c2] = !colSwap[c1][c2];
    }

    public static void main(String[] args) {
        readData(args[1]);

        if (result != null) {
            //Test write input data
            try (PrintWriter pw = new PrintWriter("E:\\test.txt")) {
                for (int i = 0; i < system.length; i++) {
                    pw.println();

                    for (int j = 0; j < Line.length; j++) {
                        pw.print(system[i].getCoef(j));
                        pw.print(" ");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while writing test input data occurs: " + e.getStackTrace());
            }
            //

            solveSystem();

            try (PrintWriter pw = new PrintWriter(args[3])) {
                if (infiniteSolutions) {
                    pw.println("Infinitely many solutions");
                } else if (noSolutions) {
                    pw.println("No solutions");
                } else {
                    for (double r: result) {
                        pw.println(r);
                    }
                }

                System.out.printf("Saved to file %s", args[3]);
            } catch (Exception e) {
                System.out.println("Error while writing result file occurs: " + e.getStackTrace());
            }
        }
    }

    private static void readData(String path) {
        try (Scanner sc = new Scanner(new File(path))) {
            int varCount = sc.nextInt();
            int eqCount = sc.nextInt();

            Line.setLength(varCount + 1);
            system = new Line[eqCount];

            for (int row = 0; row < eqCount; row++) {
                double coefs[] = new double[varCount + 1];
                for (int col = 0; col <= varCount; col++) {
                    coefs[col] = sc.nextDouble();
                }

                system[row] = new Line(coefs, row);
            }

            result = new double[varCount];
            colSwap = new boolean[varCount][varCount];
        } catch (Exception e) {
            System.out.println("Error while reading data occurs: " + e.getStackTrace());
        }
    }

    private static void solveSystem() {
        System.out.println("Start solving the equation.");

        System.out.println("Rows manipulation:");
        for (int r = 0, col = 0, lastCol = 0; r < system.length; r++) {
            double coef1 = system[r].getCoef(col);

            //Find row with coef1 != 0
            if (coef1 != 0) {
                if (lastCol != col) {
                    swapColumns(lastCol, col);
                }
                for (int j = r + 1; j < system.length; j++) {
                    double coef2 = system[j].getCoef(col);
                    if (coef2 != 0) {
                        system[j].addLine(system[r], -coef2 / coef1);
                    }
                }

                col++;
                lastCol = col;
            } else {
                int index = -1;
                for (int j = r + 1; j < system.length; j++) {
                    double coef2 = system[j].getCoef(col);
                    if (coef2 != 0) {
                        index = j;
                        break;
                    }
                }

                if (index != -1) {
                    system[r].swapWithLine(system[index]);
                } else {
                    //Non-zero element not found
                    lastCol = col++;
                }

                if (col >= result.length) {
                    break;
                }

                r--;
            }
        }

        //Analyze if exists full zero row with non zero result
        boolean noSol = false;
        int zeroLinesCount = 0;
        for (int r = 0; r < system.length; r++) {
            Line line = system[r];
            boolean allZero = true;
            for (int c = 0; c < result.length; c++) {
                if (line.getCoef(c) != 0) {
                    allZero = false;
                    break;
                }
            }

            if (allZero) {
                if (line.getResult() == 0) {
                    line.setZeroLine(true);
                    zeroLinesCount++;
                } else {
                    noSol = true;
                    break;
                }
            }
        }
        if (noSol) {
            noSolutions = true;
            System.out.println("No solutions");
            return;
        } else if (system.length - zeroLinesCount < result.length) {
            infiniteSolutions = true;
            System.out.println("Infinitely many solutions");
            return;
        }

        //
        for (int i = 0; i < result.length; i++) {
            for (int j = 1; j < result.length; j++) {
                if (colSwap[i][j]) {
                    swapColumns(i, j);
                }
            }
        }

        for (int i = result.length - 1; i >= 0; i--) {
            double co = system[i].getCoef(i);
            if (co != 1 && co != 0) {
                system[i].mult(1 / co);
            }

            for (int j = 0; j < i; j++) {
                if (system[j].getCoef(i) != 0) {
                    system[j].addLine(system[i], - system[j].getCoef(i) / system[i].getCoef(i));
                }
            }

            result[i] = system[i].getResult();
        }

        System.out.print("The solution is: (");
        System.out.print(result[0]);
        for (int i = 1; i < result.length; i++) {
            System.out.print(", ");
            System.out.print(result[i]);
        }
        System.out.println(")");
    }
}
