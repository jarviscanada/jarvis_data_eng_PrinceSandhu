package ca.jrvs.apps.grep;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.log4j.BasicConfigurator;

public class JavaGrepImp implements JavaGrep{

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;

    /**
     * Top-level search workflow.
     * @throws IOException
     */
    @Override
    public void process() throws IOException {
        List<String> matchedLines = new ArrayList<>();
        for(File file : listFiles(this.getRootPath())){
            for(String line : readLines(file)){
                if(containsPattern(line)){
                    matchedLines.add(line);
                }
            }
        }
        writeToFile(matchedLines);
    }

    /**
     * Traverse a given directory and return all files.
     * @param rootDir input directory.
     * @return files under the rootDir.
     */
    @Override
    public List<File> listFiles(String rootDir) {
        List<File> files = new ArrayList<>();
        File root = new File(rootDir);

        if(root.listFiles() != null){
            for(File file : root.listFiles()){
                if(file.isFile()){
                    files.add(file);
                }
                else if(file.isDirectory()){
                    files.addAll(listFiles(file.getAbsolutePath()));
                }
            }
        }
        return files;
    }

    /**
     * Read a file and return all the lines.
     * @param inputFile file to be read.
     * @throws IllegalArgumentException if a given inputFile is not a file.
     */
    @Override
    public List<String> readLines(File inputFile) {
        List<String> lines = new ArrayList<>();
        try{
            String line;
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null){
                lines.add(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Check if a line contains the regex pattern (passed by the user).
     * @param line input string.
     * @return true if there is a match.
     */
    @Override
    public boolean containsPattern(String line) {
        Pattern pattern = Pattern.compile(this.getRegex());
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }

    /**
     * Write lines to a file.
     * @param lines matched line.
     * @throws IOException if write failed.
     */
    @Override
    public void writeToFile(List<String> lines) throws IOException {
        FileWriter fileWriter = new FileWriter(this.getOutFile());
        for(String line : lines){
            fileWriter.write(line + "\n");
        }
        fileWriter.close();
    }

    @Override
    public String getRootPath() {
        return this.rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex(){
        return this.regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getOutFile() {
        return this.outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public static void main(String[] args) {
        if(args.length != 3){
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile.");
        }

        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: JavaGrep regex rootPath outFile");
        }

        // Use default logger config
        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception ex) {
            javaGrepImp.logger.error("Error: Unable to process", ex);
        }
    }

}