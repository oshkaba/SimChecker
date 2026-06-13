import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Main class for the SimChecker program.
// It reads two source files, removes comments, tokenizes them, fingerprints them,
// compares them, and prints a similarity report to the terminal.
public class SimChecker
{
    // These values are hidden from the user because they are internal algorithm settings.
    private static final int DEFAULT_K = 7;
    private static final int DEFAULT_WINDOW = 5;

    // Program entry point.
    public static void main(String[] args)
    {
        try
        {
            Config config = parseArgs(args);

            // parseArgs returns null only when the user asks for help.
            if(config == null)
            {
                return;
            }

            validateFile(config.file1);
            validateFile(config.file2);
            config.language1 = detectLanguage(config.file1);
            config.language2 = detectLanguage(config.file2);

            if(!config.language1.equals(config.language2))
            {
                throw new IllegalArgumentException("Files must be the same language type. Detected " + config.language1 + " and " + config.language2 + ".");
            }

            // Read the original files so the report can show matching source lines.
            List<String> file1Lines = Files.readAllLines(config.file1);
            List<String> file2Lines = Files.readAllLines(config.file2);

            // Remove comments before tokenizing so comments do not affect similarity.
            List<String> file1CleanLines = CommentRemover.removeComments(file1Lines, config.language1);
            List<String> file2CleanLines = CommentRemover.removeComments(file2Lines, config.language2);

            // Convert source code into normalized tokens.
            List<Tokenizer.Token> file1Tokens = Tokenizer.tokenize(file1CleanLines);
            List<Tokenizer.Token> file2Tokens = Tokenizer.tokenize(file2CleanLines);

            // A file with fewer tokens than k cannot create even one k-gram.
            if(file1Tokens.size() < config.k || file2Tokens.size() < config.k)
            {
                ReportPrinter.printMessage("One or both files are too small to compare with k = " + config.k + ".", config.outputFile);
                return;
            }

            // Build k-grams from each token stream.
            List<KGramBuilder.KGram> file1KGrams = KGramBuilder.buildKGrams(file1Tokens, config.k);
            List<KGramBuilder.KGram> file2KGrams = KGramBuilder.buildKGrams(file2Tokens, config.k);

            // Apply winnowing to select fingerprints from the k-grams.
            List<Fingerprinter.Fingerprint> file1Fingerprints = Fingerprinter.buildFingerprints(file1KGrams, config.window);
            List<Fingerprinter.Fingerprint> file2Fingerprints = Fingerprinter.buildFingerprints(file2KGrams, config.window);

            // Compare the fingerprints and collect the matching regions.
            SimilarityEngine.SimilarityResult result = SimilarityEngine.compare(config.file1.toString(), config.file2.toString(), config.language1, file1KGrams, file2KGrams, file1Fingerprints, file2Fingerprints, config.k, config.window);
            // Always print to the terminal. Save to a file only when -output is used.
            ReportPrinter.print(result, file1Lines, file2Lines, config.outputFile);
        }
        catch(IllegalArgumentException e)
        {
            System.out.println("Error: " + e.getMessage());
            printUsage();
        }
        catch(IOException e)
        {
            System.out.println("Error reading or writing file: " + e.getMessage());
        }
    }

    // Parses the command line arguments and stores them in a Config object.
    private static Config parseArgs(String[] args)
    {
        if(args.length == 1 && isHelpOption(args[0]))
        {
            printUsage();
            return null;
        }
        if(args.length < 2)
        {
            throw new IllegalArgumentException("Missing required source files.");
        }
        if(isHelpOption(args[0]) || isHelpOption(args[1]))
        {
            printUsage();
            return null;
        }

        Path file1 = Path.of(args[0]);
        Path file2 = Path.of(args[1]);
        int k = DEFAULT_K;
        int window = DEFAULT_WINDOW;
        Path outputFile = null;
        int i = 2;

        while(i < args.length)
        {
            if(args[i].equals("-output"))
            {
                if(i + 1 >= args.length)
                {
                    throw new IllegalArgumentException("Missing value after -output.");
                }
                outputFile = Path.of(args[i + 1]);
                i += 2;
            }
            else if(isHelpOption(args[i]))
            {
                printUsage();
                return null;
            }
            else
            {
                throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        return new Config(file1, file2, k, window, outputFile);
    }

    // Makes sure the given path exists and points to a supported source file.
    private static void validateFile(Path file)
    {
        if(!Files.exists(file))
        {
            throw new IllegalArgumentException("File does not exist: " + file);
        }
        if(!Files.isRegularFile(file))
        {
            throw new IllegalArgumentException("Path is not a regular file: " + file);
        }
        detectLanguage(file);
    }

    // Detects the source language from the file extension.
    private static String detectLanguage(Path file)
    {
        String name = file.toString().toLowerCase();
        if(name.endsWith(".java"))
        {
            return "Java";
        }
        if(name.endsWith(".c") || name.endsWith(".h"))
        {
            return "C";
        }
        if(name.endsWith(".cpp") || name.endsWith(".cc") || name.endsWith(".cxx") || name.endsWith(".hpp") || name.endsWith(".hh") || name.endsWith(".hxx"))
        {
            return "C++";
        }
        if(name.endsWith(".py"))
        {
            return "Python";
        }
        if(name.endsWith(".js"))
        {
            return "JavaScript";
        }
        throw new IllegalArgumentException("Unsupported file type: " + file);
    }

    // Checks whether an argument is a help flag.
    private static boolean isHelpOption(String value)
    {
        return value.equals("-help") || value.equals("-h");
    }

    // Prints the allowed command format.
    private static void printUsage()
    {
        System.out.println();
        System.out.println("SimChecker compares two source files and prints their similarity percentage.");
        System.out.println();
        System.out.println("java SimChecker <File1> <File2> [-output <file.txt>] [-help]");
        System.out.println();
        System.out.println("<File1>                   First source file to compare");
        System.out.println("<File2>                   Second source file to compare");
        System.out.println("[-output <file.txt>]      Save the report to a text file");
        System.out.println("[-help] or [-h]           Print this help message");
        System.out.println();
        System.out.println("Supported files: .java, .c, .h, .cpp, .hpp, .py, .js");
    }
    
    // Small container for command line settings.
    private static class Config
    {
        Path file1;
        Path file2;
        String language1;
        String language2;
        int k;
        int window;
        Path outputFile;
        Config(Path file1, Path file2, int k, int window, Path outputFile)
        {
            this.file1 = file1;
            this.file2 = file2;
            this.k = k;
            this.window = window;
            this.outputFile = outputFile;
        }
    }
}
