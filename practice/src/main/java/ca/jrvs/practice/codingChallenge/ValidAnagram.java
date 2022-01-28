package ca.jrvs.practice.codingChallenge;

import java.util.Arrays;

public class ValidAnagram {

    /**
     * Big-O: O(n*log(n))
     * @param s input string.
     * @param t input string.
     * @return true if palindrome, false otherwise.
     */
    public boolean isAnagram(String s, String t) {
        if(s.length() != t.length()) return false;

        char[] sArr = s.toCharArray();
        char[] tArr = t.toCharArray();

        Arrays.sort(sArr);
        Arrays.sort(tArr);

        for(int i = 0; i < sArr.length; i++){
            if(sArr[i] != tArr[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Big-O: O(n)
     * @param s input string.
     * @param t input string.
     * @return true if palindrome, false otherwise.
     */
    public boolean isAnagramFast(String s, String t) {
        if(s.length() != t.length()) return false;

        int[] sFreq = new int[26];
        int[] tFreq = new int[26];

        for(char c : s.toCharArray()){
            sFreq[c - 'a']++;
        }

        for(char c : t.toCharArray()){
            tFreq[c - 'a']++;
        }

        for(int i = 0; i < 26; i++){
            if(sFreq[i] != tFreq[i]){
                return false;
            }
        }

        return true;
    }
}
