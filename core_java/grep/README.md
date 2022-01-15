# Introduction
The Grep App of the Core Java project has been implemented to mimic the Linux `Grep` command in Java, which, given a regular expression and a root directory, enables users to search for matching strings from files and writes the matched strings to an output file. The first implementation of the Grep App, `JavaGrepImp`, utilizes nested `for` loops to scan files for matching strings, whereas the second implementation, `JavaGrepLambdaImp`, relies on Streams to accomplish the same goal. 

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
(30-60 words)
Discuss the memory issue and how would you fix it

# Test
How did you test your application manually? (e.g. prepare sample data, run some test cases manually, compare result)

# Deployment
How you dockerize your app for easier distribution?

# Improvement
List three things you can improve in this project.
