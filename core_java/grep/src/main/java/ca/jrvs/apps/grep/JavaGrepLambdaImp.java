package ca.jrvs.apps.grep;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.stream.Collectors;

public class JavaGrepLambdaImp extends JavaGrepImp{

    /**
     * Read a file and return all the lines.
     * @param inputFile file to be read.
     * @return List of all lines.
     * @throws IllegalArgumentException if a given inputFile is not a file.
     */
    @Override
    public List<String> readLines(File inputFile) throws IllegalArgumentException{
        List<String> lines = new ArrayList<>();
        try{
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            lines = bufferedReader.lines().collect(Collectors.toList());
        } catch (IOException e){
            throw new IllegalArgumentException("File does not exist or file parsing issue.");
        }

        return lines;
    }

    /**
     * Traverse a given directory and return all files.
     * @param rootDir input directory.
     * @return files under the rootDir.
     */
    @Override
    public List<File> listFiles(String rootDir){
        List<File> files = new ArrayList<>();
        File root = new File(rootDir);

        if(root.listFiles() != null){
            Arrays.stream(root.listFiles()).forEach(file -> {
                if(file.isFile()){
                    files.add(file);
                }
                else if(file.isDirectory()){
                    files.addAll(listFiles(file.getAbsolutePath()));
                }
            });
        }

        return files;
    }

    public static void main(String[] args){
        if (args.length != 3){
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile.");
        }

        JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
        javaGrepLambdaImp.setRegex(args[0]);
        javaGrepLambdaImp.setRootPath(args[1]);
        javaGrepLambdaImp.setOutFile(args[2]);

        try{
            javaGrepLambdaImp.process();
        } catch(Exception ex){
            javaGrepLambdaImp.logger.error("Error: Unable to process.", ex);
        }
    }

}
