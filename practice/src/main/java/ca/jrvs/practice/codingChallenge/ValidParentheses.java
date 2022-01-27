package ca.jrvs.practice.codingChallenge;

import java.util.Stack;

public class ValidParentheses {

    /**
     * Big-O: O(n)
     * @param s input string.
     * @return boolean true if parenthesis are valid, false otherwise.
     */
    public boolean validParentheses(String s){
        Stack<Character> stack = new Stack<Character>();

        for(int i = 0; i < s.length(); i++){
            if(!stack.isEmpty() && (s.charAt(i) == '}' && stack.peek() == '{')){
                stack.pop();
            }
            else if(!stack.isEmpty() && (s.charAt(i) == ']' && stack.peek() == '[')){
                stack.pop();
            }
            else if(!stack.isEmpty() && (s.charAt(i) == ')' && stack.peek() == '(')){
                stack.pop();
            }
            else{
                stack.push(s.charAt(i));
            }
        }
        return stack.isEmpty();
    }
}