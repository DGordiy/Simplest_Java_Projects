package budget;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

class Purchase {
    private final String name;
    private final String category;
    private final double price;

    static final List<String> CATEGORIES = List.of("Food", "Clothes", "Entertainment", "Other");

    public Purchase(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s $%.2f", name, price);
    }

}

class Wallet {
    double balance;
    List<Purchase> purchases;
    private final Scanner scanner;

    Wallet(Scanner scanner) {
        this.purchases = new ArrayList<>();
        this.scanner = scanner;
    }

    public void addIncome() {
        System.out.println("Enter income:");

        balance += Double.parseDouble(scanner.nextLine());

        System.out.println("Income was added!\n");
    }

    public void addPurchase() {
        System.out.println("Choose the type of purchase");

        int index = 0;
        for (String type : Purchase.CATEGORIES) {
            System.out.println(String.format("%d) %s", ++index, type));
        }
        System.out.println(String.format("%d) Back", ++index));

        //Selection of type, than name and price
        int selectionIndex = Integer.parseInt(scanner.nextLine());

        System.out.println();
        if (selectionIndex < index) {
            System.out.println("Enter purchase name:");
            String name = scanner.nextLine();
            System.out.println("Enter its price:");
            double price = Double.parseDouble(scanner.nextLine());

            purchases.add(new Purchase(name, Purchase.CATEGORIES.get(selectionIndex - 1), price));
            balance -= price;

            System.out.println("Purchase was added!\n");

            addPurchase();
        }
    }

    public void showPurchases() {
        if (purchases.isEmpty()) {
            System.out.println("Purchase list is empty!\n");
            return;
        }

        System.out.println("Choose the type of purchases");
        int index = 0;
        for (String type : Purchase.CATEGORIES) {
            System.out.println(String.format("%d) %s", ++index, type));
        }
        System.out.println(String.format("%d) All", ++index));
        System.out.println(String.format("%d) Back", ++index));

        //Selection of type
        int selectionIndex = Integer.parseInt(scanner.nextLine());
        System.out.println();
        if (selectionIndex < index) {
            showPurchasesByCategory(selectionIndex < index - 2 ? selectionIndex - 1 : -1);

            System.out.println();
            showPurchases();
        }
    }

    public void showBalance() {
        System.out.printf("Balance: $%.2f\n\n", balance);
    }

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new File("purchases.txt"))) {
            pw.println(balance);
            for (var purchase : purchases) {
                pw.println(purchase.getCategory());
                pw.println(purchase.getName());
                pw.println(purchase.getPrice());
            }

            System.out.println("Purchases were saved!\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try (Scanner scanner = new Scanner(new File("purchases.txt"))) {
            purchases.clear();

            balance = Double.parseDouble(scanner.nextLine());
            while (scanner.hasNextLine()) {
                String category = scanner.nextLine();
                String name = scanner.nextLine();
                double price = Double.parseDouble(scanner.nextLine());

                purchases.add(new Purchase(name, category, price));
            }

            System.out.println("Purchases were loaded!\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void analyzePurchases() {
        System.out.println("How do you want to sort?");
        System.out.println("1) Sort all purchases");
        System.out.println("2) Sort by type");
        System.out.println("3) Sort certain type");
        System.out.println("4) Back");

        int selectionIndex = Integer.parseInt(scanner.nextLine());
        if (selectionIndex < 4) {
            showSortedPurchases(selectionIndex);
            analyzePurchases();
        } else {
            System.out.println();
        }

    }

    private void showPurchasesByCategory(int categoryIndex) {
        String category = null;
        if (categoryIndex >= 0) {
            category = Purchase.CATEGORIES.get(categoryIndex);
            System.out.println(category + ":");
        } else {
            System.out.println("All:");
        }

        double total = 0;
        for (var purchase : purchases) {
            if (category == null || purchase.getCategory().equals(category)) {
                System.out.println(purchase);
                total += purchase.getPrice();
            }
        }

        if (total == 0) {
            System.out.println("Purchase list is empty!\n");
        } else {
            System.out.println(String.format("Total sum: $%.2f\n", total));
        }
    }

    private void showPurchasesGroupedByCategory() {
        Map<String, Double> groups = new HashMap<>();

        for (Purchase purchase : purchases) {
            groups.put(purchase.getCategory(), groups.getOrDefault(purchase.getCategory(), 0d) + purchase.getPrice());
        }

        System.out.println(String.format("%s - $%.2f", "Food", groups.get("Food"), groups.getOrDefault("Food", 0d)));
        System.out.println(String.format("%s - $%.2f", "Entertainment", groups.get("Entertainment"), groups.getOrDefault("Entertainment", 0d)));
        System.out.println(String.format("%s - $%.2f", "Clothes", groups.get("Clothes"), groups.getOrDefault("Clothes", 0d)));
        System.out.println(String.format("%s - $%.2f", "Other", groups.get("Other"), groups.getOrDefault("Other", 0d)));
        System.out.println();
    }

    private void showSortedPurchases(int sortType) {
        System.out.println();

        purchases.sort(Comparator.comparing(Purchase::getPrice).reversed());

        switch (sortType) {
            case 1:
                showPurchasesByCategory(-1);
                break;
            case 2:
                showPurchasesGroupedByCategory();
                break;
            case 3:
                System.out.println("Choose the type of purchases");
                int index = 0;
                for (String type : Purchase.CATEGORIES) {
                    System.out.println(String.format("%d) %s", ++index, type));
                }

                int selectionIndex = Integer.parseInt(scanner.nextLine());
                System.out.println();

                showPurchasesByCategory(selectionIndex - 1);
                break;
            default:
        }

    }
}

class BudgetManager {
    private final Scanner scanner;
    Wallet wallet;

    BudgetManager() {
        scanner = new Scanner(System.in);
        wallet = new Wallet(scanner);
    }

    public void start() {
        selectAction();
    }

    public void selectAction() {
        System.out.println("Choose your action:");
        System.out.println("1) Add income");
        System.out.println("2) Add purchase");
        System.out.println("3) Show list of purchases");
        System.out.println("4) Balance");
        System.out.println("5) Save");
        System.out.println("6) Load");
        System.out.println("7) Analyze (Sort)");
        System.out.println("0) Exit");

        int choise = Integer.parseInt(scanner.nextLine());

        System.out.println();

        switch (choise) {
            case 1:
                wallet.addIncome();
                break;
            case 2:
                wallet.addPurchase();
                break;
            case 3:
                wallet.showPurchases();
                break;
            case 4:
                wallet.showBalance();
                break;
            case 5:
                wallet.saveToFile();
                break;
            case 6:
                wallet.loadFromFile();
                break;
            case 7:
                wallet.analyzePurchases();
                break;
            default:
                System.out.println("Bye!");
                return;
        }

        selectAction();
    }
}

public class Main {
    public static void main(String[] args) {
        BudgetManager budgetManager = new BudgetManager();
        budgetManager.start();
    }
}