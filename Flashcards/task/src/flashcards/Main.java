package flashcards;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class Deck {
    Map<String, String> cards;
    Map<String, String> termsByAnswers;
    Map<String, Integer> mistakes;
    List<String> inOut;

    final Scanner scanner;

    Deck() {
        this.scanner = new Scanner(System.in);
        this.cards = new LinkedHashMap<>();
        this.termsByAnswers = new HashMap<>();
        this.mistakes = new HashMap<>();
        this.inOut = new ArrayList<>();
    }

    private void printOutString(String str) {
        inOut.add(str);
        System.out.println(str);
    }

    private String printInString() {
        String str = scanner.nextLine();
        inOut.add("> " + str);

        return str;
    }

    public void start() {
        String command;
        do {
            printOutString("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            command = printInString().trim().toLowerCase();

            switch (command) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importCards();
                    break;
                case "export":
                    exportCards();
                    break;
                case "ask":
                    askUser();
                    break;
                case "exit":
                    return;
                case "log":
                    saveLog();
                    break;
                case "hardest card":
                    showHardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    printOutString("Unknown command.");
            }

            printOutString("");
        } while (true);
    }

    private void addCard() {
        printOutString("The card:");
        String term = printInString();
        if (cards.containsKey(term)) {
            printOutString("The card \"" + term + "\" already exists.");
            return;
        }

        printOutString("The definition of the card:");
        String def = printInString();
        if (cards.containsValue(def)) {
            printOutString("The definition \"" + def + "\" already exists.");
            return;
        }

        cards.put(term, def);
        termsByAnswers.put(def, term);

        printOutString(String.format("The pair (\"%s\":\"%s\") has been added.", term, def));
    }

    private void removeCard() {
        printOutString("The card:");
        String term = printInString();
        if (cards.containsKey(term)) {
            termsByAnswers.remove(cards.remove(term));
            mistakes.remove(term);
            printOutString("The card \"" + term + "\" has been removed.");
        } else {
            printOutString(String.format("Can't remove \"%s\": there is no such card.", term));
        }
    }

    private void askUser() {
        if (cards.size() == 0) {
            printOutString("No cards in deck.");
            return;
        }

        printOutString("How many times to ask?");
        int timesToAsk = Integer.parseInt(printInString());

        Random random = new Random();
        for (int i = 0; i < timesToAsk; i++) {
            int cardNum = random.nextInt(cards.size());
            String term = "";

            for (String key : cards.keySet()) {
                if (cardNum-- == 0) {
                    term = key;
                    break;
                }
            }

            String def = cards.get(term);
            printOutString("Print the definition of \"" + term + "\":");
            String answer = printInString();
            if (answer.equals(def)) {
                printOutString("Correct answer");
            } else {
                mistakes.put(term, mistakes.getOrDefault(term, 0) + 1);

                if (termsByAnswers.containsKey(answer)) {
                    printOutString("Wrong answer. The correct one is \"" + def + "\", you've just written the definition of \"" + termsByAnswers.get(answer) + "\".");
                } else {
                    printOutString("Wrong answer. The correct one is \"" + def + "\".");
                }
            }
        }
    }

    private void importCards() {
        printOutString("File name:");
        String fileName = printInString();

        importCards(fileName);
    }

    public void importCards(String fileName) {
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            printOutString("File not found.");
            return;
        }

        try {
            List<String> dataLines = Files.readAllLines(path);
            for (int i = 0; i < dataLines.size(); i += 3) {
                String term = dataLines.get(i);
                String def = dataLines.get(i + 1);

                mistakes.put(term, Integer.parseInt(dataLines.get(i + 2)));

                if (cards.containsKey(term)) {
                    termsByAnswers.remove(cards.remove(term));
                }
                cards.put(term, def);
                termsByAnswers.put(def, term);
            }

            printOutString(String.format("%d cards have been loaded.", dataLines.size() / 3));
        } catch (Exception e) {
            printOutString(e.getMessage());
        }
    }

    private void exportCards() {
        printOutString("File name:");
        String fileName = printInString();

        exportCards(fileName);
    }

    public void exportCards(String fileName) {
        Path path = Path.of(fileName);

        try (PrintWriter pw = new PrintWriter(Files.newOutputStream(path))) {
            cards.forEach((k, v) -> {
                pw.println(k);
                pw.println(v);
                pw.println(mistakes.getOrDefault(k, 0));
            });

            printOutString(String.format("%d cards have been saved.", cards.size()));
        } catch (Exception e) {
            printOutString(e.getMessage());
        }
    }

    private void saveLog() {
        printOutString("File name:");
        String fileName = printInString();
        Path path = Path.of(fileName);

        try (PrintWriter pw = new PrintWriter(Files.newOutputStream(path))) {
            printOutString("Log file have been saved.");

            for (String str : inOut) {
                pw.println(str);
            }
        } catch (Exception e) {
            printOutString(e.getMessage());
        }
    }

    private void showHardestCard() {
        if (mistakes.isEmpty()) {
            printOutString("There are no cards with errors.");
        } else {
            int maxMistakesCountByCard = 0;
            for (Integer i : mistakes.values()) {
                maxMistakesCountByCard = Math.max(i, maxMistakesCountByCard);
            }

            if (maxMistakesCountByCard == 0) {
                printOutString("There are no cards with errors.");
                return;
            }

            List<String> hardestCards = new ArrayList<>();
            for (var e : mistakes.entrySet()) {
                if (e.getValue() == maxMistakesCountByCard) {
                    hardestCards.add(e.getKey());
                }
            }

            StringBuilder msg = new StringBuilder();
            if (hardestCards.size() == 1) {
                msg.append("The hardest card is \"" + hardestCards.get(0) + "\". You have " + maxMistakesCountByCard + " errors answering it.");
            } else {
                msg.append("The hardest cards are \"" + hardestCards.get(0) + "\"");
                for (int i = 1; i < hardestCards.size(); i++) {
                    msg.append(", \"" + hardestCards.get(i) + "\"");
                }

                msg.append(". You have " + maxMistakesCountByCard + " errors answering them.");
            }

            printOutString(msg.toString());
        }
    }

    private void resetStats() {
        mistakes.clear();

        printOutString("Card statistics has been reset.");
    }
}

public class Main {
    public static void main(String[] args) {
        String importFileName = null;
        String exportFileName = null;

        for (int i = 0; i < args.length; i++) {
            if ("-import".equals(args[i])) {
                importFileName = args[i + 1];
                i++;
            } else if ("-export".equals(args[i])) {
                exportFileName = args[i + 1];
                i++;
            }
        }

        Deck deck = new Deck();
        if (importFileName != null) {
            deck.importCards(importFileName);
        }
        deck.start();

        System.out.println("Bye bye!");
        if (exportFileName != null) {
            deck.exportCards(exportFileName);
        }

    }
}