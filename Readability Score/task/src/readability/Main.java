package readability;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final Pattern VOWELS_PATTERN = Pattern.compile("([^aeiouy][aeiouy]|^[aeiouy])");

    @Deprecated
    private static String ageNeededAsString(int score) {
        String result;

        switch (score) {
            case 1:
                result = "5-6";
                break;
            case 2:
                result = "6-7";
                break;
            case 3:
                result = "7-9";
                break;
            case 4:
                result = "9-10";
                break;
            case 5:
                result = "10-11";
                break;
            case 6:
                result = "11-12";
                break;
            case 7:
                result = "12-13";
                break;
            case 8:
                result = "13-14";
                break;
            case 9:
                result = "14-15";
                break;
            case 10:
                result = "15-16";
                break;
            case 11:
                result = "16-17";
                break;
            case 12:
                result = "17-18";
                break;
            case 13:
                result = "18-24";
                break;
            default:
                result = "24+";
        }

        return result;
    }

    private static int ageNeededAsInt(double score) {
        int result;

        switch ((int) Math.round(score)) {
            case 1:
                result = 6;
                break;
            case 2:
                result = 7;
                break;
            case 3:
                result = 9;
                break;
            case 4:
                result = 10;
                break;
            case 5:
                result = 11;
                break;
            case 6:
                result = 12;
                break;
            case 7:
                result = 13;
                break;
            case 8:
                result = 14;
                break;
            case 9:
                result = 15;
                break;
            case 10:
                result = 16;
                break;
            case 11:
                result = 17;
                break;
            case 12:
                result = 18;
                break;
            case 13:
                result = 24;
                break;
            default:
                result = 100;
        }

        return result;
    }

    private static double getARI(int sentencesCount, int wordsCount, int charsCount) {
        return 4.71 * charsCount / wordsCount + 0.5 * wordsCount / sentencesCount - 21.43;
    }

    private static double getFK(int sentencesCount, int wordsCount, int syllablesCount) {
        return 0.39 * wordsCount / sentencesCount + 11.8 * syllablesCount / wordsCount - 15.59;
    }

    private static double getSMOG(int sentencesCount, int polysyllablesCount) {
        return 1.043 * Math.sqrt((double)polysyllablesCount * 30 / sentencesCount) + 3.1291;
    }

    private static double getCL(int sentencesCount, int wordsCount, int charsCount) {
        double l = (double) charsCount / wordsCount * 100;
        double s = (double) sentencesCount / wordsCount * 100;

        return 0.0588 * l - 0.296 * s - 15.8;
    }

    private static int syllablesCount(String word) {
        Matcher matcher = VOWELS_PATTERN.matcher(word.toLowerCase());
        long count = matcher.results().count();
        if (word.endsWith("e")) {
            count--;
        }
        return (int) Math.max(count, 1);
    }

    public static void main(String[] args) {
        String text;
        int sentencesCount = 0;
        int wordsCount = 0;
        int charsCount = 0;
        int syllablesCount = 0;
        int polysyllablesCount = 0;

        try (Scanner scanner = new Scanner(new FileReader(args[0]))) {
            text = scanner.nextLine();

            sentencesCount += text.split("[.!?]").length;
            charsCount += text.replaceAll("\\s", "").split("").length;

            String[] words = text.replaceAll("\\d\\,\\d", "11").split("[\\W\\s]+");
            wordsCount += words.length;

            for (String word : words) {
                int syl = syllablesCount(word);
                syllablesCount += syl;
                if (syl > 2) {
                    polysyllablesCount++;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Words: " + wordsCount);
        System.out.println("Sentences: " + sentencesCount);
        System.out.println("Characters: " + charsCount);
        System.out.println("Syllables: " + syllablesCount);
        System.out.println("Polysyllables: " + polysyllablesCount);

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String typeOfScore = new Scanner(System.in).nextLine().toUpperCase();

        double averageAge = 0;

        int age;
        double score;

        int typeCount = 0;
        System.out.println();
        if ("ARI".equals(typeOfScore) || "ALL".equals(typeOfScore)) {
            typeCount++;
            score = getARI(sentencesCount, wordsCount, charsCount);
            age = ageNeededAsInt(score);
            averageAge += age;

            System.out.printf("Automated readability index: %.2f (about %d year olds).\n", score, age);
        }
        if ("FK".equals(typeOfScore) || "ALL".equals(typeOfScore)) {
            typeCount++;
            score = getFK(sentencesCount, wordsCount, syllablesCount);
            age = ageNeededAsInt(score);
            averageAge += age;

            System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d year olds).\n", score, age);
        }
        if ("SMOG".equals(typeOfScore) || "ALL".equals(typeOfScore)) {
            typeCount++;
            score = getSMOG(sentencesCount, polysyllablesCount);
            age = ageNeededAsInt(score);
            averageAge += age;

            System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).\n", score, age);
        }
        if ("CL".equals(typeOfScore) || "ALL".equals(typeOfScore)) {
            typeCount++;
            score = getCL(sentencesCount, wordsCount, charsCount);
            age = ageNeededAsInt(score);
            averageAge += age;

            System.out.printf("Coleman–Liau index: %.2f (about %d year olds).\n", score, age);
        }

        if (typeCount > 0) {
            averageAge /= typeCount;
            System.out.printf("\nThis text should be understood in average by %.2f year olds.\n", averageAge);
        }

        /*
        System.out.printf("The score is: %.2f\n", score);
        System.out.println("This text should be understood by " + ageNeeded((int) Math.ceil(score)) + " year olds.");
        */
    }
}
