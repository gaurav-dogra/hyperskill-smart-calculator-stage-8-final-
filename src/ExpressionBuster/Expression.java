package ExpressionBuster;

import java.util.*;

public class Expression {

    private final Map<String, Integer> mapOfVariables;
    private final Deque<String> postfix;
    private static final String INVALID_IDENTIFIER = "Invalid identifier";
    private static final String INVALID_ASSIGNMENT = "Invalid assignment";
    private static final String UNKNOWN_VARIABLE = "Unknown variable";
    private static final String INVALID_EXPRESSION = "Invalid expression";

    public Expression() {
        mapOfVariables = new HashMap<>();
        postfix = new ArrayDeque<>();
    }

    public String compute(String expression) {
        expression = expression.replaceAll("\\s+", "");
        expression = expression.replaceAll("--", "+");
        expression = expression.replaceAll("\\++", "+");
        expression = expression.replaceAll("\\+-", "-");
        expression = expression.replaceAll("-+", "-");

        //System.out.println("expression = " + expression);

        // more than one consecutive / or * make the expression INVALID
        if (expression.matches(".*(//)+.*|.*(\\*\\*)+.*")) {
            return INVALID_EXPRESSION;
        }

        if (expression.matches(".*=.*")) {
            // error checking assignment expression
            return updateMapOfVariables(expression);
        }

        // if input is an integer or a known variable
        if (integerValueOf(expression) != null) {
            return integerValueOf(expression) + "";
        } else if (expression.matches("[a-zA-Z]+")) { // legit variable name but not in map
            return UNKNOWN_VARIABLE;
        }

        if (!checkBrackets(expression)) {
            return INVALID_EXPRESSION;
        }

        if (!convertToPostfix(expression)) {
            return null; // return null on error
        }

        return computePostfix();
    }

    private String computePostfix() {
        Deque<Integer> deque = new ArrayDeque<>();
        //System.out.println("postfix = " + postfix);
        for (String token : postfix) {
            if (token.matches("[+\\-*^/]")) {
                Integer valueTwo = deque.removeLast();
                Integer valueOne = deque.removeLast();

                switch (token) {
                    case "+":
                        deque.add(valueOne + valueTwo);
                        break;
                    case "-":
                        deque.add(valueOne - valueTwo);
                        break;
                    case "*":
                        deque.add(valueOne * valueTwo);
                        break;
                    case "/":
                        deque.add(valueOne / valueTwo);
                        break;
                    case "^":
                        deque.add((int)Math.pow(valueOne, valueTwo));
                        break;
                }
            } else {
                deque.add(integerValueOf(token));
            }
        }
        postfix.clear();
        return deque.removeLast()+"";
    }

    private Integer integerValueOf(String rightSide) {
        if (mapOfVariables.get(rightSide) != null) {
            return mapOfVariables.get(rightSide);
        } else {
            try {
                return Integer.parseInt(rightSide);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private String updateMapOfVariables(String expression) {
        String leftSide = expression.substring(0, expression.indexOf('='));
        String rightSide = expression.substring(expression.indexOf("=")+1);

        // leftSide can only have a variable and variable name can only contain alphabets
        if (!leftSide.matches("[A-Za-z]+")) {
            return INVALID_IDENTIFIER;
        }
        // if correct variable name but not in map, and not an Integer
        if(rightSide.matches("[A-Za-z]+") && !checkMap(rightSide) && !isInteger(rightSide)) {
            return UNKNOWN_VARIABLE;
        }

        if(!rightSide.matches("[A-Za-z]+|-?\\d+")) {
            // check if right side can be solved as an expression
            Expression rightSideAsExpression = new Expression();
            String result = rightSideAsExpression.compute(rightSide);
            try {
                Integer.parseInt(result);
                rightSide = result;
            } catch (NumberFormatException e) {
                return INVALID_ASSIGNMENT;
            }
        }

        mapOfVariables.put(leftSide, integerValueOf(rightSide));
        return null; // return null if all is well

    }

    private boolean isInteger(String rightSide) {
        try {
            Integer.parseInt(rightSide);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkMap(String rightSide) {
        return mapOfVariables.get(rightSide) != null;
    }

    private boolean convertToPostfix(String inputExpression) {

        String expression = inputExpression.replaceAll("\\s+", "");
        // split the expression on delimiters +, -, /, *, ^, (, ), the results also keep the delimiter
        // e.g. (a + b) * c is { (, a, +, b, ), *, c }
        String[] array = expression.split("(?<=[+\\-*/()^])|(?=[+\\-*/()^])");

        // it is not a valid expression e.g. a2a
        if (array.length == 1) {
            return false;
        }

        Deque<String> deque = new ArrayDeque<>();

        for (String expressionToken : array) {
            if (expressionToken.matches("[A-Za-z0-9]+")) { // variable or number
                postfix.add(expressionToken);
            } else if (expressionToken.equals("(") || expressionToken.equals("^")) { // highest priority added to stack
                deque.add(expressionToken);
            } else if (expressionToken.equals(")")) { // pop all till you reach "("
                while (!deque.isEmpty()) {
                    String stackTop = deque.removeLast();
                    if (stackTop.equals("(")) {
                        break;
                    } else {
                        postfix.add(stackTop);
                    }
                }
            } else { // for +, -, *, /
                while (!deque.isEmpty()) {
                    String stackTop = deque.peekLast();
                    if (getPriority(expressionToken) == getPriority(stackTop)) {
                        postfix.add(deque.removeLast()); // stackTop goes to postfix
                    } else if (getPriority(stackTop) < getPriority(expressionToken)) {
                        break;
                    } else {
                        postfix.add(deque.removeLast());
                    }
                }
                deque.add(expressionToken);
            }

        }
        while (!deque.isEmpty()) { // emptying the stack
            String dequeElement = deque.removeLast();
            if (!dequeElement.equals("(")) {
                postfix.add(dequeElement);
            }
        }
        return true;
    }

    private static int getPriority(String s) {

        switch (s) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    private boolean checkBrackets(String expression) {
        // returns false if -> no of closing brackets != no. of opening brackets
        // returns false if -> there is closing bracket before corresponding opening bracket
        int countOpeningBrackets = countCharacter(expression, '(');
        int countClosingBrackets = countCharacter(expression, ')');
        return countOpeningBrackets == countClosingBrackets && checkOrderOfBrackets(expression);
    }

    private boolean checkOrderOfBrackets(String expression) {
        // returns false if -> there is closing bracket before corresponding opening bracket
        int openingBrackets = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                openingBrackets++;
            } else if (expression.charAt(i) == ')') {
                openingBrackets--;
                if (openingBrackets < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int countCharacter(String expression, Character character) {
        int sum = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == character) {
                sum++;
            }
        }
        return sum;
    }
}