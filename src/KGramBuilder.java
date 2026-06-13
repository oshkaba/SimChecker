import java.util.ArrayList;
import java.util.List;

// Builds k-grams from normalized tokens.
// A k-gram is a group of k neighboring tokens.
public class KGramBuilder
{
    // Converts the token list into overlapping k-token patterns.
    public static List<KGram> buildKGrams(List<Tokenizer.Token> tokens, int k)
    {
        List<KGram> kgrams = new ArrayList<KGram>();
        if(tokens == null || tokens.size() < k)
        {
            return kgrams;
        }
        for(int i = 0; i <= tokens.size() - k; i++)
        {
            StringBuilder text = new StringBuilder();
            int startLine = tokens.get(i).lineNumber;
            int endLine = tokens.get(i + k - 1).lineNumber;

            // Join the k tokens into one normalized text pattern.
            for(int j = i; j < i + k; j++)
            {
                if(j > i)
                {
                    text.append(" ");
                }
                text.append(tokens.get(j).value);
            }
            kgrams.add(new KGram(text.toString(), startLine, endLine));
        }
        return kgrams;
    }

    // Stores one k-gram and the original source line range it came from.
    public static class KGram
    {
        String text;
        int startLine;
        int endLine;
        
        KGram(String text, int startLine, int endLine)
        {
            this.text = text;
            this.startLine = startLine;
            this.endLine = endLine;
        }
    }
}
