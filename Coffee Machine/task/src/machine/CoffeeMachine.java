package machine;

import java.util.Scanner;

enum MachineStates {
    CHOOSING_ACTION,
    BUYING_COFFEE,
    MAKING_COFFEE,
    FILLING_MACHINE,
    TAKING_MONEY,
    STOP
}

public class CoffeeMachine {
    static Scanner scanner = new Scanner(System.in);

    final static int CUP_NEEDS_WATER = 200;
    final static int CUP_NEEDS_MILK = 50;
    final static int CUP_NEEDS_COFFEE = 15;

    static int water = 400;
    static int milk = 540;
    static int coffee = 120;
    static int cups = 9;
    static int money = 550;

    static MachineStates state;

    public static void main(String[] args) {
        selectAction();
    }

    private static void selectAction() {
        state = MachineStates.CHOOSING_ACTION;

        System.out.println();
        System.out.println("Write action (buy, fill, take, remaining, exit):");

        action(scanner.nextLine());
    }

    private static void action(String action) {
        switch (state) {
            case CHOOSING_ACTION:
                switch (action) {
                    case "buy":
                        buyCoffee();
                        break;
                    case "fill":
                        fillMachine();
                        break;
                    case "take":
                        takeMoney();
                        break;
                    case "remaining":
                        printStatus();
                        break;
                    case "exit":
                        state = MachineStates.STOP;
                        break;
                    default:
                        System.out.println("Incorrect action selected!");
                }
                break;
            case BUYING_COFFEE:
                switch (action) {
                    case "1":
                        makeCoffee(250, 0, 16, 4);
                        break;
                    case "2":
                        makeCoffee(350, 75, 20, 7);
                        break;
                    case "3":
                        makeCoffee(200, 100, 12, 6);
                        break;
                    case "back":
                        break;
                    default:
                        System.out.println("Incorrect action selected!");
                }
                break;
            default:
                state = MachineStates.CHOOSING_ACTION;
        }

        if (state != MachineStates.STOP) {
            state = MachineStates.CHOOSING_ACTION;
            selectAction();
        }
    }

    private static void printStatus() {
        System.out.println("\nThe coffee machine has:");
        System.out.printf("%d of water\n", water);
        System.out.printf("%d of milk\n", milk);
        System.out.printf("%d of coffee beans\n", coffee);
        System.out.printf("%d of disposable cups\n", cups);
        System.out.printf("%d of money\n", money);
    }

    private static void buyCoffee() {
        state = MachineStates.BUYING_COFFEE;

        System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
        action(scanner.nextLine());
    }

    private static void fillMachine() {
        state = MachineStates.FILLING_MACHINE;

        System.out.println("Write how many ml of water do you want to add:");
        water += scanner.nextInt();

        System.out.println("Write how many ml of milk do you want to add:");
        milk += scanner.nextInt();

        System.out.println("Write how many grams of coffee beans do you want to add:");
        coffee += scanner.nextInt();

        System.out.println("Write how many disposable cups of coffee do you want to add:");
        cups += scanner.nextInt();
    }

    private static void takeMoney() {
        state = MachineStates.TAKING_MONEY;

        System.out.printf("I gave you $%d\n", money);
        money = 0;
    }

    //Kinds of coffee
    private static void makeCoffee(int needWater, int needMilk, int needCoffee, int needMoney) {
        state = MachineStates.MAKING_COFFEE;

        if (cups == 0) {
            System.out.println("No cups in coffee machine!");
        } else if (water < needWater || milk < needMilk || coffee < needCoffee) {
            System.out.println("Sorry, not enough resurces, making you a coffee!");
        } else {
            water -= needWater;
            milk -= needMilk;
            coffee -= needCoffee;
            money += needMoney;

            cups--;
        }
    }

}
