package ca.jrvs.practice.codingChallenge;

import java.util.LinkedList;
import java.util.Queue;

public class StackUsingQueue {

    Queue<Integer> queue;

    //Constructor.
    public StackUsingQueue(){
        queue = new LinkedList<Integer>();
    }

    /**
     * Big-O: O(n)
     * @param x: int to be added to the back of the queue.
     */
    public void push(int x){
        queue.add(x);
        for(int i = 0; i < queue.size()-1; i++){
            queue.add(queue.poll());
        }
    }

    /**
     * Big-O: O(1)
     * @return int: the elmeent at the front of the queue.
     */
    public int pop(){
        return queue.poll();
    }

    /**
     * Big-O: O(1)
     * @return int: the element at the front of the queue.
     */
    public int peek(){
        return queue.peek();
    }

    /**
     * Big-O: O(1)
     * @return true: if stack is empty, false: otherwise.
     */
    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
