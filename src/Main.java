import ExpressionBuster.Expression;

import java.util.Scanner;

class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Expression expression = new Expression();

        label:
        while (true) {
            String input = scanner.nextLine();

            switch (input) {
                case "/exit":
                    System.out.println("Bye!");
                    break label;
                case "/help":
                    System.out.println("this application supports BigInteger values, variables, brackets, addition, subtraction, multiplication" +
                            "division and power");
                    break;
                default:
                    if (input.matches("")) {
                        break;
                    } else if (input.matches("/.*")) {
                        System.out.println("Unknown command");
                        break;
                    } else {
                        String result = expression.compute(input);
                        if (result != null) {
                            System.out.println(result);
                        }
                    }
            }
        }
    }
}