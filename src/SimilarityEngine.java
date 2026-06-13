import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Compares the fingerprints from two source files and calculates similarity.
public class SimilarityEngine
{
    // Nearby matching fingerprints are merged into one larger report region.
    private static final int MERGE_GAP = 2;

    // Main comparison method used by SimChecker.
    public static SimilarityResult compare(String file1Name, String file2Name, String language, List<KGramBuilder.KGram> file1KGrams, List<KGramBuilder.KGram> file2KGrams,
            List<Fingerprinter.Fingerprint> file1Fingerprints, List<Fingerprinter.Fingerprint> file2Fingerprints, int k, int window)
    {
        // Index fingerprints by hash so matching hashes can be found quickly.
        Map<Long, List<Fingerprinter.Fingerprint>> index1 = buildIndex(file1Fingerprints);
        Map<Long, List<Fingerprinter.Fingerprint>> index2 = buildIndex(file2Fingerprints);
        int sharedCount = countSharedFingerprints(index1, index2);
        int totalCount = file1Fingerprints.size() + file2Fingerprints.size();
        double similarity = 0.0;

        // Dice coefficient style formula.
        if(totalCount > 0)
        {
            similarity = (2.0 * sharedCount / totalCount) * 100.0;
        }

        // Convert matching fingerprints into source-code line ranges.
        List<MatchRegion> matches = findMatchingRegions(index1, index2);
        matches = mergeMatches(matches);
        return new SimilarityResult(file1Name, file2Name, language, similarity, sharedCount, file1Fingerprints.size(), file2Fingerprints.size(), file1KGrams.size(), file2KGrams.size(), k, window, matches);
    }

    // Builds a hash table from fingerprint hash to all fingerprints with that hash.
    private static Map<Long, List<Fingerprinter.Fingerprint>> buildIndex(List<Fingerprinter.Fingerprint> fingerprints)
    {
        Map<Long, List<Fingerprinter.Fingerprint>> index = new HashMap<Long, List<Fingerprinter.Fingerprint>>();
        for(Fingerprinter.Fingerprint fingerprint : fingerprints)
        {
            if(!index.containsKey(fingerprint.hash))
            {
                index.put(fingerprint.hash, new ArrayList<Fingerprinter.Fingerprint>());
            }
            index.get(fingerprint.hash).add(fingerprint);
        }
        return index;
    }

    // Counts shared fingerprints without overcounting duplicate hashes.
    private static int countSharedFingerprints(Map<Long, List<Fingerprinter.Fingerprint>> index1, Map<Long, List<Fingerprinter.Fingerprint>> index2)
    {
        int shared = 0;

        for(Long hash : index1.keySet())
        {
            if(index2.containsKey(hash))
            {
                int count1 = index1.get(hash).size();
                int count2 = index2.get(hash).size();
                if(count1 < count2)
                {
                    shared += count1;
                }
                else
                {
                    shared += count2;
                }
            }
        }
        return shared;
    }

    // Creates one MatchRegion for each shared fingerprint pair.
    private static List<MatchRegion> findMatchingRegions(Map<Long, List<Fingerprinter.Fingerprint>> index1, Map<Long, List<Fingerprinter.Fingerprint>> index2)
    {
        List<MatchRegion> matches = new ArrayList<MatchRegion>();
        for(Long hash : index1.keySet())
        {
            if(index2.containsKey(hash))
            {
                List<Fingerprinter.Fingerprint> file1Matches = index1.get(hash);
                List<Fingerprinter.Fingerprint> file2Matches = index2.get(hash);
                int limit = file1Matches.size();

                if(file2Matches.size() < limit)
                {
                    limit = file2Matches.size();
                }
                for(int i = 0; i < limit; i++)
                {
                    Fingerprinter.Fingerprint fp1 = file1Matches.get(i);
                    Fingerprinter.Fingerprint fp2 = file2Matches.get(i);
                    matches.add(new MatchRegion(fp1.startLine, fp1.endLine, fp2.startLine, fp2.endLine, 1, fp1.pattern));
                }
            }
        }
        return matches;
    }

    // Combines nearby matches so the final report shows larger readable regions.
    private static List<MatchRegion> mergeMatches(List<MatchRegion> matches)
    {
        if(matches.size() <= 1)
        {
            return matches;
        }

        Collections.sort(matches, new Comparator<MatchRegion>()
        {
            public int compare(MatchRegion a, MatchRegion b)
            {
                if(a.file1Start != b.file1Start)
                {
                    return a.file1Start - b.file1Start;
                }
                if(a.file2Start != b.file2Start)
                {
                    return a.file2Start - b.file2Start;
                }
                if(a.file1End != b.file1End)
                {
                    return a.file1End - b.file1End;
                }
                return a.file2End - b.file2End;
            }
        });

        List<MatchRegion> merged = new ArrayList<MatchRegion>();
        MatchRegion current = matches.get(0);

        for(int i = 1; i < matches.size(); i++)
        {
            MatchRegion next = matches.get(i);
            if(canMerge(current, next))
            {
                current.file1End = max(current.file1End, next.file1End);
                current.file2End = max(current.file2End, next.file2End);
                current.sharedKGrams += next.sharedKGrams;
            }
            else
            {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    // Two regions can merge if they are close in both files.
    private static boolean canMerge(MatchRegion current, MatchRegion next)
    {
        boolean file1Close = next.file1Start <= current.file1End + MERGE_GAP;
        boolean file2Close = next.file2Start <= current.file2End + MERGE_GAP;
        return file1Close && file2Close;
    }

    // Returns the larger of two integers.
    private static int max(int a, int b)
    {
        if(a > b)
        {
            return a;
        }
        return b;
    }

    // Stores all final similarity numbers and matching regions.
    public static class SimilarityResult
    {
        String file1Name;
        String file2Name;
        String language;
        double similarity;
        int sharedFingerprints;
        int file1FingerprintCount;
        int file2FingerprintCount;
        int file1KGramCount;
        int file2KGramCount;
        int k;
        int window;

        List<MatchRegion> matches;
        SimilarityResult(String file1Name, String file2Name, String language, double similarity, int sharedFingerprints, int file1FingerprintCount,
                int file2FingerprintCount, int file1KGramCount, int file2KGramCount, int k, int window, List<MatchRegion> matches)
        {
            this.file1Name = file1Name;
            this.file2Name = file2Name;
            this.language = language;
            this.similarity = similarity;
            this.sharedFingerprints = sharedFingerprints;
            this.file1FingerprintCount = file1FingerprintCount;
            this.file2FingerprintCount = file2FingerprintCount;
            this.file1KGramCount = file1KGramCount;
            this.file2KGramCount = file2KGramCount;
            this.k = k;
            this.window = window;
            this.matches = matches;
        }
    }
}

// Stores one region of code that appears similar in both input files.
class MatchRegion
{
    int file1Start;
    int file1End;
    int file2Start;
    int file2End;
    int sharedKGrams;
    String samplePattern;
    
    // Creates a match with line ranges for both files and a sample normalized pattern.
    MatchRegion(int file1Start, int file1End, int file2Start, int file2End, int sharedKGrams, String samplePattern)
    {
        this.file1Start = file1Start;
        this.file1End = file1End;
        this.file2Start = file2Start;
        this.file2End = file2End;
        this.sharedKGrams = sharedKGrams;
        this.samplePattern = samplePattern;
    }
}
