import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
public class CalculatorParser
{
    private final Map<Character, Integer> precedence = new HashMap<>();
    public CalculatorParser()
    {
        precedence.put('+', 1);
        precedence.put('-', 1);
        precedence.put('*', 2);
        precedence.put('/', 2);
    }
    public int solve(String input)
    {
        ArrayDeque<Integer> numbers = new ArrayDeque<>();
        ArrayDeque<Character> symbols = new ArrayDeque<>();
        for(int index = 0; index < input.length(); index++)
        {
            char token = input.charAt(index);
            if(Character.isWhitespace(token))
            {
                continue;
            }
            if(Character.isDigit(token))
            {
                int value = 0;
                while(index < input.length() && Character.isDigit(input.charAt(index)))
                {
                    value = value * 10 + input.charAt(index) - '0';
                    index++;
                }
                index--;
                numbers.push(value);
            }
            else if(token == '(')
            {
                symbols.push(token);
            }
            else if(token == ')')
            {
                while(symbols.peek() != '(')
                {
                    reduce(numbers, symbols);
                }
                symbols.pop();
            }
            else if(precedence.containsKey(token))
            {
                while(!symbols.isEmpty() && symbols.peek() != '(' && precedence.get(symbols.peek()) >= precedence.get(token))
                {
                    reduce(numbers, symbols);
                }
                symbols.push(token);
            }
        }
        while(!symbols.isEmpty())
        {
            reduce(numbers, symbols);
        }
        return numbers.pop();
    }
    private void reduce(ArrayDeque<Integer> numbers, ArrayDeque<Character> symbols)
    {
        int second = numbers.pop();
        int first = numbers.pop();
        char symbol = symbols.pop();
        if(symbol == '+')
        {
            numbers.push(first + second);
        }
        else if(symbol == '-')
        {
            numbers.push(first - second);
        }
        else if(symbol == '*')
        {
            numbers.push(first * second);
        }
        else
        {
            numbers.push(first / second);
        }
    }
    public static void main(String[] args)
    {
        CalculatorParser parser = new CalculatorParser();
        String[] examples = {"8 + 4 * 3", "(8 + 4) * 3", "100 / 5 + 7 * 2", "90 - (10 + 5) * 3"};
        for(String example : examples)
        {
            System.out.println(example + " = " + parser.solve(example));
        }
    }
}






