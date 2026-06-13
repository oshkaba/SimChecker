import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// Converts source lines into normalized tokens.
// The tokenizer supports common syntax from Java, C, C++, Python, and JavaScript.
// Identifiers, numbers, strings, and chars are normalized so renamed code can still match.
public class Tokenizer
{
    // Keywords are kept as themselves instead of being changed to ID.
    private static final Set<String> KEYWORDS = new HashSet<String>();

    // Fill the keyword set once when the class loads.
    static
    {
        addJavaKeywords();
        addCKeywords();
        addCppKeywords();
        addPythonKeywords();
        addJavaScriptKeywords();
    }

    // Tokenizes all cleaned source lines.
    public static List<Token> tokenize(List<String> lines)
    {
        List<Token> tokens = new ArrayList<Token>();

        for(int lineIndex = 0; lineIndex < lines.size(); lineIndex++)
        {
            String line = lines.get(lineIndex);
            int lineNumber = lineIndex + 1;
            int i = 0;

            while(i < line.length())
            {
                char current = line.charAt(i);
                // Ignore whitespace because spacing should not affect similarity.

                if(Character.isWhitespace(current))
                {
                    i++;
                }
                // Normalize double-quoted strings to STR.
                else if(current == '"')
                {
                    i = readQuotedText(line, i, '"');
                    tokens.add(new Token("STR", lineNumber));
                }
                // Normalize single-quoted chars or strings to STR.
                else if(current == '\'')
                {
                    i = readQuotedText(line, i, '\'');
                    tokens.add(new Token("STR", lineNumber));
                }
                // Normalize JavaScript template strings to STR.
                else if(current == '`')
                {
                    i = readQuotedText(line, i, '`');
                    tokens.add(new Token("STR", lineNumber));
                }
                // Keep keywords, but normalize variable and function names to ID.
                else if(isIdentifierStart(current))
                {
                    int start = i;
                    i++;

                    while(i < line.length() && isIdentifierPart(line.charAt(i)))
                    {
                        i++;
                    }

                    String word = line.substring(start, i);

                    if(KEYWORDS.contains(word))
                    {
                        tokens.add(new Token(word, lineNumber));
                    }
                    else
                    {
                        tokens.add(new Token("ID", lineNumber));
                    }
                }
                // Normalize every number literal to NUM.
                else if(Character.isDigit(current))
                {
                    i = readNumber(line, i);
                    tokens.add(new Token("NUM", lineNumber));
                }
                else
                {
                    // Operators and punctuation are kept because they describe code structure.
                    String operator = readOperator(line, i);
                    tokens.add(new Token(operator, lineNumber));
                    i += operator.length();
                }
            }
        }
        return tokens;
    }

    // Skips over a quoted string, char, or template literal, including escaped characters.
    private static int readQuotedText(String line, int start, char quote)
    {
        int i = start + 1;
        while(i < line.length())
        {
            char current = line.charAt(i);
            if(current == '\\' && i + 1 < line.length())
            {
                i += 2;
            }
            else if(current == quote)
            {
                return i + 1;
            }
            else
            {
                i++;
            }
        }
        return i;
    }

    // Skips over a number literal.
    private static int readNumber(String line, int start)
    {
        int i = start;

        while(i < line.length())
        {
            char current = line.charAt(i);
            if(Character.isLetterOrDigit(current) || current == '_' || current == '.')
            {
                i++;
            }
            else
            {
                break;
            }
        }
        return i;
    }

    // Reads the longest supported operator starting at the current index.
    private static String readOperator(String line, int start)
    {
        String[] operators =
        {
                "...", "===", "!==", ">>>=", "<<=", ">>=", "**=", "//=", ":=",
                ">>>", "==", "!=", "<=", ">=", "&&", "||", "++", "--", "+=", "-=",
                "*=", "/=", "%=", "&=", "|=", "^=", "<<", ">>", "::", "->", "=>",
                "**", "//", "and", "or", "not"
        };
        for(String operator : operators)
        {
            if(start + operator.length() <= line.length())
            {
                String part = line.substring(start, start + operator.length());
                if(part.equals(operator))
                {
                    return operator;
                }
            }
        }
        return String.valueOf(line.charAt(start));
    }

    // Checks whether a character can start an identifier in supported languages.
    private static boolean isIdentifierStart(char value)
    {
        return Character.isLetter(value) || value == '_' || value == '$';
    }

    // Checks whether a character can continue an identifier in supported languages.
    private static boolean isIdentifierPart(char value)
    {
        return Character.isLetterOrDigit(value) || value == '_' || value == '$';
    }

    // Adds all values from an array to the keyword set.
    private static void addKeywords(String[] words)
    {
        for(String word : words)
        {
            KEYWORDS.add(word);
        }
    }

    // Adds common Java keywords.
    private static void addJavaKeywords()
    {
        addKeywords(new String[]
        {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "instanceof", "int", "interface", "long", "native", "new", "package",
                "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
                "volatile", "while", "true", "false", "null"
        });
    }

    // Adds common C keywords.
    private static void addCKeywords()
    {
        addKeywords(new String[]
        {
                "auto", "break", "case", "char", "const", "continue", "default", "do",
                "double", "else", "enum", "extern", "float", "for", "goto", "if", "inline",
                "int", "long", "register", "restrict", "return", "short", "signed", "sizeof",
                "static", "struct", "switch", "typedef", "union", "unsigned", "void", "volatile",
                "while", "_Bool", "_Complex", "_Imaginary"
        });
    }

    // Adds common C++ keywords.
    private static void addCppKeywords()
    {
        addKeywords(new String[]
        {
                "alignas", "alignof", "and", "and_eq", "asm", "bitand", "bitor", "bool",
                "catch", "char16_t", "char32_t", "class", "compl", "concept", "constexpr", "const_cast",
                "decltype", "delete", "dynamic_cast", "explicit", "export", "false", "friend", "mutable",
                "namespace", "new", "noexcept", "not", "not_eq", "nullptr", "operator", "or",
                "or_eq", "private", "protected", "public", "reinterpret_cast", "requires", "static_assert",
                "static_cast", "template", "this", "thread_local", "throw", "true", "try", "typename",
                "using", "virtual", "wchar_t", "xor", "xor_eq"
        });
    }

    // Adds common Python keywords and built-in constants.
    private static void addPythonKeywords()
    {
        addKeywords(new String[]
        {
                "False", "None", "True", "and", "as", "assert", "async", "await", "break",
                "class", "continue", "def", "del", "elif", "else", "except", "finally", "for",
                "from", "global", "if", "import", "in", "is", "lambda", "nonlocal", "not",
                "or", "pass", "raise", "return", "try", "while", "with", "yield"
        });
    }

    // Adds common JavaScript keywords.
    private static void addJavaScriptKeywords()
    {
        addKeywords(new String[]
        {
                "await", "break", "case", "catch", "class", "const", "continue", "debugger", "default",
                "delete", "do", "else", "export", "extends", "false", "finally", "for", "function",
                "if", "import", "in", "instanceof", "let", "new", "null", "return", "super",
                "switch", "this", "throw", "true", "try", "typeof", "undefined", "var", "void",
                "while", "with", "yield"
        });
    }
    
    // Stores one normalized token and the original line where it appeared.
    public static class Token
    {
        String value;
        int lineNumber;
        Token(String value, int lineNumber)
        {
            this.value = value;
            this.lineNumber = lineNumber;
        }
    }
}
