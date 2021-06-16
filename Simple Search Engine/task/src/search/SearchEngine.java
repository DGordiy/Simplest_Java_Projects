package search;

import java.io.FileReader;
import java.util.*;

interface SearchingStrategy {
    List<String> findPeople(String strToFind);
}

public class SearchEngine {
    private class SearchAllStrategy implements SearchingStrategy {
        @Override
        public List<String> findPeople(String strToFind) {
            List<String> result = new ArrayList<>();

            String[] wordsToFind = strToFind.toUpperCase().split("\\s+");
            if (invertedIndex.containsKey(wordsToFind[0])) {
                Set<Integer> indexes = invertedIndex.get(wordsToFind[0]);
                for (int ind : indexes) {
                    boolean found = true;
                    for (String word : wordsToFind) {
                        if (!(invertedIndex.containsKey(word) && invertedIndex.get(word).contains(ind))) {
                            found = false;
                            break;
                        }
                    }

                    if (found) {
                        result.add(people.get(ind));
                    }
                }
            }

            return result;
        }
    }

    private class SearchAnyStrategy implements SearchingStrategy {
        @Override
        public List<String> findPeople(String strToFind) {
            List<String> result = new ArrayList<>();

            Set<Integer> indexes = new HashSet<>();
            for (String word : strToFind.toUpperCase().split("\\s+")) {
                if (invertedIndex.containsKey(word)) {
                    indexes.addAll(invertedIndex.get(word));
                }
            }

            for (int ind : indexes) {
                result.add(people.get(ind));
            }

            return result;
        }
    }

    private class SearchNoneStrategy implements SearchingStrategy {
        @Override
        public List<String> findPeople(String strToFind) {
            List<String> result = new ArrayList<>();

            Set<Integer> indexes = new HashSet<>();
            for (int i = 0; i < people.size(); i++) {
                indexes.add(i);
            }

            for (String word : strToFind.toUpperCase().split("\\s+")) {
                if (invertedIndex.containsKey(word)) {
                    for (int ind : invertedIndex.get(word)) {
                        indexes.remove(ind);
                    }
                }
            }

            for (int ind : indexes) {
                result.add(people.get(ind));
            }

            return result;
        }
    }

    private final Scanner scanner = new Scanner(System.in);
    private List<String> people;
    private Map<String, Set<Integer>> invertedIndex;

    SearchEngine() {
        people = new ArrayList<>();
        invertedIndex = new HashMap<>();
    }

    public void setData(String fileName) {
        people.clear();
        invertedIndex.clear();

        try (Scanner sc = new Scanner(new FileReader(fileName))) {
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                for (String word : data.split("\\s+")) {
                    addWordToInvertedIndex(word, people.size());
                }

                people.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void addWordToInvertedIndex(String word, int index) {
        Set<Integer> set = invertedIndex.getOrDefault(word.toUpperCase(), new HashSet<>());
        set.add(index);
        invertedIndex.put(word.toUpperCase(), set);
    }

    public void mainMenu() {
        System.out.println();
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");

        String option = scanner.nextLine();
        switch (option) {
            case "1":
                findPerson();
                break;
            case "2":
                printAll();
                break;
            case "0":
                exit();
                break;
            default:
                System.out.println("Incorrect option! Try again.");
        }

        mainMenu();
    }

    private void exit() {
        scanner.close();

        System.out.println();
        System.out.println("Bye!");
        System.exit(0);
    }

    public void findPerson() {
        SearchingStrategy strategy;

        System.out.println("\nSelect a matching strategy: ALL, ANY, NONE");
        switch (scanner.nextLine().toUpperCase()) {
            case "ALL":
                strategy = new SearchAllStrategy();
                break;
            case "ANY":
                strategy = new SearchAnyStrategy();
                break;
            case "NONE":
                strategy = new SearchNoneStrategy();
                break;
            default:
                System.out.println("Invalid strategy.");
                return;
        }

        System.out.println("\nEnter a name or email to search all suitable people.");
        List<String> found = strategy.findPeople(scanner.nextLine());
        if (found.size() > 0) {
            for (String person : found) {
                System.out.println(person);
            }
        } else {
            System.out.println("No matching people found.");
        }
    }

    public void printAll() {
        System.out.println();
        System.out.println("=== List of people ===");
        for (String person : people) {
            System.out.println(person);
        }
    }

    /*public List<String> findPeople(String strToFind) {
        List<String> result = new ArrayList<>();

        if (invertedIndex.containsKey(strToFind.toUpperCase())) {
            List<Integer> indexes = invertedIndex.get(strToFind.toUpperCase());
            for (Integer ind : indexes) {
                result.add(people.get(ind));
            }
        }

        return result;
    }
    */
}
