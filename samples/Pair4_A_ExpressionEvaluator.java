import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
public class ExpressionEvaluator
{
    private final Map<Character, Integer> priority = new HashMap<>();
    public ExpressionEvaluator()
    {
        priority.put('+', 1);
        priority.put('-', 1);
        priority.put('*', 2);
        priority.put('/', 2);
    }
    public int evaluate(String expression)
    {
        ArrayDeque<Integer> values = new ArrayDeque<>();
        ArrayDeque<Character> ops = new ArrayDeque<>();
        for(int i = 0; i < expression.length(); i++)
        {
            char ch = expression.charAt(i);
            if(Character.isWhitespace(ch))
            {
                continue;
            }
            if(Character.isDigit(ch))
            {
                int number = 0;
                while(i < expression.length() && Character.isDigit(expression.charAt(i)))
                {
                    number = number * 10 + expression.charAt(i) - '0';
                    i++;
                }
                i--;
                values.push(number);
            }
            else if(ch == '(')
            {
                ops.push(ch);
            }
            else if(ch == ')')
            {
                while(ops.peek() != '(')
                {
                    apply(values, ops);
                }
                ops.pop();
            }
            else if(priority.containsKey(ch))
            {
                while(!ops.isEmpty() && ops.peek() != '(' && priority.get(ops.peek()) >= priority.get(ch))
                {
                    apply(values, ops);
                }
                ops.push(ch);
            }
        }
        while(!ops.isEmpty())
        {
            apply(values, ops);
        }
        return values.pop();
    }
    private void apply(ArrayDeque<Integer> values, ArrayDeque<Character> ops)
    {
        int right = values.pop();
        int left = values.pop();
        char op = ops.pop();
        if(op == '+')
        {
            values.push(left + right);
        }
        else if(op == '-')
        {
            values.push(left - right);
        }
        else if(op == '*')
        {
            values.push(left * right);
        }
        else
        {
            values.push(left / right);
        }
    }
    public static void main(String[] args)
    {
        ExpressionEvaluator calc = new ExpressionEvaluator();
        String[] tests = {"8 + 4 * 3", "(8 + 4) * 3", "100 / 5 + 7 * 2", "90 - (10 + 5) * 3"};
        for(String test : tests)
        {
            System.out.println(test + " = " + calc.evaluate(test));
        }
    }
}






