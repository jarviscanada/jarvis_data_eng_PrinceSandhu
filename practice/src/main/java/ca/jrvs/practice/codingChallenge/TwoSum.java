package ca.jrvs.practice.codingChallenge;

import java.util.HashMap;

public class TwoSum {

    /**
     * Big-O: O(n^2)
     * @param nums array of integers.
     * @param target target sum.
     * @return int[] the array of the two values that sum to target.
     */
    public int[] twoSumSlow(int[] nums, int target) {
        int[] result = new int[2];
        for(int i = 0; i < nums.length; i++){
            for(int j = i+1; j < nums.length; j++){
                if(nums[i] + nums[j] == target){
                    result[0] = nums[i];
                    result[1] = nums[j];
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Big-O: O(n)
     * @param nums array of integers.
     * @param target target sum.
     * @return int[] the array of the two values that sum to target.
     */
    public int[] twoSumFast(int[] nums, int target) {
        int[] result = new int[2];
        HashMap<Integer, Integer> hmap = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            if (hmap.containsKey(target - nums[i])) {
                result[0] = hmap.get(target - nums[i]);
                result[1] = i;
            }
            hmap.put(nums[i], i);
        }
        return result;
    }
}