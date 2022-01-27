package ca.jrvs.practice.codingChallenge;

public class FibonacciStairs {

    /**
     * Big-O: O(2^n)
     * @param n (primitive int).
     * @return int the Fibonacci number.
     */
    public int recursiveFib(int n){
        if(n == 1 || n == 2){
            return 1;
        }
        else{
            return recursiveFib(n-1) + recursiveFib(n-2);
        }
    }

    /**
     * Big-O: O(n)
     * @param n (primitive int).
     * @return int the Fibonacci number.
     */
    public int dpFib(int n) {
        if(n <= 1) return n;

        int[] dp = new int[n+1];
        dp[0] = 0;
        dp[1] = 1;

        for(int i = 2; i <= n; i++){
            dp[i] = dp[i-1]+dp[i-2];
        }
        return dp[n];
    }

    /**
     * Big-O: O(n)
     * @param n the number of stairs.
     * @return int the number of distinct ways to climb to the top.
     */
    public int climbStairs(int n) {
        int[] steps = new int[n+1];
        steps[0] = 1;
        steps[1] = 1;

        for(int i = 2; i <= n; i++){
            steps[i] = steps[i-1] + steps[i-2];
        }
        return steps[n];
    }
}
