import java.util.ArrayList;
import java.util.List;

// Turns k-grams into MOSS-style fingerprints using winnowing.
// Winnowing keeps selected hashes instead of comparing every k-gram.
public class Fingerprinter
{
    // Hashes each k-gram and selects fingerprints using a sliding window.
    public static List<Fingerprint> buildFingerprints(List<KGramBuilder.KGram> kgrams, int window)
    {
        List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        if(kgrams == null || kgrams.size() == 0)
        {
            return fingerprints;
        }
        List<Fingerprint> hashed = hashKGrams(kgrams);
        if(window < 1)
        {
            window = 1;
        }
        // If the file is smaller than the window, keep the smallest available hash.
        if(hashed.size() <= window)
        {
            addSmallest(fingerprints, hashed, 0, hashed.size() - 1, -1);
            return fingerprints;
        }
        int lastSelected = -1;
        // Slide the window across the hashed k-grams and keep the rightmost minimum.
        for(int start = 0; start <= hashed.size() - window; start++)
        {
            int end = start + window - 1;
            int selected = getRightmostSmallest(hashed, start, end);
            if(selected != lastSelected)
            {
                Fingerprint fp = hashed.get(selected);
                fingerprints.add(fp);
                lastSelected = selected;
            }
        }
        return fingerprints;
    }

    // Creates one hash value for every k-gram.
    private static List<Fingerprint> hashKGrams(List<KGramBuilder.KGram> kgrams)
    {
        List<Fingerprint> hashed = new ArrayList<Fingerprint>();
        for(int i = 0; i < kgrams.size(); i++)
        {
            KGramBuilder.KGram kgram = kgrams.get(i);
            long hash = hashText(kgram.text);
            hashed.add(new Fingerprint(
                    hash,
                    kgram.startLine,
                    kgram.endLine,
                    i,
                    kgram.text
            ));
        }
        return hashed;
    }
    
    // Adds the smallest hash in a range if it was not already selected.
    private static void addSmallest(List<Fingerprint> fingerprints, List<Fingerprint> hashed, int start, int end, int lastSelected)
    {
        int selected = getRightmostSmallest(hashed, start, end);
        if(selected != lastSelected)
        {
            fingerprints.add(hashed.get(selected));
        }
    }

    // Finds the rightmost smallest hash in the current window.
    private static int getRightmostSmallest(List<Fingerprint> hashed, int start, int end)
    {
        int selected = start;
        long smallest = hashed.get(start).hash;
        for(int i = start + 1; i <= end; i++)
        {
            long current = hashed.get(i).hash;
            if(current <= smallest)
            {
                smallest = current;
                selected = i;
            }
        }
        return selected;
    }

    // Uses a simple FNV-style hash for stable k-gram fingerprint values.
    private static long hashText(String text)
    {
        long hash = 1469598103934665603L;
        long prime = 1099511628211L;
        for(int i = 0; i < text.length(); i++)
        {
            hash = hash ^ text.charAt(i);
            hash = hash * prime;
        }
        if(hash < 0)
        {
            hash = -hash;
        }
        return hash;
    }

    // Stores one selected fingerprint and the source location it represents.
    public static class Fingerprint
    {
        long hash;
        int startLine;
        int endLine;
        int position;
        String pattern;
        
        Fingerprint(long hash, int startLine, int endLine, int position, String pattern)
        {
            this.hash = hash;
            this.startLine = startLine;
            this.endLine = endLine;
            this.position = position;
            this.pattern = pattern;
        }
    }
}
