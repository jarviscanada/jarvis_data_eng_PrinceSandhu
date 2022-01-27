package ca.jrvs.practice.codingChallenge;

class EvenOrOdd {

    /**
     * Big-O: O(1)
     * Justification: modulus operator.
     * @param num (primitive int).
     * @return String even or odd.
     */
    public String moduloApproach(int num) {
        return num % 2 == 0 ? "Even" : "Odd";
    }

    /**
     *Big-O: O(1)
     * Justification: bit operator.
     * @param num (primitive int).
     * @return String even or odd.
     */
    public String bitOperatorApproach(int num){
        return (num ^ 1) == num + 1 ? "even" : "odd";
    }
}
