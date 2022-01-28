package ca.jrvs.practice.codingChallenge;

public class ValidPalindrome {

    /**
     * Big-O: O(n)
     * @param s input string.
     * @return true if palindrome, false otherwise.
     */
    public boolean validPalindrome(String s){
        int leftPointer = 0;
        int rightPointer = s.length()-1;

        while(leftPointer < rightPointer){
            while(leftPointer < rightPointer && !Character.isLetterOrDigit(s.charAt(leftPointer))){
                leftPointer++;
            }
            while(leftPointer < rightPointer && !Character.isLetterOrDigit(s.charAt(rightPointer))){
                rightPointer--;
            }
            if(leftPointer <= rightPointer &&
                    Character.toLowerCase(s.charAt(leftPointer)) != Character.toLowerCase(s.charAt(rightPointer))){
                return false;
            }
            leftPointer++;
            rightPointer--;
        }
        return true;
    }
}
