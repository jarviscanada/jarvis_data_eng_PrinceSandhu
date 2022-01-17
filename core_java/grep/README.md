# Introduction
The Grep App of the Core Java project has been implemented to mimic the Linux `Grep` command in Java, which, given a regular expression and a root directory, enables users to search for matching strings from files and writes the matched strings to an output file. The first implementation of the Grep App, `JavaGrepImp`, utilizes nested `for` loops to scan files for matching strings, whereas the second implementation, `JavaGrepLambdaImp`, relies on Streams to accomplish the aforementioned goal. 

Technologies utilized:

- Java
- Maven
- Docker

# Quick Start

The Grep App takes three arguments:
- `regex`: a special text string for describing a search pattern.
- `rootPath`: root directory path.
- `outFile`: output file name.

```
regex=".*Romeo.*Juliet.*"
rootPath="./data"
outfile="grep_out.txt"

#Pull the Docker Image from Docker Hub.
docker pull princesandhu/grep

#Run the Docker Container.
docker run --rm -v `pwd`/data:/data -v `pwd`/log:/log princesandhu/grep ${regex} ${src_dir} /log/${outfile}

#Inspect the outfile for the result.
```

# Implementation
## Pseudocode
```
matchedLines = []
//Recursively list files in the specified directory.
for file in listFilesRecursively(rootDir)
  for line in readLines(file)
      if containsPattern(line)
        matchedLines.add(line)

//Write the matched lines to the specified output file.
writeToFile(matchedLines)
```

## Performance Issue
The Grep App is limited by the default size of the heap, which is 5MB. If the file being read by the program is greater than 5MB, an `OutOfMemoryError` is thrown. A possible workaround is to increase the heap size to exceed the size of the largest file that is being read using the `-Xms` command. The workaround of choice, however, is to utilize Stream APIs, as they effectively bypass storing matched lines to a list and instead write directly to the output file once the operation is complete.

# Test
## Unit Tests

`writeToFile()`:\
Test result: pass.
```
@Test
public void writeToFileTest() throws IOException {
    testImp.setOutFile("test.txt");
    List<String> lines = new ArrayList<>();
    lines.add("Element1");
    lines.add("Element2");
    testImp.writeToFile(lines); //Inspect test.txt
}
```

`containsPattern()`:\
Test result: pass.
```
@Test
public boolean containsPatternTest(String line){
    testImp.setRegex("Element1");
    boolean match = testImp.containsPattern("Element1, Element2, Element3, Element4");
    assertTrue("pattern matches", match);
}
```

`readLines()`:\
Test result: pass.
```
@Test
public void readLinesTest() throws IOException {
    List<String> lines = testImp.readLines(new File("./data/txt/shakespeare.txt"));
    String firstLine = "This is the 100th Etext file presented by Project Gutenberg, and";
    assertTrue("Line matches", lines.get(0) == firstLine);
}
```

To avoid redundancy, the methods in `JavaGrepLambdaImp` were tested similarly, and produced the expected output.

## Verifying Output

Test Case: Line contains the words: "Romeo" and "Juliet".\
Test Result: pass.
```
docker run --rm -v `pwd`/data:/data -v `pwd`/log:/log princesandhu/grep ".*Romeo.*Juliet.*" "./data" /log/"grep_out.txt"

//grep_out.txt file:
cat grep_out.txt:
    Is father, mother, Tybalt, Romeo, Juliet,
Enter Romeo and Juliet aloft, at the Window.
    And Romeo dead; and Juliet, dead before,
    Romeo, there dead, was husband to that Juliet;
```


# Deployment
For ease of distribution, the Grep App has been packaged into a Docker Image and uploaded to Docker Hub. The Dockerfile is as follows:
```
FROM openjdk:8-alpine
COPY target/grep*.jar /usr/local/app/grep/lib/grep.jar
ENTRYPOINT ["java","-jar","/usr/local/app/grep/lib/grep.jar"]
```

# Improvements
1. Include various `grep` flag options, such as `-i` to ignore case.
2. Search files for multiple regex patterns, as the program is currently limited to only one regex pattern.
3. Provide option to skip specified files and directories.
