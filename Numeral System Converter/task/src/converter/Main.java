package converter;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static char[] NUMBERS = new char[36];

    private static void init() {
        //Initialization
        for (int i = 0; i < 10; i++) {
            NUMBERS[i] = (char) ('0' + i);
        }
        for (int i = 10; i < 36; i++) {
            NUMBERS[i] = (char) ('a' + i - 10);
        }
    }

    //Calculate with array and loop
    private static String decToNumSys(int dec, int radix) {


        //Convert number
        char[] sysChArray = new char[100];
        int digits = 0;

        while (dec > 0) {
            sysChArray[digits++] = NUMBERS[dec % radix];
            dec /= radix;
        }

        for (int i = 0; i < digits / 2; i++) {
            char tmp = sysChArray[i];
            sysChArray[i] = sysChArray[digits - i - 1];
            sysChArray[digits - i - 1] = tmp;
        }

        //Making result
        String r = new String(sysChArray, 0, digits);
        String prefix;
        switch (radix) {
            case 2:
                prefix = "0b";
                break;
            case 8:
                prefix = "0";
                break;
            case 16:
                prefix = "0x";
                break;
            default:
                prefix = "";
        }

        return prefix + r;
    }

    //Calculate with built-in functions
    private static String numToOtherSys(String num, int radix1, int radix2) {
        int intNum = 0;

        if (radix1 == 1) {
            intNum = num.length();
        } else {
            intNum = Integer.parseInt(num, radix1);
        }

        if (radix2 == 1) {
            char[] result = new char[intNum];
            Arrays.fill(result, '1');

            return new String(result);
        } else {
            return Integer.toString(intNum, radix2);
        }
    }

    //Calculate fractional
    private static String fracToRadix(String num, int radix1, int radix2) {
        String[] parts = num.split("\\.", -1);
        String intPart = numToOtherSys(parts[0], radix1, radix2);

        if (parts.length > 1 && !"0".equals(parts[1])) {
            String fracStr = parts[1].toLowerCase();
            double fracDEC;

            //Convert frac to DEC
            if (radix1 == 10) {
                fracDEC = Double.parseDouble(fracStr);
            } else {
                double frac = 0;
                long j = radix1;
                for (int i = 0; i < fracStr.length(); i++, j *= radix1) {
                    char c = fracStr.charAt(i);
                    frac += (double) (c >= 'a' ? 10 + (c - 'a') : c - '0') / j;
                }
                fracDEC = frac;
            }

            while (fracDEC >= 1) {
                fracDEC /= 10;
            }

            //
            String fracPart;
            if (radix2 == 10) {
                fracPart = Integer.toString((int) (fracDEC * 10000));
            } else {
                char[] frac2 = new char[5];
                for (int i = 0; i < 5; i++) {
                    fracDEC *= radix2;
                    int fracInt = (int) fracDEC;
                    frac2[i] = NUMBERS[fracInt];
                    fracDEC -= fracInt;
                }
                fracPart = new String(frac2);
            }

            return intPart + "." + fracPart;
        } else {
            return intPart;
        }
    }

    public static void main(String[] args) {
        init();
        Scanner scanner = new Scanner(System.in);

        try {
            int radix1 = scanner.nextInt();
            String num = scanner.next();
            int radix2 = scanner.nextInt();

            if (radix1 < 1 || radix1 > 36 || radix2 < 1 || radix2 > 36) {
                System.out.println("Error: incorrect radix");
            } else {
                System.out.println(fracToRadix(num, radix1, radix2));
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: incorrect numbers");
        } catch (NoSuchElementException e) {
            System.out.println("Error: incorrect count of input data");
        }
    }
}
