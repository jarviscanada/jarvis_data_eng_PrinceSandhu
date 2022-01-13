package ca.jrvs.apps.practice;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class LambdaStreamExcImp implements LambdaStreamExc{
    /**
     * Creates a String stream from an array
     *
     * @param strings
     * @return String stream
     */
    @Override
    public Stream<String> createStrStream(String[] strings){
        return Stream.of(strings);
    }

    /**
     * Converts all strings to uppercase
     *
     * @param strings
     * @return String stream
     */
    @Override
    public Stream<String> toUpperCase(String[] strings){
        return Stream.of(strings).map(s -> s.toUpperCase());
    }

    /**
     * Filer strings that contain the specified pattern
     *
     * @param stringStream
     * @param pattern
     * @return String stream
     */
    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern){
        return stringStream.filter(s -> !s.contains(pattern));
    }

    /**
     * Creates an intStream from an int array
     *
     * @param arr
     * @return int stream
     */
    @Override
    public IntStream createIntStream(int[] arr){
        return IntStream.of(arr);
    }

    /**
     * Convert a stream to a list
     *
     * @param stream
     * @return List
     */
    @Override
    public <E> List<E> toList(Stream<E> stream){
        return stream.collect(Collectors.toList());
    }

    /**
     * Convert intStream to list
     *
     * @param intStream
     * @return List of Integers
     */
    @Override
    public List<Integer> toList(IntStream intStream){
        return intStream.boxed().collect(Collectors.toList());
    }

    /**
     * Create an IntStream given a range (start to end inclusive)
     *
     * @param start
     * @param end
     * @return IntStream
     */
    @Override
    public IntStream createIntStream(int start, int end){
        return IntStream.of(start, end);
    }

    /**
     * Convert intStream to doubleStream
     * Compute square root of each element
     *
     * @param intStream
     * @return DoubleStream
     */
    @Override
    public DoubleStream squareRootIntStream(IntStream intStream){
        return intStream.mapToDouble(val -> Math.sqrt(val));
    }

    /**
     * Filter all even numbers and return odd numbers from an intStream
     *
     * @param intStream
     * @return IntStream
     */
    @Override
    public IntStream getOdd(IntStream intStream){
        return intStream.filter(val -> val % 2 == 1);
    }

    /**
     * Return a lambda function that prints a message with a prefix and suffix
     * e.g.
     * LambdaStreamExc lse = new LambdaStreamImp();
     * Consumer<String> printer = lse.getLambdaPrinter("start>", "<end");
     * printer.accept("Message body");
     * <p>
     * sout:
     * start>Message body<end
     *
     * @param prefix prefix str
     * @param suffix suffix str
     * @return Consumer<String>
     */
    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix){
        return message -> System.out.println(prefix + message + suffix);
    }

    /**
     * Print each message with a given printer
     * Please use `getLambdaPrinter` method
     * <p>
     * e.g.
     * String[] messages = {"a","b", "c"};
     * lse.printMessages(messages, lse.getLambdaPrinter("msg:", "!") );
     * <p>
     * sout:
     * msg:a!
     * msg:b!
     * msg:c!
     *
     * @param messages
     * @param printer
     */
    @Override
    public void printMessages(String[] messages, Consumer<String> printer){
        for(String message : messages){
            printer.accept(message);
        }
    }

    /**
     * Print all odd number from a intStream.
     * Please use `createIntStream` and `getLambdaPrinter` methods
     * <p>
     * e.g.
     * lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));
     * <p>
     * sout:
     * odd number:1!
     * odd number:3!
     * odd number:5!
     *
     * @param intStream
     * @param printer
     */
    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer){
        getOdd(intStream).mapToObj(i -> Integer.toString(i)).forEach(i -> printer.accept(i));
    }

    /**
     * Square each number from the input.
     * - using flatMap
     *
     * @param ints
     * @return
     */
    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints){
        return ints.flatMap(i -> i.stream().map(streamInt -> streamInt * streamInt));
    }

}
