package ca.jrvs.apps.practice;

public class RegexExcImp implements RegexExc{
    /**
     * return true if filename extension is jpg or jpeg (case-insensitive).
     * @param filename
     * @return
     */
    public boolean matchJpeg(String filename){
        return filename.matches("(?i).+\\.jpe?g");
    }

    /**
     * return true if IP is valid.
     * IP address range is from 0.0.0.0 to 999.999.999.999
     * @param ip
     * @return
     */
    public boolean matchIp(String ip){
        return ip.matches("(\\d{1,3}\\.){3}\\d{1,3}");
    }

    /**
     * return true if line is empty (empty, whitespace, tab, etc.).
     * @param line
     * @return
     */
    public boolean isEmptyLine(String line){
        return line.matches("^\\s*$");
    }
}
