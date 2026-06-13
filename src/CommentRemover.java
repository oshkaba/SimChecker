import java.util.ArrayList;
import java.util.List;

// Removes comments while preserving strings and character literals.
// Java, C, C++, and JavaScript use // and /* */ comments.
// Python uses # comments and triple-quoted blocks.
public class CommentRemover
{
    // Returns a new list of lines with comments removed for the detected language.
    public static List<String> removeComments(List<String> lines, String language)
    {
        if(language.equals("Python"))
        {
            return removePythonComments(lines);
        }
        return removeCStyleComments(lines);
    }
    // Removes comments from Java, C, C++, and JavaScript style languages.
    private static List<String> removeCStyleComments(List<String> lines)
    {
        List<String> cleanedLines = new ArrayList<String>();
        boolean inBlockComment = false;
        boolean inString = false;
        boolean inChar = false;
        boolean inTemplate = false;
        for(String line : lines)
        {
            StringBuilder cleaned = new StringBuilder();
            int i = 0;
            while(i < line.length())
            {
                char current = line.charAt(i);
                char next = nextChar(line, i);
                // Ignore characters until the end of a block comment is found.
                if(inBlockComment)
                {
                    if(current == '*' && next == '/')
                    {
                        inBlockComment = false;
                        appendSpaceIfNeeded(cleaned);
                        i += 2;
                    }
                    else
                    {
                        i++;
                    }
                }
                // Keep full double-quoted strings because comment markers inside strings are not comments.
                else if(inString)
                {
                    if(current == '\\' && i + 1 < line.length())
                    {
                        cleaned.append(current);
                        cleaned.append(line.charAt(i + 1));
                        i += 2;
                    }
                    else
                    {
                        cleaned.append(current);
                        if(current == '"')
                        {
                            inString = false;
                        }
                        i++;
                    }
                }
                // Keep full single-quoted chars or strings for the same reason.
                else if(inChar)
                {
                    if(current == '\\' && i + 1 < line.length())
                    {
                        cleaned.append(current);
                        cleaned.append(line.charAt(i + 1));
                        i += 2;
                    }
                    else
                    {
                        cleaned.append(current);
                        if(current == '\'')
                        {
                            inChar = false;
                        }
                        i++;
                    }
                }
                // Keep JavaScript template strings for the same reason.
                else if(inTemplate)
                {
                    if(current == '\\' && i + 1 < line.length())
                    {
                        cleaned.append(current);
                        cleaned.append(line.charAt(i + 1));
                        i += 2;
                    }
                    else
                    {
                        cleaned.append(current);
                        if(current == '`')
                        {
                            inTemplate = false;
                        }
                        i++;
                    }
                }
                else
                {
                    // A line comment removes the rest of the current line.
                    if(current == '/' && next == '/')
                    {
                        appendSpaceIfNeeded(cleaned);
                        break;
                    }
                    // A block comment can continue across multiple lines.
                    else if(current == '/' && next == '*')
                    {
                        appendSpaceIfNeeded(cleaned);
                        inBlockComment = true;
                        i += 2;
                    }
                    else
                    {
                        cleaned.append(current);
                        if(current == '"')
                        {
                            inString = true;
                        }
                        else if(current == '\'')
                        {
                            inChar = true;
                        }
                        else if(current == '`')
                        {
                            inTemplate = true;
                        }
                        i++;
                    }
                }
            }
            cleanedLines.add(cleaned.toString());
        }
        return cleanedLines;
    }

    // Removes comments from Python source lines.
    private static List<String> removePythonComments(List<String> lines)
    {
        List<String> cleanedLines = new ArrayList<String>();
        boolean inTripleSingle = false;
        boolean inTripleDouble = false;
        for(String line : lines)
        {
            StringBuilder cleaned = new StringBuilder();
            int i = 0;
            while(i < line.length())
            {
                char current = line.charAt(i);
                // Ignore text until a closing triple-single quote is found.
                if(inTripleSingle)
                {
                    if(startsWith(line, i, "'''") )
                    {
                        inTripleSingle = false;
                        appendSpaceIfNeeded(cleaned);
                        i += 3;
                    }
                    else
                    {
                        i++;
                    }
                }
                // Ignore text until a closing triple-double quote is found.
                else if(inTripleDouble)
                {
                    if(startsWith(line, i, "\"\"\"") )
                    {
                        inTripleDouble = false;
                        appendSpaceIfNeeded(cleaned);
                        i += 3;
                    }
                    else
                    {
                        i++;
                    }
                }
                else
                {
                    // Triple quotes are treated as block comments or docstrings.
                    if(startsWith(line, i, "'''") )
                    {
                        appendSpaceIfNeeded(cleaned);
                        inTripleSingle = true;
                        i += 3;
                    }
                    else if(startsWith(line, i, "\"\"\"") )
                    {
                        appendSpaceIfNeeded(cleaned);
                        inTripleDouble = true;
                        i += 3;
                    }
                    // A # comment removes the rest of the current line.
                    else if(current == '#')
                    {
                        appendSpaceIfNeeded(cleaned);
                        break;
                    }
                    // Keep regular strings because # inside strings is not a comment.
                    else if(current == '"' || current == '\'')
                    {
                        i = copyFullQuote(line, cleaned, i, current);
                    }
                    else
                    {
                        cleaned.append(current);
                        i++;
                    }
                }
            }
            cleanedLines.add(cleaned.toString());
        }
        return cleanedLines;
    }

    // Copies a full one-line Python quote while preserving escaped characters.
    private static int copyFullQuote(String line, StringBuilder cleaned, int start, char quote)
    {
        int i = start;
        cleaned.append(line.charAt(i));
        i++;
        while(i < line.length())
        {
            char current = line.charAt(i);
            cleaned.append(current);
            if(current == '\\' && i + 1 < line.length())
            {
                cleaned.append(line.charAt(i + 1));
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

    // Safely returns the next character or null character if none exists.
    private static char nextChar(String line, int index)
    {
        if(index + 1 < line.length())
        {
            return line.charAt(index + 1);
        }
        return '\0';
    }

    // Checks whether a line starts with a given value at an index.
    private static boolean startsWith(String line, int index, String value)
    {
        if(index + value.length() > line.length())
        {
            return false;
        }
        return line.substring(index, index + value.length()).equals(value);
    }
    
    // Adds a space where a comment was removed so tokens do not merge together.
    private static void appendSpaceIfNeeded(StringBuilder cleaned)
    {
        if(cleaned.length() == 0)
        {
            return;
        }
        char last = cleaned.charAt(cleaned.length() - 1);
        if(!Character.isWhitespace(last))
        {
            cleaned.append(' ');
        }
    }
}
