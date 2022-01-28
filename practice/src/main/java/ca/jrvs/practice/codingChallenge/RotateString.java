package ca.jrvs.practice.codingChallenge;

public class RotateString {

    /**
     * Big-O: O(n^2)
     * @param s input string.
     * @param goal goal string.
     * @return true if s contains goal, false otherwise.
     */
    public boolean rotateString(String s, String goal) {
        return (s.length() == goal.length() && (s+s).contains(goal));
    }
}
