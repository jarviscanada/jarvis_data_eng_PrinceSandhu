package ca.jrvs.practice.codingChallenge;

public class Atoi {

    /**
     * Big-O: O(n)
     * @param num number string.
     * @return int integer representation of intput string.
     */
    public int builtInAtoi(String num) {
        return Integer.parseInt(num);
    }

    /**
     * Big-O: O(n)
     * @param num number string.
     * @return int integer representation of intput string.
     */
    public int Atoi(String num){
        //Handle empty string.
        if(num.length() == 0 || num == " ") return 0;

        int index = 0;
        int sign = 1;
        int total = 0;

        //Remove spaces.
        while(index < num.length() && num.charAt(index) == ' '){
            index++;
        }

        if(index == num.length()) return 0;

        //Handle sign.
        if(num.charAt(index) == '+' || num.charAt(index) == '-'){
            sign = num.charAt(index) == '+' ? 1 : -1;
            index++;
        }

        //Convert number.
        while(index < num.length()){
            int digit = num.charAt(index) - '0';
            if (digit < 0 || digit > 9) break;

            //Overflow check
            if (((Integer.MAX_VALUE - digit)/10) < total){
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            total = (10 * total) + digit;
            index++;
        }
        return sign * total;
    }
}
