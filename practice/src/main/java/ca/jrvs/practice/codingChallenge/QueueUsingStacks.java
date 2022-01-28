package ca.jrvs.practice.codingChallenge;

import java.util.Stack;

public class QueueUsingStacks {

    Stack<Integer> pushStack;
    Stack<Integer> popStack;

    //Constructor
    public QueueUsingStacks(){
        pushStack = new Stack<>();
        popStack = new Stack<>();
    }

    /**
     * Big-O: O(1)
     * @param x int being pushed to the back of the queue.
     */
    public void push(int x){
        pushStack.push(x);
    }

    /**
     * Big-O: O(n)
     * @return x remove element from the front of the queue
     */
    public int pop(){
        while(popStack.isEmpty()){
            while(!pushStack.isEmpty()){
                popStack.push(pushStack.pop());
            }
        }
        return popStack.pop();
    }

    /**
     * Big-O: O(n)
     * @return x return element from the front of the queue.
     */
    public int peek(){
        while(popStack.isEmpty()){
            while(!pushStack.isEmpty()){
                popStack.push(popStack.pop());
            }
        }
        return popStack.peek();
    }

    /**
     * Big-O: O(1)
     * @return true if queue is empty, false otherwise.
     */
    public boolean isEmpty(){
        return pushStack.isEmpty() && popStack.isEmpty();
    }
}
