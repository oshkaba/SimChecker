# SimChecker

SimChecker is a Java program that compares two source code files and reports how similar they are. It removes comments, tokenizes the code, builds k-grams, uses fingerprinting, and prints a similarity report.

## Requirements

SimChecker requires:

* Ubuntu or WSL Ubuntu
* Java JDK
* javac
* java

The project folder contains the Java source files inside the `src/` folder. The Makefile builds them into `SimChecker.jar`.

## Install Dependencies

Run:

```bash
sudo apt update
sudo apt install default-jdk
```

Check that Java is installed:

```bash
java -version
javac -version
```

## Build and Run

Go into the project folder:

```bash
cd SimChecker
```

Build the jar:

```bash
make
```

Run SimChecker with the jar:

```bash
java -jar SimChecker.jar <File1> <File2>
```

## Save Report to a File

Use `-output` to save the report:

```bash
java -jar SimChecker.jar file1.java file2.java -output report.txt
```

The report will still print in the terminal.

## Help

Show the help message:

```bash
java -jar SimChecker.jar -help
```

or:

```bash
make help
```

## Supported Files

SimChecker supports:

```text
.java
.c
.h
.cpp
.hpp
.py
.js
```

Both files must be the same language type.

## Clean

To remove generated files:

```bash
make clean
```
