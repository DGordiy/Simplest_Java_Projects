package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Calculator {
    public void processStatement(String line) {
        String statement = line.trim();

        if (statement.length() == 0) {
            return;
        }

        if (statement.startsWith("/")) {
            this.handleCommand(statement.substring(1));
        } else if (assignmentMatcher.reset(statement).matches()) {
            this.handleVariableAssignment();
        } else {
            this.handleExpression(statement);
        }
    }

    private final HashMap<String, BigInteger> variables = new HashMap<>();

    private final Matcher variableNameMatcher = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE).matcher("");
    private final Matcher assignmentMatcher = Pattern.compile("([^=\\s]+)\\s*=\\s*(.+)").matcher("");
    private final Matcher tokenMatcher = Pattern.compile("(?<value>[\\w\\d]+)|(?<parenthesis>[()])|(?<operator>-[- ]*|\\+[+ ]*|\\*[* ]*|/[/ ]*|\\^[\\^ ]*)", Pattern.CASE_INSENSITIVE).matcher("");
    private final Matcher invalidCharMatcher = Pattern.compile("[^-a-z0-9()^*/+\\s]", Pattern.CASE_INSENSITIVE).matcher("");
    private final HashMap<String, Integer> precedence = new HashMap<>() {{
        put("+", 1);
        put("-", 1);
        put("*", 2);
        put("/", 2);
        put("^", 3);
    }};

    private void handleCommand(String command) {
        switch (command) {
            case "help":
                System.out.println("The program calculates mathematical expressions");
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    private void handleVariableAssignment() {
        String variableName = assignmentMatcher.group(1);
        if (!variableNameMatcher.reset(variableName).matches()) {
            System.out.println("Invalid identifier");
            return;
        }

        try {
            BigInteger variableValue = calculateExpression(assignmentMatcher.group(2));
            if (variableValue == null) {
                System.out.println("Invalid assignment");
            } else {
                variables.put(variableName, variableValue);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleExpression(String expression) {
        try {
            BigInteger expressionValue = calculateExpression(expression);

            if (expressionValue == null) {
                System.out.println("Invalid expression");
            } else {
                System.out.println(expressionValue);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private BigInteger calculateExpression(String expression) throws Exception {
        ArrayList<String> postfixExpression = convertToPostfix(expression);

        if (postfixExpression == null || postfixExpression.size() == 0) {
            return null;
        }

        Stack<BigInteger> stack = new Stack<>();

        Matcher variableMatcher = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE).matcher("");
        Matcher numberMatcher = Pattern.compile("\\d+").matcher("");

        for (String s: postfixExpression) {
            if (variableMatcher.reset(s).matches()) {
                if (variables.containsKey(s)) {
                    BigInteger value = variables.get(s);
                    stack.push(value);
                    continue;
                } else {
                    throw new Exception("Unknown variable");
                }
            }

            if (numberMatcher.reset(s).matches()) {
                stack.push(new BigInteger(s));
                continue;
            }

            if (precedence.containsKey(s)) {
                BigInteger value2 = stack.pop();
                BigInteger value1 = stack.pop();

                switch (s) {
                    case "^":
                        //stack.push((int) Math.pow(value1, value2));
                        stack.push(value1.pow(value2.intValue()));
                        break;
                    case "*":
                        stack.push(value1.multiply(value2));
                        break;
                    case "/":
                        stack.push(value1.divide(value2));
                        break;
                    case "+":
                        stack.push(value1.add(value2));
                        break;
                    case "-":
                        stack.push(value1.subtract(value2));
                        break;
                }

                continue;
            }

            return null;
        }

        return stack.pop();
    }

    private ArrayList<String> convertToPostfix(String expression) {
        if (invalidCharMatcher.reset(expression).find()) {
            return null;
        }

        ArrayList<String> postfixExpression = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        int parentheses = 0;

        tokenMatcher.reset(expression);

        while (tokenMatcher.find()) {
            String value = tokenMatcher.group("value");
            String parenthesis = tokenMatcher.group("parenthesis");
            String operator = tokenMatcher.group("operator");

            if (value != null) {
                postfixExpression.add(value);
            }

            if (parenthesis != null) {
                if (parenthesis.charAt(0) == '(') {
                    stack.push(parenthesis);
                    parentheses++;
                } else {
                    while (true) {
                        if (stack.empty()) {
                            return null;
                        }
                        String element = stack.pop();
                        if (element.charAt(0) == '(') {
                            break;
                        }
                        postfixExpression.add(element);
                        parentheses--;
                    }
                }
            }

            if (operator != null) {
                operator = operator.replaceAll("\\s+", "");
                if (operator.length() > 1) {
                    switch (operator.charAt(0)) {
                        case '+':
                            operator = "+";
                            break;
                        case '-':
                            operator = operator.length() % 2 == 0 ? "+" : "-";
                            break;
                        default:
                            return null;
                    }
                }

                int operatorPrecedence = precedence.get(operator);

                if (!stack.empty() && stack.peek().charAt(0) != '(' && operatorPrecedence <= precedence.get(stack.peek())) {
                    do {
                        postfixExpression.add(stack.pop());
                    } while (!stack.empty() && stack.peek().charAt(0) != '(' && precedence.get(stack.peek()) >= operatorPrecedence);

                }
                if (postfixExpression.isEmpty()) {
                    postfixExpression.add("0");
                }
                stack.push(operator);
            }
        }

        if (parentheses != 0) {
            return null;
        }

        while (!stack.empty()) {
            postfixExpression.add(stack.pop());
        }

        return postfixExpression;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();

        while (!scanner.hasNext("/exit")) {
            calculator.processStatement(scanner.nextLine());
        }

        System.out.println("Bye!");
    }
}