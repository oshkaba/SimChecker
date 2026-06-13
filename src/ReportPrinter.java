import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Builds and prints the final similarity report.
// The report always appears in the terminal and is saved only when -output is used.
public class ReportPrinter
{
    // Prints a full similarity report and optionally writes it to a text file.
    public static void print(SimilarityEngine.SimilarityResult result, List<String> file1Lines, List<String> file2Lines, Path outputFile) throws IOException
    {
        String report = buildReport(result, file1Lines, file2Lines);
        System.out.print(report);
        writeReportIfRequested(report, outputFile);
    }

    // Prints a simple message report for cases where normal comparison cannot continue.
    public static void printMessage(String message, Path outputFile) throws IOException
    {
        StringBuilder report = new StringBuilder();
        report.append("SimChecker Result\n\n");
        report.append(message).append("\n");
        System.out.print(report.toString());
        writeReportIfRequested(report.toString(), outputFile);
    }

    // Saves the report only if the user supplied the -output flag.
    private static void writeReportIfRequested(String report, Path outputFile) throws IOException
    {
        if(outputFile == null)
        {
            return;
        }
        Files.write(outputFile, report.getBytes(StandardCharsets.UTF_8));
        System.out.println();
        System.out.println("Report saved to: " + outputFile);
    }

    // Builds the text shown in the terminal and optional output file.
    private static String buildReport(SimilarityEngine.SimilarityResult result, List<String> file1Lines, List<String> file2Lines)
    {
        StringBuilder report = new StringBuilder();
        report.append("SimChecker Result\n");
        report.append("=================\n\n");
        report.append("File 1: ").append(result.file1Name).append("\n");
        report.append("File 2: ").append(result.file2Name).append("\n");
        report.append("Language: ").append(result.language).append("\n\n");
        report.append(String.format("Similarity: %.2f%%%n", result.similarity));
        report.append("Shared matches: ").append(result.sharedFingerprints).append("\n");
        report.append("File 1 selected matches: ").append(result.file1FingerprintCount).append("\n");
        report.append("File 2 selected matches: ").append(result.file2FingerprintCount).append("\n");
        report.append("File 1 code chunks checked: ").append(result.file1KGramCount).append("\n");
        report.append("File 2 code chunks checked: ").append(result.file2KGramCount).append("\n");
        report.append("Pattern size: ").append(result.k).append("\n");
        report.append("Scan window: ").append(result.window).append("\n");
        report.append("\n");

        if(result.matches == null || result.matches.size() == 0)
        {
            report.append("No similar regions found.\n");
            return report.toString();
        }

        report.append("Similar regions\n");
        report.append("===============\n\n");

        for(int i = 0; i < result.matches.size(); i++)
        {
            appendMatch(report, result, result.matches.get(i), file1Lines, file2Lines, i + 1);
        }
        return report.toString();
    }

    // Adds one matching region to the report.
    private static void appendMatch(StringBuilder report, SimilarityEngine.SimilarityResult result, MatchRegion match, List<String> file1Lines, List<String> file2Lines, int number)
    {
        report.append("Match ").append(number).append("\n");
        report.append("-------\n");
        report.append("Evidence: ").append(match.sharedKGrams).append(" shared match");

        if(match.sharedKGrams != 1)
        {
            report.append("s");
        }
        report.append("\n");
        report.append(result.file1Name).append(" lines ").append(match.file1Start).append(" to ").append(match.file1End).append("\n");
        report.append(result.file2Name).append(" lines ").append(match.file2Start).append(" to ").append(match.file2End).append("\n");
        report.append("Matching normalized pattern: ").append(shorten(match.samplePattern, 120)).append("\n\n");
        report.append(result.file1Name).append(" code:\n");
        appendCode(report, file1Lines, match.file1Start, match.file1End);
        report.append("\n");
        report.append(result.file2Name).append(" code:\n");
        appendCode(report, file2Lines, match.file2Start, match.file2End);
        report.append("\n");
    }

    // Adds source lines with line numbers to the report.
    private static void appendCode(StringBuilder report, List<String> lines, int start, int end)
    {
        int first = clamp(start, 1, lines.size());
        int last = clamp(end, 1, lines.size());

        for(int i = first; i <= last; i++)
        {
            report.append(String.format("%4d | %s%n", i, lines.get(i - 1)));
        }
    }

    // Keeps a line number inside the valid file range.
    private static int clamp(int value, int min, int max)
    {
        if(value < min)
        {
            return min;
        }
        if(value > max)
        {
            return max;
        }
        return value;
    }
    
    // Shortens a long normalized pattern so the report stays readable.
    private static String shorten(String text, int maxLength)
    {
        if(text.length() <= maxLength)
        {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
