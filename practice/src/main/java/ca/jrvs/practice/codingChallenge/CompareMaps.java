package ca.jrvs.practice.codingChallenge;

import java.util.Map;

public class CompareMaps {

    /**
     *  Problem: compare two maps in Java
     * @param m1, m2 the two maps to be compared.
     * @return String true or false.
     */
    public <K,V> boolean compareMaps(Map<K,V> m1, Map<K,V> m2) {
        return m1.equals(m2);
    }
}