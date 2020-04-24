package ExpressionBuster;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ExpressionTest {
    private Expression expression;
    private final String input;
    private final String output;

    public ExpressionTest(String input, String output) {
        this.input = input;
        this.output = output;
    }

    @org.junit.Before
    public void setup() {
        expression = new Expression();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testConditions() {
        return Arrays.asList(new Object[][] {
                {"8 * 3 + 12 * (4 - 2)", "48"},
                {"2 - 2 + 3", "3"},
                {"4 * (2 + 3", "Invalid expression"},
                {"-10", "-10"},
                {"1 +++ 2 * 3 -- 4", "11"},
                {"3 *** 5", "Invalid expression"},
                {"2^2", "4"},
                {"2*2^3", "16"},
                {"a1 = 8", "Invalid identifier"},
                {"n = a2a", "Invalid assignment"},
                {"a = 7 = 8", "Invalid assignment"},
                {"e", "Unknown variable"},
                {"c = 10 + 5", null},
                {"112234567890 + 112234567890 * (10000000999 - 999)", "1122345679012234567890"}
                // I don't know how to test the below
                //                a=4
                //                b=5
                //                c=6
                //                a*2+b*3+c*(2+3)
        });

    }

    @org.junit.Test
    public void computeTest() {
        String result = expression.compute(input);
        assertEquals(output, result);
    }
}