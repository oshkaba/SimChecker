# Build SimChecker from the organized src/ folder.
# The program is packaged into SimChecker.jar so it can be run with java -jar.

JAVAC=javac
JAR=jar
JFLAGS=-g -d .
MAIN=SimChecker
JARFILE=SimChecker.jar
ARGS=-help

SOURCES=src/CommentRemover.java src/Tokenizer.java src/KGramBuilder.java src/Fingerprinter.java src/SimilarityEngine.java src/ReportPrinter.java src/SimChecker.java

all: $(JARFILE)

$(JARFILE): $(SOURCES)
	$(JAVAC) $(JFLAGS) $(SOURCES)
	$(JAR) cfe $(JARFILE) $(MAIN) *.class
	rm -f *.class


run: all
	java -jar $(JARFILE) $(ARGS)

# Show the program help message.
help: all
	java -jar $(JARFILE) -help


# Remove generated files.
clean:
	rm -f *.class $(JARFILE) SimCheckerReport.txt sample_report.txt report.txt
