# Introduction
Discuss the design of each app. What does the app do? What technologies have you used? (e.g. core java, libraries, lambda, IDE, docker, etc..)
...

Technologies utilized:

Java
Maven
Docker

# Quick Start
How to use your apps? 

#Implemenation
## Pseudocode
````
matchedLines = []
//Recursively list files in the specified directory.
for file in listFilesRecursively(rootDir)
  for line in readLines(file)
      if containsPattern(line)
        matchedLines.add(line)

//Write the matched lines to the specified output file.
writeToFile(matchedLines)
````

## Performance Issue
(30-60 words)
Discuss the memory issue and how would you fix it

# Test
How did you test your application manually? (e.g. prepare sample data, run some test cases manually, compare result)

# Deployment
How you dockerize your app for easier distribution?

# Improvement
List three things you can improve in this project.
