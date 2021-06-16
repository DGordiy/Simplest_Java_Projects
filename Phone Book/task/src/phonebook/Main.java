package phonebook;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Person implements Serializable, Comparable {

    static final long SerialVersionUID = 1L;

    private String name;
    private String phone;

    Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return getName().compareTo(((Person) o).getName());
    }
}

class TableEntity<T> {
    private String key;
    private T value;

    TableEntity(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

class HashTable<T> {
    private int size;
    private TableEntity[] table;

    HashTable(int size) {
        this.size = size;
        table = new TableEntity[size];
    }

    public boolean put(String key, T value) {
        int i = findIndex(key);
        if (i == -1) {
            rehash(key.hashCode() / size);
            return put(key, value);
        } else {
            table[i] = new TableEntity(key, value);
        }

        return true;
    }

    public T get(String key) {
        int i = findIndex(key);
        if (i != -1 && table[i] != null) {
            return (T) table[i].getValue();
        }

        return null;
    }

    public int findIndex(String key) {
        int hash = 0;

        for (char c : key.toCharArray()) {
            hash += 10 * c;
        }

        return hash;
    }

    public void rehash(int factor) {
        TableEntity[] oldTable = Arrays.copyOf(table, size);
        size *= factor;
        table = new TableEntity[size];
        for (TableEntity e : oldTable) {
            if (e != null) {
                put(e.getKey(), (T) e.getValue());
            }
        }
    }
}

public class Main {

    static final String SORTED_DIR_PATH = "/Users/Dead/IdeaProjects/Phone Book datafiles/sortedDir.txt";
    static final String DIR_PATH = "/Users/Dead/IdeaProjects/Phone Book datafiles/directory.txt";
    static final String FIND_PATH = "/Users/Dead/IdeaProjects/Phone Book datafiles/find.txt";

    static List<Person> directory;
    static List<String> find;

    static int counter = 0;

    static long startTime = 0;
    static long linearSearchingMillis = 0;

    public static void main(String[] args) throws IOException {

        // Get all the lines from the files into lists
        directory = new ArrayList<>();

        if (Files.exists(Paths.get(SORTED_DIR_PATH))) {
            try (FileInputStream fis = new FileInputStream(SORTED_DIR_PATH); ObjectInputStream ois = new ObjectInputStream(fis)) {
                while (ois.available() > 0) {
                    directory.add((Person) ois.readObject());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (String str : Files.readAllLines(Paths.get(DIR_PATH))) {
                int i = str.indexOf(" ");
                directory.add(new Person(str.substring(i + 1), str.substring(0, i)));
            }
        }

        find = Files.readAllLines(Paths.get(FIND_PATH));

        linearSearchTest();

        System.out.println();
        jumpSearchTest();

        System.out.println();
        binarySearchTest();

        System.out.println();
        hashTableSearchTest();
    }

    public static void printFormatMillis(String prefix, long totalMillis) {
        long minutes = (totalMillis / 1000) / 60;
        long seconds = (totalMillis / 1000) % 60;
        long millis = totalMillis - 1000 * (minutes * 60 + seconds);

        System.out.printf("%s: %d min. %d sec. %d ms.\n", prefix, minutes, seconds, millis);
    }

    public static void linearSearchTest() {
        System.out.println("Start searching (linear search)...");

        // Start time in milliseconds
        startTime = System.currentTimeMillis();

        //SEARCHING
        //counter = 0;
        for (String findLine : find) {
            for (Person p : directory) {
                if (p.getName().contains(findLine)) {
                    counter++;
                    break;
                }
            }
        }

        // Total search time in milliseconds
        linearSearchingMillis = System.currentTimeMillis() - startTime;

        // Output
        System.out.printf("Found %d / %d entries. ", counter, find.size());
        printFormatMillis("Time taken", linearSearchingMillis);
    }

    public static void jumpSearchTest() {
        System.out.println("Start searching (bubble sort + jump search)...");

        // Start time in milliseconds
        startTime = System.currentTimeMillis();

        boolean sortFinished = bubbleSort();

        // Total sort time in milliseconds
        long sortMilliseconds = System.currentTimeMillis() - startTime;

        //SEARCHING
        //counter = 0;
        for (String findLine : find) {
            if (jumpSearch(findLine) != null) {
                counter++;
            }
        }

        // Total time in milliseconds
        long totalMilliseconds = System.currentTimeMillis() - startTime;
        long searchMilliseconds = totalMilliseconds - sortMilliseconds;

        // Output
        System.out.printf("Found %d / %d entries. ", counter, find.size());
        printFormatMillis("Time taken", totalMilliseconds);

        printFormatMillis("Sorting time", sortMilliseconds);

        if (!sortFinished) {
            System.out.print(" - STOPPED, moved to linear search");
        }
        System.out.println();

        printFormatMillis("Searching time", searchMilliseconds);
    }

    //true if sorting was finished
    //false if sorting was broken
    public static boolean bubbleSort() {
        long timeLimit = linearSearchingMillis * 10;
        for (int i = 0; i < directory.size(); i++) {
            for (int j = 0; j < directory.size() - i; j++) {
                if (System.currentTimeMillis() - startTime > timeLimit) {
                    return false;
                }

                if (directory.get(i).compareTo(directory.get(j)) > 0) {
                    Person tmp = directory.get(i);
                    directory.set(i, directory.get(j));
                    directory.set(j, tmp);
                }
            }
        }

        if (!Files.exists(Paths.get(SORTED_DIR_PATH))) {
            try (FileOutputStream fos = new FileOutputStream(SORTED_DIR_PATH); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                for (Person p : directory) {
                    oos.writeObject(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static Person jumpSearch(String targetName) {
        if (directory.get(0).getName().contains(targetName)) {
            return directory.get(0);
        }

        int left = 0;
        int right = 0;
        final int DIR_LEN = directory.size();
        int step = (int) Math.sqrt(DIR_LEN);
        while (right < DIR_LEN - 1) {
            right = Math.min(DIR_LEN - 1, right + step);

            if (directory.get(right).getName().compareTo(targetName) >= 0) {
                break;
            }

            left = right;
        }

        if (right == DIR_LEN - 1 && !directory.get(right).getName().contains(targetName)) {
            return null;
        }

        for (int i = right; i > left; i--) {
            if (directory.get(i).getName().contains(targetName)) {
                return directory.get(i);
            }

            if (directory.get(i).getName().compareTo(targetName) < 0) {
                break;
            }
        }

        return null;
    }

    public static void binarySearchTest() {
        System.out.println("Start searching (quick sort + binary search)...");

        // Start time in milliseconds
        startTime = System.currentTimeMillis();

        quickSort(0, directory.size() - 1);

        // Total sort time in milliseconds
        long sortMilliseconds = System.currentTimeMillis() - startTime;

        //SEARCHING
        //counter = 0;
        for (String findLine : find) {
            if (binarySearch(findLine) != null) {
                counter++;
            }
        }

        // Total time in milliseconds
        long totalMilliseconds = System.currentTimeMillis() - startTime;
        long searchMilliseconds = totalMilliseconds - sortMilliseconds;

        // Output
        System.out.printf("Found %d / %d entries. ", counter, find.size());
        printFormatMillis("Time taken", totalMilliseconds);

        printFormatMillis("Sorting time", sortMilliseconds);

        printFormatMillis("Searching time", searchMilliseconds);
    }

    public static void quickSort(int left, int right) {
        if (left < right) {
            int pivotIndex = partition(left, right);
            quickSort(left, pivotIndex - 1);
            quickSort(pivotIndex + 1, right);
        }
    }

    public static int partition(int left, int right) {
        Person pivot = directory.get(right);

        int partIndex = left;
        for (int i = left; i < right; i++) {
            if (directory.get(i).compareTo(pivot) <= 0) {
                Collections.swap(directory, i, partIndex);
                partIndex++;
            }
        }

        Collections.swap(directory, partIndex, right);

        return partIndex;
    }

    public static Person binarySearch(String targetName) {
        Person result = null;

        int left = 0;
        int right = directory.size() - 1;
        while (left < right) {
            int mid = (left + right) >>> 1;

            if (directory.get(mid).getName().contains(targetName)) {
                result = directory.get(mid);
                break;
            } else if (directory.get(mid).getName().compareTo(targetName) < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    public static void hashTableSearchTest() {
        System.out.println("Start searching (hash table)...");

        // Start time in milliseconds
        startTime = System.currentTimeMillis();

        HashTable<String> ht = new HashTable<>(50_000);
        for (Person p : directory) {
            ht.put(p.getName(), p.getPhone());
        }

        // Total creating time in milliseconds
        long creatingMilliseconds = System.currentTimeMillis() - startTime;

        //SEARCHING
        counter = 0;
        for (String findLine : find) {
            if (ht.get(findLine) != null) {
                counter++;
            }
        }

        // Total time in milliseconds
        long totalMilliseconds = System.currentTimeMillis() - startTime;
        long searchMilliseconds = totalMilliseconds - creatingMilliseconds;

        // Output
        System.out.printf("Found %d / %d entries. ", counter, find.size());
        printFormatMillis("Time taken", totalMilliseconds);

        printFormatMillis("Creating time", creatingMilliseconds);

        System.out.println();

        printFormatMillis("Searching time", searchMilliseconds);
    }


}