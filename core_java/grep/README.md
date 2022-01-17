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
How did you test your application manually? (e.g. prepare sample data, run some test cases manually, compare result)

# Deployment
How you dockerize your app for easier distribution?

# Improvements
1. Include various `Grep` flag options, such as `-i` to ignore case.
2. Search files for multiple regex patterns, as the program is currently limited to only one regex pattern.
3. Provide option to skip specified files and directories.
